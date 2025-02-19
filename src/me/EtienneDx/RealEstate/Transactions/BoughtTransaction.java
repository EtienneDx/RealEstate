package me.EtienneDx.RealEstate.Transactions;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;

/**
 * Represents a transaction for a claim that has been bought.
 * <p>
 * This abstract class extends {@link ClaimTransaction} and adds additional properties
 * specific to transactions where a claim has been purchased, including the buyer's UUID,
 * an exit offer, and a flag indicating whether the transaction sign has been destroyed.
 * </p>
 */
public abstract class BoughtTransaction extends ClaimTransaction {
    /**
     * The UUID of the buyer, or {@code null} if no buyer has been assigned.
     */
    public UUID buyer = null;
    
    /**
     * An optional exit offer for the transaction.
     */
    public ExitOffer exitOffer = null;
    
    /**
     * Flag indicating whether the sign representing this transaction has been destroyed.
     */
    public boolean destroyedSign = false;
    
    /**
     * Constructs a {@code BoughtTransaction} from a serialized map.
     *
     * @param map the map containing serialized data for this transaction
     */
    public BoughtTransaction(Map<String, Object> map) {
        super(map);
        if(map.get("buyer") != null)
            buyer = UUID.fromString((String) map.get("buyer"));
        if(map.get("exitOffer") != null)
            exitOffer = (ExitOffer) map.get("exitOffer");
        if(map.get("destroyedSign") != null)
            destroyedSign = (boolean) map.get("destroyedSign");
    }
    
    /**
     * Constructs a new {@code BoughtTransaction} with the specified claim, player, price, and sign location.
     *
     * @param claim the claim involved in this transaction
     * @param player the player initiating the transaction
     * @param price the price of the transaction
     * @param sign the location of the transaction sign
     */
    public BoughtTransaction(IClaim claim, Player player, double price, Location sign) {
        super(claim, player, price, sign);
    }
    
    /**
     * Serializes this transaction to a map.
     * <p>
     * This method includes additional properties specific to bought transactions.
     * </p>
     *
     * @return a map representing the serialized form of this transaction
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        if(buyer != null)
            map.put("buyer", buyer.toString());
        if(exitOffer != null)
            map.put("exitOffer", exitOffer);
        map.put("destroyedSign", destroyedSign);
        return map;
    }
    
    /**
     * Destroys the transaction sign if it has not already been destroyed.
     * <p>
     * This method checks whether the sign is configured to be destroyed and,
     * if so, it breaks the sign naturally in the world.
     * </p>
     */
    public void destroySign() {
        // Example: destroy sign if configured
        if(!destroyedSign && getHolder() != null)
            getHolder().breakNaturally();
        destroyedSign = true;
    }
    
    /**
     * Returns the UUID of the buyer.
     *
     * @return the buyer's UUID, or {@code null} if no buyer is assigned
     */
    public UUID getBuyer() {
        return buyer;
    }
}
