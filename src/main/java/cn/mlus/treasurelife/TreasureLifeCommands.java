package cn.mlus.treasurelife;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TreasureLifeCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register /treasurelife command (OP only)
        registerAdminCommand(dispatcher, "treasurelife");
        // Register /tl alias (OP only)
        registerAdminCommand(dispatcher, "tl");

        // Register public /lives command for players to check their own lives
        registerPublicCommand(dispatcher, "lives");
    }

    private static void registerAdminCommand(CommandDispatcher<CommandSourceStack> dispatcher, String name) {
        dispatcher.register(
            Commands.literal(name)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("lives")
                    .executes(TreasureLifeCommands::showLives)
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(TreasureLifeCommands::showOtherLives)))
                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, 100))
                            .executes(TreasureLifeCommands::setLives))))
                .then(Commands.literal("add")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                            .executes(TreasureLifeCommands::addLives))))
                .then(Commands.literal("remove")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                            .executes(TreasureLifeCommands::removeLives))))
        );
    }

    private static void registerPublicCommand(CommandDispatcher<CommandSourceStack> dispatcher, String name) {
        dispatcher.register(
            Commands.literal(name)
                .executes(TreasureLifeCommands::showOwnLives)
        );
    }

    private static int showOwnLives(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            int lives = LifeManager.getLives(player);
            context.getSource().sendSuccess(
                () -> Component.translatable("commands.treasurelife.lives_self", lives),
                false
            );
            return lives;
        }
        return 0;
    }

    private static int showLives(CommandContext<CommandSourceStack> context) {
        return showOwnLives(context);
    }

    private static int showOtherLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        int lives = LifeManager.getLives(target);
        context.getSource().sendSuccess(
            () -> Component.translatable("commands.treasurelife.lives_other", target.getName().getString(), lives),
            true
        );
        return lives;
    }

    private static int setLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        LifeManager.setLives(target, amount);

        context.getSource().sendSuccess(
            () -> Component.translatable("commands.treasurelife.set", target.getName().getString(), amount),
            true
        );

        // If player has lives and is in spectator, respawn them
        if (LifeManager.hasLives(target) && target.isSpectator()) {
            LifeManager.respawnAsSurvival(target);
        } else if (!LifeManager.hasLives(target) && !target.isSpectator()) {
            LifeManager.respawnAsSpectator(target);
        }

        return amount;
    }

    private static int addLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        int newLives = LifeManager.getLives(target) + amount;
        LifeManager.setLives(target, newLives);

        context.getSource().sendSuccess(
            () -> Component.translatable("commands.treasurelife.add", amount, target.getName().getString(), newLives),
            true
        );

        // If player now has lives and was in spectator, respawn them
        if (LifeManager.hasLives(target) && target.isSpectator()) {
            LifeManager.respawnAsSurvival(target);
        }

        return newLives;
    }

    private static int removeLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        int newLives = Math.max(0, LifeManager.getLives(target) - amount);
        LifeManager.setLives(target, newLives);

        context.getSource().sendSuccess(
            () -> Component.translatable("commands.treasurelife.remove", amount, target.getName().getString(), newLives),
            true
        );

        // If player now has no lives, put them in spectator
        if (!LifeManager.hasLives(target) && !target.isSpectator()) {
            LifeManager.respawnAsSpectator(target);
        }

        return newLives;
    }
}
