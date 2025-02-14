package me.EtienneDx.RealEstate.ClaimAPI.GriefPrevention;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import me.EtienneDx.RealEstate.ClaimEvents;
import me.EtienneDx.RealEstate.RealEstate;
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
        me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission permission = null;
        switch (event.getRequiredPermission()) {
            case Access:
                permission = me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission.ACCESS;
                break;
            case Build:
                permission = me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission.BUILD;
                break;
            case Edit:
                permission = me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission.EDIT;
                break;
            case Inventory:
                permission = me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission.CONTAINER;
                break;
            case Manage:
                permission = me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission.MANAGE;
                break;
        }

        String denialReason = ClaimEvents.onClaimPermission(
            new GPClaim(event.getClaim()),
            event.getCheckedPlayer(),
            permission
        );
        if(denialReason != null)
        {
            event.setDenialReason(() -> denialReason);
        }
    }

    // more of a safety measure, normally it shouldn't be needed
    @EventHandler
    public void onClaimDeleted(ClaimDeletedEvent event) {
        ClaimEvents.onClaimDeleted(new GPClaim(event.getClaim()));
    }
}
