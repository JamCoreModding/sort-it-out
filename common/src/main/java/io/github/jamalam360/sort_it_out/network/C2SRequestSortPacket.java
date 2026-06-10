package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.jamlib.api.network.PacketKind;
import io.github.jamalam360.jamlib.api.network.PacketPayload;
import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record C2SRequestSortPacket(int containerId, int slotIndex) implements PacketPayload<C2SRequestSortPacket> {
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SRequestSortPacket> STREAM_CODEC = StreamCodec.of(
			(buf, packet) -> {
				buf.writeContainerId(packet.containerId());
				buf.writeInt(packet.slotIndex());
			},
			(buf) -> new C2SRequestSortPacket(buf.readContainerId(), buf.readInt())
	);
	public static final PacketKind<C2SRequestSortPacket> KIND = PacketKind.of(SortItOut.id("request_sort"), STREAM_CODEC);

	@Override
	public PacketKind<C2SRequestSortPacket> getKind() {
		return KIND;
	}

	@Override
	public C2SRequestSortPacket getPayload() {
		return this;
	}
}
