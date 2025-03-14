package io.github.jamalam360.sort_it_out.client;

import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.SortableContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class ClientSortableContainer implements SortableContainer {
	private final int size;
	private final Container container;
	private final AbstractContainerMenu menu;
	private final HashMap<Integer, Integer> containerToMenuSlots = new HashMap<>();

	public ClientSortableContainer(Container container) {
		this.size = container.getContainerSize();
		this.container = container;
		this.menu = Minecraft.getInstance().player.containerMenu;

		for (Slot slot : this.menu.slots) {
			if (slot.container == this.container) {
				containerToMenuSlots.put(slot.getContainerSlot(), slot.index);
			}
		}
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public ItemStack getItem(int i) {
		return this.container.getItem(i);
	}

	@Override
	public void mergeStacks(int destination, int source) {
		ItemStack sourceItem = this.getItem(source);
		int sourceCount = sourceItem.getCount();
		ItemStack destinationItem = this.getItem(destination);
		int destinationCount = destinationItem.getCount();

		if ((!sourceItem.isEmpty() || !destinationItem.isEmpty()) && destination != source) {
			if (destinationCount + sourceCount <= destinationItem.getMaxStackSize()) {
				this.pickup(source);
				this.place(destination, destinationItem.copyWithCount(destinationCount + sourceCount));
			} else {
				int requiredToFillDestination = destinationItem.getMaxStackSize() - destinationCount;
				ItemStack remainder = sourceItem.copyWithCount(sourceCount - requiredToFillDestination);
				this.pickup(source);
				this.placeExpectingRemainder(destination, destinationItem.copyWithCount(destinationItem.getMaxStackSize()), remainder);
				this.place(source, remainder);
			}
		}
	}

	@Override
	public void swapStacks(int a, int b) {
		ItemStack aItem = this.getItem(a).copy();
		ItemStack bItem = this.getItem(b).copy();

		if ((!aItem.isEmpty() || !bItem.isEmpty()) && a != b) {
			if (aItem.isEmpty()) {
				this.moveStack(b, a);
			} else if (bItem.isEmpty()) {
				this.moveStack(a, b);
			} else if (!ContainerSorterUtil.canMerge(aItem, bItem)) {
				this.pickup(a);
				this.placeAndPickupItemInSlot(b, aItem);
				this.place(a, bItem);
			} else {
				int tempSlot = this.findWorkingSlot(aItem, a, b);
				this.moveStack(a, tempSlot);
				this.moveStack(b, a);
				this.moveStack(tempSlot, b);
			}
		}
	}

	private void moveStack(int src, int dst) {
		ItemStack item = this.getItem(src).copy();
		this.pickup(src);
		this.place(dst, item);
	}

	private void pickup(int slot) {
		ClientPacketWorkQueue.INSTANCE.submit(new ClientPacketWorkQueue.PickupItemAction(this.menu, containerToMenuSlots.getOrDefault(slot, slot), this.getItem(slot).copy()));
		this.container.setItem(slot, ItemStack.EMPTY);
	}

	private void place(int slot, ItemStack item) {
		ClientPacketWorkQueue.INSTANCE.submit(new ClientPacketWorkQueue.PlaceItem(this.menu, containerToMenuSlots.getOrDefault(slot, slot), item, ItemStack.EMPTY));
		this.container.setItem(slot, item);
	}

	private void placeAndPickupItemInSlot(int slot, ItemStack carried) {
		ClientPacketWorkQueue.INSTANCE.submit(new ClientPacketWorkQueue.PlaceItem(this.menu, containerToMenuSlots.getOrDefault(slot, slot), carried, this.getItem(slot).copy()));
		this.container.setItem(slot, carried);
	}

	private void placeExpectingRemainder(int slot, ItemStack newSlotContents, ItemStack expectedRemainder) {
		ClientPacketWorkQueue.INSTANCE.submit(new ClientPacketWorkQueue.PlaceItem(this.menu, containerToMenuSlots.getOrDefault(slot, slot), newSlotContents, expectedRemainder));
		this.container.setItem(slot, newSlotContents);
	}

	private int findWorkingSlot(ItemStack workingItem, int... blacklist) {
		for (int i = 0; i < this.size; i++) {
			int finalI = i;
			if (Arrays.stream(blacklist).anyMatch((b) -> finalI == b)) {
				continue;
			}

			if (this.getItem(i).isEmpty() || this.getItem(i).getItem() != workingItem.getItem()) {
				return i;
			}
		}

		return -1;
	}
}
