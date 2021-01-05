package com.lukegraham.addictiveadditions.init;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.entities.AdhesiveProjectile;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityInit {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, AddictiveAdditions.MOD_ID);

    public static final RegistryObject<EntityType<AdhesiveProjectile>> ADHESIVE = ENTITY_TYPES.register("adhesive",
            () -> EntityType.Builder.create((EntityType.IFactory<AdhesiveProjectile>) AdhesiveProjectile::new, EntityClassification.MISC).size(0.25f, 0.25f)
                    .build(new ResourceLocation(AddictiveAdditions.MOD_ID, "adhesive").toString()));
}
