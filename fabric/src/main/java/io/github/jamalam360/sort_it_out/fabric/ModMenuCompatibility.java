package io.github.jamalam360.sort_it_out.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.jamalam360.jamlib.client.config.gui.ConfigScreen;
import io.github.jamalam360.sort_it_out.client.SortItOutClient;

/**
 * This has to be done on 1.20.1 as ModMenu has a bug that has not had a fix backported,
 * meaning that JamLib can't provide config screens for config managers registered during
 * client init.
 */
public class ModMenuCompatibility implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> new ConfigScreen<>(SortItOutClient.CONFIG, screen);
	}
}
