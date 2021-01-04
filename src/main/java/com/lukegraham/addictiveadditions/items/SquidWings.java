package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SquidWings extends DescribableItem {
    static private int MAX = 7*20;
    static private int RECOVER_RATE = 2;
    static private double ACCELERATION = 0.2D;
    static private double MAX_SPEED = 0.25D;

    public SquidWings(Properties properties) {
        super(properties, "Allows upward flight for " + (MAX / 20) + " seconds at a time");
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (entityIn instanceof PlayerEntity && !worldIn.isRemote()){
            ServerPlayerEntity player = (ServerPlayerEntity) entityIn;

            if (getTime(stack) > 0){
                if (KeyboardHelper.isHoldingSpace()){
                    Vector3d motion = player.getMotion();
                    AddictiveAdditions.LOGGER.debug(motion.toString());
                    double y = Math.min(motion.y + ACCELERATION, MAX_SPEED);
                    player.setMotion(motion.x, y, motion.z);
                    player.connection.sendPacket(new SEntityVelocityPacket(player));

                    addTime(stack, -1);
                }
            }

            if (player.isOnGround() || player.isInWater()){
                addTime(stack, RECOVER_RATE);
            }
        }
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
