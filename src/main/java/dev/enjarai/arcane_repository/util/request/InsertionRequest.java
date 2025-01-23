package dev.enjarai.arcane_repository.util.request;

import dev.enjarai.arcane_repository.extension.IndexesBooks;
import dev.enjarai.arcane_repository.registry.item.MysticalBookItem;
import dev.enjarai.arcane_repository.registry.item.page.type.storage.ItemStorageTypePage;
import dev.enjarai.arcane_repository.repository.request.Request;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InsertionRequest extends Request {
    private final ItemStack itemStack;
    // TODO priority?

    public InsertionRequest(ItemStack itemStack) {
        super(itemStack.getCount());
        this.itemStack = itemStack;
    }

    @Override
    public void apply(LibraryIndex index) {
        var sources = new ArrayList<>(index.arcane_repository$getSources());

        // Sort sources by priority.
        sources.sort(Comparator.comparingInt((source) -> {
            var book = source.getBook();
            if (book.getItem() instanceof MysticalBookItem bookItem) {
                if (bookItem.getTypePage(book).orElse(null) instanceof ItemStorageTypePage page) {
                    return page.getInsertPriority(book, getItemStack());
                }
            }
            return 0;
        }));
        Collections.reverse(sources);

        // Insert into sources in order of priority.
        for (IndexSource source : sources) {
            if (isSatisfied()) break;

            var book = source.getBook();
            if (book.getItem() instanceof MysticalBookItem bookItem) {
                if (bookItem.getTypePage(book).orElse(null) instanceof ItemStorageTypePage page) {
                    ItemStack stackCopy = getItemStack().copy();
                    int amountInserted = page.tryAddItem(book, getItemStack());
                    satisfy(amountInserted);

                    if (amountInserted > 0 && source.blockEntity() instanceof IndexesBooks bookIndexer) {
                        int bookIndex = bookIndexer.arcane_repository$findIndex(book);
                        if (bookIndex > -1) {
                            bookIndexer.arcane_repository$addToIndex(stackCopy.getItem(), bookIndex);
                        }
                    }

                    source.onInteractionComplete();
                    runBlockAffectedCallback(source.blockEntity());
                }
            }
        }
    }

    @Override
    public void satisfy(int amount) {
        itemStack.decrement(amount);
        super.satisfy(amount);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
