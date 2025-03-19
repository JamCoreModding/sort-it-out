package io.github.jamalam360.sort_it_out.util;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.sort_it_out.network.C2SRequestSortPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Fireball;

public class NetworkManager2 {
	public static <T extends CustomPacketPayload> void sendToServer(T payload, StreamCodec<FriendlyByteBuf, T> codec) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		codec.encode(buf, payload);
		NetworkManager.sendToServer(payload.type().location(), buf);
	}

	public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload, StreamCodec<FriendlyByteBuf, T> codec) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		codec.encode(buf, payload);
		NetworkManager.sendToPlayer(player, payload.type().location(), buf);
	}
}
