package com.colonyai.module.impl;

import com.colonyai.module.BaseModule;
import net.minecraft.client.Minecraft;

public class SettingsModule extends BaseModule
{
    @Override
    public String id()
    {
        return "settings";
    }

    @Override
    public String displayName()
    {
        return "Settings";
    }

    @Override
    protected void onSafeTick(final Minecraft mc)
    {
        // Reserved for runtime settings synchronization.
    }
}
