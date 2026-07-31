package io.github.chakyl.cobblemonfarmers.blockentity.renderer;

import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.chakyl.cobblemonfarmers.block.EnergyPylonBlock;
import io.github.chakyl.cobblemonfarmers.blockentity.EnergyPylonBlockEntity;
import io.github.chakyl.cobblemonfarmers.tag.CobblemonFarmersTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static io.github.chakyl.cobblemonfarmers.utils.PokeUtils.getPokemonOffset;
import static io.github.chakyl.cobblemonfarmers.utils.RenderUtils.renderBonusParticles;

public class EnergyPylonBlockEntityRenderer implements BlockEntityRenderer<EnergyPylonBlockEntity> {
    private final Map<Item, Float> itemRotations = new HashMap<>();

    public EnergyPylonBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }


    @Override
    public void render(EnergyPylonBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

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

        if (pBlockEntity.hasLevel() && pBlockEntity.hasBonusMult()) {
            if (pBlockEntity.getLevel().getRandom().nextFloat() < 0.08F) {
                renderBonusParticles(pBlockEntity.getLevel(), pBlockEntity.getBlockPos().above(), ParticleTypes.ENCHANT);
            }
        }
        if (pBlockEntity.hasLevel() && pBlockEntity.hasBonusSpeed()) {
            if (pBlockEntity.getLevel().getRandom().nextFloat() < 0.01F) {
                renderBonusParticles(pBlockEntity.getLevel(), pBlockEntity.getBlockPos().above(), ParticleTypes.ANGRY_VILLAGER);
            }
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}