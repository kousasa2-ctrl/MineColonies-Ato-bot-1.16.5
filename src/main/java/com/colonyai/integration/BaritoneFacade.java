package com.colonyai.integration;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.util.math.BlockPos;

public final class BaritoneFacade
{
    private BaritoneFacade()
    {
    }

    public static IBaritone getPrimaryBaritone()
    {
        try
        {
            return BaritoneAPI.getProvider().getPrimaryBaritone();
        }
        catch (final Throwable ignored)
        {
            return null;
        }
    }

    public static boolean hasBaritone()
    {
        return getPrimaryBaritone() != null;
    }

    public static void pathTo(final BlockPos pos)
    {
        final IBaritone baritone = getPrimaryBaritone();
        if (baritone == null || pos == null)
        {
            return;
        }

        baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
    }
}
