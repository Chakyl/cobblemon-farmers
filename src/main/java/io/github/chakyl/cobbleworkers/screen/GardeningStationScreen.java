package io.github.chakyl.cobbleworkers.screen;


import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.chakyl.cobbleworkers.CobbleWorkers;
import io.github.chakyl.cobbleworkers.network.PacketHandler;
import io.github.chakyl.cobbleworkers.network.ServerBoundSwapPriorityPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static io.github.chakyl.cobbleworkers.utils.GuiUtils.renderPokemon;

public class GardeningStationScreen extends AbstractContainerScreen<GardeningStationMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobbleWorkers.MODID, "textures/gui/gardening_station.png");
    public static final ResourceLocation SWAP_BUTTON = new ResourceLocation(CobbleWorkers.MODID, "textures/gui/swap_button.png");
    private static final Component CONTAINER_LABEL = Component.translatable("gui.cobble_workers.gardening_station");
    private static final ResourceLocation COBBLE_FONT = CobblemonResources.INSTANCE.getDEFAULT_LARGE();

    private static final int PROGRESS_TEXTURE_X = 0;
    private static final int PROGRESS_TEXTURE_Y = 166;
    private static final int PROGRESS_WIDTH = 68;
    private static final int PROGRESS_HEIGHT = 10;
    private static final int PROGRESS_GUI_X = 91;
    private static final int PROGRESS_GUI_Y = 26;
    private static final int PROGRESS_GUI_HEIGHT = PROGRESS_HEIGHT * 2;

    protected StateSwitchingButton togglePriorityButton;

    public GardeningStationScreen(GardeningStationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 245;
        this.inventoryLabelX = 26;
    }

    @Override
    protected void init() {
        super.init();
        this.togglePriorityButton = this.addRenderableWidget(new TogglePriorityButton(this.leftPos + 34, this.topPos + 20, 40, 20, this.menu.getPrioritySwapped()));
        this.togglePriorityButton.initTextureValues(0, 0, 28, 18, SWAP_BUTTON);
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        int centralX = (5 - this.font.width(CONTAINER_LABEL) / 2) + 46;
        GuiUtilsKt.drawText(pGuiGraphics, COBBLE_FONT, MutableComponent.create(this.title.getContents()), centralX, 4, false, 0xFFFFFFFF, false, 0, 0);
        GuiUtilsKt.drawText(pGuiGraphics, COBBLE_FONT, MutableComponent.create(this.playerInventoryTitle.getContents()), centralX, 74, false, 4210752, false, 0, 0);
        GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobble_workers.party").withStyle(ChatFormatting.BOLD), 205, 1, 0xFFFFFFFF, true);
        GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobble_workers.workers_assigned", this.menu.getWorkersAssigned()), 220, 112, 0xFFFFFFFF, true);
        RenderHelperKt.drawScaledText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobble_workers.swap_priority"), 44, 33, 0.7f, 1, 200, 0xFFFFFFFF, false, true, 0, 0);

        double speedMod = this.menu.getSpeedModifier();
        if (speedMod > 0) {
            GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobble_workers.speed", speedMod), centralX + 44, 45, 0xFFFFFFFF, true);
        }
        int workingRadius = this.menu.getAoeRadius();
        if (workingRadius > 0) {
            GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobble_workers.working_radius", workingRadius), centralX + 44, 56, 0xFFFFFFFF, true);
        }

        int index = 0;
        for (int level : this.menu.getPartyLevels()) {
            if (level > 0) {
                RenderHelperKt.drawScaledText(pGuiGraphics, COBBLE_FONT, Component.literal("Lv." + level), index % 2 == 0 ? 182 : 213, (((index / 2) * 31) + (index % 2 == 0 ? 15 : 23)), 0.7f, 1, 200, 0xFFFFFFFF, false, true, 0, 0);
            }
            index++;
        }
        Pokemon pokemon = this.menu.getWorkerPokemon();
        if (pokemon != null) {
            RenderHelperKt.drawScaledText(pGuiGraphics, COBBLE_FONT, Component.literal("Lv." + pokemon.getLevel()), 8, 15, 0.7f, 1, 200, 0xFFFFFFFF, false, true, 0, 0);
            GuiUtilsKt.drawText(pGuiGraphics, COBBLE_FONT, Component.translatable("info.cobble_workers.gardening_station.type." + pokemon.getPrimaryType().getName().toLowerCase()), centralX + 90, 34, false, 0xFFFFFFFF, false, 0, 0);
            if (pokemon.getSecondaryType() != null) {
                GuiUtilsKt.drawText(pGuiGraphics, COBBLE_FONT, Component.translatable("info.cobble_workers.gardening_station.type." + pokemon.getSecondaryType().getName().toLowerCase()), centralX + 90, 48, false, 0xFFFFFFFF, false, 0, 0);
            }
        }

    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        if (this.menu.getPrioritySwapped()) guiGraphics.blit(TEXTURE, x + 36, y + 34, 0, 176, 4, 4);
        renderProgressArrow(guiGraphics, x, y);
        renderPokemon(guiGraphics, x, y, this.menu.getPrimaryType(), this.menu.getSecondaryType(), -10);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + PROGRESS_GUI_X, y + PROGRESS_GUI_Y, PROGRESS_TEXTURE_X, PROGRESS_TEXTURE_Y, menu.getScaledProgress(), PROGRESS_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        if (isMouseOverCraftingTimeArea(mouseX, mouseY)) {
            Component tooltip = getCraftingTimeTooltip();
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private boolean isMouseOverCraftingTimeArea(int mouseX, int mouseY) {
        int craftingTimeAreaLeft = this.leftPos + PROGRESS_GUI_X;
        int craftingTimeAreaTop = this.topPos + PROGRESS_GUI_Y - (PROGRESS_GUI_HEIGHT / 2);
        int craftingTimeAreaRight = this.leftPos + PROGRESS_GUI_X + PROGRESS_WIDTH;
        int craftingTimeAreaBottom = this.topPos + PROGRESS_GUI_Y + (PROGRESS_GUI_HEIGHT / 2);

        return mouseX >= craftingTimeAreaLeft && mouseX <= craftingTimeAreaRight &&
                mouseY >= craftingTimeAreaTop && mouseY <= craftingTimeAreaBottom;
    }

    private Component getCraftingTimeTooltip() {
        int totalTicks = this.menu.getTotalProcessingTime();
        int currentTicks = this.menu.getCurrentProcessingTime();
        int remainingTicks = totalTicks - currentTicks;

        if (remainingTicks > 0 && this.menu.getScaledProgress() > 0) {
            int seconds = remainingTicks / 20;
            int minutes = seconds / 60;
            seconds %= 60;

            String formattedTime = String.format("%d:%02d Seconds", minutes, seconds);
            return Component.translatable("tooltip.cobble_workers.craft_station.processing_time", formattedTime);
        } else {
            return Component.translatable("tooltip.cobble_workers.craft_station.processing_time", "0:00 Seconds");
        }
    }

    private class TogglePriorityButton extends StateSwitchingButton {
        private static final Component PRIORITIZE_PRIMARY_TYPE = Component.translatable("gui.cobble_workers.prioritize_primary");
        private static final Component PRIORITIZE_SECONDARY_TYPE = Component.translatable("gui.cobble_workers.prioritize_secondary");

        public TogglePriorityButton(int x, int y, int width, int height, boolean state) {
            super(x, y, width, height, state);
            this.updateTooltip();
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.isStateTriggered = !this.isStateTriggered;
            PacketHandler.sendToServer(new ServerBoundSwapPriorityPacket());
            this.updateTooltip();
        }

        private void updateTooltip() {
            this.setTooltip(Tooltip.create(this.isStateTriggered ? PRIORITIZE_PRIMARY_TYPE : PRIORITIZE_SECONDARY_TYPE));
        }
    }
}
