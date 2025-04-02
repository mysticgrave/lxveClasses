package net.lxve.lxve_mods.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.lxve.lxve_mods.capability.PlayerClassCapability;
import net.lxve.lxve_mods.capability.PlayerClassCapabilityProvider;
import net.lxve.lxve_mods.classes.ClassRegistry;
import net.minecraftforge.common.util.LazyOptional;

public class TestClassCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register the main command with both full name and alias
        dispatcher.register(Commands.literal("AscendantClasses")
            .requires(source -> source.hasPermission(2)) // Requires op level 2
            .then(Commands.literal("help")
                .executes(context -> showHelp(context.getSource())))
            .then(Commands.literal("list")
                .executes(context -> listClasses(context.getSource())))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.literal("unlock")
                    .then(Commands.argument("class", StringArgumentType.word())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            String className = StringArgumentType.getString(context, "class");
                            return unlockClass(context.getSource(), player, className);
                        })))
                .then(Commands.literal("set")
                    .then(Commands.argument("class", StringArgumentType.word())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            String className = StringArgumentType.getString(context, "class");
                            return setClass(context.getSource(), player, className);
                        })))
                .then(Commands.literal("remove")
                    .then(Commands.argument("class", StringArgumentType.word())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            String className = StringArgumentType.getString(context, "class");
                            return removeClass(context.getSource(), player, className);
                        })))
                .then(Commands.literal("info")
                    .executes(context -> {
                        Player player = EntityArgument.getPlayer(context, "player");
                        return showClassInfo(context.getSource(), player);
                    }))));

        // Register the alias
        dispatcher.register(Commands.literal("ac")
            .requires(source -> source.hasPermission(2)) // Requires op level 2
            .then(Commands.literal("help")
                .executes(context -> showHelp(context.getSource())))
            .then(Commands.literal("list")
                .executes(context -> listClasses(context.getSource())))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.literal("unlock")
                    .then(Commands.argument("class", StringArgumentType.word())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            String className = StringArgumentType.getString(context, "class");
                            return unlockClass(context.getSource(), player, className);
                        })))
                .then(Commands.literal("set")
                    .then(Commands.argument("class", StringArgumentType.word())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            String className = StringArgumentType.getString(context, "class");
                            return setClass(context.getSource(), player, className);
                        })))
                .then(Commands.literal("remove")
                    .then(Commands.argument("class", StringArgumentType.word())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            String className = StringArgumentType.getString(context, "class");
                            return removeClass(context.getSource(), player, className);
                        })))
                .then(Commands.literal("info")
                    .executes(context -> {
                        Player player = EntityArgument.getPlayer(context, "player");
                        return showClassInfo(context.getSource(), player);
                    }))));
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("=== AscendantClasses Help ==="), false);
        source.sendSuccess(() -> Component.literal("Available commands:"), false);
        source.sendSuccess(() -> Component.literal("/ac help - Show this help message"), false);
        source.sendSuccess(() -> Component.literal("/ac list - List all available classes"), false);
        source.sendSuccess(() -> Component.literal("/ac <player> info - Show player's current class and unlocked classes"), false);
        source.sendSuccess(() -> Component.literal("/ac <player> unlock <class> - Unlock a class for a player"), false);
        source.sendSuccess(() -> Component.literal("/ac <player> set <class> - Set a player's active class"), false);
        source.sendSuccess(() -> Component.literal("/ac <player> remove <class> - Remove a class from a player"), false);
        source.sendSuccess(() -> Component.literal("Note: You can also use /AscendantClasses instead of /ac"), false);
        return 1;
    }

    private static int listClasses(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("=== Available Classes ==="), false);
        var classes = ClassRegistry.getAvailableClasses();
        if (classes.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No classes available"), false);
        } else {
            for (String className : classes) {
                source.sendSuccess(() -> Component.literal("- " + className), false);
            }
        }
        return 1;
    }

    private static int showClassInfo(CommandSourceStack source, Player player) {
        LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
        
        capability.ifPresent(cap -> {
            String currentClass = cap.getCurrentClass();
            source.sendSuccess(() -> Component.literal("Current class: " + (currentClass != null ? currentClass : "None")), true);
            
            var unlockedClasses = cap.getUnlockedClasses();
            if (unlockedClasses.isEmpty()) {
                source.sendSuccess(() -> Component.literal("Unlocked classes: None"), true);
            } else {
                source.sendSuccess(() -> Component.literal("Unlocked classes: " + String.join(", ", unlockedClasses)), true);
            }
        });
        
        return 1;
    }

    private static int unlockClass(CommandSourceStack source, Player player, String className) {
        if (!ClassRegistry.hasClass(className)) {
            source.sendFailure(Component.literal("Class " + className + " does not exist"));
            return 0;
        }
        
        LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
        capability.ifPresent(cap -> {
            cap.unlockClass(className);
            source.sendSuccess(() -> Component.literal("Unlocked class " + className + " for player " + player.getName().getString()), true);
        });
        
        return 1;
    }

    private static int setClass(CommandSourceStack source, Player player, String className) {
        if (!ClassRegistry.hasClass(className)) {
            source.sendFailure(Component.literal("Class " + className + " does not exist"));
            return 0;
        }
        
        LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
        capability.ifPresent(cap -> {
            if (!cap.hasUnlockedClass(className)) {
                source.sendFailure(Component.literal("Player has not unlocked class " + className));
                return;
            }
            
            cap.setCurrentClass(className);
            source.sendSuccess(() -> Component.literal("Set class to " + className + " for player " + player.getName().getString()), true);
        });
        
        return 1;
    }

    private static int removeClass(CommandSourceStack source, Player player, String className) {
        if (!ClassRegistry.hasClass(className)) {
            source.sendFailure(Component.literal("Class " + className + " does not exist"));
            return 0;
        }
        
        LazyOptional<PlayerClassCapability> capability = player.getCapability(PlayerClassCapability.PLAYER_CLASS);
        capability.ifPresent(cap -> {
            if (!cap.hasUnlockedClass(className)) {
                source.sendFailure(Component.literal("Player has not unlocked class " + className));
                return;
            }
            
            // If this is their current class, clear it
            if (className.equals(cap.getCurrentClass())) {
                cap.setCurrentClass(null);
            }
            
            // Remove the class from unlocked classes
            cap.removeClass(className);
            source.sendSuccess(() -> Component.literal("Removed class " + className + " from player " + player.getName().getString()), true);
        });
        
        return 1;
    }
} 