package com.whezzel.perplayerinsomnia;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

import java.util.function.Predicate;

public final class PhantomPermissions {
	private static volatile boolean permissionsBackendAvailable;

	private PhantomPermissions() {
	}

	public static void detectPermissionsBackend() {
		permissionsBackendAvailable = FabricLoader.getInstance().isModLoaded("luckperms");
		if (permissionsBackendAvailable) {
			PerPlayerInsomnia.info("Permissions backend detected; command access will use permission nodes");
		} else {
			PerPlayerInsomnia.info("No permissions backend detected; using vanilla command access fallbacks");
		}
	}

	public static Predicate<CommandSourceStack> requireAccess() {
		return PhantomPermissions::canAccess;
	}

	public static Predicate<CommandSourceStack> requireStatus() {
		return PhantomPermissions::canAccess;
	}

	public static Predicate<CommandSourceStack> requireToggle() {
		return PhantomPermissions::canToggle;
	}

	public static Predicate<CommandSourceStack> requireAdmin() {
		return PhantomPermissions::canAdmin;
	}

	private static boolean canAccess(CommandSourceStack source) {
		return canToggle(source) || canAdmin(source);
	}

	private static boolean canToggle(CommandSourceStack source) {
		if (!permissionsBackendAvailable) {
			return true;
		}

		return Permissions.check(source, PermissionNodes.TOGGLE, PermissionLevel.GAMEMASTERS);
	}

	private static boolean canAdmin(CommandSourceStack source) {
		if (!permissionsBackendAvailable) {
			return hasGamemasterPermission(source);
		}

		return Permissions.check(source, PermissionNodes.ADMIN, PermissionLevel.GAMEMASTERS);
	}

	private static boolean hasGamemasterPermission(CommandSourceStack source) {
		return source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS));
	}
}