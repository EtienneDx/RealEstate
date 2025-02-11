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
	    if (event == null || event.getClaim() == null || event.getDenialReason() == null) {
	        return;
	    }

	    // Retrieve the active transaction for this claim
	    Transaction transaction = RealEstate.transactionsStore.getTransaction(event.getClaim());

	    // Ensure transaction exists and player is valid
	    if (transaction == null || event.getCheckedPlayer() == null || event.getCheckedUUID() == null) {
	        return;
	    }

	    // Debugging: Log transaction type
	    RealEstate.instance.getLogger().info("Transaction found: " + transaction.getClass().getSimpleName());

	    // Check if the player is the owner or has admin permissions
	    boolean isOwner = event.getCheckedUUID().equals(transaction.getOwner());
	    boolean isAdmin = event.getClaim().isAdminClaim() && event.getCheckedPlayer().hasPermission("griefprevention.adminclaims");

	    if (RealEstate.instance.config.DebugMode) {
	    	RealEstate.instance.getLogger().info("isOwner: " + isOwner);
	    	RealEstate.instance.getLogger().info("isAdmin: " + isAdmin);
	    	RealEstate.instance.getLogger().info("isAdminClaim: " + event.getClaim().isAdminClaim());
	    	RealEstate.instance.getLogger().info("Player has GP.AdminClaims: " + event.getCheckedPlayer().hasPermission("griefprevention.adminclaims"));
	    	RealEstate.instance.getLogger().info("User Who Triggered: " + event.getCheckedUUID());
	    	RealEstate.instance.getLogger().info("Reason for Denial: " + event.getDenialReason());
	    }
	    
	    // Ensure it's a valid "BoughtTransaction" with an actual buyer, but NOT a rental
	    if ((isOwner || isAdmin) && transaction instanceof BoughtTransaction) {
	        BoughtTransaction boughtTransaction = (BoughtTransaction) transaction;
	        if (boughtTransaction.getBuyer() != null) {
	            switch (event.getRequiredPermission()) {
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
	    }

	    // Check if permission is Edit or Manage and deny access to subclaims in transactions
	    if (event.getRequiredPermission() == ClaimPermission.Edit || event.getRequiredPermission() == ClaimPermission.Manage) {
	        for (Claim child : event.getClaim().children) {
	            if (child == null) continue; // Avoid potential null references

	            Transaction childTransaction = RealEstate.transactionsStore.getTransaction(child);
	            if (childTransaction instanceof BoughtTransaction) {
	                BoughtTransaction boughtTransaction = (BoughtTransaction) childTransaction;
	                if (boughtTransaction.getBuyer() != null) {
	                    event.setDenialReason(() -> Messages.getMessage(RealEstate.instance.messages.msgErrorSubclaimInTransaction));
	                    return; // Stop further checks after setting a denial reason
	                }
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
