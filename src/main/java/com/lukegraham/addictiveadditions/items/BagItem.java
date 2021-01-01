package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BagItem extends Item implements INamedContainerProvider {
    public static String NBT_TAG = AddictiveAdditions.MOD_ID + ".baginventory";
    private String type;

    public BagItem(Properties properties, String typeIn) {
        super(properties);
        this.type = typeIn;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("Holds " + getSize() + " stacks of items, right click to open"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (handIn == Hand.OFF_HAND)  return super.onItemRightClick(worldIn, playerIn, handIn);
        playerIn.openContainer(this);
        return ActionResult.resultSuccess(playerIn.getHeldItem(Hand.MAIN_HAND));
    }


    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(this.type + " Bag");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        IInventory inventory = getInventory(player.getHeldItem(Hand.MAIN_HAND));

        if (this.type.equals("Small")){
            return new ChestContainer(ContainerType.GENERIC_9X1, id, playerInventory, inventory, 1);
        } else if (this.type.equals("Shulker")){
            return new ShulkerBoxContainer(id, playerInventory, inventory);
        }

        throw new ValueException("INVALID BAG TYPE: " + this.type);
    }

    private int getSize(){
        return type.equals("Small") ? 9 : 27;
    }

    private IInventory getInventory(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (!stack.hasTag() || !tag.contains(NBT_TAG)) {
            return new BagInventory(new CompoundNBT(), getSize(), stack);
        } else {
            CompoundNBT nbt = tag.getCompound(NBT_TAG);
            return new BagInventory(nbt, getSize(), stack);
        }
    }

    public static class BagInventory implements IInventory{
        private static final String BAGID_NBT_TAG = AddictiveAdditions.MOD_ID + "bagID";
        private NonNullList<ItemStack> items;
        private int size;
        private String id;
        public BagInventory(CompoundNBT nbt, int sizeIn, ItemStack theBag){
            this.size = sizeIn;
            this.items = NonNullList.withSize(this.size, ItemStack.EMPTY);
            int i = 0;
            while (nbt.contains(String.valueOf(i))) {
                CompoundNBT tag = nbt.getCompound(String.valueOf(i));
                ItemStack stack = ItemStack.read(tag);
                this.items.set(i, stack);
                i++;
            }

            this.id = UUID.randomUUID().toString();
            if (!theBag.hasTag()) theBag.setTag(new CompoundNBT());
            CompoundNBT tag = theBag.getTag();
            tag.putString(BAGID_NBT_TAG, id);
            theBag.setTag(tag);
        }

        public CompoundNBT writeToNBT(){
            CompoundNBT nbt = new CompoundNBT();

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
            ItemStack itemstack = ItemStackHelper.getAndSplit(this.items, index, count);
            return itemstack;
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            return ItemStackHelper.getAndRemove(this.items, index);
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            this.items.set(index, stack);
        }

        @Override
        public void markDirty() {

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
            for (ItemStack stack : player.inventory.mainInventory){
                CompoundNBT tag = stack.getTag();
                if (!stack.hasTag()) tag = new CompoundNBT();
                if (tag.contains(BAGID_NBT_TAG) && tag.getString(BAGID_NBT_TAG).equals(this.id)){
                    tag.put(BagItem.NBT_TAG, this.writeToNBT());
                    stack.setTag(tag);
                }
            }
        }
    }
}