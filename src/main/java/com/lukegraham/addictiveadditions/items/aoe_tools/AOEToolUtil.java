package com.lukegraham.addictiveadditions.items.aoe_tools;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WebBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolItem;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AOEToolUtil {
    public interface IAOEtool {
        default Iterable<BlockPos> getAOEBlocks(ItemStack stack, World world, PlayerEntity player, BlockPos pos) {
            return AOEToolUtil.locateAOEBlocks(stack, player, pos);
        }
    }

    public static BlockRayTraceResult rayTracePlayer(PlayerEntity player) {
        return (BlockRayTraceResult) player.pick(player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()),0,false);
    }

    public static List<BlockPos> locateAOEBlocks(ItemStack stack, PlayerEntity player, BlockPos origin) {
        return locateAOEBlocks(stack, player, origin, rayTracePlayer(player), true);
    }


    // Based on Actually Additions drill logic
    public static List<BlockPos> locateAOEBlocks(ItemStack stack, PlayerEntity player, BlockPos origin, BlockRayTraceResult trace, boolean isBreaking) {
        if (trace.getType() != RayTraceResult.Type.BLOCK) return Collections.emptyList();

        World world = player.getEntityWorld();
        BlockState mainState = world.getBlockState(origin).getBlockState();
        if (mainState.isAir()) return Collections.emptyList();
        float mainHardness = mainState.getBlockHardness(world, origin);
        Item item = stack.getItem();


        // figure out on which axis the plane should be based on the direction the player is looking
        int radius = 1;
        int xRange = radius;
        int yRange = radius;
        int zRange = 0;
        if (trace.getFace().getAxis() == Direction.Axis.Y) {
            zRange = radius;
            yRange = 0;
        }
        if (trace.getFace().getAxis() == Direction.Axis.X) {
            xRange = 0;
            zRange = radius;
        }

        int harvestLevel = ((ToolItem)stack.getItem()).getTier().getHarvestLevel();
        boolean isRightTool = (item instanceof HammerItem && mainState.getHarvestTool() == ToolType.PICKAXE) || (item instanceof ExcavatorItem && mainState.getHarvestTool() == ToolType.SHOVEL) || (item instanceof LumberAxeItem && mainState.getHarvestTool() == ToolType.AXE);
        if (!isRightTool) return Collections.emptyList();

        ArrayList<BlockPos> toBreak = new ArrayList<>();
        for (int x = origin.getX() - xRange; x <= origin.getX() + xRange; x++) {
            for (int y = origin.getY() - yRange; y <= origin.getY() + yRange; y++) {
                for (int z = origin.getZ() - zRange; z <= origin.getZ() + zRange; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (origin == pos) continue;

                    BlockState state = world.getBlockState(pos);
                    int blockLevel = world.getBlockState(pos).getHarvestLevel();
                    boolean rightTool = (item instanceof HammerItem && state.getHarvestTool() == ToolType.PICKAXE) || (item instanceof ExcavatorItem && state.getHarvestTool() == ToolType.SHOVEL) || (item instanceof LumberAxeItem && state.getHarvestTool() == ToolType.AXE);
                    boolean similarHardness = state.getBlockHardness(world, pos) <= mainHardness + 5.0F;  // don't let you break obsidian at the speed of stone
                    boolean validHarvestLevel = blockLevel <= harvestLevel;  // don't mine netherite with an iron hammer
                    if (rightTool && similarHardness && validHarvestLevel) toBreak.add(pos);
                }
            }
        }

        return toBreak;
    }

    /**
     * Tries to break a block as if this player had broken it.  This is a complex operation.
     *
     * @param stack  The player's current held stack, main hand.
     * @param world  The player's world.
     * @param player The player that is breaking this block.
     * @param pos    The pos to break.
     * @return If the break was successful.
     */
    public static boolean breakExtraBlock(ItemStack stack, World world, PlayerEntity player, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        ToolItem item = (ToolItem) stack.getItem();

        if ((item instanceof HammerItem && state.getHarvestTool() != ToolType.PICKAXE) || (item instanceof ExcavatorItem && state.getHarvestTool() != ToolType.SHOVEL) || (item instanceof LumberAxeItem && state.getHarvestTool() != ToolType.AXE)) {
            return false;
        }
        Block block = state.getBlock();

        if (player.abilities.isCreativeMode) {
            if (state.removedByPlayer(world, pos, player, false, world.getFluidState(pos))) {
                block.onPlayerDestroy(world, pos, state);
            }

            // send update to client
            if (!world.isRemote) {
                ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(world, pos));
            }
            return true;
        }

        // callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
        stack.onBlockDestroyed(world, state, pos, player);

        // server sided handling
        if (!world.isRemote) {
            // send the blockbreak event
            int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);
            if (xp == -1) return false;

            TileEntity tileEntity = world.getTileEntity(pos);
            if (block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos))) { // boolean is if block can be harvested, checked above
                block.onPlayerDestroy(world, pos, state);
                block.harvestBlock(world, player, pos, state, tileEntity, stack);
                block.dropXpOnBlockBreak((ServerWorld) world, pos, xp);
            }

            // always send block update to client
            ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(world, pos));
        }
        // client sided handling
        else {
            // clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
            // the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.

            // following code can be found in PlayerControllerMP.onPlayerDestroyBlock
            world.playEvent(2001, pos, Block.getStateId(state));
            if (block.removedByPlayer(state, world, pos, player, true, world.getFluidState(pos))) {
                block.onPlayerDestroy(world, pos, state);
            }
            // callback to the tool
            stack.onBlockDestroyed(world, state, pos, player);
        }
        return true;
    }
}
