package com.colonyai.client.gui;

import com.colonyai.integration.MineColoniesFacade;
import com.colonyai.integration.MineColoniesFacade.BuilderRequest;
import com.colonyai.integration.MineColoniesFacade.RequestStatus;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class BuilderHudOverlay
{
    private BuilderHudOverlay()
    {
    }

    @SubscribeEvent
    public static void render(final RenderGameOverlayEvent.Post event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
        {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.level == null)
        {
            return;
        }

        final List<BuilderRequest> requests = MineColoniesFacade.getBuilderRequests();
        if (requests.isEmpty())
        {
            return;
        }

        final MatrixStack stack = event.getMatrixStack();
        int y = 8;
        for (final BuilderRequest request : requests)
        {
            drawCard(stack, 8, y, request);
            y += 38;
            if (y > event.getWindow().getGuiScaledHeight() - 40)
            {
                break;
            }
        }
    }

    private static void drawCard(final MatrixStack stack, final int x, final int y, final BuilderRequest request)
    {
        final Minecraft mc = Minecraft.getInstance();
        final int w = 210;
        final int h = 32;

        AbstractGui.fill(stack, x, y, x + w, y + h, 0xAA111111);
        AbstractGui.fill(stack, x + 1, y + h - 9, x + w - 1, y + h - 3, 0xFF2C2C2C);

        final int progress = Math.max(0, Math.min(100, request.getProgress()));
        final int progressWidth = (w - 4) * progress / 100;
        AbstractGui.fill(stack, x + 2, y + h - 8, x + 2 + progressWidth, y + h - 4, 0xFF4CAF50);

        final ItemStack stackRequested = request.getStack();
        mc.font.draw(stack, "Builder @ " + request.getBuilderPos().getX() + ", " + request.getBuilderPos().getY() + ", " + request.getBuilderPos().getZ(), x + 6, y + 4, 0xFFFFFF);
        mc.font.draw(stack, stackRequested.getHoverName().getString() + " x" + stackRequested.getCount(), x + 6, y + 15,
            request.getStatus() == RequestStatus.READY_FOR_DELIVERY ? 0xFFFFA500 : 0xFFE0E0E0);
    }
}
