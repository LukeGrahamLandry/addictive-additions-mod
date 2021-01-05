package com.lukegraham.addictiveadditions.tile_entity;

import com.lukegraham.addictiveadditions.init.TileEntityInit;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class VacuumHopperTile extends InventoryTile implements ITickableTileEntity, INamedContainerProvider {
    int time;
    public VacuumHopperTile() {
        super(TileEntityInit.VACUUM_HOPPER.get(), 27);
        this.time = 0;
    }

    @Override
    public void tick() {
        if (!world.isRemote()){
            this.time++;
            if (time % 20 == 0){
                AxisAlignedBB box = getArea();
                List<ItemEntity> items = getItemsIn(box);
                pickupAll(items);
            }
        }
    }

    private void pickupAll(List<ItemEntity> items) {
        for (ItemEntity item : items){
            ItemStack remainingStack = this.addStackToInventory(item.getItem());
            if (remainingStack.isEmpty()){
                item.remove();
            } else {
                item.setItem(remainingStack);
            }
        }
    }

    private List<ItemEntity> getItemsIn(AxisAlignedBB box) {
        return this.world.getEntitiesWithinAABB(ItemEntity.class, box);
    }

    private AxisAlignedBB getArea() {
        int radius = 7;

        int z1 = this.pos.getZ() + radius;
        int z2 = this.pos.getZ() - radius;
        int x1 = this.pos.getX() + radius;
        int x2 = this.pos.getX() - radius;
        int y1 = this.pos.getY() + radius;
        int y2 = this.pos.getY() - radius;

        return new AxisAlignedBB(x1,y1,z1,x2,y2,z2);
    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new ChestContainer(ContainerType.GENERIC_9X3, id, playerInventory, this, 3);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Vacuum Hopper");
    }
}
