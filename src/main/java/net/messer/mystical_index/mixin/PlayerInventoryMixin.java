package net.messer.mystical_index.mixin;

import net.messer.mystical_index.item.custom.book.MysticalBookItem;
import net.messer.mystical_index.item.custom.page.attribute.PickupAttributePage;
import net.messer.mystical_index.item.custom.page.type.ItemStorageTypePage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void onItemPickup(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if(canInterceptPickup((PlayerInventory)(Object) this, stack)) cir.setReturnValue(true);
    }

    private static boolean canInterceptPickup(PlayerInventory inventory, ItemStack itemPickedUp){
        var foundBooks = inventory.main.stream()
                .filter(itemStack -> itemStack.getItem() instanceof MysticalBookItem)
                .toList();


        for(ItemStack bookStack: foundBooks){
            var bookitem = (MysticalBookItem) bookStack.getItem();
            if(bookitem.getTypePage(bookStack) instanceof ItemStorageTypePage storageTypePage){
                var foundPage = bookitem.getAttributePage(bookStack,"pickup");
                if(foundPage != null && bookitem.getAttributePage(bookStack, "pickup") instanceof PickupAttributePage pickupAttributePage){
                    var insertedAmount = storageTypePage.tryAddItem(bookStack, itemPickedUp);
                    if(insertedAmount > 0)
                    {
                        itemPickedUp.decrement(insertedAmount);
                        return itemPickedUp.getCount() <= 0;
                    }
                }
            }
        }

        return false;
    }
}
