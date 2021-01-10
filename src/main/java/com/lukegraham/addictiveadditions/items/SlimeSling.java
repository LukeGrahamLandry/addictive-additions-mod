package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.events.RepairHandler;
import com.lukegraham.addictiveadditions.init.ItemInit;
import com.lukegraham.addictiveadditions.items.aoe_tools.AOEToolUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

public class SlimeSling extends DescribableItem {
    public SlimeSling(Properties properties) {
        super(properties, "Flings you into the sky");
        RepairHandler.addRepairRecipe(this, Items.SLIME_BALL, 75, 10);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity player, int timeLeft) {
        if (!player.world.isRemote() && player.isOnGround()){
            boolean lookingAtGround = AOEToolUtil.rayTracePlayer((PlayerEntity) player).getType() == RayTraceResult.Type.BLOCK;
            if (!lookingAtGround) return;

            Vector3d direction = player.getLookVec().normalize().inverse();
            int timeUsed = Math.min(this.getUseDuration(stack) - timeLeft, 40);  // cap at 2 seconds
            double magnitude = timeUsed / 3.0D;
            Vector3d velocity = direction.scale(magnitude);
            AddictiveAdditions.LOGGER.debug(velocity);
            player.addVelocity(velocity.x, velocity.y / 3, velocity.z);
            player.velocityChanged = true;

            stack.damageItem(1, player, (p) -> {
                p.sendBreakAnimation(Hand.MAIN_HAND);
            });
        }
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!playerIn.isOnGround()) return ActionResult.resultSuccess(itemstack);

        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
    }
}