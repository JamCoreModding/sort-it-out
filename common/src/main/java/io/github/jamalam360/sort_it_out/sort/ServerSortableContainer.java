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
		int newDestinationCount = Math.min(this.container.getItem(destination).getCount() + this.container.getItem(source).getCount(), this.container.getItem(source).getMaxStackSize());
		int newSourceCount = this.container.getItem(source).getCount() - newDestinationCount - this.container.getItem(destination).getCount();
		this.container.getItem(destination).setCount(newDestinationCount);
		this.container.getItem(source).setCount(newSourceCount);
	}

	@Override
	public void swapStacks(int a, int b) {
		ItemStack temp = this.container.getItem(a);
		this.container.setItem(a, this.container.getItem(b));
		this.container.setItem(b, temp);
	}
}
