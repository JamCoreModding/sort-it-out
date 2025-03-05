package io.github.jamalam360.sort_it_out.mixin;

import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.ServerSortableContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
	@Shadow
	public abstract Slot getSlot(int slotId);

	@Shadow
	@Final
	public NonNullList<Slot> slots;

	@Inject(
			method = "doClick",
			at = @At("HEAD"),
			cancellable = true
	)
	private void sort_it_out$triggerSortOnMiddleClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
		if (slotId >= 0 && slotId < this.slots.size() && !this.getSlot(slotId).hasItem() && button == 2 && !player.level().isClientSide) {
			Container container = this.slots.get(slotId).container;
			ContainerSorterUtil.sortWithQuickSort(container, new ServerSortableContainer(container), ServerUserPreferences.INSTANCE.getPlayerPreferences(player));
			SortItOut.playSortSound(player);
			ci.cancel();
		}
	}
}
