package io.github.jamalam360.sort_it_out.client.mixin;

import io.github.jamalam360.sort_it_out.client.mixinsupport.MutableSpriteImageButton;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ImageButton.class)
public class ImageButtonMixin implements MutableSpriteImageButton {
	@Mutable
	@Shadow
	@Final
	protected ResourceLocation resourceLocation;

	@Override
	public void setSprite(ResourceLocation location) {
		this.resourceLocation = location;
	}
}
