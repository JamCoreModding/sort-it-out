package io.github.jamalam360.sort_it_out.sort;

import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class QuickSortContainerSorter implements ContainerSorter {
	public static final QuickSortContainerSorter INSTANCE = new QuickSortContainerSorter();

	private QuickSortContainerSorter() {
	}

	@Override
	public void sort(SortableContainer container, int startIndex, int containerSize, UserPreferences preferences) {
		for (int i = startIndex; i < startIndex + containerSize; i++) {
			for (int j = startIndex; j < startIndex + containerSize; j++) {
				if (i != j && ContainerSorterUtil.canMerge(container.getItem(i), container.getItem(j))) {
					container.mergeStacks(i, j);
				}
			}
		}

		quickSort(container, startIndex, startIndex + containerSize - 1, preferences.createComparator());
	}

	private static void quickSort(SortableContainer container, int low, int high, Comparator<ItemStack> comparator) {
		if (low < high) {
			int partition = partition(container, low, high, comparator);
			quickSort(container, low, partition - 1, comparator);
			quickSort(container, partition + 1, high, comparator);
		}
	}

	private static int partition(SortableContainer container, int low, int high, Comparator<ItemStack> comparator) {
		ItemStack pivot = container.getItem(high);
		int i = low - 1;

		for (int j = low; j <= high - 1; j++) {
			if (comparator.compare(container.getItem(j), pivot) < 0) {
				i++;
				container.swapStacks(i, j);
			}
		}

		container.swapStacks(i + 1, high);
		return i + 1;
	}
}
