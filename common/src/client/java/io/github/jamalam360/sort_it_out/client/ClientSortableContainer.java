package io.github.jamalam360.sort_it_out.client;

import io.github.jamalam360.sort_it_out.client.worker.ClickAction;
import io.github.jamalam360.sort_it_out.client.worker.ClientSortWorker;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.SortableContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class ClientSortableContainer implements SortableContainer {
	private final int size;
	private final NonNullList<ItemStack> workingContainer;
	private final AbstractContainerMenu menu;
	private final HashMap<Integer, Integer> containerToMenuSlots = new HashMap<>();

	public ClientSortableContainer(Container container) {
		this.size = container.getContainerSize();
		this.workingContainer = NonNullList.withSize(this.size, ItemStack.EMPTY);
		this.menu = Minecraft.getInstance().player.containerMenu;

		for (Slot slot : this.menu.slots) {
			if (slot.container == container) {
				containerToMenuSlots.put(slot.getContainerSlot(), slot.index);
			}
		}

		for (int i = 0; i < this.size; i++) {
			this.workingContainer.set(i, container.getItem(i).copy());
		}

		ClientSortWorker.INSTANCE.start(Minecraft.getInstance(), SortItOutClient.CONFIG.get().packetSendInterval);
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public ItemStack getItem(int i) {
		return this.workingContainer.get(i);
	}

	@Override
	public void mergeStacks(int destination, int source) {
		ItemStack sourceItem = this.getItem(source).copy();
		int sourceCount = sourceItem.getCount();
		ItemStack destinationItem = this.getItem(destination).copy();
		int destinationCount = destinationItem.getCount();

		if ((!sourceItem.isEmpty() || !destinationItem.isEmpty()) && destination != source) {
			if (destinationCount + sourceCount <= destinationItem.getMaxStackSize()) {
				this.pickup(source, sourceItem);
				this.place(destination, destinationItem.copyWithCount(destinationCount + sourceCount));
			} else {
				int requiredToFillDestination = destinationItem.getMaxStackSize() - destinationCount;
				ItemStack remainder = sourceItem.copyWithCount(sourceCount - requiredToFillDestination);
				this.pickup(source, sourceItem);
				this.placeAndPickupItemInSlot(destination, destinationItem.copyWithCount(destinationItem.getMaxStackSize()), remainder);
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
				this.pickup(a, aItem);
				this.placeAndPickupItemInSlot(b, aItem, bItem);
				this.place(a, bItem);
			} else {
				int tempSlot = this.findWorkingSlot(aItem, a, b);
				this.moveStack(a, tempSlot);
				this.moveStack(b, a);
				this.moveStack(tempSlot, b);
			}
		}
	}

	@Override
	public void afterSort() {
		ClientSortWorker.INSTANCE.complete();
	}

	private void moveStack(int src, int dst) {
		ItemStack item = this.getItem(src).copy();
		this.pickup(src, item);
		this.place(dst, item);
	}

	private void pickup(int slot, ItemStack item) {
		ClientSortWorker.INSTANCE.push(new ClickAction(
				this.menu.containerId,
				containerToMenuSlots.getOrDefault(slot, slot),
				ItemStack.EMPTY,
				item
		));
		this.workingContainer.set(slot, ItemStack.EMPTY);
	}

	private void place(int slot, ItemStack item) {
		ClientSortWorker.INSTANCE.push(new ClickAction(
				this.menu.containerId,
				containerToMenuSlots.getOrDefault(slot, slot),
				item,
				ItemStack.EMPTY
		));
		this.workingContainer.set(slot, item.copy());
	}

	private void placeAndPickupItemInSlot(int slot, ItemStack carried, ItemStack slotItem) {
		ClientSortWorker.INSTANCE.push(new ClickAction(
				this.menu.containerId,
				containerToMenuSlots.getOrDefault(slot, slot),
				carried,
				slotItem
		));
		this.workingContainer.set(slot, carried.copy());
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
