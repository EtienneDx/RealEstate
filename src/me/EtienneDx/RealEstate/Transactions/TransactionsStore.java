package me.EtienneDx.RealEstate.Transactions;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.EtienneDx.RealEstate.Messages;
import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.Utils;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;

public class TransactionsStore
{
    public final String dataFilePath = RealEstate.pluginDirPath + "transactions.data";
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();

    public HashMap<String, ClaimSell> claimSell;
    public HashMap<String, ClaimRent> claimRent;
    public HashMap<String, ClaimLease> claimLease;
    public HashMap<String, ClaimAuction> claimAuction;
    
    public TransactionsStore()
    {
    	loadData();
    	new BukkitRunnable()
    	{
			
			@Override
			public void run()
			{
				Iterator<ClaimRent> it1 = claimRent.values().iterator();
				while(it1.hasNext())
				{
					if(it1.next().update())
						it1.remove();
				}

				Iterator<ClaimLease> it2 = claimLease.values().iterator();
				while(it2.hasNext())
				{
					if(it2.next().update())
						it2.remove();
				}

				Iterator<ClaimAuction> it3 = claimAuction.values().iterator();
				while(it3.hasNext())
				{
					if(it3.next().update())
						it3.remove();
				}
				saveData();
			}
		}.runTaskTimer(RealEstate.instance, 1200L, 1200L);// run every 60 seconds
    }
    
    public void loadData()
    {
    	claimSell = new HashMap<>();
    	claimRent = new HashMap<>();
    	claimLease = new HashMap<>();
    	claimAuction = new HashMap<>();
    	
    	File file = new File(this.dataFilePath);
    	
    	if(file.exists())
    	{
	    	FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	    	try {
				RealEstate.instance.addLogEntry(new String(Files.readAllBytes(FileSystems.getDefault().getPath(this.dataFilePath))));
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	ConfigurationSection sell = config.getConfigurationSection("Sell");
	    	ConfigurationSection rent = config.getConfigurationSection("Rent");
	    	ConfigurationSection lease = config.getConfigurationSection("Lease");
	    	ConfigurationSection auction = config.getConfigurationSection("Auction");
	    	if(sell != null)
	    	{
	    		RealEstate.instance.addLogEntry(sell.toString());
	    		RealEstate.instance.addLogEntry(sell.getKeys(false).size() + "");
		    	for(String key : sell.getKeys(false))
				{
					ClaimSell cs = (ClaimSell)sell.get(key);
					claimSell.put(key, cs);
				}
	    	}
	    	if(rent != null)
	    	{
				for(String key : rent.getKeys(false))
				{
					ClaimRent cr = (ClaimRent)rent.get(key);
					claimRent.put(key, cr);
				}
			}
	    	if(lease != null)
	    	{
				for(String key : lease.getKeys(false))
				{
					ClaimLease cl = (ClaimLease)lease.get(key);
					claimLease.put(key, cl);
		    	}
			}
	    	if(auction != null)
	    	{
				for(String key : auction.getKeys(false))
				{
					ClaimAuction cl = (ClaimAuction)auction.get(key);
					claimAuction.put(key, cl);
		    	}
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
		for (ClaimAuction cl : claimAuction.values())
			config.set("Auction." + cl.claimId, cl);
        try
        {
			config.save(new File(this.dataFilePath));
		}
        catch (IOException e)
        {
			RealEstate.instance.log.info("Unable to write to the data file at \"" + this.dataFilePath + "\"");
		}
    }
	
	public boolean anyTransaction(IClaim claim)
	{
		return claim != null && 
				!claim.isWilderness() &&
				(claimSell.containsKey(claim.getId()) || 
						claimRent.containsKey(claim.getId()) || 
						claimLease.containsKey(claim.getId()) || 
						claimAuction.containsKey(claim.getId()));
	}

	public Transaction getTransaction(IClaim claim)
	{
		if(claimSell.containsKey(claim.getId()))
			return claimSell.get(claim.getId());
		if(claimRent.containsKey(claim.getId()))
			return claimRent.get(claim.getId());
		if(claimLease.containsKey(claim.getId()))
			return claimLease.get(claim.getId());
		if(claimAuction.containsKey(claim.getId()))
			return claimAuction.get(claim.getId());
		return null;
	}

	public void cancelTransaction(IClaim claim)
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
		if(tr instanceof ClaimAuction)
		{
			claimAuction.remove(String.valueOf(((ClaimAuction) tr).claimId));
		}
		saveData();
	}
	
	public boolean canCancelTransaction(Transaction tr)
	{
		return tr instanceof ClaimSell ||
				(tr instanceof ClaimAuction && ((ClaimRent)tr).buyer == null || RealEstate.instance.config.cfgCancelAuction) ||
				(tr instanceof ClaimRent && ((ClaimRent)tr).buyer == null) ||
				(tr instanceof ClaimLease && ((ClaimLease)tr).buyer == null);
	}

	public void sell(IClaim claim, Player player, double price, Location sign)
	{
		ClaimSell cs = new ClaimSell(claim, claim.isAdminClaim() ? null : player, price, sign);
		claimSell.put(claim.getId(), cs);
		cs.update();
		saveData();
		
		RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " + (player == null ? "The Server" : player.getName()) + 
				" has made " + (claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.isParentClaim() ? "claim" : "subclaim") + " for sale at " +
                "[" + claim.getWorld() + ", " +
                "X: " + claim.getX() + ", " +
                "Y: " + claim.getY() + ", " +
                "Z: " + claim.getZ() + "] " +
                "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
	
		String claimPrefix = claim.isAdminClaim() ? RealEstate.instance.messages.keywordAdminClaimPrefix :
				RealEstate.instance.messages.keywordClaimPrefix;
		String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim :
				RealEstate.instance.messages.keywordSubclaim;

		if(player != null)
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimCreatedSell,
					claimPrefix,
					claimTypeDisplay,
					RealEstate.econ.format(price));
		}
		if(RealEstate.instance.config.cfgBroadcastSell)
		{
			for(Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if(p != player)
				{
					Messages.sendMessage(p, RealEstate.instance.messages.msgInfoClaimCreatedSellBroadcast,
							player == null ? RealEstate.instance.messages.keywordTheServer : player.getDisplayName(),
							claimPrefix,
							claimTypeDisplay,
							RealEstate.econ.format(price));
				}
			}
		}
	}

