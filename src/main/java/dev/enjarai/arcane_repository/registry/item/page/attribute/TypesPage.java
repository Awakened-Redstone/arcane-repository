package dev.enjarai.arcane_repository.registry.item.page.attribute;

import dev.enjarai.arcane_repository.registry.item.page.type.storage.ItemStorageTypePage;
import net.minecraft.item.ItemStack;

public class TypesPage extends ItemStorageTypePage.ItemStorageAttributePage {
    public TypesPage(String id) {
        super(id);
    }

    @Override
    public double getTypesMultiplier(ItemStack page) {
        return 2;
    }

    @Override
    public int getColor() {
        return 0x00ffff;
    }
}
