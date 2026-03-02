package com.colonyai.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager
{
    private final List<Module> modules = new ArrayList<Module>();

    public void register(final Module module)
    {
        modules.add(module);
    }

    public List<Module> getModules()
    {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> getModulesByCategory(final Category category)
    {
        final List<Module> filtered = new ArrayList<Module>();
        for (final Module module : modules)
        {
            if (module.getCategory() == category)
            {
                filtered.add(module);
            }
        }
        return filtered;
    }

    public void tickEnabledModules()
    {
        for (final Module module : modules)
        {
            if (module.isEnabled())
            {
                module.onTick();
            }
        }
    }

    public void disableAll()
    {
        for (final Module module : modules)
        {
            module.setEnabled(false);
        }
    }
}
