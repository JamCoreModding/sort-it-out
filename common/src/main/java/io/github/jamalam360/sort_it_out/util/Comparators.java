package io.github.jamalam360.sort_it_out.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Comparator;
import java.util.Optional;

public class Comparators {
	public static final Comparator<ItemStack> EMPTINESS = Comparator.comparing(ItemStack::isEmpty);
	public static final Comparator<ItemStack> COUNT = Comparator.comparingInt(ItemStack::getCount).reversed();
	public static final Comparator<ItemStack> CREATIVE_TAB = Comparator.comparing(CreativeModeTabLookup.INSTANCE::getOrder);
	public static final Comparator<ItemStack> DISPLAY_NAME = Comparator.comparing((stack) -> stack.getDisplayName().getString());
	public static final Comparator<ItemStack> DURABILITY = Comparator.comparingInt(ItemStack::getDamageValue).reversed();
	public static final Comparator<ItemStack> ENCHANTMENTS = Comparator.comparing((stack) -> Optional.ofNullable(stack.get(stack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS)).flatMap(enchantments -> enchantments.keySet().stream().min(Comparator.comparing(holder -> holder.value().description().getString()))).map(holder -> holder.value().description().getString()).orElse(""));
	public static final Comparator<ItemStack> NAMESPACE = Comparator.comparing((stack) -> BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
}
