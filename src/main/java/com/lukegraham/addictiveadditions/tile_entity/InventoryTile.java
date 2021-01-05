package com.lukegraham.addictiveadditions.tile_entity;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.items.BagItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public abstract class InventoryTile extends TileEntity implements IInventory {
    private final int size;
    private NonNullList<ItemStack> items;
    public InventoryTile(TileEntityType<?> tileEntityTypeIn, int inventorySize) {
        super(tileEntityTypeIn);
        this.size = inventorySize;
        this.items = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);

        int i = 0;
        while (nbt.contains(String.valueOf(i))) {
            CompoundNBT tag = nbt.getCompound(String.valueOf(i));
            ItemStack stack = ItemStack.read(tag);
            this.items.set(i, stack);
            i++;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);

        for (int i=0;i<this.items.size();i++){
            ItemStack stack = this.items.get(i);
            CompoundNBT tag = stack.write(new CompoundNBT());
            nbt.put(String.valueOf(i), tag);
        }

        return nbt;
    }

    @Override
    public int getSizeInventory() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        this.markDirty();
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.items, index, count);
        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        this.markDirty();
        return ItemStackHelper.getAndRemove(this.items, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.items.set(index, stack);
        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        this.markDirty();
    }


    // returns the remaining stack. (ItemStack.EMPTY if the whole stack was stored)
    public ItemStack addStackToInventory(ItemStack stack){
        for (int i=0;i<this.getSizeInventory();i++){
            ItemStack currentStack = this.getStackInSlot(i);
            if (currentStack.isEmpty()) {
                this.setInventorySlotContents(i, stack);
                return ItemStack.EMPTY;
            }

            boolean sameItem = ItemStack.areItemsEqual(stack, currentStack);
            if (sameItem){
                int storable = currentStack.getMaxStackSize() - currentStack.getCount();

                if (storable >= stack.getCount()){
                    currentStack.grow(stack.getCount());
                    this.setInventorySlotContents(i, currentStack);
                    return ItemStack.EMPTY;
                }

                currentStack.grow(storable);
                this.setInventorySlotContents(i, currentStack);
                stack.shrink(storable);
            }
        }

        return stack;
    }
}

