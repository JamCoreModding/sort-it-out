package io.github.jamalam360.sort_it_out.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static io.github.jamalam360.sort_it_out.command.Arguments.*;
import static io.github.jamalam360.sort_it_out.command.CommandFeedback.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SortItOutCommands {
	public static void register() {
		CommandRegistrationEvent.EVENT.register(SortItOutCommands::registerCommands);
	}

	private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext register, Commands.CommandSelection selection) {
		dispatcher.register(
				literal("sortitout")
						.then(literal("help")
								.executes(ctx -> {
									ctx.getSource().sendSuccess(() -> CommandFeedback.translatable(ctx, "text.sort_it_out.command.help"), false);
									return Command.SINGLE_SUCCESS;
								})
						)
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
												sortingComparator("comparator" + 0).executes(ctx -> setComparators(ctx, 1))
														.then(sortingComparator("comparator" + 1).executes(ctx -> setComparators(ctx, 2))
																.then(sortingComparator("comparator" + 2).executes(ctx -> setComparators(ctx, 3))
																		.then(sortingComparator("comparator" + 3).executes(ctx -> setComparators(ctx, 4))
																				.then(sortingComparator("comparator" + 4).executes(ctx -> setComparators(ctx, 5)))
																		)
																)
														)
										)
								)
								.then(literal("slotSortingTrigger")
										.executes(SortItOutCommands::echoSlotSortingTrigger)
										.then(slotSortingTrigger("value")
												.executes(SortItOutCommands::setSlotSortingTrigger)
										)
								)
						)
		);
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
		ctx.getSource().sendSuccess(() -> formatInvertSorting(ctx), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setInvertSorting(CommandContext<CommandSourceStack> ctx) {
		modifyConfig(ctx, (prefs) -> prefs.invertSorting = BoolArgumentType.getBool(ctx, "value"));
		ctx.getSource().sendSuccess(() -> formatInvertSorting(ctx), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int echoComparators(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> formatComparators(ctx), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setComparators(CommandContext<CommandSourceStack> ctx, int cardinality) {
		List<UserPreferences.SortingComparator> comparators = new ArrayList<>();

		for (int i = 0; i < cardinality; i++) {
			comparators.add(getSortingComparator(ctx, "comparator" + i));
		}

		modifyConfig(ctx, (prefs) -> prefs.comparators = comparators);
		ctx.getSource().sendSuccess(() -> formatComparators(ctx), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int echoSlotSortingTrigger(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> formatSlotSortingTrigger(ctx), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setSlotSortingTrigger(CommandContext<CommandSourceStack> ctx) {
		UserPreferences.SlotSortingTrigger trigger = getSlotSortingTrigger(ctx, "value");

		if (trigger == null) {
			return 0;
		}

		modifyConfig(ctx, (prefs) -> prefs.slotSortingTrigger = trigger);
		ctx.getSource().sendSuccess(() -> formatSlotSortingTrigger(ctx), false);
		return Command.SINGLE_SUCCESS;
	}
}
