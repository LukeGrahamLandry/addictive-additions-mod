package com.lukegraham.addictiveadditions.world;

import com.lukegraham.addictiveadditions.init.BlockInit;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

// Working ore generation in forge 1.16.3!
// To get this to work, add this to the constructor of your mod's main class:
// MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, OreGen::addFeaturesToBiomes).

public class OreGen {
    public static void addFeaturesToBiomes(BiomeLoadingEvent event) {
        if (!event.getCategory().equals(Biome.Category.NETHER) && !event.getCategory().equals(Biome.Category.THEEND)) {
            // addOreToBiome(event.getGeneration(), BlockInit.SMILE_BLOCK.get(), 60, 10, 6, 20);
        }
    }

    private static void addOreToBiome(BiomeGenerationSettingsBuilder biomeGenSettings, Block oreBlock, int maxHeight, int minHeight, int veinSize, int veinsPerChunk){
        // mappings weirdness:
        // BASE_STONE_OVERWORLD might be field_241882_a
        // RANGE might be field_242907_l

        ConfiguredFeature<?, ?> feature = Feature.ORE.withConfiguration(
                new OreFeatureConfig(
                        OreFeatureConfig.FillerBlockType.field_241882_a,
                        oreBlock.getDefaultState(),
                        veinSize))
                .withPlacement(Placement.field_242907_l.configure(new TopSolidRangeConfig(minHeight, 0, maxHeight))).func_242731_b(veinsPerChunk);

        biomeGenSettings.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).add(() -> feature);
    }
}
