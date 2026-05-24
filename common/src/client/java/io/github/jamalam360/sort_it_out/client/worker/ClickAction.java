package io.github.jamalam360.sort_it_out.client.worker;

import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public record ClickAction(
		int containerId,
		int slotId,
		ItemStack newSlotItem,
		ItemStack newCarriedItem
) {
	public boolean execute(Minecraft minecraft) {
		minecraft.gameMode.handleInventoryMouseClick(
				this.containerId(),
				this.slotId(),
				getPlaceButton(newSlotItem, newCarriedItem),
				ClickType.PICKUP,
				minecraft.player
		);

		ItemStack carried = minecraft.player.containerMenu.getCarried();
		ItemStack slot = minecraft.player.containerMenu.getSlot(this.slotId()).getItem();
		if (!ItemStack.isSameItemSameComponents(this.newCarriedItem(), carried)) {
			SortItOut.LOGGER.info("Expected to be carrying {}, but found {}", this.newCarriedItem(), carried);
			return false;
		} else if (!ItemStack.isSameItemSameComponents(this.newSlotItem(), slot)) {
			SortItOut.LOGGER.info("Expected slot to contain {}, but found {}", this.newSlotItem(), slot);
			return false;
		} else {
			return true;
		}
	}

	private static int getPlaceButton(ItemStack stack1, ItemStack stack2) {
		if ((stack1.getItem() instanceof BundleItem && !stack2.isEmpty()) || (stack2.getItem() instanceof BundleItem && !stack1.isEmpty())) {
			return GLFW.GLFW_MOUSE_BUTTON_RIGHT;
		} else {
			return GLFW.GLFW_MOUSE_BUTTON_LEFT;
		}
	}
}
