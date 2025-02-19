package me.EtienneDx.RealEstate;

import org.bukkit.entity.Player;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.Transactions.BoughtTransaction;
import me.EtienneDx.RealEstate.Transactions.Transaction;

/**
 * Provides helper methods to handle claim-related events.
 * <p>
 * These methods are used by the RealEstate plugin to check permissions on claims
 * involved in transactions and to cancel transactions when a claim is deleted.
 * </p>
 */
public class ClaimEvents {

    /**
     * Default constructor for ClaimEvents.
     * <p>
     * This class is not meant to be instantiated; all methods are static.
     * The constructor is provided only to eliminate Javadoc warnings.
     * </p>
     */
    public ClaimEvents() {
        // No instantiation required.
    }

    /**
     * Checks if a player is allowed to perform a certain action on a claim,
     * given an ongoing transaction.
     * <p>
     * The method examines an active transaction for the specified claim.
     * If the transaction is a {@link BoughtTransaction} with a non-null buyer and the player is either
     * the owner of the transaction or (in the case of admin claims) has the appropriate admin permission,
     * then a specific error message is returned based on the type of permission (EDIT, ACCESS, BUILD, CONTAINER, or MANAGE).
     * In addition, if the action involves editing or managing, the method checks all child claims
     * for active transactions and returns an error message if any are found.
     * If no conditions are met that would prevent the action, {@code null} is returned.
     * </p>
     *
     * @param claim      the claim being checked
     * @param player     the player attempting the action
     * @param permission the permission type being checked
     * @return an error message if the action is not allowed; {@code null} if the action is permitted
     */
    public static String onClaimPermission(IClaim claim, Player player, ClaimPermission permission) {
        Transaction transaction = RealEstate.transactionsStore.getTransaction(claim);
        // We only have to remove the owner's access; the rest is handled by GriefPrevention.
        if (transaction != null &&
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

        // Check child claims for active transactions if the permission is EDIT or MANAGE.
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

    /**
     * Handles the deletion of a claim by cancelling any ongoing transactions.
     * <p>
     * When a claim is deleted, this method cancels the transaction associated with the claim,
     * and then iterates over all child claims (if any) to cancel their transactions as well.
     * </p>
     *
     * @param claim the claim that has been deleted
     */
    public static void onClaimDeleted(IClaim claim) {
        Transaction tr = RealEstate.transactionsStore.getTransaction(claim);
        if (tr != null) {
            tr.tryCancelTransaction(null, true);
        }
        for (IClaim child : claim.getChildren()) {
            tr = RealEstate.transactionsStore.getTransaction(child);
            if (tr != null) {
                tr.tryCancelTransaction(null, true);
            }
        }
    }
}
