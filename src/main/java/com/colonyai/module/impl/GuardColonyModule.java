package com.colonyai.module.impl;

import com.colonyai.integration.BaritoneFacade;
import com.colonyai.integration.MineColoniesFacade;
import com.colonyai.module.Category;
import com.colonyai.module.Module;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class GuardColonyModule extends Module
{
    public GuardColonyModule()
    {
        super("Guard Colony", "Find and attack nearby colony hostiles with Baritone pathing.", Category.COMBAT);
    }

    @Override
    public void onTick()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
        {
            return;
        }

        if (!BaritoneFacade.hasBaritone() || !MineColoniesFacade.hasMineColonies())
        {
            return;
        }

        final List<MobEntity> hostiles = MineColoniesFacade.getNearbyHostiles(24.0D);
        if (hostiles.isEmpty())
        {
            return;
        }

        final MobEntity target = hostiles.get(0);
        BaritoneFacade.pathTo(target.blockPosition());

        if (mc.player.distanceTo(target) <= 3.4D)
        {
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(mc.player.getUsedItemHand());
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
