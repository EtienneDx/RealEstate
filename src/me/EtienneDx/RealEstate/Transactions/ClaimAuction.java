package me.EtienneDx.RealEstate.Transactions;

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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.EtienneDx.RealEstate.Messages;
import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.RealEstateSign;
import me.EtienneDx.RealEstate.Utils;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import net.md_5.bungee.api.ChatColor;

/**
 * Represents an auction transaction for a claim.
 * <p>
 * This transaction handles the bidding process and finalizing an auction.
 * </p>
 */
public class ClaimAuction extends ClaimTransaction {

    /**
     * The UUID of the current highest bidder, or {@code null} if no bid has been made.
     */
    public UUID buyer = null;
    
    /**
     * The time when the auction ends.
     */
    public LocalDateTime endDate = null;
    
    /**
     * The minimum bid increment required.
     */
    public double bidStep = 1;

    /**
     * Constructs a ClaimAuction from a serialized map.
     *
     * @param map the map containing serialized transaction data
     */
    public ClaimAuction(Map<String, Object> map) {
        super(map);
        if (map.get("buyer") != null)
            buyer = UUID.fromString((String) map.get("buyer"));
        if (map.get("endDate") != null)
            endDate = LocalDateTime.parse((String) map.get("endDate"), DateTimeFormatter.ISO_DATE_TIME);
        bidStep = (double) map.get("bidStep");
    }

    /**
     * Constructs a new ClaimAuction.
     *
     * @param claim the claim being auctioned
     * @param player the player initiating the auction
     * @param price the starting price of the auction
     * @param sign the location of the auction sign
     * @param endDate the end time of the auction
     * @param bidStep the bid increment step
     */
    public ClaimAuction(IClaim claim, Player player, double price, Location sign, LocalDateTime endDate, double bidStep) {
        super(claim, player, price, sign);
        this.endDate = endDate;
        this.bidStep = bidStep;
    }

