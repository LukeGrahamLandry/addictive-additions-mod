package com.lukegraham.addictiveadditions.tile_entity;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.blocks.MobSlayerBlock;
import com.lukegraham.addictiveadditions.init.TileEntityInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class MobSlayerTile extends InventoryTile implements ITickableTileEntity, INamedContainerProvider {
    int time;
    double fuel_owed;
    double COST_PER_DAMAGE = 100;  // one coal is worth 1600
    public MobSlayerTile() {
        super(TileEntityInit.MOB_SLAYER.get(), 9);
        this.time = 0;
        this.fuel_owed = 0;
    }

    @Override
    public void tick() {
        if (!world.isRemote()){
            this.time++;
            if (time % 20 == 0 && hasFuel()){
                AxisAlignedBB box = getKillBox();
                List<LivingEntity> mobs = getMobsIn(box);
                damageAll(mobs);

                consumeFuel();
            }
        }
    }

    private void damageAll(List<LivingEntity> mobs) {
        for (LivingEntity mob : mobs){
            if (mob.isAlive()){
                if (mob instanceof PlayerEntity){
                    mob.attackEntityFrom(DamageSource.GENERIC, 1.0F);
                } else {
                    mob.attackEntityFrom(DamageSource.GENERIC, 20.0F);
                }
                this.fuel_owed += COST_PER_DAMAGE;
            }
        }
    }

    private List<LivingEntity> getMobsIn(AxisAlignedBB box) {
        return this.world.getEntitiesWithinAABB(LivingEntity.class, box);
    }

    private AxisAlignedBB getKillBox() {
        int sideLength = 7;
        int height = 3;
        Direction facing = this.getBlockState().get(MobSlayerBlock.FACING);

        // SOUTH
        int z1 = this.pos.getZ() + 1;
        int z2 = z1 + sideLength;
        int x1 = (int) (this.pos.getX() + Math.floor(sideLength / 2.0D)) + 1;
        int x2 = (int) (this.pos.getX() - Math.floor(sideLength / 2.0D)) - 1;
        int y1 = this.pos.getY();
        int y2 = y1 + height;

        if (facing == Direction.NORTH){
            z1 -= 1;
            z2 = z1 - sideLength;
        } else if (facing == Direction.EAST){
            x1 = this.pos.getX() + 1;
            x2 = x1 + sideLength;
            z1 = (int) (this.pos.getZ() + Math.floor(sideLength / 2.0D)) + 1;
            z2 = (int) (this.pos.getZ() - Math.floor(sideLength / 2.0D)) - 1;
        } else if (facing == Direction.WEST){
            x1 = this.pos.getX();
            x2 = x1 - sideLength;
            z1 = (int) (this.pos.getZ() + Math.floor(sideLength / 2.0D)) + 1;
            z2 = (int) (this.pos.getZ() - Math.floor(sideLength / 2.0D)) - 1;
        }

        return new AxisAlignedBB(x1,y1,z1,x2,y2,z2);
    }

    private void consumeFuel() {
        if (!hasFuel()) return;
        int fuelSlot = findFuelSlotIndex();
        ItemStack fuel = this.getStackInSlot(fuelSlot);
        int value = ForgeHooks.getBurnTime(fuel);
        while (this.fuel_owed >= value && fuel.getCount() > 0){
            this.fuel_owed -= value;
            if (fuel.getCount() == 1 && fuel.hasContainerItem()) {  // if lava bucket, return bucket
                fuel = fuel.getContainerItem();
                break;
            } else {
                fuel.shrink(1);
            }
        }

        this.setInventorySlotContents(fuelSlot, fuel);
        // world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    private int findFuelSlotIndex() {
        for (int i=0;i<9;i++){
            ItemStack stack = this.getStackInSlot(i);
            boolean isFuel = ForgeHooks.getBurnTime(stack) >= COST_PER_DAMAGE;
            if (isFuel) return i;
        }
        return -1;
    }

    private boolean hasFuel() {
        return findFuelSlotIndex() > -1;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putDouble("fuel_owed", this.fuel_owed);
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.fuel_owed = nbt.getDouble("fuel_owed");
    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new ChestContainer(ContainerType.GENERIC_9X1, id, playerInventory, this, 1);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Mob Slayer");
    }
}
