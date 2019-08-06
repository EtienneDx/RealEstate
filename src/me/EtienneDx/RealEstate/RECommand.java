package me.EtienneDx.RealEstate;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import me.EtienneDx.RealEstate.Transactions.BoughtTransaction;
import me.EtienneDx.RealEstate.Transactions.ClaimRent;
import me.EtienneDx.RealEstate.Transactions.ExitOffer;
import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

@CommandAlias("re|realestate")
public class RECommand extends BaseCommand
{
	@Subcommand("info")
	@Description("Gives the player informations about the claim he is standing in")
	@CommandPermission("realestate.info")
	public static void info(Player player)
	{
		if(RealEstate.transactionsStore.anyTransaction(
				GriefPrevention.instance.dataStore.getClaimAt(((Player)player).getLocation(), false, null)))
		{
			Transaction tr = RealEstate.transactionsStore.getTransaction(
					GriefPrevention.instance.dataStore.getClaimAt(((Player)player).getLocation(), false, null));
			tr.preview((Player)player);
		}
		else
		{
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "No transaction found at your location!");
		}
	}
	
	@Subcommand("renewrent")
	@Description("Allows the player renting a claim or subclaim to enable or disable the automatic renew of his rent")
	@Conditions("partOfRent")
    @CommandCompletion("enable|disable")
	public static void renewRent(Player player, @Optional String newStatus)
	{
		Location loc = player.getLocation();
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
		ClaimRent cr = (ClaimRent)RealEstate.transactionsStore.getTransaction(claim);
		String claimType = claim.parent == null ? "claim" : "subclaim";
		if(!RealEstate.instance.config.cfgEnableAutoRenew)
		{
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Automatic renew is disabled!");
			return;
		}
		if(newStatus == null)
		{
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "Automatic renew is currently " + 
					ChatColor.GREEN + (cr.autoRenew ? "enabled" : "disabled") + ChatColor.AQUA + " for this " + claimType + "!");
		}
		else if(!newStatus.equalsIgnoreCase("enable") && !newStatus.equalsIgnoreCase("disable"))
		{
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Usage : /re renewrent [enable|disable]!");
		}
		else if(cr.buyer.equals(player.getUniqueId()))
		{
			cr.autoRenew = newStatus.equalsIgnoreCase("enable");
			RealEstate.transactionsStore.saveData();
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "Automatic renew is now " + 
					ChatColor.GREEN + (cr.autoRenew ? "enabled" : "disabled") + ChatColor.AQUA + " for this " + claimType + "!");
		}
		else
		{
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Only the buyer may change this setting!");
		}
	}
	
	/*@Subcommand("seller")
	@Description("Displays or changes the seller of a claim (admin only)")
	@Conditions("inPendingTransactionClaim")
	public static void setSeller(Player player, @Optional String newSeller)
	{
		Location loc = player.getLocation();
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
		Transaction tr = RealEstate.transactionsStore.getTransaction(claim);
		if(!claim.isAdminClaim())
		{
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "This is not an admin claim");
		}
		else if(newSeller == null)
		{
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "The seller of this claim is " + 
					ChatColor.GREEN + (tr.getOwner() == null ? "the server" : Bukkit.getPlayer(tr.getOwner()).getDisplayName()));
		}
		else if(!RealEstate.perms.has(player, "realestate.admin"))
		{
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "You don't have the permission to change the seller");
		}
		else if(newSeller.equalsIgnoreCase("server"))
		{
			tr.setOwner(null);
			tr.update();
			RealEstate.transactionsStore.saveData();
			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "Changed the seller to the server");
		}
		else
		{
			Player newOwner = Bukkit.getPlayer(newSeller);
			if(newOwner == null)
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + "Couldn't find this player (he may be offline)");
			}
			else if(!RealEstate.perms.has(newOwner, "realestate.admin"))
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"This player doesn't have the right to lease/rent/sell admin claims");
			}
			else
			{
				tr.setOwner(newOwner.getUniqueId());
				tr.update();
				RealEstate.transactionsStore.saveData();
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "Changed the seller to " + 
						ChatColor.GREEN + newOwner.getDisplayName());
			}
		}
	}*/
	
	@Subcommand("exitoffer")
	@Conditions("partOfBoughtTransaction")
	public class ExitOfferCommand extends BaseCommand
	{
		@Subcommand("info")
		@Default
		@Description("View informations about the exit offer")
		public void info(Player player)
		{
			BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(player);
			if(bt.exitOffer == null)
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "There is currently no exit offer for this claim!");
			}
			else if(bt.exitOffer.offerBy.equals(player.getUniqueId()))
			{
				String msg = RealEstate.instance.config.chatPrefix + ChatColor.AQUA + "You offered to exit the contract for " + 
						ChatColor.GREEN + bt.exitOffer.price + " " + RealEstate.econ.currencyNamePlural() + ChatColor.AQUA + 
						", but your offer hasn't been accepted or denied yet...\n";
				msg += ChatColor.AQUA + "To cancel your offer, just type " + ChatColor.LIGHT_PURPLE + "/re exitoffer cancel";
				player.sendMessage(msg);
			}
			else// it is the other person
			{
				String msg = RealEstate.instance.config.chatPrefix + ChatColor.GREEN + Bukkit.getOfflinePlayer(bt.exitOffer.offerBy).getName() +
						ChatColor.AQUA + " offered to exit the contract for " + 
						ChatColor.GREEN + bt.exitOffer.price + " " + RealEstate.econ.currencyNamePlural() + "\n";
				msg += ChatColor.AQUA + "To accept the offer, just type " + ChatColor.LIGHT_PURPLE + "/re exitoffer accept\n";
				msg += ChatColor.AQUA + "To refuse the offer, just type " + ChatColor.LIGHT_PURPLE + "/re exitoffer refuse\n";
				player.sendMessage(msg);
			}
		}
		
		@Subcommand("create")
		@Description("Creates an offer to break an ongoing transaction")
		public void create(Player player, @Conditions("positiveDouble") Double price)
		{
			BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(player);
			if(bt.exitOffer != null)
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"There is already an exit proposition for this transaction!");
				return;
			}
			if(bt.buyer == null)
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"No one is engaged by this transaction yet!");
				return;
			}
			bt.exitOffer = new ExitOffer(player.getUniqueId(), price);

			player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
					"The proposition has been successfully created!");
			UUID other = player.getUniqueId().equals(bt.owner) ? bt.buyer : bt.owner;
			OfflinePlayer otherP = Bukkit.getOfflinePlayer(other);
			Location loc = player.getLocation();
			String claimType = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null).parent == null ? "claim" : "subclaim";
			if(otherP.isOnline())
			{
				((Player)otherP).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + 
						ChatColor.AQUA + " has created an offer to exit the rent/lease contract for the " + claimType + " at " + 
						ChatColor.BLUE + "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: "
						+ loc.getBlockZ() + "]" + ChatColor.AQUA + " for " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
			}
			else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
        	{
        		User u = RealEstate.ess.getUser(other);
        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + 
						ChatColor.AQUA + " has created an offer to exit the rent/lease contract for the " + claimType + " at " + 
						ChatColor.BLUE + "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: "
						+ loc.getBlockZ() + "]" + ChatColor.AQUA + " for " + ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural());
        	}
		}
		
		@Subcommand("accept")
		@Description("Accepts an offer to break an ongoing transaction")
		public void accept(Player player)
		{
			Location loc = player.getLocation();
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
			BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(claim);
			String claimType = claim.parent == null ? "claim" : "subclaim";
			if(bt.exitOffer == null)
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"There has been no exit propositions for this transaction!");
			}
			else if(bt.exitOffer.offerBy.equals(player.getUniqueId()))
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"You can't accept or refuse an offer you made!");
			}
			else if(Utils.makePayment(player.getUniqueId(), bt.exitOffer.offerBy, bt.exitOffer.price, true, false))
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
						"This exit offer has been accepted, the " + claimType + " is no longer rented or leased!");
				UUID other = player.getUniqueId().equals(bt.owner) ? bt.buyer : bt.owner;
				OfflinePlayer otherP = Bukkit.getOfflinePlayer(other);
				if(otherP.isOnline())
				{
					((Player)otherP).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + 
							ChatColor.AQUA + " has accepted your offer to exit the rent/lease contract for the " + claimType + " at " + 
							ChatColor.BLUE + "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + 
							", Z: " + loc.getBlockZ() + "]. It is no longer rented or leased.");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(other);
	        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + 
							ChatColor.AQUA + " has accepted your offer to exit the rent/lease contract for the " + claimType + " at " + 
							ChatColor.BLUE + "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + 
							", Z: " + loc.getBlockZ() + "]. It is no longer rented or leased.");
	        	}
				bt.exitOffer = null;
				claim.dropPermission(bt.buyer.toString());
				GriefPrevention.instance.dataStore.saveClaim(claim);
				bt.buyer = null;
				bt.update();// eventual cancel is contained in here
			}
			// the make payment takes care of sending error if need be
		}
		
		@Subcommand("refuse")
		@Description("Refuses an offer to break an ongoing transaction")
		public void refuse(Player player)
		{
			Location loc = player.getLocation();
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
			BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(claim);
			String claimType = claim.parent == null ? "claim" : "subclaim";
			if(bt.exitOffer == null)
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"There has been no exit propositions for this transaction!");
			}
			else if(bt.exitOffer.offerBy.equals(player.getUniqueId()))
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"You can't accept or refuse an offer you made!");
			}
			else
			{
				bt.exitOffer = null;
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
						"This exit offer has been refused");
				UUID other = player.getUniqueId().equals(bt.owner) ? bt.buyer : bt.owner;
				OfflinePlayer otherP = Bukkit.getOfflinePlayer(other);
				if(otherP.isOnline())
				{
					((Player)otherP).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + 
							ChatColor.AQUA + " has refused your offer to exit the rent/lease contract for the " + claimType + " at " + 
							ChatColor.BLUE + "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + 
							", Z: " + loc.getBlockZ() + "]");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(other);
	        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + 
							ChatColor.AQUA + " has refused your offer to exit the rent/lease contract for the " + claimType + " at " + 
							ChatColor.BLUE + "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + 
							", Z: " + loc.getBlockZ() + "]");
	        	}
			}
		}
		
		@Subcommand("cancel")
		@Description("Cancels an offer to break an ongoing transaction")
		public void cancel(Player player)
		{
			Location loc = player.getLocation();
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
			BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(claim);
			String claimType = claim.parent == null ? "claim" : "subclaim";
			if(bt.exitOffer.offerBy.equals(player.getUniqueId()))
			{
				bt.exitOffer = null;
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.AQUA + 
						"This exit offer has been cancelled");
				UUID other = player.getUniqueId().equals(bt.owner) ? bt.buyer : bt.owner;
				OfflinePlayer otherP = Bukkit.getOfflinePlayer(other);
				if(otherP.isOnline())
				{
					((Player)otherP).sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + 
							ChatColor.AQUA + " has cancelled his offer to exit the rent/lease contract for the " + claimType + " at " + 
							ChatColor.BLUE + "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: "
							+ loc.getBlockZ() + "]");
				}
				else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
	        	{
	        		User u = RealEstate.ess.getUser(other);
	        		u.addMail(RealEstate.instance.config.chatPrefix + ChatColor.GREEN + player.getName() + 
							ChatColor.AQUA + " has cancelled his offer to exit the rent/lease contract for the " + claimType + " at " + 
							ChatColor.BLUE + "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: "
							+ loc.getBlockZ() + "]");
	        	}
			}
			else
			{
				player.sendMessage(RealEstate.instance.config.chatPrefix + ChatColor.RED + 
						"Only the player who created this exit proposition may cancel it");
			}
		}
	}
	
	@HelpCommand
	public static void onHelp(CommandSender sender, CommandHelp help)
	{
        help.showHelp();
	}
}
