package me.EtienneDx.RealEstate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class REListener implements Listener
{
	void registerEvents()
	{
		PluginManager pm = RealEstate.instance.getServer().getPluginManager();
		
		pm.registerEvents(this, RealEstate.instance);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		if(RealEstate.instance.dataStore.cfgSellKeywords.contains(event.getLine(0).toLowerCase()) || 
				RealEstate.instance.dataStore.cfgLeaseKeywords.contains(event.getLine(0).toLowerCase()) || 
				RealEstate.instance.dataStore.cfgRentKeywords.contains(event.getLine(0).toLowerCase()))
		{
			Player player = event.getPlayer();
			Location loc = event.getBlock().getLocation();
			
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
			if(claim == null)// must have something to sell
			{
				player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "The sign you placed is not inside a claim!");
                event.setCancelled(true);
                event.getBlock().breakNaturally();
                return;
			}
			if(RealEstate.transactionsStore.anyTransaction(claim))
			{
				player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "This claim is already to sell/lease!");
                event.setCancelled(true);
                event.getBlock().breakNaturally();
                return;
			}
			
			// empty is considered a wish to sell
			if(RealEstate.instance.dataStore.cfgSellKeywords.contains(event.getLine(0).toLowerCase()))
			{
				String type = claim.parent == null ? "claim" : "subclaim";
				if(!RealEstate.perms.has(player, "realestate." + type + ".sell"))
				{
					player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You don't have the permission to sell " + type + "s!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}

				// check for a valid price
				double price;
				try
				{
					price = getDouble(event, 1, RealEstate.instance.dataStore.cfgPriceSellPerBlock * claim.getArea());
				}
				catch (NumberFormatException e)
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "The price you entered is not a valid number!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				if(price <= 0)
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "The price must be greater than 0!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				
				if(claim.isAdminClaim() && !RealEstate.perms.has(player, "realestate.admin"))// admin may sell admin claims
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You don't have the permission to sell admin claims!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				else if(type.equals("claim") && !player.getUniqueId().equals(claim.ownerID))// only the owner may sell his claim
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You can only sell claims you own!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				
				// we should be good to sell it now
				event.setCancelled(true);// need to cancel the event, so we can update the sign elsewhere
				RealEstate.transactionsStore.sell(claim, player, price, event.getBlock().getLocation());
			}
			else if(RealEstate.instance.dataStore.cfgRentKeywords.contains(event.getLine(0).toLowerCase()))// we want to rent it
			{
				String type = claim.parent == null ? "claim" : "subclaim";
				if(!RealEstate.perms.has(player, "realestate." + type + ".rent"))
				{
					player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You don't have the permission to rent " + type + "s!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}

				// check for a valid price
				double price;
				try
				{
					price = getDouble(event, 1, RealEstate.instance.dataStore.cfgPriceRentPerBlock * claim.getArea());
				}
				catch (NumberFormatException e)
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "The price you entered is not a valid number!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				if(price <= 0)
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "The price must be greater than 0!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				
				if(event.getLine(2).isEmpty())
				{
					event.setLine(2, RealEstate.instance.dataStore.cfgRentTime);
				}
				int duration = parseDuration(event.getLine(2));
				if(duration == 0)
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "Couldn't read the date!\n" + 
	                		"Date must be formatted as follow" + ChatColor.GREEN + "10 weeks" + ChatColor.RED + " or " + 
	                		ChatColor.GREEN + "3 days" + ChatColor.RED + " or " +  ChatColor.GREEN + "1 week 3 days");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				
				if(claim.isAdminClaim() && !RealEstate.perms.has(player, "realestate.admin"))// admin may rent admin claims
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You don't have the permission to rent admin claims!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				else if(type.equals("claim") && !player.getUniqueId().equals(claim.ownerID))// only the owner may sell his claim
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You can only rent claims you own!");
	                event.setCancelled(true);
	                event.getBlock().breakNaturally();
	                return;
				}
				
				// all should be good, we can create the rent
				event.setCancelled(true);
				RealEstate.transactionsStore.rent(claim, player, price, event.getBlock().getLocation(), duration);
			}
		}
	}
	
	private int parseDuration(String line)
	{
		Pattern p = Pattern.compile("^(?:(?<weeks>\\d{1,2}) ?w(?:eeks?)?)? ?(?:(?<days>\\d{1,2}) ?d(?:ays?)?)?$", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(line);
		if(!line.isEmpty() && m.matches()) 
		{
			int ret = 0;
			if(m.group("weeks") != null)
				ret += 7 * Integer.parseInt(m.group("weeks"));
			if(m.group("days") != null)
				ret += Integer.parseInt(m.group("days"));
			return ret;
		}
		return 0;
	}

	private double getDouble(SignChangeEvent event, int line, double defaultValue) throws NumberFormatException
	{
		if(event.getLine(line).isEmpty())// if no price precised, make it the default one
		{
			event.setLine(line, Double.toString(defaultValue));
		}
		return Double.parseDouble(event.getLine(line));
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getState() instanceof Sign)
		{
			Sign sign = (Sign)event.getClickedBlock().getState();
			if(ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase(ChatColor.stripColor(RealEstate.instance.dataStore.cfgSignsHeader)))// it is a real estate sign
			{
				Player player = event.getPlayer();
				Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getClickedBlock().getLocation(), false, null);
				
				if(!RealEstate.transactionsStore.anyTransaction(claim))
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
	                		"This claim is no longer for rent or for sell, sorry...");
	                event.getClickedBlock().breakNaturally();
	                event.setCancelled(true);
	                return;
				}
				
				Transaction tr = RealEstate.transactionsStore.getTransaction(claim);
				if(player.isSneaking())
					tr.preview(player);
				else
					tr.interact(player);
			}
		}
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event)
	{
		if(event.getBlock().getState() instanceof Sign)
		{
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getBlock().getLocation(), false, null);
			if(claim != null)
			{
				Transaction tr = RealEstate.transactionsStore.getTransaction(claim);
				if(tr != null && event.getBlock().equals(tr.getHolder()))
				{
					if(event.getPlayer() != null && !tr.getOwner().equals(event.getPlayer().getUniqueId()) && 
							!RealEstate.perms.has(event.getPlayer(), "realestate.destroysigns"))
					{
						event.getPlayer().sendMessage(RealEstate.instance.dataStore.chatPrefix + 
								ChatColor.RED + "Only the author of the sell/rent sign is allowed to destroy it");
						event.setCancelled(true);
						return;
					}
					// the sign has been destroy, we can remove the transaction
					RealEstate.transactionsStore.cancelTransaction(tr);
				}
			}
		}
	}
}
