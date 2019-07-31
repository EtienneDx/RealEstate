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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.PluginManager;

import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class REListener implements Listener
{
	void registerEvents()
	{
		PluginManager pm = RealEstate.instance.getServer().getPluginManager();

		pm.registerEvents(this, RealEstate.instance);
		//RealEstate.instance.getCommand("re").setExecutor(this);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		if(RealEstate.instance.config.cfgSellKeywords.contains(event.getLine(0).toLowerCase()) || 
				RealEstate.instance.config.cfgLeaseKeywords.contains(event.getLine(0).toLowerCase()) || 
				RealEstate.instance.config.cfgRentKeywords.contains(event.getLine(0).toLowerCase()))
		{
			Player player = event.getPlayer();
			Location loc = event.getBlock().getLocation();

			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
			if(claim == null)// must have something to sell
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "The sign you placed is not inside a claim!");
				event.setCancelled(true);
				event.getBlock().breakNaturally();
				return;
			}
			if(RealEstate.transactionsStore.anyTransaction(claim))
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "This claim already has an ongoing transaction!");
				event.setCancelled(true);
				event.getBlock().breakNaturally();
				return;
			}
			if(RealEstate.transactionsStore.anyTransaction(claim.parent))
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "The parent claim already has an ongoing transaction!");
				event.setCancelled(true);
				event.getBlock().breakNaturally();
				return;
			}
			for(Claim c : claim.children)
			{
				if(RealEstate.transactionsStore.anyTransaction(c))
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
							"A subclaim of this claim already has an ongoing transaction!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
			}

			// empty is considered a wish to sell
			if(RealEstate.instance.config.cfgSellKeywords.contains(event.getLine(0).toLowerCase()))
			{
				if(!RealEstate.instance.config.cfgEnableSell)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Selling is disabled!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				String type = claim.parent == null ? "claim" : "subclaim";
				if(!RealEstate.perms.has(player, "realestate." + type + ".sell"))
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You don't have the permission to sell " + type + "s!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				// check for a valid price
				double price;
				try
				{
					price = getDouble(event, 1, RealEstate.instance.config.cfgPriceSellPerBlock * claim.getArea());
				}
				catch (NumberFormatException e)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "The price you entered is not a valid number!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				if(price <= 0)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "The price must be greater than 0!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				if(claim.isAdminClaim() && !RealEstate.perms.has(player, "realestate.admin"))// admin may sell admin claims
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You don't have the permission to sell admin claims!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				else if(type.equals("claim") && !player.getUniqueId().equals(claim.ownerID))// only the owner may sell his claim
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You can only sell claims you own!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				// we should be good to sell it now
				event.setCancelled(true);// need to cancel the event, so we can update the sign elsewhere
				RealEstate.transactionsStore.sell(claim, player, price, event.getBlock().getLocation());
			}
			else if(RealEstate.instance.config.cfgRentKeywords.contains(event.getLine(0).toLowerCase()))// we want to rent it
			{
				if(!RealEstate.instance.config.cfgEnableRent)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Renting is disabled!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				String type = claim.parent == null ? "claim" : "subclaim";
				if(!RealEstate.perms.has(player, "realestate." + type + ".rent"))
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You don't have the permission to rent " + type + "s!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				// check for a valid price
				double price;
				try
				{
					price = getDouble(event, 1, RealEstate.instance.config.cfgPriceRentPerBlock * claim.getArea());
				}
				catch (NumberFormatException e)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "The price you entered is not a valid number!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				if(price <= 0)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "The price must be greater than 0!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				if(event.getLine(2).isEmpty())
				{
					event.setLine(2, RealEstate.instance.config.cfgRentTime);
				}
				int duration = parseDuration(event.getLine(2));
				if(duration == 0)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Couldn't read the date!\n" + 
							"Date must be formatted as follow : " + ChatColor.GREEN + "10 weeks" + ChatColor.RED + " or " + 
							ChatColor.GREEN + "3 days" + ChatColor.RED + " or " +  ChatColor.GREEN + "1 week 3 days");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				int rentPeriods = 1;
				if(RealEstate.instance.config.cfgEnableRentPeriod)
				{
					if(event.getLine(3).isEmpty())
					{
						event.setLine(3, "1");
					}
					try
					{
						rentPeriods = Integer.parseInt(event.getLine(3));
					}
					catch (NumberFormatException e)
					{
						player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
								"The number of rent periods you entered is not a valid number!");
						event.setCancelled(true);
						event.getBlock().breakNaturally();
						return;
					}
					if(rentPeriods <= 0)
					{
						player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
								"The number of rent periods must be greater than 0!");
						event.setCancelled(true);
						event.getBlock().breakNaturally();
						return;
					}
				}

				if(claim.isAdminClaim() && !RealEstate.perms.has(player, "realestate.admin"))// admin may rent admin claims
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You don't have the permission to rent admin claims!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				else if(type.equals("claim") && !player.getUniqueId().equals(claim.ownerID))// only the owner may sell his claim
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You can only rent claims you own!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				// all should be good, we can create the rent
				event.setCancelled(true);
				RealEstate.transactionsStore.rent(claim, player, price, event.getBlock().getLocation(), duration, rentPeriods);
			}
			else if(RealEstate.instance.config.cfgLeaseKeywords.contains(event.getLine(0).toLowerCase()))// we want to rent it
			{
				if(!RealEstate.instance.config.cfgEnableLease)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Leasing is disabled!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				String type = claim.parent == null ? "claim" : "subclaim";
				if(!RealEstate.perms.has(player, "realestate." + type + ".lease"))
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You don't have the permission to lease " + type + "s!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				// check for a valid price
				double price;
				try
				{
					price = getDouble(event, 1, RealEstate.instance.config.cfgPriceLeasePerBlock * claim.getArea());
				}
				catch (NumberFormatException e)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "The price you entered is not a valid number!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				if(price <= 0)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "The price must be greater than 0!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				if(event.getLine(2).isEmpty())
				{
					event.setLine(2, "" + RealEstate.instance.config.cfgLeasePayments);
				}
				int paymentsCount;
				try
				{
					paymentsCount = Integer.parseInt(event.getLine(2));
				}
				catch(Exception e)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
							"The number of payments you enterred is not a valid number!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				if(event.getLine(3).isEmpty())
				{
					event.setLine(3, RealEstate.instance.config.cfgLeaseTime);
				}
				int frequency = parseDuration(event.getLine(3));
				if(frequency == 0)
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Couldn't read the date!\n" + 
							"Date must be formatted as follow" + ChatColor.GREEN + "10 weeks" + ChatColor.RED + " or " + 
							ChatColor.GREEN + "3 days" + ChatColor.RED + " or " +  ChatColor.GREEN + "1 week 3 days");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				if(claim.isAdminClaim() && !RealEstate.perms.has(player, "realestate.admin"))// admin may rent admin claims
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You don't have the permission to lease admin claims!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}
				else if(type.equals("claim") && !player.getUniqueId().equals(claim.ownerID))// only the owner may sell his claim
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You can only lease claims you own!");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return;
				}

				// all should be good, we can create the rent
				event.setCancelled(true);
				RealEstate.transactionsStore.lease(claim, player, price, event.getBlock().getLocation(), frequency, paymentsCount);
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
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.HAND) &&
				event.getClickedBlock().getState() instanceof Sign)
		{
			Sign sign = (Sign)event.getClickedBlock().getState();
			// it is a real estate sign
			if(ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase(ChatColor.stripColor(RealEstate.instance.config.cfgSignsHeader)))
			{
				Player player = event.getPlayer();
				Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getClickedBlock().getLocation(), false, null);

				if(!RealEstate.transactionsStore.anyTransaction(claim))
				{
					player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
							"This claim is no longer for rent, sell or lease, sorry...");
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
					if(event.getPlayer() != null && tr.getOwner() != null  && !event.getPlayer().getUniqueId().equals(tr.getOwner()) && 
							!RealEstate.perms.has(event.getPlayer(), "realestate.destroysigns"))
					{
						event.getPlayer().sendMessage(RealEstate.instance.config.chatPrefix + 
								ChatColor.RED + "Only the author of the sell/rent/lease sign is allowed to destroy it");
						event.setCancelled(true);
						return;
					}
					else if(event.getPlayer() != null && tr.getOwner() == null && !RealEstate.perms.has(event.getPlayer(), "realestate.admin"))
					{
						event.getPlayer().sendMessage(RealEstate.instance.config.chatPrefix + 
								ChatColor.RED + "Only an admin is allowed to destroy this sign");
						event.setCancelled(true);
						return;
					}
					// the sign has been destroy, we can try to cancel the transaction
					if(!tr.tryCancelTransaction(event.getPlayer()))
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
