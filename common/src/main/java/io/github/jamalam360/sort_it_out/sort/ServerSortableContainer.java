package io.github.jamalam360.sort_it_out.sort;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class ServerSortableContainer implements SortableContainer {
	private final Container container;

	public ServerSortableContainer(Container container) {
		this.container = container;
	}

	@Override
	public int getSize() {
		return this.container.getContainerSize();
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
				this.container.setItem(source, ItemStack.EMPTY);
				this.container.setItem(destination, destinationItem.copyWithCount(destinationCount + sourceCount));
			} else {
				this.container.setItem(source, sourceItem.copyWithCount(sourceCount - (destinationItem.getMaxStackSize() - destinationCount)));
				this.container.setItem(destination, destinationItem.copyWithCount(destinationItem.getMaxStackSize()));
			}
		}
	}

	@Override
	public void swapStacks(int a, int b) {
		ItemStack temp = this.container.getItem(a);
		this.container.setItem(a, this.container.getItem(b));
		this.container.setItem(b, temp);
	}
}
