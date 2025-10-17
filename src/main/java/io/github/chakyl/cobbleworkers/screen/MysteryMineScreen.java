package io.github.chakyl.cobbleworkers.screen;


import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.chakyl.cobbleworkers.CobbleWorkers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MysteryMineScreen extends AbstractContainerScreen<MysteryMineMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(CobbleWorkers.MODID, "textures/gui/craft_station.png");
    public static final ResourceLocation ELEMENT_TEXTURE = new ResourceLocation("cobblemon:textures/gui/types_small.png");
    private static final Component CONTAINER_LABEL = Component.translatable("gui.cobble_workers.craft_station");
    private static final ResourceLocation COBBLE_FONT = CobblemonResources.INSTANCE.getDEFAULT_LARGE();

    public MysteryMineScreen(MysteryMineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
        int centralX = (5 - this.font.width(CONTAINER_LABEL) / 2);
        GuiUtilsKt.drawText(pGuiGraphics, COBBLE_FONT, MutableComponent.create(this.title.getContents()), this.inventoryLabelX + 2, 4, false, 0xFFFFFFFF,  false, 0, 0);
        GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, this.playerInventoryTitle, this.inventoryLabelX, 74, 4210752, false);
        GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobble_workers.party").withStyle(ChatFormatting.BOLD), 205, 1, 0xFFFFFFFF, true);
        double speedMod = this.menu.getSpeedModifier();
        if (speedMod > 0) {
            GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobble_workers.speed", speedMod), centralX + 70, 45, 0xFFFFFFFF, true);
        }
        int multChance = this.menu.getMultChance();
        if (multChance > 0) {
            GuiUtilsKt.drawCenteredText(pGuiGraphics, COBBLE_FONT, Component.translatable("gui.cobble_workers.mult_chance", multChance + "%"), centralX + 70, 56, 0xFFFFFFFF, true);
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
        }

    }

    protected void renderPokemon(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        Pokemon pokemon = this.menu.getWorkerPokemon();
        if (pokemon == null) return;
        ElementalType secondaryElement = pokemon.getSecondaryType();
        if (secondaryElement != null) {
            pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 56, pMouseY + 18, 0, secondaryElement.getTextureXMultiplier() * 18, 18, 18, 18, 324, 18);
        }
        pGuiGraphics.blit(ELEMENT_TEXTURE, pMouseX + 37, pMouseY + 18, 0, pokemon.getPrimaryType().getTextureXMultiplier() * 18, 18, 18, 18, 324, 18);

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderProgressArrow(guiGraphics, x, y);
        renderPokemon(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 114, y + 23, 0, 166, menu.getScaledProgress(), 10);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

    }
}