package com.colonyai.client;

import com.colonyai.client.gui.ColonyMainScreen;
import com.colonyai.module.ModuleManager;
import com.colonyai.module.impl.AutoEatModule;
import com.colonyai.module.impl.FullBrightModule;
import com.colonyai.module.impl.GuardColonyModule;
import com.colonyai.module.impl.LogisticsModule;
import com.colonyai.module.impl.SettingsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class ClientBootstrap
{
    private static final ModuleManager MODULE_MANAGER = new ModuleManager();
    private static KeyBinding menuKey;
    private static KeyBinding panicKey;

    private ClientBootstrap()
    {
    }

    public static void init()
    {
        menuKey = new KeyBinding("key.colonyai.menu", KeyConflictContext.IN_GAME, KeyModifier.NONE, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.colonyai");
        panicKey = new KeyBinding("key.colonyai.panic", KeyConflictContext.IN_GAME, KeyModifier.NONE, GLFW.GLFW_KEY_BACKSPACE, "key.categories.colonyai");
        ClientRegistry.registerKeyBinding(menuKey);
        ClientRegistry.registerKeyBinding(panicKey);

        MODULE_MANAGER.register(new GuardColonyModule());
        MODULE_MANAGER.register(new LogisticsModule());
        MODULE_MANAGER.register(new AutoEatModule());
        MODULE_MANAGER.register(new FullBrightModule());
        MODULE_MANAGER.register(new SettingsModule());

        MinecraftForge.EVENT_BUS.register(ClientBootstrap.class);
    }

    @SubscribeEvent
    public static void onKeyInput(final InputEvent.KeyInputEvent event)
    {
        if (menuKey.consumeClick())
        {
            Minecraft.getInstance().setScreen(new ColonyMainScreen(MODULE_MANAGER));
        }

        if (panicKey.consumeClick())
        {
            MODULE_MANAGER.disableAll();
        }
    }
}
