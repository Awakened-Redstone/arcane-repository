package dev.enjarai.arcane_repository.registry.item.page.type.storage;

import dev.enjarai.arcane_repository.registry.item.page.TypePageItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

import java.util.List;

import static dev.enjarai.arcane_repository.registry.ModItems.BLOCK_STORAGE_TYPE_PAGE;

public class BlockStorageTypePage extends ItemStorageTypePage {
    public BlockStorageTypePage(String id) {
        super(id);
    }

    @Override
    public int getColor() {
        return 0x888888;
    }

    @Override
    public boolean mixColor(ItemStack stack) {
        return true;
    }

    @Override
    public MutableText getTypeDisplayName() {
        return super.getTypeDisplayName().fillStyle(Style.EMPTY.withColor(getColor()));
    }

    @Override
    protected int getBaseInsertPriority(ItemStack book) {
        return 1;
    }

    @Override
    protected boolean canInsert(ItemStack book, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof BlockItem)) return false;

        return super.canInsert(book, itemStack);
    }

    public static abstract class BlockStorageAttributePage extends ItemStorageAttributePage {
        public BlockStorageAttributePage(String id) {
            super(id);
        }

        @Override
        public List<TypePageItem> getCompatibleTypes(ItemStack page) {
            return List.of(BLOCK_STORAGE_TYPE_PAGE);
        }
    }
}
