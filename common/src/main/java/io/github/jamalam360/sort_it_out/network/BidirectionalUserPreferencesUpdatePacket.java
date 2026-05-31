package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.jamlib.api.network.PayloadType;
import io.github.jamalam360.jamlib.api.network.StreamCodecNetworkPayloadType;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;

public class BidirectionalUserPreferencesUpdatePacket {
	// TODO: version this
	private static final StreamCodec<RegistryFriendlyByteBuf, UserPreferences> BASE_STREAM_CODEC = StreamCodec.of(
			(buf, prefs) -> {
				buf.writeBoolean(prefs.invertSorting);
				buf.writeEnum(prefs.slotSortingTrigger);
				buf.writeInt(prefs.comparators.size());
				prefs.comparators.forEach(buf::writeEnum);
			},
			(buf) -> {
				UserPreferences prefs = new UserPreferences();
				prefs.invertSorting = buf.readBoolean();
				prefs.slotSortingTrigger = buf.readEnum(UserPreferences.SlotSortingTrigger.class);
				int size = buf.readInt();
				prefs.comparators = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					prefs.comparators.add(i, buf.readEnum(UserPreferences.SortingComparator.class));
				}

				return prefs;
			}
	);

	public record S2C(UserPreferences preferences) {
		public static final StreamCodec<RegistryFriendlyByteBuf, S2C> STREAM_CODEC = BASE_STREAM_CODEC.map(S2C::new, S2C::preferences);
		public static final PayloadType<S2C> TYPE = new PayloadType<>(SortItOut.id("s2c_user_preferences"));

		public static class Type implements StreamCodecNetworkPayloadType<S2C> {
			public static final Type INSTANCE = new Type();

			private Type() {
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, S2C> getStreamCodec() {
				return STREAM_CODEC;
			}
		}
	}

	public record C2S(UserPreferences preferences) {
		public static final StreamCodec<RegistryFriendlyByteBuf, C2S> STREAM_CODEC = BASE_STREAM_CODEC.map(C2S::new, C2S::preferences);
		public static final PayloadType<C2S> TYPE = new PayloadType<>(SortItOut.id("c2s_user_preferences"));

		public static class Type implements StreamCodecNetworkPayloadType<C2S> {
			public static final Type INSTANCE = new Type();

			private Type() {
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, C2S> getStreamCodec() {
				return STREAM_CODEC;
			}
		}
	}
}
