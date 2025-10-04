package io.github.jamalam360.sort_it_out.mixin;

import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import io.github.jamalam360.sort_it_out.sort.ServerSortableContainer;
import io.github.jamalam360.sort_it_out.util.AbstractContainerMenuMixinImpl;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

	@Shadow public abstract ItemStack getCarried();

	@Unique
	private final AbstractContainerMenuMixinImpl sort_it_out$impl = new AbstractContainerMenuMixinImpl();

	@Inject(
			method = "doClick",
			at = @At("HEAD"),
			cancellable = true
	)
	private void sort_it_out$triggerSortOnMiddleClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
		if (slotId < 0 || slotId >= this.slots.size() || player.level().isClientSide()) {
			return;
		}

		UserPreferences preferences = ServerUserPreferences.INSTANCE.getPlayerPreferences(player);
		if (this.sort_it_out$impl.shouldSort(this.getSlot(slotId), button, clickType, this.getCarried(), player)) {
			Container container = this.slots.get(slotId).container;
			ContainerSorterUtil.sortWithQuickSort(container, new ServerSortableContainer(container), preferences);
			SortItOut.playSortSound(player);

			if (preferences.slotSortingTrigger == UserPreferences.SlotSortingTrigger.PRESS_OFFHAND_KEY) {
				((ServerPlayer) player).connection.send(new ClientboundSetPlayerInventoryPacket(Inventory.SLOT_OFFHAND, player.getOffhandItem()));
			}

			ci.cancel();
		}
	}
}
