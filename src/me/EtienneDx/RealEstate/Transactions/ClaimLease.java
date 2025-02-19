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

import com.earth2me.essentials.User;

import me.EtienneDx.RealEstate.Messages;
import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.RealEstateSign;
import me.EtienneDx.RealEstate.Utils;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import net.md_5.bungee.api.ChatColor;

/**
 * Represents a lease transaction for a claim.
 * <p>
 * A ClaimLease handles the process of leasing a claim by a player,
 * including updating the sign with lease details, processing payments,
 * and transferring claim ownership when the lease expires.
 * </p>
 */
public class ClaimLease extends BoughtTransaction {

    /** The time when the last lease payment was made. */
    public LocalDateTime lastPayment = null;
    
    /** The frequency (in days) at which lease payments are due. */
    public int frequency;
    
    /** The number of remaining lease payments. */
    public int paymentsLeft;
    
    /**
     * Constructs a ClaimLease transaction from a serialized map.
     *
     * @param map the map containing serialized transaction data
     */
    public ClaimLease(Map<String, Object> map) {
        super(map);
        if (map.get("lastPayment") != null)
            lastPayment = LocalDateTime.parse((String) map.get("lastPayment"), DateTimeFormatter.ISO_DATE_TIME);
        frequency = (int) map.get("frequency");
        paymentsLeft = (int) map.get("paymentsLeft");
    }
    
    /**
     * Constructs a new ClaimLease transaction.
     *
     * @param claim         the claim being leased
     * @param player        the player leasing the claim
     * @param price         the price per payment period
     * @param sign          the location of the lease sign
     * @param frequency     the number of days between lease payments
     * @param paymentsLeft  the total number of lease payments required
     */
    public ClaimLease(IClaim claim, Player player, double price, Location sign, int frequency, int paymentsLeft) {
        super(claim, player, price, sign);
        this.frequency = frequency;
        this.paymentsLeft = paymentsLeft;
    }

