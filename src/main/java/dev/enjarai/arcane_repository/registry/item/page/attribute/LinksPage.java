package dev.enjarai.arcane_repository.registry.item.page.attribute;

import dev.enjarai.arcane_repository.registry.item.page.type.IndexingTypePage;
import net.minecraft.item.ItemStack;

public class LinksPage extends IndexingTypePage.IndexingAttributePage {
    public LinksPage(String id) {
        super(id);
    }

    @Override
    public double getLinksMultiplier(ItemStack page) {
        return 2;
    }

    @Override
    public int getColor() {
        return 0x2222ff;
    }
}