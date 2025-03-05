package io.github.jamalam360.sort_it_out.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.Comparator;

public class Comparators {
	public static final Comparator<ItemStack> EMPTINESS = Comparator.comparing(ItemStack::isEmpty);
	public static final Comparator<ItemStack> COUNT = Comparator.comparingInt(ItemStack::getCount).reversed();
	public static final Comparator<ItemStack> DURABILITY = Comparator.comparingInt(ItemStack::getDamageValue).reversed();
	public static final Comparator<ItemStack> DISPLAY_NAME = Comparator.comparing((stack) -> stack.getDisplayName().getString());
	public static final Comparator<ItemStack> NAMESPACE = Comparator.comparing((stack) -> BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
}
