package com.whezzel.perplayerinsomnia;

import com.whezzel.perplayerinsomnia.command.PhantomCommands;
import com.whezzel.perplayerinsomnia.integration.LuckPermsIntegration;
import com.whezzel.perplayerinsomnia.storage.PhantomSettingsManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerPlayerInsomnia implements ModInitializer {
	public static final String MOD_ID = "perplayerinsomnia";
	public static final String LOG_PREFIX = "[PerPlayerInsomnia] ";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void info(String message, Object... args) {
		LOGGER.info(LOG_PREFIX + message, args);
	}

	public static void warn(String message, Object... args) {
		if (args.length > 0 && args[args.length - 1] instanceof Throwable throwable) {
			LOGGER.warn(LOG_PREFIX + message, throwable);
			return;
		}

		LOGGER.warn(LOG_PREFIX + message, args);
	}

	public static void error(String message, Object... args) {
		if (args.length > 0 && args[args.length - 1] instanceof Throwable throwable) {
			LOGGER.error(LOG_PREFIX + message, throwable);
			return;
		}

		LOGGER.error(LOG_PREFIX + message, args);
	}

	@Override
	public void onInitialize() {
		PhantomSettingsManager.load();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				PhantomCommands.register(dispatcher));

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			PhantomSettingsManager.bindServer(server);
			PhantomPermissions.detectPermissionsBackend();
			LuckPermsIntegration.registerPermissionNodes();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server ->
				PhantomSettingsManager.save());

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
				PhantomSettingsManager.copyPlayerState(oldPlayer, newPlayer));

		ServerPlayerEvents.JOIN.register(PhantomSettingsManager::applyStoredState);

		info("Initialized");
	}
}