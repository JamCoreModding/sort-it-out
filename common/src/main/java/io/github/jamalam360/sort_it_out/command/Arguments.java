package io.github.jamalam360.sort_it_out.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.commands.Commands.argument;

public class Arguments {
	public static RequiredArgumentBuilder<CommandSourceStack, String> sortingComparator(String name) {
		return argument(name, StringArgumentType.string())
				.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(Stream.of(UserPreferences.SortingComparator.values()).map(Enum::name), builder));
	}

	@Nullable
	public static UserPreferences.SortingComparator getSortingComparator(CommandContext<CommandSourceStack> ctx, String name) {
		String value = ctx.getArgument(name, String.class);

		try {
			return UserPreferences.SortingComparator.valueOf(value);
		} catch (IllegalArgumentException e) {
			ctx.getSource().sendFailure(CommandFeedback.translatable(ctx, "text.sort_it_out.command.unknown_comparator", value,  Arrays.stream(UserPreferences.SortingComparator.values()).map(Enum::name).collect(Collectors.joining(", "))));
		}

		return null;
	}

	public static RequiredArgumentBuilder<CommandSourceStack, String> slotSortingTrigger(String name) {
		return argument(name, StringArgumentType.string())
				.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(Stream.of(UserPreferences.SlotSortingTrigger.values()).map(Enum::name), builder));
	}

	@Nullable
	public static UserPreferences.SlotSortingTrigger getSlotSortingTrigger(CommandContext<CommandSourceStack> ctx, String name) {
		String value = ctx.getArgument(name, String.class);

		try {
			return UserPreferences.SlotSortingTrigger.valueOf(value);
		} catch (IllegalArgumentException e) {
			ctx.getSource().sendFailure(CommandFeedback.translatable(ctx, "text.sort_it_out.command.unknown_slot_sorting_trigger", value, Arrays.stream(UserPreferences.SlotSortingTrigger.values()).map(Enum::name).collect(Collectors.joining(", "))));
		}

		return null;
	}
}
