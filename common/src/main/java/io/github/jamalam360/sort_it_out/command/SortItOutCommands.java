package io.github.jamalam360.sort_it_out.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SortItOutCommands {
	public static void register() {
		CommandRegistrationEvent.EVENT.register(SortItOutCommands::registerCommands);
	}

	private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext register, Commands.CommandSelection selection) {
		dispatcher.register(
				literal("sortitout")
						.then(literal("preferences")
								.then(literal("invertSorting")
										.executes(SortItOutCommands::echoInvertSorting)
										.then(argument("value", bool())
												.executes(SortItOutCommands::setInvertSorting)
										)
								)
								.then(literal("comparators")
										.executes(SortItOutCommands::echoComparators)
										.then(
												sortingComparator(0).then(sortingComparator(1).then(sortingComparator(2).then(sortingComparator(3))))
										)
								)
						)
		);
	}

	private static RequiredArgumentBuilder<CommandSourceStack, String> sortingComparator(int index) {
		return argument("comparator" + index, StringArgumentType.string())
				.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(Stream.of(UserPreferences.SortingComparator.values()).map(Enum::name), builder))
				.executes(ctx -> setComparators(ctx, index + 1));
	}

	private static UserPreferences getPlayerPrefs(CommandContext<CommandSourceStack> ctx) {
		return ServerUserPreferences.INSTANCE.getPlayerPreferences(ctx.getSource().getPlayer());
	}

	private static void modifyConfig(CommandContext<CommandSourceStack> ctx, Consumer<UserPreferences> modifier) {
		ConfigManager<UserPreferences> manager = ServerUserPreferences.INSTANCE.getPlayerConfigManager(ctx.getSource().getPlayer());
		modifier.accept(manager.get());
		manager.save();

		if (ctx.getSource().getPlayer() != null && NetworkManager.canPlayerReceive(ctx.getSource().getPlayer(), BidirectionalUserPreferencesUpdatePacket.S2C.TYPE)) {
			NetworkManager.sendToPlayer(ctx.getSource().getPlayer(), new BidirectionalUserPreferencesUpdatePacket.S2C(manager.get()));
		}
	}

	private static int echoInvertSorting(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> translatable(ctx, "text.sort_it_out.command.invert_sorting", getPlayerPrefs(ctx).invertSorting ? Component.literal("Yes") : Component.literal("No")), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setInvertSorting(CommandContext<CommandSourceStack> ctx) {
		modifyConfig(ctx, (prefs) -> prefs.invertSorting = BoolArgumentType.getBool(ctx, "value"));
		ctx.getSource().sendSuccess(() -> translatable(ctx, "text.sort_it_out.command.invert_sorting", getPlayerPrefs(ctx).invertSorting ? Component.literal("Yes") : Component.literal("No")), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int echoComparators(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> translatable(ctx, "text.sort_it_out.command.comparators", formatComparators(ctx, getPlayerPrefs(ctx).comparators)), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setComparators(CommandContext<CommandSourceStack> ctx, int cardinality) {
		List<UserPreferences.SortingComparator> comparators = new ArrayList<>();

		for (int i = 0; i < cardinality; i++) {
			String value = ctx.getArgument("comparator" + i, String.class);

			try {
				comparators.add(UserPreferences.SortingComparator.valueOf(value));
			} catch (IllegalArgumentException e) {
				ctx.getSource().sendFailure(translatable(ctx, "text.sort_it_out.command.unknown_comparator", value, formatComparators(ctx, Arrays.stream(UserPreferences.SortingComparator.values()).toList())));
			}
		}

		modifyConfig(ctx, (prefs) -> prefs.comparators = comparators);
		ctx.getSource().sendSuccess(() -> translatable(ctx, "text.sort_it_out.command.comparators", formatComparators(ctx, getPlayerPrefs(ctx).comparators)), false);
		return Command.SINGLE_SUCCESS;
	}

	private static Component formatComparators(CommandContext<CommandSourceStack> ctx, List<UserPreferences.SortingComparator> comparators) {
		MutableComponent result = createComparatorComponent(ctx, comparators.getFirst());

		for (int i = 1; i < comparators.size(); i++) {
			result = result.append(", ").append(createComparatorComponent(ctx, comparators.get(i)));
		}

		return result;
	}

	private static MutableComponent createComparatorComponent(CommandContext<CommandSourceStack> ctx, UserPreferences.SortingComparator comparator) {
		return translatable(ctx, "config.sort_it_out.client_preferences.comparators." + comparator.name().toLowerCase());
	}

	private static MutableComponent translatable(CommandContext<CommandSourceStack> ctx, String key, Object... args) {
		if (NetworkManager.canPlayerReceive(ctx.getSource().getPlayer(), BidirectionalUserPreferencesUpdatePacket.S2C.TYPE)) {
			return Component.translatable(key, args);
		} else {
			String lang = ctx.getSource().getPlayer() == null ? "en_us" : ctx.getSource().getPlayer().clientInformation().language();

			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof Component component) {
					args[i] = component.getString();
				}
			}

			return Component.literal(String.format(ServerTranslationsHelper.getTranslation(lang, key), args));
		}
	}
}
