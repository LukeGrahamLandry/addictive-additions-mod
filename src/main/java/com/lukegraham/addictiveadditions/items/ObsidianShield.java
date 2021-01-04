package com.lukegraham.addictiveadditions.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.lukegraham.addictiveadditions.init.ItemInit;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ObsidianShield extends ShieldItem {
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public ObsidianShield(Properties builder) {
        super(builder.setISTER(() -> ISTER::new));

        ImmutableMultimap.Builder<Attribute, AttributeModifier> mods = ImmutableMultimap.builder();
        mods.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "AntiKnockback modifier", 1.0D, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = mods.build();

        ItemModelsProperties.func_239418_a_(this, new ResourceLocation("blocking"), (p_239421_0_, p_239421_1_, p_239421_2_) -> {
            return p_239421_2_ != null && p_239421_2_.isHandActive() && p_239421_2_.getActiveItemStack() == p_239421_0_ ? 1.0F : 0.0F;
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote() && entityIn.getFireTimer() > 0){
            entityIn.extinguish();
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        boolean validSlot = equipmentSlot == EquipmentSlotType.MAINHAND || equipmentSlot == EquipmentSlotType.OFFHAND;
        return validSlot ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("A shield that blocks knockback and extinguishes fire"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    static class ISTER extends ItemStackTileEntityRenderer{
        ShieldModel model = new ShieldModel();
        public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
            Item item = stack.getItem();
            boolean flag = stack.getChildTag("BlockEntityTag") != null;
            matrixStack.push();
            matrixStack.scale(1.0F, -1.0F, -1.0F);
            // change next line to change texture
            RenderMaterial rendermaterial = flag ? ModelBakery.LOCATION_SHIELD_BASE : ModelBakery.LOCATION_SHIELD_NO_PATTERN;
            IVertexBuilder ivertexbuilder = rendermaterial.getSprite().wrapBuffer(ItemRenderer.getEntityGlintVertexBuilder(buffer, this.model.getRenderType(rendermaterial.getAtlasLocation()), true, stack.hasEffect()));
            this.model.func_228294_b_().render(matrixStack, ivertexbuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            if (flag) {
                List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.getPatternColorData(ShieldItem.getColor(stack), BannerTileEntity.getPatternData(stack));
                BannerTileEntityRenderer.func_241717_a_(matrixStack, buffer, combinedLight, combinedOverlay, this.model.func_228293_a_(), rendermaterial, false, list, stack.hasEffect());
            } else {
                this.model.func_228293_a_().render(matrixStack, ivertexbuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            matrixStack.pop();
        }
    }

}
