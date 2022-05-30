package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
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

    private boolean down = false;

    //@SubscribeEvent
    public void keydown(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (GLFW.GLFW_KEY_LEFT_CONTROL == event.getKey()) {
            if (GLFW.GLFW_PRESS == event.getAction()) {
                down = true;
                minecraft.mouseHandler.releaseMouse();
            } else if (GLFW.GLFW_RELEASE == event.getAction()) {
                down = false;
                minecraft.mouseHandler.grabMouse();
            }
        }
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (GLFW.GLFW_KEY_B == event.getKey()) {
            minecraft.setScreen(new DrawScreen());
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if (down) {
            if (event.player.tickCount % 20 == 0) {
                System.out.println(Minecraft.getInstance().mouseHandler.xpos());
            }
        }
    }


    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        PacketHandler.init();
    }

    /*ideas
    - click block
    - wirte chatmessage
     */

}
