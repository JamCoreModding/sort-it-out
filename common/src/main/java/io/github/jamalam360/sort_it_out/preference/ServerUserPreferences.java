package io.github.jamalam360.sort_it_out.preference;

import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.TreeMap;
import java.util.UUID;

public class ServerUserPreferences {
	public static final ServerUserPreferences INSTANCE = new ServerUserPreferences();
	private final TreeMap<UUID, ConfigManager<UserPreferences>> configManagers = new TreeMap<>();
	@Nullable
	private ConfigManager<? extends UserPreferences> clientSetUserPreferences = null;

	private ServerUserPreferences() {
	}

	public UserPreferences getPlayerPreferences(Player player) {
		if (this.clientSetUserPreferences != null) {
			return this.clientSetUserPreferences.get();
		}

		return this.getPlayerConfigManager(player).get();
	}

	public ConfigManager<UserPreferences> getPlayerConfigManager(Player player) {
		if (!this.configManagers.containsKey(player.getUUID())) {
			this.configManagers.put(player.getUUID(), new ConfigManager<>(SortItOut.MOD_ID, player.getStringUUID(), UserPreferences.class));
		}

		return this.configManagers.get(player.getUUID());
	}

	public void setClientUserPreferences(ConfigManager<? extends UserPreferences> clientUserPreferences) {
		if (this.clientSetUserPreferences != null) {
			throw new IllegalStateException("setClientUserPreferences called twice");
		}

		this.clientSetUserPreferences = clientUserPreferences;
	}
}
