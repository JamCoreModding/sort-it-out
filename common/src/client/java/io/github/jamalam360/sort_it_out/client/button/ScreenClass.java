package io.github.jamalam360.sort_it_out.client.button;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ScreenClass(String srg, String intermediary) {
	public static final Codec<ScreenClass> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.STRING.fieldOf("srg").forGetter(ScreenClass::srg),
					Codec.STRING.fieldOf("intermediary").forGetter(ScreenClass::intermediary)
			).apply(instance, ScreenClass::new)
	);
}
