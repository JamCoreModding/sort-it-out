package io.github.jamalam360.sort_it_out.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.literal;

public class SortItOutClient {
	public static final ConfigManager<Config> CONFIG = new ConfigManager<>(SortItOut.MOD_ID, "client_preferences", Config.class);
	private static final KeyMapping SORT_KEY_BINDING = new KeyMapping("key.sort_it_out.sort", GLFW.GLFW_KEY_I, "category.sort_it_out");
	private static boolean isClientSortingForced = false;

	public static void init() {
		ServerUserPreferences.INSTANCE.setClientUserPreferences(CONFIG);
		KeyMappingRegistry.register(SORT_KEY_BINDING);
		ClientTickEvent.CLIENT_LEVEL_POST.register(SortItOutClient::postLevelTick);
		ClientPlayLifecycleEvents.JOIN.register((mc) -> CONFIG.get().sync());
		ClientGuiEvent.RENDER_CONTAINER_FOREGROUND.register((SortItOutClient::renderContainerForeground));
		ClientScreenInputEvent.KEY_RELEASED_PRE.register(SortItOutClient::keyReleased);

		if (Platform.isDevelopmentEnvironment()) {
			ClientCommandRegistrationEvent.EVENT.register(((dispatcher, context) -> dispatcher.register(literal("sortitout").then(literal("toggle_force_client_sort").executes((ctx) -> {
				isClientSortingForced = !isClientSortingForced;
				ctx.getSource().arch$sendSuccess(() -> Component.literal("Client Sorting Forced: " + isClientSortingForced), false);
				return 0;
			})))));
		}
	}

	public static void sortOnEitherSide(AbstractContainerMenu menu, Slot slot) {
		if (NetworkManager.canServerReceive(C2SRequestSortPacket.TYPE) && !isClientSortingForced) {
			NetworkManager.sendToServer(new C2SRequestSortPacket(menu.containerId, slot.index));
		} else if (!ClientPacketWorkQueue.INSTANCE.hasWorkRemaining()) {
			ContainerSorterUtil.sortWithQuickSort(slot.container, new ClientSortableContainer(slot.container), CONFIG.get());
		}
	}

	private static void postLevelTick(ClientLevel level) {
		ClientPacketWorkQueue.INSTANCE.tick();

		if (SORT_KEY_BINDING.consumeClick() && Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> containerScreen) {
			int mouseX = (int) (Minecraft.getInstance().mouseHandler.xpos() * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double) Minecraft.getInstance().getWindow().getScreenWidth());
			int mouseY = (int) (Minecraft.getInstance().mouseHandler.ypos() * (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double) Minecraft.getInstance().getWindow().getScreenHeight());

			Slot slot = ((AbstractContainerScreenAccessor) containerScreen).invokeGetHoveredSlot(mouseX, mouseY);
			if (slot == null) {
				return;
			}

			sortOnEitherSide(Minecraft.getInstance().player.containerMenu, slot);
		}
	}

	private static void renderContainerForeground(AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (ClientPacketWorkQueue.INSTANCE.hasWorkRemaining()) {
			Font font = Minecraft.getInstance().font;
			Component component = Component.translatable("text.sort_it_out.sort_in_progress");
			graphics.pose().pushPose();
			graphics.pose().translate(0, -((AbstractContainerScreenAccessor) screen).getTopPos(), 0);
			graphics.drawCenteredString(font, component, ((AbstractContainerScreenAccessor) screen).getImageWidth() / 2, 6, 0xFFFFFF);
			graphics.pose().popPose();
		}
	}

	private static EventResult keyReleased(Minecraft minecraft, Screen screen, int keyCode, int scanCode, int modifiers) {
		if (SORT_KEY_BINDING.matches(keyCode, scanCode)) {
			KeyMapping.click(InputConstants.Type.KEYSYM.getOrCreate(keyCode));
			return EventResult.interruptTrue();
		}

		return EventResult.pass();
	}
}
