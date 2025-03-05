package io.github.jamalam360.sort_it_out.preference;

import io.github.jamalam360.sort_it_out.util.Comparators;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserPreferences {
	public boolean invertSorting = false;
	public List<SortingComparator> comparators = new ArrayList<>(List.of(
			SortingComparator.DISPLAY_NAME,
			SortingComparator.COUNT,
			SortingComparator.DURABILITY
	));

	public Comparator<ItemStack> createComparator() {
		Comparator<ItemStack> comparator = Comparators.EMPTINESS;

		for (SortingComparator sortingComparator : this.comparators) {
			Comparator<ItemStack> chain = switch (sortingComparator) {
				case DISPLAY_NAME -> Comparators.DISPLAY_NAME;
				case NAMESPACE -> Comparators.NAMESPACE;
				case COUNT -> Comparators.COUNT;
				case DURABILITY -> Comparators.DURABILITY;
			};

			comparator = comparator.thenComparing(chain);
		}

		return this.invertSorting ? comparator.reversed() : comparator;
	}

	public enum SortingComparator {
		DISPLAY_NAME,
		NAMESPACE,
		COUNT,
		DURABILITY;
	}
}