	public void rent(IClaim claim, Player player, double price, Location sign, int duration, boolean buildTrust)
	{
		ClaimRent cr = new ClaimRent(claim, claim.isAdminClaim() ? null : player, price, sign, duration, buildTrust);
		claimRent.put(claim.getId(), cr);
		cr.update();
		saveData();
		
		RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " + (player == null ? "The Server" : player.getName()) + 
				" has made " + (claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.isParentClaim() ? "claim" : "subclaim") + " for" + (buildTrust ? "" : " container") + " rent at " +
				"[" + claim.getWorld() + ", " +
                "X: " + claim.getX() + ", " +
                "Y: " + claim.getY() + ", " +
                "Z: " + claim.getZ() + "] " +
                "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
	
		String claimPrefix = claim.isAdminClaim() ? RealEstate.instance.messages.keywordAdminClaimPrefix :
				RealEstate.instance.messages.keywordClaimPrefix;
		String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim :
				RealEstate.instance.messages.keywordSubclaim;
		
		if(player != null)
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimCreatedRent,
					claimPrefix,
					claimTypeDisplay,
					RealEstate.econ.format(price),
					Utils.getTime(duration, null, false));
		}
		if(RealEstate.instance.config.cfgBroadcastSell)
		{
			for(Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if(p != player)
				{
					Messages.sendMessage(p, RealEstate.instance.messages.msgInfoClaimCreatedRentBroadcast,
							player == null ? RealEstate.instance.messages.keywordTheServer : player.getDisplayName(),
							claimPrefix,
							claimTypeDisplay,
							RealEstate.econ.format(price),
							Utils.getTime(duration, null, false));
				}
			}
		}
	}

	public void lease(IClaim claim, Player player, double price, Location sign, int frequency, int paymentsCount)
	{
		ClaimLease cl = new ClaimLease(claim, claim.isAdminClaim() ? null : player, price, sign, frequency, paymentsCount);
		claimLease.put(claim.getId(), cl);
		cl.update();
		saveData();
		
		RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " + (player == null ? "The Server" : player.getName()) + 
				" has made " + (claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.isParentClaim() ? "claim" : "subclaim") + " for lease at " +
				"[" + claim.getWorld() + ", " +
                "X: " + claim.getX() + ", " +
                "Y: " + claim.getY() + ", " +
                "Z: " + claim.getZ() + "] " +
                "Payments Count : " + paymentsCount + " " + 
                "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
	
		String claimPrefix = claim.isAdminClaim() ? RealEstate.instance.messages.keywordAdminClaimPrefix :
				RealEstate.instance.messages.keywordClaimPrefix;
		String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim :
				RealEstate.instance.messages.keywordSubclaim;
		
		if(player != null)
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimCreatedLease,
					claimPrefix,
					claimTypeDisplay,
					RealEstate.econ.format(price),
					paymentsCount + "",
					Utils.getTime(frequency, null, false));
		}
		if(RealEstate.instance.config.cfgBroadcastSell)
		{
			for(Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if(p != player)
				{
					Messages.sendMessage(p, RealEstate.instance.messages.msgInfoClaimCreatedLeaseBroadcast,
							player == null ? RealEstate.instance.messages.keywordTheServer : player.getDisplayName(),
							claimPrefix,
							claimTypeDisplay,
							RealEstate.econ.format(price),
							paymentsCount + "",
							Utils.getTime(frequency, null, false));
				}
			}
		}
	}

	public void auction(IClaim claim, Player player, double price, Location sign, int duration, double bidStep)
	{
		LocalDateTime endDate = LocalDateTime.now().plusDays(duration);
		ClaimAuction ca = new ClaimAuction(claim, claim.isAdminClaim() ? null : player, price, sign, endDate, bidStep);
		claimAuction.put(claim.getId(), ca);
		ca.update();
		saveData();
		
		RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " + (player == null ? "The Server" : player.getName()) + 
				" has made " + (claim.isAdminClaim() ? "an admin" : "a") + " " + (claim.isParentClaim() ? "claim" : "subclaim") + " for auction at " +
				"[" + claim.getWorld() + ", " +
                "X: " + claim.getX() + ", " +
                "Y: " + claim.getY() + ", " +
                "Z: " + claim.getZ() + "] " +
                "Price: " + price + " " + RealEstate.econ.currencyNamePlural() +
				"Bid Step: " + bidStep + " " + RealEstate.econ.currencyNamePlural() +
				"End Date: " + endDate.toString());
	
		String claimPrefix = claim.isAdminClaim() ? RealEstate.instance.messages.keywordAdminClaimPrefix :
				RealEstate.instance.messages.keywordClaimPrefix;
		String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim :
				RealEstate.instance.messages.keywordSubclaim;

		if(player != null)
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimCreatedAuction,
					claimPrefix,
					claimTypeDisplay,
					RealEstate.econ.format(price),
					RealEstate.econ.format(bidStep),
					Utils.getTime(duration, null, false));
		}
		if(RealEstate.instance.config.cfgBroadcastSell)
		{
			for(Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if(p != player)
				{
					Messages.sendMessage(p, RealEstate.instance.messages.msgInfoClaimCreatedAuctionBroadcast,
							player == null ? RealEstate.instance.messages.keywordTheServer : player.getDisplayName(),
							claimPrefix,
							claimTypeDisplay,
							RealEstate.econ.format(price),
							RealEstate.econ.format(bidStep),
							Utils.getTime(duration, null, false));
				}
			}
		}
	}

	public Transaction getTransaction(Player player)
	{
		if(player == null) return null;
		IClaim c = RealEstate.claimAPI.getClaimAt(player.getLocation());
		return getTransaction(c);
	}
}
