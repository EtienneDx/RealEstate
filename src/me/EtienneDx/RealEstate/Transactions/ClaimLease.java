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

import me.EtienneDx.RealEstate.Messages;
import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.Utils;
import me.EtienneDx.RealEstate.RealEstateSign;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
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
	
	public ClaimLease(IClaim claim, Player player, double price, Location sign, int frequency, int paymentsLeft)
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
				RealEstateSign s = new RealEstateSign((Sign) sign.getBlock().getState());
				s.setLine(0, Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));
				s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceLease);
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
		
		String claimType = RealEstate.claimAPI.getClaimAt(sign).isParentClaim() ?
				RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
		String location = "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + 
				", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]";
		
		if(Utils.makePayment(owner, buyer, price, false, false))
		{
			lastPayment = LocalDateTime.now();
			paymentsLeft--;
			if(paymentsLeft > 0)
			{
				if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
				{
					Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyer, 
							claimType,
							location, 
							RealEstate.econ.format(price), 
							paymentsLeft + "");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.buyer);
					u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyer, 
							claimType,
							location, 
							RealEstate.econ.format(price), 
							paymentsLeft + ""));
	        	}
				
				if(owner != null)
				{
					if(seller != null && seller.isOnline() && RealEstate.instance.config.cfgMessageOwner)
					{
						Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwner, 
								buyerPlayer.getName(),
								claimType,
								location, 
								RealEstate.econ.format(price), 
								paymentsLeft + "");
					}
					else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
		        	{
		        		User u = RealEstate.ess.getUser(this.owner);
						u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwner,
								buyerPlayer.getName(),
								claimType,
								location,
								RealEstate.econ.format(price),
								paymentsLeft + ""));
		        	}
				}
			}
			else
			{
				if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
				{
					Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyerFinal, 
							claimType,
							location, 
							RealEstate.econ.format(price));
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.buyer);
					u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyerFinal,
							claimType,
							location,
							RealEstate.econ.format(price)));
	        	}
				
				if(seller != null && seller.isOnline() && RealEstate.instance.config.cfgMessageOwner)
				{
					Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwnerFinal, 
							buyerPlayer.getName(),
							claimType,
							location, 
							RealEstate.econ.format(price));
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.owner);
					u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwnerFinal,
							buyerPlayer.getName(),
							claimType,
							location,
							RealEstate.econ.format(price)));
	        	}
				IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
				
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
			
			IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
			
			String claimType = claim.isParentClaim() ? 
					RealEstate.instance.messages.keywordClaim :
					RealEstate.instance.messages.keywordSubclaim;
			String location = "[" + sign.getWorld().getName() + ", X: " + 
					sign.getBlockX() + ", Y: " + 
					sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]";
			
			if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
			{
				Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyerCancelled, 
						claimType,
						location, 
						RealEstate.econ.format(price));
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	    	{
	    		User u = RealEstate.ess.getUser(this.buyer);
				u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyerCancelled,
						claimType,
						location,
						RealEstate.econ.format(price)));
	    	}
			if(seller != null && seller.isOnline() && RealEstate.instance.config.cfgMessageOwner)
			{
				Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwnerCancelled, 
						buyerPlayer.getName(),
						claimType,
						location, 
						RealEstate.econ.format(price));
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	    	{
	    		User u = RealEstate.ess.getUser(this.owner);
				u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwnerCancelled,
						buyerPlayer.getName(),
						claimType,
						location,
						RealEstate.econ.format(price)));
	    	}
			
			claim.removeManager(buyer);
			claim.dropPlayerPermissions(buyer);
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
				IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
				if(p != null) {
					Messages.sendMessage(p, RealEstate.instance.messages.msgErrorCantCancelAlreadyLeased,
						claim.isParentClaim() ?
							RealEstate.instance.messages.keywordClaim :
							RealEstate.instance.messages.keywordSubclaim
						);
				}
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
		IClaim claim = RealEstate.claimAPI.getClaimAt(sign);// getting by id creates errors for subclaims
		if(claim == null || claim.isWilderness())
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimDoesNotExist);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		String claimType = claim.isParentClaim() ? "claim" : "subclaim";
		String claimTypeDisplay = claim.isParentClaim() ? 
			RealEstate.instance.messages.keywordClaim :
			RealEstate.instance.messages.keywordSubclaim;
		
		if (owner != null && owner.equals(player.getUniqueId()))
        {
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyOwner, claimTypeDisplay);
            return;
        }
		if(claim.isParentClaim() && owner != null && !owner.equals(claim.getOwner()))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNotLeasedByOwner, claimTypeDisplay);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		if(!player.hasPermission("realestate." + claimType + ".lease"))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoLeasePermission, claimTypeDisplay);
            return;
		}
		if(player.getUniqueId().equals(buyer) || buyer != null)
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyLeased, claimTypeDisplay);
            return;
		}
		
		if(Utils.makePayment(owner, player.getUniqueId(), price, false, true))// if payment succeed
		{
			buyer = player.getUniqueId();
			lastPayment = LocalDateTime.now();
			paymentsLeft--;
			claim.addPlayerPermissions(buyer, ClaimPermission.BUILD);
			claim.addPlayerPermissions(player.getUniqueId(), ClaimPermission.MANAGE);
			RealEstate.claimAPI.saveClaim(claim);
			getHolder().breakNaturally();// leases don't have signs indicating the remaining time
			update();
			RealEstate.transactionsStore.saveData();

			String location = "[" + player.getLocation().getWorld() + ", " +
				"X: " + player.getLocation().getBlockX() + ", " +
				"Y: " + player.getLocation().getBlockY() + ", " +
				"Z: " + player.getLocation().getBlockZ() + "]";
			
			RealEstate.instance.addLogEntry(
                    "[" + RealEstate.transactionsStore.dateFormat.format(RealEstate.transactionsStore.date) + "] " + player.getName() + 
                    " has started leasing a " + claimType + " at " +
                    location +
                    " Price: " + price + " " + RealEstate.econ.currencyNamePlural());

			if(owner != null)
			{
				OfflinePlayer seller = Bukkit.getOfflinePlayer(owner);
				if(RealEstate.instance.config.cfgMessageOwner && seller.isOnline())
				{
					Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimOwnerLeaseStarted,
						player.getName(),
						claimTypeDisplay,
						RealEstate.econ.format(price),
						location,
						paymentsLeft + "");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.owner);
	        		u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimOwnerLeaseStarted,
						player.getName(),
						claimTypeDisplay,
						RealEstate.econ.format(price),
						location,
						paymentsLeft + ""));
	        	}
			}
			
			Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimBuyerLeaseStarted,
					claimTypeDisplay,
					RealEstate.econ.format(price),
					paymentsLeft + "");
		}
	}

	@Override
	public void preview(Player player)
	{
		IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
		if(player.hasPermission("realestate.info"))
		{
			String claimType = claim.isParentClaim() ? "claim" : "subclaim";
			String claimTypeDisplay = claim.isParentClaim() ? 
				RealEstate.instance.messages.keywordClaim :
				RealEstate.instance.messages.keywordSubclaim;
			String msg;
			msg = Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeaseHeader) + "\n";
			if(buyer == null)
			{
				msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoGeneralLeaseNoBuyer,
						claimTypeDisplay,
						paymentsLeft + "",
						RealEstate.econ.format(price),
						Utils.getTime(frequency, null, true)) + "\n";

				if(claimType.equalsIgnoreCase("claim"))
				{
					msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoOwner,
							claim.getOwnerName()) + "\n";
	            }
	            else
	            {
					msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoMainOwner,
	            			claim.getParent().getOwnerName()) + "\n";
					msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoNote) + "\n";
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
				
				msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoGeneralLeaseBuyer,
						claimTypeDisplay,
						Bukkit.getOfflinePlayer(buyer).getName(),
						RealEstate.econ.format(price),
						paymentsLeft + "",
						Utils.getTime(daysLeft, timeRemaining, true),
						Utils.getTime(frequency, null, true)) + "\n";
				if(claimType.equalsIgnoreCase("claim"))
				{
					msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoOwner,
							claim.getOwnerName()) + "\n";
				}
				else
				{
					msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoMainOwner,
							claim.getParent().getOwnerName()) + "\n";
				}
			}
			Messages.sendMessage(player, msg, false);
		}
		else
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoInfoPermission);
		}
	}

	@Override
	public void msgInfo(CommandSender cs)
	{
		IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
		String location = "[" + claim.getWorld().getName() + ", " +
		"X: " + claim.getX() + ", " +
		"Y: " + claim.getY() + ", " +
		"Z: " + claim.getZ() + "]";

		Messages.sendMessage(cs, RealEstate.instance.messages.msgInfoClaimInfoLeaseOneline,
				claim.getArea() + "",
				location,
				RealEstate.econ.format(price),
				paymentsLeft + "");
	}
	public int getFrequency() {
	    return this.frequency;
	}

	public int getPaymentsLeft() {
	    return this.paymentsLeft;
	}

}
