package io.github.jamalam360.sort_it_out.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class SortItOutPlatformImpl {
	public static String translateToRuntimeMappings(String mojmapClassName, String intermediaryClassName) {
		return FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", intermediaryClassName);
	}
}
