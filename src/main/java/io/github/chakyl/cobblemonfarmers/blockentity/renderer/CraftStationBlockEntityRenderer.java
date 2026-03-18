package io.github.chakyl.cobblemonfarmers.blockentity.renderer;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.cobblemonfarmers.block.CraftStationBlock;
import io.github.chakyl.cobblemonfarmers.blockentity.CraftStationBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getPokemonOffset;

public class CraftStationBlockEntityRenderer implements BlockEntityRenderer<CraftStationBlockEntity> {
    private final Map<ItemStack, Float> itemRotations = new HashMap<>();

    public CraftStationBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    private void renderItem(ItemStack stack, CraftStationBlockEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int index) {
        if (stack == null || stack.isEmpty()) return;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int maxStackDisplay = 6;
        float baseHeight = 1.01f;
        float heightIncrement = stack.getItem() instanceof BlockItem ? 0.2f : 0.03f;
        Direction direction = pBlockEntity.getBlockState().getValue(CraftStationBlock.FACING);
        boolean rotated = direction == Direction.EAST || direction == Direction.WEST;
        float indexTranslation = index == 0 ? 0.25f : 0.75f;
        for (int i = 0; i < maxStackDisplay && i < stack.getCount(); i++) {
            itemRotations.computeIfAbsent(stack, k -> 30F);
            float rotationAngle = itemRotations.get(stack);
            pPoseStack.pushPose();
            pPoseStack.translate(!rotated ? indexTranslation : 0.5f, baseHeight, rotated ? indexTranslation : 0.5f);
            pPoseStack.scale(0.4f, 0.4f, 0.4f);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(270));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(rotationAngle));
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
            pPoseStack.popPose();
            baseHeight += heightIncrement;
        }
    }

    @Override
    public void render(CraftStationBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        List<ItemStack> itemStacks = pBlockEntity.getRenderItems();
        int index = 0;
        for (ItemStack stack : itemStacks) {
            this.renderItem(stack, pBlockEntity, pPoseStack, pBuffer, index);
            index++;
        }
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        PokemonEntity pokemonEntity = pBlockEntity.getWorkerEntity();
        if (pokemonEntity != null) {
            Set<String> workerAspects = pBlockEntity.getWorkerAspects();
            if (workerAspects != null) {
                pokemonEntity.getEntityData().set(PokemonEntity.getASPECTS(), workerAspects);
            }
            BlockState blockState = pBlockEntity.getBlockState();
            pPoseStack.pushPose();
            pPoseStack.translate(getPokemonOffset(blockState, true), 0.01, getPokemonOffset(blockState, false));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(pokemonEntity.getYRot()));
            entityRenderDispatcher.render(pokemonEntity, 0, 0, 0, 0, pPartialTick, pPoseStack, pBuffer, pPackedLight);
            pPoseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}