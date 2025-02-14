package me.EtienneDx.RealEstate.ClaimAPI.GriefDefender;

import java.util.UUID;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.ClaimResult;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.IClaimAPI;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;

public class GriefDefenderAPI implements IClaimAPI{

    @Override
    public IClaim getClaimAt(Location location) {
        return new GDClaim(GriefDefender.getCore().getClaimAt(location));
    }

    @Override
    public void saveClaim(IClaim claim) {
        // GD auto saves
    }

    @Override
    public IPlayerData getPlayerData(UUID player) {
        return new GDPlayerData(GriefDefender.getCore().getPlayerData(Bukkit.getPlayer(player).getWorld().getUID(), player));
    }

    @Override
    public void changeClaimOwner(IClaim claim, UUID newOwner) {
        if(claim instanceof GDClaim) {
            ClaimResult res = ((GDClaim) claim).getClaim().transferOwner(newOwner);
            if(!res.successful()) {
                throw new RuntimeException(res.getResultType().toString());
            }
        }
    }

    @Override
    public void registerEvents() {
        new GDPermissionListener();
    }
    
}
