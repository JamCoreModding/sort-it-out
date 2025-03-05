package io.github.jamalam360.sort_it_out.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.jamalam360.sort_it_out.preference.UserPreferences;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortingComparatorListArgumentType implements ArgumentType<List<UserPreferences.SortingComparator>> {
	@Override
	public List<UserPreferences.SortingComparator> parse(final StringReader reader) throws CommandSyntaxException {
		List<UserPreferences.SortingComparator> values = new ArrayList<>();

		while (reader.canRead() && reader.peek() != ' ') {
			String name = reader.readUnquotedString();

			try {
				values.add(UserPreferences.SortingComparator.valueOf(name));
			} catch (IllegalArgumentException e) {
				throw new SimpleCommandExceptionType(() -> "Unknown sort mode provided ('" + name + "')").create();
			}

			if (reader.canRead() && reader.peek() == ',') {
				reader.expect(',');
				while (reader.canRead() && reader.peek() == ' ') {
					reader.expect(' ');
				}
			}
		}

		return values;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(Stream.iterate(1, (i) -> i != UserPreferences.SortingComparator.values().length, (i) -> i + 1)
				.flatMap((length) -> generatePermutations(List.of(UserPreferences.SortingComparator.values()), length).stream())
				.map((l) -> l.stream().map(Enum::name).collect(Collectors.joining(","))), builder);
	}

	private List<List<UserPreferences.SortingComparator>> generatePermutations(List<UserPreferences.SortingComparator> values, int length) {
		if (length == 1) {
			return values.stream().map(List::of).toList();
		}

		List<List<UserPreferences.SortingComparator>> result = new ArrayList<>();
		for (UserPreferences.SortingComparator value : values) {
			List<UserPreferences.SortingComparator> remainingElements = new ArrayList<>(values);
			remainingElements.remove(value);
			List<List<UserPreferences.SortingComparator>> smallerPermutations = this.generatePermutations(remainingElements, length - 1);

			for (List<UserPreferences.SortingComparator> permutation : smallerPermutations) {
				List<UserPreferences.SortingComparator> finalPermutation = new ArrayList<>(permutation);
				finalPermutation.addFirst(value);
				result.add(finalPermutation);
			}
		}

		return result;
	}

	@Override
	public Collection<String> getExamples() {
		return List.of(
				"COUNT",
				"COUNT,NAMESPACE",
				"NAMESPACE, DURABILITY, COUNT"
		);
	}
}
