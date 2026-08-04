package io.github.chakyl.cobblemonfarmers.blockentity.renderer;

import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.cobblemonfarmers.blockentity.CrystalBallBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getPokemonOffset;
import static io.github.chakyl.cobblemonfarmers.utils.RenderUtils.renderBonusParticles;
import static io.github.chakyl.cobblemonfarmers.utils.RenderUtils.renderTheyOrb;

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

    @Override
    public void render(CrystalBallBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        renderItem(pBlockEntity.getRenderItem(), pBlockEntity, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pPartialTick);
        renderTheyOrb(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, (float) pBlockEntity.getLevel().getGameTime() + pPartialTick);
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
        if (pBlockEntity.hasLevel() && pBlockEntity.hasBonusSpeed()) {
            if (pBlockEntity.getLevel().getRandom().nextFloat() < 0.01F) {
                renderBonusParticles(pBlockEntity.getLevel(), pBlockEntity.getBlockPos(), ParticleTypes.ANGRY_VILLAGER);
            }
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}