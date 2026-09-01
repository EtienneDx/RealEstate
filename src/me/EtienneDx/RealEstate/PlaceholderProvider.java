package me.EtienneDx.RealEstate;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.EtienneDx.RealEstate.Transactions.ClaimLease;
import me.EtienneDx.RealEstate.Transactions.ClaimRent;
import me.EtienneDx.RealEstate.Transactions.ClaimSell;
import me.EtienneDx.RealEstate.Transactions.ClaimTransaction;
import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * Registers PlaceholderAPI placeholders exposing the price of the ongoing
 * transaction (if any) at a player's current location.
 * <p>
 * Supported placeholders: {@code %realestate_claim_rent_amount%},
 * {@code %realestate_claim_sell_amount%}, {@code %realestate_claim_lease_amount%}.
 * </p>
 */
public class PlaceholderProvider extends PlaceholderExpansion {

    public PlaceholderProvider() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            register();
        }
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        return onRequest(player, identifier);
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        final Player player = offlinePlayer instanceof Player ? (Player) offlinePlayer : null;
        if (player == null) {
            return "";
        }

        final Transaction transaction = RealEstate.transactionsStore.getTransaction(player);
        if (!(transaction instanceof ClaimTransaction)) {
            return "";
        }
        final ClaimTransaction claimTransaction = (ClaimTransaction) transaction;

        switch (identifier) {
            case "claim_rent_amount":
                if (!(claimTransaction instanceof ClaimRent)) {
                    return "";
                }
                return RealEstate.econ.format(claimTransaction.price);
            case "claim_sell_amount":
                if (!(claimTransaction instanceof ClaimSell)) {
                    return "";
                }
                return RealEstate.econ.format(claimTransaction.price);
            case "claim_lease_amount":
                if (!(claimTransaction instanceof ClaimLease)) {
                    return "";
                }
                return RealEstate.econ.format(claimTransaction.price);
            default:
                return null;
        }
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "RealEstate";
    }

    @Override
    public String getVersion() {
        return RealEstate.instance.getDescription().getVersion();
    }

    @Override
    public String getAuthor() {
        return String.join(", ", RealEstate.instance.getDescription().getAuthors());
    }

    @Override
    public boolean persist() {
        return true;
    }
}
