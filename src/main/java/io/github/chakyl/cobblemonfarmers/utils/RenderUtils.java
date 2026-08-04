package io.github.chakyl.cobblemonfarmers.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;

import static io.github.chakyl.cobblemonfarmers.event.ClientEvents.ClientModBusEvents.CRYSTAL_BALL_BALL;

public class RenderUtils {
    public static void renderBonusParticles(Level level, BlockPos blockPos, SimpleParticleType particleType) {
        double spread = 1.05;
        double x = blockPos.getX() + spread + (level.getRandom().nextDouble() - spread) * spread;
        double y = blockPos.getY() + 0.5 + spread + (level.getRandom().nextDouble() - spread) * spread;
        double z = blockPos.getZ() + spread + (level.getRandom().nextDouble() - spread) * spread;
        level.addParticle(particleType, x, y, z, 0.05, 0.01, 0.01);
    }

    public static void renderTheyOrb(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, int combinedOverlay, float tickTime) {
        renderTheyOrb(pPoseStack, pBuffer, packedLight, combinedOverlay, tickTime, 0.0f);
    }

    public static void renderTheyOrb(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, int combinedOverlay, float tickTime, float verticalOffset) {
        float rotationSpeed = tickTime * 2.0F;
        float normalassBob = Mth.sin(tickTime * 0.1F) * 0.05F;

        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 1.3D + normalassBob + verticalOffset, 0.5D);

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

}
