package io.github.jamalam360.sort_it_out.client.mixin;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.sort_it_out.client.SortItOutClient;
import io.github.jamalam360.sort_it_out.client.button.ScreenSortButton;
import io.github.jamalam360.sort_it_out.client.button.ScreenSortButtonsLoader;
import io.github.jamalam360.sort_it_out.client.gui.SortButton;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.util.AbstractContainerMenuMixinImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
	@Shadow
	protected int leftPos;

	@Shadow
	protected int topPos;

	@Shadow @Final protected AbstractContainerMenu menu;

	@Shadow protected int imageHeight;

	@Unique
	private final AbstractContainerMenuMixinImpl sort_it_out$impl = new AbstractContainerMenuMixinImpl();

	@Unique
	private int sort_it_out$previousLeftPos = this.leftPos;

	@Unique
	private int sort_it_out$previousTopPos = this.topPos;

	protected AbstractContainerScreenMixin(Component title) {
		super(title);
	}

	@Inject(
			method = "init",
			at = @At("TAIL")
	)
	private void sort_it_out$addSortButtons(CallbackInfo ci) {
		this.sort_it_out$initButtons();
	}

	@Inject(
			method = "containerTick",
			at = @At("TAIL")
	)
	private void sort_it_out$checkForLeftOrTopPosChange(CallbackInfo ci) {
		if (this.leftPos != this.sort_it_out$previousLeftPos || this.topPos != this.sort_it_out$previousTopPos) {
			this.sort_it_out$initButtons();
		}
	}

	@Inject(
			method = "slotClicked",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleInventoryMouseClick(IIILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V"
			),
			cancellable = true
	)
	private void sort_it_out$triggerSortOnMiddleClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
		if (slotId < 0 || slotId >= this.menu.slots.size() || NetworkManager.canServerReceive(BidirectionalUserPreferencesUpdatePacket.C2S.TYPE.location())) {
			return;
		}

		if (this.sort_it_out$impl.shouldSort(this.menu.getSlot(slotId), mouseButton, type, this.menu.getCarried(), Minecraft.getInstance().player)) {
			SortItOutClient.sortOnEitherSide(this.menu, this.menu.getSlot(slotId));
			ci.cancel();
		}
	}

	@Unique
	private void sort_it_out$initButtons() {
		if ((Screen) this instanceof CreativeModeInventoryScreen) {
			return;
		}

		List<SortButton> existingSortButtons = new ArrayList<>();
		for (GuiEventListener widget : this.children()) {
			if (widget instanceof SortButton sortButton) {
				existingSortButtons.add(sortButton);
			}
		}

		for (SortButton button : existingSortButtons) {
			this.removeWidget(button);
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

		this.sort_it_out$previousLeftPos = this.leftPos;
		this.sort_it_out$previousTopPos = this.topPos;
	}
}
