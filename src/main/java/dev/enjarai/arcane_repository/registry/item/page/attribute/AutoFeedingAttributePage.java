package dev.enjarai.arcane_repository.registry.item.page.attribute;

import dev.enjarai.arcane_repository.registry.item.MysticalBookItem;
import dev.enjarai.arcane_repository.registry.item.page.AttributePageItem;
import dev.enjarai.arcane_repository.registry.item.page.TypePageItem;
import dev.enjarai.arcane_repository.registry.item.page.type.storage.FoodStorageTypePage;
import dev.enjarai.arcane_repository.registry.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.List;

public class AutoFeedingAttributePage extends AttributePageItem {
    public AutoFeedingAttributePage(String id) {
        super(id);
    }

    @Override
    public void appendAttributes(ItemStack page, NbtCompound nbt) {
    }

    @Override
    public int getColor() {
        return 0x550033;
    }

    @Override
    public List<TypePageItem> getCompatibleTypes(ItemStack page) {
        return List.of(ModItems.FOOD_STORAGE_TYPE_PAGE);
    }

    @Override
    public boolean bookCanHaveMultiple(ItemStack page) {
        return false;
    }

    @Override
    public void book$inventoryTick(ItemStack book, World world, Entity entity, int slot, boolean selected) {
        super.book$inventoryTick(book, world, entity, slot, selected);

        if (entity instanceof PlayerEntity player && !player.isCreative() && player.canConsume(false)){
            var usedBook = (MysticalBookItem) book.getItem();

            if (usedBook.getTypePage(book).orElse(null) instanceof FoodStorageTypePage foodPage) {
                var stack = foodPage.tryRemoveFirstStack(book, 1, itemStack -> {
                    var foodComponent = itemStack.getComponents().get(DataComponentTypes.FOOD);
                    if (foodComponent == null) {
                        return false;
                    }

                    return player.canConsume(foodComponent.canAlwaysEat())
                            && 20 - player.getHungerManager().getFoodLevel() >= Math.min(6, foodComponent.nutrition());
                });
                stack.ifPresent(itemStack -> player.eatFood(world, itemStack, itemStack.get(DataComponentTypes.FOOD)));
            }
        }
    }
}
