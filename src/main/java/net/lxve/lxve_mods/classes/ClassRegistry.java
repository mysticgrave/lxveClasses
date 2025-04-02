package net.lxve.lxve_mods.classes;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.lxve.lxve_mods.LxveMods;
import net.lxve.lxve_mods.capability.PlayerClassCapability;
import net.lxve.lxve_mods.capability.PlayerClassCapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * Registry for managing available classes and their unlock conditions.
 */
@Mod.EventBusSubscriber(modid = LxveMods.MOD_ID)
public class ClassRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassRegistry.class);
    private static final Map<String, ClassInfo> CLASSES = new HashMap<>();
    private static final Set<String> AVAILABLE_CLASSES = new HashSet<>();

    /**
     * Registers a new class with its unlock condition.
     * @param className The name of the class
     * @param unlockCondition The condition that must be met to unlock the class
     */
    public static void registerClass(String className, Predicate<Player> unlockCondition) {
        CLASSES.put(className, new ClassInfo(className, unlockCondition));
        AVAILABLE_CLASSES.add(className);
    }

    /**
     * Checks if a class exists.
     * @param className The name of the class to check
     * @return true if the class exists, false otherwise
     */
    public static boolean hasClass(String className) {
        return CLASSES.containsKey(className);
    }

    /**
     * Gets the unlock condition for a class.
     * @param className The name of the class
     * @return The unlock condition, or null if the class doesn't exist
     */
    public static Predicate<Player> getUnlockCondition(String className) {
        ClassInfo info = CLASSES.get(className);
        return info != null ? info.unlockCondition() : null;
    }

    /**
     * Checks if a player meets the unlock condition for a class.
     * @param player The player to check
     * @param className The name of the class to check
     * @return true if the player meets the unlock condition, false otherwise
     */
    public static boolean canUnlockClass(Player player, String className) {
        Predicate<Player> condition = getUnlockCondition(className);
        return condition != null && condition.test(player);
    }

    /**
     * Gets all registered class names.
     * @return An array of all registered class names
     */
    public static String[] getAllClassNames() {
        return CLASSES.keySet().toArray(new String[0]);
    }

    /**
     * Gets all available classes.
     * @return A set of all available class names
     */
    public static Set<String> getAvailableClasses() {
        return new HashSet<>(AVAILABLE_CLASSES);
    }

    /**
     * Internal class to store class information.
     */
    private record ClassInfo(String name, Predicate<Player> unlockCondition) {}

    /**
     * Event handler to check for class unlocks and apply effects on player tick.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerEvent event) {
        Player player = event.getEntity();
        LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
        
        capability.ifPresent(cap -> {
            // Check for class unlocks
            for (String className : CLASSES.keySet()) {
                if (!cap.hasUnlockedClass(className) && canUnlockClass(player, className)) {
                    cap.unlockClass(className);
                    // Send dramatic unlock message to the player
                    if (player instanceof ServerPlayer serverPlayer) {
                        // Send the main title
                        serverPlayer.connection.send(new ClientboundSetTitleTextPacket(
                            Component.literal("§6§lA New Power Awakens")
                        ));
                        // Send the subtitle
                        serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(
                            Component.literal("§eYou feel a new power surging within you...")
                        ));
                        // Set the timing (fade in, stay, fade out)
                        serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(20, 60, 20));
                    }
                }
            }

            // Apply class effects
            String currentClass = cap.getCurrentClass();
            if (currentClass != null) {
                // Remove all previous effects first
                player.removeAllEffects();
                
                // Apply new effects based on current class
                if ("TestClass".equals(currentClass)) {
                    // Apply permanent Strength I effect without particles
                    Holder<MobEffect> strengthEffect = ForgeRegistries.MOB_EFFECTS.getHolder(ResourceLocation.tryParse("minecraft:strength")).orElse(null);
                    if (strengthEffect != null) {
                        player.addEffect(new MobEffectInstance(strengthEffect, Integer.MAX_VALUE, 0, true, false));
                    }
                } else if ("Echofist".equals(currentClass)) {
                    // Apply permanent Strength II and Speed I effects without particles
                    Holder<MobEffect> strengthEffect = ForgeRegistries.MOB_EFFECTS.getHolder(ResourceLocation.tryParse("minecraft:strength")).orElse(null);
                    Holder<MobEffect> speedEffect = ForgeRegistries.MOB_EFFECTS.getHolder(ResourceLocation.tryParse("minecraft:speed")).orElse(null);
                    
                    if (strengthEffect != null) {
                        player.addEffect(new MobEffectInstance(strengthEffect, Integer.MAX_VALUE, 1, true, false));
                    }
                    if (speedEffect != null) {
                        player.addEffect(new MobEffectInstance(speedEffect, Integer.MAX_VALUE, 0, true, false));
                    }
                } else if ("Deathbound".equals(currentClass)) {
                    // Apply permanent Resistance II and Regeneration I effects without particles
                    Holder<MobEffect> resistanceEffect = ForgeRegistries.MOB_EFFECTS.getHolder(ResourceLocation.tryParse("minecraft:resistance")).orElse(null);
                    Holder<MobEffect> regenerationEffect = ForgeRegistries.MOB_EFFECTS.getHolder(ResourceLocation.tryParse("minecraft:regeneration")).orElse(null);
                    
                    if (resistanceEffect != null) {
                        player.addEffect(new MobEffectInstance(resistanceEffect, Integer.MAX_VALUE, 1, true, false));
                    }
                    if (regenerationEffect != null) {
                        player.addEffect(new MobEffectInstance(regenerationEffect, Integer.MAX_VALUE, 0, true, false));
                    }
                }
            }
        });
    }

    /**
     * Event handler to track block breaks for class unlocks.
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
        
        capability.ifPresent(cap -> {
            cap.incrementBlocksBroken();
        });
    }

    /**
     * Event handler to track mob kills with fists.
     */
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player && 
            event.getEntity() instanceof LivingEntity && 
            !(event.getEntity() instanceof Player)) { // Don't count player kills
            
            // Check if the player was using their fist (no item in hand)
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.isEmpty() || mainHand.getItem() == Items.AIR) {
                LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
                capability.ifPresent(cap -> {
                    cap.incrementMobsKilledWithFist();
                    LOGGER.info("Player {} killed a mob with fists. Total kills: {}", 
                        player.getName().getString(), 
                        cap.getMobsKilledWithFist());
                });
            }
        }
    }

    /**
     * Event handler to track player deaths for the Deathbound class.
     */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            LOGGER.info("Player death event triggered for player: {}", player.getName().getString());
            LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
            
            capability.ifPresent(cap -> {
                long currentTime = System.currentTimeMillis();
                List<Long> timestamps = cap.getDeathTimestamps();
                LOGGER.info("Before adding new death. Current timestamps: {}. Count: {}", 
                    timestamps, 
                    timestamps.size());
                    
                cap.addDeathTimestamp(currentTime);
                timestamps = cap.getDeathTimestamps();
                LOGGER.info("After adding new death. Current timestamps: {}. Count: {}", 
                    timestamps, 
                    timestamps.size());
            });
        }
    }
} 