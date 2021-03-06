package com.lukegraham.addictiveadditions.items;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.util.KeyboardHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AntiTrampleCharm extends DescribableItem {
    public AntiTrampleCharm(Properties properties) {
        super(properties, "Makes you not trample crops");
    }

    @SubscribeEvent
    public static void noTrample(BlockEvent.FarmlandTrampleEvent event){
        if (event.getEntity() instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) event.getEntity();
            for  (ItemStack stack : player.inventory.mainInventory){
                if (stack.getItem() instanceof AntiTrampleCharm){
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
