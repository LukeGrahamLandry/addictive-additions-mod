package com.lukegraham.addictiveadditions.items.aoe_tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class LumberAxeItem extends AxeItem implements AOEToolUtil.IAOEtool {
    public LumberAxeItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("Chops down a whole tree"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        List<BlockPos> AOEBlocks = AOETreeUtil.getBlocks(player,pos.toImmutable());
        if (!KeyboardHelper.isHoldingShift() && player instanceof ServerPlayerEntity) {
            for (BlockPos pos1 : AOEBlocks) {
                AOEToolUtil.breakExtraBlock(stack, player.world,player, pos1);
            }
        }
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return state.isIn(BlockTags.LEAVES) ? 100 : super.getDestroySpeed(stack, state) / 4.0F;
    }

    public Iterable<BlockPos> getAOEBlocks(ItemStack stack, World world, PlayerEntity player, BlockPos pos){
        return AOETreeUtil.getBlocks(player, pos);
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float strength = 1 + (EnchantmentHelper.getKnockbackModifier(attacker) / 2.0F);
        target.applyKnockback(strength,  MathHelper.sin(attacker.rotationYaw * ((float)Math.PI / 180F)), (-MathHelper.cos(attacker.rotationYaw * ((float)Math.PI / 180F))));
        return super.hitEntity(stack, target, attacker);
    }
}
