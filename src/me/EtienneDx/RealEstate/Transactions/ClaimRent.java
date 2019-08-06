package me.EtienneDx.RealEstate.Transactions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.md_5.bungee.api.ChatColor;

public class ClaimRent extends BoughtTransaction
{
	LocalDateTime startDate = null;
	int duration;
	public boolean autoRenew = false;
	public int periodCount = 0;
	public int maxPeriod;
	
	public ClaimRent(Map<String, Object> map)
	{
		super(map);
		if(map.get("startDate") != null)
			startDate = LocalDateTime.parse((String) map.get("startDate"), DateTimeFormatter.ISO_DATE_TIME);
		duration = (int)map.get("duration");
		autoRenew = (boolean) map.get("autoRenew");
		periodCount = (int) map.get("periodCount");
		maxPeriod = (int) map.get("maxPeriod");
	}
	
	public ClaimRent(Claim claim, Player player, double price, Location sign, int duration, int rentPeriods)
	{
		super(claim, player, price, sign);
		this.duration = duration;
		this.maxPeriod = RealEstate.instance.config.cfgEnableRentPeriod ? rentPeriods : 1;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();

		if(startDate != null)
			map.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE_TIME));
		map.put("duration", duration);
		map.put("autoRenew",  autoRenew);
		map.put("periodCount", periodCount);
		map.put("maxPeriod", maxPeriod);
		
		return map;
	}

	@Override
	public void update()
	{
		if(buyer == null)
		{
			if(destroyedSign)
			{
				RealEstate.transactionsStore.cancelTransaction(this);
			}
			else if(sign.getBlock().getState() instanceof Sign)
			{
				Sign s = (Sign) sign.getBlock().getState();
				s.setLine(0, RealEstate.instance.config.cfgSignsHeader);
				s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceRent);
				//s.setLine(2, owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "SERVER");
				if(RealEstate.instance.config.cfgUseCurrencySymbol)
				{
					s.setLine(2, RealEstate.instance.config.cfgCurrencySymbol + " " + price);
				}
				else
				{
					s.setLine(2, price + " " + RealEstate.econ.currencyNamePlural());
				}
				s.setLine(3, (maxPeriod > 1 ? maxPeriod + "x " : "") + Utils.getTime(duration, null, false));
				s.update(true);
			}
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
				payRent();
			}
			else if(sign.getBlock().getState() instanceof Sign)
			{
				Sign s = (Sign) sign.getBlock().getState();
				s.setLine(0, RealEstate.instance.config.cfgSignsHeader);
				s.setLine(1, Utils.getSignString("Rented by " + Bukkit.getOfflinePlayer(buyer).getName()));
				s.setLine(2, "Time remaining : ");
				
				int daysLeft = duration - days - 1;// we need to remove the current day
				Duration timeRemaining = Duration.ofHours(24).minus(hours);
				
				s.setLine(3, Utils.getTime(daysLeft, timeRemaining, false));
				s.update(true);
			}
		}
		
	}

	private void unRent(boolean msgBuyer)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
		claim.dropPermission(buyer.toString());
		GriefPrevention.instance.dataStore.saveClaim(claim);
		if(msgBuyer && Bukkit.getOfflinePlayer(buyer).isOnline() && RealEstate.instance.config.cfgMessageBuyer)
		{
			Bukkit.getPlayer(buyer).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
					"The rent for the " + (claim.parent == null ? "claim" : "subclaim") + " at " + ChatColor.BLUE + "[" + 
					sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
					sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + ChatColor.AQUA + " is now over, your access has been revoked.");
		}
		buyer = null;
		RealEstate.transactionsStore.saveData();
		update();
	}

	private void payRent()
	{
		if(buyer == null) return;

		OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(this.buyer);
		OfflinePlayer seller = owner == null ? null : Bukkit.getOfflinePlayer(owner);
		
		String claimType = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null).parent == null ? "claim" : "subclaim";
		
		if((autoRenew || periodCount < maxPeriod) && Utils.makePayment(owner, this.buyer, price, false, false))
		{
			periodCount = (periodCount + 1) % maxPeriod;
			startDate = LocalDateTime.now();
			if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
			{
				((Player)buyerPlayer).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
						"Paid rent for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + 
						", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
						ChatColor.AQUA + "for the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
        	{
        		User u = RealEstate.ess.getUser(this.buyer);
        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
						"Paid rent for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + 
						", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
						ChatColor.AQUA + "for the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
        	}
			
			if(seller != null)
			{
				if(seller.isOnline() && RealEstate.instance.config.cfgMessageOwner)
				{
					((Player)seller).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + buyerPlayer.getName() + 
							" has paid rent for the " + claimType + " at " + ChatColor.BLUE + "[" + 
							sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
							sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
							ChatColor.AQUA + "at the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.owner);
	        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + buyerPlayer.getName() + 
							" has paid rent for the " + claimType + " at " + ChatColor.BLUE + "[" + 
							sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
							sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
							ChatColor.AQUA + "at the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
	        	}
			}
			
		}
		else if (autoRenew)
		{
			if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
			{
				((Player)buyerPlayer).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"Couldn't pay the rent for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + 
						sign.getBlockX() + ", Y: " + 
						sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + ChatColor.RED + ", your access has been revoked.");
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
        	{
        		User u = RealEstate.ess.getUser(this.buyer);
        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"Couldn't pay the rent for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + 
						sign.getBlockX() + ", Y: " + 
						sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + ChatColor.RED + ", your access has been revoked.");
        	}
			unRent(false);
			return;
		}
		else
		{
			unRent(true);
			return;
		}
		update();
		RealEstate.transactionsStore.saveData();
	}
	
	@Override
	public boolean tryCancelTransaction(Player p)
	{
		if(buyer != null)
		{
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
			if(p != null)
				p.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "This " + (claim.parent == null ? "claim" : "subclaim") + 
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
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "This claim does not exist!");
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		String claimType = claim.parent == null ? "claim" : "subclaim";
		
		if (owner.equals(player.getUniqueId()))
        {
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You already own this " + claimType + "!");
            return;
        }
		if(claim.parent == null && !owner.equals(claim.ownerID))
		{
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + Bukkit.getPlayer(owner).getDisplayName() + 
            		" does not have the right to rent this " + claimType + "!");
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		if(!player.hasPermission("realestate." + claimType + ".rent"))
		{
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You do not have the permission to rent " + 
            		claimType + "s!");
            return;
		}
		if(player.getUniqueId().equals(buyer))
		{
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You are already renting this " + 
            		claimType + "!");
            return;
		}
		if(buyer != null)
		{
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Someone already rents this " + 
            		claimType + "!");
            return;
		}
		
		if(Utils.makePayment(owner, player.getUniqueId(), price, false, true))// if payment succeed
		{
			buyer = player.getUniqueId();
			startDate = LocalDateTime.now();
			autoRenew = false;
			claim.setPermission(buyer.toString(), ClaimPermission.Build);
			GriefPrevention.instance.dataStore.saveClaim(claim);
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
			if(RealEstate.instance.config.cfgMessageOwner && seller.isOnline())
			{
				((Player)seller).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + ChatColor.AQUA + 
						" has just rented your " + claimType + " at " +
						ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + sign.getBlockY() + ", Z: "
						+ sign.getBlockZ() + "]" + ChatColor.AQUA +
                        " for " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
        	{
        		User u = RealEstate.ess.getUser(this.owner);
        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + ChatColor.AQUA + 
						" has just rented your " + claimType + " at " +
						ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + sign.getBlockY() + ", Z: "
						+ sign.getBlockZ() + "]" + ChatColor.AQUA +
                        " for " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
        	}
			
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "You have successfully rented this " + claimType + 
					" for " + ChatColor.GREEN + price + RealEstate.econ.currencyNamePlural());
			
			destroySign();
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
			if(buyer == null)
			{
				msg += ChatColor.AQUA + "This " + claimType + " is for rent for " +
						ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + " for " + 
						(maxPeriod > 1 ? "" + ChatColor.GREEN + maxPeriod + ChatColor.AQUA + " periods of " : "") +
						ChatColor.GREEN + Utils.getTime(duration, null, true) + "\n";
				
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
						ChatColor.GREEN + Bukkit.getOfflinePlayer(buyer).getName() + ChatColor.AQUA + " for " +
						ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + " for " + 
						(maxPeriod - periodCount > 1 ? "" + ChatColor.GREEN + (maxPeriod - periodCount) + ChatColor.AQUA + " periods of " + 
						ChatColor.GREEN + Utils.getTime(duration, null, false) + ChatColor.AQUA + ". The current period will end in " : "another ") +
						ChatColor.GREEN + Utils.getTime(daysLeft, timeRemaining, true) + "\n";
				if((owner.equals(player.getUniqueId()) || buyer.equals(player.getUniqueId())) && RealEstate.instance.config.cfgEnableAutoRenew)
				{
					msg += ChatColor.AQUA + "Automatic renew is currently " + ChatColor.LIGHT_PURPLE + (autoRenew ? "enabled" : "disabled") + "\n";
				}
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
			msg = RealEstate.instance.config.chatPrefix + ChatColor.RED + "You don't have the permission to view real estate informations!";
		}
		player.sendMessage(msg);
	}

}
