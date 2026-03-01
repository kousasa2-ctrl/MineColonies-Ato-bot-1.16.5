package com.colonyai.integration;

import com.ldtteam.minecolonies.api.MinecoloniesAPIProxy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public final class MineColoniesFacade
{
    private static final RequestStatus DEFAULT_STATUS = RequestStatus.MISSING;

    private MineColoniesFacade()
    {
    }

    public static List<BuilderRequest> getBuilderRequests()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
        {
            return Collections.emptyList();
        }

        final List<BuilderRequest> requests = new ArrayList<BuilderRequest>();
        final List<BlockPos> warehouses = findWarehouses();

        try
        {
            final Object colonyManager = MinecoloniesAPIProxy.getColonyManager();
            if (colonyManager == null)
            {
                return Collections.emptyList();
            }

            final Method getIColony = findMethod(colonyManager.getClass(), "getIColony", 2);
            if (getIColony == null)
            {
                return Collections.emptyList();
            }

            final Object colony = getIColony.invoke(colonyManager, mc.player.level.dimension(), mc.player.blockPosition());
            if (colony == null)
            {
                return Collections.emptyList();
            }

            final Method getBuildingManager = colony.getClass().getMethod("getBuildingManager");
            final Object buildingManager = getBuildingManager.invoke(colony);
            if (buildingManager == null)
            {
                return Collections.emptyList();
            }

            final Method getBuildings = findMethod(buildingManager.getClass(), "getBuildings", 0);
            if (getBuildings == null)
            {
                return Collections.emptyList();
            }

            final Iterable<?> buildings = asIterable(getBuildings.invoke(buildingManager));
            for (final Object building : buildings)
            {
                if (building == null || !building.getClass().getName().contains("BuildingBuilder"))
                {
                    continue;
                }

                final BlockPos builderPos = invokePos(building, "getPosition");
                final int progress = invokeInt(building, "getBuildingLevel", 0) * 20;
                final List<ItemStack> requiredItems = invokeItemStacks(building, "getOpenRequests");

                for (final ItemStack requested : requiredItems)
                {
                    final boolean hasInWarehouse = existsInWarehouse(warehouses, requested.getItem(), requested.getCount());
                    final RequestStatus status = hasInWarehouse ? RequestStatus.READY_FOR_DELIVERY : DEFAULT_STATUS;
                    requests.add(new BuilderRequest(builderPos, requested.copy(), status, Math.min(100, Math.max(0, progress))));
                }
            }
        }
        catch (final Exception ignored)
        {
            return Collections.emptyList();
        }

        return requests;
    }

    public static List<BlockPos> activeConstructionSites()
    {
        final List<BuilderRequest> requests = getBuilderRequests();
        final List<BlockPos> sites = new ArrayList<BlockPos>();
        for (final BuilderRequest request : requests)
        {
            if (!sites.contains(request.getBuilderPos()))
            {
                sites.add(request.getBuilderPos());
            }
        }
        return sites;
    }

    public static List<BlockPos> queryStorageForItem(final Item item, final boolean globalSearch)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
        {
            return Collections.emptyList();
        }

        final List<BlockPos> result = new ArrayList<BlockPos>();
        for (final TileEntity tileEntity : mc.level.blockEntityList)
        {
            if (tileEntity == null)
            {
                continue;
            }

            final TileEntityType<?> type = tileEntity.getType();
            final String typeName = String.valueOf(type.getRegistryName());
            final boolean isWarehouse = typeName.contains("warehouse");
            if (!globalSearch && !isWarehouse)
            {
                continue;
            }

            if (containsItem(tileEntity, new ItemStack(item), 1))
            {
                result.add(tileEntity.getBlockPos());
            }
        }
        return result;
    }

    public static boolean isCitizenEntity(final Object entity)
    {
        return entity != null && entity.getClass().getName().contains("EntityCitizen");
    }

    private static List<BlockPos> findWarehouses()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
        {
            return Collections.emptyList();
        }

        final List<BlockPos> warehouses = new ArrayList<BlockPos>();
        for (final TileEntity tileEntity : mc.level.blockEntityList)
        {
            if (tileEntity == null || tileEntity.getType().getRegistryName() == null)
            {
                continue;
            }

            if (tileEntity.getType().getRegistryName().toString().contains("warehouse"))
            {
                warehouses.add(tileEntity.getBlockPos());
            }
        }
        return warehouses;
    }

    private static boolean existsInWarehouse(final List<BlockPos> warehouses, final Item item, final int count)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
        {
            return false;
        }

        for (final BlockPos pos : warehouses)
        {
            final TileEntity tile = mc.level.getBlockEntity(pos);
            if (containsItem(tile, new ItemStack(item), count))
            {
                return true;
            }
        }

        return false;
    }

    private static boolean containsItem(final TileEntity tile, final ItemStack template, final int minCount)
    {
        if (tile == null)
        {
            return false;
        }

        try
        {
            final Class<?> capabilityItemHandler = Class.forName("net.minecraftforge.items.CapabilityItemHandler");
            final Object cap = capabilityItemHandler.getField("ITEM_HANDLER_CAPABILITY").get(null);
            final Method getCapability = tile.getClass().getMethod("getCapability", Class.forName("net.minecraftforge.common.capabilities.Capability"));
            final Object lazyOptional = getCapability.invoke(tile, cap);
            final Method resolve = lazyOptional.getClass().getMethod("resolve");
            final java.util.Optional<?> optional = (java.util.Optional<?>) resolve.invoke(lazyOptional);
            if (!optional.isPresent())
            {
                return false;
            }

            final Object handler = optional.get();
            final Method slotsMethod = handler.getClass().getMethod("getSlots");
            final Method stackInSlotMethod = handler.getClass().getMethod("getStackInSlot", int.class);
            final int slots = (Integer) slotsMethod.invoke(handler);

            int found = 0;
            for (int i = 0; i < slots; i++)
            {
                final ItemStack stack = (ItemStack) stackInSlotMethod.invoke(handler, i);
                if (!stack.isEmpty() && stack.getItem() == template.getItem())
                {
                    found += stack.getCount();
                    if (found >= minCount)
                    {
                        return true;
                    }
                }
            }
        }
        catch (final Exception ignored)
        {
            return false;
        }

        return false;
    }

    private static Method findMethod(final Class<?> owner, final String name, final int params)
    {
        for (final Method method : owner.getMethods())
        {
            if (method.getName().equals(name) && method.getParameterTypes().length == params)
            {
                return method;
            }
        }
        return null;
    }

    private static Iterable<?> asIterable(final Object value)
    {
        if (value instanceof Iterable)
        {
            return (Iterable<?>) value;
        }

        if (value instanceof java.util.Map)
        {
            return ((java.util.Map<?, ?>) value).values();
        }

        return Collections.emptyList();
    }

    private static BlockPos invokePos(final Object owner, final String method)
    {
        try
        {
            return (BlockPos) owner.getClass().getMethod(method).invoke(owner);
        }
        catch (final Exception ignored)
        {
            return BlockPos.ZERO;
        }
    }

    private static int invokeInt(final Object owner, final String method, final int fallback)
    {
        try
        {
            return (Integer) owner.getClass().getMethod(method).invoke(owner);
        }
        catch (final Exception ignored)
        {
            return fallback;
        }
    }

    private static List<ItemStack> invokeItemStacks(final Object owner, final String method)
    {
        try
        {
            final Iterable<?> raw = (Iterable<?>) owner.getClass().getMethod(method).invoke(owner);
            final List<ItemStack> items = new ArrayList<ItemStack>();
            for (final Object entry : raw)
            {
                if (entry instanceof ItemStack)
                {
                    items.add(((ItemStack) entry).copy());
                    continue;
                }

                final Method getStackMethod = entry.getClass().getMethod("getStack");
                final ItemStack stack = (ItemStack) getStackMethod.invoke(entry);
                if (!stack.isEmpty())
                {
                    items.add(stack.copy());
                }
            }
            return items;
        }
        catch (final Exception ignored)
        {
            return Collections.emptyList();
        }
    }

    public enum RequestStatus
    {
        READY_FOR_DELIVERY,
        MISSING
    }

    public static final class BuilderRequest
    {
        private final BlockPos builderPos;
        private final ItemStack stack;
        private final RequestStatus status;
        private final int progress;

        public BuilderRequest(final BlockPos builderPos, final ItemStack stack, final RequestStatus status, final int progress)
        {
            this.builderPos = builderPos;
            this.stack = stack;
            this.status = status;
            this.progress = progress;
        }

        public BlockPos getBuilderPos()
        {
            return builderPos;
        }

        public ItemStack getStack()
        {
            return stack;
        }

        public RequestStatus getStatus()
        {
            return status;
        }

        public int getProgress()
        {
            return progress;
        }
    }
}
