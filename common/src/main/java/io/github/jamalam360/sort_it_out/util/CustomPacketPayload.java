package io.github.jamalam360.sort_it_out.util;

import net.minecraft.resources.ResourceLocation;

public interface CustomPacketPayload {
	Type<? extends CustomPacketPayload> type();

	record Type<T>(ResourceLocation location) {}
}
