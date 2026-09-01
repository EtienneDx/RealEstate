package me.EtienneDx.RealEstate.ClaimAPI;

import java.util.UUID;
import org.bukkit.Location;

import me.EtienneDx.RealEstate.Transactions.Transaction;

/**
 * The IClaimAPI interface defines the methods for interacting with claims.
 * Implementations of this interface provide functionality to:
 * <ul>
 *   <li>Retrieve a claim at a specific location.</li>
 *   <li>Save or update claim data.</li>
 *   <li>Access player-specific claim data.</li>
 *   <li>Change the ownership of a claim.</li>
 *   <li>Register claim-related events.</li>
 * </ul>
 */
public interface IClaimAPI {

    /**
     * Retrieves the claim at the specified Bukkit location.
     *
     * @param location the Bukkit location to check for a claim
     * @return an instance of IClaim representing the claim at the given location,
     *         or {@code null} if no claim exists at that location
     */
    public IClaim getClaimAt(Location location);

    /**
     * Saves the specified claim.
     * Implementations should persist any changes to the claim data.
     *
     * @param claim the claim to be saved
     */
    public void saveClaim(IClaim claim);

    /**
     * Retrieves the player data associated with the specified UUID.
     *
     * @param player the UUID of the player whose claim data is being requested
     * @return an instance of IPlayerData representing the player's claim data
     */
    public IPlayerData getPlayerData(UUID player);

    /**
     * Changes the owner of the specified claim to the new owner.
     *
     * @param claim    the claim whose ownership is to be transferred
     * @param newOwner the UUID of the new owner
     */
    public void changeClaimOwner(IClaim claim, UUID newOwner);

    /**
     * Registers any necessary event listeners for claim-related events.
     */
    public void registerEvents();

    /**
     * Retrieves the ongoing RealEstate transaction (sale, rent, lease, or auction)
     * associated with the claim that has the given provider-specific unique ID, if any.
     * <p>
     * This is a data-access hook, not a UI feature: it exists so that a claim provider's
     * own map integration (for example GriefDefender's built-in BlueMap/Dynmap support)
     * can query RealEstate for transaction details (price, buyer, rental period, etc.)
     * to display in its own claim popups. RealEstate does not render any map UI itself.
     * </p>
     * <p>
     * Not every claim provider is able to look up a claim purely by its unique ID
     * (some only support lookup by location); implementations that cannot should
     * simply return {@code null} rather than throwing.
     * </p>
     *
     * @param claimUniqueId the provider-specific unique ID of the claim to look up
     * @return the {@link Transaction} currently associated with the claim, or {@code null}
     *         if no such claim could be found, this provider cannot look up claims by ID,
     *         or no transaction is currently associated with the claim
     */
    public Transaction getTransaction(UUID claimUniqueId);
}
