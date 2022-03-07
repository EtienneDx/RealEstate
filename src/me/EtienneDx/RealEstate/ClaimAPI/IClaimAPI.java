package me.EtienneDx.RealEstate.ClaimAPI;

import java.util.UUID;

import org.bukkit.Location;

public interface IClaimAPI
{
    public IClaim getClaimAt(Location location);

    public void saveClaim(IClaim claim);

    public IPlayerData getPlayerData(UUID player);

    public void changeClaimOwner(IClaim claim, UUID newOwner);

    public void registerEvents();
}
