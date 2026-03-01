package com.colonyai.module.impl;

import com.colonyai.integration.MineColoniesFacade;
import com.colonyai.module.BaseModule;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.registries.ForgeRegistries;

public class CombatModule extends BaseModule
{
    private static final ResourceLocation ANCIENT_TOME_ID = new ResourceLocation("minecolonies", "ancienttome");

    @Override
    public String id()
    {
        return "combat";
    }

    @Override
    public String displayName()
    {
        return "Combat AI";
    }

    @Override
    protected void onSafeTick(final Minecraft mc)
    {
        if (mc.player == null || mc.level == null)
        {
            return;
        }

        lootPriority(mc);
    }

    public boolean attack(final Minecraft mc)
    {
        if (!allowAttack(mc))
        {
            return false;
        }

        final RayTraceResult result = mc.hitResult;
        if (!(result instanceof EntityRayTraceResult))
        {
            return false;
        }

        final Entity target = ((EntityRayTraceResult) result).getEntity();
        if (target == null)
        {
            return false;
        }

        mc.gameMode.attack(mc.player, target);
        mc.player.swing(net.minecraft.util.Hand.MAIN_HAND);
        return true;
    }

    public boolean allowAttack(final Minecraft mc)
    {
        final RayTraceResult result = mc.hitResult;
        if (result instanceof EntityRayTraceResult)
        {
            final Entity target = ((EntityRayTraceResult) result).getEntity();
            if (MineColoniesFacade.isCitizenEntity(target))
            {
                return false;
            }
        }

        final double reach = getCurrentReachDistance(mc.player.getMainHandItem());
        if (isCitizenNearby(mc, Math.max(4.0D, reach)))
        {
            return false;
        }

        return true;
    }

    public boolean isCitizenNearby(final Minecraft mc, final double radius)
    {
        final AxisAlignedBB area = mc.player.getBoundingBox().inflate(radius);
        final List<Entity> nearby = mc.level.getEntities(mc.player, area);
        for (final Entity entity : nearby)
        {
            if (MineColoniesFacade.isCitizenEntity(entity))
            {
                return true;
            }
        }

        return false;
    }

    private double getCurrentReachDistance(final ItemStack heldStack)
    {
        if (heldStack.isEmpty())
        {
            return 3.0D;
        }

        final Multimap<Attribute, AttributeModifier> modifiers = heldStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
        for (final java.util.Map.Entry<Attribute, Collection<AttributeModifier>> entry : modifiers.asMap().entrySet())
        {
            final Attribute attribute = entry.getKey();
            final String name = attribute.getDescriptionId().toLowerCase();
            if (!name.contains("reach"))
            {
                continue;
            }

            double value = 3.0D;
            for (final AttributeModifier modifier : entry.getValue())
            {
                value += modifier.getAmount();
            }
            return Math.max(3.0D, value);
        }

        return 3.0D;
    }

    private void lootPriority(final Minecraft mc)
    {
        for (final Entity entity : mc.level.entitiesForRendering())
        {
            if (!(entity instanceof ItemEntity) || entity.distanceTo(mc.player) > 15.0F)
            {
                continue;
            }

            final ItemStack stack = ((ItemEntity) entity).getItem();
            final ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (key != null && key.equals(ANCIENT_TOME_ID))
            {
                mc.player.getNavigation().moveTo(entity, 1.2D);
                return;
            }
        }
    }
}
