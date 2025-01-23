package dev.enjarai.arcane_repository.registry;

import dev.enjarai.arcane_repository.ArcaneRepository;
import dev.enjarai.arcane_repository.mixin.accessor.LootTablesAccessor;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModLootTables {
    public static final RegistryKey<LootTable> DROP_ENDER_PEARL_SHARDS = register("drop_ender_pearl_shards");

    public static void registerLootTables() {
        ArcaneRepository.LOGGER.debug("Registering loot tables for " + ArcaneRepository.MOD_ID);
    }

    private static RegistryKey<LootTable> register(String id) {
        return registerLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, ArcaneRepository.id(id)));
    }

    private static RegistryKey<LootTable> registerLootTable(RegistryKey<LootTable> key) {
        if (LootTablesAccessor.getLootTableRegistry().add(key)) {
            return key;
        } else {
            throw new IllegalArgumentException(key.getValue() + " is already a registered built-in loot table");
        }
    }
}
