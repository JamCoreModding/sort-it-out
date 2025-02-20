package io.github.jamalam360.sort_it_out.preference;

import io.github.jamalam360.sort_it_out.util.Comparators;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class UserPreferences {
	public boolean invertSorting = false;
	public SortMode sortMode = SortMode.ALPHABETIC;

	public Comparator<ItemStack> createComparator() {
		Comparator<ItemStack> primaryComparator = switch (this.sortMode) {
			case ALPHABETIC -> Comparators.DISPLAY_NAME;
			case NAMESPACE -> Comparators.NAMESPACE;
		};

		Comparator<ItemStack> comparator = Comparators.EMPTINESS
				.thenComparing(primaryComparator)
				.thenComparing(Comparators.COUNT);
		return this.invertSorting ? comparator.reversed() : comparator;
	}

	public enum SortMode {
		ALPHABETIC,
		NAMESPACE;
	}
}
