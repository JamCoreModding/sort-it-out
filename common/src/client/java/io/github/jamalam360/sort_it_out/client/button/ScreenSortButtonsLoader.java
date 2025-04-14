package io.github.jamalam360.sort_it_out.client.button;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScreenSortButtonsLoader extends SimpleJsonResourceReloadListener {
	public static final ScreenSortButtonsLoader INSTANCE = new ScreenSortButtonsLoader();
	private List<ScreenSortButtons> values;

	private ScreenSortButtonsLoader() {
		super(new Gson(), "sort_buttons");
	}

	@Nullable
	public List<ScreenSortButton> getCustomButtonsForScreen(AbstractContainerScreen<?> screen) {
		ResourceLocation id;

		try {
			id = BuiltInRegistries.MENU.getKey(screen.getMenu().getType());
		} catch (UnsupportedOperationException ignored) {
			return null;
		}

		for (ScreenSortButtons buttons : this.values) {
			if (buttons.type().equals(id)) {
				return buttons.sortButtons();
			}
		}

		return null;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> values, ResourceManager resourceManager, ProfilerFiller profiler) {
		this.values = new ArrayList<>();

		for (Map.Entry<ResourceLocation, JsonElement> el : values.entrySet()) {
			DataResult<ScreenSortButtons> result = ScreenSortButtons.CODEC.parse(JsonOps.INSTANCE, el.getValue());

			if (result.error().isPresent()) {
				SortItOut.LOGGER.error("Failed to decode screen sort button definition at {}: {}", el.getKey(), result.error().get().message());
			} else {
				this.values.add(result.get().orThrow());
			}
		}

		SortItOut.LOGGER.info("Loaded {} sort button locations", values.size());
	}
}
