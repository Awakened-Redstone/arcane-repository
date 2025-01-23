package dev.enjarai.arcane_repository.extension;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Map;
import java.util.Set;

public interface IndexesBooks {
    Map<Item, Set<Byte>> arcane_repository$getIndex();
    void arcane_repository$index();
    void arcane_repository$addToIndex(@NotNull Item item, @Range(from = 0, to = 5) int index);
    void arcane_repository$removeFromIndex(@NotNull Item item, @Range(from = 0, to = 5) int index);
    int arcane_repository$findIndex(@NotNull ItemStack stack);
}
