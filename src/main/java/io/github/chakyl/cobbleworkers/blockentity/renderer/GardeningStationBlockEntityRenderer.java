package io.github.chakyl.cobbleworkers.blockentity.renderer;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.cobbleworkers.blockentity.GardeningStationBlockEntity;
import io.github.chakyl.cobbleworkers.blockentity.MysteryMineBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class GardeningStationBlockEntityRenderer implements BlockEntityRenderer<GardeningStationBlockEntity> {
    private final Map<ItemStack, Float> itemRotations = new HashMap<>();

    public GardeningStationBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }
    @Override
    public void render(GardeningStationBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if(pBlockEntity.hasInput()) return;
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        PokemonEntity pokemonEntity = pBlockEntity.getWorkerEntity();
        if (pokemonEntity != null) {
            BlockState blockState = pBlockEntity.getBlockState();
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 1, 0.5);
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