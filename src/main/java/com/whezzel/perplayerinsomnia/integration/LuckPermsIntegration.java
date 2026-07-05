package com.whezzel.perplayerinsomnia.integration;

import com.whezzel.perplayerinsomnia.PerPlayerInsomnia;
import com.whezzel.perplayerinsomnia.PermissionNodes;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class LuckPermsIntegration {
	private LuckPermsIntegration() {
	}

	public static void registerPermissionNodes() {
		if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
			return;
		}

		try {
			Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
			Method getApi = providerClass.getMethod("get");
			Object api = getApi.invoke(null);

			Field pluginField = api.getClass().getDeclaredField("plugin");
			pluginField.setAccessible(true);
			Object plugin = pluginField.get(api);

			Method getRegistry = plugin.getClass().getMethod("getPermissionRegistry");
			Object registry = getRegistry.invoke(plugin);

			Method offer = registry.getClass().getMethod("offer", String.class);
			for (String permission : PermissionNodes.all()) {
				offer.invoke(registry, permission);
			}

			PerPlayerInsomnia.info("Registered {} permission node(s) with LuckPerms", PermissionNodes.all().size());
		} catch (Exception exception) {
			PerPlayerInsomnia.warn("Failed to register permission nodes with LuckPerms", exception);
		}
	}
}