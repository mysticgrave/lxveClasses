package net.lxve.lxve_mods.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;
import java.util.Set;
import java.util.List;

/**
 * Capability interface for managing player classes.
 * This interface defines the methods that can be used to interact with a player's class data.
 */
public interface PlayerClassCapability extends INBTSerializable<CompoundTag> {
    Capability<PlayerClassCapability> PLAYER_CLASS = CapabilityManager.get(new CapabilityToken<PlayerClassCapability>() {});

    /**
     * Gets the player's current class.
     * @return The name of the current class, or null if no class is selected
     */
    String getCurrentClass();

    /**
     * Sets the player's current class.
     * @param playerClass The name of the class to set
     */
    void setCurrentClass(String playerClass);

    /**
     * Gets all unlocked classes for the player.
     * @return A set of class names that the player has unlocked
     */
    Set<String> getUnlockedClasses();

    /**
     * Adds a class to the player's unlocked classes.
     * @param playerClass The name of the class to unlock
     */
    void unlockClass(String playerClass);

    /**
     * Checks if the player has unlocked a specific class.
     * @param playerClass The name of the class to check
     * @return true if the player has unlocked the class, false otherwise
     */
    boolean hasUnlockedClass(String playerClass);

    /**
     * Gets the number of blocks the player has broken.
     * @return The number of blocks broken
     */
    int getBlocksBroken();

    /**
     * Increments the player's block break counter.
     */
    void incrementBlocksBroken();

    /**
     * Gets the number of mobs killed with fists.
     * @return The number of mobs killed with fists
     */
    int getMobsKilledWithFist();

    /**
     * Increments the player's mob kill counter.
     */
    void incrementMobsKilledWithFist();

    /**
     * Removes a class from the player's unlocked classes.
     * @param playerClass The name of the class to remove
     */
    void removeClass(String playerClass);

    /**
     * Gets the list of death timestamps.
     * @return List of death timestamps in milliseconds
     */
    List<Long> getDeathTimestamps();

    /**
     * Adds a new death timestamp.
     * @param timestamp The timestamp of the death in milliseconds
     */
    void addDeathTimestamp(long timestamp);

    /**
     * Clears death timestamps older than 2 minutes.
     */
    void clearOldDeathTimestamps();
} 