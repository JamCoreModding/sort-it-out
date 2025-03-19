package io.github.jamalam360.sort_it_out.util;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class StreamCodec<F, T> {
	private final BiConsumer<F, T> encoder;
	private final Function<F, T> decoder;

	private StreamCodec(BiConsumer<F, T> encoder, Function<F, T> decoder) {
		this.encoder = encoder;
		this.decoder = decoder;
	}

	public static <F, T> StreamCodec<F, T> of(BiConsumer<F, T> encoder, Function<F, T> decoder) {
		return new StreamCodec<>(encoder, decoder);
	}

	public void encode(F f, T t) {
		this.encoder.accept(f, t);
	}

	public T decode(F f) {
		return this.decoder.apply(f);
	}

	public <R> StreamCodec<F, R> map(Function<T, R> mapper, Function<R, T> unmapper) {
		return new StreamCodec<>((a, b) -> this.encoder.accept(a, unmapper.apply(b)), (a) -> mapper.apply(this.decoder.apply(a)));
	}
}
