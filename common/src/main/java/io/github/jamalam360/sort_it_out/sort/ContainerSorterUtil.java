package io.github.jamalam360.sort_it_out.sort;

import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.function.Function;

public class ContainerSorterUtil {
	public static void sortWithQuickSort(Container container, SortableContainer sortableContainer, UserPreferences preferences) {
		if (container instanceof Inventory) {
			QuickSortContainerSorter.INSTANCE.sort(sortableContainer, 9, 27, preferences);
		} else {
			QuickSortContainerSorter.INSTANCE.sort(sortableContainer, preferences);
		}
	}

	public static boolean canMerge(ItemStack a, ItemStack b) {
		if (!a.isStackable() || !b.isStackable()) {
			return false;
		}

		if (a.getCount() == a.getMaxStackSize() || b.getCount() == b.getMaxStackSize()) {
			return false;
		}

		return ItemStack.isSameItemSameComponents(a, b);
	}
}
