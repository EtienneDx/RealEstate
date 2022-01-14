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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.md_5.bungee.api.ChatColor;

public class ClaimLease extends BoughtTransaction
{
	public LocalDateTime lastPayment = null;
	public int frequency;
	public int paymentsLeft;
	
	public ClaimLease(Map<String, Object> map)
	{
		super(map);
		if(map.get("lastPayment") != null)
			lastPayment = LocalDateTime.parse((String) map.get("lastPayment"), DateTimeFormatter.ISO_DATE_TIME);
		frequency = (int)map.get("frequency");
		paymentsLeft = (int)map.get("paymentsLeft");
	}
	
	public ClaimLease(Claim claim, Player player, double price, Location sign, int frequency, int paymentsLeft)
	{
		super(claim, player, price, sign);
		this.frequency = frequency;
		this.paymentsLeft = paymentsLeft;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();

		if(lastPayment != null)
			map.put("lastPayment", lastPayment.format(DateTimeFormatter.ISO_DATE_TIME));
		map.put("frequency", frequency);
		map.put("paymentsLeft", paymentsLeft);
		
		return map;
	}
	
	@Override
	public boolean update()
	{
		if(buyer == null)// not yet leased
		{
			if(sign.getBlock().getState() instanceof Sign)
			{
				Sign s = (Sign)sign.getBlock().getState();
				s.setLine(0, RealEstate.instance.messages.getMessage(RealEstate.instance.config.cfgSignsHeader));
				s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceLease);
				//s.setLine(2, owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "SERVER");
				//s.setLine(2, paymentsLeft + "x " + price + " " + RealEstate.econ.currencyNamePlural());
				if(RealEstate.instance.config.cfgUseCurrencySymbol)
				{
					if(RealEstate.instance.config.cfgUseDecimalCurrency == false)
					{
						s.setLine(2, paymentsLeft + "x " + RealEstate.instance.config.cfgCurrencySymbol + " " + (int)Math.round(price));
					}
					else
					{
						s.setLine(2, paymentsLeft + "x " + RealEstate.instance.config.cfgCurrencySymbol + " " + price);
					}
				}
				else
				{
					if(RealEstate.instance.config.cfgUseDecimalCurrency == false)
					{
						s.setLine(2, paymentsLeft + "x " + (int)Math.round(price) + " " + RealEstate.econ.currencyNamePlural());
					}
					else
					{
						s.setLine(2, paymentsLeft + "x " + price + " " + RealEstate.econ.currencyNamePlural());
					}
				}
				s.setLine(3, Utils.getTime(frequency, null, false));
				s.update(true);
			}
			else
			{
				return true;
			}
			
		}
		else
		{
			int days = Period.between(lastPayment.toLocalDate(), LocalDate.now()).getDays();
			Duration hours = Duration.between(lastPayment.toLocalTime(), LocalTime.now());
			if(hours.isNegative() && !hours.isZero())
	        {
	            hours = hours.plusHours(24);
	            days--;
	        }
			if(days >= frequency)// we exceeded the time limit!
			{
				payLease();
			}
		}
		return false;
	}

	private void payLease()
	{
		if(buyer == null) return;

		OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(buyer);
		OfflinePlayer seller = owner == null ? null : Bukkit.getOfflinePlayer(owner);
		
		String claimType = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null).parent == null ? "claim" : "subclaim";
		
		if(Utils.makePayment(owner, buyer, price, false, false))
		{
			lastPayment = LocalDateTime.now();
			paymentsLeft--;
			if(paymentsLeft > 0)
			{
				if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
				{
					((Player)buyerPlayer).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
							"Paid lease for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + 
							", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
							ChatColor.AQUA + " for the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + 
							ChatColor.AQUA + ", " + ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " payments left");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.buyer);
	        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
							"Paid lease for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + 
							", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
							ChatColor.AQUA + " for the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + 
							ChatColor.AQUA + ", " + ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " payments left");
	        	}
				
				if(owner != null)
				{
					if(seller.isOnline() && RealEstate.instance.config.cfgMessageOwner)
					{
						((Player)seller).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + buyerPlayer.getName() + 
								ChatColor.AQUA + " has paid lease for the " + claimType + " at " + ChatColor.BLUE + "[" + 
								sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
								sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
								ChatColor.AQUA + " at the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + 
								ChatColor.AQUA + ", " + ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " payments left");
					}
					else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
		        	{
		        		User u = RealEstate.ess.getUser(this.owner);
		        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + buyerPlayer.getName() + 
								ChatColor.AQUA + " has paid lease for the " + claimType + " at " + ChatColor.BLUE + "[" + 
								sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
								sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
								ChatColor.AQUA + " at the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + 
								ChatColor.AQUA + ", " + ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " payments left");
		        	}
				}
			}
			else
			{
				if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
				{
					((Player)buyerPlayer).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
							"Paid final lease for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + 
							", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
							ChatColor.AQUA + " for the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + 
							ChatColor.AQUA + ", the " + claimType + " is now yours");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.buyer);
	        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
							"Paid final lease for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + 
							", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
							ChatColor.AQUA + " for the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + 
							ChatColor.AQUA + ", the " + claimType + " is now yours");
	        	}
				
				if(seller.isOnline() && RealEstate.instance.config.cfgMessageOwner)
				{
					((Player)seller).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + buyerPlayer.getName() + 
							ChatColor.AQUA + " has paid lease for the " + claimType + " at " + ChatColor.BLUE + "[" + 
							sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
							sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
							ChatColor.AQUA + "at the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + 
							ChatColor.AQUA + ", the " + claimType + " is now his property");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.owner);
	        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + buyerPlayer.getName() + 
							ChatColor.AQUA + " has paid lease for the " + claimType + " at " + ChatColor.BLUE + "[" + 
							sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
							sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
							ChatColor.AQUA + "at the price of " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + 
							ChatColor.AQUA + ", the " + claimType + " is now his property");
	        	}
				Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
				
				Utils.transferClaim(claim, buyer, owner);
				RealEstate.transactionsStore.cancelTransaction(this);// the transaction is finished
			}
		}
		else
		{
			this.exitLease();
		}
		// no need to re update, since there's no sign 
		RealEstate.transactionsStore.saveData();
	}
	
	private void exitLease()
	{
		if(buyer != null)
		{
			OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(buyer);
			OfflinePlayer seller = owner == null ? null : Bukkit.getOfflinePlayer(owner);
			
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
			
			String claimType = claim.parent == null ? "claim" : "subclaim";
			
			if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
			{
				((Player)buyerPlayer).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"Couldn't pay the lease for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + 
						sign.getBlockX() + ", Y: " + 
						sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + ChatColor.RED + ", the transaction has been cancelled.");
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	    	{
	    		User u = RealEstate.ess.getUser(this.buyer);
	    		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"Couldn't pay the lease for the " + claimType + " at " + ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + 
						sign.getBlockX() + ", Y: " + 
						sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + ChatColor.RED + ", the transaction has been cancelled.");
	    	}
			if(seller.isOnline() && RealEstate.instance.config.cfgMessageOwner)
			{
				((Player)seller).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + buyerPlayer.getName() + 
						ChatColor.AQUA + " couldn't pay lease for the " + claimType + " at " + ChatColor.BLUE + "[" + 
						sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
						sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
						ChatColor.AQUA + ", the transaction has been cancelled");
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	    	{
	    		User u = RealEstate.ess.getUser(this.owner);
	    		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + buyerPlayer.getName() + 
						ChatColor.AQUA + " couldn't pay lease for the " + claimType + " at " + ChatColor.BLUE + "[" + 
						sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
						sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]" + 
						ChatColor.AQUA + ", the transaction has been cancelled");
	    	}
			
			claim.managers.remove(buyer.toString());
			claim.dropPermission(buyer.toString());
		}
		else
		{
			getHolder().breakNaturally();// the sign should still be there since the lease has netver begun
		}
		RealEstate.transactionsStore.cancelTransaction(this);
	}

	@Override
	public boolean tryCancelTransaction(Player p, boolean force)
	{
		if(buyer != null)
		{
			if(p.hasPermission("realestate.admin") && force == true)
			{
				this.exitLease();
				return true;
			}
			else
			{
				Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
				if(p != null)
					p.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "This " + (claim.parent == null ? "claim" : "subclaim") + 
	            		" is currently leased, you can't cancel the transaction!");
	            return false;
			}
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
		
		if (owner != null && owner.equals(player.getUniqueId()))
        {
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You already own this " + claimType + "!");
            return;
        }
		if(claim.parent == null && owner != null && !owner.equals(claim.ownerID))
		{
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + Bukkit.getPlayer(owner).getDisplayName() + 
            		" does not have the right to put this " + claimType + " for lease!");
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		if(!player.hasPermission("realestate." + claimType + ".lease"))
		{
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You do not have the permission to lease " + 
            		claimType + "s!");
            return;
		}
		if(player.getUniqueId().equals(buyer))
		{
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You are already leasing this " + 
            		claimType + "!");
            return;
		}
		if(buyer != null)
		{
            player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Someone already leases this " + 
            		claimType + "!");
            return;
		}
		
		if(Utils.makePayment(owner, player.getUniqueId(), price, false, true))// if payment succeed
		{
			buyer = player.getUniqueId();
			lastPayment = LocalDateTime.now();
			paymentsLeft--;
			claim.setPermission(buyer.toString(), ClaimPermission.Build);
			claim.setPermission(player.getUniqueId().toString(), ClaimPermission.Manage);
			GriefPrevention.instance.dataStore.saveClaim(claim);
			getHolder().breakNaturally();// leases don't have signs indicating the remaining time
			update();
			RealEstate.transactionsStore.saveData();
			
			RealEstate.instance.addLogEntry(
                    "[" + RealEstate.transactionsStore.dateFormat.format(RealEstate.transactionsStore.date) + "] " + player.getName() + 
                    " has started leasing a " + claimType + " at " +
                    "[" + player.getLocation().getWorld() + ", " +
                    "X: " + player.getLocation().getBlockX() + ", " +
                    "Y: " + player.getLocation().getBlockY() + ", " +
                    "Z: " + player.getLocation().getBlockZ() + "] " +
                    "Price: " + price + " " + RealEstate.econ.currencyNamePlural());

			if(owner != null)
			{
				OfflinePlayer seller = Bukkit.getOfflinePlayer(owner);
				if(RealEstate.instance.config.cfgMessageOwner && seller.isOnline())
				{
					((Player)seller).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + ChatColor.AQUA + 
							" has just paid for your lease for the " + claimType + " at " +
							ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + sign.getBlockY() + ", Z: "
							+ sign.getBlockZ() + "]" + ChatColor.AQUA +
	                        " for " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + ", " +
	                        ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " payments left");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.owner);
	        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + ChatColor.AQUA + 
							" has just paid for your lease for the " + claimType + " at " +
							ChatColor.BLUE + "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + sign.getBlockY() + ", Z: "
							+ sign.getBlockZ() + "]" + ChatColor.AQUA +
	                        " for " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + ", " +
	                        ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " payments left");
	        	}
			}
			
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "You have successfully paid lease for this " + claimType + 
					" for " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + ", " +
                    ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " payments left");
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
				msg += ChatColor.AQUA + "This " + claimType + " is for lease for " +
						ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " payments of " +
						ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + " each.\nPayments are due every " + 
						ChatColor.GREEN + Utils.getTime(frequency, null, true) + "\n";

				if(claimType.equalsIgnoreCase("claim"))
				{
					msg += ChatColor.AQUA + "The current owner is: " + ChatColor.GREEN + claim.getOwnerName();
	            }
	            else
	            {
	            	msg += ChatColor.AQUA + "The main claim owner is: " + ChatColor.GREEN + claim.getOwnerName() + "\n";
	            	msg += ChatColor.LIGHT_PURPLE + "Note: " + ChatColor.AQUA + "You will only get access to this subclaim!";
	            }
			}
			else
			{
				int days = Period.between(lastPayment.toLocalDate(), LocalDate.now()).getDays();
				Duration hours = Duration.between(lastPayment.toLocalTime(), LocalTime.now());
				if(hours.isNegative() && !hours.isZero())
		        {
		            hours = hours.plusHours(24);
		            days--;
		        }
				int daysLeft = frequency - days - 1;// we need to remove the current day
				Duration timeRemaining = Duration.ofHours(24).minus(hours);
				
				msg += ChatColor.AQUA + "This " + claimType + " is currently leased by " + 
						ChatColor.GREEN + Bukkit.getOfflinePlayer(buyer).getName() + ChatColor.AQUA + " for " +
						ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + ". There is " +
						ChatColor.GREEN + paymentsLeft + ChatColor.AQUA +  " payments left. Next payment is in " + 
						ChatColor.GREEN + Utils.getTime(daysLeft, timeRemaining, true) + ChatColor.AQUA + ".\n";
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

	@Override
	public void msgInfo(CommandSender cs)
	{
		cs.sendMessage(ChatColor.DARK_GREEN + "" + GriefPrevention.instance.dataStore.getClaim(claimId).getArea() + 
				ChatColor.AQUA + " blocks to " + ChatColor.DARK_GREEN + "Lease " + ChatColor.AQUA + "at " + ChatColor.DARK_GREEN + 
				"[" + GriefPrevention.instance.dataStore.getClaim(claimId).getLesserBoundaryCorner().getWorld().getName() + ", " +
                "X: " + GriefPrevention.instance.dataStore.getClaim(claimId).getLesserBoundaryCorner().getBlockX() + ", " +
                "Y: " + GriefPrevention.instance.dataStore.getClaim(claimId).getLesserBoundaryCorner().getBlockY() + ", " +
                "Z: " + GriefPrevention.instance.dataStore.getClaim(claimId).getLesserBoundaryCorner().getBlockZ() + "] " + ChatColor.AQUA + "for " + 
                ChatColor.GREEN + paymentsLeft + ChatColor.AQUA + " periods of " + ChatColor.GREEN + Utils.getTime(frequency, Duration.ZERO, false) +
                ChatColor.AQUA + ", each period costs " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural()
                );
	}

}
