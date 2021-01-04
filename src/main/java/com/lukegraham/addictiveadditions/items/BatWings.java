package com.lukegraham.addictiveadditions.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class BatWings extends DescribableItem {
    static private int MAX = 30*20;
    static private int RECOVER_RATE = 2;

    public BatWings(Properties properties) {
        super(properties, "Allows creative flight for " + (MAX / 20) + " seconds at a time");
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (entityIn instanceof PlayerEntity && !worldIn.isRemote()){
            PlayerEntity player = (ServerPlayerEntity) entityIn;

            if (getTime(stack) == 0)
                setFlightState(player, false);
            else {
                setFlightState(player, true);
                if (player.abilities.isFlying){
                    addTime(stack, -1);
                }
            }

            // recharge fly time while not flying
            if (!player.abilities.isFlying && (player.isOnGround() || player.isInWater())){
                addTime(stack, RECOVER_RATE);
            }
        }
    }

    // stop flying if you drop the item
    @Override
    public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
        setFlightState(player, false);
        return true;
    }

    private void setFlightState(PlayerEntity player, boolean allowedToFly){
        if (allowedToFly){
            player.abilities.allowFlying = true;
        } else {
            if (!player.isCreative() && !player.isSpectator()) {
                player.abilities.allowFlying = false;
                player.abilities.isFlying = false;
            }
        }

        // Sync with client
        player.sendPlayerAbilities();
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
