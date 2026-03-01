package com.colonyai;

import com.colonyai.client.ClientBootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(ColonyAIMod.MOD_ID)
public class ColonyAIMod
{
    public static final String MOD_ID = "colony_ai";

    public ColonyAIMod()
    {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientBootstrap::init);
    }
}
