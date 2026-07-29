package io.github.chakyl.cobblemonfarmers.blockentity.renderer;

import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.chakyl.cobblemonfarmers.CobblemonFarmers;
import io.github.chakyl.cobblemonfarmers.blockentity.CrystalBallBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.HashSet;
import java.util.Set;

import static io.github.chakyl.cobblemonfarmers.event.ClientEvents.ClientModBusEvents.CRYSTAL_BALL_BALL;
import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getPokemonOffset;

public class CrystalBallBlockEntityRenderer implements BlockEntityRenderer<CrystalBallBlockEntity> {

    public CrystalBallBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static void renderItem(ItemStack stack, CrystalBallBlockEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, int combinedOverlay, float partialTick) {
        if (stack == null || stack.isEmpty()) return;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        float time = (float) pBlockEntity.getLevel().getGameTime() + partialTick;
        float fuckassBob = Mth.sin(time * 0.1F) / 2.0F + 0.5F;
        fuckassBob = (fuckassBob * fuckassBob + fuckassBob) * 0.1F;
        float rotationSpeed = time * 3.0F;
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 1.25D + (fuckassBob * 0.5D), 0.5D);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotationSpeed));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(rotationSpeed * 0.75F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(rotationSpeed * 0.5F));

        pPoseStack.scale(0.6F, 0.6F, 0.6F);

        itemRenderer.renderStatic(
                stack,
                net.minecraft.world.item.ItemDisplayContext.GROUND,
                packedLight,
                combinedOverlay,
                pPoseStack,
                pBuffer,
                pBlockEntity.getLevel(),
                (int) pBlockEntity.getBlockPos().asLong()
        );

        pPoseStack.popPose();
    }

    private static void renderCustomCube(CrystalBallBlockEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, int combinedOverlay, float partialTick) {
        float time = (float) pBlockEntity.getLevel().getGameTime() + partialTick;
        float rotationSpeed = time * 2.0F;
        float normalassBob = Mth.sin(time * 0.1F) * 0.05F;

        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 1.3D + normalassBob, 0.5D);

        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotationSpeed));
        pPoseStack.translate(-0.5D, -0.5D, -0.5D);

        BakedModel bakedModel = Minecraft.getInstance().getModelManager().getModel(CRYSTAL_BALL_BALL);
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityTranslucentCull(bakedModel.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, null).get(0).getSprite().atlasLocation()));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                pPoseStack.last(),
                consumer,
                null,
                bakedModel,
                1.0F, 1.0F, 1.0F,
                packedLight,
                combinedOverlay,
                ModelData.EMPTY,
                null
        );

        pPoseStack.popPose();
    }


    @Override
    public void render(CrystalBallBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        renderItem(pBlockEntity.getRenderItem(), pBlockEntity, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pPartialTick);
        renderCustomCube(pBlockEntity, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pPartialTick);
        PokemonEntity pokemonEntity = pBlockEntity.getWorkerEntity();
        if (pokemonEntity != null) {
            Set<String> workerAspects = pBlockEntity.getWorkerAspects();
            if (workerAspects != null) {
                pokemonEntity.getEntityData().set(PokemonEntity.getASPECTS(), workerAspects);
            }
            Set<String> newAspects = new HashSet<>(pokemonEntity.getAspects());
            newAspects.addAll(pokemonEntity.getForm().getAspects());
            pokemonEntity.getEntityData().set(PokemonEntity.getASPECTS(), newAspects);
            BlockState blockState = pBlockEntity.getBlockState();
            pPoseStack.pushPose();
            float hitbox = pokemonEntity.getPokemon().getSpecies().getHitbox().width;
            pPoseStack.translate(getPokemonOffset(blockState, hitbox, true), 0.01, getPokemonOffset(blockState, hitbox, false));
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