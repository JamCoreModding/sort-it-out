package io.github.jamalam360.sort_it_out.client;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.jamlib.config.ConfigExtensions;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import io.github.jamalam360.sort_it_out.util.NetworkManager2;
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
		this.sync();
	}

	public void sync() {
		if (NetworkManager.canServerReceive(BidirectionalUserPreferencesUpdatePacket.C2S.TYPE.location()) && !Minecraft.getInstance().isSingleplayer()) {
			SortItOut.LOGGER.info("Sending updated preferences to server");
			NetworkManager2.sendToServer(new BidirectionalUserPreferencesUpdatePacket.C2S(this), BidirectionalUserPreferencesUpdatePacket.C2S.STREAM_CODEC);
		}
	}
}
