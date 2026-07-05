package com.whezzel.perplayerinsomnia.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.whezzel.perplayerinsomnia.PerPlayerInsomnia;
import com.whezzel.perplayerinsomnia.PhantomAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class PhantomSettingsManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Type PLAYER_SETTINGS_TYPE = new TypeToken<Map<String, Boolean>>() {}.getType();

	private static final Path CONFIG_PATH = FabricLoader.getInstance()
			.getConfigDir()
			.resolve(PerPlayerInsomnia.MOD_ID + ".json");

	private static PhantomConfig config = new PhantomConfig();
	private static MinecraftServer server;

	private PhantomSettingsManager() {
	}

	public static void bindServer(MinecraftServer boundServer) {
		server = boundServer;
	}

	public static void load() {
		if (!Files.exists(CONFIG_PATH)) {
			save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			PhantomConfig loaded = GSON.fromJson(reader, PhantomConfig.class);
			if (loaded != null) {
				if (loaded.playerSettings == null) {
					loaded.playerSettings = new HashMap<>();
				}
				config = loaded;
			}
		} catch (IOException exception) {
			PerPlayerInsomnia.LOGGER.error("Failed to load phantom settings config", exception);
		}
	}

	public static void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException exception) {
			PerPlayerInsomnia.LOGGER.error("Failed to save phantom settings config", exception);
		}
	}

	public static boolean getDefaultEnabled() {
		return config.defaultEnabled;
	}

	public static void setDefaultEnabled(boolean enabled) {
		config.defaultEnabled = enabled;
		save();
	}

	public static boolean resolveEnabled(ServerPlayer player) {
		PhantomAccess access = (PhantomAccess) player;
		if (access.perplayerinsomnia$hasOverride()) {
			return access.perplayerinsomnia$arePhantomsEnabled();
		}

		return getStoredSetting(player.getUUID())
				.orElse(config.defaultEnabled);
	}

	public static Optional<Boolean> getStoredSetting(UUID playerId) {
		Boolean stored = config.playerSettings.get(playerId.toString());
		return Optional.ofNullable(stored);
	}

	public static void setPlayerSetting(ServerPlayer player, boolean enabled) {
		PhantomAccess access = (PhantomAccess) player;
		access.perplayerinsomnia$setPhantomsEnabled(enabled);
		config.playerSettings.put(player.getUUID().toString(), enabled);
		save();
	}

	public static void clearPlayerSetting(ServerPlayer player) {
		PhantomAccess access = (PhantomAccess) player;
		access.perplayerinsomnia$clearOverride();
		config.playerSettings.remove(player.getUUID().toString());
		save();
	}

	public static void setOfflinePlayerSetting(UUID playerId, boolean enabled) {
		config.playerSettings.put(playerId.toString(), enabled);
		save();

		if (server != null) {
			ServerPlayer onlinePlayer = server.getPlayerList().getPlayer(playerId);
			if (onlinePlayer != null) {
				setPlayerSetting(onlinePlayer, enabled);
			}
		}
	}

	public static void clearOfflinePlayerSetting(UUID playerId) {
		config.playerSettings.remove(playerId.toString());
		save();

		if (server != null) {
			ServerPlayer onlinePlayer = server.getPlayerList().getPlayer(playerId);
			if (onlinePlayer != null) {
				clearPlayerSetting(onlinePlayer);
			}
		}
	}

	public static void applyStoredState(ServerPlayer player) {
		getStoredSetting(player.getUUID()).ifPresent(enabled ->
				((PhantomAccess) player).perplayerinsomnia$setPhantomsEnabled(enabled));
	}

	public static void copyPlayerState(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
		PhantomAccess oldAccess = (PhantomAccess) oldPlayer;
		PhantomAccess newAccess = (PhantomAccess) newPlayer;

		if (oldAccess.perplayerinsomnia$hasOverride()) {
			newAccess.perplayerinsomnia$setPhantomsEnabled(oldAccess.perplayerinsomnia$arePhantomsEnabled());
		} else {
			newAccess.perplayerinsomnia$clearOverride();
		}
	}

	private static final class PhantomConfig {
		private boolean defaultEnabled = true;
		private Map<String, Boolean> playerSettings = new HashMap<>();
	}
}