package me.EtienneDx.RealEstate;

import org.bukkit.entity.Player;

import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.Transactions.BoughtTransaction;
import me.EtienneDx.RealEstate.Transactions.Transaction;

public class ClaimEvents {
    public static String onClaimPermission(IClaim claim, Player player, ClaimPermission permission) {
        Transaction transaction = RealEstate.transactionsStore.getTransaction(claim);
        // we only have to remove the owner's access, the rest is handled by GP
        if (
        // if there is a transaction and the player is the owner
        transaction != null &&
                (player.getUniqueId().equals(transaction.getOwner()) ||
                        (claim.isAdminClaim() && player.hasPermission("griefprevention.adminclaims")))
                &&
                transaction instanceof BoughtTransaction &&
                ((BoughtTransaction) transaction).getBuyer() != null) {
            switch (permission) {
                case EDIT:
                    return Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantEdit);
                case ACCESS:
                    return Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantAccess);
                case BUILD:
                    return Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantBuild);
                case CONTAINER:
                    return Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantInventory);
                case MANAGE:
                    return Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantManage);
            }
        }

        if (permission == ClaimPermission.EDIT || permission == ClaimPermission.MANAGE) {
            for (IClaim child : claim.getChildren()) {
                Transaction tr = RealEstate.transactionsStore.getTransaction(child);
                if (tr != null &&
                        tr instanceof BoughtTransaction &&
                        ((BoughtTransaction) tr).getBuyer() != null) {
                    return Messages.getMessage(RealEstate.instance.messages.msgErrorSubclaimInTransaction);
                }
            }
        }
        return null;
    }

    public static void onClaimDeleted(IClaim claim) {
        Transaction tr = RealEstate.transactionsStore.getTransaction(claim);
        if(tr != null) tr.tryCancelTransaction(null, true);
        for (IClaim child : claim.getChildren()) {
            tr = RealEstate.transactionsStore.getTransaction(child);
            if(tr != null) tr.tryCancelTransaction(null, true);
        }
    }
}
