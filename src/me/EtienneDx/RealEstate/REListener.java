package me.EtienneDx.RealEstate;

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
		if(RealEstate.instance.dataStore.cfgSigns.contains(event.getLine(0).toLowerCase()))
		{
			Player player = event.getPlayer();
			Location loc = event.getBlock().getLocation();
			
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
			if(claim == null)// must have something to sell
			{
				player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "The sign you placed is not inside a claim!");
                event.setCancelled(true);
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
			if(event.getLine(1).isEmpty() || RealEstate.instance.dataStore.cfgSellKeywords.contains(event.getLine(1).toLowerCase()))
			{
				String type = claim.parent == null ? "claim" : "subclaim";
				if(!RealEstate.perms.has(player, "realestate." + type + ".sell"))
				{
					player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You don't have the permission to sell " + type + "s!");
	                event.setCancelled(true);
	                return;
				}
				if(event.getLine(2).isEmpty())// if no price precised, make it the default one
				{
					event.setLine(2, Double.toString(RealEstate.instance.dataStore.cfgPriceSellPerBlock * claim.getArea()));
				}
				// check for a valid price
				double price;
				try
				{
					price = Double.parseDouble(event.getLine(2));
				}
				catch (NumberFormatException e)
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "The price you entered is not a valid number!");
	                event.setCancelled(true);
	                return;
				}
				if(price <= 0)
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "The price must be greater than 0!");
	                event.setCancelled(true);
	                return;
				}
				
				if(claim.isAdminClaim() && !RealEstate.perms.has(player, "realestate.admin"))// admin may sell admin claims
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You don't have the permission to sell admin claims!");
	                event.setCancelled(true);
	                return;
				}
				else if(type.equals("claim") && !player.getName().equalsIgnoreCase(claim.getOwnerName()))// only the owner may sell his claim
				{
	                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You can only sell claims you own!");
	                event.setCancelled(true);
	                return;
				}
				
				// we should be good to sell it now
				event.setCancelled(true);// need to cancel the event, so we can update the sign elsewhere
				RealEstate.transactionsStore.sell(claim, player, price, event.getBlock().getLocation());
			}
		}
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
			Transaction tr = RealEstate.transactionsStore.getTransaction(claim);
			if(tr.getHolder() == event.getBlock() && event.getPlayer() != null && !tr.getOwner().equals(event.getPlayer().getUniqueId()) && 
					!RealEstate.perms.has(event.getPlayer(), "realestate.destroysigns"))
			{
				event.getPlayer().sendMessage(RealEstate.instance.dataStore.chatPrefix + 
						ChatColor.RED + "Only the author of the sell/rent sign is allowed to destroy it");
				event.setCancelled(true);
				return;
			}
			// the sign has been destroy, we can remove the transaction
			RealEstate.transactionsStore.cancelTransaction(claim);
		}
	}
}
