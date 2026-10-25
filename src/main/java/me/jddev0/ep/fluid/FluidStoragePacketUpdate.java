package me.jddev0.ep.fluid;

import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Used for FluidSyncS2CPacket
 *
 * On the client the values are only synced while a menu of the block entity is opened (See MenuStorageSyncTracker)
 * Exception: Fluid tanks are also synced to players nearby, because the fluid is rendered in the world
 */
public interface FluidStoragePacketUpdate {
    int getTankCount();
    FluidStack getFluid(int tank);
    int getTankCapacity(int tank);

    void setFluid(int tank, FluidStack fluidStack);
    void setTankCapacity(int tank, int capacity);
}
