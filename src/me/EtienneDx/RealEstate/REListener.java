package me.EtienneDx.RealEstate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.PluginManager;

import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.Transactions.Transaction;

/**
 * The REListener class handles various RealEstate-related events,
 * such as sign changes, player interactions with signs, and block break events.
 * <p>
 * This listener ensures that RealEstate transaction rules are enforced when players
 * interact with claim-related signs.
 * </p>
 */
public class REListener implements Listener {
	
	/**
	 * Constructs a new REListener instance.
	 */
	public REListener() {}

    /**
     * Registers this listener with the Bukkit PluginManager.
     */
    void registerEvents() {
        PluginManager pm = RealEstate.instance.getServer().getPluginManager();
        pm.registerEvents(this, RealEstate.instance);
    }

    /**
     * Handles sign change events.
     * <p>
     * This method checks if the sign being changed is intended for a RealEstate transaction
     * (sell, rent, lease, container rent, or auction) based on its first line.
     * It validates the sign’s contents (price, duration, permissions, etc.) and, if valid,
     * creates the appropriate transaction. If any check fails, the event is cancelled and
     * the sign is broken.
     * </p>
     *
     * @param event the SignChangeEvent triggered when a sign is changed
     */
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (RealEstate.instance.config.cfgSellKeywords.contains(event.getLine(0).toLowerCase()) ||
            RealEstate.instance.config.cfgLeaseKeywords.contains(event.getLine(0).toLowerCase()) ||
            RealEstate.instance.config.cfgRentKeywords.contains(event.getLine(0).toLowerCase()) ||
            RealEstate.instance.config.cfgContainerRentKeywords.contains(event.getLine(0).toLowerCase()) ||
            RealEstate.instance.config.cfgAuctionKeywords.contains(event.getLine(0).toLowerCase())) {
            Player player = event.getPlayer();
            Location loc = event.getBlock().getLocation();

            IClaim claim = RealEstate.claimAPI.getClaimAt(loc);
            if (claim == null || claim.isWilderness()) { // must have something to sell
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNotInClaim);
                event.setCancelled(true);
                event.getBlock().breakNaturally();
                return;
            }
            if (RealEstate.transactionsStore.anyTransaction(claim)) {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignOngoingTransaction);
                event.setCancelled(true);
                event.getBlock().breakNaturally();
                return;
            }
            if (RealEstate.transactionsStore.anyTransaction(claim.getParent())) {
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignParentOngoingTransaction);
                event.setCancelled(true);
                event.getBlock().breakNaturally();
                return;
            }
            for (IClaim c : claim.getChildren()) {
                if (RealEstate.transactionsStore.anyTransaction(c)) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignSubclaimOngoingTransaction);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
            }

            // Empty first line is considered a wish to sell.
            if (RealEstate.instance.config.cfgSellKeywords.contains(event.getLine(0).toLowerCase())) {
                if (!RealEstate.instance.config.cfgEnableSell) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignSellingDisabled);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                String type = claim.isParentClaim() ? "claim" : "subclaim";
                String typeDisplay = claim.isParentClaim() ?
                        RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
                if (!RealEstate.perms.has(player, "realestate." + type + ".sell")) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoSellPermission, typeDisplay);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Check for a valid price.
                double price;
                try {
                    price = getDouble(event, 1, RealEstate.instance.config.cfgPriceSellPerBlock * claim.getArea());
                } catch (NumberFormatException e) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidNumber, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                if (price <= 0) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNegativePrice, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                if ((price % 1) != 0 && !RealEstate.instance.config.cfgUseDecimalCurrency) { // if the price has a decimal number AND decimal currency is disabled
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNonIntegerPrice, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                if (claim.isAdminClaim()) {
                    if (!RealEstate.perms.has(player, "realestate.admin")) { // admin may sell admin claims
                        Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoAdminSellPermission, typeDisplay);
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        return;
                    }
                } else if (type.equals("claim") && !player.getUniqueId().equals(claim.getOwner())) { // only the owner may sell his claim
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNotOwner, typeDisplay);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // All checks passed; create the sell transaction.
                event.setCancelled(true); // Cancel the event so the sign can be updated elsewhere.
                RealEstate.transactionsStore.sell(claim, claim.isAdminClaim() ? null : player, price, event.getBlock().getLocation());
            }
            else if (RealEstate.instance.config.cfgRentKeywords.contains(event.getLine(0).toLowerCase()) ||
                     RealEstate.instance.config.cfgContainerRentKeywords.contains(event.getLine(0).toLowerCase())) { // Rent
                if (!RealEstate.instance.config.cfgEnableRent) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignRentingDisabled);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                String type = claim.isParentClaim() ? "claim" : "subclaim";
                String typeDisplay = claim.isParentClaim() ?
                        RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
                if (!RealEstate.perms.has(player, "realestate." + type + ".rent")) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoRentPermission, typeDisplay);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Check for a valid price.
                double price;
                try {
                    price = getDouble(event, 1, RealEstate.instance.config.cfgPriceRentPerBlock * claim.getArea());
                } catch (NumberFormatException e) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidNumber, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                if (price <= 0) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNegativePrice, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                if ((price % 1) != 0 && !RealEstate.instance.config.cfgUseDecimalCurrency) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNonIntegerPrice, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                if (event.getLine(2).isEmpty()) {
                    event.setLine(2, RealEstate.instance.config.cfgRentTime);
                }
                int duration = parseDuration(event.getLine(2));
                if (duration == 0) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidDuration, event.getLine(2),
                            "10 weeks",
                            "3 days",
                            "1 week 3 days");
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                if (claim.isAdminClaim()) {
                    if (!RealEstate.perms.has(player, "realestate.admin")) { // admin may rent admin claims
                        Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoAdminRentPermission, typeDisplay);
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        return;
                    }
                } else if (type.equals("claim") && !player.getUniqueId().equals(claim.getOwner())) { // only owner may rent his claim
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNotOwner, typeDisplay);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Create the rent transaction.
                event.setCancelled(true);
                RealEstate.transactionsStore.rent(claim, player, price, event.getBlock().getLocation(), duration,
                        RealEstate.instance.config.cfgRentKeywords.contains(event.getLine(0).toLowerCase()));
            }
            else if (RealEstate.instance.config.cfgLeaseKeywords.contains(event.getLine(0).toLowerCase())) { // Lease
                if (!RealEstate.instance.config.cfgEnableLease) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignLeasingDisabled);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                String type = claim.isParentClaim() ? "claim" : "subclaim";
                String typeDisplay = claim.isParentClaim() ?
                        RealEstate.instance.messages.keywordClaim :
                        RealEstate.instance.messages.keywordSubclaim;
                if (!RealEstate.perms.has(player, "realestate." + type + ".lease")) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoLeasePermission, typeDisplay);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Check for a valid price.
                double price;
                try {
                    price = getDouble(event, 1, RealEstate.instance.config.cfgPriceLeasePerBlock * claim.getArea());
                } catch (NumberFormatException e) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidNumber, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                if (price <= 0) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNegativePrice, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                if ((price % 1) != 0 && !RealEstate.instance.config.cfgUseDecimalCurrency) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNonIntegerPrice, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                if (event.getLine(2).isEmpty()) {
                    event.setLine(2, "" + RealEstate.instance.config.cfgLeasePayments);
                }
                int paymentsCount;
                try {
                    paymentsCount = Integer.parseInt(event.getLine(2));
                } catch (Exception e) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidNumber, event.getLine(2));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                if (event.getLine(3).isEmpty()) {
                    event.setLine(3, RealEstate.instance.config.cfgLeaseTime);
                }
                int frequency = parseDuration(event.getLine(3));
                if (frequency == 0) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidDuration, event.getLine(3),
                            "10 weeks",
                            "3 days",
                            "1 week 3 days");
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                if (claim.isAdminClaim()) {
                    if (!RealEstate.perms.has(player, "realestate.admin")) { // admin may lease admin claims
                        Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoAdminLeasePermission, typeDisplay);
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        return;
                    }
                } else if (type.equals("claim") && !player.getUniqueId().equals(claim.getOwner())) { // only owner may lease his claim
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNotOwner, typeDisplay);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Create the lease transaction.
                event.setCancelled(true);
                RealEstate.transactionsStore.lease(claim, player, price, event.getBlock().getLocation(), frequency, paymentsCount);
            }
            else if (RealEstate.instance.config.cfgAuctionKeywords.contains(event.getLine(0).toLowerCase())) {
                if (!RealEstate.instance.config.cfgEnableAuction) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignAuctionDisabled);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                String type = claim.isParentClaim() ? "claim" : "subclaim";
                String typeDisplay = claim.isParentClaim() ?
                        RealEstate.instance.messages.keywordClaim :
                        RealEstate.instance.messages.keywordSubclaim;
                if (!RealEstate.perms.has(player, "realestate." + type + ".auction")) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoAuctionPermission, typeDisplay);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Check for a valid price.
                double price;
                try {
                    price = getDouble(event, 1, RealEstate.instance.config.cfgPriceAuctionPerBlock * claim.getArea());
                } catch (NumberFormatException e) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidNumber, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                if (price <= 0) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNegativePrice, event.getLine(1));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Check for a valid bid step.
                double bidStep;
                try {
                    bidStep = getDouble(event, 2, RealEstate.instance.config.cfgPriceAuctionBidStep);
                } catch (NumberFormatException e) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidNumber, event.getLine(2));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }
                if (bidStep <= 0) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNegativeBidStep, event.getLine(2));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Check for a valid duration.
                if (event.getLine(3).isEmpty()) {
                    event.setLine(3, RealEstate.instance.config.cfgLeaseTime);
                }
                int duration = parseDuration(event.getLine(3));
                if (duration == 0) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorInvalidDuration, event.getLine(3),
                            "10 weeks",
                            "3 days",
                            "1 week 3 days");
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                if (claim.isAdminClaim()) {
                    if (!RealEstate.perms.has(player, "realestate.admin")) { // admin may auction admin claims
                        Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoAdminAuctionPermission, typeDisplay);
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        return;
                    }
                } else if (type.equals("claim") && !player.getUniqueId().equals(claim.getOwner())) { // only owner may auction his claim
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNotOwner, typeDisplay);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return;
                }

                // Create the auction transaction.
                event.setCancelled(true);
                RealEstate.transactionsStore.auction(claim, player, price, event.getBlock().getLocation(), duration, bidStep);
            }
        }
    }

    /**
     * Parses a duration string expressed in weeks and days.
     * <p>
     * The expected format is for example "1w 3d", where "w" represents weeks and "d" represents days.
     * </p>
     *
     * @param line the duration string from the sign
     * @return the total duration in days, or 0 if the format is invalid
     */
    private int parseDuration(String line) {
        Pattern p = Pattern.compile("^(?:(?<weeks>\\d{1,2}) ?w(?:eeks?)?)? ?(?:(?<days>\\d{1,2}) ?d(?:ays?)?)?$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(line);
        if (!line.isEmpty() && m.matches()) {
            int ret = 0;
            if (m.group("weeks") != null)
                ret += 7 * Integer.parseInt(m.group("weeks"));
            if (m.group("days") != null)
                ret += Integer.parseInt(m.group("days"));
            return ret;
        }
        return 0;
    }

    /**
     * Retrieves a double value from a specific line in a sign change event.
     * <p>
     * If the specified line is empty, the default value is set on that line.
     * </p>
     *
     * @param event        the sign change event
     * @param line         the line number to read
     * @param defaultValue the default value to use if the line is empty
     * @return the parsed double value
     * @throws NumberFormatException if the value cannot be parsed as a double
     */
    private double getDouble(SignChangeEvent event, int line, double defaultValue) throws NumberFormatException {
        if (event.getLine(line).isEmpty()) { // if no price specified, use default
            event.setLine(line, Double.toString(defaultValue));
        }
        return Double.parseDouble(event.getLine(line));
    }

    /**
     * Handles player interaction events with RealEstate signs.
     * <p>
     * When a player right-clicks a block with a sign, if it is a RealEstate sign,
     * this method either previews the transaction (if the player is sneaking) or
     * initiates an interaction with the transaction.
     * </p>
     *
     * @param event the player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.HAND) &&
            event.getClickedBlock().getState() instanceof Sign) {
            RealEstateSign s = new RealEstateSign((Sign) event.getClickedBlock().getState());
            // Check if it is a RealEstate sign.
            if (s.isRealEstateSign()) {
                Player player = event.getPlayer();
                IClaim claim = RealEstate.claimAPI.getClaimAt(event.getClickedBlock().getLocation());

                if (!RealEstate.transactionsStore.anyTransaction(claim)) {
                    Messages.sendMessage(player, RealEstate.instance.messages.msgErrorSignNoTransaction);
                    event.getClickedBlock().breakNaturally();
                    event.setCancelled(true);
                    return;
                }

                Transaction tr = RealEstate.transactionsStore.getTransaction(claim);
                if (player.isSneaking())
                    tr.preview(player);
                else
                    tr.interact(player);
            }
        }
    }

    /**
     * Handles block break events for RealEstate signs.
     * <p>
     * When a RealEstate sign is broken, this method checks whether the player breaking the sign
     * has the proper permissions. If the player is not authorized, the event is cancelled.
     * If authorized, the method attempts to cancel the transaction associated with the sign.
     * </p>
     *
     * @param event the block break event
     */
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            IClaim claim = RealEstate.claimAPI.getClaimAt(event.getBlock().getLocation());
            if (claim != null && !claim.isWilderness()) {
                Transaction tr = RealEstate.transactionsStore.getTransaction(claim);
                if (tr != null && event.getBlock().equals(tr.getHolder())) {
                    if (event.getPlayer() != null && tr.getOwner() != null && !event.getPlayer().getUniqueId().equals(tr.getOwner()) &&
                        !RealEstate.perms.has(event.getPlayer(), "realestate.destroysigns")) {
                        Messages.sendMessage(event.getPlayer(), RealEstate.instance.messages.msgErrorSignNotAuthor);
                        event.setCancelled(true);
                        return;
                    } else if (event.getPlayer() != null && tr.getOwner() == null && !RealEstate.perms.has(event.getPlayer(), "realestate.admin")) {
                        Messages.sendMessage(event.getPlayer(), RealEstate.instance.messages.msgErrorSignNotAdmin);
                        event.setCancelled(true);
                        return;
                    }
                    // Sign has been broken; attempt to cancel the associated transaction.
                    if (!tr.tryCancelTransaction(event.getPlayer())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
