package me.EtienneDx.RealEstate.ClaimAPI.WorldGuard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.Location;
import org.bukkit.World;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.IClaimAPI;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;

public class WorldGuardAPI implements IClaimAPI {

    private final RegionContainer container;

    public WorldGuardAPI() {
        // Obtain the RegionContainer via the WorldGuard platform API.
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    @Override
    public IClaim getClaimAt(Location bukkitLocation) {
        // Convert the Bukkit location to a WorldEdit location using BukkitAdapter.
        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(bukkitLocation);
        RegionQuery query = container.createQuery();
        // Use size() instead of isEmpty() for ApplicableRegionSet
        ApplicableRegionSet regions = query.getApplicableRegions(weLocation);
        if (regions.size() > 0) {
            // For this example, we take the first region.
            ProtectedRegion region = regions.iterator().next();
            // Wrap the region in our WGClaim implementation.
            return new WGClaim(region, bukkitLocation.getWorld());
        }
        return null;
    }

    @Override
    public void saveClaim(IClaim claim) {
        // WorldGuard auto-saves; no action needed.
    }

    @Override
    public IPlayerData getPlayerData(java.util.UUID player) {
        // Not supported in this implementation; return null or a dummy implementation.
        return null;
    }

    @Override
    public void changeClaimOwner(IClaim claim, java.util.UUID newOwner) {
        // Changing a claim's owner is not supported by WorldGuard.
        throw new UnsupportedOperationException("Changing claim owner is not supported with WorldGuard.");
    }

    @Override
    public void registerEvents() {
        // No events to register for WorldGuard integration in this example.
    }
}
