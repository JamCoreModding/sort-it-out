package io.github.jamalam360.sort_it_out.util;

import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AbstractContainerMenuMixinImpl {
	public boolean shouldSort(Slot slot, int button, ClickType clickType, ItemStack carried, Player player) {
		UserPreferences preferences = ServerUserPreferences.INSTANCE.getPlayerPreferences(player);

		return switch (preferences.slotSortingTrigger) {
			case PRESS_OFFHAND_KEY -> button == Inventory.SLOT_OFFHAND;
			case PRESS_OFFHAND_KEY_EMPTY_SLOT -> button == Inventory.SLOT_OFFHAND && !slot.hasItem() && player.getOffhandItem().isEmpty();
			case DOUBLE_CLICK_EMPTY_SLOT -> button == 0 && clickType == ClickType.PICKUP_ALL && !slot.hasItem() && carried.isEmpty();
		};
	}
}
