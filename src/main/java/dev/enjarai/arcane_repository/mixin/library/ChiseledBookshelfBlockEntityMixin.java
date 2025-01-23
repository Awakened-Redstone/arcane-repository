package dev.enjarai.arcane_repository.mixin.library;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import dev.enjarai.arcane_repository.extension.IndexesBooks;
import dev.enjarai.arcane_repository.registry.item.MysticalBookItem;
import dev.enjarai.arcane_repository.registry.item.page.type.storage.ItemStorageTypePage;
import dev.enjarai.arcane_repository.util.BigStack;
import dev.enjarai.arcane_repository.util.FallbackUtils;
import dev.enjarai.arcane_repository.util.MathUtil;
import dev.enjarai.arcane_repository.util.ModifiedChiseledBookshelfBlockEntity;
import dev.enjarai.arcane_repository.util.codec.CodecUtils;
import dev.enjarai.arcane_repository.util.request.IndexInteractable;
import dev.enjarai.arcane_repository.util.request.IndexSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class ChiseledBookshelfBlockEntityMixin extends BlockEntity implements IndexInteractable, ModifiedChiseledBookshelfBlockEntity {
    public ChiseledBookshelfBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow public abstract int size();
    @Shadow public abstract ItemStack getStack(int slot);

    @Unique float elapsed = 0f;
    @Unique int lastSlot = -1;
    @Unique BlockPos lastHitPos = BlockPos.ORIGIN;

    @Inject(method = {"setStack", "removeStack*", "clear"}, at = @At("TAIL"))
    private void rebuildIndexOnInventoryChange(CallbackInfo ci) {
        arcane_repository$index();
    }

    @Override
    public Set<IndexSource> arcane_repository$getSources() {
        ImmutableSet.Builder<IndexSource> builder = ImmutableSet.builder();

        for (int i = 0; i < size(); i++) {
            var book = getStack(i);
            if (book.getItem() instanceof MysticalBookItem mysticalBookItem) {
                if (mysticalBookItem.getTypePage(book).orElse(null) instanceof ItemStorageTypePage) {
                    builder.add(new IndexSource(book, this));
                }
            }
        }

        return builder.build();
    }

    @Override
    public void arcane_repository$onInteractionComplete() {
        // Send new state to clients if applicable
        if (getWorld() != null) {
            getWorld().updateListeners(
              getPos(), getCachedState(),
              getCachedState(), Block.NOTIFY_LISTENERS
            );
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        //TODO: Not send the entire NBT to the clients, they don't need the index
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public float arcane_repository$getElapsed() {
        return elapsed;
    }

    @Override
    public void arcane_repository$setElapsed(float elapsed) {
        this.elapsed = elapsed;
    }

    @Override
    public int arcane_repository$getLastSlot() {
        return lastSlot;
    }

    @Override
    public void arcane_repository$setLastSlot(int lastSlot) {
        this.lastSlot = lastSlot;
    }

    @Override
    public BlockPos arcane_repository$getLastHitPos() {
        return lastHitPos;
    }

    @Override
    public void arcane_repository$setLastHitPos(BlockPos lastHitPos) {
        this.lastHitPos = lastHitPos;
    }

    @Override
    public Map<Item, Set<Byte>> arcane_repository$getIndex() {
        return index;
    }

    @Override
    public void arcane_repository$index() {
        index.clear();
        for (byte i = 0; i < size(); i++) {
            var book = getStack(i);
            if (book.getItem() instanceof MysticalBookItem mysticalBookItem) {
                if (mysticalBookItem.getTypePage(book).orElse(null) instanceof ItemStorageTypePage page) {
                    for (BigStack bigStack : page.getContents(book)) {
                        final byte index = i;
                        this.index.compute(bigStack.getItem(), (item, bytes) -> {
                            Set<Byte> set = Objects.requireNonNullElseGet(bytes, LinkedHashSet::new);
                            set.add(index);
                            return set;
                        });
                    }
                }
            }
        }
    }

    @Override
    public void arcane_repository$addToIndex(@NotNull Item item, @Range(from = 0, to = 5) int index) {
        this.index.compute(item, (ignored, bytes) -> {
            Set<Byte> set = Objects.requireNonNullElseGet(bytes, LinkedHashSet::new);
            set.add(MathUtil.toByte(index));
            return set;
        });
    }

    @Override
    public void arcane_repository$removeFromIndex(@NotNull Item item, @Range(from = 0, to = 5) int index) {
        this.index.computeIfPresent(item, (ignored, slots) -> {
            slots.remove(MathUtil.toByte(index));
            // If final set is empty it should not be in the cache as the item is not in the storage
            return slots.isEmpty() ? null : slots;
        });
    }

    @Override
    public int arcane_repository$findIndex(@NotNull ItemStack stack) {
        for (byte i = 0; i < size(); i++) {
            if (ItemStack.areEqual(getStack(i), stack)) return i;
        }

        return -1;
    }
}
