package earth.terrarium.lilwings.block;

import earth.terrarium.lilwings.entity.ButterflyEntity;
import earth.terrarium.lilwings.registry.LilWingsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ButterflyJarBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(3, 12, 3, 13, 14, 13),
            Block.box(2, 0, 2, 14, 10, 14),
            Block.box(4, 10, 4, 12, 12, 12),
            Block.box(5, 14, 5, 11, 16, 11)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public ButterflyJarBlock() {
        super(Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.5f).noOcclusion());
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntityIn, ItemStack itemStack) {
        if (!level.isClientSide() && blockEntityIn instanceof ButterflyJarBlockEntity blockEntity) {
            ButterflyEntity butterfly = blockEntity.getEntityType().create(level);
            if (butterfly != null && level instanceof ServerLevel serverLevel) {
                butterfly.load(blockEntity.getButterflyData());
                butterfly.setCatchAmount(0);
                butterfly.setPos(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);

                if (butterfly.getButterfly().particleType() != null) {
                    serverLevel.sendParticles(butterfly.getButterfly().particleType(), pos.getX() + 0.5, pos.getY() + 0.08f, pos.getZ() + 0.5, 25, 0, 0, 0, 0.5f);
                }
                level.addFreshEntity(butterfly);
            }
        }

        super.playerDestroy(level, player, pos, blockState, blockEntityIn, itemStack);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, LilWingsBlocks.BUTTERFLY_JAR_ENTITY.get(), ButterflyJarBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return Items.GLASS_BOTTLE.getDefaultInstance();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ButterflyJarBlockEntity(blockPos, blockState);
    }
}
