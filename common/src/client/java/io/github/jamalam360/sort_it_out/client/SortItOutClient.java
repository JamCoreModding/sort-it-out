package io.github.jamalam360.sort_it_out.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.jamlib.events.client.ClientPlayLifecycleEvents;
import io.github.jamalam360.sort_it_out.C2SRequestSortPacket;
import io.github.jamalam360.sort_it_out.C2SUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.client.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

public class SortItOutClient {
	public static final ConfigManager<Config> CONFIG = new ConfigManager<>(SortItOut.MOD_ID, "client_preferences", Config.class);
	private static final KeyMapping SORT_KEY_BINDING = new KeyMapping("key.sort_it_out.sort", GLFW.GLFW_KEY_I, "category.sort_it_out");

	public static void init() {
		KeyMappingRegistry.register(SORT_KEY_BINDING);

		ClientScreenInputEvent.KEY_PRESSED_PRE.register(((minecraft, screen, keyCode, scanCode, modifiers) -> {
			if (!SORT_KEY_BINDING.matches(keyCode, scanCode)) {
				return EventResult.pass();
			}

			Player player = Minecraft.getInstance().player;
			if (screen instanceof AbstractContainerScreen<?> containerScreen) {
				int mouseX = (int)(
						Minecraft.getInstance().mouseHandler.xpos() * (double)Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth()
				);
				int mouseY = (int)(
						Minecraft.getInstance().mouseHandler.ypos() * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight()
				);

				Slot slot = ((AbstractContainerScreenAccessor)containerScreen).invokeGetHoveredSlot(mouseX, mouseY);
				if (slot == null) {
					return EventResult.pass();
				}
				triggerSort(player.containerMenu, slot.index);
				return EventResult.interruptTrue();
			}

			return EventResult.pass();
		}));

		ClientPlayLifecycleEvents.JOIN.register((mc) -> {
			NetworkManager.sendToServer(new C2SUserPreferencesUpdatePacket(CONFIG.get()));
		});
	}

	public static void triggerSort(AbstractContainerMenu menu, int slotIndex) {
		if (NetworkManager.canServerReceive(C2SRequestSortPacket.TYPE)) {
			NetworkManager.sendToServer(new C2SRequestSortPacket(menu.containerId, slotIndex));
		} else {
			// TODO: client sort here
		}
	}
}
