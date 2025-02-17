package me.EtienneDx.RealEstate.ClaimAPI.GriefDefender;

import java.util.UUID;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.ClaimResult;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.IClaimAPI;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;

/**
 * GriefDefenderAPI is an implementation of the {@link IClaimAPI} interface that integrates with the GriefDefender plugin.
 * <p>
 * It provides methods to retrieve claims, save claims, access player data, change claim ownership, and register
 * event listeners for claim-related events.
 * </p>
 */
public class GriefDefenderAPI implements IClaimAPI {

    /**
     * Default constructor for GriefDefenderAPI.
     */
    public GriefDefenderAPI() {
        // Default constructor.
    }

    /**
     * Retrieves the claim at the specified Bukkit location.
     *
     * @param location the Bukkit location where the claim is being checked
     * @return an IClaim instance representing the claim at the given location
     */
    @Override
    public IClaim getClaimAt(Location location) {
        return new GDClaim(GriefDefender.getCore().getClaimAt(location));
    }

    /**
     * Saves the specified claim.
     * <p>
     * Note: GriefDefender automatically saves claims, so no additional action is required.
     * </p>
     *
     * @param claim the claim to save
     */
    @Override
    public void saveClaim(IClaim claim) {
        // GriefDefender auto-saves claims; no additional implementation needed.
    }

    /**
     * Retrieves the player data for the specified player UUID.
     *
     * @param player the UUID of the player whose claim data is to be retrieved
     * @return an IPlayerData instance representing the player's claim data
     */
    @Override
    public IPlayerData getPlayerData(UUID player) {
        return new GDPlayerData(GriefDefender.getCore().getPlayerData(Bukkit.getPlayer(player).getWorld().getUID(), player));
    }

    /**
     * Changes the owner of the specified claim to a new owner.
     *
     * @param claim    the claim whose ownership is to be changed
     * @param newOwner the UUID of the new owner
     * @throws RuntimeException if the transfer of ownership is unsuccessful
     */
    @Override
    public void changeClaimOwner(IClaim claim, UUID newOwner) {
        if (claim instanceof GDClaim) {
            ClaimResult res = ((GDClaim) claim).getClaim().transferOwner(newOwner);
            if (!res.successful()) {
                throw new RuntimeException(res.getResultType().toString());
            }
        }
    }

    /**
     * Registers event listeners for GriefDefender integration.
     * <p>
     * This method initializes the GriefDefender permission listener.
     * </p>
     */
    @Override
    public void registerEvents() {
        new GDPermissionListener();
    }
}
