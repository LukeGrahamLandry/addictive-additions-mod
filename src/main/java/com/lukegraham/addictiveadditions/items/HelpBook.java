package com.lukegraham.addictiveadditions.items;


import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.init.ItemInit;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HelpBook extends WritableBookItem {
    public HelpBook(Properties builder) {
        super(builder);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(new StringTextComponent("Learn about the features of Addictive Additions"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        setContents(stack);

        Minecraft.getInstance().displayGuiScreen(new ReadBookScreen(new ReadBookScreen.WrittenBookInfo(stack)));

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    private void setContents(ItemStack stack){
        CompoundNBT tag = stack.getTag();
        if (tag == null) tag = new CompoundNBT();

        tag.putString("author", "Luke");
        tag.putString("title", "Addictive Additions Help");
        tag.putBoolean("resolved", true);

        ListNBT pages = getPages();
        tag.put("pages", pages);

        stack.setTag(tag);
    }

    private ListNBT getPages(){
        ListNBT pages = new ListNBT();

        addPage("This mod (Addictive Additions) reimplements many of my favourite features from other mods to make your life more convenient. The rest of this book lists its features. Use JEI to see recipes.", pages);
        addPage("Squid wings allow direct upwards flight for 7 seconds at a time. Bat wings allow creative flight for 30 seconds at a time.", pages);
        addPage("Small bags hold 1 row of items. Shulker bags hold 3 rows of items. Ender bags open your ender chest.", pages);
        addPage("Hammers (iron, diamond, netherite) are a 3x3 pickaxe. Excavator (iron) is a 3x3 shovel. Lumber axe (iron) chops down a whole tree.", pages);
        addPage("Anti-Trample charm prevents you from breaking farm land when you fall on it.", pages);
        addPage("The Magic Mirror teleports you to your spawn point", pages);
        addPage("The mob slayer kills entities in a 7x7 square in front of it (requires fuel). Vacuum hopper picks up items within 7 blocks. Cursed Earth spawns mobs very quickly, spreads in darkness and burns in day light", pages);
        addPage("A Cardboard Box lets you pick up a chest and retain its contents. An Obsidian Shield grants knock back resistance and extinguishes fire. An Arcane Tome stores experience points", pages);
        addPage("", pages);
        addPage("", pages);

        return pages;
    }

    private void addPage(String content, ListNBT pages){
        INBT page = StringNBT.valueOf("{\"text\": \"" + content + "\"}");
        // INBT page = (INBT) StringNBT.valueOf(ITextComponent.Serializer.toJson(itextcomponent));
        pages.add(page);
    }

    // Give the book the first time they join the world
    private static final String NBT_KEY = AddictiveAdditions.MOD_ID + ".first_joined";
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {

            CompoundNBT data = player.getPersistentData();
            CompoundNBT persistent;
            if (!data.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
                data.put(PlayerEntity.PERSISTED_NBT_TAG, (persistent = new CompoundNBT()));
            } else {
                persistent = data.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
            }

            if (!persistent.contains(NBT_KEY)) {
                persistent.putBoolean(NBT_KEY, true);

                ItemStack stack = new ItemStack(ItemInit.HELP_BOOK.get());
                player.inventory.addItemStackToInventory(stack);
            }
        }
    }
}