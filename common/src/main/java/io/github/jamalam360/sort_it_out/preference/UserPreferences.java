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
		Comparator<ItemStack> comparator = this.invertSorting ? Comparators.EMPTINESS.reversed() : Comparators.EMPTINESS;

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

	// When adding a new comparator, ensure that the command chain is long enough in {@link SortItOutCommands}
	public enum SortingComparator {
		DISPLAY_NAME,
		NAMESPACE,
		COUNT,
		DURABILITY;
	}
}
