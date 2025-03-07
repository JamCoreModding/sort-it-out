package io.github.jamalam360.sort_it_out.sort;

import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.world.entity.player.Player;

public interface ContainerSorter {
	default void sort(SortableContainer container, UserPreferences preferences) {
		this.sort(container, 0, container.getSize(), preferences);
	}

	void sort(SortableContainer container, int startIndex, int containerSize, UserPreferences preferences);

	default void mergeStacks(SortableContainer container, int startIndex, int containerSize) {
		for (int i = startIndex; i < startIndex + containerSize; i++) {
			for (int j = startIndex; j < startIndex + containerSize; j++) {
				if (i != j && ContainerSorterUtil.canMerge(container.getItem(i), container.getItem(j))) {
					container.mergeStacks(i, j);
				}
			}
		}
	}
}
