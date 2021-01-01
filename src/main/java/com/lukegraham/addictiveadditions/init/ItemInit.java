package com.lukegraham.addictiveadditions.init;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.items.BatWings;
import com.lukegraham.addictiveadditions.items.DescribableItem;
import com.lukegraham.addictiveadditions.items.SquidWings;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AddictiveAdditions.MOD_ID);

    public static final RegistryObject<Item> WINGS_OF_BAT = ITEMS.register("wings_of_bat",
            () -> new BatWings(new Item.Properties().group(ModItemGroup.instance)));

    public static final RegistryObject<Item> WINGS_OF_SQUID = ITEMS.register("wings_of_squid",
            () -> new SquidWings(new Item.Properties().group(ModItemGroup.instance)));

    public static final RegistryObject<Item> BAT_WING = createDescriptionItem("bat_wing", "Dropped by bats and used to craft Wings of the Bat.");

    private static RegistryObject<Item> createDescriptionItem(String name, String description){
        return ITEMS.register(name, () -> new DescribableItem(props(), description));
    }

    private static Item.Properties props(){
        return new Item.Properties().group(ModItemGroup.instance);
    }
    // a new creative tab
    public static class ModItemGroup extends ItemGroup {
        public static final ModItemGroup instance = new ModItemGroup(ItemGroup.GROUPS.length, "addictiveadditions");
        private ModItemGroup(int index, String label) {
            super(index, label);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(WINGS_OF_BAT.get());
        }
    }

}
