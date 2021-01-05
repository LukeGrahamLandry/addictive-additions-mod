package com.lukegraham.addictiveadditions.entities;

import com.lukegraham.addictiveadditions.init.EntityInit;
import com.lukegraham.addictiveadditions.init.ItemInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class AdhesiveProjectile extends SnowballEntity {
    public AdhesiveProjectile(EntityType<? extends SnowballEntity> p_i50159_1_, World p_i50159_2_) {
        super(p_i50159_1_, p_i50159_2_);
        setItem(new ItemStack(ItemInit.ADHESIVE.get()));
    }

    public AdhesiveProjectile(World worldIn, LivingEntity throwerIn) {
        this(worldIn, throwerIn.getPosX(), throwerIn.getPosYEye() - (double)0.1F, throwerIn.getPosZ());
        this.setShooter(throwerIn);
    }

    public AdhesiveProjectile(World worldIn, double x, double y, double z) {
        this(EntityInit.ADHESIVE.get(), worldIn);
        this.setPosition(x,y,z);
    }

    protected Item getDefaultItem() {
        return ItemInit.ADHESIVE.get();
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        if (entity instanceof LivingEntity){
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60*20, 4));
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.POISON, 5*20, 1));
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
