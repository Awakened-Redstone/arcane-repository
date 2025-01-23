package dev.enjarai.arcane_repository.registry.item.page;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface TypeDependentPage {
    List<TypePageItem> getCompatibleTypes(ItemStack page);
}
