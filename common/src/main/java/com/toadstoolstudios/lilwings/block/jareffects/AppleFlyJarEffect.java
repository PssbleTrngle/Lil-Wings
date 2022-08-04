package com.toadstoolstudios.lilwings.block.jareffects;

import com.toadstoolstudios.lilwings.block.ButterflyJarBlockEntity;
import com.toadstoolstudios.lilwings.registry.LilWingsEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class AppleFlyJarEffect implements JarEffect {
    @Override
    public void tickEffect(Level level, ButterflyJarBlockEntity blockEntity) {
        if (blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().below()).is(Blocks.GOLD_BLOCK)) {
            if ((int) (Math.random() * 100) == 1) {
                blockEntity.setEntityType(LilWingsEntities.GOLD_APPLEFLY_BUTTERFLY.entityType().get());
            }
        }
    }

    @Override
    public ParticleOptions getParticleType() {
        return null;
    }
}
