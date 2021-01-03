package com.lukegraham.addictiveadditions.items.aoe_tools;

import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class LumberAxeItem extends AxeItem implements AOEToolUtil.IAOEtool {
    public LumberAxeItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("Chops down a whole tree"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        List<BlockPos> AOEBlocks = AOETreeUtil.getBlocks(player,pos.toImmutable());
        if (!KeyboardHelper.isHoldingShift() && player instanceof ServerPlayerEntity) {
            for (BlockPos pos1 : AOEBlocks) {
                AOEToolUtil.breakExtraBlock(stack, player.world,player, pos1);
            }
        }
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return state.isIn(BlockTags.LEAVES) ? 100 : super.getDestroySpeed(stack, state) / 4.0F;
    }

    public Iterable<BlockPos> getAOEBlocks(ItemStack stack, World world, PlayerEntity player, BlockPos pos){
        return AOETreeUtil.getBlocks(player, pos);
    }
}
