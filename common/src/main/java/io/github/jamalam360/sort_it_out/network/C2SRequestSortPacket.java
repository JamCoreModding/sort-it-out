package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.jamlib.api.network.PayloadType;
import io.github.jamalam360.jamlib.api.network.StreamCodecNetworkPayloadType;
import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record C2SRequestSortPacket(int containerId, int slotIndex) {
	public static final PayloadType<C2SRequestSortPacket> TYPE = new PayloadType<>(SortItOut.id("request_sort"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SRequestSortPacket> STREAM_CODEC = StreamCodec.of(
			(buf, packet) -> {
				buf.writeContainerId(packet.containerId());
				buf.writeInt(packet.slotIndex());
			},
			(buf) -> new C2SRequestSortPacket(buf.readContainerId(), buf.readInt())
	);

	public static class Type implements StreamCodecNetworkPayloadType<C2SRequestSortPacket> {
		public static final Type INSTANCE = new Type();

		private Type() {
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, C2SRequestSortPacket> getStreamCodec() {
			return STREAM_CODEC;
		}
	}
}
