package io.github.jamalam360.sort_it_out.client;

import io.github.jamalam360.jamlib.api.config.ConfigExtensions;
import io.github.jamalam360.jamlib.api.config.ConfigManager;
import io.github.jamalam360.jamlib.api.network.Network;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Config extends UserPreferences implements ConfigExtensions<Config> {
	public int packetSendInterval = 3;

	@Override
	public List<Link> getLinks() {
		return List.of(
				new Link(Link.DISCORD, "https://jamalam.tech/discord", Component.translatable("config.sort_it_out.discord")),
				new Link(Link.GITHUB, "https://github.com/JamCoreModding/sort-it-out", Component.translatable("config.sort_it_out.github"))
		);
	}

	@Override
	public List<ValidationError> getValidationErrors(ConfigManager<Config> manager, FieldValidationInfo info) {
		List<ValidationError> errors = ConfigExtensions.super.getValidationErrors(manager, info);

		if (info.name().equals("comparators")) {
			@SuppressWarnings("unchecked") List<SortingComparator> list = (List<SortingComparator>) info.value();

			if (list.isEmpty()) {
				errors.add(new ValidationError(ValidationError.Type.ERROR, info, Component.translatable("config.sort_it_out.client_preferences.comparators.empty_list")));
			}
		}

		return errors;
	}

	@Override
	public void afterSave() {
		// Only sync when in game
		if (Minecraft.getInstance() != null && Minecraft.getInstance().level != null) {
			if (SortItOutClient.justReceivedFromServer) {
				SortItOutClient.justReceivedFromServer = false;
			} else {
				this.sync();
			}
		}
	}

	public void sync() {
		if (this.shouldSync()) {
			SortItOut.LOGGER.info("Sending updated preferences to server");
			Network.sendToServer(BidirectionalUserPreferencesUpdatePacket.C2S.TYPE, new BidirectionalUserPreferencesUpdatePacket.C2S(this));
		}
	}

	private boolean shouldSync() {
		return Network.getServerCapability().canReceive(BidirectionalUserPreferencesUpdatePacket.C2S.TYPE) && // Only send if the server has SIO installed
						Minecraft.getInstance().getSingleplayerServer() == null; // and we are not in single player or hosting a LAN server
	}
}
