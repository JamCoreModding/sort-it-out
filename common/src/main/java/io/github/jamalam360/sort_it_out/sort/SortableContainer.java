package io.github.jamalam360.sort_it_out.sort;

import net.minecraft.world.item.ItemStack;

public interface SortableContainer {
	int getSize();
	ItemStack getItem(int i);
	void mergeStacks(int destination, int source);
	void swapStacks(int a, int b);
}
