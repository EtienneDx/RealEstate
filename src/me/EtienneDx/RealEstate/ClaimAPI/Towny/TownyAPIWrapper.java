package me.EtienneDx.RealEstate.ClaimAPI.Towny;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.IClaimAPI;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;
import org.bukkit.Location;
import java.util.UUID;

/**
 * TownyAPIWrapper provides a Towny-specific implementation of the IClaimAPI interface.
 * <p>
 * Since Towny does not use claims in the same manner as other claim management plugins,
 * this implementation wraps a Towny plot as an IClaim.
 * </p>
 */
public class TownyAPIWrapper implements IClaimAPI {

	
	/**
	 * Instantiates a new TownyAPIWrapper.
	 */
	public TownyAPIWrapper() {}
	
    /**
     * Retrieves the Towny plot (wrapped as an IClaim) at the given location.
     * <p>
     * Note: Towny does not have claims like GriefPrevention, so this method wraps
     * the Towny plot at the specified location.
     * </p>
     *
     * @param location the Bukkit location to check for a Towny plot
     * @return an instance of IClaim representing the Towny plot at the given location
     */
    @Override
    public IClaim getClaimAt(Location location) {
        return new TownyClaim(location);
    }

    /**
     * Saves the given claim.
     * <p>
     * Towny claims are managed internally by Towny; therefore, no saving is necessary.
     * </p>
     *
     * @param claim the claim to be saved
     */
    @Override
    public void saveClaim(IClaim claim) {
        // Towny claims are managed internally by Towny.
        // No saving is necessary.
    }

    /**
     * Retrieves the player data for the specified player UUID.
     * <p>
     * This method creates a TownyPlayerData wrapper for the given player's data.
     * </p>
     *
     * @param player the UUID of the player whose claim data is requested
     * @return an instance of IPlayerData representing the player's data
     */
    @Override
    public IPlayerData getPlayerData(UUID player) {
        return new TownyPlayerData(player);
    }

    /**
     * Changes the owner of the specified claim to the new owner.
     * <p>
     * Changing the owner of a Towny plot is not supported in the same way as with other claim
     * management systems. This method always throws an UnsupportedOperationException.
     * </p>
     *
     * @param claim    the claim whose ownership is to be transferred
     * @param newOwner the UUID of the new owner
     * @throws UnsupportedOperationException always, since changing owner is not supported
     */
    @Override
    public void changeClaimOwner(IClaim claim, UUID newOwner) {
        throw new UnsupportedOperationException("Changing claim owner is not supported for Towny claims.");
    }

    /**
     * Registers any necessary Towny-specific events.
     * <p>
     * This implementation is empty; if Towny-specific events need to be registered,
     * add the registration logic here.
     * </p>
     */
    @Override
    public void registerEvents() {
        // If you want to register Towny-specific events, do so here.
    }
    
    /**
     * Retrieves a Resident object corresponding to the given resident name.
     * <p>
     * If the resident is not registered within Towny, this method returns null.
     * </p>
     *
     * @param name the name of the resident
     * @return the Resident associated with the given name, or null if not found
     */
    public Resident getResident(String name) {
        return TownyUniverse.getInstance().getResident(name);
    }
}
