package net.lxve.lxve_mods;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LxveMods.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Configs for " + LxveMods.MOD_ID);

        // Add your config options here
        // Example:
        // BUILDER.comment("General settings").push("general");
        // BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
} 