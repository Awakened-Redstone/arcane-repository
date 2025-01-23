package dev.enjarai.arcane_repository.util;

import net.minecraft.util.math.BlockPos;

public interface ModifiedChiseledBookshelfBlockEntity {
    float arcane_repository$getElapsed();
    void arcane_repository$setElapsed(float elapsed);
    int arcane_repository$getLastSlot();
    void arcane_repository$setLastSlot(int lastSlot);
    BlockPos arcane_repository$getLastHitPos();
    void arcane_repository$setLastHitPos(BlockPos lastHitPos);
}
