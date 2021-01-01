package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class BatWings extends Item {
    private int MAX = 30*20;
    private int RECOVER_RATE = 2;

    public BatWings(Properties properties) {
        super(properties);
    }

    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("Allows creative flight for " + (MAX / 20) + " seconds at a time"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (entityIn instanceof PlayerEntity && !worldIn.isRemote()){
            PlayerEntity player = (ServerPlayerEntity) entityIn;

            if (getTime(stack) == 0){
                if (!player.isCreative() && !player.isSpectator()) {
                    player.abilities.allowFlying = false;
                    player.abilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
            } else {
                player.abilities.allowFlying = true;
                player.sendPlayerAbilities();
                if (player.abilities.isFlying){
                    addTime(stack, -1);
                }
            }

            if (!player.abilities.isFlying && (player.isOnGround() || player.isInWater())){
                addTime(stack, RECOVER_RATE);
            }
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
        if (!player.isCreative() && !player.isSpectator()) {
            player.abilities.allowFlying = false;
            player.abilities.isFlying = false;
            player.sendPlayerAbilities();
        }
        return true;
    }

    public boolean showDurabilityBar(ItemStack stack){
        return getTime(stack) != MAX;
    }

    // @return 0.0 for 100% (no damage / full bar), 1.0 for 0% (fully damaged / empty bar)
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - (getTime(stack) / (double)MAX);
    }

    private int getTime(ItemStack stack){
        if (!stack.hasTag()) return 0;
        CompoundNBT tag = stack.getTag();
        if (!tag.contains("time")) return 0;
        return tag.getInt("time");
    }

    private void addTime(ItemStack stack, int amount){
        CompoundNBT tag = stack.getTag();
        if (!stack.hasTag()) tag = new CompoundNBT();
        int time = 0;
        if (tag.contains("time")) time = tag.getInt("time");
        time += amount;
        time = Math.min(time, MAX);
        time = Math.max(time, 0);
        tag.putInt("time", time);
        stack.setTag(tag);
    }
}
