package io.github.jamalam360.sort_it_out.fabric;

import io.github.jamalam360.sort_it_out.client.SortItOutClient;
import net.fabricmc.api.ClientModInitializer;

public class SortItOutClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SortItOutClient.init();
	}
}
