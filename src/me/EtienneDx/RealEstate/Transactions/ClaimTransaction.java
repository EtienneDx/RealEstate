package me.EtienneDx.RealEstate.Transactions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;

/**
 * Represents a generic claim transaction.
 * <p>
 * This abstract class provides the base for all claim-related transactions
 * (e.g. selling, renting, leasing). It handles common data such as the claim ID,
 * owner, price, and sign location, and implements serialization for storage.
 * </p>
 */
public abstract class ClaimTransaction implements ConfigurationSerializable, Transaction {

    /**
     * The unique ID of the claim.
     */
    public String claimId;

    /**
     * The UUID of the claim owner. If null, the claim is considered an admin claim.
     */
    public UUID owner = null;

    /**
     * The transaction price.
     */
    public double price;

    /**
     * The location of the sign representing this transaction.
     */
    public Location sign = null;
    
    /**
     * Constructs a new ClaimTransaction.
     *
     * @param claim the claim involved in the transaction
     * @param player the player initiating the transaction; may be null for admin claims
     * @param price the transaction price
     * @param sign the location of the sign associated with the transaction
     */
    public ClaimTransaction(IClaim claim, Player player, double price, Location sign) {
        this.claimId = claim.getId();
        this.owner = player != null ? player.getUniqueId() : null;
        this.price = price;
        this.sign = sign;
    }
    
    /**
     * Constructs a ClaimTransaction from a serialized map.
     *
     * @param map the map containing serialized transaction data
     */
    public ClaimTransaction(Map<String, Object> map) {
        this.claimId = String.valueOf(map.get("claimId"));
        if (map.get("owner") != null) {
            String ownerStr = (String) map.get("owner");
            // If the owner string is "SERVER", set owner to null (indicating an admin claim)
            if ("SERVER".equalsIgnoreCase(ownerStr)) {
                this.owner = null;
            } else {
                this.owner = UUID.fromString(ownerStr);
            }
        }
        this.price = (double) map.get("price");
        if (map.get("signLocation") != null)
            this.sign = (Location) map.get("signLocation");
    }

    /**
     * Default constructor.
     */
    public ClaimTransaction() { }
    
    /**
     * Serializes this transaction into a map.
     *
     * @return a map containing the serialized transaction data
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("claimId", this.claimId);
        if (owner != null)
            map.put("owner", owner.toString());
        map.put("price", this.price);
        if (sign != null)
            map.put("signLocation", sign);
        return map;
    }
    
    /**
     * Retrieves the Block that holds the sign for this transaction.
     * 
     * @return the Block if the sign state is an instance of {@code Sign}, otherwise null
     */
    @Override
    public Block getHolder() {
        return sign.getBlock().getState() instanceof Sign ? sign.getBlock() : null;
    }
    
    /**
     * Returns the owner's UUID.
     *
     * @return the UUID of the owner, or null for admin claims
     */
    @Override
    public UUID getOwner() {
        return owner;
    }
    
    /**
     * Sets the owner's UUID.
     *
     * @param newOwner the new owner's UUID
     */
    @Override
    public void setOwner(UUID newOwner) {
        this.owner = newOwner;
    }
    
    /**
     * Attempts to cancel the transaction using the specified player.
     *
     * @param p the player attempting to cancel the transaction
     * @return true if the cancellation was successful, false otherwise
     */
    @Override
    public boolean tryCancelTransaction(Player p) {
        return this.tryCancelTransaction(p, false);
    }
    
    // --- Added getter methods ---
    
    /**
     * Retrieves the claim associated with this transaction.
     * <p>
     * This method uses the RealEstate API to obtain the claim based on the sign location.
     * </p>
     *
     * @return the {@link IClaim} associated with this transaction
     */
    public IClaim getClaim() {
        return me.EtienneDx.RealEstate.RealEstate.claimAPI.getClaimAt(sign);
    }
    
    /**
     * Returns the owner's UUID.
     *
     * @return the UUID of the owner, or null if not set
     */
    public UUID getOwnerUUID() {
        return owner;
    }
    
    /**
     * Returns the transaction price.
     *
     * @return the price of the transaction
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Returns the location of the sign associated with this transaction.
     *
     * @return the sign {@link Location}
     */
    public Location getSign() {
        return sign;
    }
}
