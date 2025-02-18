package io.github.jamalam360.sort_it_out.client;

import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.SortableContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class ClientSortableContainer implements SortableContainer {
	private final int size;
	private final AbstractContainerMenu menu;

	public ClientSortableContainer(Container container) {
		this.size = container.getContainerSize();
		this.menu = Minecraft.getInstance().player.containerMenu;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public ItemStack getItem(int i) {
		return this.menu.getSlot(i).getItem();
	}

	@Override
	public void mergeStacks(int destination, int source) {
		ItemStack sourceItem = this.getItem(source);
		ItemStack destinationItem = this.getItem(destination);
		int newDestinationCount = Math.min(destinationItem.getCount() + sourceItem.getCount(), sourceItem.getMaxStackSize());
		int newSourceCount = sourceItem.getCount() - newDestinationCount - destinationItem.getCount();
		ItemStack pickedUpItem = sourceItem.copyWithCount(sourceItem.getCount() - newSourceCount);
		ItemStack newSourceItem = sourceItem.copyWithCount(newSourceCount);
		ItemStack newDestinationItem = destinationItem.copyWithCount(newDestinationCount);
		this.splitPickup(source, pickedUpItem, newSourceItem);
		this.place(destination, newDestinationItem);
	}

	@Override
	public void swapStacks(int a, int b) {
		ItemStack aItem = this.getItem(a).copy();
		ItemStack bItem = this.getItem(b).copy();

		if (aItem.isEmpty() && bItem.isEmpty() || a == b) {
			return;
		} else if (aItem.isEmpty()) {
			this.moveStack(b, a);
		} else if (bItem.isEmpty()) {
			this.moveStack(a, b);
		} else if (!ContainerSorterUtil.canMerge(aItem, bItem)) {
			this.pickup(a);
			this.placeAndPickup(b, aItem);
			this.place(a, bItem);
		} else {
			int tempSlot = this.findWorkingSlot(aItem, a, b);
			this.moveStack(a, tempSlot);
			this.moveStack(b, a);
			this.moveStack(tempSlot, b);
		}
	}

	private void moveStack(int src, int dst) {
		ItemStack item = this.getItem(src).copy();
		this.pickup(src);
		this.place(dst, item);
	}

	private void pickup(int slot) {
		ClientPacketWorkQueue.INSTANCE.submit(new ClientPacketWorkQueue.PickupItemAction(this.menu, slot, this.getItem(slot).copy(), ItemStack.EMPTY));
		this.menu.getSlot(slot).set(ItemStack.EMPTY);
	}

	private void splitPickup(int slot, ItemStack pickup, ItemStack remainder) {
		ClientPacketWorkQueue.INSTANCE.submit(new ClientPacketWorkQueue.PickupItemAction(this.menu, slot, pickup, remainder));
		this.menu.getSlot(slot).set(remainder);
	}

	private void place(int slot, ItemStack item) {
		ClientPacketWorkQueue.INSTANCE.submit(new ClientPacketWorkQueue.PlaceItem(this.menu, slot, item, ItemStack.EMPTY));
		this.menu.getSlot(slot).set(item);
	}

	private void placeAndPickup(int slot, ItemStack carried) {
		ClientPacketWorkQueue.INSTANCE.submit(new ClientPacketWorkQueue.PlaceItem(this.menu, slot, carried, this.getItem(slot).copy()));
		this.menu.getSlot(slot).set(carried);
	}

	private int findWorkingSlot(ItemStack workingItem, int... blacklist) {
		for (int i = 0; i < this.size; i++) {
			int finalI = i;
			if (Arrays.stream(blacklist).anyMatch((b) -> finalI == b)) {
				continue;
			}

			if (this.menu.getSlot(i).getItem().isEmpty() || this.menu.getSlot(i).getItem().getItem() != workingItem.getItem()) {
				return i;
			}
		}

		return -1;
	}
}
