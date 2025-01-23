package dev.enjarai.arcane_repository.repository;

import com.mojang.logging.LogUtils;
import dev.enjarai.arcane_repository.extension.IndexesBooks;
import dev.enjarai.arcane_repository.registry.ModDataComponentTypes;
import dev.enjarai.arcane_repository.registry.item.MysticalBookItem;
import dev.enjarai.arcane_repository.registry.item.page.TypePageItem;
import dev.enjarai.arcane_repository.registry.item.page.type.IndexingTypePage;
import dev.enjarai.arcane_repository.util.FallbackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.*;

public class Repository {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final ItemStack book;
    private final Map<Item, Set<BlockPos>> itemIndex;

    public Repository(ItemStack book) {
        this.itemIndex = new HashMap<>();

        if (book.getItem() instanceof MysticalBookItem mysticalBook) {
            Optional<TypePageItem> perhapsTypePage = mysticalBook.getTypePage(book);
            if (perhapsTypePage.isEmpty()) {
                throw new IllegalArgumentException("Tried to create repository from a book without a type page!");
            } else if (!(perhapsTypePage.get() instanceof IndexingTypePage)) {
                throw new IllegalArgumentException("Tried to create repository from a book without an indexing type page!");
            }

            this.book = book;
        } else {
            throw new IllegalArgumentException("Tried to create repository from a non mystical book item");
        }
    }

    public Repository(ItemStack book, Map<Item, Set<BlockPos>> itemIndex) {
        this.itemIndex = itemIndex;
        this.book = book;
    }

    public Map<Item, Set<BlockPos>> getItemIndex() {
        return itemIndex;
    }

    public void buildIndex(World world) {
        itemIndex.clear();

        for (BlockPos blockPos : book.getOrDefault(ModDataComponentTypes.LINKED_BLOCKS, List.<BlockPos>of())) {
            if (world.getBlockEntity(blockPos) instanceof IndexesBooks bookshelf) {
                bookshelf.arcane_repository$getIndex().forEach((indexedItem, indexedSlot) -> {
                    itemIndex.compute(indexedItem, (item, pos) -> {
                        Set<BlockPos> set = Objects.requireNonNullElseGet(pos, LinkedHashSet::new);
                        set.add(blockPos);
                        return set;
                    });
                });
            }
        }
    }

    public void addToIndex(Item item, BlockPos pos) {
        itemIndex.compute(item, (ignored, blockPos) -> {
            Set<BlockPos> set = Objects.requireNonNullElseGet(blockPos, LinkedHashSet::new);
            set.add(pos);
            return set;
        });
    }

    public void removeFromIndex(Item item, BlockPos pos) {
        itemIndex.computeIfPresent(item, (ignored, blocks) -> {
            blocks.remove(pos);
            // If final set is empty it should not be in the cache as the item is not in the storage
            return blocks.isEmpty() ? null : blocks;
        });
    }
}
