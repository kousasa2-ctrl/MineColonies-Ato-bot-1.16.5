package com.colonyai.module;

public interface Module
{
    String id();

    String displayName();

    void tick();

    void setEnabled(boolean enabled);

    boolean isEnabled();
}
