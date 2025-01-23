package dev.enjarai.arcane_repository.registry;

import dev.enjarai.arcane_repository.registry.block.MysticalLecternBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import dev.enjarai.arcane_repository.ArcaneRepository;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block MYSTICAL_LECTERN = registerBlock("mystical_lectern",
            new MysticalLecternBlock(FabricBlockSettings.create().strength(1.5f)), false);


    private static Block registerBlock(String name, Block block, boolean hasItem) {
        if (hasItem) {
            registerBlockItem(name, block);
        }
        return Registry.register(Registries.BLOCK, Identifier.of(ArcaneRepository.MOD_ID, name), block);
    }

    private static BlockItem registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(ArcaneRepository.MOD_ID, name),
                new BlockItem(block, new Item.Settings().maxCount(64)));
    }

    public static void registerModBlocks(){
        ArcaneRepository.LOGGER.info("Registering blocks for " + ArcaneRepository.MOD_ID);
    }
}
