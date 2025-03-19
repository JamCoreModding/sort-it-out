package io.github.jamalam360.sort_it_out.command;

import com.mojang.brigadier.context.CommandContext;
import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import io.github.jamalam360.sort_it_out.mixinsupport.ServerPlayerLanguageAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CommandFeedback {
	public static Component formatInvertSorting(CommandContext<CommandSourceStack> ctx) {
		return translatable(ctx, "text.sort_it_out.command.invert_sorting", getPlayerPrefs(ctx).invertSorting ? Component.literal("Yes") : Component.literal("No"));
	}

	public static Component formatComparators(CommandContext<CommandSourceStack> ctx) {
		return translatable(ctx, "text.sort_it_out.command.comparators", formatComparatorList(ctx, getPlayerPrefs(ctx).comparators));
	}

	public static Component formatComparatorList(CommandContext<CommandSourceStack> ctx, List<UserPreferences.SortingComparator> comparators) {
		MutableComponent result = formatSingleComparator(ctx, comparators.get(0));

		for (int i = 1; i < comparators.size(); i++) {
			result = result.append(", ").append(formatSingleComparator(ctx, comparators.get(i)));
		}

		return result;
	}

	private static MutableComponent formatSingleComparator(CommandContext<CommandSourceStack> ctx, UserPreferences.SortingComparator comparator) {
		return translatable(ctx, "config.sort_it_out.client_preferences.comparators." + comparator.name().toLowerCase());
	}

	public static Component formatSlotSortingTrigger(CommandContext<CommandSourceStack> ctx) {
		return translatable(ctx, "text.sort_it_out.command.slot_sorting_trigger", formatSingleSlotSortingTrigger(ctx, getPlayerPrefs(ctx).slotSortingTrigger));
	}

	private static MutableComponent formatSingleSlotSortingTrigger(CommandContext<CommandSourceStack> ctx, UserPreferences.SlotSortingTrigger comparator) {
		return translatable(ctx, "config.sort_it_out.client_preferences.slotSortingTrigger." + comparator.name().toLowerCase());
	}

	public static MutableComponent translatable(CommandContext<CommandSourceStack> ctx, String key, Object... args) {
		if (NetworkManager.canPlayerReceive(ctx.getSource().getPlayer(), BidirectionalUserPreferencesUpdatePacket.S2C.TYPE.location())) {
			return Component.translatable(key, args);
		} else {
			String lang = ctx.getSource().getPlayer() == null ? "en_us" : ((ServerPlayerLanguageAccessor) ctx.getSource().getPlayer()).sort_it_out$getLanguage();

			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof Component component) {
					args[i] = component.getString();
				}
			}

			return Component.literal(String.format(ServerTranslationsHelper.getTranslation(lang, key), args));
		}
	}

	public static UserPreferences getPlayerPrefs(CommandContext<CommandSourceStack> ctx) {
		return ServerUserPreferences.INSTANCE.getPlayerPreferences(ctx.getSource().getPlayer());
	}
}
