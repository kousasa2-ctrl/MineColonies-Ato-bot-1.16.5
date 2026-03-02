package com.colonyai.module.impl;

import com.colonyai.module.Category;
import com.colonyai.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class AutoEatModule extends Module
{
    public AutoEatModule()
    {
        super("Auto Eat", "Automatically consumes food from hotbar when hungry.", Category.PLAYER);
    }

    @Override
    public void onTick()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameMode == null)
        {
            return;
        }

        if (mc.player.getFoodData().getFoodLevel() > 14)
        {
            return;
        }

        for (int slot = 0; slot < 9; slot++)
        {
            final ItemStack stack = mc.player.inventory.getItem(slot);
            if (!stack.isEmpty() && stack.isEdible())
            {
                mc.player.inventory.selected = slot;
                mc.gameMode.useItem(mc.player, mc.level, mc.player.getUsedItemHand());
                break;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && isEnabled())
        {
            onTick();
        }
    }
}
