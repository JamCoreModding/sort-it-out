package io.github.jamalam360.sort_it_out.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.registries.DeferredRegister;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.mixin.ArgumentTypeInfosAccessor;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SortItOutCommands {
	private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(SortItOut.MOD_ID, Registries.COMMAND_ARGUMENT_TYPE);

	public static void register() {
		SingletonArgumentInfo<SortingComparatorListArgumentType> info = SingletonArgumentInfo.contextFree(SortingComparatorListArgumentType::new);
		ARGUMENT_TYPES.register(SortItOut.id("sorting_comparator"), () -> info);
		ARGUMENT_TYPES.register();
		ArgumentTypeInfosAccessor.getByClass().put(SortingComparatorListArgumentType.class, info);
		CommandRegistrationEvent.EVENT.register(SortItOutCommands::registerCommands);
	}

	private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext register, net.minecraft.commands.Commands.CommandSelection selection) {
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
										.then(RequiredArgumentBuilder.<CommandSourceStack, List<UserPreferences.SortingComparator>>argument("comparators", new SortingComparatorListArgumentType())
												.executes(SortItOutCommands::setComparators))

								)
						)
		);
	}

	private static UserPreferences getPlayerPrefs(CommandContext<CommandSourceStack> ctx) {
		return ServerUserPreferences.INSTANCE.getPlayerPreferences(ctx.getSource().getPlayer());
	}

	private static void modifyConfig(CommandContext<CommandSourceStack> ctx, Consumer<UserPreferences> modifier) {
		ConfigManager<UserPreferences> manager = ServerUserPreferences.INSTANCE.getPlayerConfigManager(ctx.getSource().getPlayer());
		modifier.accept(manager.get());
		manager.save();

		if (NetworkManager.canPlayerReceive(ctx.getSource().getPlayer(), BidirectionalUserPreferencesUpdatePacket.S2C.TYPE)) {
			NetworkManager.sendToPlayer(ctx.getSource().getPlayer(), new BidirectionalUserPreferencesUpdatePacket.S2C(manager.get()));
		}
	}

	private static int echoInvertSorting(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> Component.translatable("text.sort_it_out.command.invert_sorting", getPlayerPrefs(ctx).invertSorting ? Component.literal("Yes") : Component.literal("No")), false);
		return 0;
	}

	private static int setInvertSorting(CommandContext<CommandSourceStack> ctx) {
		modifyConfig(ctx, (prefs) -> prefs.invertSorting = BoolArgumentType.getBool(ctx, "value"));
		ctx.getSource().sendSuccess(() -> Component.translatable("text.sort_it_out.command.comparators", getPlayerPrefs(ctx).comparators.stream().map(Enum::name).collect(Collectors.joining(", "))), false);
		return 0;
	}

	private static int echoComparators(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> Component.translatable("text.sort_it_out.command.comparators", formatComparators(getPlayerPrefs(ctx).comparators)), false);
		return 0;
	}

	@SuppressWarnings("unchecked")
	private static int setComparators(CommandContext<CommandSourceStack> ctx) {
		List<UserPreferences.SortingComparator> args = ctx.getArgument("comparators", List.class);

		if (args.isEmpty()) {
			ctx.getSource().sendFailure(Component.translatable("text.sort_it_out.command.comparators.empty_list"));
		}

		modifyConfig(ctx, (prefs) -> prefs.comparators = args);
		ctx.getSource().sendSuccess(() -> Component.translatable("text.sort_it_out.command.comparators", formatComparators(getPlayerPrefs(ctx).comparators)), false);
		return 0;
	}

	private static Component formatComparators(List<UserPreferences.SortingComparator> comparators) {
		MutableComponent result = createComparatorComponent(comparators.getFirst());

		for (int i = 1; i < comparators.size(); i++) {
			result = result.append(", ").append(createComparatorComponent(comparators.get(i)));
		}

		return result;
	}

	private static MutableComponent createComparatorComponent(UserPreferences.SortingComparator comparator) {
		return Component.translatable("config.sort_it_out.client_preferences.comparators." + comparator.name().toLowerCase());
	}
}
