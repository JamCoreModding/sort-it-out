package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SUserPreferencesUpdatePacket(UserPreferences preferences) implements CustomPacketPayload {
	public static final Type<C2SUserPreferencesUpdatePacket> TYPE = new Type<>(SortItOut.id("user_preferences"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SUserPreferencesUpdatePacket> STREAM_CODEC = StreamCodec.of(
			(buf, prefs) -> {
				buf.writeBoolean(prefs.preferences().invertSorting);
				buf.writeEnum(prefs.preferences().sortMode);
			},
			(buf) -> {
				UserPreferences prefs = new UserPreferences();
				prefs.invertSorting = buf.readBoolean();
				prefs.sortMode = buf.readEnum(UserPreferences.SortMode.class);
				return new C2SUserPreferencesUpdatePacket(prefs);
			}
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
