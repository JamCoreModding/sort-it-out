package io.github.jamalam360.sort_it_out.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.jamlib.events.client.ClientPlayLifecycleEvents;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.client.mixin.AbstractContainerScreenAccessor;
import io.github.jamalam360.sort_it_out.network.C2SRequestSortPacket;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

public class SortItOutClient {
	public static final ConfigManager<Config> CONFIG = new ConfigManager<>(SortItOut.MOD_ID, "client_preferences", Config.class);
	private static final KeyMapping SORT_KEY_BINDING = new KeyMapping("key.sort_it_out.sort", GLFW.GLFW_KEY_I, "category.sort_it_out");

	public static void init() {
		ServerUserPreferences.INSTANCE.setClientUserPreferences(CONFIG);
		KeyMappingRegistry.register(SORT_KEY_BINDING);
		ClientPlayLifecycleEvents.JOIN.register((mc) -> CONFIG.get().sync());

		// TODO: do something
		AtomicBoolean dumb = new AtomicBoolean(false);
		ClientScreenInputEvent.KEY_PRESSED_PRE.register((minecraft, screen, keyCode, scanCode, modifiers) -> {
			if (SORT_KEY_BINDING.matches(keyCode, scanCode)) {
				if (dumb.get()) {
					KeyMapping.click(InputConstants.Type.KEYSYM.getOrCreate(keyCode));
					dumb.set(false);
					return EventResult.interruptTrue();
				} else {
					dumb.set(true);
				}
			}

			return EventResult.pass();
		});

		ClientTickEvent.CLIENT_LEVEL_POST.register((level) -> {
			ClientPacketWorkQueue.INSTANCE.tick();

			if (!SORT_KEY_BINDING.consumeClick()) {
				return;
			}

			if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> containerScreen) {
				int mouseX = (int)(
						Minecraft.getInstance().mouseHandler.xpos() * (double)Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth()
				);
				int mouseY = (int)(
						Minecraft.getInstance().mouseHandler.ypos() * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight()
				);

				Slot slot = ((AbstractContainerScreenAccessor)containerScreen).invokeGetHoveredSlot(mouseX, mouseY);
				if (slot == null) {
					return;
				}

				sortOnEitherSide(Minecraft.getInstance().player.containerMenu, slot);
			}
		});
	}

	public static void sortOnEitherSide(AbstractContainerMenu menu, Slot slot) {
		if (NetworkManager.canServerReceive(C2SRequestSortPacket.TYPE)) {
			NetworkManager.sendToServer(new C2SRequestSortPacket(menu.containerId, slot.index));
		} else if (!ClientPacketWorkQueue.INSTANCE.hasWorkRemaining()) {
			ContainerSorterUtil.sortWithQuickSort(slot.container, new ClientSortableContainer(slot.container), CONFIG.get());
		}
	}
}
