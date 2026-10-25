package me.jddev0.ep.fluid;

import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Used for FluidSyncS2CPacket
 */
public interface FluidStoragePacketUpdate {
    int getTankCount();
    FluidStack getFluid(int tank);
    int getTankCapacity(int tank);

    void setFluid(int tank, FluidStack fluidStack);
    void setTankCapacity(int tank, int capacity);
}
