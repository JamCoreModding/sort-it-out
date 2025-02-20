package io.github.jamalam360.sort_it_out.client.mixin;

import io.github.jamalam360.sort_it_out.client.mixinsupport.MutableSpriteSpriteIconButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpriteIconButton.class)
public class SpriteIconButtonMixin implements MutableSpriteSpriteIconButton {
	@Mutable
	@Shadow
	@Final
	protected ResourceLocation sprite;

	@Override
	public void setSprite(ResourceLocation location) {
		this.sprite = location;
	}
}
