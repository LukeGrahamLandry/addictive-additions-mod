package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EnderBag extends DescribableItem {
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.enderchest");

    public EnderBag(Properties properties) {
        super(properties, "Right click to open your ender chest");
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        EnderChestInventory enderchestinventory = playerIn.getInventoryEnderChest();
        playerIn.openContainer(new SimpleNamedContainerProvider((id, inventory, player) -> {
                return ChestContainer.createGeneric9X3(id, inventory, enderchestinventory);
            }, CONTAINER_NAME));
        playerIn.addStat(Stats.OPEN_ENDERCHEST);

        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }
}