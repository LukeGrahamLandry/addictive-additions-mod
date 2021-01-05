package com.lukegraham.addictiveadditions.init;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.tile_entity.MobSlayerTile;
import com.lukegraham.addictiveadditions.tile_entity.VacuumHopperTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityInit {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, AddictiveAdditions.MOD_ID);

    public static final RegistryObject<TileEntityType<MobSlayerTile>> MOB_SLAYER
            = TILE_ENTITY_TYPES.register("mob_slayer",
            () -> TileEntityType.Builder.create(MobSlayerTile::new, BlockInit.MOB_SLAYER.get()).build(null));

    public static final RegistryObject<TileEntityType<VacuumHopperTile>> VACUUM_HOPPER = TILE_ENTITY_TYPES.register("vacuum_hopper",
            () -> TileEntityType.Builder.create(VacuumHopperTile::new, BlockInit.VACUUM_HOPPER.get()).build(null));
}