package com.lukegraham.addictiveadditions.items.aoe_tools;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.function.Predicate;

public class AOETreeUtil {

    private static final BlockPos[] NEIGHBOR_POSITIONS = new BlockPos[26];

    static {
        NEIGHBOR_POSITIONS[0] = new BlockPos(1, 0, 0);
        NEIGHBOR_POSITIONS[1] = new BlockPos(-1, 0, 0);
        NEIGHBOR_POSITIONS[2] = new BlockPos(0, 0, 1);
        NEIGHBOR_POSITIONS[3] = new BlockPos(0, 0, -1);
        NEIGHBOR_POSITIONS[4] = new BlockPos(0, 1, 0);
        NEIGHBOR_POSITIONS[5] = new BlockPos(0, -1, 0);

        NEIGHBOR_POSITIONS[6] = new BlockPos(1, 0, 1);
        NEIGHBOR_POSITIONS[7] = new BlockPos(1, 0, -1);
        NEIGHBOR_POSITIONS[8] = new BlockPos(-1, 0, 1);
        NEIGHBOR_POSITIONS[9] = new BlockPos(-1, 0, -1);

        NEIGHBOR_POSITIONS[10] = new BlockPos(1, 1, 0);
        NEIGHBOR_POSITIONS[11] = new BlockPos(-1, 1, 0);
        NEIGHBOR_POSITIONS[12] = new BlockPos(0, 1, 1);
        NEIGHBOR_POSITIONS[13] = new BlockPos(0, 1, -1);

        NEIGHBOR_POSITIONS[14] = new BlockPos(1, -1, 0);
        NEIGHBOR_POSITIONS[15] = new BlockPos(-1, -1, 0);
        NEIGHBOR_POSITIONS[16] = new BlockPos(0, -1, 1);
        NEIGHBOR_POSITIONS[17] = new BlockPos(0, -1, -1);

        NEIGHBOR_POSITIONS[18] = new BlockPos(1, 1, 1);
        NEIGHBOR_POSITIONS[19] = new BlockPos(1, 1, -1);
        NEIGHBOR_POSITIONS[20] = new BlockPos(-1, 1, 1);
        NEIGHBOR_POSITIONS[21] = new BlockPos(-1, 1, -1);

        NEIGHBOR_POSITIONS[22] = new BlockPos(1, -1, 1);
        NEIGHBOR_POSITIONS[23] = new BlockPos(1, -1, -1);
        NEIGHBOR_POSITIONS[24] = new BlockPos(-1, -1, 1);
        NEIGHBOR_POSITIONS[25] = new BlockPos(-1, -1, -1);
    }

    public static List<BlockPos> getBlocks(PlayerEntity player, BlockPos base) {
        HashSet<BlockPos> known = new HashSet<>();
        Predicate<BlockState> matcher = state -> state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.LEAVES);
        walk(player, matcher, base, known);
        return new ArrayList<>(known);
    }

    private static void walk(PlayerEntity player, Predicate<BlockState> matcher, BlockPos base, HashSet<BlockPos> known) {
        Set<BlockPos> traversed = new HashSet<>();
        Deque<BlockPos> openSet = new ArrayDeque<>();
        openSet.add(base);
        traversed.add(base);

        while (!openSet.isEmpty()) {
            BlockPos ptr = openSet.pop();
            BlockState toCheck = player.world.getBlockState(ptr);
            if (matcher.test(toCheck) && known.add(ptr)) {
                for (BlockPos side : NEIGHBOR_POSITIONS) {
                    BlockPos offset = ptr.add(side);
                    if (traversed.add(offset)) {
                        openSet.add(offset);
                    }
                }
            }
        }
    }
}
