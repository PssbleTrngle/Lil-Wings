package earth.terrarium.lilwings.block.jareffects;

import earth.terrarium.lilwings.block.ButterflyJarBlockEntity;
import earth.terrarium.lilwings.registry.LilWingsParticles;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CrystalpuffJarEffect implements JarEffect {

    private static final int MAX_TIME = 20 * 5;
    private static final int MAX_GROW_TIME = 20 * 5;
    private int growTime;
    private int lastParticle;

    List<BlockPos> amethystArea = Util.make(() -> {
        List<BlockPos> list = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(-3, -2, -3, 3, 5, 3)) {
            list.add(pos.immutable());
        }

        return list;
    });


    private BlockPos crystalPos;
    private int checkCooldown;

    @Override
    public void tickEffect(Level level, ButterflyJarBlockEntity blockEntity) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        if (crystalPos == null) {
            checkCooldown++;

            if (checkCooldown >= MAX_TIME) {
                crystalPos = findNearestCrystal(level, blockEntity.getBlockPos());
                checkCooldown = 0;
            }
        } else {
            growTime++;
            lastParticle++;

            if (lastParticle >= 10) {
                spawnParticle(serverLevel, crystalPos, 0.5, 0.2f, 0.5);
                spawnParticle(serverLevel, crystalPos, 0.5, 0.2f, 0.5);
                spawnParticle(serverLevel, crystalPos, 0.5, 0.2f, 0.5);
                lastParticle = 0;
            }

            BlockState state = level.getBlockState(crystalPos);
            if (state.is(Blocks.SMALL_AMETHYST_BUD)) {
                checkAndGrow(level, Blocks.MEDIUM_AMETHYST_BUD);
            } else if (state.is(Blocks.MEDIUM_AMETHYST_BUD)) {
                checkAndGrow(level, Blocks.LARGE_AMETHYST_BUD);
            } else if (state.is(Blocks.LARGE_AMETHYST_BUD)) {
                checkAndGrow(level, Blocks.AMETHYST_CLUSTER);
            } else {
                crystalPos = null;
                growTime = 0;
            }
        }
    }

    public void checkAndGrow(Level level, Block targetBlock) {
        if (growTime >= MAX_GROW_TIME) {
            level.setBlock(crystalPos, targetBlock.withPropertiesOf(level.getBlockState(crystalPos)), Block.UPDATE_ALL);
            growTime = 0;
            crystalPos = null;
        }
    }

    @Override
    public ParticleOptions getParticleType() {
        return LilWingsParticles.AMETHYST_GROW.get();
    }

    public BlockPos findNearestCrystal(Level level, BlockPos jarPos) {
        for (BlockPos pos : amethystArea) {
            BlockPos relativePos = jarPos.offset(pos);
            BlockState state = level.getBlockState(relativePos);
            if (!state.isAir() && (state.is(Blocks.SMALL_AMETHYST_BUD) || state.is(Blocks.MEDIUM_AMETHYST_BUD) || state.is(Blocks.LARGE_AMETHYST_BUD))) {
                return relativePos;
            }
        }
        return null;
    }
}
