package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class ArcaneTome extends Item {
    public ArcaneTome(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("Right click to store xp, shift right click to retrieve."));
            tooltip.add(new StringTextComponent("Currently holding " + getXP(stack) + " xp"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
        ItemStack stack = playerIn.getHeldItem(handIn);
        int xp = getXP(stack);

        int e = playerIn.xpBarCap();
        if (KeyboardHelper.isHoldingShift()) {
            if (xp >= e) {
                xp -= e;
                playerIn.giveExperiencePoints(e);
            } else {
                playerIn.giveExperiencePoints(xp);
                xp = 0;
            }
        } else {
            if (playerIn.experienceTotal >= e) {
                xp += e;
                playerIn.giveExperiencePoints(-e);
            } else {
                xp += playerIn.experienceTotal;
                playerIn.giveExperiencePoints(-playerIn.experienceTotal);
            }
        }

        setXP(stack, xp);

        return super.onItemRightClick(worldIn, playerIn, handIn);

    }

    private int getXP(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) return 0;

        return tag.getInt("xp");
    }

    private void setXP(ItemStack stack, int xp) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) tag = new CompoundNBT();
        tag.putInt("xp", xp);
        stack.setTag(tag);
    }
}
