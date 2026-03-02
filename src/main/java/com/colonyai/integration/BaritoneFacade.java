package com.colonyai.integration;

import java.lang.reflect.Method;
import net.minecraft.util.math.BlockPos;

public final class BaritoneFacade
{
    private static final String BARITONE_API_CLASS = "baritone.api.BaritoneAPI";
    private static final String GOAL_BLOCK_CLASS = "baritone.api.pathing.goals.GoalBlock";

    private BaritoneFacade()
    {
    }

    public static Object getPrimaryBaritone()
    {
        try
        {
            final Class<?> apiClass = Class.forName(BARITONE_API_CLASS);
            final Method getProvider = apiClass.getMethod("getProvider");
            final Object provider = getProvider.invoke(null);
            if (provider == null)
            {
                return null;
            }

            final Method getPrimaryBaritone = provider.getClass().getMethod("getPrimaryBaritone");
            return getPrimaryBaritone.invoke(provider);
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
        if (pos == null)
        {
            return;
        }

        final Object baritone = getPrimaryBaritone();
        if (baritone == null)
        {
            return;
        }

        try
        {
            final Method getCustomGoalProcess = baritone.getClass().getMethod("getCustomGoalProcess");
            final Object customGoalProcess = getCustomGoalProcess.invoke(baritone);
            if (customGoalProcess == null)
            {
                return;
            }

            final Class<?> goalBlockClass = Class.forName(GOAL_BLOCK_CLASS);
            final Object goalBlock = goalBlockClass.getConstructor(BlockPos.class).newInstance(pos);
            final Method setGoalAndPath = customGoalProcess.getClass().getMethod("setGoalAndPath", Class.forName("baritone.api.pathing.goals.Goal"));
            setGoalAndPath.invoke(customGoalProcess, goalBlock);
        }
        catch (final Throwable ignored)
        {
        }
    }
}
