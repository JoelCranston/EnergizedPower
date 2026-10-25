package me.jddev0.ep.energy;

/**
 * Used for EnergySyncS2CPacket
 *
 * On the client the values are only synced while a menu of the block entity is opened (See MenuStorageSyncTracker)
 */
public interface EnergyStoragePacketUpdate {
    int getEnergy();
    int getCapacity();

    void setEnergy(int energy);
    void setCapacity(int capacity);
}
