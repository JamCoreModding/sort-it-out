package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class BidirectionalUserPreferencesUpdatePacket {
	private static final StreamCodec<RegistryFriendlyByteBuf, UserPreferences> BASE_STREAM_CODEC = StreamCodec.of(
			(buf, prefs) -> {
				buf.writeBoolean(prefs.invertSorting);
				buf.writeEnum(prefs.sortMode);
			},
			(buf) -> {
				UserPreferences prefs = new UserPreferences();
				prefs.invertSorting = buf.readBoolean();
				prefs.sortMode = buf.readEnum(UserPreferences.SortMode.class);
				return prefs;
			}
	);

	public record S2C(UserPreferences preferences) implements CustomPacketPayload {
		public static final StreamCodec<RegistryFriendlyByteBuf, S2C> STREAM_CODEC = BASE_STREAM_CODEC.map(S2C::new, S2C::preferences);
		public static final Type<S2C> TYPE = new Type<>(SortItOut.id("s2c_user_preferences"));

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public record C2S(UserPreferences preferences) implements CustomPacketPayload {
		public static final StreamCodec<RegistryFriendlyByteBuf, C2S> STREAM_CODEC = BASE_STREAM_CODEC.map(C2S::new, C2S::preferences);
		public static final CustomPacketPayload.Type<C2S> TYPE = new CustomPacketPayload.Type<>(SortItOut.id("c2s_user_preferences"));

		@Override
		public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}
