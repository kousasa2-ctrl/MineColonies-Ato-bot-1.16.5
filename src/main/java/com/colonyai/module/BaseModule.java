package com.colonyai.module;

import net.minecraft.client.Minecraft;

public abstract class BaseModule implements Module
{
    private boolean enabled;

    @Override
    public void tick()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (!enabled || mc.player == null || mc.level == null)
        {
            return;
        }

        // Input blocking requirement: no automation while any GUI is open.
        if (mc.screen != null)
        {
            return;
        }

        onSafeTick(mc);
    }

    protected abstract void onSafeTick(Minecraft mc);

    @Override
    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }
}
