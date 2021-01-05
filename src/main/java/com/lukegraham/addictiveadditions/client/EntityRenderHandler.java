package com.lukegraham.addictiveadditions.client;

import com.lukegraham.addictiveadditions.AddictiveAdditions;
import com.lukegraham.addictiveadditions.init.EntityInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AddictiveAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRenderHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
       RenderingRegistry.registerEntityRenderingHandler(EntityInit.ADHESIVE.get(), (manager)-> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}