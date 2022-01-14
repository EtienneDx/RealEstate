package me.EtienneDx.RealEstate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import me.EtienneDx.RealEstate.Transactions.BoughtTransaction;
import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.ryanhamshire.GriefPrevention.events.ClaimPermissionCheckEvent;

public class ClaimPermissionListener implements Listener {
	void registerEvents()
	{
		PluginManager pm = RealEstate.instance.getServer().getPluginManager();

		pm.registerEvents(this, RealEstate.instance);
	}
    
    @EventHandler
    public void onClaimPermission(ClaimPermissionCheckEvent event) {
        Transaction b = RealEstate.transactionsStore.getTransaction(event.getClaim());
        if(
            b != null &&
            event.getCheckedUUID().equals(b.getOwner()) &&
            b instanceof BoughtTransaction &&
            ((BoughtTransaction)b).getBuyer() != null
        ) {
            switch(event.getRequiredPermission()) {
                case Edit:
                            event.setDenialReason(() -> RealEstate.instance.messages.msgErrorClaimInTransactionCantEdit);
                    break;
                case Access:
                            event.setDenialReason(() -> RealEstate.instance.messages.msgErrorClaimInTransactionCantAccess);
                    break;
                case Build:
                            event.setDenialReason(() -> RealEstate.instance.messages.msgErrorClaimInTransactionCantBuild);
                    break;
                case Inventory:
                            event.setDenialReason(() -> RealEstate.instance.messages.msgErrorClaimInTransactionCantInventory);
                    break;
                case Manage:
                            event.setDenialReason(() -> RealEstate.instance.messages.msgErrorClaimInTransactionCantManage);
                    break;
                default:
                    break;
            }
        }
    }
}
