package io.github.jamalam360.sort_it_out.client.button;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ScreenClass(String mojmap, String intermediary) {
	public static final Codec<ScreenClass> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.STRING.fieldOf("mojmap").forGetter(ScreenClass::mojmap),
					Codec.STRING.fieldOf("intermediary").forGetter(ScreenClass::intermediary)
			).apply(instance, ScreenClass::new)
	);
}