    /**
     * Serializes this ClaimAuction into a map for storage.
     *
     * @return a map containing the serialized properties of the auction
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        if (buyer != null)
            map.put("buyer", buyer.toString());
        if (endDate != null)
            map.put("endDate", endDate.format(DateTimeFormatter.ISO_DATE_TIME));
        map.put("bidStep", bidStep);
        return map;
    }

    /**
     * Updates the auction status.
     * <p>
     * This method checks if the auction has ended and, if so, finalizes the auction
     * by transferring claim ownership and updating the auction sign accordingly.
     * Otherwise, it updates the sign with the remaining time and current bid details.
     * </p>
     *
     * @return {@code true} if the auction ended and was finalized; {@code false} otherwise
     */
    @Override
    public boolean update() {
        int days = Period.between(LocalDate.now(), endDate.toLocalDate()).getDays();
        Duration hours = Duration.between(LocalTime.now(), endDate.toLocalTime());
        if (hours.isNegative() && !hours.isZero()) {
            hours = hours.plusHours(24);
            days--;
        }
        if (days < 0) {
            if (buyer != null) {
                IClaim claim = RealEstate.claimAPI.getClaimAt(sign); // Getting by id creates errors for subclaims
                OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(buyer);
                OfflinePlayer ownerPlayer = owner != null ? Bukkit.getOfflinePlayer(owner) : null;
                if (claim == null || claim.isWilderness()) {
                    if (!Utils.makePayment(buyer, null, price, false, false)) {
                        RealEstate.instance.log.warning("Couldn't reimburse " + price + " to " + buyerPlayer.getName() + " for the auction of a deleted claim");
                    }
                    if (buyerPlayer.isOnline()) {
                        Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgErrorClaimDoesNotExistAuction);
                    }
                    RealEstate.transactionsStore.cancelTransaction(claim);
                } else if (!Utils.makePayment(owner, null, price, false, false)) {
                    RealEstate.instance.log.warning("Couldn't pay " + price + " to " + claim.getOwnerName() + " for the auction of a claim");
                    if (buyerPlayer.isOnline()) {
                        Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgErrorAuctionCouldntPayOwner);
                        if (!Utils.makePayment(buyer, null, price, false, false)) {
                            RealEstate.instance.log.warning("Couldn't reimburse " + price + " to " + buyerPlayer.getName() + " for the cancellation of an auction");
                        }
                        if (buyerPlayer.isOnline()) {
                            Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoAuctionCancelled);
                        }
                    }
                    if (owner != null && ownerPlayer != null && ownerPlayer.isOnline()) {
                        Messages.sendMessage(ownerPlayer.getPlayer(), RealEstate.instance.messages.msgErrorAuctionCouldntReceiveOwner);
                    }
                    RealEstate.transactionsStore.cancelTransaction(claim);
                } else {
                    Utils.transferClaim(claim, buyer, owner);
                    if (getHolder().getState() instanceof Sign) {
                        RealEstateSign s = new RealEstateSign((Sign) getHolder().getState());
                        s.setLine(0, "");
                        s.setLine(1, Messages.getMessage(RealEstate.instance.messages.msgSignAuctionWon, false));
                        s.setLine(2, buyerPlayer.getName());
                        s.setLine(3, "");
                        s.update(true);
                    }
                    return true;
                }
            }
            if (getHolder().getState() instanceof Sign) {
                RealEstateSign s = new RealEstateSign((Sign) getHolder().getState());
                s.setLine(0, "");
                s.setLine(1, Messages.getMessage(RealEstate.instance.messages.msgSignAuctionEnded, false));
                s.setLine(2, "");
                s.setLine(3, "");
                s.update(true);
            }
            return true;
        } else {
            // Update the auction sign with current details.
            if (sign.getBlock().getState() instanceof Sign) {
                RealEstateSign s = new RealEstateSign((Sign) sign.getBlock().getState());
                s.setLine(0, Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));
                s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceAuction);
                String remaining = Utils.getTime(days, hours, false);
                s.setLine(2, Messages.getMessage(RealEstate.instance.messages.msgSignAuctionRemainingTime, false, remaining));
                if (buyer != null) {
                    String name = Bukkit.getOfflinePlayer(buyer).getName();
                    s.setLine(3, Messages.getMessage(RealEstate.instance.messages.msgSignAuctionHighestBidder, false, name, RealEstate.econ.format(price)));
                } else {
                    s.setLine(3, Messages.getMessage(RealEstate.instance.messages.msgSignAuctionNoBider, false));
                }
                s.update(true);
            } else {
                RealEstate.transactionsStore.cancelTransaction(this);
            }
            return false;
        }
    }

    /**
     * Attempts to cancel the auction transaction.
     *
     * @param p the player attempting the cancellation
     * @param force if {@code true}, forces cancellation regardless of conditions
     * @return {@code true} if the transaction was cancelled successfully; {@code false} otherwise
     */
    @Override
    public boolean tryCancelTransaction(Player p, boolean force) {
        if (buyer == null || RealEstate.instance.config.cfgCancelAuction || force || p.hasPermission("realestate.admin")) {
            if (buyer != null) {
                OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(buyer);
                if (!Utils.makePayment(buyer, null, price, false, false)) {
                    RealEstate.instance.log.warning("Couldn't reimburse " + price + " to " + buyerPlayer.getName() + " for the cancellation of an auction");
                }
                if (buyerPlayer.isOnline()) {
                    Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoAuctionCancelled);
                }
            }
            RealEstate.transactionsStore.cancelTransaction(this);
            return true;
        } else if (p != null) {
            p.sendMessage(Messages.getMessage(RealEstate.instance.messages.msgErrorCantCancelAuction));
        }
        return false;
    }

    /**
     * Processes interaction with the auction sign.
     * <p>
     * If the claim is invalid or the player is the owner, displays an error message.
     * Otherwise, it initiates a bid by increasing the current price.
     * </p>
     *
     * @param player the player interacting with the auction sign
     */
    @Override
    public void interact(Player player) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign); // Getting by id creates errors for subclaims
        if (claim == null || claim.isWilderness()) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimDoesNotExist);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
        }
        String claimTypeDisplay = claim.isParentClaim() ?
            RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
        
        if (owner != null && owner.equals(player.getUniqueId())) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyOwner, claimTypeDisplay);
            return;
        }
        if (claim.isParentClaim() && owner != null && !owner.equals(claim.getOwner())) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNotAuctionedByOwner, claimTypeDisplay);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
        }
        bid(player, price + bidStep);
    }

    /**
     * Processes a bid from a player.
     * <p>
     * This method validates the bid, processes payment, updates the highest bidder,
     * and then updates the auction status.
     * </p>
     *
     * @param player the player placing the bid
     * @param price the bid amount
     */
    public void bid(Player player, double price) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign); // Getting by id creates errors for subclaims
        if (claim == null || claim.isWilderness()) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimDoesNotExist);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
        }
        String claimType = claim.isParentClaim() ? "claim" : "subclaim";
        String claimTypeDisplay = claim.isParentClaim() ?
            RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;

        if (!player.hasPermission("realestate." + claimType + ".bid")) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoAuctionPermission, claimTypeDisplay);
            return;
        }
        if (RealEstate.instance.config.cfgDisableOutbidSelf && player.getUniqueId().equals(buyer)) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyHighestBidder, claimTypeDisplay);
            return;
        }
        if (RealEstate.econ.getBalance(player) < price) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNoMoneySelf, RealEstate.econ.format(price));
            return;
        }
        if (!Utils.makePayment(null, player.getUniqueId(), price, false, false)) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNoMoneySelf, RealEstate.econ.format(price));
            return;
        }
        if (buyer != null && !Utils.makePayment(buyer, null, this.price, false, false)) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorCouldntReimburseOther, RealEstate.econ.format(this.price));
            if (!Utils.makePayment(player.getUniqueId(), null, price, false, false)) {
                RealEstate.instance.log.warning("Couldn't reimburse " + player.getName() + " for " + RealEstate.econ.format(price) + ", the money has been lost!");
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorCouldntReimburseSelf, RealEstate.econ.format(price));
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorContactAdmin);
            }
            return;
        }
        buyer = player.getUniqueId();
        this.price = price;
        update();
        RealEstate.transactionsStore.saveData();
    }

    /**
     * Provides a preview of the auction information to a player.
     * <p>
     * This method sends the player details about the auction, including header, current bid,
     * remaining time, bid step, and owner information.
     * </p>
     *
     * @param player the player requesting the preview
     */
    @Override
    public void preview(Player player) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
        if (player.hasPermission("realestate.info")) {
            String claimType = claim.isParentClaim() ? "claim" : "subclaim";
            String claimTypeDisplay = claim.isParentClaim() ?
                RealEstate.instance.messages.keywordClaim :
                RealEstate.instance.messages.keywordSubclaim;
            String msg;
            msg = Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoAuctionHeader) + "\n";
            if (buyer == null) {
                msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoAuctionNoBidder, 
                    claimTypeDisplay,
                    RealEstate.econ.format(price)) + "\n";
            } else {
                OfflinePlayer buyer = Bukkit.getOfflinePlayer(this.buyer);
                msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoAuctionHighestBidder, 
                    claimTypeDisplay,
                    buyer.getName(),
                    RealEstate.econ.format(price)) + "\n";
            }

            int days = Period.between(LocalDate.now(), endDate.toLocalDate()).getDays();
            Duration hours = Duration.between(LocalTime.now(), endDate.toLocalTime());
            if (hours.isNegative() && !hours.isZero()) {
                hours = hours.plusHours(24);
                days--;
            }

            if (days < 0) {
                Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimInfoAuctionEnded, claimTypeDisplay);
                update(); // update will end the auction properly
                return;
            }

            msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoAuctionTimeRemaining, 
                Utils.getTime(days, hours, true)) + "\n";
            
            msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoAuctionBidStep, 
                RealEstate.econ.format(bidStep)) + "\n";

            if (claimType.equalsIgnoreCase("claim")) {
                msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoOwner,
                        claim.getOwnerName()) + "\n";
            } else {
                msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoMainOwner,
                        claim.getParent().getOwnerName()) + "\n";
            }

            Messages.sendMessage(player, msg, false);
        }
    }

    /**
     * Sends auction information to a CommandSender.
     * <p>
     * This method displays an oneline summary of the auction including area, location, price,
     * remaining time, and bid step.
     * </p>
     *
     * @param cs the CommandSender to send the message to
     */
    @Override
    public void msgInfo(CommandSender cs) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
        String location = "[" + claim.getWorld().getName() + ", " +
            "X: " + claim.getX() + ", " +
            "Y: " + claim.getY() + ", " +
            "Z: " + claim.getZ() + "]";

        int days = Period.between(LocalDate.now(), endDate.toLocalDate()).getDays();
        Duration hours = Duration.between(LocalTime.now(), endDate.toLocalTime());
        if (hours.isNegative() && !hours.isZero()) {
            hours = hours.plusHours(24);
            days--;
        }

        Messages.sendMessage(cs, RealEstate.instance.messages.msgInfoClaimInfoAuctionOneline,
            claim.getArea() + "",
            location,
            RealEstate.econ.format(price),
            Utils.getTime(days, hours, true),
            RealEstate.econ.format(bidStep));
    }

    /**
     * Returns the bid increment step for the auction.
     *
     * @return the bid step as a double
     */
    public double getBidStep() {
        return this.bidStep;
    }

    /**
     * Returns the UUID of the current highest bidder.
     *
     * @return the buyer's UUID, or {@code null} if no bid has been made
     */
    public UUID getBuyer() {
        return this.buyer;
    }

    /**
     * Returns the end date and time of the auction.
     *
     * @return the auction's end date as a {@link LocalDateTime}
     */
    public LocalDateTime getEndDate() {
        return this.endDate;
    }
}
