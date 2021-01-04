package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.init.ItemInit;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MagicMirror extends DescribableItem {
    Random rand = new Random();
    public MagicMirror(Properties properties) {
        super(properties, "Hold right click to return to your spawn point. Doesn't work across dimensions");
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity && !worldIn.isRemote()) {
            BlockPos pos = ((ServerPlayerEntity)entityLiving).func_241140_K_();
            if (pos == null){
                if (entityLiving.getBedPosition().isPresent()) pos = entityLiving.getBedPosition().get();
                else return stack;
            }
            entityLiving.teleportKeepLoaded(pos.getX(), pos.getY(), pos.getZ());

            stack.damageItem(1, entityLiving, (p) -> {
                p.sendBreakAnimation(Hand.MAIN_HAND);
            });
        }

        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (player.world.isRemote) {
            for(int i = 0; i < 32; ++i) {
                player.world.addParticle(ParticleTypes.PORTAL, player.getPosXRandom(0.5D), player.getPosYRandom() - 0.25D, player.getPosZRandom(0.5D), (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack stack) {
        return 50;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (handIn == Hand.OFF_HAND) return super.onItemRightClick(worldIn, playerIn, handIn);

        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
    }

    @SubscribeEvent
    public static void handleRepair(AnvilUpdateEvent event){
        boolean isEnder = event.getRight().getItem() == Items.ENDER_EYE || event.getRight().getItem() == Items.ENDER_PEARL;
        if (event.getLeft().getItem() == ItemInit.MAGIC_MIRROR.get() && isEnder){
            ItemStack out = event.getLeft().copy();
            out.setDamage(out.getDamage() - 100);
            event.setOutput(out);
            event.setCost(10);
            event.setMaterialCost(1);
        }
    }

}