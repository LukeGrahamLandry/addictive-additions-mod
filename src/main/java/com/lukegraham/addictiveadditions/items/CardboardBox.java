package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class CardboardBox extends BlockItem {
    public CardboardBox(Properties properties) {
        super(Blocks.CHEST, properties);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("Shift right click a chest to pick it up along with its contents"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTag();
    }

    // pickup a chest
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!stack.hasTag() && !worldIn.isRemote() && KeyboardHelper.isHoldingShift()){
            BlockRayTraceResult ray = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.NONE);
            if (ray.getType() != RayTraceResult.Type.BLOCK) return super.onItemRightClick(worldIn, playerIn, handIn);

            TileEntity tile = worldIn.getTileEntity(ray.getPos());
            if (!(tile instanceof ChestTileEntity)) return super.onItemRightClick(worldIn, playerIn, handIn);

            CompoundNBT chestData = tile.write(new CompoundNBT());
            CompoundNBT tag = new CompoundNBT();
            tag.put("BlockEntityTag", chestData);
            stack.setTag(tag);

            ((ChestTileEntity)tile).clear();
            worldIn.setBlockState(ray.getPos(), Blocks.AIR.getDefaultState());
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getItem().hasTag())
            return super.onItemUse(context);
        else
            return onItemRightClick(context.getWorld(), context.getPlayer(), context.getHand()).getType();
    }

    public String getTranslationKey(){
        return super.getDefaultTranslationKey();
    }
}
