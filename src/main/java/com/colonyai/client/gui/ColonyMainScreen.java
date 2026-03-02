package com.colonyai.client.gui;

import com.colonyai.module.Category;
import com.colonyai.module.Module;
import com.colonyai.module.ModuleManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public final class ColonyMainScreen extends Screen
{
    private final ModuleManager moduleManager;
    private Category selectedCategory = Category.COMBAT;

    public ColonyMainScreen(final ModuleManager moduleManager)
    {
        super(new StringTextComponent("ColonyAI"));
        this.moduleManager = moduleManager;
    }

    @Override
    protected void init()
    {
        this.clearWidgets();

        final int leftX = this.width / 2 - 160;
        final int topY = this.height / 2 - 100;

        int categoryY = topY + 24;
        for (final Category category : Category.values())
        {
            this.addButton(new Button(leftX + 10, categoryY, 100, 20, new StringTextComponent(category.getDisplayName()), button -> {
                selectedCategory = category;
                init();
            }));
            categoryY += 24;
        }

        final List<Module> modules = moduleManager.getModulesByCategory(selectedCategory);
        int moduleY = topY + 24;
        for (final Module module : modules)
        {
            this.addButton(new ModuleButton(leftX + 125, moduleY, 185, 20, module));
            moduleY += 24;
        }
    }

    @Override
    public void render(final MatrixStack stack, final int mouseX, final int mouseY, final float partialTicks)
    {
        this.renderBackground(stack);
        final int leftX = this.width / 2 - 160;
        final int topY = this.height / 2 - 100;

        fill(stack, leftX, topY, leftX + 320, topY + 200, 0xCC111111);
        fill(stack, leftX + 120, topY + 20, leftX + 121, topY + 190, 0xFF444444);

        Minecraft.getInstance().fontRenderer.draw(stack, "Categories", leftX + 10, topY + 8, 0xFFFFFF);
        Minecraft.getInstance().fontRenderer.draw(stack, selectedCategory.getDisplayName() + " Modules", leftX + 125, topY + 8, 0xFFFFFF);

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private static final class ModuleButton extends Button
    {
        private final Module module;

        private ModuleButton(final int x, final int y, final int width, final int height, final Module module)
        {
            super(x, y, width, height, new StringTextComponent(module.getName()), button -> {
                module.toggle();
                button.setMessage(new StringTextComponent(module.getName()));
            });
            this.module = module;
        }

        @Override
        public void renderButton(final MatrixStack stack, final int mouseX, final int mouseY, final float partialTicks)
        {
            final int color = module.isEnabled() ? 0xCC118833 : 0xCC882222;
            fill(stack, this.x, this.y, this.x + this.width, this.y + this.height, color);
            Minecraft.getInstance().fontRenderer.draw(stack, module.getName(), this.x + 6, this.y + 6, 0xFFFFFF);
        }
    }
}
