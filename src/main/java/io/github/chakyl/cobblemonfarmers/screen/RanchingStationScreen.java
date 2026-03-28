package io.github.chakyl.cobblemonfarmers.screen;


import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.ArrayList;
import java.util.List;

import static io.github.chakyl.cobblemonfarmers.utils.GuiUtils.renderPokemonTypesOnly;

public class RanchingStationScreen extends AbstractContainerScreen<RanchingStationMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/gui/ranching_station.png");
    public static final ResourceLocation ACTIONS_TEXTURE = new ResourceLocation(CobblemonFarmers.MODID, "textures/gui/ranching_station_actions.png");
    public static final ResourceLocation ELEMENT_TEXTURE = new ResourceLocation("cobblemon:textures/gui/types_small.png");
    public static final ResourceLocation SWAP_BUTTON = new ResourceLocation(CobblemonFarmers.MODID, "textures/gui/swap_button.png");
    private static final Component CONTAINER_LABEL = Component.translatable("gui.cobblemon_farmers.ranching_station");
    private static final ResourceLocation COBBLE_FONT = CobblemonResources.INSTANCE.getDEFAULT_LARGE();

    private static final int PROGRESS_TEXTURE_X = 0;
    private static final int PROGRESS_TEXTURE_Y = 166;
    private static final int PROGRESS_WIDTH = 24;
    private static final int PROGRESS_HEIGHT = 10;
    private static final int RP_GUI_X = 84;
    private static final int RP_GUI_Y = 63;
    private static final int PROGRESS_GUI_HEIGHT = PROGRESS_HEIGHT * 2;

    protected StateSwitchingButton togglePriorityButton;

    public RanchingStationScreen(RanchingStationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 245;
        this.inventoryLabelX = 26;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        int centralX = (5 - this.font.width(CONTAINER_LABEL) / 2) + 34;
        GuiUtilsKt.drawText(pGuiGraphics, COBBLE_FONT, MutableComponent.create(this.title.getContents()), centralX + 8, 4, false, 0xFFFFFFFF, false, 0, 0);
        GuiUtilsKt.drawText(pGuiGraphics, COBBLE_FONT, MutableComponent.create(this.playerInventoryTitle.getContents()), centralX + 8, 74, false, 4210752, false, 0, 0);
        GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobblemon_farmers.party").withStyle(ChatFormatting.BOLD), 205, 1, 0xFFFFFFFF, true);
        GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobblemon_farmers.actions").withStyle(ChatFormatting.BOLD), -16, 1, 0xFFFFFFFF, true);
        GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobblemon_farmers.workers_assigned", this.menu.getWorkersAssigned()), 220, 112, 0xFFFFFFFF, true);

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
        guiGraphics.blit(ACTIONS_TEXTURE, x - 36, y, 0, 0, 36, 98);
        int iconSize = 16;
        int iconOffsetX = x - 25;
        if (this.menu.getCanShear()) guiGraphics.blit(ACTIONS_TEXTURE, iconOffsetX, y + 18, 0, 112, iconSize, iconSize);
        if (this.menu.getCanForage())
            guiGraphics.blit(ACTIONS_TEXTURE, iconOffsetX, y + 46, iconSize, 112, iconSize, iconSize);
        if (this.menu.getCanMilk())
            guiGraphics.blit(ACTIONS_TEXTURE, iconOffsetX, y + 74, iconSize * 2, 112, iconSize, iconSize);
        int hearts = this.menu.getRanchingPower();
        int heartSize = 8;
        for (int i = 0; i < hearts; i++) {
            guiGraphics.blit(TEXTURE, x + RP_GUI_X + (i * heartSize) + 1, y + RP_GUI_Y, 0, 176, heartSize, heartSize);

        }
        renderPokemonTypesOnly(guiGraphics, x, y, this.menu.getWorkerPokemon());
    }

    private boolean isMouseOverHearts(int mouseX, int mouseY) {
        int heartWidth = 9 * 10;
        int ranchingPowerAreaLeft = this.leftPos + RP_GUI_X;
        int ranchingPowerAreaTop = this.topPos + RP_GUI_Y - (8 / 2);
        int ranchingPowerAreaRight = this.leftPos + RP_GUI_X + heartWidth;
        int ranchingPowerAreaBottom = this.topPos + RP_GUI_Y + (8 / 2);

        return mouseX >= ranchingPowerAreaLeft && mouseX <= ranchingPowerAreaRight &&
                mouseY >= ranchingPowerAreaTop && mouseY <= ranchingPowerAreaBottom;
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        if (isMouseOverHearts(mouseX, mouseY)) {
            List<Component> tooltipList = new ArrayList<>();
            tooltipList.add(Component.translatable("tooltip.cobblemon_farmers.ranching_station.ranching_power", this.menu.getRanchingPower()));
            tooltipList.add(Component.translatable("tooltip.cobblemon_farmers.ranching_station.ranching_power_desc", this.menu.getRanchingPower()).withStyle(ChatFormatting.GRAY));
            guiGraphics.renderComponentTooltip(this.font, tooltipList,  mouseX,  mouseY);
        }
    }

}