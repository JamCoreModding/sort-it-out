package io.github.jamalam360.sort_it_out.sort;

import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.world.entity.player.Player;

public interface ContainerSorter {
	default void sort(SortableContainer container, UserPreferences preferences) {
		this.sort(container, 0, container.getSize(), preferences);
	}

	void sort(SortableContainer container, int startIndex, int containerSize, UserPreferences preferences);
}
