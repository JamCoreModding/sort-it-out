package io.github.jamalam360.sort_it_out.client.button;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ScreenSortButton(int xOffset, int yOffset, int slotStartIndex) {
	public static final Codec<ScreenSortButton> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.INT.fieldOf("xOffset").forGetter(ScreenSortButton::xOffset),
					Codec.INT.fieldOf("yOffset").forGetter(ScreenSortButton::yOffset),
					Codec.INT.fieldOf("slotStartIndex").forGetter(ScreenSortButton::slotStartIndex)
			).apply(instance, ScreenSortButton::new)
	);
}
