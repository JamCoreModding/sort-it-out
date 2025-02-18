package io.github.jamalam360.sort_it_out.client;

import io.github.jamalam360.sort_it_out.SortItOut;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.Queue;

public class ClientPacketWorkQueue {
	public static final ClientPacketWorkQueue INSTANCE = new ClientPacketWorkQueue();
	private final Queue<ClickAction> workQueue;
	private int ticks = 0;

	private ClientPacketWorkQueue() {
		this.workQueue = new LinkedList<>();
	}

	public boolean hasWorkRemaining() {
		return !this.workQueue.isEmpty();
	}

	public void submit(ClickAction action) {
		this.workQueue.offer(action);
	}

	public void tick() {
		ticks++;

		if (ticks >= SortItOutClient.CONFIG.get().packetSendInterval) {
			ticks = 0;
			this.poll();
		}
	}

	private void poll() {
		ClickAction action = this.workQueue.poll();

		if (action != null && Minecraft.getInstance().getConnection() != null) {
			ServerboundContainerClickPacket packet = action.toPacket();
			if (Minecraft.getInstance().screen == null || (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> containerScreen && containerScreen.getMenu().containerId != packet.getContainerId())) {
				SortItOut.LOGGER.info("Aborting sort as the player closed the container window before it was completed");
				this.workQueue.clear();
			} else {
		    	Minecraft.getInstance().getConnection().send(packet);
			}
		}
	}

	public interface ClickAction {
		ServerboundContainerClickPacket toPacket();
	}

	public record PickupItemAction(AbstractContainerMenu menu, int slot, ItemStack pickedUp, ItemStack remainder) implements ClickAction {
		@Override
		public ServerboundContainerClickPacket toPacket() {
			return new ServerboundContainerClickPacket(
					this.menu().containerId,
					this.menu().getStateId(),
					this.slot(),
					GLFW.GLFW_MOUSE_BUTTON_LEFT,
					ClickType.PICKUP,
					this.pickedUp(),
					Int2ObjectMaps.singleton(this.slot(), this.remainder())
			);
		}
	}

	public record PlaceItem(AbstractContainerMenu menu, int slot, ItemStack newSlotItem, ItemStack newCarriedItem) implements ClickAction {
		@Override
		public ServerboundContainerClickPacket toPacket() {
			return new ServerboundContainerClickPacket(
					this.menu().containerId,
					this.menu().getStateId(),
					this.slot(),
					GLFW.GLFW_MOUSE_BUTTON_LEFT,
					ClickType.PICKUP,
					this.newCarriedItem(),
					Int2ObjectMaps.singleton(this.slot(), this.newSlotItem())
			);
		}
	}
}
