package io.github.jamalam360.sort_it_out.client.button;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

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
		if (screen instanceof InventoryScreen) {
			return List.of(new ScreenSortButton(158, 68, 9));
		}

		ResourceLocation id;

		try {
			id = BuiltInRegistries.MENU.getKey(screen.getMenu().getType());
		} catch (UnsupportedOperationException ignored) {
			id = null;
		}

		ResourceLocation finalId = id;

		for (ScreenSortButtons buttons : this.values) {
			if (buttons.type().equals(finalId)) {
				return buttons.sortButtons();
			}
		}

		return null;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> values, ResourceManager resourceManager, ProfilerFiller profiler) {
		this.values = values.values().stream().map(el -> ScreenSortButtons.CODEC.parse(JsonOps.INSTANCE, el).getOrThrow(false, JsonParseException::new)).toList();
		SortItOut.LOGGER.info("Loaded {} sort button locations", values.size());
	}
}
