package me.EtienneDx.RealEstate.ClaimAPI.GriefPrevention;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import me.EtienneDx.RealEstate.ClaimEvents;
import me.EtienneDx.RealEstate.RealEstate;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimPermissionCheckEvent;

/**
 * Listens for GriefPrevention claim events and delegates them to the RealEstate claim event logic.
 * <p>
 * This listener handles claim permission checks and claim deletions to enforce RealEstate's rules.
 * </p>
 */
public class ClaimPermissionListener implements Listener {

    /**
     * Default constructor for ClaimPermissionListener.
     * <p>
     * This constructor is provided for documentation purposes. Use {@link #registerEvents()} to register this listener.
     * </p>
     */
    public ClaimPermissionListener() {
        // No initialization required.
    }

    /**
     * Registers this listener with the Bukkit PluginManager.
     * Call this method during plugin initialization to ensure that events are captured.
     */
    public void registerEvents() {
        PluginManager pm = RealEstate.instance.getServer().getPluginManager();
        pm.registerEvents(this, RealEstate.instance);
    }
    
    /**
     * Handles the ClaimPermissionCheckEvent triggered by GriefPrevention.
     * <p>
     * Maps GriefPrevention's required permission to a corresponding RealEstate {@link me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission}
     * and calls {@link ClaimEvents#onClaimPermission} to determine if the action should be denied.
     * If a denial reason is provided, it sets the denial reason on the event.
     * </p>
     *
     * @param event the claim permission check event from GriefPrevention
     */
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
        if (denialReason != null) {
            event.setDenialReason(() -> denialReason);
        }
    }

    /**
     * Handles the ClaimDeletedEvent triggered by GriefPrevention.
     * <p>
     * This method ensures that when a claim is deleted, RealEstate is notified so that any ongoing
     * transactions for that claim can be cancelled.
     * </p>
     *
     * @param event the claim deletion event from GriefPrevention
     */
    @EventHandler
    public void onClaimDeleted(ClaimDeletedEvent event) {
        ClaimEvents.onClaimDeleted(new GPClaim(event.getClaim()));
    }
}
