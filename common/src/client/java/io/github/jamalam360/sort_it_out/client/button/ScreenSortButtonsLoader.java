package io.github.jamalam360.sort_it_out.client.button;

import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ScreenSortButtonsLoader extends SimpleJsonResourceReloadListener<ScreenSortButtons> {
	public static final ScreenSortButtonsLoader INSTANCE = new ScreenSortButtonsLoader();
	private List<ScreenSortButtons> values;

	private ScreenSortButtonsLoader() {
		super(ScreenSortButtons.CODEC, FileToIdConverter.json("sort_buttons"));
	}

	@Nullable
	public List<ScreenSortButton> getCustomButtonsForScreen(AbstractContainerScreen<?> screen) {
		Identifier id;

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
	protected void apply(Map<Identifier, ScreenSortButtons> values, ResourceManager resourceManager, ProfilerFiller profiler) {
		this.values = List.copyOf(values.values());
		SortItOut.LOGGER.info("Loaded {} sort button locations", values.size());
	}
}
