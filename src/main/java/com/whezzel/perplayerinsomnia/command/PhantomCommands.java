package com.whezzel.perplayerinsomnia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.whezzel.perplayerinsomnia.PhantomPermissions;
import com.whezzel.perplayerinsomnia.storage.PhantomSettingsManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public final class PhantomCommands {
	private PhantomCommands() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("phantoms")
				.requires(PhantomPermissions.requireAccess())
				.then(Commands.literal("status")
						.requires(PhantomPermissions.requireStatus())
						.executes(PhantomCommands::showSelfStatus))
				.then(Commands.literal("enable")
						.requires(PhantomPermissions.requireToggle())
						.executes(context -> setSelfEnabled(context, true)))
				.then(Commands.literal("disable")
						.requires(PhantomPermissions.requireToggle())
						.executes(context -> setSelfEnabled(context, false)))
				.then(Commands.literal("toggle")
						.requires(PhantomPermissions.requireToggle())
						.executes(PhantomCommands::toggleSelf))
				.then(Commands.literal("set")
						.requires(PhantomPermissions.requireAdmin())
						.then(Commands.argument("targets", EntityArgument.players())
								.then(Commands.literal("enable")
										.executes(context -> setTargetsEnabled(context, true)))
								.then(Commands.literal("disable")
										.executes(context -> setTargetsEnabled(context, false)))))
				.then(Commands.literal("get")
						.requires(PhantomPermissions.requireAdmin())
						.then(Commands.argument("targets", EntityArgument.players())
								.executes(PhantomCommands::showTargetStatus)))
				.then(Commands.literal("reset")
						.requires(PhantomPermissions.requireAdmin())
						.then(Commands.argument("targets", EntityArgument.players())
								.executes(PhantomCommands::resetTargets)))
				.then(Commands.literal("default")
						.requires(PhantomPermissions.requireAdmin())
						.then(Commands.literal("get")
								.executes(PhantomCommands::showDefault))
						.then(Commands.literal("set")
								.then(Commands.argument("enabled", BoolArgumentType.bool())
										.executes(PhantomCommands::setDefault)))));
	}

	private static int showSelfStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		context.getSource().sendSuccess(() -> statusMessage(player.getName().getString(), PhantomSettingsManager.resolveEnabled(player)), false);
		return 1;
	}

	private static int setSelfEnabled(CommandContext<CommandSourceStack> context, boolean enabled) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		PhantomSettingsManager.setPlayerSetting(player, enabled);
		context.getSource().sendSuccess(() -> changedMessage(player.getName().getString(), enabled), false);
		return 1;
	}

	private static int toggleSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		boolean enabled = !PhantomSettingsManager.resolveEnabled(player);
		PhantomSettingsManager.setPlayerSetting(player, enabled);
		context.getSource().sendSuccess(() -> changedMessage(player.getName().getString(), enabled), false);
		return 1;
	}

	private static int setTargetsEnabled(CommandContext<CommandSourceStack> context, boolean enabled) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
		for (ServerPlayer target : targets) {
			PhantomSettingsManager.setPlayerSetting(target, enabled);
		}

		context.getSource().sendSuccess(() -> Component.literal("Updated phantom spawning for " + targets.size() + " player(s)."), true);
		return targets.size();
	}

	private static int showTargetStatus(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
		for (ServerPlayer target : targets) {
			context.getSource().sendSuccess(() ->
					statusMessage(target.getName().getString(), PhantomSettingsManager.resolveEnabled(target)), false);
		}
		return targets.size();
	}

	private static int resetTargets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
		for (ServerPlayer target : targets) {
			PhantomSettingsManager.clearPlayerSetting(target);
		}

		context.getSource().sendSuccess(() -> Component.literal("Reset phantom spawning preference for " + targets.size() + " player(s)."), true);
		return targets.size();
	}

	private static int showDefault(CommandContext<CommandSourceStack> context) {
		boolean enabled = PhantomSettingsManager.getDefaultEnabled();
		context.getSource().sendSuccess(() -> Component.literal("Default phantom spawning is " + (enabled ? "enabled" : "disabled") + "."), false);
		return 1;
	}

	private static int setDefault(CommandContext<CommandSourceStack> context) {
		boolean enabled = BoolArgumentType.getBool(context, "enabled");
		PhantomSettingsManager.setDefaultEnabled(enabled);
		context.getSource().sendSuccess(() -> Component.literal("Default phantom spawning set to " + (enabled ? "enabled" : "disabled") + "."), true);
		return 1;
	}

	private static Component statusMessage(String playerName, boolean enabled) {
		return Component.literal(playerName + ": phantom spawning is " + (enabled ? "enabled" : "disabled") + ".");
	}

	private static Component changedMessage(String playerName, boolean enabled) {
		return Component.literal(playerName + ": phantom spawning is now " + (enabled ? "enabled" : "disabled") + ".");
	}
}