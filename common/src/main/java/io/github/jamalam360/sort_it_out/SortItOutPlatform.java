package io.github.jamalam360.sort_it_out;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Nullable;

public class SortItOutPlatform {
	@ExpectPlatform
	public static String translateToRuntimeMappings(String srgClassName, String intermediaryClassName) {
		throw new AssertionError();
	}
}
