package me.EtienneDx.RealEstate;

import java.time.Duration;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

public class Utils
{
    public static boolean makePayment(UUID receiver, UUID giver, double amount, boolean msgSeller, boolean msgBuyer)
    {
    	// seller might be null if it is the server
    	OfflinePlayer s = receiver != null ? Bukkit.getOfflinePlayer(receiver) : null, b = Bukkit.getOfflinePlayer(giver);
    	if(!RealEstate.econ.has(b, amount))
    	{
    		if(b.isOnline() && msgBuyer)
    		{
    			((Player)b).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
    					"You don't have enough money to make this transaction!");
    		}
    		if(s != null && s.isOnline() && msgSeller)
    		{
    			((Player)s).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
    					b.getName() + " doesn't have enough money to make this transaction!");
    		}
    		return false;
    	}
    	EconomyResponse resp = RealEstate.econ.withdrawPlayer(b, amount);
    	if(!resp.transactionSuccess())
    	{
    		if(b.isOnline() && msgBuyer)
    		{
    			((Player)b).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
    					"Could not withdraw the money!");
    		}
    		if(s != null && s.isOnline() && msgSeller)
    		{
    			((Player)s).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
    					"Could not withdraw the money!");
    		}
    		return false;
    	}
    	if(s != null)
    	{
    		resp = RealEstate.econ.depositPlayer(s, amount);
    		if(!resp.transactionSuccess())
    		{
    			if(b.isOnline() && msgBuyer)
        		{
        			((Player)b).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
        					"Could not deposit to " + s.getName() + ", refunding Player!");
        		}
        		if(s != null && s.isOnline() && msgSeller)
        		{
        			((Player)s).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
        					"Could not deposit to you, refunding" + b.getName() + "!");
        		}
        		RealEstate.econ.depositPlayer(b, amount);
        		return false;
    		}
    	}
    	
    	return true;
    }
	
	public static String getTime(int days, Duration hours, boolean details)
	{
		String time = "";
		if(days >= 7)
		{
			time += (days / 7) + " week" + (days >= 14 ? "s" : "");
		}
		if(days % 7 > 0)
		{
			time += (time.isEmpty() ? "" : " ") + (days % 7) + " day" + (days % 7 > 1 ? "s" : "");
		}
		if((details || days < 7) && hours != null && hours.toHours() > 0)
		{
			time += (time.isEmpty() ? "" : " ") + hours.toHours() + " hour" + (hours.toHours() > 1 ? "s" : "");
		}
		if((details || days == 0) && hours != null && (time.isEmpty() || hours.toMinutes() % 60 > 0))
		{
			time += (time.isEmpty() ? "" : " ") + (hours.toMinutes() % 60) + " min" + (hours.toMinutes() % 60 > 1 ? "s" : "");
		}
		
		return time;
	}
	
	public static void transferClaim(Claim claim, UUID buyer, UUID seller)
	{
		// blocks transfer :
		// if transfert is true, the seller will lose the blocks he had
		// and the buyer will get them
		// (that means the buyer will keep the same amount of remaining blocks after the transaction)
		if(claim.parent == null && RealEstate.instance.config.cfgTransferClaimBlocks)
		{
			PlayerData buyerData = GriefPrevention.instance.dataStore.getPlayerData(buyer);
			PlayerData sellerData = GriefPrevention.instance.dataStore.getPlayerData(seller);
			
			// the seller has to provide the blocks
			sellerData.setBonusClaimBlocks(sellerData.getBonusClaimBlocks() - claim.getArea());
			if (sellerData.getBonusClaimBlocks() < 0)// can't have negative bonus claim blocks, so if need be, we take into the accrued 
	        {
	            sellerData.setAccruedClaimBlocks(sellerData.getAccruedClaimBlocks() + sellerData.getBonusClaimBlocks());
	            sellerData.setBonusClaimBlocks(0);
	        }
			
			// the buyer receive them
			buyerData.setBonusClaimBlocks(buyerData.getBonusClaimBlocks() + claim.getArea());
		}
		
		// start to change owner
		if(claim.parent == null)
			for(Claim child : claim.children)
			{
				child.clearPermissions();
				child.managers.clear();
			}
		claim.clearPermissions();
		
		try
		{
			if(claim.parent == null)
				GriefPrevention.instance.dataStore.changeClaimOwner(claim, buyer);
			else
			{
				claim.setPermission(buyer.toString(), ClaimPermission.Build);
			}
		}
		catch (Exception e)// error occurs when trying to change subclaim owner
		{
			e.printStackTrace();
			return;
		}
		GriefPrevention.instance.dataStore.saveClaim(claim);
					
	}
	
	public static String getSignString(String str)
	{
		if(str.length() > 16)
			str = str.substring(0, 16);
		return str;
	}
}
