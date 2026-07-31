package io.github.chakyl.cobblemonfarmers.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class RenderUtils {
    public static void renderBonusParticles(Level level, BlockPos blockPos, SimpleParticleType particleType) {
        double spread = 1.05;
        double x = blockPos.getX() + spread + (level.getRandom().nextDouble() - spread) * spread;
        double y = blockPos.getY() + 0.5 + spread + (level.getRandom().nextDouble() - spread) * spread;
        double z = blockPos.getZ() + spread + (level.getRandom().nextDouble() - spread) * spread;
        level.addParticle(particleType, x, y, z, 0.05, 0.01, 0.01);
    }
}
