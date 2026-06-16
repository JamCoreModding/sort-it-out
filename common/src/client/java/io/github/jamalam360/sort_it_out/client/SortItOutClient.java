package io.github.jamalam360.sort_it_out.client;

import io.github.jamalam360.jamlib.api.config.ConfigManager;
import io.github.jamalam360.jamlib.api.network.Network;
import io.github.jamalam360.jamlib.api.network.PacketDirection;
import io.github.jamalam360.jamlib.api.pack.PackReloadListenerRegistry;
import io.github.jamalam360.jamlib.client.api.command.ClientCommandRegistrationEvent;
import io.github.jamalam360.jamlib.client.api.events.ClientConnectionEvents;
import io.github.jamalam360.jamlib.client.api.events.ClientContainerRenderEvents;
import io.github.jamalam360.jamlib.client.api.events.ClientLevelTickEvents;
import io.github.jamalam360.jamlib.client.api.keymapping.KeyMappingRegistry;
import io.github.jamalam360.jamlib.client.api.network.ClientNetworkEvents;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.client.button.ScreenSortButtonsLoader;
import io.github.jamalam360.sort_it_out.client.mixin.AbstractContainerScreenAccessor;
import io.github.jamalam360.sort_it_out.client.worker.ClientSortWorker;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.network.C2SRequestSortPacket;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.util.CreativeModeTabLookup;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

import static io.github.jamalam360.jamlib.client.api.command.ClientCommandBuilders.literal;

public class SortItOutClient {
	public static final ConfigManager<Config> CONFIG = new ConfigManager<>(SortItOut.MOD_ID, "client_preferences", Config.class);
	public static boolean justReceivedFromServer = false;
	private static KeyMapping sortKeyMapping;
	private static boolean isClientSortingForced = false;
	private static boolean isSlotIndexOverlayEnabled = false;

	public static void init() {
		ServerUserPreferences.INSTANCE.setClientUserPreferences(CONFIG);
		PackReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, SortItOut.id("sort_button_reloader"), ScreenSortButtonsLoader.INSTANCE);
		sortKeyMapping = new KeyMapping("key.sort_it_out.sort", GLFW.GLFW_KEY_I, KeyMapping.Category.register(SortItOut.id("sort_it_out")));
		KeyMappingRegistry.register(sortKeyMapping, true);
		ClientLevelTickEvents.POST_TICK.listen(SortItOutClient::postLevelTick);
		ClientNetworkEvents.SERVER_CAPABILITIES_HANDSHAKE_COMPLETED.listen(() -> CONFIG.get().sync());
		ClientConnectionEvents.CONNECT.listen((mc) -> CreativeModeTabLookup.INSTANCE.buildLookup(mc.level));
		ClientContainerRenderEvents.RENDER_FOREGROUND.listen(SortItOutClient::renderContainerForeground);

		Network.registerHandler(PacketDirection.CLIENTBOUND, BidirectionalUserPreferencesUpdatePacket.S2C.KIND, (ctx, prefs) -> {
			justReceivedFromServer = true;
			CONFIG.get().invertSorting = prefs.preferences().invertSorting;
			CONFIG.get().slotSortingTrigger = prefs.preferences().slotSortingTrigger;
			CONFIG.get().comparators = prefs.preferences().comparators;
			CONFIG.save();
			SortItOut.LOGGER.info("Received updated preferences from server (via config-edit commands)");
		});

		ClientCommandRegistrationEvent.EVENT.listen(((dispatcher, context) -> dispatcher.register(
				literal("sortitoutc")
						.then(literal("toggle_force_client_sort")
								.executes((ctx) -> {
									isClientSortingForced = !isClientSortingForced;
									ctx.getSource().client$sendSuccess(Component.literal("Client Sorting Forced: " + isClientSortingForced));
									return 0;
								})
						)
						.then(literal("toggle_slot_index_debug_renderer")
								.executes((ctx) -> {
									isSlotIndexOverlayEnabled = !isSlotIndexOverlayEnabled;
									ctx.getSource().client$sendSuccess(Component.literal("Slot Index Debug Overlay: " + isSlotIndexOverlayEnabled));
									return 0;
								})
						)
		)));
	}

	public static void sortOnEitherSide(AbstractContainerMenu menu, Slot slot) {
		if (Network.getServerCapability().canReceive(C2SRequestSortPacket.KIND) && !isClientSortingForced) {
			Network.sendToServer(new C2SRequestSortPacket(menu.containerId, slot.index));
		} else if (!ClientSortWorker.INSTANCE.isWorking()) {
			ContainerSorterUtil.sortWithSelectionSort(slot.container, new ClientSortableContainer(slot.container), CONFIG.get());
		} else {
			return;
		}

		SortItOut.playSortSound(Minecraft.getInstance().player);
	}

	private static void postLevelTick(ClientLevel level) {
		while (sortKeyMapping.consumeClick()) {
			if (Minecraft.getInstance().gui.screen() instanceof AbstractContainerScreen<?> containerScreen) {
				int mouseX = (int) (Minecraft.getInstance().mouseHandler.xpos() * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double) Minecraft.getInstance().getWindow().getScreenWidth());
				int mouseY = (int) (Minecraft.getInstance().mouseHandler.ypos() * (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double) Minecraft.getInstance().getWindow().getScreenHeight());

				Slot slot = ((AbstractContainerScreenAccessor) containerScreen).invokeGetHoveredSlot(mouseX, mouseY);
				if (slot == null) {
					return;
				}

				sortOnEitherSide(Minecraft.getInstance().player.containerMenu, slot);
			}
		}
	}

	private static void renderContainerForeground(AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, double delta) {
		if (isSlotIndexOverlayEnabled) {
			Identifier type;

			try {
				type = BuiltInRegistries.MENU.getKey(screen.getMenu().getType());
			} catch (UnsupportedOperationException e) {
				type = null;
			}

			graphics.centeredText(Minecraft.getInstance().font, "" + type, ((AbstractContainerScreenAccessor) screen).getImageWidth() / 2, -50, 0xFFFFFF);
			graphics.centeredText(Minecraft.getInstance().font, screen.getClass().getName(), ((AbstractContainerScreenAccessor) screen).getImageWidth() / 2, -40, 0xFFFFFF);

			for (Slot slot : screen.getMenu().slots) {
				graphics.text(Minecraft.getInstance().font, "" + slot.index, slot.x, slot.y, 0xFFFFFF);
			}
		}
	}
}
