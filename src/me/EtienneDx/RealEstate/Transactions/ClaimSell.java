package me.EtienneDx.RealEstate.Transactions;

import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

import me.EtienneDx.RealEstate.Messages;
import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.md_5.bungee.api.ChatColor;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

public class ClaimSell extends ClaimTransaction
{
	public ClaimSell(Claim claim, Player player, double price, Location sign)
	{
		super(claim, player, price, sign);
	}
	
	public ClaimSell(Map<String, Object> map)
	{
		super(map);
	}

	@Override
	public boolean update()
	{
		if(sign.getBlock().getState() instanceof Sign)
		{
			Sign s = (Sign) sign.getBlock().getState();
			s.setLine(0, Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));
			s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceSell);
			s.setLine(2, owner != null ? Utils.getSignString(Bukkit.getOfflinePlayer(owner).getName()) : "SERVER");
			if(RealEstate.instance.config.cfgUseCurrencySymbol)
			{
				if(RealEstate.instance.config.cfgUseDecimalCurrency == false)
				{
					s.setLine(3, RealEstate.instance.config.cfgCurrencySymbol + " " + (int)Math.round(price));
				}
				else
				{
					s.setLine(3, RealEstate.instance.config.cfgCurrencySymbol + " " + price);
				}
			}
			else
			{
				if(RealEstate.instance.config.cfgUseDecimalCurrency == false)
				{
					s.setLine(3, (int)Math.round(price) + " " + RealEstate.econ.currencyNamePlural());
				}
				else
				{
					s.setLine(3, price + " " + RealEstate.econ.currencyNamePlural());
				}
			}
			s.update(true);
		}
		else
		{
			RealEstate.transactionsStore.cancelTransaction(this);
		}
		return false;
	}
	
	@Override
	public boolean tryCancelTransaction(Player p, boolean force)
	{
		// nothing special here, this transaction can only be waiting for a buyer
		RealEstate.transactionsStore.cancelTransaction(this);
		return true;
	}

	@Override
	public void interact(Player player)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);// getting by id creates errors for subclaims
		if(claim == null)
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimDoesNotExist);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		String claimType = claim.parent == null ? "claim" : "subclaim";
		String claimTypeDisplay = claim.parent == null ? 
			RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
		
		if (player.getUniqueId().equals(owner))
        {
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyOwner, claimTypeDisplay);
            return;
        }
		if(claim.parent == null && owner != null && !owner.equals(claim.ownerID))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNotSoldByOwner, claimTypeDisplay);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		if(!player.hasPermission("realestate." + claimType + ".buy"))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoBuyPermission, claimTypeDisplay);
            return;
		}
		// for real claims, you may need to have enough claim blocks in reserve to purchase it (if transferClaimBlocks is false)
		if(claimType.equalsIgnoreCase("claim") && !RealEstate.instance.config.cfgTransferClaimBlocks && 
				GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getRemainingClaimBlocks() < claim.getArea())
		{
			int remaining = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getRemainingClaimBlocks();
			int area = claim.getArea();
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoClaimBlocks,
				area + "",
				remaining + "",
				(area - remaining) + "");
            return;			
		}
		// the player has the right to buy, let's make the payment
		
		if(Utils.makePayment(owner, player.getUniqueId(), price, false, true))// if payment succeed
		{
			Utils.transferClaim(claim, player.getUniqueId(), owner);
			// normally, this is always the case, so it's not necessary, but until I proven my point, here
			if(claim.parent != null || claim.ownerID.equals(player.getUniqueId()))
			{
				String location = "[" + player.getLocation().getWorld() + ", " +
					"X: " + player.getLocation().getBlockX() + ", " +
					"Y: " + player.getLocation().getBlockY() + ", " +
					"Z: " + player.getLocation().getBlockZ() + "]";

				Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimBuyerSold,
						claimTypeDisplay,
						RealEstate.econ.format(price));
						
                RealEstate.instance.addLogEntry(
                        "[" + RealEstate.transactionsStore.dateFormat.format(RealEstate.transactionsStore.date) + "] " + player.getName() + 
                        " has purchased a " + claimType + " at " +
                                "[" + player.getLocation().getWorld() + ", " +
                                "X: " + player.getLocation().getBlockX() + ", " +
                                "Y: " + player.getLocation().getBlockY() + ", " +
                                "Z: " + player.getLocation().getBlockZ() + "] " +
                                "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
                
                if(RealEstate.instance.config.cfgMessageOwner && owner != null)
                {
                	OfflinePlayer oldOwner = Bukkit.getOfflinePlayer(owner);
                	if(oldOwner.isOnline())
                	{
						Messages.sendMessage(oldOwner.getPlayer(), RealEstate.instance.messages.msgInfoClaimOwnerSold,
								player.getName(),
								claimTypeDisplay,
								RealEstate.econ.format(price),
								location);
                	}
                	else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
                	{
                		User u = RealEstate.ess.getUser(owner);
						u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimOwnerSold,
								player.getName(),
								claimTypeDisplay,
								RealEstate.econ.format(price),
								location));
                	}
                }
			}
            else
            {
				Messages.sendMessage(player, RealEstate.instance.messages.msgErrorUnexpected);
                return;
            }
			RealEstate.transactionsStore.cancelTransaction(claim);
		}
	}
	
	@Override
	public void preview(Player player)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
		if(player.hasPermission("realestate.info"))
		{
			String claimType = claim.parent == null ? "claim" : "subclaim";
			String claimTypeDisplay = claim.parent == null ? 
				RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;

			String msg = Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoSellHeader) + "\n";

			msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoSellGeneral,
					claimTypeDisplay,
					RealEstate.econ.format(price)) + "\n";

			if(claimType.equalsIgnoreCase("claim"))
			{
				msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoOwner,
						claim.getOwnerName()) + "\n";
			}
			else
			{
				msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoMainOwner,
						claim.parent.getOwnerName()) + "\n";
				msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoNote) + "\n";
			}
			Messages.sendMessage(player, msg, false);
		}
		else
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoInfoPermission);
		}
	}

	@Override
	public void setOwner(UUID newOwner)
	{
		this.owner = newOwner;
	}

	@Override
	public void msgInfo(CommandSender cs)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
		if(claim == null) {
			tryCancelTransaction(null, true);
			return;
		}
		String location = "[" + claim.getLesserBoundaryCorner().getWorld().getName() + ", " +
		"X: " + claim.getLesserBoundaryCorner().getBlockX() + ", " +
		"Y: " + claim.getLesserBoundaryCorner().getBlockY() + ", " +
		"Z: " + claim.getLesserBoundaryCorner().getBlockZ() + "]";

		Messages.sendMessage(cs, RealEstate.instance.messages.msgInfoClaimInfoSellOneline,
				claim.getArea() + "",
				location,
				RealEstate.econ.format(price));
	}
}
