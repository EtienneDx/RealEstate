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

/**
 * Represents a rental transaction for a claim.
 * <p>
 * This transaction handles the leasing of claims by a renter. It supports auto-renewal,
 * periodic lease payments, and updates the sign display with the current lease status.
 * </p>
 */
public class ClaimRent extends BoughtTransaction {

    /** The date and time when the lease started or the last payment was made. */
    LocalDateTime startDate = null;
    /** The duration (in days) of the lease period. */
    int duration;
    /** If true, the lease is set to auto-renew once the duration expires. */
    public boolean autoRenew = false;
    /** Indicates whether the lease grants building trust (if true) or container trust (if false). */
    public boolean buildTrust = true;
    
    /**
     * Constructs a ClaimRent transaction from a serialized map.
     *
     * @param map the map containing serialized data for this lease transaction
     */
    public ClaimRent(Map<String, Object> map) {
        super(map);
        if(map.get("startDate") != null)
            startDate = LocalDateTime.parse((String) map.get("startDate"), DateTimeFormatter.ISO_DATE_TIME);
        duration = (int) map.get("duration");
        autoRenew = (boolean) map.get("autoRenew");
        try {
            buildTrust = (boolean) map.get("buildTrust");
        }
        catch (Exception e) {
            buildTrust = true;
        }
    }
    
    /**
     * Constructs a new ClaimRent transaction.
     *
     * @param claim the claim being rented
     * @param player the player initiating the rent
     * @param price the rental price per period
     * @param sign the location of the sign representing the rent
     * @param duration the lease period (in days)
     * @param buildTrust if true, grants building permissions; if false, grants container trust only
     */
    public ClaimRent(IClaim claim, Player player, double price, Location sign, int duration, boolean buildTrust) {
        super(claim, player, price, sign);
        this.duration = duration;
        this.buildTrust = buildTrust;
    }

