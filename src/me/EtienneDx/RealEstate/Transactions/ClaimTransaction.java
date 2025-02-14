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

public abstract class ClaimTransaction implements ConfigurationSerializable, Transaction {
    public String claimId;
    public UUID owner = null;
    public double price;
    public Location sign = null;
    
    public ClaimTransaction(IClaim claim, Player player, double price, Location sign) {
        this.claimId = claim.getId();
        this.owner = player != null ? player.getUniqueId() : null;
        this.price = price;
        this.sign = sign;
    }
    
    public ClaimTransaction(Map<String, Object> map) {
        this.claimId = String.valueOf(map.get("claimId"));
        if (map.get("owner") != null) {
            String ownerStr = (String) map.get("owner");
            // If the owner string is "SERVER", set owner to null (i.e. admin claim)
            if ("SERVER".equalsIgnoreCase(ownerStr)) {
                this.owner = null;
            } else {
                this.owner = UUID.fromString(ownerStr);
            }
        }
        this.price = (double) map.get("price");
        if(map.get("signLocation") != null)
            this.sign = (Location) map.get("signLocation");
    }

    
    public ClaimTransaction() { }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("claimId", this.claimId);
        if(owner != null)
            map.put("owner", owner.toString());
        map.put("price", this.price);
        if(sign != null)
            map.put("signLocation", sign);
        return map;
    }
    
    @Override
    public Block getHolder() {
        return sign.getBlock().getState() instanceof Sign ? sign.getBlock() : null;
    }
    
    @Override
    public UUID getOwner() {
        return owner;
    }
    
    @Override
    public void setOwner(UUID newOwner) {
        this.owner = newOwner;
    }
    
    @Override
    public boolean tryCancelTransaction(Player p) {
        return this.tryCancelTransaction(p, false);
    }
    
    // --- Added getter methods ---
    public IClaim getClaim() {
        // Use your API to retrieve the claim from the sign.
        return me.EtienneDx.RealEstate.RealEstate.claimAPI.getClaimAt(sign);
    }
    
    public UUID getOwnerUUID() {
        return owner;
    }
    
    public double getPrice() {
        return price;
    }
    
    public Location getSign() {
        return sign;
    }
}
