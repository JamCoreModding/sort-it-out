package io.github.jamalam360.sort_it_out.client.mixin;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.sort_it_out.client.ClientPacketWorkQueue;
import io.github.jamalam360.sort_it_out.client.SortItOutClient;
import io.github.jamalam360.sort_it_out.client.button.ScreenSortButton;
import io.github.jamalam360.sort_it_out.client.button.ScreenSortButtonsLoader;
import io.github.jamalam360.sort_it_out.client.gui.SortButton;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
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

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
	@Shadow
	protected int leftPos;

	@Shadow
	protected int imageWidth;

	@Shadow
	protected int topPos;

	@Shadow @Final protected AbstractContainerMenu menu;

	@Shadow protected int imageHeight;

	protected AbstractContainerScreenMixin(Component title) {
		super(title);
	}

	@SuppressWarnings("ConstantValue")
	@Inject(
			method = "init",
			at = @At("TAIL")
	)
	private void sort_it_out$addSortButtons(CallbackInfo ci) {
		if ((Screen) this instanceof CreativeModeInventoryScreen) {
			return;
		}

		List<ScreenSortButton> customButtons = ScreenSortButtonsLoader.INSTANCE.getCustomButtonsForScreen((AbstractContainerScreen<?>) (Object) this);
		if (customButtons != null) {
			for (ScreenSortButton button : customButtons) {
				this.addRenderableWidget(new SortButton(this.leftPos + button.xOffset(), this.topPos + button.yOffset(), this.menu, this.menu.slots.get(button.slotStartIndex())));
			}
		} else {
			// If custom sort buttons defs. are not present, at least guess where the player inventory sort button should be
			Slot invSlot = null;

			for (Slot slot : this.menu.slots) {
				if (slot.container instanceof Inventory) {
					invSlot = slot;
					break;
				}
			}

			if (invSlot != null) {
				this.addRenderableWidget(new SortButton(this.leftPos + 158, this.topPos + this.imageHeight - 95, this.menu, invSlot));
			}
		}
	}

	@Inject(
			method = "slotClicked",
			at = @At("HEAD"),
			cancellable = true
	)
	private void sort_it_out$triggerSortOnMiddleClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
		if (slot != null && !slot.hasItem() && mouseButton == 2 && !NetworkManager.canServerReceive(BidirectionalUserPreferencesUpdatePacket.C2S.TYPE)) {
			if (ClientPacketWorkQueue.INSTANCE.hasWorkRemaining()) {
				return;
			}

			SortItOutClient.sortOnEitherSide(this.menu, slot);
			ci.cancel();
		}
	}
}
