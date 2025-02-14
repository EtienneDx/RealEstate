package me.EtienneDx.RealEstate.Transactions;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;

public abstract class BoughtTransaction extends ClaimTransaction {
    public UUID buyer = null;
    public ExitOffer exitOffer = null;
    public boolean destroyedSign = false;
    
    public BoughtTransaction(Map<String, Object> map) {
        super(map);
        if(map.get("buyer") != null)
            buyer = UUID.fromString((String) map.get("buyer"));
        if(map.get("exitOffer") != null)
            exitOffer = (ExitOffer) map.get("exitOffer");
        if(map.get("destroyedSign") != null)
            destroyedSign = (boolean) map.get("destroyedSign");
    }
    
    public BoughtTransaction(IClaim claim, Player player, double price, Location sign) {
        super(claim, player, price, sign);
    }
    
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
    
    public void destroySign() {
        // Example: destroy sign if configured
        if(!destroyedSign && getHolder() != null)
            getHolder().breakNaturally();
        destroyedSign = true;
    }
    
    public UUID getBuyer() {
        return buyer;
    }
}
