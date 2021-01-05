package com.lukegraham.addictiveadditions.init;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.items.*;
import com.lukegraham.addictiveadditions.items.aoe_tools.ExcavatorItem;
import com.lukegraham.addictiveadditions.items.aoe_tools.HammerItem;
import com.lukegraham.addictiveadditions.items.aoe_tools.LumberAxeItem;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AddictiveAdditions.MOD_ID);

    public static final RegistryObject<Item> WINGS_OF_BAT = ITEMS.register("wings_of_bat", () -> new BatWings(props().maxStackSize(1)));
    public static final RegistryObject<Item> WINGS_OF_SQUID = ITEMS.register("wings_of_squid", () -> new SquidWings(props().maxStackSize(1)));

    public static final RegistryObject<Item> SHULKER_BAG = ITEMS.register("shulker_bag", () -> new BagItem(props().maxStackSize(1), "Shulker"));
    public static final RegistryObject<Item> ENDER_BAG = ITEMS.register("ender_bag", () -> new EnderBag(props().maxStackSize(1)));
    public static final RegistryObject<Item> SMALL_BAG = ITEMS.register("small_bag", () -> new BagItem(props().maxStackSize(1), "Small"));

    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register("iron_hammer", () -> new HammerItem(ItemTier.IRON, 6, -3.5F, props()));
    public static final RegistryObject<Item> DIAMOND_HAMMER = ITEMS.register("diamond_hammer", () -> new HammerItem(ItemTier.DIAMOND, 6, -3.5F, props()));
    public static final RegistryObject<Item> NETHERITE_HAMMER = ITEMS.register("netherite_hammer", () -> new HammerItem(ItemTier.NETHERITE, 6, -3.5F, props()));
    public static final RegistryObject<Item> IRON_EXCAVATOR = ITEMS.register("iron_excavator", () -> new ExcavatorItem(ItemTier.IRON, 6, -3.5F, props()));
    public static final RegistryObject<Item> IRON_LUMBER_AXE = ITEMS.register("iron_lumber_axe", () -> new LumberAxeItem(ItemTier.IRON, 6, -3.5F, props()));

    public static final RegistryObject<Item> ANTI_TRAMPLE_CHARM = ITEMS.register("anti_trample_charm", () -> new AntiTrampleCharm(props()));
    public static final RegistryObject<Item> HELP_BOOK = ITEMS.register("help_book", () -> new HelpBook(props()));
    public static final RegistryObject<Item> CARDBOARD_BOX = ITEMS.register("cardboard_box", () -> new CardboardBox(props()));
    public static final RegistryObject<Item> OBSIDIAN_SHIELD = ITEMS.register("obsidian_shield", () -> new ObsidianShield(props()));
    public static final RegistryObject<Item> MAGIC_MIRROR = ITEMS.register("magic_mirror", () -> new MagicMirror(props().maxDamage(150)));
    public static final RegistryObject<Item> ARCANE_TOME = ITEMS.register("arcane_tome", () -> new ArcaneTome(props().maxStackSize(1)));

    // INGREDIENTS
    public static final RegistryObject<Item> BAT_WING = createDescriptionItem("bat_wing", "Dropped by bats and used to make Wings of the Bat and adhesive blend.");
    public static final RegistryObject<Item> ADHESIVE_BLEND = createDescriptionItem("adhesive_blend", "Smelted into Adhesive.");
    public static final RegistryObject<Item> ADHESIVE = ITEMS.register("adhesive", () -> new AdhesiveItem(props().maxStackSize(16)));
    public static final RegistryObject<Item> IRON_GEAR = createDescriptionItem("iron_gear", "Used to make machines.");
    public static final RegistryObject<Item> GOLD_GEAR = createDescriptionItem("gold_gear", "Used to make machines.");
    public static final RegistryObject<Item> DIAMOND_GEAR = createDescriptionItem("diamond_gear", "Used to make machines.");
    public static final RegistryObject<Item> NETHERITE_GEAR = createDescriptionItem("netherite_gear", "Used to make machines.");

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
            return new ItemStack(SMALL_BAG.get());
        }
    }

}
