package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MOD_ID)
public class ExampleMod {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "examplemod";


    public ExampleMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (GLFW.GLFW_KEY_B == event.getKey()) {
            minecraft.setScreen(new DrawScreen(false));
        }
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof DrawScreen && RenderGameOverlayEvent.ElementType.ALL == event.getType()) {
            //event.setCanceled(true);
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    /*ideas
    - click block
    - wirte chatmessage
    - swap items
    - combine multiple steps
    - crafting menu
     */

}
