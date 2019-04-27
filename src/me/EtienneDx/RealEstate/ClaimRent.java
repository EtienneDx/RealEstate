package me.EtienneDx.RealEstate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.md_5.bungee.api.ChatColor;

public class ClaimRent extends ClaimTransaction
{
	LocalDateTime startDate = null;
	int duration;
	UUID rentedBy = null;
	boolean autoRenew = false;
	
	public ClaimRent(Map<String, Object> map)
	{
		super(map);
		if(map.get("startDate") != null)
			startDate = LocalDateTime.parse((String) map.get("startDate"), DateTimeFormatter.ISO_DATE_TIME);
		duration = (int)map.get("duration");
		if(map.get("rentedBy") != null)
			rentedBy = UUID.fromString((String)map.get("rentedBy"));
		autoRenew = (boolean) map.get("autoRenew");
	}
	
	public ClaimRent(Claim claim, Player player, double price, Location sign, int duration)
	{
		super(claim, player, price, sign);
		this.duration = duration;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();

		if(startDate != null)
			map.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE_TIME));
		map.put("duration", duration);
		if(rentedBy != null)
			map.put("rentedBy", rentedBy.toString());
		map.put("autoRenew",  autoRenew);
		
		return map;
	}

	@Override
	public void update()
	{
		if(sign.getBlock().getState() instanceof Sign)
		{
			Sign s = (Sign) sign.getBlock().getState();
			if(rentedBy == null)
			{
				s.setLine(0, RealEstate.instance.dataStore.cfgSignsHeader);
				s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.dataStore.cfgReplaceRent);
				//s.setLine(2, owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "SERVER");
				s.setLine(2, price + " " + RealEstate.econ.currencyNamePlural());
				s.setLine(3, Utils.getTime(duration, null, false));
				s.update(true);
			}
			else
			{
				// we want to know how much time has gone by since startDate
				int days = Period.between(startDate.toLocalDate(), LocalDate.now()).getDays();
				Duration hours = Duration.between(startDate.toLocalTime(), LocalTime.now());
				if(hours.isNegative() && !hours.isZero())
		        {
		            hours = hours.plusHours(24);
		            days--;
		        }
				if(days >= duration)// we exceeded the time limit!
				{
					// both functions will call update again to update the sign
					if(autoRenew)
						payRent();
					else
						unRent(true);
				}
				else
				{
					s.setLine(0, RealEstate.instance.dataStore.cfgSignsHeader);
					s.setLine(1, ("Rented by " + Bukkit.getOfflinePlayer(rentedBy).getName()).substring(0, 16));
					s.setLine(2, "Time remaining : ");
					
					int daysLeft = duration - days - 1;// we need to remove the current day
					Duration timeRemaining = Duration.ofHours(24).minus(hours);
					
					s.setLine(3, Utils.getTime(daysLeft, timeRemaining, false));
					s.update(true);
				}
				
			}
		}
		else
		{
			RealEstate.transactionsStore.cancelTransaction(this);
		}
	}

	private void unRent(boolean msgBuyer)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
		claim.dropPermission(rentedBy.toString());
		if(msgBuyer && Bukkit.getOfflinePlayer(rentedBy).isOnline() && RealEstate.instance.dataStore.cfgMessageBuyer)
		{
			Bukkit.getPlayer(rentedBy).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + 
					"The rent for the " + (claim.parent == null ? "claim" : "subclaim") + " at " + ChatColor.BLUE + "[" + 
					sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
					sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + ChatColor.AQUA + " is now over, your access has been revoked.");
		}
		rentedBy = null;
		RealEstate.transactionsStore.saveData();
		update();
	}

	private void payRent()
	{
		if(rentedBy == null) return;

		OfflinePlayer buyer = Bukkit.getOfflinePlayer(rentedBy);
		OfflinePlayer seller = Bukkit.getOfflinePlayer(owner);
		
		String claimType = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null).parent == null ? "claim" : "subclaim";
		
		if(Utils.makePayment(owner, rentedBy, price, false, false))
		{
			startDate = LocalDateTime.now();
			if(buyer.isOnline() && RealEstate.instance.dataStore.cfgMessageBuyer)
			{
				((Player)buyer).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + 
						"Paid rent for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + 
						", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
						ChatColor.AQUA + "for the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
			}
			
			if(seller.isOnline() && RealEstate.instance.dataStore.cfgMessageOwner)
			{
				((Player)seller).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + buyer.getName() + 
						" has paid rent for the " + claimType + " at " + ChatColor.BLUE + "[" + 
						sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
						sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
						ChatColor.AQUA + "at the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
			}
		}
		else
		{
			if(buyer.isOnline() && RealEstate.instance.dataStore.cfgMessageBuyer)
			{
				((Player)buyer).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
						"Couldn't pay the rent for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + 
						sign.getBlockX() + ", Y: " + 
						sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + ChatColor.RED + ", your access has been revoked.");
			}
			unRent(false);
		}
		update();
		RealEstate.transactionsStore.saveData();
	}
	
	@Override
	public boolean tryCancelTransaction(Player p)
	{
		if(rentedBy != null)
		{
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
			if(p != null)
				p.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "This " + (claim.parent == null ? "claim" : "subclaim") + 
            		" is currently rented, you can't cancel the transaction!");
            return false;
		}
		else
		{
			RealEstate.transactionsStore.cancelTransaction(this);
			return true;
		}
	}

	@Override
	public void interact(Player player)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);// getting by id creates errors for subclaims
		if(claim == null)
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "This claim does not exist!");
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		String claimType = claim.parent == null ? "claim" : "subclaim";
		
		if (owner.equals(player.getUniqueId()))
        {
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You already own this " + claimType + "!");
            return;
        }
		if(claim.parent == null && !owner.equals(claim.ownerID))
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + Bukkit.getPlayer(owner).getDisplayName() + 
            		" does not have the right to rent this " + claimType + "!");
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		if(!player.hasPermission("realestate." + claimType + ".rent"))
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You do not have the permission to rent " + 
            		claimType + "s!");
            return;
		}
		if(player.getUniqueId().equals(rentedBy))
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You are already renting this " + 
            		claimType + "!");
            return;
		}
		if(rentedBy != null)
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "Someone already rents this " + 
            		claimType + "!");
            return;
		}
		
		if(Utils.makePayment(owner, player.getUniqueId(), price, false, true))// if payment succeed
		{
			rentedBy = player.getUniqueId();
			startDate = LocalDateTime.now();
			autoRenew = false;
			claim.setPermission(rentedBy.toString(), ClaimPermission.Build);
			update();
			RealEstate.transactionsStore.saveData();
			
			RealEstate.instance.addLogEntry(
                    "[" + RealEstate.transactionsStore.dateFormat.format(RealEstate.transactionsStore.date) + "] " + player.getName() + 
                    " has rented a " + claimType + " at " +
                    "[" + player.getLocation().getWorld() + ", " +
                    "X: " + player.getLocation().getBlockX() + ", " +
                    "Y: " + player.getLocation().getBlockY() + ", " +
                    "Z: " + player.getLocation().getBlockZ() + "] " +
                    "Price: " + price + " " + RealEstate.econ.currencyNamePlural());

			OfflinePlayer seller = Bukkit.getOfflinePlayer(owner);
			if(RealEstate.instance.dataStore.cfgMessageOwner && seller.isOnline())
			{
				((Player)seller).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.GREEN + player.getName() + ChatColor.AQUA + 
						" has just rented your " + claimType + " at " +
                        "[" + sign.getWorld().getName() + ", " +
                        "X: " + sign.getBlockX() + ", " +
                        "Y: " + sign.getBlockY() + ", " +
                        "Z: " + sign.getBlockZ() + "] " +
                        " for " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
			}
			
			player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + "You have successfully rented this " + claimType + 
					" for " + ChatColor.GREEN + price + RealEstate.econ.currencyNamePlural());
		}
	}

	@Override
	public void preview(Player player)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
		String msg = "";
		if(player.hasPermission("realestate.info"))
		{
			String claimType = claim.parent == null ? "claim" : "subclaim";
			msg = ChatColor.BLUE + "-----= " + ChatColor.WHITE + "[" + ChatColor.GOLD + "RealEstate Rent Info" + ChatColor.WHITE + "]" + 
					ChatColor.BLUE + " =-----\n";
			if(rentedBy == null)
			{
				msg += ChatColor.AQUA + "This " + claimType + " is for rent for " +
						ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + " for a duration of " + 
						ChatColor.GREEN + Utils.getTime(duration, null, true) + "\n";
				if(rentedBy.equals(player.getUniqueId()) && RealEstate.instance.dataStore.cfgEnableAutoRenew)
				{
					msg += ChatColor.AQUA + "Automatic renew is currently " + ChatColor.LIGHT_PURPLE + (autoRenew ? "enable" : "disable") + "\n";
				}
				if(claimType.equalsIgnoreCase("claim"))
				{
					msg += ChatColor.AQUA + "The current owner is: " + ChatColor.GREEN + claim.getOwnerName();
	            }
	            else
	            {
	            	msg += ChatColor.AQUA + "The main claim owner is: " + ChatColor.GREEN + claim.getOwnerName() + "\n";
	            	msg += ChatColor.LIGHT_PURPLE + "Note: " + ChatColor.AQUA + "You will only rent access to this subclaim!";
	            }
			}
			else
			{
				int days = Period.between(startDate.toLocalDate(), LocalDate.now()).getDays();
				Duration hours = Duration.between(startDate.toLocalTime(), LocalTime.now());
				if(hours.isNegative() && !hours.isZero())
		        {
		            hours = hours.plusHours(24);
		            days--;
		        }
				int daysLeft = duration - days - 1;// we need to remove the current day
				Duration timeRemaining = Duration.ofHours(24).minus(hours);
				
				msg += ChatColor.AQUA + "This " + claimType + " is currently rented by " + 
						ChatColor.GREEN + Bukkit.getOfflinePlayer(rentedBy).getName() + ChatColor.AQUA + " for " +
						ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + " for another " + 
						ChatColor.GREEN + Utils.getTime(daysLeft, timeRemaining, true) + "\n";
				if(claimType.equalsIgnoreCase("claim"))
				{
					msg += ChatColor.AQUA + "The current owner is: " + ChatColor.GREEN + claim.getOwnerName();
	            }
	            else
	            {
	            	msg += ChatColor.AQUA + "The main claim owner is: " + ChatColor.GREEN + claim.getOwnerName();
	            }
			}
		}
		else
		{
			msg = RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You don't have the permission to view real estate informations!";
		}
		player.sendMessage(msg);
	}

}
