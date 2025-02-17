package io.github.jamalam360.sort_it_out;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.sort.ContainerSorter;
import io.github.jamalam360.sort_it_out.sort.ServerSortableContainer;
import io.github.jamalam360.sort_it_out.sort.SortableContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

		NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2SUserPreferencesUpdatePacket.TYPE, C2SUserPreferencesUpdatePacket.STREAM_CODEC, (prefs, ctx) -> {
			ConfigManager<UserPreferences> configManager = getPlayerConfigManager(ctx.getPlayer());
			configManager.get().invertSorting = prefs.preferences().invertSorting;
			configManager.get().sortMode = prefs.preferences().sortMode;
			configManager.save();
			LOGGER.info("Updated config for player {}", ctx.getPlayer().getUUID());
		});

		NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2SRequestSortPacket.TYPE, C2SRequestSortPacket.STREAM_CODEC, (packet, ctx) -> {
			if (ctx.getPlayer().containerMenu.containerId == packet.containerId()) {
				AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
				Container container = menu.slots.get(packet.slotIndex()).container;
				SortableContainer sortableContainer = new ServerSortableContainer(container);

				if (container instanceof Inventory) {
					ContainerSorter.sortContainer(sortableContainer, 9, 27, SortItOut.getPlayerPreferences(ctx.getPlayer()));
				} else {
					ContainerSorter.sortContainer(sortableContainer, SortItOut.getPlayerPreferences(ctx.getPlayer()));
				}
			}
		});
	}

	public static UserPreferences getPlayerPreferences(Player player) {
		return getPlayerConfigManager(player).get();
	}

	private static ConfigManager<UserPreferences> getPlayerConfigManager(Player player) {
		if (!PLAYER_PREFERENCES.containsKey(player.getUUID())) {
			PLAYER_PREFERENCES.put(player.getUUID(), new ConfigManager<>(MOD_ID, player.getStringUUID(), UserPreferences.class));
		}

		return PLAYER_PREFERENCES.get(player.getUUID());
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
