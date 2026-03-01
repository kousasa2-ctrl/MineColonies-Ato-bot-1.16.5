package com.colonyai.module.impl;

import com.colonyai.module.BaseModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AfkSurvivalModule extends BaseModule
{
    @Override
    public String id()
    {
        return "afk_survival";
    }

    @Override
    public String displayName()
    {
        return "AFK / Survival";
    }

    @Override
    protected void onSafeTick(final Minecraft mc)
    {
        final ClientPlayerEntity player = mc.player;
        if (player == null)
        {
            return;
        }

        if (player.getHealth() < 14.0F)
        {
            eatBestMatch(player, true);
            return;
        }

        if (player.getFoodData().getFoodLevel() < 18)
        {
            eatBestMatch(player, false);
        }
    }

    private void eatBestMatch(final ClientPlayerEntity player, final boolean combatPriority)
    {
        for (int i = 0; i < player.inventory.getContainerSize(); i++)
        {
            final ItemStack stack = player.inventory.getItem(i);
            if (stack.isEmpty() || !stack.isEdible())
            {
                continue;
            }

            if (combatPriority && isHighSaturation(stack.getItem()))
            {
                player.inventory.selected = i;
                return;
            }

            if (!combatPriority && isLowSaturation(stack.getItem()))
            {
                player.inventory.selected = i;
                return;
            }
        }
    }

    private boolean isHighSaturation(final Item item)
    {
        return item == Items.COOKED_BEEF || item == Items.COOKED_PORKCHOP || item == Items.COOKED_MUTTON;
    }

    private boolean isLowSaturation(final Item item)
    {
        return item == Items.CARROT || item == Items.APPLE || item == Items.BEETROOT;
    }
}
