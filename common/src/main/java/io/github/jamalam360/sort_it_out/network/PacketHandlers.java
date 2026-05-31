package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.jamlib.api.config.ConfigManager;
import io.github.jamalam360.jamlib.api.network.Network;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.ServerSortableContainer;
import net.minecraft.world.Container;

public class PacketHandlers {
	public static void register() {
		Network.registerPayloadType(BidirectionalUserPreferencesUpdatePacket.S2C.TYPE, BidirectionalUserPreferencesUpdatePacket.S2C.Type.INSTANCE);
		Network.registerPayloadType(BidirectionalUserPreferencesUpdatePacket.C2S.TYPE, BidirectionalUserPreferencesUpdatePacket.C2S.Type.INSTANCE);
		Network.registerPayloadType(C2SRequestSortPacket.TYPE, C2SRequestSortPacket.Type.INSTANCE);

		Network.registerHandler(Network.Direction.SERVER_BOUND, BidirectionalUserPreferencesUpdatePacket.C2S.TYPE, (ctx, prefs) -> {
			ConfigManager<UserPreferences> configManager = ServerUserPreferences.INSTANCE.getPlayerConfigManager(ctx.player());
			configManager.get().invertSorting = prefs.preferences().invertSorting;
			configManager.get().comparators = prefs.preferences().comparators;
			configManager.save();
			SortItOut.LOGGER.info("Received updated preferences from client {}", ctx.player().getStringUUID());
		});

		Network.registerHandler(Network.Direction.SERVER_BOUND, C2SRequestSortPacket.TYPE, (ctx, packet) -> {
			if (ctx.player().containerMenu.containerId == packet.containerId()) {
				Container container = ctx.player().containerMenu.slots.get(packet.slotIndex()).container;
				ContainerSorterUtil.sortWithQuickSort(container, new ServerSortableContainer(container), ServerUserPreferences.INSTANCE.getPlayerPreferences(ctx.player()));
			}
		});
	}
}
