package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.jamlib.api.config.ConfigManager;
import io.github.jamalam360.jamlib.api.network.Network;
import io.github.jamalam360.jamlib.api.network.PacketDirection;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.ServerSortableContainer;
import net.minecraft.world.Container;

public class PacketHandlers {
	public static void register() {
		Network.registerHandler(PacketDirection.SERVERBOUND, BidirectionalUserPreferencesUpdatePacket.C2S.KIND, (ctx, prefs) -> {
			ConfigManager<UserPreferences> configManager = ServerUserPreferences.INSTANCE.getPlayerConfigManager(ctx.getPlayer());
			configManager.get().invertSorting = prefs.preferences().invertSorting;
			configManager.get().slotSortingTrigger = prefs.preferences().slotSortingTrigger;
			configManager.get().comparators = prefs.preferences().comparators;
			configManager.save();
			SortItOut.LOGGER.info("Received updated preferences from client {}", ctx.getPlayer().getStringUUID());
		});

		Network.registerHandler(PacketDirection.SERVERBOUND, C2SRequestSortPacket.KIND, (ctx, packet) -> {
			if (ctx.getPlayer().containerMenu.containerId == packet.containerId()) {
				Container container = ctx.getPlayer().containerMenu.slots.get(packet.slotIndex()).container;
				ContainerSorterUtil.sortWithSelectionSort(container, new ServerSortableContainer(container), ServerUserPreferences.INSTANCE.getPlayerPreferences(ctx.getPlayer()));
			}
		});
	}
}
