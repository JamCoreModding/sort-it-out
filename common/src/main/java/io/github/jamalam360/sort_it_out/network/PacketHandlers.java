package io.github.jamalam360.sort_it_out.network;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.ServerSortableContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;

public class PacketHandlers {
	public static void register() {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, BidirectionalUserPreferencesUpdatePacket.C2S.TYPE, BidirectionalUserPreferencesUpdatePacket.C2S.STREAM_CODEC, (prefs, ctx) -> {
			ConfigManager<UserPreferences> configManager = ServerUserPreferences.INSTANCE.getPlayerConfigManager(ctx.getPlayer());
			configManager.get().invertSorting = prefs.preferences().invertSorting;
			configManager.get().comparators = prefs.preferences().comparators;
			configManager.save();
			SortItOut.LOGGER.info("Received updated preferences from client");
		});

		NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2SRequestSortPacket.TYPE, C2SRequestSortPacket.STREAM_CODEC, (packet, ctx) -> {
			if (ctx.getPlayer().containerMenu.containerId == packet.containerId()) {
				Container container = ctx.getPlayer().containerMenu.slots.get(packet.slotIndex()).container;
				ContainerSorterUtil.sortWithQuickSort(container, new ServerSortableContainer(container), ServerUserPreferences.INSTANCE.getPlayerPreferences(ctx.getPlayer()));
			}
		});
	}
}
