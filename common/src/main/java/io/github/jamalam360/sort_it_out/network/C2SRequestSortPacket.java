package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SRequestSortPacket(int containerId, int slotIndex) implements CustomPacketPayload {
	public static final Type<C2SRequestSortPacket> TYPE = new Type<>(SortItOut.id("request_sort"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SRequestSortPacket> STREAM_CODEC = StreamCodec.of(
			(buf, packet) -> {
				buf.writeContainerId(packet.containerId());
				buf.writeInt(packet.slotIndex());
			},
			(buf) -> new C2SRequestSortPacket(buf.readContainerId(), buf.readInt())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
