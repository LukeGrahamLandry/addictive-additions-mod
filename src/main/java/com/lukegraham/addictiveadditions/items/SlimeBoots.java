package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import com.lukegraham.addictiveadditions.util.ModArmorMaterial;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SlimeBoots extends ArmorItem {
    public SlimeBoots(Properties builderIn) {
        super(ModArmorMaterial.SLIME, EquipmentSlotType.FEET, builderIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("Makes you bounce"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    // static HashMap<LivingEntity, Float> fallDistance = new HashMap<>();
    @SubscribeEvent
    public static void recordBounceAmount(LivingFallEvent event){
        LivingEntity player = event.getEntityLiving();
        if (player.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof SlimeBoots && !player.getEntityWorld().isRemote()
            && !player.isSuppressingBounce()){
            // fallDistance.put(event.getEntityLiving(), event.getDistance())
            float v = Math.min(event.getDistance(), 10) / 6.0F / (event.getDamageMultiplier() + 0.5F);
            if (event.getDistance() > 10) v += event.getDistance() / 18;

            v = Math.min(v, 3.0F);
            AddictiveAdditions.LOGGER.debug(v);
            player.setMotion(player.getMotion().x * 2, v, player.getMotion().z * 2);
            player.velocityChanged = true;
            event.setCanceled(true);
        }
    }
}
