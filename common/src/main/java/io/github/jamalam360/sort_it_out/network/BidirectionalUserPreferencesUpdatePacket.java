package io.github.jamalam360.sort_it_out.network;

import io.github.jamalam360.jamlib.api.network.PacketKind;
import io.github.jamalam360.jamlib.api.network.PacketPayload;
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

	public record S2C(UserPreferences preferences) implements PacketPayload<S2C> {
		public static final StreamCodec<RegistryFriendlyByteBuf, S2C> STREAM_CODEC = BASE_STREAM_CODEC.map(S2C::new, S2C::preferences);
		public static final PacketKind<S2C> KIND = PacketKind.of(SortItOut.id("s2c_user_preferences"), STREAM_CODEC);

		@Override
		public PacketKind<S2C> getKind() {
			return KIND;
		}

		@Override
		public S2C getPayload() {
			return this;
		}
	}

	public record C2S(UserPreferences preferences) implements PacketPayload<C2S> {
		public static final StreamCodec<RegistryFriendlyByteBuf, C2S> STREAM_CODEC = BASE_STREAM_CODEC.map(C2S::new, C2S::preferences);
		public static final PacketKind<C2S> KIND = PacketKind.of(SortItOut.id("c2s_user_preferences"), STREAM_CODEC);

		@Override
		public PacketKind<C2S> getKind() {
			return KIND;
		}

		@Override
		public C2S getPayload() {
			return this;
		}
	}
}
