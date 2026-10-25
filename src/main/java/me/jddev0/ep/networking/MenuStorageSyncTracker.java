package me.jddev0.ep.networking;

import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Syncs the energy and fluid storage of a block entity to the player which has its menu opened.
 * Packets are only sent if a value changed since the last sync.
 * This replaces broadcasting sync packets to all players nearby on every storage change.
 */
public final class MenuStorageSyncTracker {
    private final BlockEntity blockEntity;
    private final ServerPlayer player;

    private int lastEnergy = -1;
    private int lastCapacity = -1;

    private FluidStack[] lastFluids = new FluidStack[0];
    private int[] lastTankCapacities = new int[0];

    private MenuStorageSyncTracker(BlockEntity blockEntity, ServerPlayer player) {
        this.blockEntity = blockEntity;
        this.player = player;
    }

    /**
     * The current values are assumed to be already synced to the player (createMenu() syncs them before the menu is opened)
     *
     * @return null if the player is not a server player (Client-side menu)
     */
    public static @Nullable MenuStorageSyncTracker create(BlockEntity blockEntity, Player player) {
        if(!(player instanceof ServerPlayer serverPlayer))
            return null;

        MenuStorageSyncTracker tracker = new MenuStorageSyncTracker(blockEntity, serverPlayer);
        tracker.sync(false);
        return tracker;
    }

    /**
     * Should be called in AbstractContainerMenu.broadcastChanges() (Once per tick while the menu is open)
     */
    public void sync() {
        sync(true);
    }

    private void sync(boolean sendPackets) {
        if(blockEntity instanceof EnergyStoragePacketUpdate energyStorage)
            syncEnergy(energyStorage, sendPackets);

        if(blockEntity instanceof FluidStoragePacketUpdate fluidStorage)
            syncFluids(fluidStorage, sendPackets);
    }

    private void syncEnergy(EnergyStoragePacketUpdate energyStorage, boolean sendPackets) {
        int energy = energyStorage.getEnergy();
        int capacity = energyStorage.getCapacity();
        if(energy == lastEnergy && capacity == lastCapacity)
            return;

        lastEnergy = energy;
        lastCapacity = capacity;

        if(sendPackets)
            ModMessages.sendToPlayer(new EnergySyncS2CPacket(energy, capacity, blockEntity.getBlockPos()), player);
    }

    private void syncFluids(FluidStoragePacketUpdate fluidStorage, boolean sendPackets) {
        int tankCount = fluidStorage.getTankCount();
        if(lastFluids.length != tankCount) {
            lastFluids = new FluidStack[tankCount];

            lastTankCapacities = new int[tankCount];
            Arrays.fill(lastTankCapacities, -1);
        }

        for(int i = 0;i < tankCount;i++) {
            FluidStack fluid = fluidStorage.getFluid(i);
            if(fluid == null)
                fluid = FluidStack.EMPTY;

            int capacity = fluidStorage.getTankCapacity(i);
            if(lastFluids[i] != null && FluidStack.isSameFluidSameComponents(fluid, lastFluids[i]) &&
                    fluid.getAmount() == lastFluids[i].getAmount() && capacity == lastTankCapacities[i])
                continue;

            lastFluids[i] = fluid.copy();
            lastTankCapacities[i] = capacity;

            if(sendPackets)
                ModMessages.sendToPlayer(new FluidSyncS2CPacket(i, fluid, capacity, blockEntity.getBlockPos()), player);
        }
    }
}
