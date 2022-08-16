package me.EtienneDx.RealEstate;

import java.time.Duration;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;
import net.milkbowl.vault.economy.EconomyResponse;

public class Utils {
    public static boolean makePayment(UUID receiver, UUID giver, double amount, boolean msgReceiver, boolean msgGiver) {
        // seller might be null if it is the server
        OfflinePlayer giveTo = receiver != null ? Bukkit.getOfflinePlayer(receiver) : null;
        OfflinePlayer takeFrom = giver != null ? Bukkit.getOfflinePlayer(giver) : null;
        if (takeFrom != null && !RealEstate.econ.has(takeFrom, amount)) {
            if (takeFrom.isOnline() && msgGiver) {
                Messages.sendMessage(takeFrom.getPlayer(), RealEstate.instance.messages.msgErrorNoMoneySelf);
            }
            if (giveTo != null && giveTo.isOnline() && msgReceiver) {
                Messages.sendMessage(giveTo.getPlayer(), RealEstate.instance.messages.msgErrorNoMoneyOther, takeFrom.getName());
            }
            return false;
        }
        if (takeFrom != null) {
            EconomyResponse resp = RealEstate.econ.withdrawPlayer(takeFrom, amount);
            if (!resp.transactionSuccess()) {
                if (takeFrom.isOnline() && msgGiver) {
                    Messages.sendMessage(takeFrom.getPlayer(), RealEstate.instance.messages.msgErrorNoWithdrawSelf);
                }
                if (giveTo != null && giveTo.isOnline() && msgReceiver) {
                    Messages.sendMessage(giveTo.getPlayer(), RealEstate.instance.messages.msgErrorNoWithdrawOther);
                }
                return false;
            }
        }
        if (giveTo != null) {
            EconomyResponse resp = RealEstate.econ.depositPlayer(giveTo, amount);
            if (!resp.transactionSuccess()) {
                if (takeFrom != null && takeFrom.isOnline() && msgGiver) {
                    Messages.sendMessage(giveTo.getPlayer(), RealEstate.instance.messages.msgErrorNoDepositOther, giveTo.getName());
                }
                if (giveTo.isOnline() && msgReceiver) {
                    assert takeFrom != null;
                    Messages.sendMessage(takeFrom.getPlayer(), RealEstate.instance.messages.msgErrorNoDepositSelf, takeFrom.getName());
                }
                // refund
                RealEstate.econ.depositPlayer(takeFrom, amount);
                return false;
            }
        }

        return true;
    }

    public static String getTime(int days, Duration hours, boolean details) {
        String time = "";
        if (days >= 7) {
            time += (days / 7) + " " + RealEstate.instance.config.cfgDatesWeek + (days >= 14 ? "s" : "");
        }
        if (days % 7 > 0) {
            time += (time.isEmpty() ? "" : " ") + (days % 7) + " " + RealEstate.instance.config.cfgDatesDay + (days % 7 > 1 ? "s" : "");
        }
        if ((details || days < 7) && hours != null && hours.toHours() > 0) {
            time += (time.isEmpty() ? "" : " ") + hours.toHours() + " " + RealEstate.instance.config.cfgDatesHour + (hours.toHours() > 1 ? "s" : "");
        }
        if ((details || days == 0) && hours != null && (time.isEmpty() || hours.toMinutes() % 60 > 0)) {
            time += (time.isEmpty() ? "" : " ") + (hours.toMinutes() % 60) + " min" + (hours.toMinutes() % 60 > 1 ? "s" : "");
        }

        return time;
    }

    public static void transferClaim(IClaim claim, UUID buyer, UUID seller) {
        // blocks transfer :
        // if transfert is true, the seller will lose the blocks he had
        // and the buyer will get them
        // (that means the buyer will keep the same amount of remaining blocks after the transaction)
        if (claim.isParentClaim() && RealEstate.instance.config.cfgTransferClaimBlocks) {
            IPlayerData buyerData = RealEstate.claimAPI.getPlayerData(buyer);
            if (seller != null) {
                IPlayerData sellerData = RealEstate.claimAPI.getPlayerData(seller);

                // the seller has to provide the blocks
                sellerData.setBonusClaimBlocks(sellerData.getBonusClaimBlocks() - claim.getArea());
                if (sellerData.getBonusClaimBlocks() < 0)// can't have negative bonus claim blocks, so if need be, we take into the accrued
                {
                    sellerData.setAccruedClaimBlocks(sellerData.getAccruedClaimBlocks() + sellerData.getBonusClaimBlocks());
                    sellerData.setBonusClaimBlocks(0);
                }
            }

            // the buyer receive them
            buyerData.setBonusClaimBlocks(buyerData.getBonusClaimBlocks() + claim.getArea());
        }

        // start to change owner
        if (claim.isParentClaim()) {
            for (IClaim child : claim.getChildren()) {
                child.clearPlayerPermissions();
                child.clearManagers();
            }
        }
        claim.clearPlayerPermissions();

        try {
            if (claim.isParentClaim())
                RealEstate.claimAPI.changeClaimOwner(claim, buyer);
            else {
                claim.addPlayerPermissions(buyer, ClaimPermission.BUILD);
            }
        } catch (Exception e)// error occurs when trying to change subclaim owner
        {
            e.printStackTrace();
            return;
        }
        RealEstate.claimAPI.saveClaim(claim);

    }

    public static String getSignString(String str) {
        if (str.length() > 16)
            str = str.substring(0, 16);
        return str;
    }
}
