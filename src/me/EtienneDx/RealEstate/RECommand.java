package me.EtienneDx.RealEstate;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import co.aikar.commands.annotation.Syntax;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.Transactions.BoughtTransaction;
import me.EtienneDx.RealEstate.Transactions.ClaimAuction;
import me.EtienneDx.RealEstate.Transactions.ClaimRent;
import me.EtienneDx.RealEstate.Transactions.ExitOffer;
import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.EtienneDx.RealEstate.Transactions.TransactionsStore;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * The RECommand class provides command handlers for the RealEstate plugin.
 * <p>
 * It includes subcommands for retrieving claim information, listing transactions,
 * renewing rent, bidding on auctioned claims, cancelling transactions, and managing exit offers.
 * </p>
 */
@CommandAlias("re|realestate")
public class RECommand extends BaseCommand
{
	/**
	 * The RealEstate plugin instance.
	 * <p>
	 * This is the base instance for the commands for the RealEstate plugin.
	 * </p>
	 */
	public RECommand() {}
	
    /**
     * Displays information about the claim that the player is standing in.
     * <p>
     * If there is an ongoing transaction for the claim, its details are shown;
     * otherwise, a "no transaction found" message is sent.
     * </p>
     *
     * @param player the player executing the command
     */
    @Subcommand("info")
    @Description("Gives the player informations about the claim he is standing in")
    @CommandPermission("realestate.info")
    public static void info(Player player)
    {
        if(RealEstate.transactionsStore.anyTransaction(
                RealEstate.claimAPI.getClaimAt(player.getLocation())))
        {
            Transaction tr = RealEstate.transactionsStore.getTransaction(
                    RealEstate.claimAPI.getClaimAt(player.getLocation()));
            tr.preview(player);
        }
        else
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgNoTransactionFoundHere);
        }
    }
    
    /**
     * Lists all RealEstate offers (sell, rent, or lease) currently existing.
     * <p>
     * The type can be specified ("all", "sell", "rent", or "lease") and a page number.
     * </p>
     *
     * @param sender the command sender (player or console)
     * @param type   the type of offers to list (optional; defaults to "all")
     * @param page   the page number to display (optional; defaults to 1)
     */
    @Subcommand("list")
    @Description("Displays the list of all real estate offers currently existing")
    @CommandCompletion("all|sell|rent|lease")
    @Syntax("[all|sell|rent|lease] <page>")
    public static void list(CommandSender sender, @Optional String type, @Default("1") int page)
    {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if(page <= 0)
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgPageMustBePositive);
            return;
        }
        int count = 0;
        int start = (page - 1) * RealEstate.instance.config.cfgPageSize;
        String typeMsg;
        if(type == null || type.equalsIgnoreCase("all"))
        {
            count = RealEstate.transactionsStore.claimSell.values().size() + RealEstate.transactionsStore.claimRent.values().size() +
                    RealEstate.transactionsStore.claimLease.values().size();
            typeMsg = "Real Estate offers";
        }
        else if(type.equalsIgnoreCase("sell"))
        {
            count = RealEstate.transactionsStore.claimSell.values().size();
            typeMsg = "Sell offers";
        }
        else if(type.equalsIgnoreCase("rent"))
        {
            count = RealEstate.transactionsStore.claimRent.values().size();
            typeMsg = "Rent offers";
        }
        else if(type.equalsIgnoreCase("lease"))
        {
            count = RealEstate.transactionsStore.claimLease.values().size();
            typeMsg = "Lease offers";
        }
        else
        {
            Messages.sendMessage(sender, RealEstate.instance.messages.msgErrorInvalidOption);
            return;
        }
        if(count == 0)
        {
            Messages.sendMessage(sender, RealEstate.instance.messages.msgNoTransactionFound);
        }
        else
        {
            ArrayList<Transaction> transactions = new ArrayList<Transaction>(count);
            if(type == null || type.equalsIgnoreCase("all"))
            {
                transactions.addAll(RealEstate.transactionsStore.claimSell.values());
                transactions.addAll(RealEstate.transactionsStore.claimRent.values());
                transactions.addAll(RealEstate.transactionsStore.claimLease.values());
            }
            else if(type.equalsIgnoreCase("sell"))
            {
                transactions.addAll(RealEstate.transactionsStore.claimSell.values());
            }
            else if(type.equalsIgnoreCase("rent"))
            {
                transactions.addAll(RealEstate.transactionsStore.claimRent.values());
            }
            else if(type.equalsIgnoreCase("lease"))
            {
                transactions.addAll(RealEstate.transactionsStore.claimLease.values());
            }
            
            int max = Math.min(start + RealEstate.instance.config.cfgPageSize, count);
            if(start <= max)
            {
                int pageCount = (int)Math.ceil(count / (double)RealEstate.instance.config.cfgPageSize);
                Messages.sendMessage(sender, RealEstate.instance.messages.msgListTransactionsHeader, 
                        typeMsg, String.valueOf(page), String.valueOf(pageCount));
                for(int i = start; i < max; i++)
                {
                    RealEstate.instance.log.info("transaction " + i);
                    transactions.get(i).msgInfo(sender);
                }
                if(page < pageCount)
                {
                    Messages.sendMessage(sender, RealEstate.instance.messages.msgListNextPage, (type != null ? type : "all"), String.valueOf(page + 1));
                }
            }
            else
            {
                Messages.sendMessage(sender, RealEstate.instance.messages.msgPageNotExists);
            }
        }
    }
    
    /**
     * Allows a player to view or change the automatic renew status of a rent transaction.
     * <p>
     * Without a parameter, the current status is displayed; with a parameter ("enable" or "disable"),
     * the status is updated.
     * </p>
     *
     * @param player    the player executing the command
     * @param newStatus the new status ("enable" or "disable"); optional
     */
    @Subcommand("renewrent")
    @Description("Allows the player renting a claim or subclaim to enable or disable the automatic renew of his rent")
    @Conditions("partOfRent")
    @CommandCompletion("enable|disable")
    @Syntax("[enable|disable]")
    public static void renewRent(Player player, @Optional String newStatus)
    {
        Location loc = player.getLocation();
        IClaim claim = RealEstate.claimAPI.getClaimAt(loc);
        ClaimRent cr = (ClaimRent)RealEstate.transactionsStore.getTransaction(claim);
        String claimType = claim.isParentClaim() ? 
            RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
        if(!RealEstate.instance.config.cfgEnableAutoRenew)
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorAutoRenewDisabled);
            return;
        }
        if(newStatus == null)
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgRenewRentCurrently, cr.autoRenew ? 
                    RealEstate.instance.messages.keywordEnabled :
                    RealEstate.instance.messages.keywordDisabled,
                claimType);
        }
        else if(!newStatus.equalsIgnoreCase("enable") && !newStatus.equalsIgnoreCase("disable"))
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorCommandUsage, "/re renewrent [enable|disable]");
        }
        else if(cr.buyer != null && cr.buyer.equals(player.getUniqueId()))
        {
            cr.autoRenew = newStatus.equalsIgnoreCase("enable");
            RealEstate.transactionsStore.saveData();
            Messages.sendMessage(player, RealEstate.instance.messages.msgRenewRentNow, cr.autoRenew ? 
                    RealEstate.instance.messages.keywordEnabled :
                    RealEstate.instance.messages.keywordDisabled,
                claimType);
        }
        else
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorBuyerOnly);
        }
    }
    
    /**
     * Subcommands for managing exit offers.
     * <p>
     * This inner class provides commands to view, create, accept, refuse, or cancel an exit offer.
     * </p>
     */
    @Subcommand("exitoffer")
    @Conditions("partOfBoughtTransaction")
    public class ExitOfferCommand extends BaseCommand
    {
    	/**
		 * The ExitOfferCommand constructor.
		 * <p>
		 * This is the constructor for the ExitOfferCommand class.
		 * </p>
		 */
    	public ExitOfferCommand() {}
    	
        /**
         * Displays information about the current exit offer.
         *
         * @param player the player executing the command
         */
        @Subcommand("info")
        @Default
        @Description("View informations about the exit offer")
        public void info(Player player)
        {
            BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(player);
            if(bt.exitOffer == null)
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferNone);
            }
            else if(bt.exitOffer.offerBy.equals(player.getUniqueId()))
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferMadeByStatus, 
                        RealEstate.econ.format(bt.exitOffer.price));
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferCancel, 
                        "/re exitoffer cancel");
            }
            else // it is the other player
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferMadeToStatus, 
                    Bukkit.getOfflinePlayer(bt.exitOffer.offerBy).getName(), RealEstate.econ.format(bt.exitOffer.price));
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferAccept, 
                        "/re exitoffer accept");
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferReject,
                        "/re exitoffer refuse");
            }
        }
        
        /**
         * Creates an exit offer with the specified price.
         *
         * @param player the player creating the exit offer
         * @param price  the price of the exit offer (must be positive)
         */
        @Subcommand("create")
        @Description("Creates an offer to break an ongoing transaction")
        @Syntax("<price>")
        public void create(Player player, @Conditions("positiveDouble") Double price)
        {
            BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(player);
            if(bt.exitOffer != null)
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorExitOfferAlreadyExists);
                return;
            }
            if(bt.buyer == null)
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorExitOfferNoBuyer);
                return;
            }
            bt.exitOffer = new ExitOffer(player.getUniqueId(), price);

            Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferCreatedBySelf, 
                    RealEstate.econ.format(price));

            UUID other = player.getUniqueId().equals(bt.owner) ? bt.buyer : bt.owner;
            if(other != null) // not an admin claim
            {
                OfflinePlayer otherP = Bukkit.getOfflinePlayer(other);
                Location loc = player.getLocation();
                String claimType = RealEstate.claimAPI.getClaimAt(loc).isParentClaim() ? 
                    RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
                String location = "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: "
                    + loc.getBlockZ() + "]";

                if(otherP.isOnline())
                {
                    Messages.sendMessage(otherP.getPlayer(), RealEstate.instance.messages.msgInfoExitOfferCreatedByOther, 
                            player.getName(), claimType, RealEstate.econ.format(price), location);
                }
                else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
                {
                    User u = RealEstate.ess.getUser(other);
                    u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoExitOfferCreatedByOther, 
                            player.getName(), claimType, RealEstate.econ.format(price), location));
                }
            }
        }
        
        /**
         * Accepts the current exit offer.
         *
         * @param player the player accepting the exit offer
         */
        @Subcommand("accept")
        @Description("Accepts an offer to break an ongoing transaction")
        public void accept(Player player)
        {
            Location loc = player.getLocation();
            IClaim claim = RealEstate.claimAPI.getClaimAt(loc);
            BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(claim);
            String claimType = claim.isParentClaim() ? "claim" : "subclaim";
            if(bt.exitOffer == null)
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorExitOfferNone);
            }
            else if(bt.exitOffer.offerBy.equals(player.getUniqueId()))
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorExitOfferCantAcceptSelf);
            }
            else if(Utils.makePayment(player.getUniqueId(), bt.exitOffer.offerBy, bt.exitOffer.price, true, false))
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferAcceptedBySelf, 
                        claimType, RealEstate.econ.format(bt.exitOffer.price));

                UUID other = player.getUniqueId().equals(bt.owner) ? bt.buyer : bt.owner;
                String location = "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + 
                    ", Z: " + loc.getBlockZ() + "]";
                if(other != null)
                {
                    OfflinePlayer otherP = Bukkit.getOfflinePlayer(other);
                    if(otherP.isOnline())
                    {
                        Messages.sendMessage(otherP.getPlayer(), RealEstate.instance.messages.msgInfoExitOfferAcceptedByOther, 
                                player.getName(), claimType, RealEstate.econ.format(bt.exitOffer.price), location);
                    }
                    else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
                    {
                        User u = RealEstate.ess.getUser(other);
                        u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoExitOfferAcceptedByOther,
                                player.getName(), claimType, RealEstate.econ.format(bt.exitOffer.price), location));
                    }
                }
                bt.exitOffer = null;
                claim.dropPlayerPermissions(bt.buyer);
                claim.removeManager(bt.buyer);
                RealEstate.claimAPI.saveClaim(claim);
                bt.buyer = null;
                bt.update(); // eventual cancel is contained in here
            }
            // the makePayment takes care of sending error if need be
        }
        
        /**
         * Refuses the current exit offer.
         *
         * @param player the player refusing the exit offer
         */
        @Subcommand("refuse")
        @Description("Refuses an offer to break an ongoing transaction")
        public void refuse(Player player)
        {
            Location loc = player.getLocation();
            IClaim claim = RealEstate.claimAPI.getClaimAt(loc);
            BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(claim);
            String claimType = claim.isParentClaim() ? "claim" : "subclaim";
            if(bt.exitOffer == null)
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorExitOfferNone);
            }
            else if(bt.exitOffer.offerBy.equals(player.getUniqueId()))
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorExitOfferCantRefuseSelf);
            }
            else
            {
                bt.exitOffer = null;
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferRejectedBySelf);
                UUID other = player.getUniqueId().equals(bt.owner) ? bt.buyer : bt.owner;
                String location = "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + 
                    ", Z: " + loc.getBlockZ() + "]";
                if(other != null)
                {
                    OfflinePlayer otherP = Bukkit.getOfflinePlayer(other);
                    if(otherP.isOnline())
                    {
                        Messages.sendMessage(otherP.getPlayer(), RealEstate.instance.messages.msgInfoExitOfferRejectedByOther, 
                                player.getName(), claimType, location);
                    }
                    else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
                    {
                        User u = RealEstate.ess.getUser(other);
                        u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoExitOfferRejectedByOther,
                                player.getName(), claimType, location));
                    }
                }
            }
        }
        
        /**
         * Cancels the current exit offer.
         *
         * @param player the player cancelling the exit offer
         */
        @Subcommand("cancel")
        @Description("Cancels an offer to break an ongoing transaction")
        public void cancel(Player player)
        {
            Location loc = player.getLocation();
            IClaim claim = RealEstate.claimAPI.getClaimAt(loc);
            BoughtTransaction bt = (BoughtTransaction)RealEstate.transactionsStore.getTransaction(claim);
            String claimType = claim.isParentClaim() ? "claim" : "subclaim";
            if(bt.exitOffer.offerBy.equals(player.getUniqueId()))
            {
                bt.exitOffer = null;
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoExitOfferCancelledBySelf);
                
                UUID other = player.getUniqueId().equals(bt.owner) ? bt.buyer : bt.owner;
                String location = "[" + loc.getWorld().getName() + ", X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + 
                    ", Z: " + loc.getBlockZ() + "]";
                if(other != null)
                {
                    OfflinePlayer otherP = Bukkit.getOfflinePlayer(other);
                    if(otherP.isOnline())
                    {
                        Messages.sendMessage(otherP.getPlayer(), RealEstate.instance.messages.msgInfoExitOfferCancelledByOther, 
                                player.getName(), claimType, location);
                    }
                    else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null)
                    {
                        User u = RealEstate.ess.getUser(other);
                        u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoExitOfferCancelledByOther,
                                player.getName(), claimType, location));
                    }
                }
            }
            else
            {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorExitOfferCantCancelOther);
            }
        }
    }

    /**
     * Bids on an auctioned claim.
     * <p>
     * This command requires the claim to be currently under auction.
     * </p>
     *
     * @param player the player placing the bid
     * @param bid    the bid amount (must be a positive double)
     */
    @Subcommand("bid")
    @Conditions("claimIsAuctioned")
    @CommandPermission("realestate.bid")
    @Syntax("<bid>")
    public static void bid(Player player, @Conditions("positiveDouble") double bid)
    {
        Location loc = player.getLocation();
        IClaim claim = RealEstate.claimAPI.getClaimAt(loc);
        ClaimAuction ca = (ClaimAuction)RealEstate.transactionsStore.getTransaction(claim);
        ca.bid(player, bid);
    }
    
    /**
     * Cancels the transaction for the claim at the player's location.
     * <p>
     * Requires the player to have the "realestate.admin" permission.
     * </p>
     *
     * @param player the player executing the cancellation command
     */
    @Subcommand("cancel")
    @Conditions("claimHasTransaction")
    @CommandPermission("realestate.admin")
    public static void cancelTransaction(Player player)
    {
        Location loc = player.getLocation();
        IClaim claim = RealEstate.claimAPI.getClaimAt(loc);
        Transaction t = RealEstate.transactionsStore.getTransaction(claim);
        t.tryCancelTransaction(player, true);
    }
    
    /**
     * Displays the help information for the RealEstate commands.
     *
     * @param sender the command sender requesting help
     * @param help   the CommandHelp object containing the help details
     */
    @HelpCommand
    public static void onHelp(CommandSender sender, CommandHelp help)
    {
        help.showHelp();
    }
}
