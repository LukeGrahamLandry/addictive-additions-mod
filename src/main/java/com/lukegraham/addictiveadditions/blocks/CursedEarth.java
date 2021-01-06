package com.lukegraham.addictiveadditions.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class CursedEarth extends Block {
    private static final Random rand = new Random();

    public CursedEarth(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isRemote()){
            if (worldIn.getBlockState(pos.up()).isSolid()){
                worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
                return;
            }

            if (worldIn.getLight(pos.up()) <= 8){  // spawn
                LivingEntity mob = (LivingEntity) getNextMob().create(worldIn);
                mob.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
                worldIn.addEntity(mob);

            } else if (worldIn.canBlockSeeSky(pos.up()) && worldIn.isDaytime()){  // burn
                worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
                worldIn.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
            }

            scheduleTick(worldIn, pos);
        }
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (worldIn.isRemote()) return;

        if (worldIn.getLight(pos.up()) <= 8){  // spread
            for(int i = 0; i < 15; ++i) {
                BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (worldIn.getBlockState(blockpos).isIn(Blocks.DIRT) || worldIn.getBlockState(blockpos).isIn(Blocks.GRASS)){
                    if (!worldIn.getBlockState(blockpos.up()).isSolid())
                        worldIn.setBlockState(blockpos, this.getDefaultState());
                }
            }
        }
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
        scheduleTick(worldIn, pos);
    }

    private void scheduleTick(World worldIn, BlockPos pos) {
        int cooldown = 200 + rand.nextInt(100);
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, cooldown);
    }

    private static EntityType getNextMob(){
        int i = rand.nextInt(5);
        switch (i){
            case 0: return EntityType.ZOMBIE;
            case 1: return EntityType.SKELETON;
            case 2: return EntityType.SPIDER;
            case 3: return EntityType.CREEPER;
            case 4: {
                int j = rand.nextInt(4);
                switch (j){
                    case 0: return EntityType.WITCH;
                    case 1: return EntityType.ENDERMAN;
                    case 2:
                    case 3:
                        return EntityType.ZOMBIE;
                }
            }
        }

        return EntityType.ZOMBIE;
    }
}
