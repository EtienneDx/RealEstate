package me.EtienneDx.RealEstate.Transactions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Represents an exit offer for a RealEstate transaction.
 * <p>
 * An exit offer is used to propose an early exit from an ongoing transaction.
 * It contains the UUID of the player making the offer and the offered price.
 * </p>
 */
public class ExitOffer implements ConfigurationSerializable {

    /**
     * The UUID of the player who made the exit offer.
     */
    public UUID offerBy;
    
    /**
     * The price offered to exit the transaction.
     */
    public double price;
    
    /**
     * Constructs an ExitOffer with the specified offeror and price.
     *
     * @param offerBy the UUID of the player making the exit offer
     * @param price   the offered price for the exit
     */
    public ExitOffer(UUID offerBy, double price) {
        this.offerBy = offerBy;
        this.price = price;
    }
    
    /**
     * Constructs an ExitOffer from a serialized map.
     *
     * @param map a map containing the serialized exit offer data
     */
    public ExitOffer(Map<String, Object> map) {
        offerBy = UUID.fromString((String) map.get("offerBy"));
        price = (double) map.get("price");
    }
    
    /**
     * Serializes this ExitOffer into a map.
     *
     * @return a map representation of this ExitOffer
     */
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("offerBy", offerBy.toString());
        map.put("price", price);
        return map;
    }
}
