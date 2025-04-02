package net.lxve.lxve_mods.capability;

import net.minecraft.core.HolderLookup;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.lxve.lxve_mods.LxveMods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = LxveMods.MOD_ID)
public class PlayerClassCapabilitySync {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerClassCapabilitySync.class);

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        LOGGER.info("Cloning player class capability...");
        event.getOriginal().getCapability(PlayerClassCapability.PLAYER_CLASS).ifPresent(oldCap -> {
            event.getEntity().getCapability(PlayerClassCapability.PLAYER_CLASS).ifPresent(newCap -> {
                if (oldCap instanceof PlayerClassCapabilityImpl oldImpl && newCap instanceof PlayerClassCapabilityImpl newImpl) {
                    newImpl.deserializeNBT(null, oldImpl.serializeNBT(null));
                }
            });
        });
    }
} 