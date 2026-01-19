package io.github.jamalam360.sort_it_out.util;

import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CreativeModeTabLookup {
	public static final CreativeModeTabLookup INSTANCE = new CreativeModeTabLookup();
	private final HashMap<Item, CreativeModeTab> lookup;

	private CreativeModeTabLookup() {
		this.lookup = new HashMap<>();
	}

	@Nullable
	public CreativeModeTab lookup(ItemStack stack) {
		return this.lookup.get(stack.getItem());
	}

	public void build(Level level) {
		this.lookup.clear();

		for (Map.Entry<ResourceKey<CreativeModeTab>, CreativeModeTab> entry : BuiltInRegistries.CREATIVE_MODE_TAB.entrySet()) {
			CreativeModeTab tab = entry.getValue();

			try {
				tab.buildContents(new CreativeModeTab.ItemDisplayParameters(level.enabledFeatures(), false, level.registryAccess()));
			} catch (Exception e) {
				SortItOut.LOGGER.error("Failed to build tab contents for {}", entry.getKey(), e);
			}

			this.associate(tab, tab.getDisplayItems());
		}

		SortItOut.LOGGER.info("Built creative tab lookup with {} entries", this.lookup.size());
	}

	private void associate(CreativeModeTab tab, Collection<ItemStack> displayItems) {
		for (ItemStack stack : displayItems) {
			if (this.lookup.containsKey(stack.getItem())) {
				continue;
			}

			this.lookup.put(stack.getItem(), tab);
		}
	}
}
