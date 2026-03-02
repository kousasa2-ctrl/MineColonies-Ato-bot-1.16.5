package com.colonyai.module;

import net.minecraftforge.common.MinecraftForge;

public abstract class Module
{
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;

    protected Module(final String name, final String description, final Category category)
    {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public final void toggle()
    {
        setEnabled(!enabled);
    }

    public final void setEnabled(final boolean enabled)
    {
        if (this.enabled == enabled)
        {
            return;
        }

        this.enabled = enabled;
        if (enabled)
        {
            MinecraftForge.EVENT_BUS.register(this);
            onEnable();
        }
        else
        {
            onDisable();
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Category getCategory()
    {
        return category;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void onEnable()
    {
    }

    public void onDisable()
    {
    }

    public void onTick()
    {
    }
}
