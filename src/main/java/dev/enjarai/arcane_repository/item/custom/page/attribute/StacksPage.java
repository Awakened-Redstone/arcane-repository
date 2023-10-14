package dev.enjarai.arcane_repository.item.custom.page.attribute;

import dev.enjarai.arcane_repository.item.custom.page.type.ItemStorageTypePage;
import net.minecraft.item.ItemStack;

public class StacksPage extends ItemStorageTypePage.ItemStorageAttributePage {
    public StacksPage(String id) {
        super(id);
    }

    @Override
    public double getStacksMultiplier(ItemStack page) {
        return 2;
    } // TODO make this percentages, and display as such

    @Override
    public int getColor() {
        return 0xffff00;
    }
}
