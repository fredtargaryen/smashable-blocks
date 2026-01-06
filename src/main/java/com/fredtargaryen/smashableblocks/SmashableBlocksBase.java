// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks;

import com.fredtargaryen.smashableblocks.registry.CustomRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

import static com.fredtargaryen.smashableblocks.DataReference.MODID;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MODID)
@EventBusSubscriber
public final class SmashableBlocksBase {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SmashableBlocksBase(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addModRegistries);

        AttachmentTypes.register(modEventBus);

        CustomRegistries.init(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        BreakSystem.INSTANCE.setup();
    }

    //@SubscribeEvent
    private void addModRegistries(DataPackRegistryEvent.NewRegistry event) {
        CustomRegistries.onDataPackRegistry(event);
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        BreakSystem.INSTANCE.importBlockBehaviours(event.getServer());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    //////////////////
    //LOGGER METHODS//
    //////////////////
    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }
}
