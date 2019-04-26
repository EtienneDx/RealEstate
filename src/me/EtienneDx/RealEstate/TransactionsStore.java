package me.EtienneDx.RealEstate;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.ryanhamshire.GriefPrevention.Claim;
import net.md_5.bungee.api.ChatColor;

public class TransactionsStore
{
    public final String dataFilePath = RealEstate.pluginDirPath + "transactions.data";
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();

    public HashMap<String, ClaimSell> claimSell;
    public HashMap<String, ClaimRent> claimRent;
    
    public TransactionsStore()
    {
    	loadData();
    	new BukkitRunnable()
    	{
			
			@Override
			public void run()
			{
				for(ClaimRent cr : claimRent.values())
				{
					cr.update();
				}
			}
		}.runTaskTimer(RealEstate.instance, 0, 1200L);// run every 60 seconds
    }
    
    public void loadData()
    {
    	claimSell = new HashMap<>();
    	claimRent = new HashMap<>();
    	
    	FileConfiguration config = YamlConfiguration.loadConfiguration(new File(this.dataFilePath));
    	for(String key : config.getKeys(true))
    	{
			RealEstate.instance.log.info(key);
    		if(key.startsWith("Sell."))
    		{
    			ClaimSell cs = (ClaimSell)config.get(key);
    			claimSell.put(key.substring(5), cs);
    		}
    		else if(key.startsWith("Rent."))
    		{
    			ClaimRent cr = (ClaimRent)config.get(key);
    			claimRent.put(key.substring(5), cr);
    		}
    	}
    }
    
    public void saveData()
    {
    	YamlConfiguration config = new YamlConfiguration();
        for (ClaimSell cs : claimSell.values())
            config.set("Sell." + cs.claimId, cs);
        for (ClaimRent cr : claimRent.values())
            config.set("Rent." + cr.claimId, cr);
        try
        {
			config.save(new File(this.dataFilePath));
		}
        catch (IOException e)
        {
			RealEstate.instance.log.info("Unable to write to the data file at \"" + this.dataFilePath + "\"");
		}
    }
	
	public boolean anyTransaction(Claim claim)
	{
		return claimSell.containsKey(claim.getID().toString()) || claimRent.containsKey(claim.getID().toString());
	}

	public Transaction getTransaction(Claim claim)
	{
		if(claimSell.containsKey(claim.getID().toString()))
			return claimSell.get(claim.getID().toString());
		if(claimRent.containsKey(claim.getID().toString()))
			return claimRent.get(claim.getID().toString());
		return null;
	}

	public void cancelTransaction(Claim claim)
	{
		if(anyTransaction(claim))
		{
			Transaction tr = getTransaction(claim);
			cancelTransaction(tr);
		}
		saveData();
	}

	public void cancelTransaction(Transaction tr)
	{
		tr.getHolder().breakNaturally();
		if(tr instanceof ClaimSell)
		{
			claimSell.remove(String.valueOf(((ClaimSell) tr).claimId));
		}
		if(tr instanceof ClaimRent)
		{
			claimRent.remove(String.valueOf(((ClaimRent) tr).claimId));
		}
		saveData();
	}

	public void sell(Claim claim, Player player, double price, Location sign)
	{
		ClaimSell cs = new ClaimSell(claim, player, price, sign);
		claimSell.put(claim.getID().toString(), cs);
		cs.update();
		saveData();
		
		RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " + player.getName() + 
				" has made " + (claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " for sale at " +
                "[" + player.getLocation().getWorld() + ", " +
                "X: " + player.getLocation().getBlockX() + ", " +
                "Y: " + player.getLocation().getBlockY() + ", " +
                "Z: " + player.getLocation().getBlockZ() + "] " +
                "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
		
		if(player != null)
		{
			player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + "You have successfully created " + 
					(claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " sale for " + 
					ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
		}
		if(RealEstate.instance.dataStore.cfgBroadcastSell)
		{
			for(Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if(p != player)
				{
					p.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.DARK_GREEN + player.getDisplayName() + 
							ChatColor.AQUA + " has put " + 
							(claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " for sale for " + 
							ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
				}
			}
		}
	}

	public void rent(Claim claim, Player player, double price, Location sign, int duration)
	{
		ClaimRent cr = new ClaimRent(claim, player, price, sign, duration);
		claimRent.put(claim.getID().toString(), cr);
		cr.update();
		saveData();
		
		RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " + player.getName() + 
				" has made " + (claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " for rent at " +
                "[" + player.getLocation().getWorld() + ", " +
                "X: " + player.getLocation().getBlockX() + ", " +
                "Y: " + player.getLocation().getBlockY() + ", " +
                "Z: " + player.getLocation().getBlockZ() + "] " +
                "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
		
		if(player != null)
		{
			player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + "You have successfully created " + 
					(claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " rent for " + 
					ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
		}
		if(RealEstate.instance.dataStore.cfgBroadcastSell)
		{
			for(Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if(p != player)
				{
					p.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.DARK_GREEN + player.getDisplayName() + 
							ChatColor.AQUA + " has put " + 
							(claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " for rent for " + 
							ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
				}
			}
		}
	}
}
