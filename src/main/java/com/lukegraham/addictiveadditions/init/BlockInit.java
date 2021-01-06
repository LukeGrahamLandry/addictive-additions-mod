package com.lukegraham.addictiveadditions.init;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.blocks.CursedEarth;
import com.lukegraham.addictiveadditions.blocks.MobSlayerBlock;
import com.lukegraham.addictiveadditions.blocks.VacuumHopperBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AddictiveAdditions.MOD_ID);

    public static final RegistryObject<Block> MOB_SLAYER = BLOCKS.register("mob_slayer",
            () -> new MobSlayerBlock(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).setRequiresTool()));

    public static final RegistryObject<Block> VACUUM_HOPPER = BLOCKS.register("vacuum_hopper",
            () -> new VacuumHopperBlock(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).setRequiresTool()));

    public static final RegistryObject<Block> CURSED_EARTH = BLOCKS.register("cursed_earth",
            () -> new CursedEarth(Block.Properties.create(Material.EARTH).harvestTool(ToolType.SHOVEL)));

    // automaticlly creates items for all blocks
    // you could do it manually instead by registering BlockItems in your ItemInit class
    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        // for each block we registered above...
        BLOCKS.getEntries().stream().map(RegistryObject::get).forEach( (block) -> {
            // make an item properties object that puts it in your creative tab
            final Item.Properties properties = new Item.Properties().group(ItemInit.ModItemGroup.instance);

            // make a block item that places the block
            final BlockItem blockItem = new BlockItem(block, properties);

            // register the block item with the same name as the block
            blockItem.setRegistryName(block.getRegistryName());
            registry.register(blockItem);
        });
    }
}
