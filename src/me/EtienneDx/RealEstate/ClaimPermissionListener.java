package me.EtienneDx.RealEstate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import me.EtienneDx.RealEstate.Transactions.BoughtTransaction;
import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimPermissionCheckEvent;

public class ClaimPermissionListener implements Listener {
	void registerEvents()
	{
		PluginManager pm = RealEstate.instance.getServer().getPluginManager();

		pm.registerEvents(this, RealEstate.instance);
	}
    
    @EventHandler
    public void onClaimPermission(ClaimPermissionCheckEvent event) {
        Transaction transaction = RealEstate.transactionsStore.getTransaction(event.getClaim());
        // we only have to remove the owner's access, the rest is handled by GP
        if(
            // if there is a transaction and the player is the owner
            transaction != null &&
            (
                event.getCheckedUUID().equals(transaction.getOwner()) ||
                (event.getClaim().isAdminClaim() && event.getCheckedPlayer().hasPermission("griefprevention.adminclaims"))
            ) &&
            transaction instanceof BoughtTransaction &&
                ((BoughtTransaction)transaction).getBuyer() != null
        ) {
            switch(event.getRequiredPermission()) {
                case Edit:
                    event.setDenialReason(() -> Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantEdit));
                    break;
                case Access:
                    event.setDenialReason(() -> Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantAccess));
                    break;
                case Build:
                    event.setDenialReason(() -> Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantBuild));
                    break;
                case Inventory:
                    event.setDenialReason(() -> Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantInventory));
                    break;
                case Manage:
                    event.setDenialReason(() -> Messages.getMessage(RealEstate.instance.messages.msgErrorClaimInTransactionCantManage));
                    break;
                default:
                    break;
            }
        }

        if(event.getRequiredPermission() == ClaimPermission.Edit || event.getRequiredPermission() == ClaimPermission.Manage) {
            for (Claim child : event.getClaim().children) {
                Transaction tr = RealEstate.transactionsStore.getTransaction(child);
                if(tr != null && 
                    tr instanceof BoughtTransaction &&
                    ((BoughtTransaction)tr).getBuyer() != null
                ) {
                    event.setDenialReason(() -> Messages.getMessage(RealEstate.instance.messages.msgErrorSubclaimInTransaction));
                }
            }
        }
    }

    // more of a safety measure, normally it shouldn't be needed
    @EventHandler
    public void onClaimDeleted(ClaimDeletedEvent event) {
        Transaction tr = RealEstate.transactionsStore.getTransaction(event.getClaim());
        if(tr != null) tr.tryCancelTransaction(null, true);
        for (Claim child : event.getClaim().children) {
            tr = RealEstate.transactionsStore.getTransaction(child);
            if(tr != null) tr.tryCancelTransaction(null, true);
        }
    }
}
