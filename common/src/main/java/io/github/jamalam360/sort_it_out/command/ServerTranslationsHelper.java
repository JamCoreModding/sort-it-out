package io.github.jamalam360.sort_it_out.command;

import java.util.HashMap;
import java.util.Map;

// Can't use Text.translatable if the mod is not installed clientside, and this is easier than using something like STA.
public class ServerTranslationsHelper {
	private static final Map<String, Map<String, String>> LANGUAGES = new HashMap<>();

	public static String getTranslation(String language, String key) {
		return LANGUAGES.getOrDefault(language, LANGUAGES.get("en_us")).getOrDefault(key, key);
	}

	static {
		// gradle-auto-populated
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("category.sort_it_out", "Sort It Out");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("key.sort_it_out.sort", "Sort Hovered Inventory");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("text.sort_it_out.sort_in_progress", "Sorting in progress; do not close this screen");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("text.sort_it_out.sort", "Sort container");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("text.sort_it_out.command.invert_sorting", "Invert Sorting: %s");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("text.sort_it_out.command.comparators", "Comparators: %s");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("text.sort_it_out.command.unknown_comparator", "Unknown comparator %s (valid values: %s)");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.discord", "Chat on Discord");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.github", "Report Issues on GitHub");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.packetSendInterval", "Packet Send Interval");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.packetSendInterval.tooltip", "The interval, in ticks, at which to send packets to the server when sorting client side. Too low of a value could cause you to be kicked from the server.");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.invertSorting", "Invert Sorting Order");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.comparators", "Comparators");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.comparators.tooltip", "A list of indicators to use when comparing items for sorting. If the first comparator says two items are equal, the next comparator will be used (and so on if that says they are equal).");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.comparators.empty_list", "Comparator list cannot be empty");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.comparators.display_name", "By Name");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.comparators.namespace", "By Namespace");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.comparators.count", "By Quantity");
		LANGUAGES.computeIfAbsent("en_us", (ignored) -> new HashMap<>()).put("config.sort_it_out.client_preferences.comparators.durability", "By Durability");
		// gradle-auto-populated
	}
}