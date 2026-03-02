package com.colonyai.module.impl;

import com.colonyai.module.Category;
import com.colonyai.module.Module;
import net.minecraft.client.Minecraft;

public final class FullBrightModule extends Module
{
    private double previousGamma = -1.0D;

    public FullBrightModule()
    {
        super("FullBright", "Forces maximum gamma while the module is enabled.", Category.PLAYER);
    }

    @Override
    public void onEnable()
    {
        final Minecraft mc = Minecraft.getInstance();
        previousGamma = mc.options.gamma;
        mc.options.gamma = 16.0D;
    }

    @Override
    public void onDisable()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (previousGamma >= 0.0D)
        {
            mc.options.gamma = previousGamma;
        }
    }
}
