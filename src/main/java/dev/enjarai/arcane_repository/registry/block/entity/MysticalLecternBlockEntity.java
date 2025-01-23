package dev.enjarai.arcane_repository.registry.block.entity;

import dev.enjarai.arcane_repository.registry.item.MysticalBookItem;
import dev.enjarai.arcane_repository.registry.ModBlockEntities;
import dev.enjarai.arcane_repository.util.LecternTracker;
import dev.enjarai.arcane_repository.util.MathUtil;
import dev.enjarai.arcane_repository.util.state.PageLecternState;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.block.LecternBlock.HAS_BOOK;

public class MysticalLecternBlockEntity extends LecternBlockEntity { // TODO separate IndexingBlockEntity
    public static final double LECTERN_DETECTION_RADIUS = 2d;

    public DefaultedList<ItemStack> items = DefaultedList.of();
    public List<BlockPos> linkedBlocks = new ArrayList<>();
    public int tick = 0;
    public float bookRotation = 0;
    public float bookRotationTarget = 0;
    public PageLecternState typeState;
    public PageLecternState actionState;

    public MysticalLecternBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.items, registryLookup);
        nbt.put("linkedBlocks", BlockPos.CODEC.listOf().encodeStart(registryLookup.getOps(NbtOps.INSTANCE), linkedBlocks).getOrThrow());
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.items, registryLookup);
        if (nbt.contains("linkedBlocks")) {
            linkedBlocks = new ArrayList<>(BlockPos.CODEC.listOf().parse(registryLookup.getOps(NbtOps.INSTANCE), nbt.get("linkedBlocks")).getOrThrow());
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    private void initState() {
        var bookItem = ((MysticalBookItem) getBook().getItem());
        var typePage = bookItem.getTypePage(getBook());
        var actionPage = bookItem.getActionPage(getBook());

        typePage.ifPresent(p -> typeState = p.lectern$getState(this));
        actionPage.ifPresent(p -> actionState = p.lectern$getState(this));

        bookItem.lectern$afterPlaced(this);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, MysticalLecternBlockEntity lectern) {
        if (!world.isClient()) {
            if (state.get(HAS_BOOK)) {
                if (lectern.typeState == null) {
                    lectern.initState();
                }
            }
        } else {
            if (state.get(HAS_BOOK)) {
                var closestPlayer = world.getClosestPlayer(
                  pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                  LECTERN_DETECTION_RADIUS, false
                );

                float rotationDelta = (lectern.bookRotationTarget - lectern.bookRotation) * 0.1f;
                lectern.bookRotation = lectern.bookRotation + rotationDelta;

                if (closestPlayer == null) {
                    lectern.bookRotationTarget = 0;
                } else {
                    double xOffset = closestPlayer.getX() - ((double) pos.getX() + 0.5);
                    double zOffset = closestPlayer.getZ() - ((double) pos.getZ() + 0.5);
                    float rotationTarget = (float) Math.atan2(zOffset, xOffset);
                    float rotationOffset = (float) Math.toRadians(state.get(LecternBlock.FACING).rotateYClockwise().asRotation());
                    lectern.bookRotationTarget = MathHelper.clamp(MathUtil.fixRadians(rotationTarget - rotationOffset), -0.4f, 0.4f);
                }
            }
        }

        lectern.tick++;

        if (state.get(HAS_BOOK)) {
            if (lectern.getBook().getItem() instanceof MysticalBookItem book) {
                book.lectern$serverTick(world, pos, state, lectern);
            }

            LecternTracker.addIndexLectern(lectern);
        } else {
            LecternTracker.removeIndexLectern(lectern);
        }
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.MYSTICAL_LECTERN;
    }
}
