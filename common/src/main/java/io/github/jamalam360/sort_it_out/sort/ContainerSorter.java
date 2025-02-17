package io.github.jamalam360.sort_it_out.sort;

import io.github.jamalam360.sort_it_out.UserPreferences;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class ContainerSorter {
	public static void sortContainer(SortableContainer container, UserPreferences preferences) {
		sortContainer(container, 0, container.getSize(), preferences);
	}

	public static void sortContainer(SortableContainer container, int startIndex, int inventorySize, UserPreferences preferences) {
		System.out.println(startIndex);
		System.out.println(inventorySize);
		for (int i = startIndex; i < startIndex + inventorySize; i++) {
			for (int j = startIndex; j < startIndex + inventorySize; j++) {
				if (i != j && canMerge(container.getItem(i), container.getItem(j))) {
					container.mergeStacks(i, j);
				}
			}
		}

		quickSort(container, startIndex, inventorySize - 1, preferences.createComparator());
	}

	private static boolean canMerge(ItemStack a, ItemStack b) {
		if (!a.isStackable() || !b.isStackable()) {
			return false;
		}

		if (a.getCount() == a.getMaxStackSize() || b.getCount() == b.getMaxStackSize()) {
			return false;
		}

		return ItemStack.isSameItemSameComponents(a, b);
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
