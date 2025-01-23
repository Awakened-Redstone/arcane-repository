package dev.enjarai.arcane_repository.registry;

import dev.enjarai.arcane_repository.ArcaneRepository;
import dev.enjarai.arcane_repository.component.OverstackingStorageComponent;
import dev.enjarai.arcane_repository.component.StorageFilterComponent;
import dev.enjarai.arcane_repository.component.MysticalBookComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<MysticalBookComponent> MYSTICAL_BOOK = register("mystical_book", builder -> builder.codec(MysticalBookComponent.CODEC));
    public static final ComponentType<NbtCompound> PAGE_ATTRIBUTES = register("page_attributes", builder -> builder.codec(NbtCompound.CODEC));
    public static final ComponentType<List<BlockPos>> LINKED_BLOCKS = register("linked_blocks", builder -> builder.codec(BlockPos.CODEC.listOf()));
    public static final ComponentType<StorageFilterComponent> STORAGE_FILTERS = register("storage_filters", builder -> builder.codec(StorageFilterComponent.CODEC));
    public static final ComponentType<OverstackingStorageComponent> OVERSTACKING_STORAGE = register("overstacking_storage", builder -> builder.codec(OverstackingStorageComponent.CODEC));
    public static final ComponentType<UUID> REPOSITORY = register("repository", builder -> builder.codec(Uuids.CODEC));

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, ArcaneRepository.id(id), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        ArcaneRepository.LOGGER.debug("Registering data components for " + ArcaneRepository.MOD_ID);
    }
}
