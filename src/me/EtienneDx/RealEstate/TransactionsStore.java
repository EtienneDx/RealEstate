package me.EtienneDx.RealEstate;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
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
    public HashMap<String, ClaimLease> claimLease;
    
    public TransactionsStore()
    {
    	loadData();
    	new BukkitRunnable()
    	{
			
			@Override
			public void run()
			{
				Collection<ClaimRent> col = claimRent.values();// need intermediate since some may get removed in the process
				for(ClaimRent cr : col)
				{
					cr.update();
				}
				Collection<ClaimLease> co = claimLease.values();// need intermediate since some may get removed in the process
				for(ClaimLease cl : co)
				{
					cl.update();
				}
			}
		}.runTaskTimer(RealEstate.instance, 0, 1200L);// run every 60 seconds
    }
    
    public void loadData()
    {
    	claimSell = new HashMap<>();
    	claimRent = new HashMap<>();
    	claimLease = new HashMap<>();
    	
    	FileConfiguration config = YamlConfiguration.loadConfiguration(new File(this.dataFilePath));
    	for(String key : config.getKeys(true))
    	{
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
    		else if(key.startsWith("Lease."))
    		{
    			ClaimLease cl = (ClaimLease)config.get(key);
    			claimLease.put(key.substring(6), cl);
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
        for (ClaimLease cl : claimLease.values())
            config.set("Lease." + cl.claimId, cl);
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
		return claim != null && 
				(claimSell.containsKey(claim.getID().toString()) || 
						claimRent.containsKey(claim.getID().toString()) || 
						claimLease.containsKey(claim.getID().toString()));
	}

	public Transaction getTransaction(Claim claim)
	{
		if(claimSell.containsKey(claim.getID().toString()))
			return claimSell.get(claim.getID().toString());
		if(claimRent.containsKey(claim.getID().toString()))
			return claimRent.get(claim.getID().toString());
		if(claimLease.containsKey(claim.getID().toString()))
			return claimLease.get(claim.getID().toString());
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
		if(tr.getHolder() != null)
			tr.getHolder().breakNaturally();
		if(tr instanceof ClaimSell)
		{
			claimSell.remove(String.valueOf(((ClaimSell) tr).claimId));
		}
		if(tr instanceof ClaimRent)
		{
			claimRent.remove(String.valueOf(((ClaimRent) tr).claimId));
		}
		if(tr instanceof ClaimLease)
		{
			claimLease.remove(String.valueOf(((ClaimLease) tr).claimId));
		}
		saveData();
	}
	
	public boolean canCancelTransaction(Transaction tr)
	{
		return tr instanceof ClaimSell || (tr instanceof ClaimRent && ((ClaimRent)tr).rentedBy == null) || 
				(tr instanceof ClaimLease && ((ClaimLease)tr).buyer == null);
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
			player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + "You have successfully put " + 
					(claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " for rent for " + 
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

	public void lease(Claim claim, Player player, double price, Location sign, int frequency, int paymentsCount)
	{
		ClaimLease cl = new ClaimLease(claim, player, price, sign, frequency, paymentsCount);
		claimLease.put(claim.getID().toString(), cl);
		cl.update();
		saveData();
		
		RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " + player.getName() + 
				" has made " + (claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " for lease at " +
                "[" + player.getLocation().getWorld() + ", " +
                "X: " + player.getLocation().getBlockX() + ", " +
                "Y: " + player.getLocation().getBlockY() + ", " +
                "Z: " + player.getLocation().getBlockZ() + "] " +
                "Payments Count : " + paymentsCount + " " + 
                "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
		
		if(player != null)
		{
			player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + "You have successfully put " + 
					(claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " for lease for " + 
					ChatColor.GREEN + paymentsCount + ChatColor.AQUA + " payments of " +
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
							(claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.parent == null ? "claim" : "subclaim") + " for lease for " + 
							ChatColor.GREEN + paymentsCount + ChatColor.AQUA + " payments of " +
							ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
				}
			}
		}
	}
}
