package io.github.chakyl.cobblemonfarmers.blockentity.renderer;

import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.cobblemonfarmers.blockentity.RanchingStationBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RanchingStationBlockEntityRenderer implements BlockEntityRenderer<RanchingStationBlockEntity> {
    private final Map<ItemStack, Float> itemRotations = new HashMap<>();

    public RanchingStationBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RanchingStationBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        PokemonEntity pokemonEntity = pBlockEntity.getWorkerEntity();
        if (pokemonEntity != null) {
            Set<String> workerAspects = pBlockEntity.getWorkerAspects();
            if (workerAspects != null) {
                pokemonEntity.getEntityData().set(PokemonEntity.getASPECTS(), workerAspects);
            }
            Set<String> newAspects = new HashSet<>(pokemonEntity.getAspects());
            newAspects.addAll(pokemonEntity.getForm().getAspects());
            pokemonEntity.getEntityData().set(PokemonEntity.getASPECTS(), newAspects);
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 0.2, 0.5);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(pokemonEntity.getYRot()));
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<?> renderer = dispatcher.getRenderer(pokemonEntity);
            if (renderer instanceof PokemonRenderer pRenderer) {
                pRenderer.render(pokemonEntity, 0, pPartialTick, pPoseStack, pBuffer, pPackedLight);
            }
            pPoseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}