package io.github.jamalam360.sort_it_out.client;

import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.jamlib.config.ConfigExtensions;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.client.Minecraft;

import java.util.List;

public class Config extends UserPreferences implements ConfigExtensions<Config> {
	public int packetSendInterval = 3;

	// TODO: replace this with a dedicated on save method in JamLib
	@Override
	public List<ValidationError> getValidationErrors(ConfigManager<Config> manager, FieldValidationInfo info) {
		List<ValidationError> errors = ConfigExtensions.super.getValidationErrors(manager, info);

		if (errors.isEmpty()) {
			this.sync();
		}

		return errors;
	}

	public void sync() {
		if (NetworkManager.canServerReceive(BidirectionalUserPreferencesUpdatePacket.C2S.TYPE) && !Minecraft.getInstance().isSingleplayer()) {
			SortItOut.LOGGER.info("Sending updated preferences to server");
			NetworkManager.sendToServer(new BidirectionalUserPreferencesUpdatePacket.C2S(this));
		}
	}
}
