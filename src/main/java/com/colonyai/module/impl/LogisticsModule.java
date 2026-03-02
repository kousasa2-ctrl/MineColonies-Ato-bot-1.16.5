package com.colonyai.module.impl;

import com.colonyai.integration.MineColoniesFacade;
import com.colonyai.module.Category;
import com.colonyai.module.Module;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class LogisticsModule extends Module
{
    private int ticker;

    public LogisticsModule()
    {
        super("Logistics Watch", "Track MineColonies integration availability and worker state hooks.", Category.LOGISTICS);
    }

    @Override
    public void onTick()
    {
        if (!MineColoniesFacade.hasMineColonies())
        {
            return;
        }

        ticker++;
        if (ticker > 200)
        {
            ticker = 0;
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
