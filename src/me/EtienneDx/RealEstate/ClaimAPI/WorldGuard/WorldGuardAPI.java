package me.EtienneDx.RealEstate.ClaimAPI.WorldGuard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.Location;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.IClaimAPI;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;

import java.util.UUID;

/**
 * WorldGuardAPI provides an implementation of the IClaimAPI interface using WorldGuard.
 * <p>
 * This class uses WorldGuard's platform API to query and wrap claims in the WGClaim class.
 * Note that some operations (such as changing a claim's owner) are not supported.
 * </p>
 */
public class WorldGuardAPI implements IClaimAPI {

    private final RegionContainer container;

    /**
     * Constructs a new WorldGuardAPI instance.
     * <p>
     * It obtains the RegionContainer via the WorldGuard platform API.
     * </p>
     */
    public WorldGuardAPI() {
        // Obtain the RegionContainer via the WorldGuard platform API.
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    /**
     * Retrieves a claim at the specified Bukkit location.
     * <p>
     * The method converts the given Bukkit location into a WorldEdit location and queries for applicable
     * WorldGuard regions. If one or more regions are found, the first region is wrapped in a WGClaim instance.
     * </p>
     *
     * @param bukkitLocation the Bukkit location to check for a claim
     * @return an IClaim instance representing the claim at the location, or {@code null} if no claim exists
     */
    @Override
    public IClaim getClaimAt(Location bukkitLocation) {
        // Convert the Bukkit location to a WorldEdit location using BukkitAdapter.
        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(bukkitLocation);
        RegionQuery query = container.createQuery();
        // Query applicable regions at this location.
        ApplicableRegionSet regions = query.getApplicableRegions(weLocation);
        if (regions.size() > 0) {
            // For this example, we take the first region.
            ProtectedRegion region = regions.iterator().next();
            // Wrap the region in our WGClaim implementation.
            return new WGClaim(region, bukkitLocation.getWorld());
        }
        return null;
    }

    /**
     * Saves the specified claim.
     * <p>
     * For WorldGuard, claims are automatically saved; thus, no action is required.
     * </p>
     *
     * @param claim the claim to be saved
     */
    @Override
    public void saveClaim(IClaim claim) {
        // WorldGuard auto-saves; no action needed.
    }

    /**
     * Retrieves player-specific claim data.
     * <p>
     * Not supported in this implementation.
     * </p>
     *
     * @param player the UUID of the player
     * @return {@code null} as player data is not implemented
     */
    @Override
    public IPlayerData getPlayerData(UUID player) {
        // Not supported in this implementation; return null or a dummy implementation.
        return null;
    }

    /**
     * Changes the owner of the specified claim.
     * <p>
     * Changing a claim's owner is not supported by WorldGuard. This method always throws an exception.
     * </p>
     *
     * @param claim    the claim whose owner is to be changed
     * @param newOwner the UUID of the new owner
     * @throws UnsupportedOperationException always thrown as this operation is not supported
     */
    @Override
    public void changeClaimOwner(IClaim claim, UUID newOwner) {
        // Changing a claim's owner is not supported by WorldGuard.
        throw new UnsupportedOperationException("Changing claim owner is not supported with WorldGuard.");
    }

    /**
     * Registers any necessary event listeners for claim-related events.
     * <p>
     * No events are registered for WorldGuard integration in this implementation.
     * </p>
     */
    @Override
    public void registerEvents() {
        // No events to register for WorldGuard integration in this example.
    }
    
    // -- Added helper methods for granting/revoking player permissions --

    /**
     * Adds the given player's permission to the specified claim.
     * <p>
     * This method delegates to the WGClaim implementation's {@code addPlayerPermissions} method.
     * </p>
     *
     * @param claim      the claim (must be an instance of WGClaim)
     * @param player     the UUID of the player to grant permission
     * @param permission the permission type to add (e.g. BUILD, ACCESS, etc.)
     */
    public void addPlayerPermission(IClaim claim, UUID player, ClaimPermission permission) {
        if (claim instanceof WGClaim) {
            ((WGClaim) claim).addPlayerPermissions(player, permission);
        }
    }
    
    /**
     * Removes any permission for the given player from the specified claim.
     * <p>
     * This method delegates to the WGClaim implementation's {@code dropPlayerPermissions} method.
     * </p>
     *
     * @param claim  the claim (must be an instance of WGClaim)
     * @param player the UUID of the player whose permission should be removed
     */
    public void removePlayerPermission(IClaim claim, UUID player) {
        if (claim instanceof WGClaim) {
            ((WGClaim) claim).dropPlayerPermissions(player);
        }
    }
}
