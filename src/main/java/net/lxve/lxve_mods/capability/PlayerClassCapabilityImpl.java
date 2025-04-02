package net.lxve.lxve_mods.capability;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

/**
 * Implementation of the PlayerClassCapability interface.
 * This class handles the actual storage and management of player class data.
 */
public class PlayerClassCapabilityImpl implements PlayerClassCapability, ICapabilitySerializable<CompoundTag> {
    private String currentClass = "";
    private Set<String> unlockedClasses = new HashSet<>();
    private int blocksBroken = 0;
    private int mobsKilledWithFist = 0;
    private List<Long> deathTimestamps = new ArrayList<>();

    @Override
    public String getCurrentClass() {
        return currentClass;
    }

    @Override
    public void setCurrentClass(String playerClass) {
        this.currentClass = playerClass;
    }

    @Override
    public Set<String> getUnlockedClasses() {
        return unlockedClasses;
    }

    @Override
    public void unlockClass(String playerClass) {
        if (!unlockedClasses.contains(playerClass)) {
            unlockedClasses.add(playerClass);
        }
    }

    @Override
    public void removeClass(String playerClass) {
        unlockedClasses.remove(playerClass);
    }

    @Override
    public boolean hasUnlockedClass(String playerClass) {
        return unlockedClasses.contains(playerClass);
    }

    @Override
    public int getBlocksBroken() {
        return blocksBroken;
    }

    @Override
    public void incrementBlocksBroken() {
        blocksBroken++;
    }

    @Override
    public int getMobsKilledWithFist() {
        return mobsKilledWithFist;
    }

    @Override
    public void incrementMobsKilledWithFist() {
        mobsKilledWithFist++;
    }

    @Override
    public List<Long> getDeathTimestamps() {
        return deathTimestamps;
    }

    @Override
    public void addDeathTimestamp(long timestamp) {
        deathTimestamps.add(timestamp);
        // Sort timestamps to ensure proper order
        deathTimestamps.sort(Long::compareTo);
        // Only clear old timestamps if we have more than 2
        if (deathTimestamps.size() > 2) {
            clearOldDeathTimestamps();
        }
    }

    @Override
    public void clearOldDeathTimestamps() {
        // Keep the most recent 2 deaths
        while (deathTimestamps.size() > 2) {
            deathTimestamps.remove(0); // Remove oldest timestamp
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("currentClass", currentClass);
        tag.putInt("blocksBroken", blocksBroken);
        tag.putInt("mobsKilledWithFist", mobsKilledWithFist);
        
        CompoundTag unlockedClassesTag = new CompoundTag();
        int i = 0;
        for (String playerClass : unlockedClasses) {
            unlockedClassesTag.putString("class" + i++, playerClass);
        }
        tag.put("unlockedClasses", unlockedClassesTag);
        
        CompoundTag deathTimestampsTag = new CompoundTag();
        for (i = 0; i < deathTimestamps.size(); i++) {
            deathTimestampsTag.putLong("timestamp" + i, deathTimestamps.get(i));
        }
        tag.put("deathTimestamps", deathTimestampsTag);
        
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("currentClass")) {
            tag.getString("currentClass").ifPresent(value -> currentClass = value);
        }
        if (tag.contains("blocksBroken")) {
            tag.getInt("blocksBroken").ifPresent(value -> blocksBroken = value);
        }
        if (tag.contains("mobsKilledWithFist")) {
            tag.getInt("mobsKilledWithFist").ifPresent(value -> mobsKilledWithFist = value);
        }
        
        unlockedClasses.clear();
        if (tag.contains("unlockedClasses")) {
            tag.getCompound("unlockedClasses").ifPresent(unlockedClassesTag -> {
                for (int i = 0; i < unlockedClassesTag.size(); i++) {
                    String key = "class" + i;
                    if (unlockedClassesTag.contains(key)) {
                        unlockedClassesTag.getString(key).ifPresent(playerClass -> {
                            if (!playerClass.isEmpty()) {
                                unlockedClasses.add(playerClass);
                            }
                        });
                    }
                }
            });
        }
        
        deathTimestamps.clear();
        if (tag.contains("deathTimestamps")) {
            tag.getCompound("deathTimestamps").ifPresent(deathTimestampsTag -> {
                for (int i = 0; i < deathTimestampsTag.size(); i++) {
                    String key = "timestamp" + i;
                    if (deathTimestampsTag.contains(key)) {
                        deathTimestampsTag.getLong(key).ifPresent(deathTimestamps::add);
                    }
                }
            });
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == PlayerClassCapability.PLAYER_CLASS ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
    }
} 