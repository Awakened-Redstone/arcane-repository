package dev.enjarai.arcane_repository.registry.item.page.attribute;

import dev.enjarai.arcane_repository.registry.block.entity.MysticalLecternBlockEntity;
import dev.enjarai.arcane_repository.extension.RepositoryDrop;
import dev.enjarai.arcane_repository.registry.item.MysticalBookItem;
import dev.enjarai.arcane_repository.registry.item.page.AttributePageItem;
import dev.enjarai.arcane_repository.registry.item.page.TypePageItem;
import dev.enjarai.arcane_repository.registry.item.page.type.ItemInsertableTypePage;
import dev.enjarai.arcane_repository.util.WorldEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.List;

import static dev.enjarai.arcane_repository.registry.block.MysticalLecternBlock.LECTERN_INPUT_AREA_SHAPE;
import static dev.enjarai.arcane_repository.registry.ModItems.*;

public class PickupAttributePage extends AttributePageItem {
    public PickupAttributePage(String id) {
        super(id);
    }

    @Override
    public void appendAttributes(ItemStack page, NbtCompound nbt) {
    }

    @Override
    public int getColor() {
        return 0x00884c;
    }

    @Override
    public List<TypePageItem> getCompatibleTypes(ItemStack page) {
        return List.of(ITEM_STORAGE_TYPE_PAGE, FOOD_STORAGE_TYPE_PAGE, BLOCK_STORAGE_TYPE_PAGE, INDEXING_TYPE_PAGE, INDEX_SLAVE_TYPE_PAGE);
    }

    @Override
    public boolean bookCanHaveMultiple(ItemStack page) {
        return false;
    }

    @Override
    public void lectern$onEntityCollision(MysticalLecternBlockEntity lectern, BlockState state, World world, BlockPos pos, Entity entity) {
        var book = lectern.getBook();
        if (
          entity instanceof ItemEntity itemEntity &&
          book.getItem() instanceof MysticalBookItem bookItem &&
          bookItem.getTypePage(book).orElse(null) instanceof ItemInsertableTypePage itemTypePage &&
          !RepositoryDrop.cast(itemEntity).arcane_repository$isRepositoryDrop() &&
          VoxelShapes.matchesAnywhere(
                VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())),
                LECTERN_INPUT_AREA_SHAPE, BooleanBiFunction.AND)
        ) {
            var itemStack = itemEntity.getStack();
            var affected = itemTypePage.lectern$tryInsertItemStack(lectern, itemStack);
            if (affected > 0) WorldEffects.lecternPlonk(world, entity.getPos(), 0.6f, true);
        }
    }
}
