package io.github.jamalam360.sort_it_out.client.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
	@Invoker
	Slot invokeFindSlot(double mouseX, double mouseY);
	@Accessor
	int getImageWidth();
	@Accessor
	int getTopPos();
}
