package io.github.jamalam360.sort_it_out.fabric;

import io.github.jamalam360.sort_it_out.SortItOutPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.jetbrains.annotations.Nullable;

public class SortItOutPlatformImpl {
	public static String translateFromMojmapToRuntime(String mojmapClassName) {
		return FabricLoader.getInstance().getMappingResolver().mapClassName("mojmap", mojmapClassName);
	}
}
