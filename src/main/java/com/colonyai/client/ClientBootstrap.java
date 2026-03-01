package com.colonyai.client;

import com.colonyai.client.gui.ColonyMainScreen;
import com.colonyai.module.ModuleManager;
import com.colonyai.module.impl.AfkSurvivalModule;
import com.colonyai.module.impl.CombatModule;
import com.colonyai.module.impl.LogisticsModule;
import com.colonyai.module.impl.SettingsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class ClientBootstrap
{
    private static final ModuleManager MODULE_MANAGER = new ModuleManager();
    private static KeyBinding menuKey;
    private static KeyBinding killSwitchKey;

    private ClientBootstrap()
    {
    }

    public static void init()
    {
        menuKey = new KeyBinding("key.colony_ai.menu", KeyConflictContext.IN_GAME, KeyModifier.NONE, GLFW.GLFW_KEY_M, "key.categories.colony_ai");
        killSwitchKey = new KeyBinding("key.colony_ai.kill_switch", KeyConflictContext.IN_GAME, KeyModifier.NONE, GLFW.GLFW_KEY_BACKSPACE, "key.categories.colony_ai");
        ClientRegistry.registerKeyBinding(menuKey);
        ClientRegistry.registerKeyBinding(killSwitchKey);

        MODULE_MANAGER.register(new LogisticsModule());
        MODULE_MANAGER.register(new CombatModule());
        MODULE_MANAGER.register(new AfkSurvivalModule());
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

        if (killSwitchKey.consumeClick())
        {
            MODULE_MANAGER.disableAll();
        }

        if (event.getAction() == GLFW.GLFW_PRESS && isEmergencyKey(event.getKey()))
        {
            MODULE_MANAGER.pauseForTicks(100);
        }
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            MODULE_MANAGER.tick();
        }
    }

    private static boolean isEmergencyKey(final int key)
    {
        return key == GLFW.GLFW_KEY_W
            || key == GLFW.GLFW_KEY_A
            || key == GLFW.GLFW_KEY_S
            || key == GLFW.GLFW_KEY_D
            || key == GLFW.GLFW_KEY_SPACE;
    }
}
