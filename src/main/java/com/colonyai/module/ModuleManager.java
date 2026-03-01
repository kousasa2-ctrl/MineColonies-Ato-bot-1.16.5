package com.colonyai.module;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModuleManager
{
    private final Map<String, Module> modules = new LinkedHashMap<String, Module>();
    private int pauseTicks;

    public void register(final Module module)
    {
        modules.put(module.id(), module);
        module.setEnabled(true);
    }

    public void tick()
    {
        if (pauseTicks > 0)
        {
            pauseTicks--;
            return;
        }

        for (final Module module : modules.values())
        {
            module.tick();
        }
    }

    public void pauseForTicks(final int ticks)
    {
        this.pauseTicks = Math.max(this.pauseTicks, ticks);
    }

    public void disableAll()
    {
        for (final Module module : modules.values())
        {
            module.setEnabled(false);
        }
    }

    public Collection<Module> allModules()
    {
        return Collections.unmodifiableCollection(modules.values());
    }
}
