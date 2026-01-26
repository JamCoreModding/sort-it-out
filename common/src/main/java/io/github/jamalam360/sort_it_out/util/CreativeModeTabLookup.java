package io.github.jamalam360.sort_it_out.util;

import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CreativeModeTabLookup {
	public static final CreativeModeTabLookup INSTANCE = new CreativeModeTabLookup();
	private static final int TAB_ORDER_FACTOR = 100_000;
	private final Map<Item, Integer> lookup;

	private CreativeModeTabLookup() {
		this.lookup = new ConcurrentHashMap<>();
	}

	public int getOrder(ItemStack stack) {
		return this.lookup.getOrDefault(stack.getItem(), 0);
	}

	public void buildLookup(Level level) {
		this.lookup.clear();

		List<Map.Entry<ResourceKey<CreativeModeTab>, CreativeModeTab>> entries = new ArrayList<>(BuiltInRegistries.CREATIVE_MODE_TAB.entrySet());
		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<ResourceKey<CreativeModeTab>, CreativeModeTab> entry = entries.get(i);
			CreativeModeTab tab = entry.getValue();

			if (tab.isAlignedRight() && entry.getKey() != CreativeModeTabs.OP_BLOCKS) {
				continue;
			}

			try {
				tab.buildContents(new CreativeModeTab.ItemDisplayParameters(level.enabledFeatures(), false, level.registryAccess()));
			} catch (Exception e) {
				SortItOut.LOGGER.error("Failed to build tab contents for {}", entry.getKey(), e);
				continue;
			}

			List<ItemStack> displayItems = new ArrayList<>(tab.getDisplayItems());
			for (int j = 0; j < displayItems.size(); j++) {
				ItemStack stack = displayItems.get(j);
				if (this.lookup.containsKey(stack.getItem())) {
					continue;
				}

				this.lookup.put(stack.getItem(), i * TAB_ORDER_FACTOR + j);
			}
		}

		SortItOut.LOGGER.info("Built creative tab lookup with {} entries", this.lookup.size());
	}
}