    /**
     * Serializes this ClaimRent transaction to a Map.
     *
     * @return a Map representation of this lease transaction
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        if(startDate != null)
            map.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE_TIME));
        map.put("duration", duration);
        map.put("autoRenew", autoRenew);
        map.put("buildTrust", buildTrust);
        return map;
    }

    /**
     * Updates the state of the lease transaction.
     * <p>
     * If no buyer is present, it updates the sign with rental information. Otherwise, it checks if the lease
     * period has expired. If so, it processes a payment via {@link #payRent()}. If the lease is ongoing,
     * the sign is updated with the remaining time.
     * </p>
     *
     * @return false to indicate the transaction remains active
     */
    @Override
    public boolean update() {
        if(buyer == null) {
            if(sign.getBlock().getState() instanceof Sign) {
                RealEstateSign s = new RealEstateSign((Sign) sign.getBlock().getState());
                s.setLine(0, Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));
                s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceRent);
                String price_line = "";
                if(RealEstate.instance.config.cfgUseCurrencySymbol) {
                    if(RealEstate.instance.config.cfgUseDecimalCurrency == false) {
                        price_line = RealEstate.instance.config.cfgCurrencySymbol + " " + (int)Math.round(price);
                    }
                    else {
                        price_line = RealEstate.instance.config.cfgCurrencySymbol + " " + price;
                    }
                }
                else {
                    if(RealEstate.instance.config.cfgUseDecimalCurrency == false) {
                        price_line = (int)Math.round(price) + " " + RealEstate.econ.currencyNamePlural();
                    }
                    else {
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
            else {
                return true;
            }
        }
        else {
            // Calculate elapsed time since the lease started/last payment.
            int days = Period.between(startDate.toLocalDate(), LocalDate.now()).getDays();
            Duration hours = Duration.between(startDate.toLocalTime(), LocalTime.now());
            if(hours.isNegative() && !hours.isZero()) {
                hours = hours.plusHours(24);
                days--;
            }
            if(days >= duration) { // Lease period exceeded
                payRent();
            }
            else if(sign.getBlock().getState() instanceof Sign) {
                RealEstateSign s = new RealEstateSign((Sign) sign.getBlock().getState());
                s.setLine(0, ChatColor.GOLD + RealEstate.instance.config.cfgReplaceOngoingRent);
                s.setLine(1, Utils.getSignString(Bukkit.getOfflinePlayer(buyer).getName()));
                s.setLine(2, "Time remaining : ");
                int daysLeft = duration - days - 1; // Remove current day
                Duration timeRemaining = Duration.ofHours(24).minus(hours);
                s.setLine(3, Utils.getTime(daysLeft, timeRemaining, false));
                s.update(true);
            }
        }
        return false;
    }

    /**
     * Cancels the rent by dropping player permissions and resetting the claim.
     *
     * @param msgBuyer if true, sends a cancellation message to the buyer
     */
    private void unRent(boolean msgBuyer) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
        claim.dropPlayerPermissions(buyer);
        claim.removeManager(buyer);
        claim.setInheritPermissions(true);
        RealEstate.claimAPI.saveClaim(claim);
        if(msgBuyer && Bukkit.getOfflinePlayer(buyer).isOnline() && RealEstate.instance.config.cfgMessageBuyer) {
            String location = "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
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

    /**
     * Processes a lease payment.
     * <p>
     * If the payment is successful, updates the lease and decrements the number of remaining payments.
     * If the payment fails, the lease is canceled.
     * </p>
     */
    private void payRent() {
        if(buyer == null) return;

        OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(this.buyer);
        OfflinePlayer seller = owner == null ? null : Bukkit.getOfflinePlayer(owner);
        String claimType = RealEstate.claimAPI.getClaimAt(sign).isParentClaim() ?
                RealEstate.instance.messages.keywordClaim :
                RealEstate.instance.messages.keywordSubclaim;
        String location = "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() + ", Y: " + 
                sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]";
        
        if(autoRenew && Utils.makePayment(owner, this.buyer, price, false, false)) {
            startDate = LocalDateTime.now();
            if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer) {
                Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoRentPaymentBuyer,
                        claimType,
                        location,
                        RealEstate.econ.format(price));
            }
            else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                User u = RealEstate.ess.getUser(this.buyer);
                u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentPaymentBuyer,
                        claimType,
                        location,
                        RealEstate.econ.format(price)));
            }
            
            if(seller != null) {
                if(seller.isOnline() && RealEstate.instance.config.cfgMessageOwner) {
                    Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoRentPaymentOwner,
                            buyerPlayer.getName(),
                            claimType,
                            location,
                            RealEstate.econ.format(price));
                }
                else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                    User u = RealEstate.ess.getUser(this.owner);
                    u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentPaymentOwner,
                            buyerPlayer.getName(),
                            claimType,
                            location,
                            RealEstate.econ.format(price)));
                }
            }
        }
        else if (autoRenew) {
            if(buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer) {
                Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoRentPaymentBuyerCancelled,
                        claimType,
                        location,
                        RealEstate.econ.format(price));
            }
            else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                User u = RealEstate.ess.getUser(this.buyer);
                u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentPaymentBuyerCancelled,
                        claimType,
                        location,
                        RealEstate.econ.format(price)));
            }
            unRent(false);
            return;
        }
        else {
            unRent(true);
            return;
        }
        update();
        RealEstate.transactionsStore.saveData();
    }
    
    /**
     * Attempts to cancel this lease transaction.
     * <p>
     * If the player has admin permission or cancellation is forced, the lease is cancelled.
     * Otherwise, an error message is sent.
     * </p>
     *
     * @param p the player attempting cancellation
     * @param force if true, forces cancellation regardless of other conditions
     * @return true if the transaction is cancelled; false otherwise
     */
    @Override
    public boolean tryCancelTransaction(Player p, boolean force) {
        if(buyer != null) {
            if(p.hasPermission("realestate.admin") || force == true) {
                this.unRent(true);
                RealEstate.transactionsStore.cancelTransaction(this);
                return true;
            }
            else {
                if(p != null) {
                    IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
                    Messages.sendMessage(p, RealEstate.instance.messages.msgErrorCantCancelAlreadyRented,
                            claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim);
                }
                return false;
            }
        }
        else {
            RealEstate.transactionsStore.cancelTransaction(this);
            return true;
        }
    }
    
    /**
     * Processes player interaction with the rent sign.
     * <p>
     * Validates the player's eligibility to rent the claim and, if payment succeeds,
     * updates the claim permissions, logs the transaction, and destroys the sign.
     * </p>
     *
     * @param player the player interacting with the rent sign
     */
    @Override
    public void interact(Player player) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign); // getting by id creates errors for subclaims
        if(claim == null || claim.isWilderness()) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimDoesNotExist);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
        }
        String claimType = claim.isParentClaim() ? "claim" : "subclaim";
        String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
        
        if (owner != null && owner.equals(player.getUniqueId())) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyOwner, claimTypeDisplay);
            return;
        }
        if(claim.isParentClaim() && owner != null && !owner.equals(claim.getOwner())) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNotRentedByOwner, claimTypeDisplay);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
        }
        if(!player.hasPermission("realestate." + claimType + ".rent")) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoRentPermission, claimTypeDisplay);
            return;
        }
        if(player.getUniqueId().equals(buyer) || buyer != null) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyRented, claimTypeDisplay);
            return;
        }
        
        if(Utils.makePayment(owner, player.getUniqueId(), price, false, true)) { // if payment succeed
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
            
            if(owner != null) {
                OfflinePlayer seller = Bukkit.getOfflinePlayer(owner);
                String location = "[" + sign.getWorld().getName() + ", " + 
                        "X: " + sign.getBlockX() + ", " + 
                        "Y: " + sign.getBlockY() + ", " + 
                        "Z: " + sign.getBlockZ() + "]";
            
                if(RealEstate.instance.config.cfgMessageOwner && seller.isOnline()) {
                    Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimOwnerRented,
                            player.getName(),
                            claimTypeDisplay,
                            RealEstate.econ.format(price),
                            location);
                }
                else if(RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
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
    
    /**
     * Previews detailed lease information to a player.
     * <p>
     * This includes the lease header, current renter (if any), remaining time, and owner information.
     * </p>
     *
     * @param player the player to receive the lease preview
     */
    @Override
    public void preview(Player player) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
        if(player.hasPermission("realestate.info")) {
            String claimType = claim.isParentClaim() ? "claim" : "subclaim";
            String claimTypeDisplay = claim.isParentClaim() ? 
                RealEstate.instance.messages.keywordClaim :
                RealEstate.instance.messages.keywordSubclaim;
            String msg;
            msg = Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentHeader) + "\n";
            if(buyer == null) {
                msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoGeneralRentNoBuyer,
                        claimTypeDisplay,
                        RealEstate.econ.format(price),
                        Utils.getTime(duration, null, true)) + "\n";
                if(claimType.equalsIgnoreCase("claim")) {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoOwner,
                            claim.getOwnerName()) + "\n";
                }
                else {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoMainOwner,
                            claim.getParent().getOwnerName()) + "\n";
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoNote) + "\n";
                }
            }
            else {
                int days = Period.between(startDate.toLocalDate(), LocalDate.now()).getDays();
                Duration hours = Duration.between(startDate.toLocalTime(), LocalTime.now());
                if(hours.isNegative() && !hours.isZero()) {
                    hours = hours.plusHours(24);
                    days--;
                }
                int daysLeft = duration - days - 1; // subtract current day
                Duration timeRemaining = Duration.ofHours(24).minus(hours);
                msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoGeneralRentBuyer,
                        claimTypeDisplay,
                        Bukkit.getOfflinePlayer(buyer).getName(),
                        RealEstate.econ.format(price),
                        Utils.getTime(daysLeft, timeRemaining, true),
                        Utils.getTime(duration, null, true)) + "\n";
                if((owner != null && owner.equals(player.getUniqueId()) || buyer.equals(player.getUniqueId())) && RealEstate.instance.config.cfgEnableAutoRenew) {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoRentAutoRenew,
                            autoRenew ? RealEstate.instance.messages.keywordEnabled : RealEstate.instance.messages.keywordDisabled) + "\n";
                }
                if(claimType.equalsIgnoreCase("claim")) {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoOwner,
                            claim.getOwnerName()) + "\n";
                }
                else {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoMainOwner,
                            claim.getParent().getOwnerName()) + "\n";
                }
            }
            Messages.sendMessage(player, msg, false);
        }
        else {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoInfoPermission);
        }
    }
    
    /**
     * Sends a one-line summary of the lease transaction to a command sender.
     *
     * @param cs the command sender (console or player) to receive the summary
     */
    @Override
    public void msgInfo(CommandSender cs) {
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
