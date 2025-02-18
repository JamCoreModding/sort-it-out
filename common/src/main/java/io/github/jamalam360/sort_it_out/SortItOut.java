package io.github.jamalam360.sort_it_out;

import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.network.PacketHandlers;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;
import java.util.UUID;

public class SortItOut {
	public static final String MOD_ID = "sort_it_out";
	public static final String MOD_NAME = "Sort It Out!";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	private static final TreeMap<UUID, ConfigManager<UserPreferences>> PLAYER_PREFERENCES = new TreeMap<>();

	public static void init() {
		JamLib.checkForJarRenaming(SortItOut.class);
		PacketHandlers.register();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
