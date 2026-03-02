package com.colonyai.module.impl;

import com.colonyai.module.Category;
import com.colonyai.module.Module;

public final class SettingsModule extends Module
{
    public SettingsModule()
    {
        super("Safe Mode", "Reserved settings toggles for future behavior flags.", Category.SETTINGS);
    }
}
