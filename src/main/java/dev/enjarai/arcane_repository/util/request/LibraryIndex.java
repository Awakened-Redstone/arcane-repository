package dev.enjarai.arcane_repository.util.request;

import com.google.common.collect.ImmutableSet;
import dev.enjarai.arcane_repository.extension.IndexesBooks;
import dev.enjarai.arcane_repository.registry.ModTags;
import dev.enjarai.arcane_repository.util.WorldEffects;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class LibraryIndex implements IndexInteractable, IndexesBooks {
    public static final LibraryIndex EMPTY = new LibraryIndex();

    private final Set<IndexInteractable> interactables;
    private Map<Item, Set<Byte>> index = new HashMap<>();

    public LibraryIndex() {
        this.interactables = new HashSet<>();
    }

    public boolean isEmpty() {
        return interactables.isEmpty();
    }

    public static LibraryIndex fromRange(World world, BlockPos pos, int searchRange) {
        return fromRange(world, pos, searchRange, true);
    }

    public static LibraryIndex fromRange(World world, BlockPos pos, int searchRange, boolean particles) {
        // Iterate over nearby blocks and generate the index
        var result = new LibraryIndex();
        for (int x = -searchRange; x <= searchRange; x++) {
            for (int z = -searchRange; z <= searchRange; z++) {
                for (int y = -searchRange; y <= searchRange; y++) {
                    BlockPos testPos = pos.add(x, y, z);

                    if (world.getBlockState(testPos).isIn(ModTags.INDEX_INTRACTABLE) && world.getBlockEntity(testPos) instanceof IndexInteractable entity) {
                        result.add(entity, particles ? WorldEffects::registrationParticles : i -> {});
                    }
                }
            }
        }

        return result;
    }

    public void add(IndexInteractable interactable) {
        add(interactable, i -> {});
    }

    public void add(IndexInteractable interactable, Consumer<IndexInteractable> callback) {
        interactables.add(interactable);
        callback.accept(interactable);
    }

    public boolean remove(IndexInteractable interactable) {
        return interactables.remove(interactable);
    }

    public void merge(LibraryIndex other) {
        interactables.addAll(other.interactables);
    }

    @Override
    public Set<IndexSource> arcane_repository$getSources() {
        ImmutableSet.Builder<IndexSource> builder = ImmutableSet.builder();

        for (IndexInteractable entity : interactables) {
            builder.addAll(entity.arcane_repository$getSources());
        }

        return builder.build();
    }
}
