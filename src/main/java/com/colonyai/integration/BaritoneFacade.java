package com.colonyai.integration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public final class BaritoneFacade
{
    private static boolean suspended;

    private BaritoneFacade()
    {
    }

    public static void suspendNavigation(final boolean value)
    {
        suspended = value;
    }

    public static boolean isSuspended()
    {
        return suspended;
    }

    public static void pathTo(final BlockPos pos)
    {
        mapsTo(pos);
    }

    public static void mapsTo(final BlockPos pos)
    {
        if (suspended || pos == null)
        {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        final ClientPlayerEntity player = mc.player;
        if (player == null)
        {
            return;
        }

        try
        {
            final Class<?> apiClass = Class.forName("baritone.api.BaritoneAPI");
            final Object provider = apiClass.getMethod("getProvider").invoke(null);
            final Object baritone = provider.getClass().getMethod("getBaritoneForPlayer", ClientPlayerEntity.class).invoke(provider, player);
            final Class<?> goalBlockClass = Class.forName("baritone.api.pathing.goals.GoalBlock");
            final Object goalBlock = goalBlockClass.getConstructor(BlockPos.class).newInstance(pos);
            final Object customGoalProcess = baritone.getClass().getMethod("getCustomGoalProcess").invoke(baritone);
            customGoalProcess.getClass().getMethod("setGoalAndPath", Class.forName("baritone.api.pathing.goals.Goal")).invoke(customGoalProcess, goalBlock);
        }
        catch (final Exception ignored)
        {
            if (player.distanceToSqr(Vector3d.atCenterOf(pos)) > 2.25D)
            {
                player.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 1.2D);
            }
        }
    }

    public static boolean takeItemFromChest(final BlockPos chestPos, final ItemStack wanted)
    {
        if (chestPos == null || wanted.isEmpty() || suspended)
        {
            return false;
        }

        final Minecraft mc = Minecraft.getInstance();
        final ClientPlayerEntity player = mc.player;
        if (player == null || mc.level == null || mc.gameMode == null)
        {
            return false;
        }

        if (player.distanceToSqr(Vector3d.atCenterOf(chestPos)) > 9.0D)
        {
            mapsTo(chestPos);
            return false;
        }

        final TileEntity tileEntity = mc.level.getBlockEntity(chestPos);
        if (tileEntity == null)
        {
            return false;
        }

        final TileEntityType<?> type = tileEntity.getType();
        final String typeName = type.getRegistryName() == null ? "" : type.getRegistryName().toString();
        if (!typeName.contains("chest"))
        {
            return false;
        }

        final BlockRayTraceResult hit = new BlockRayTraceResult(Vector3d.atCenterOf(chestPos), Direction.UP, chestPos, false);
        mc.gameMode.useItemOn(player, mc.level, Hand.MAIN_HAND, hit);

        if (!(player.containerMenu instanceof ChestContainer))
        {
            return false;
        }

        final ChestContainer chest = (ChestContainer) player.containerMenu;
        final int chestSlots = chest.getRowCount() * 9;
        for (int slot = 0; slot < chestSlots; slot++)
        {
            final ItemStack stack = chest.getSlot(slot).getItem();
            if (stack.isEmpty() || stack.getItem() != wanted.getItem())
            {
                continue;
            }

            mc.gameMode.handleInventoryMouseClick(chest.containerId, slot, 0, ClickType.QUICK_MOVE, player);
            return true;
        }

        return false;
    }

    public static void clearTasks()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
        {
            return;
        }

        try
        {
            final Class<?> apiClass = Class.forName("baritone.api.BaritoneAPI");
            final Object provider = apiClass.getMethod("getProvider").invoke(null);
            final Object baritone = provider.getClass().getMethod("getBaritoneForPlayer", ClientPlayerEntity.class).invoke(provider, mc.player);
            final Object customGoalProcess = baritone.getClass().getMethod("getCustomGoalProcess").invoke(baritone);
            customGoalProcess.getClass().getMethod("onLostControl").invoke(customGoalProcess);
            final Object pathingBehavior = baritone.getClass().getMethod("getPathingBehavior").invoke(baritone);
            pathingBehavior.getClass().getMethod("cancelEverything").invoke(pathingBehavior);
        }
        catch (final Exception ignored)
        {
            mc.player.getNavigation().stop();
        }
    }
}
