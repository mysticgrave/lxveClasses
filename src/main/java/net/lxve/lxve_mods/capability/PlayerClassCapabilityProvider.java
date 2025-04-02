package net.lxve.lxve_mods.capability;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.lxve.lxve_mods.LxveMods;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = LxveMods.MOD_ID)
public class PlayerClassCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    private final PlayerClassCapabilityImpl backend = new PlayerClassCapabilityImpl();
    private final LazyOptional<PlayerClassCapability> optional = LazyOptional.of(() -> backend);

    public static final ResourceLocation ID = ResourceLocation.tryParse(LxveMods.MOD_ID + ":player_class");

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == PlayerClassCapability.PLAYER_CLASS ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return backend.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        backend.deserializeNBT(provider, nbt);
    }

    public PlayerClassCapability getBackend() {
        return backend;
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ID, new PlayerClassCapabilityProvider());
        }
    }
} 