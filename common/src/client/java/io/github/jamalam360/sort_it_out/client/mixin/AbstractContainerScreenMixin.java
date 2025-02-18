package io.github.jamalam360.sort_it_out.client.mixin;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.sort_it_out.client.ClientPacketWorkQueue;
import io.github.jamalam360.sort_it_out.client.ClientSortableContainer;
import io.github.jamalam360.sort_it_out.client.SortItOutClient;
import io.github.jamalam360.sort_it_out.network.C2SUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.sort.ContainerSorterUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
	@Inject(
			method = "slotClicked",
			at = @At("HEAD"),
			cancellable = true
	)
	private void sort_it_out$triggerSortOnMiddleClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
		if (slot != null && !slot.hasItem() && mouseButton == 2 && !NetworkManager.canServerReceive(C2SUserPreferencesUpdatePacket.TYPE)) {
			if (ClientPacketWorkQueue.INSTANCE.hasWorkRemaining()) {
				return;
			}

			ContainerSorterUtil.sortWithQuickSort(slot.container, new ClientSortableContainer(slot.container), SortItOutClient.CONFIG.get());
			ci.cancel();
		}
	}
}
