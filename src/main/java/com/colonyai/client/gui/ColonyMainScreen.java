package com.colonyai.client.gui;

import com.colonyai.module.Module;
import com.colonyai.module.ModuleManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class ColonyMainScreen extends Screen
{
    private static final int PANEL_W = 340;
    private static final int PANEL_H = 220;
    private final ModuleManager moduleManager;
    private Tab selected = Tab.LOGISTICS;

    public ColonyMainScreen(final ModuleManager moduleManager)
    {
        super(new StringTextComponent("Colony Intelligence & Automation"));
        this.moduleManager = moduleManager;
    }

    @Override
    protected void init()
    {
        this.buttons.clear();

        int tabX = (this.width - PANEL_W) / 2 + 10;
        final int tabY = (this.height - PANEL_H) / 2 + 10;

        for (final Tab tab : Tab.values())
        {
            final Tab t = tab;
            this.addButton(new Button(tabX, tabY, 75, 20, new StringTextComponent(tab.label), b -> selected = t));
            tabX += 80;
        }

        int y = tabY + 34;
        for (final Module module : moduleManager.allModules())
        {
            final Module m = module;
            this.addButton(new Button((this.width - PANEL_W) / 2 + 16, y, PANEL_W - 32, 20, labelFor(m), b -> {
                m.setEnabled(!m.isEnabled());
                b.setMessage(labelFor(m));
            }));
            y += 24;
        }
    }

    @Override
    public void render(final MatrixStack stack, final int mouseX, final int mouseY, final float partialTicks)
    {
        this.renderBackground(stack);

        final int x = (this.width - PANEL_W) / 2;
        final int y = (this.height - PANEL_H) / 2;

        fillGradient(stack, x, y, x + PANEL_W, y + PANEL_H, 0xC80A0A0A, 0xAA121212);
        drawCenteredString(stack, this.font, this.title, this.width / 2, y + 8, 0xF0F0F0);
        drawString(stack, this.font, "Tab: " + selected.label, x + 12, y + PANEL_H - 18, 0xAAAAAA);

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private StringTextComponent labelFor(final Module module)
    {
        return new StringTextComponent(module.displayName() + ": " + (module.isEnabled() ? "ON" : "OFF"));
    }

    private enum Tab
    {
        LOGISTICS("Logistics"),
        COMBAT("Combat"),
        AFK_SURVIVAL("AFK/Survival"),
        SETTINGS("Settings");

        private final String label;

        Tab(final String label)
        {
            this.label = label;
        }
    }
}
