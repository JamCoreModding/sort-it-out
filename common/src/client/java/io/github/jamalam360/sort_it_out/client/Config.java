package io.github.jamalam360.sort_it_out.client;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.jamlib.config.ConfigExtensions;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.C2SUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.UserPreferences;

import java.util.List;

public class Config extends UserPreferences implements ConfigExtensions<Config> {
	// TODO: replace this with a dedicated on save method in JamLib
	@Override
	public List<ValidationError> getValidationErrors(ConfigManager<Config> manager, FieldValidationInfo info) {
		List<ValidationError> errors = ConfigExtensions.super.getValidationErrors(manager, info);

		if (errors.isEmpty() && NetworkManager.canServerReceive(C2SUserPreferencesUpdatePacket.TYPE)) {
			SortItOut.LOGGER.info("Sending updated preferences to server");
			NetworkManager.sendToServer(new C2SUserPreferencesUpdatePacket(this));
		}

		return errors;
	}
}
