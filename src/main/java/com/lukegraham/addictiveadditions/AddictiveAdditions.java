package com.lukegraham.addictiveadditions;

import com.lukegraham.addictiveadditions.init.*;
import com.lukegraham.addictiveadditions.world.OreGen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file and the MOD_ID constant below
@Mod("addictiveadditions")
public class AddictiveAdditions
{
    // Let's you print to the console with FirstMod.LOGGER.debug(String)
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "addictiveadditions";

    public AddictiveAdditions() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        ItemInit.ITEMS.register(modEventBus);     // registers your items
        BlockInit.BLOCKS.register(modEventBus);   // registers your blocks
        EntityInit.ENTITY_TYPES.register(modEventBus);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, OreGen::addFeaturesToBiomes);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void doClientStuff(final FMLClientSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {}

}
