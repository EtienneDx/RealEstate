package me.EtienneDx.RealEstate.Transactions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

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
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class ClaimRent extends BoughtTransaction
{
	LocalDateTime startDate = null;
	int duration;
	public boolean autoRenew = false;
	@SuppressWarnings("UnusedAssignment")
	public boolean buildTrust = true;

	@SuppressWarnings("unused")
	public ClaimRent(Map<String, Object> map)
	{
		super(map);
		if(map.get("startDate") != null)
			startDate = LocalDateTime.parse((String) map.get("startDate"), DateTimeFormatter.ISO_DATE_TIME);
		duration = (int)map.get("duration");
		autoRenew = (boolean) map.get("autoRenew");
		try {
			buildTrust = (boolean) map.get("buildTrust");
		}
		catch (Exception e) {
			buildTrust = true;
		}
	}

	public ClaimRent(IClaim claim, Player player, double price, Location sign, int duration, boolean buildTrust)
	{
		super(claim, player, price, sign);
		this.duration = duration;
		this.buildTrust = buildTrust;
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();

		if(startDate != null)
			map.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE_TIME));
		map.put("duration", duration);
		map.put("autoRenew",  autoRenew);
		map.put("buildTrust", buildTrust);

		return map;
	}

	@Override
	public boolean update()
	{
		if(buyer == null)
		{
			if(sign.getBlock().getState() instanceof Sign s)
			{
				s.setLine(0, Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));
				s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceRent);
				//s.setLine(2, owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "SERVER");
				String price_line;
				if(RealEstate.instance.config.cfgUseCurrencySymbol)
				{
					if(!RealEstate.instance.config.cfgUseDecimalCurrency)
					{
						price_line = RealEstate.instance.config.cfgCurrencySymbol + " " + (int)Math.round(price);
					}
					else
					{
						price_line = RealEstate.instance.config.cfgCurrencySymbol + " " + price;
					}

				}
				else
				{
					if(!RealEstate.instance.config.cfgUseDecimalCurrency)
					{
						price_line = (int)Math.round(price) + " " + RealEstate.econ.currencyNamePlural();
					}
					else
					{
						price_line = price + " " + RealEstate.econ.currencyNamePlural();
					}
				}
				String period = Utils.getTime(duration, null, false);
				if(this.buildTrust) {
					s.setLine(2, price_line);
					s.setLine(3, period);
				} else {
					s.setLine(2, RealEstate.instance.config.cfgContainerRentLine);
					s.setLine(3, price_line + " - " + period);
				}
				s.update(true);
			}
			else
			{
				return true;
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
			else if(sign.getBlock().getState() instanceof Sign s)
			{
				s.setLine(0, ChatColor.GOLD + RealEstate.instance.config.cfgReplaceOngoingRent); //Changed the header to "[Rented]" so that it won't waste space on the next line and allow the name of the player to show underneath.
				s.setLine(1, Utils.getSignString(Objects.requireNonNull(Bukkit.getOfflinePlayer(buyer).getName())));//remove "Rented by"
				s.setLine(2, RealEstate.instance.messages.signTimeremaining);

				int daysLeft = duration - days - 1;// we need to remove the current day
				Duration timeRemaining = Duration.ofHours(24).minus(hours);

				s.setLine(3, Utils.getTime(daysLeft, timeRemaining, false));
				s.update(true);
			}
		}
		return false;

	}

	private void unRent(boolean msgBuyer)
	{
		IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
		claim.dropPlayerPermissions(buyer);
		claim.removeManager(buyer);
		claim.setInheritPermissions(true);
		RealEstate.claimAPI.saveClaim(claim);
		if(msgBuyer && Bukkit.getOfflinePlayer(buyer).isOnline() && RealEstate.instance.config.cfgMessageBuyer)
		{
			String location = "[" + Objects.requireNonNull(sign.getWorld()).getName() + ", X: " + sign.getBlockX() + ", Y: " +
					sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]";
			String claimType = claim.isParentClaim() ?
					RealEstate.instance.messages.keywordClaim :
					RealEstate.instance.messages.keywordSubclaim;

			Messages.sendMessage(Bukkit.getPlayer(buyer), RealEstate.instance.messages.msgInfoClaimInfoRentCancelled,
					claimType,
					location);
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

		String claimType = RealEstate.claimAPI.getClaimAt(sign).isParentClaim() ?
				RealEstate.instance.messages.keywordClaim :
				RealEstate.instance.messages.keywordSubclaim;
		String location = "[" + Objects.requireNonNull(sign.getWorld()).getName() + ", X: " + sign.getBlockX() + ", Y: " +
				sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]";

		if(autoRenew && Utils.makePayment(owner, this.buyer, price, false, false))
		{
			startDate = LocalDateTime.now();
			if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
			{
				Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoRentPaymentBuyer,
						claimType,
						location,
						RealEstate.econ.format(price));
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
        	{
        		User u = RealEstate.ess.getUser(this.buyer);
				u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentPaymentBuyer,
						claimType,
						location,
						RealEstate.econ.format(price)));
        	}

			if(seller != null)
			{
				if(seller.isOnline() && RealEstate.instance.config.cfgMessageOwner)
				{
					Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoRentPaymentOwner,
							buyerPlayer.getName(),
							claimType,
							location,
							RealEstate.econ.format(price));
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.owner);
					u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentPaymentOwner,
							buyerPlayer.getName(),
							claimType,
							location,
							RealEstate.econ.format(price)));
	        	}
			}

		}
		else if (autoRenew)
		{
			if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer)
			{
				Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoRentPaymentBuyerCancelled,
						claimType,
						location,
						RealEstate.econ.format(price));
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
        	{
        		User u = RealEstate.ess.getUser(this.buyer);
				u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentPaymentBuyerCancelled,
						claimType,
						location,
						RealEstate.econ.format(price)));
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
	public boolean tryCancelTransaction(Player p, boolean force)
	{
		if(buyer != null)
		{
			if(p.hasPermission("realestate.admin") || force)
			{
				this.unRent(true);
				RealEstate.transactionsStore.cancelTransaction(this);
				return true;
			}
			else
			{
				//noinspection ConstantConditions
				if(p != null) {
					IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
					Messages.sendMessage(p, RealEstate.instance.messages.msgErrorCantCancelAlreadyRented,
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
			RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;

		if (owner != null && owner.equals(player.getUniqueId()))
        {
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyOwner, claimTypeDisplay);
            return;
        }
		if(claim.isParentClaim() && owner != null && !owner.equals(claim.getOwner()))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNotRentedByOwner, claimTypeDisplay);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		if(!player.hasPermission("realestate." + claimType + ".rent"))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoRentPermission, claimTypeDisplay);
            return;
		}
		if(player.getUniqueId().equals(buyer) || buyer != null)
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyRented, claimTypeDisplay);
            return;
		}

		if(Utils.makePayment(owner, player.getUniqueId(), price, false, true))// if payment succeed
		{
			buyer = player.getUniqueId();
			startDate = LocalDateTime.now();
			autoRenew = false;
			claim.addPlayerPermissions(buyer, buildTrust ? ClaimPermission.BUILD : ClaimPermission.CONTAINER);
			claim.addPlayerPermissions(player.getUniqueId(), ClaimPermission.MANAGE);
			claim.addManager(player.getUniqueId());
			claim.setInheritPermissions(false);
			RealEstate.claimAPI.saveClaim(claim);
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

			if(owner != null)
			{
				OfflinePlayer seller = Bukkit.getOfflinePlayer(owner);
				String location = "[" + Objects.requireNonNull(sign.getWorld()).getName() + ", " +
						"X: " + sign.getBlockX() + ", " +
						"Y: " + sign.getBlockY() + ", " +
						"Z: " + sign.getBlockZ() + "]";

				if(RealEstate.instance.config.cfgMessageOwner && seller.isOnline())
				{
					Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimOwnerRented,
						player.getName(),
						claimTypeDisplay,
						RealEstate.econ.format(price),
						location);
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(this.owner);
					u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimOwnerRented,
						player.getName(),
						claimTypeDisplay,
						RealEstate.econ.format(price),
						location));
	        	}
			}

			Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimBuyerRented,
				claimTypeDisplay,
				RealEstate.econ.format(price));

			destroySign();
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
			msg = Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentHeader) + "\n";
			if(buyer == null)
			{
				msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoGeneralRentNoBuyer,
						claimTypeDisplay,
						RealEstate.econ.format(price),
						Utils.getTime(duration, null, true)) + "\n";

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
				int days = Period.between(startDate.toLocalDate(), LocalDate.now()).getDays();
				Duration hours = Duration.between(startDate.toLocalTime(), LocalTime.now());
				if(hours.isNegative() && !hours.isZero())
		        {
		            hours = hours.plusHours(24);
		            days--;
		        }
				int daysLeft = duration - days - 1;// we need to remove the current day
				Duration timeRemaining = Duration.ofHours(24).minus(hours);

				msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoGeneralRentBuyer,
						claimTypeDisplay,
						Bukkit.getOfflinePlayer(buyer).getName(),
						RealEstate.econ.format(price),
						Utils.getTime(daysLeft, timeRemaining, true),
						Utils.getTime(duration, null, true)) + "\n";

				if((owner != null && owner.equals(player.getUniqueId()) || buyer.equals(player.getUniqueId())) && RealEstate.instance.config.cfgEnableAutoRenew)
				{
					msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentAutoRenew,
						autoRenew ?
							RealEstate.instance.messages.keywordEnabled :
							RealEstate.instance.messages.keywordDisabled) + "\n";
				}
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

		Messages.sendMessage(cs, RealEstate.instance.messages.msgInfoClaimInfoRentOneline,
				claim.getArea() + "",
				location,
				RealEstate.econ.format(price),
				Utils.getTime(duration, Duration.ZERO, false));
	}

}
