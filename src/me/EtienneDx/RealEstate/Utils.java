package me.EtienneDx.RealEstate;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

public class Utils
{
    public static boolean makePayment(UUID seller, UUID buyer, double amount, boolean msgSeller, boolean msgBuyer)
    {
    	// seller might be null if it is the server
    	OfflinePlayer s = seller != null ? Bukkit.getOfflinePlayer(seller) : null, b = Bukkit.getOfflinePlayer(buyer);
    	if(!RealEstate.econ.has(b, amount))
    	{
    		if(b.isOnline() && msgBuyer)
    		{
    			((Player)b).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
    					"You don't have enough money to make this transaction!");
    		}
    		if(s != null && s.isOnline() && msgSeller)
    		{
    			((Player)s).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
    					b.getName() + " doesn't have enough money to make this transaction!");
    		}
    		return false;
    	}
    	EconomyResponse resp = RealEstate.econ.withdrawPlayer(b, amount);
    	if(!resp.transactionSuccess())
    	{
    		if(b.isOnline() && msgBuyer)
    		{
    			((Player)b).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
    					"Could not withdraw the money!");
    		}
    		if(s != null && s.isOnline() && msgSeller)
    		{
    			((Player)s).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
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
        			((Player)b).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
        					"Could not deposit to " + s.getName() + ", refunding Player!");
        		}
        		if(s != null && s.isOnline() && msgSeller)
        		{
        			((Player)s).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
        					"Could not deposit to you, refunding" + b.getName() + "!");
        		}
        		RealEstate.econ.depositPlayer(b, amount);
        		return false;
    		}
    	}
    	
    	return true;
    }
}
