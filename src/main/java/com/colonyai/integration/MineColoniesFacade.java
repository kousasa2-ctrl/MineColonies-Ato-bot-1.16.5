package com.colonyai.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.AxisAlignedBB;

public final class MineColoniesFacade
{
    private MineColoniesFacade()
    {
    }

    public static boolean hasMineColonies()
    {
        try
        {
            Class.forName("com.minecolonies.api.MinecoloniesAPI");
            return true;
        }
        catch (final Throwable ignored)
        {
            return false;
        }
    }

    public static List<MobEntity> getNearbyHostiles(final double radius)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
        {
            return Collections.emptyList();
        }

        final AxisAlignedBB box = mc.player.getBoundingBox().inflate(radius);
        final List<MobEntity> hostiles = new ArrayList<MobEntity>();
        for (final Entity entity : mc.level.getEntities(mc.player, box))
        {
            if (entity instanceof MonsterEntity && entity.isAlive())
            {
                hostiles.add((MobEntity) entity);
            }
        }
        return hostiles;
    }
}