    /**
     * Serializes the ClaimLease transaction to a Map.
     *
     * @return a Map containing all the relevant data for this lease transaction
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        if (lastPayment != null)
            map.put("lastPayment", lastPayment.format(DateTimeFormatter.ISO_DATE_TIME));
        map.put("frequency", frequency);
        map.put("paymentsLeft", paymentsLeft);
        return map;
    }
    
    /**
     * Updates the lease transaction.
     * <p>
     * If no buyer is assigned, it updates the lease sign with payment information.
     * If a buyer exists, it checks if the lease period has expired and processes a payment.
     * </p>
     *
     * @return {@code true} if the transaction is finished and should be removed; {@code false} otherwise.
     */
    @Override
    public boolean update() {
        if (buyer == null) { // Lease has not yet begun.
            if (sign.getBlock().getState() instanceof Sign) {
                RealEstateSign s = new RealEstateSign((Sign) sign.getBlock().getState());
                s.setLine(0, Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));
                s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceLease);
                if (RealEstate.instance.config.cfgUseCurrencySymbol) {
                    if (RealEstate.instance.config.cfgUseDecimalCurrency == false) {
                        s.setLine(2, paymentsLeft + "x " + RealEstate.instance.config.cfgCurrencySymbol + " " + (int) Math.round(price));
                    } else {
                        s.setLine(2, paymentsLeft + "x " + RealEstate.instance.config.cfgCurrencySymbol + " " + price);
                    }
                } else {
                    if (RealEstate.instance.config.cfgUseDecimalCurrency == false) {
                        s.setLine(2, paymentsLeft + "x " + (int) Math.round(price) + " " + RealEstate.econ.currencyNamePlural());
                    } else {
                        s.setLine(2, paymentsLeft + "x " + price + " " + RealEstate.econ.currencyNamePlural());
                    }
                }
                s.setLine(3, Utils.getTime(frequency, null, false));
                s.update(true);
            } else {
                return true;
            }
        } else {
            int days = Period.between(lastPayment.toLocalDate(), LocalDate.now()).getDays();
            Duration hours = Duration.between(lastPayment.toLocalTime(), LocalTime.now());
            if (hours.isNegative() && !hours.isZero()) {
                hours = hours.plusHours(24);
                days--;
            }
            if (days >= frequency) { // Lease payment due.
                payLease();
            }
        }
        return false;
    }

    /**
     * Processes a lease payment.
     * <p>
     * If the payment is successful, the lease payment counter is decremented,
     * notifications are sent to the buyer and owner, and if all payments have been made,
     * the claim is transferred to the buyer.
     * </p>
     */
    private void payLease() {
        if (buyer == null)
            return;

        OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(buyer);
        OfflinePlayer seller = owner == null ? null : Bukkit.getOfflinePlayer(owner);
        
        String claimType = RealEstate.claimAPI.getClaimAt(sign).isParentClaim()
                ? RealEstate.instance.messages.keywordClaim
                : RealEstate.instance.messages.keywordSubclaim;
        String location = "[" + sign.getWorld().getName() + ", X: " + sign.getBlockX() +
                ", Y: " + sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]";
        
        if (Utils.makePayment(owner, buyer, price, false, false)) {
            lastPayment = LocalDateTime.now();
            paymentsLeft--;
            if (paymentsLeft > 0) {
                if (buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer) {
                    Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyer,
                            claimType,
                            location, 
                            RealEstate.econ.format(price), 
                            paymentsLeft + "");
                } else if (RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                    User u = RealEstate.ess.getUser(this.buyer);
                    u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyer,
                            claimType,
                            location, 
                            RealEstate.econ.format(price), 
                            paymentsLeft + ""));
                }
                
                if (owner != null) {
                    if (seller != null && seller.isOnline() && RealEstate.instance.config.cfgMessageOwner) {
                        Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwner,
                                buyerPlayer.getName(),
                                claimType,
                                location, 
                                RealEstate.econ.format(price), 
                                paymentsLeft + "");
                    } else if (RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                        User u = RealEstate.ess.getUser(this.owner);
                        u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwner,
                                buyerPlayer.getName(),
                                claimType,
                                location,
                                RealEstate.econ.format(price),
                                paymentsLeft + ""));
                    }
                }
            } else {
                if (buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer) {
                    Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyerFinal,
                            claimType,
                            location, 
                            RealEstate.econ.format(price));
                } else if (RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                    User u = RealEstate.ess.getUser(this.buyer);
                    u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyerFinal,
                            claimType,
                            location,
                            RealEstate.econ.format(price)));
                }
                
                if (seller != null && seller.isOnline() && RealEstate.instance.config.cfgMessageOwner) {
                    Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwnerFinal,
                            buyerPlayer.getName(),
                            claimType,
                            location, 
                            RealEstate.econ.format(price));
                } else if (RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                    User u = RealEstate.ess.getUser(this.owner);
                    u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwnerFinal,
                            buyerPlayer.getName(),
                            claimType,
                            location,
                            RealEstate.econ.format(price)));
                }
                IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
                Utils.transferClaim(claim, buyer, owner);
                RealEstate.transactionsStore.cancelTransaction(this); // Lease is complete.
            }
        } else {
            this.exitLease();
        }
        // No further update is needed since the sign is removed after lease completion.
        RealEstate.transactionsStore.saveData();
    }
    
    /**
     * Cancels the lease and resets permissions.
     * <p>
     * Notifies both buyer and owner about the cancellation if applicable.
     * </p>
     */
    private void exitLease() {
        if (buyer != null) {
            OfflinePlayer buyerPlayer = Bukkit.getOfflinePlayer(buyer);
            OfflinePlayer seller = owner == null ? null : Bukkit.getOfflinePlayer(owner);
            
            IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
            
            String claimType = claim.isParentClaim()
                    ? RealEstate.instance.messages.keywordClaim
                    : RealEstate.instance.messages.keywordSubclaim;
            String location = "[" + sign.getWorld().getName() + ", X: " +
                    sign.getBlockX() + ", Y: " +
                    sign.getBlockY() + ", Z: " + sign.getBlockZ() + "]";
            
            if (buyerPlayer.isOnline() && RealEstate.instance.config.cfgMessageBuyer) {
                Messages.sendMessage(buyerPlayer.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyerCancelled,
                        claimType,
                        location, 
                        RealEstate.econ.format(price));
            } else if (RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                User u = RealEstate.ess.getUser(this.buyer);
                u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentBuyerCancelled,
                        claimType,
                        location,
                        RealEstate.econ.format(price)));
            }
            if (seller != null && seller.isOnline() && RealEstate.instance.config.cfgMessageOwner) {
                Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwnerCancelled,
                        buyerPlayer.getName(),
                        claimType,
                        location, 
                        RealEstate.econ.format(price));
            } else if (RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
                User u = RealEstate.ess.getUser(this.owner);
                u.addMail(Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeasePaymentOwnerCancelled,
                        buyerPlayer.getName(),
                        claimType,
                        location,
                        RealEstate.econ.format(price)));
            }
            
            claim.removeManager(buyer);
            claim.dropPlayerPermissions(buyer);
        } else {
            getHolder().breakNaturally(); // Sign remains if lease never started.
        }
        RealEstate.transactionsStore.cancelTransaction(this);
    }

    /**
     * Attempts to cancel the lease transaction.
     * <p>
     * If the player has administrative privileges or the cancellation is forced,
     * the lease is cancelled; otherwise, an error message is sent.
     * </p>
     *
     * @param p     the player attempting to cancel the lease
     * @param force whether cancellation is forced
     * @return {@code true} if the transaction was successfully cancelled, {@code false} otherwise
     */
    @Override
    public boolean tryCancelTransaction(Player p, boolean force) {
        if (buyer != null) {
            if (p.hasPermission("realestate.admin") && force == true) {
                this.exitLease();
                return true;
            } else {
                IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
                if (p != null) {
                    Messages.sendMessage(p, RealEstate.instance.messages.msgErrorCantCancelAlreadyLeased,
                            claim.isParentClaim()
                                    ? RealEstate.instance.messages.keywordClaim
                                    : RealEstate.instance.messages.keywordSubclaim);
                }
                return false;
            }
        } else {
            RealEstate.transactionsStore.cancelTransaction(this);
            return true;
        }
    }
    
    /**
     * Processes player interaction with the lease sign.
     * <p>
     * Handles lease purchase by validating the player's permissions, ensuring the claim is valid,
     * and processing the payment.
     * </p>
     *
     * @param player the player interacting with the lease sign
     */
    @Override
    public void interact(Player player) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign); // Use claim ID to avoid errors with subclaims.
        if (claim == null || claim.isWilderness()) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimDoesNotExist);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
        }
        String claimType = claim.isParentClaim() ? "claim" : "subclaim";
        String claimTypeDisplay = claim.isParentClaim()
                ? RealEstate.instance.messages.keywordClaim
                : RealEstate.instance.messages.keywordSubclaim;
        
        if (owner != null && owner.equals(player.getUniqueId())) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyOwner, claimTypeDisplay);
            return;
        }
        if (claim.isParentClaim() && owner != null && !owner.equals(claim.getOwner())) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNotLeasedByOwner, claimTypeDisplay);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
        }
        if (!player.hasPermission("realestate." + claimType + ".lease")) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoLeasePermission, claimTypeDisplay);
            return;
        }
        if (player.getUniqueId().equals(buyer) || buyer != null) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyLeased, claimTypeDisplay);
            return;
        }
        
        if (Utils.makePayment(owner, player.getUniqueId(), price, false, true)) { // If payment succeeds.
            buyer = player.getUniqueId();
            lastPayment = LocalDateTime.now();
            paymentsLeft--;
            claim.addPlayerPermissions(buyer, ClaimPermission.BUILD);
            claim.addPlayerPermissions(player.getUniqueId(), ClaimPermission.MANAGE);
            RealEstate.claimAPI.saveClaim(claim);
            getHolder().breakNaturally(); // Leases do not show remaining time on sign.
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

            if (owner != null) {
                OfflinePlayer seller = Bukkit.getOfflinePlayer(owner);
                if (RealEstate.instance.config.cfgMessageOwner && seller.isOnline()) {
                    Messages.sendMessage(seller.getPlayer(), RealEstate.instance.messages.msgInfoClaimOwnerLeaseStarted,
                            player.getName(),
                            claimTypeDisplay,
                            RealEstate.econ.format(price),
                            location,
                            paymentsLeft + "");
                } else if (RealEstate.instance.config.cfgMailOffline && RealEstate.ess != null) {
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

    /**
     * Sends a preview message to the player with lease details.
     * <p>
     * This method constructs and sends a multi-line message to the player
     * showing the lease header, lease details (number of payments, price, duration),
     * and the current owner information.
     * </p>
     *
     * @param player the player receiving the preview message
     */
    @Override
    public void preview(Player player) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
        if (player.hasPermission("realestate.info")) {
            String claimType = claim.isParentClaim() ? "claim" : "subclaim";
            String claimTypeDisplay = claim.isParentClaim()
                    ? RealEstate.instance.messages.keywordClaim
                    : RealEstate.instance.messages.keywordSubclaim;
            String msg;
            msg = Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoLeaseHeader) + "\n";
            if (buyer == null) {
                msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoGeneralLeaseNoBuyer,
                        claimTypeDisplay,
                        paymentsLeft + "",
                        RealEstate.econ.format(price),
                        Utils.getTime(frequency, null, true)) + "\n";
                if (claimType.equalsIgnoreCase("claim")) {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoOwner,
                            claim.getOwnerName()) + "\n";
                } else {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoMainOwner,
                            claim.getParent().getOwnerName()) + "\n";
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoNote) + "\n";
                }
            } else {
                int days = Period.between(lastPayment.toLocalDate(), LocalDate.now()).getDays();
                Duration hours = Duration.between(lastPayment.toLocalTime(), LocalTime.now());
                if (hours.isNegative() && !hours.isZero()) {
                    hours = hours.plusHours(24);
                    days--;
                }
                int daysLeft = frequency - days - 1; // Remove current day.
                Duration timeRemaining = Duration.ofHours(24).minus(hours);
                msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoGeneralLeaseBuyer,
                        claimTypeDisplay,
                        Bukkit.getOfflinePlayer(buyer).getName(),
                        RealEstate.econ.format(price),
                        paymentsLeft + "",
                        Utils.getTime(daysLeft, timeRemaining, true),
                        Utils.getTime(frequency, null, true)) + "\n";
                if (claimType.equalsIgnoreCase("claim")) {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoOwner,
                            claim.getOwnerName()) + "\n";
                } else {
                    msg += Messages.getMessage(RealEstate.instance.messages.msgInfoClaimInfoMainOwner,
                            claim.getParent().getOwnerName()) + "\n";
                }
            }
            Messages.sendMessage(player, msg, false);
        } else {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoInfoPermission);
        }
    }

    /**
     * Sends a one-line information message about the lease transaction to a CommandSender.
     *
     * @param cs the CommandSender to send the information message to
     */
    @Override
    public void msgInfo(CommandSender cs) {
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

    /**
     * Retrieves the frequency of lease payments.
     *
     * @return the number of days between lease payments
     */
    public int getFrequency() {
        return this.frequency;
    }

    /**
     * Retrieves the number of remaining lease payments.
     *
     * @return the number of payments left
     */
    public int getPaymentsLeft() {
        return this.paymentsLeft;
    }
}
