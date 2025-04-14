package io.github.jamalam360.sort_it_out.client.button;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ScreenSortButtons(ResourceLocation type,
                                List<ScreenSortButton> sortButtons) {
	public static final Codec<ScreenSortButtons> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					ResourceLocation.CODEC.fieldOf("type").forGetter(ScreenSortButtons::type),
					Codec.list(ScreenSortButton.CODEC).fieldOf("sortButtons").forGetter(ScreenSortButtons::sortButtons)
			).apply(instance, ScreenSortButtons::new)
	);
}
