package io.github.jamalam360.sort_it_out;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.registries.DeferredRegister;
import io.github.jamalam360.jamlib.config.ConfigManager;
import io.github.jamalam360.sort_it_out.mixin.ArgumentTypeInfosAccessor;
import io.github.jamalam360.sort_it_out.network.BidirectionalUserPreferencesUpdatePacket;
import io.github.jamalam360.sort_it_out.preference.ServerUserPreferences;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SortItOutCommands {
	private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(SortItOut.MOD_ID, Registries.COMMAND_ARGUMENT_TYPE);

	public static void register() {
		SingletonArgumentInfo<SortModeArgumentType> info = SingletonArgumentInfo.contextFree(SortModeArgumentType::new);
		ARGUMENT_TYPES.register(SortItOut.id("sort_mode"), () -> info);
		ARGUMENT_TYPES.register();
		ArgumentTypeInfosAccessor.getByClass().put(SortModeArgumentType.class, info);
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
								.then(literal("sortMode")
										.executes(SortItOutCommands::echoSortMode)
										.then(argument("value", new SortModeArgumentType())
												.executes(SortItOutCommands::setSortMode)
										)
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
		System.out.println("Updated config: " + manager.get().sortMode);
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
		ctx.getSource().sendSuccess(() -> Component.translatable("text.sort_it_out.command.invert_sorting", getPlayerPrefs(ctx).invertSorting ? Component.literal("Yes") : Component.literal("No")), false);
		return 0;
	}

	private static int echoSortMode(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> Component.translatable("text.sort_it_out.command.sort_mode", Component.translatable("config.sort_it_out.client_preferences.sortMode." + getPlayerPrefs(ctx).sortMode.name().toLowerCase())), false);
		return 0;
	}

	private static int setSortMode(CommandContext<CommandSourceStack> ctx) {
		System.out.println("doesn;t seem to work proeprly");
		System.out.println(ctx.getArgument("value", UserPreferences.SortMode.class));
		modifyConfig(ctx, (prefs) -> prefs.sortMode = ctx.getArgument("value", UserPreferences.SortMode.class));
		System.out.println(getPlayerPrefs(ctx).sortMode);
		ctx.getSource().sendSuccess(() -> Component.translatable("text.sort_it_out.command.sort_mode", Component.translatable("config.sort_it_out.client_preferences.sortMode." + getPlayerPrefs(ctx).sortMode.name().toLowerCase())), false);
		return 0;
	}

	private static class SortModeArgumentType implements ArgumentType<UserPreferences.SortMode> {
		@Override
		public UserPreferences.SortMode parse(final StringReader reader) throws CommandSyntaxException {
			String name = reader.readUnquotedString();
			try {
				return UserPreferences.SortMode.valueOf(name);
			} catch (IllegalArgumentException e) {
				throw new SimpleCommandExceptionType(() -> "Unknown sort mode provided").create();
			}
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
			return SharedSuggestionProvider.suggest(Stream.of(UserPreferences.SortMode.values()).map(Enum::name), builder);
		}

		@Override
		public Collection<String> getExamples() {
			return Stream.of(UserPreferences.SortMode.values()).map(Enum::name).collect(Collectors.toList());
		}
	}
}
