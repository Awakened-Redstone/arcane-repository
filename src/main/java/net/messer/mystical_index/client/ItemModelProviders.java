package net.messer.mystical_index.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.mixin.object.builder.client.ModelPredicateProviderRegistryAccessor;
import net.messer.mystical_index.item.ModItems;
import net.messer.mystical_index.item.custom.book.MysticalBookItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.messer.mystical_index.item.ModItems.MYSTICAL_BOOK;

@Environment(value = EnvType.CLIENT)
public class ItemModelProviders {
    public static void register() {
        registerBookModel(Registry.ITEM.getId(ModItems.ITEM_STORAGE_TYPE_PAGE), "item_storage_type");
        registerBookModel(Registry.ITEM.getId(ModItems.INDEXING_TYPE_PAGE), "indexing_type");
        registerBookModel(Registry.ITEM.getId(ModItems.FOOD_STORAGE_TYPE_PAGE), "food_storage_type");
        registerBookModel(Registry.ITEM.getId(ModItems.BLOCK_STORAGE_TYPE_PAGE), "block_storage_type");
        registerBookModel(Registry.ITEM.getId(ModItems.INDEX_SLAVE_TYPE_PAGE), "index_slave_type");

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : MYSTICAL_BOOK.getColor(stack), MYSTICAL_BOOK);
    }

    private static void registerBookModel(Identifier id, String name) {
        var checkId = id.toString();
        ModelPredicateProviderRegistryAccessor.callRegister(new Identifier(name), (itemStack, clientWorld, livingEntity, i) -> {
            if (itemStack.getItem() == MYSTICAL_BOOK) {
                if (itemStack.getOrCreateNbt().getString(MysticalBookItem.TYPE_PAGE_TAG).equals(checkId)) {
                    return 1;
                }
            }
            return 0;
        });
    }
}
