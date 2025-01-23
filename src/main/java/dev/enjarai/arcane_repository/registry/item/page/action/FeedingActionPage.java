package dev.enjarai.arcane_repository.registry.item.page.action;

import dev.enjarai.arcane_repository.registry.ModItems;
import dev.enjarai.arcane_repository.registry.item.MysticalBookItem;
import dev.enjarai.arcane_repository.registry.item.page.ActionPageItem;
import dev.enjarai.arcane_repository.registry.item.page.TypePageItem;
import dev.enjarai.arcane_repository.registry.item.page.type.storage.FoodStorageTypePage;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FeedingActionPage extends ActionPageItem {
    public FeedingActionPage(String id) {
        super(id);
    }

    @Override
    public int getColor() {
        return 0xff55dd;
    }

    @Override
    public MutableText getActionDisplayName() {
        return super.getActionDisplayName().formatted(Formatting.LIGHT_PURPLE);
    }

    @Override
    public List<TypePageItem> getCompatibleTypes(ItemStack page) {
        return List.of(ModItems.FOOD_STORAGE_TYPE_PAGE);
    }

    @Override
    public TypedActionResult<ItemStack> book$use(World world, PlayerEntity user, Hand hand) {
        var book = user.getStackInHand(hand);
        var usedBook = (MysticalBookItem) book.getItem();

        if (usedBook.getTypePage(book).orElse(null) instanceof FoodStorageTypePage foodPage) {
            ItemStack foodItem;
            try {
                foodItem = foodPage.getContents(book).getAll().get(0).getItemStack();
            } catch (IndexOutOfBoundsException e) {
                return TypedActionResult.fail(book);
            }

            var foodComponent = foodItem.get(DataComponentTypes.FOOD);
            if (foodComponent != null && user.canConsume(foodComponent.canAlwaysEat())){
                user.setCurrentHand(hand);
                return TypedActionResult.consume(book);
            }
            return TypedActionResult.fail(book);
        }

        return TypedActionResult.pass(book);
    }

    @Nullable
    @Override
    public ItemStack book$finishUsing(ItemStack book, World world, LivingEntity user) {
        var usedBook = (MysticalBookItem) book.getItem();

        if (usedBook.getTypePage(book).orElse(null) instanceof FoodStorageTypePage foodPage) {
            var food = foodPage.removeFirstStack(book, 1);
            if (food.isPresent()) {
                ItemStack stack = food.get();
                user.eatFood(world, stack, stack.get(DataComponentTypes.FOOD));
                return null;
            }
        }
        return super.book$finishUsing(book, world, user);
    }

    @Override
    public UseAction book$getUseAction(ItemStack book) {
        return UseAction.EAT;
    }

    @Override
    public int book$getMaxUseTime(ItemStack book) {
        return 32;
    }
}
