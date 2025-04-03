package net.lxve.lxve_mods;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.lxve.lxve_mods.capability.PlayerClassCapability;
import net.lxve.lxve_mods.capability.PlayerClassCapabilityProvider;
import net.lxve.lxve_mods.classes.ClassRegistry;
import net.lxve.lxve_mods.command.TestClassCommand;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(value = LxveMods.MOD_ID)
public class LxveMods {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "lxve_mods";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogManager.getLogger();

    public LxveMods() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
        // Register commands
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        // Register class unlock conditions
        ClassRegistry.registerClass("TestClass", player -> {
            LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
            return capability.map(cap -> cap.getBlocksBroken() >= 5).orElse(false);
        });

        ClassRegistry.registerClass("Echofist", player -> {
            LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
            return capability.map(cap -> cap.getMobsKilledWithFist() >= 2).orElse(false);
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("LxveMods initialization");
        // Register the capability
        CapabilityManager.get(new CapabilityToken<PlayerClassCapability>() {});
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Add creative mode items here
    }

    @SubscribeEvent
    public void onServerStarting(PlayerEvent.PlayerLoggedInEvent event) {
        LOGGER.info("Player {} logged in", event.getEntity().getName().getString());
    }

    private void registerCommands(RegisterCommandsEvent event) {
        TestClassCommand.register(event.getDispatcher());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
