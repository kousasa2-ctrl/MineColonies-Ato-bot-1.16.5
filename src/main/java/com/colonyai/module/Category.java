package com.colonyai.module;

public enum Category
{
    COMBAT("Combat"),
    LOGISTICS("Logistics"),
    PLAYER("Player"),
    SETTINGS("Settings");

    private final String displayName;

    Category(final String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
