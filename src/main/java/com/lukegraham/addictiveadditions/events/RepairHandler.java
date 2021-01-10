package com.lukegraham.addictiveadditions.events;

import com.lukegraham.addictiveadditions.init.ItemInit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RepairHandler {
    static ArrayList<RepairData> repairRecipes = new ArrayList<>();

    @SubscribeEvent
    public static void handleRepair(AnvilUpdateEvent event){
        ItemStack tool = event.getLeft();
        Item material = event.getRight().getItem();
        AtomicBoolean done = new AtomicBoolean(false);
        repairRecipes.forEach((repairData) -> {
            if (!done.get() && tool.getItem() == repairData.repairableItem && material == repairData.materialItem){
                tool.setDamage(tool.getDamage() - repairData.amountToRepair);
                event.setOutput(tool);
                event.setCost(repairData.levelCost);
                event.setMaterialCost(repairData.materialCost);
                done.set(true);
            }
        });
    }

    // add a new item to be repairable in an anvil
    public static void addRepairRecipe(Item repairableItem, Item materialItem, int durabilityToRestore, int levelCost){
        repairRecipes.add(new RepairData(repairableItem, materialItem, durabilityToRestore, levelCost, 1));
    }

    protected static class RepairData {
        protected Item repairableItem;
        protected Item materialItem;
        protected int amountToRepair;
        protected int levelCost;
        protected int materialCost;
        protected RepairData(Item repairableItemIn, Item materialItemIn, int amountToRepairIn, int levelCostIn, int materialCostIn){
            this.repairableItem = repairableItemIn;
            this.amountToRepair = amountToRepairIn;
            this.levelCost = levelCostIn;
            this.materialCost = materialCostIn;
            this.materialItem = materialItemIn;
        }
    }
}
