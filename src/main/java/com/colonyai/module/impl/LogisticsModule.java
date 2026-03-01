package com.colonyai.module.impl;

import com.colonyai.integration.BaritoneFacade;
import com.colonyai.integration.MineColoniesFacade;
import com.colonyai.integration.MineColoniesFacade.BuilderRequest;
import com.colonyai.integration.MineColoniesFacade.RequestStatus;
import com.colonyai.module.BaseModule;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class LogisticsModule extends BaseModule
{
    private enum CourierStep
    {
        IDLE,
        MOVE_TO_WAREHOUSE,
        TAKE_ITEM,
        MOVE_TO_BUILDER,
        DELIVER
    }

    private boolean globalSearch = true;
    private int patrolIndex;
    private CourierStep step = CourierStep.IDLE;
    private BuilderRequest activeRequest;
    private BlockPos warehousePos;

    @Override
    public String id()
    {
        return "logistics";
    }

    @Override
    public String displayName()
    {
        return "Smart Courier";
    }

    @Override
    protected void onSafeTick(final Minecraft mc)
    {
        runCourier(mc);

        if (step == CourierStep.IDLE && mc.level.getGameTime() % 100 == 0)
        {
            runChunkLoaderPatrol();
        }
    }

    private void runCourier(final Minecraft mc)
    {
        final ClientPlayerEntity player = mc.player;
        if (player == null)
        {
            return;
        }

        if (activeRequest == null)
        {
            final List<BuilderRequest> requests = MineColoniesFacade.getBuilderRequests();
            for (final BuilderRequest request : requests)
            {
                if (request.getStatus() == RequestStatus.READY_FOR_DELIVERY)
                {
                    activeRequest = request;
                    final List<BlockPos> storages = MineColoniesFacade.queryStorageForItem(request.getStack().getItem(), globalSearch);
                    warehousePos = storages.isEmpty() ? null : storages.get(0);
                    step = warehousePos == null ? CourierStep.IDLE : CourierStep.MOVE_TO_WAREHOUSE;
                    break;
                }
            }
            return;
        }

        if (warehousePos == null)
        {
            resetCourier();
            return;
        }

        switch (step)
        {
            case MOVE_TO_WAREHOUSE:
                BaritoneFacade.mapsTo(warehousePos);
                if (player.distanceToSqr(warehousePos.getX() + 0.5D, warehousePos.getY() + 0.5D, warehousePos.getZ() + 0.5D) <= 9.0D)
                {
                    step = CourierStep.TAKE_ITEM;
                }
                break;
            case TAKE_ITEM:
                if (BaritoneFacade.takeItemFromChest(warehousePos, activeRequest.getStack()))
                {
                    step = CourierStep.MOVE_TO_BUILDER;
                }
                break;
            case MOVE_TO_BUILDER:
                BaritoneFacade.mapsTo(activeRequest.getBuilderPos());
                if (player.distanceToSqr(activeRequest.getBuilderPos().getX() + 0.5D, activeRequest.getBuilderPos().getY() + 0.5D, activeRequest.getBuilderPos().getZ() + 0.5D) <= 9.0D)
                {
                    step = CourierStep.DELIVER;
                }
                break;
            case DELIVER:
                deliverToBuilder(player, activeRequest.getStack());
                resetCourier();
                break;
            case IDLE:
            default:
                break;
        }
    }

    private void deliverToBuilder(final ClientPlayerEntity player, final ItemStack stack)
    {
        for (int slot = 0; slot < player.inventory.getContainerSize(); slot++)
        {
            final ItemStack invStack = player.inventory.getItem(slot);
            if (!invStack.isEmpty() && invStack.getItem() == stack.getItem())
            {
                player.drop(invStack.split(Math.min(stack.getCount(), invStack.getCount())), false);
                return;
            }
        }
    }

    private void resetCourier()
    {
        activeRequest = null;
        warehousePos = null;
        step = CourierStep.IDLE;
    }

    private void runChunkLoaderPatrol()
    {
        final List<BlockPos> sites = MineColoniesFacade.activeConstructionSites();
        if (sites.isEmpty())
        {
            return;
        }

        patrolIndex = patrolIndex % sites.size();
        BaritoneFacade.mapsTo(sites.get(patrolIndex));
        patrolIndex++;
    }

    public boolean isGlobalSearch()
    {
        return globalSearch;
    }

    public void setGlobalSearch(final boolean globalSearch)
    {
        this.globalSearch = globalSearch;
    }
}
