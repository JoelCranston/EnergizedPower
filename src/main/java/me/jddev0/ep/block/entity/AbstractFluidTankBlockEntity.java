package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFluidTankBlockEntity<F extends IEnergizedPowerFluidStorage>
        extends MenuFluidStorageBlockEntity<F> {
    private boolean fluidSyncPending;

    public AbstractFluidTankBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName,
                                        int baseTankCapacity) {
        super(type, blockPos, blockState, machineName, baseTankCapacity);
    }

    public static <F extends IEnergizedPowerFluidStorage> void tick(Level level, BlockPos blockPos, BlockState state,
                                                                    AbstractFluidTankBlockEntity<F> blockEntity) {
        if(level.isClientSide())
            return;

        //Sync fluid to client at most once per tick if it changed and every 5 seconds (Offset by position to spread the load across ticks)
        if(blockEntity.fluidSyncPending || (level.getGameTime() + Math.floorMod(blockPos.asLong(), 100)) % 100 == 0) {
            blockEntity.fluidSyncPending = false;

            blockEntity.syncFluidToPlayers(64);
        }
    }

    /**
     * The fluid will be synced to players nearby in the next tick (The renderer requires the fluid on the client)
     */
    protected final void markFluidSyncPending() {
        fluidSyncPending = true;
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
    }

    public @Nullable ResourceHandler<FluidResource> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }
}