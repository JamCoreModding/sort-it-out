package io.github.jamalam360.sort_it_out.client.gui;

import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.client.ClientPacketWorkQueue;
import io.github.jamalam360.sort_it_out.client.SortItOutClient;
import io.github.jamalam360.sort_it_out.client.mixinsupport.MutableSpriteImageButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class SortButton extends ImageButton {
	private static final int WIDTH = 11;
	private static final int HEIGHT = 11;
	private static final int SPRITE_WIDTH = 11;
	private static final int SPRITE_HEIGHT = 11;
	private static final Component ANNOTATION = Component.translatable("text.sort_it_out.sort");
	private static final ResourceLocation SPRITE = SortItOut.id("textures/gui/sprites/sort_button.png");
	private static final ResourceLocation HOVERED_SPRITE = SortItOut.id("textures/gui/sprites/sort_button_hovered.png");
	private final AbstractContainerMenu menu;
	private final Slot slot;

	public SortButton(int x, int y, AbstractContainerMenu menu, Slot slot) {
		super(x, y, WIDTH, HEIGHT, 0, 0, 0, SPRITE, SPRITE_WIDTH, SPRITE_HEIGHT, SortButton::onSortPress, ANNOTATION);
		this.menu = menu;
		this.slot = slot;
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		((MutableSpriteImageButton) this).setSprite(this.isHovered() ? HOVERED_SPRITE : SPRITE);
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
	}

	private static void onSortPress(Button button) {
		if (button instanceof SortButton sortButton) {
			if (ClientPacketWorkQueue.INSTANCE.hasWorkRemaining()) {
				return;
			}

			SortItOutClient.sortOnEitherSide(sortButton.menu, sortButton.slot);
		} else {
			throw new IllegalStateException("SortButton::onPress called with non SortButton argument");
		}
	}
}
