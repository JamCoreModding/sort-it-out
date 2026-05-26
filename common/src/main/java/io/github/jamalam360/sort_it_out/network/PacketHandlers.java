package io.github.jamalam360.sort_it_out.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.ServerSortableContainer;
import net.fabricmc.api.EnvType;
import net.minecraft.world.Container;

public class PacketHandlers {
	public static void register() {
		if (Platform.getEnv() == EnvType.SERVER) {
			NetworkManager.registerS2CPayloadType(BidirectionalUserPreferencesUpdatePacket.S2C.TYPE, BidirectionalUserPreferencesUpdatePacket.S2C.STREAM_CODEC);
		}

		NetworkManager.registerReceiver(NetworkManager.Side.C2S, BidirectionalUserPreferencesUpdatePacket.C2S.TYPE, BidirectionalUserPreferencesUpdatePacket.C2S.STREAM_CODEC, (prefs, ctx) -> {
			ConfigManager<UserPreferences> configManager = ServerUserPreferences.INSTANCE.getPlayerConfigManager(ctx.getPlayer());
			configManager.get().invertSorting = prefs.preferences().invertSorting;
			configManager.get().comparators = prefs.preferences().comparators;
			configManager.save();
			SortItOut.LOGGER.info("Received updated preferences from client {}", ctx.getPlayer().getStringUUID());
		});

		NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2SRequestSortPacket.TYPE, C2SRequestSortPacket.STREAM_CODEC, (packet, ctx) -> {
			if (ctx.getPlayer().containerMenu.containerId == packet.containerId()) {
				Container container = ctx.getPlayer().containerMenu.slots.get(packet.slotIndex()).container;
				ContainerSorterUtil.sortWithQuickSort(container, new ServerSortableContainer(container), ServerUserPreferences.INSTANCE.getPlayerPreferences(ctx.getPlayer()));
			}
		});
	}
}
