package io.github.jamalam360.sort_it_out.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import io.github.jamalam360.sort_it_out.util.NetworkManager2;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static io.github.jamalam360.sort_it_out.command.Arguments.*;
import static io.github.jamalam360.sort_it_out.command.CommandFeedback.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SortItOutCommands {
	public static void register() {
		CommandRegistrationEvent.EVENT.register(SortItOutCommands::registerCommands);

		if (Platform.isDevelopmentEnvironment()) {
			CommandRegistrationEvent.EVENT.register(SortItOutCommands::registerDevCommands);
		}
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

	private static void registerDevCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext register, Commands.CommandSelection selection) {
		dispatcher.register(
				literal("sortitoutdev")
						.then(
								literal("barrel")
										.then(
												argument("size", integer(1, 27))
														.executes(SortItOutCommands::spawnBarrel)
										)
						)
		);
	}

	private static void modifyConfig(CommandContext<CommandSourceStack> ctx, Consumer<UserPreferences> modifier) {
		ConfigManager<UserPreferences> manager = ServerUserPreferences.INSTANCE.getPlayerConfigManager(ctx.getSource().getPlayer());
		modifier.accept(manager.get());
		manager.save();

		if (ctx.getSource().getPlayer() != null && NetworkManager.canPlayerReceive(ctx.getSource().getPlayer(), BidirectionalUserPreferencesUpdatePacket.S2C.TYPE.location())) {
			NetworkManager2.sendToPlayer(ctx.getSource().getPlayer(), new BidirectionalUserPreferencesUpdatePacket.S2C(manager.get()), BidirectionalUserPreferencesUpdatePacket.S2C.STREAM_CODEC);
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

	private static int spawnBarrel(CommandContext<CommandSourceStack> ctx) {
		int size = IntegerArgumentType.getInteger(ctx, "size");
		ServerPlayer player = ctx.getSource().getPlayer();
		Level level = player.level();
		level.setBlock(player.getOnPos(), Blocks.BARREL.defaultBlockState(), Block.UPDATE_ALL);
		BarrelBlockEntity blockEntity = (BarrelBlockEntity) level.getBlockEntity(player.getOnPos());
		List<Item> items = BuiltInRegistries.ITEM.stream().filter((i) -> i != Items.AIR).toList();

		while (size != 0) {
			int slot = 0;
			while (!blockEntity.getItem(slot).isEmpty()) {
				slot = level.random.nextInt(27);
			}

			Item item = items.get(level.random.nextInt(items.size()));
			ItemStack stack = item.getDefaultInstance();

			if (stack.getMaxStackSize() != 1) {
				stack.setCount(level.random.nextInt(1, stack.getMaxStackSize()));
			}

			blockEntity.setItem(slot, stack);
			size -= 1;
		}

		ctx.getSource().sendSuccess(() -> Component.literal("Spawned barrel"), false);
		return Command.SINGLE_SUCCESS;
	}
}
