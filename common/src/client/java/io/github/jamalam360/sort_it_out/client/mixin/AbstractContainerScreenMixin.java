package io.github.jamalam360.sort_it_out.client.mixin;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.sort_it_out.client.ClientPacketWorkQueue;
import io.github.jamalam360.sort_it_out.client.SortItOutClient;
import io.github.jamalam360.sort_it_out.client.gui.SortButton;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
	@Shadow
	protected int leftPos;

	@Shadow
	protected int imageWidth;

	@Shadow
	protected int topPos;

	@Shadow @Final protected AbstractContainerMenu menu;

	@Shadow protected int inventoryLabelY;

	@Shadow protected int imageHeight;

	protected AbstractContainerScreenMixin(Component title) {
		super(title);
	}

	@Inject(
			method = "init",
			at = @At("TAIL")
	)
	private void sort_it_out$addSortButtons(CallbackInfo ci) {
		Slot mainContainer = null;
		Slot invContainer = null;

		for (Slot slot : this.menu.slots) {
			if (slot.container instanceof Inventory) {
				invContainer = slot;
			} else if (mainContainer == null) {
				mainContainer = slot;
			}

			if (mainContainer != null && invContainer != null) {
				break;
			}
		}

		int x = (this.leftPos + this.imageWidth) - 19;

		if (mainContainer != null) {
			this.addRenderableWidget(new SortButton(x, this.topPos + 5, this.menu, mainContainer));
		}

		if (invContainer != null) {
			this.addRenderableWidget(new SortButton(x, this.topPos + 72, this.menu, invContainer));
		}
	}

	@Inject(
			method = "slotClicked",
			at = @At("HEAD"),
			cancellable = true
	)
	private void sort_it_out$triggerSortOnMiddleClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
		if (slot != null && !slot.hasItem() && mouseButton == 2 && !NetworkManager.canServerReceive(BidirectionalUserPreferencesUpdatePacket.C2S.TYPE)) {
			System.out.println("client side");
			if (ClientPacketWorkQueue.INSTANCE.hasWorkRemaining()) {
				return;
			}

			SortItOutClient.sortOnEitherSide(this.menu, slot);
			ci.cancel();
		}
	}
}
