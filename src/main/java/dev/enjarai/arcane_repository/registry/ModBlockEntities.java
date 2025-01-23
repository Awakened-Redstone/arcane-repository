package dev.enjarai.arcane_repository.registry;

import dev.enjarai.arcane_repository.registry.block.entity.MysticalLecternBlockEntity;
import dev.enjarai.arcane_repository.ArcaneRepository;
import io.wispforest.owo.registration.reflect.BlockEntityRegistryContainer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities implements BlockEntityRegistryContainer {
    public static final BlockEntityType<MysticalLecternBlockEntity> MYSTICAL_LECTERN = BlockEntityType.Builder.create(MysticalLecternBlockEntity::new, ModBlocks.MYSTICAL_LECTERN).build(null);
}
