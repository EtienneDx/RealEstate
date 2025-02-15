package me.EtienneDx.RealEstate.ClaimAPI.WorldGuard;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.Collections;
import java.util.UUID;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;

public class WGClaim implements IClaim {

    // A constant used to represent the "server" owner for admin claims.
    public static final UUID SERVER_UUID = new UUID(0L, 0L);

    private final ProtectedRegion region;
    private final World world;

    public WGClaim(ProtectedRegion region, World world) {
        this.region = region;
        this.world = world;
    }

    @Override
    public String getId() {
        return region.getId();
    }

    @Override
    public int getArea() {
        // Convert the region’s minimum and maximum BlockVector3 into dimensions.
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        int width = max.getX() - min.getX();
        int length = max.getZ() - min.getZ();
        return width * length;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public int getX() {
        return (int) region.getMinimumPoint().getX();
    }

    @Override
    public int getY() {
        return (int) region.getMinimumPoint().getY();
    }

    @Override
    public int getZ() {
        return (int) region.getMinimumPoint().getZ();
    }

    @Override
    public boolean isAdminClaim() {
        // If the region has no owners, treat it as an admin claim.
        return region.getOwners().getUniqueIds().isEmpty();
    }

    @Override
    public Iterable<IClaim> getChildren() {
        // WorldGuard does not support child claims; return an empty list.
        return Collections.emptyList();
    }

    @Override
    public boolean isWilderness() {
        // For our purposes, assume WorldGuard regions are never "wilderness".
        return false;
    }

    @Override
    public boolean isSubClaim() {
        // Not supported.
        return false;
    }

    @Override
    public boolean isParentClaim() {
        // Every region here is treated as a parent claim.
        return true;
    }

    @Override
    public IClaim getParent() {
        // Not supported.
        return null;
    }

    @Override
    public void dropPlayerPermissions(UUID player) {
        // Not implemented.
    }

    @Override
    public void addPlayerPermissions(UUID player, ClaimPermission permission) {
        // Not implemented.
    }

    @Override
    public void clearPlayerPermissions() {
        // Not implemented.
    }

    @Override
    public void removeManager(UUID player) {
        // Not implemented.
    }

    @Override
    public void addManager(UUID player) {
        // Not implemented.
    }

    @Override
    public void clearManagers() {
        // Not implemented.
    }

    @Override
    public UUID getOwner() {
        // If no owners, then this is an admin claim. Return the constant SERVER_UUID.
        if (isAdminClaim()) {
            return SERVER_UUID;
        }
        // Otherwise, return the first UUID in the owner set.
        return region.getOwners().getUniqueIds().iterator().next();
    }

    @Override
    public String getOwnerName() {
        // If it's an admin claim, return a fixed name.
        if (isAdminClaim()) {
            return "SERVER";
        }
        // Otherwise, look up the owner by UUID.
        UUID owner = getOwner();
        return (owner != null && Bukkit.getOfflinePlayer(owner) != null) 
                ? Bukkit.getOfflinePlayer(owner).getName() : "Unknown";
    }

    @Override
    public void setInheritPermissions(boolean inherit) {
        // Not supported.
    }
}
