package io.github.jamalam360.sort_it_out.sort;

import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class SelectionSortContainerSorter implements ContainerSorter {
	public static final SelectionSortContainerSorter INSTANCE = new SelectionSortContainerSorter();

	private SelectionSortContainerSorter() {
	}

	@Override
	public void sort(SortableContainer container, int startIndex, int containerSize, UserPreferences preferences) {
		this.mergeStacks(container, startIndex, containerSize);
		Comparator<ItemStack> comparator = preferences.createComparator();

		for (int j = startIndex; j < startIndex + containerSize - 1; j++) {
			int min = j;
			for (int i = j + 1; i < startIndex + containerSize; i++) {
				if (comparator.compare(container.getItem(i), container.getItem(min)) < 0 || container.getItem(min).isEmpty()) {
					min = i;
				}
			}

			container.swapStacks(j, min);
		}
	}
}
