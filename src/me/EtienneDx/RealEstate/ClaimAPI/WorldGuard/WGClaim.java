package me.EtienneDx.RealEstate.ClaimAPI.WorldGuard;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.Collections;
import java.util.UUID;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;

/**
 * WGClaim is an implementation of the {@link IClaim} interface for WorldGuard regions.
 * <p>
 * This class wraps a WorldGuard {@link ProtectedRegion} and provides methods to access claim information
 * such as its area, location, ownership, and permission management.
 * </p>
 */
public class WGClaim implements IClaim {

    /**
     * A constant used to represent the "server" owner for admin claims.
     */
    public static final UUID SERVER_UUID = new UUID(0L, 0L);

    private final ProtectedRegion region;
    private final World world;

    /**
     * Constructs a new WGClaim instance.
     *
     * @param region the WorldGuard ProtectedRegion representing the claim.
     * @param world  the Bukkit World in which the claim exists.
     */
    public WGClaim(ProtectedRegion region, World world) {
        this.region = region;
        this.world = world;
    }

    /**
     * Returns the unique identifier of this claim.
     *
     * @return the region ID as a String.
     */
    @Override
    public String getId() {
        return region.getId();
    }

    /**
     * Calculates and returns the area of this claim.
     * <p>
     * The area is computed by subtracting the minimum point from the maximum point along the X and Z axes,
     * then multiplying the resulting width and length.
     * </p>
     *
     * @return the area of the claim.
     */
    @Override
    public int getArea() {
        // Convert the region’s minimum and maximum BlockVector3 into dimensions.
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        int width = max.getX() - min.getX();
        int length = max.getZ() - min.getZ();
        return width * length;
    }

    /**
     * Returns the world in which this claim is located.
     *
     * @return the Bukkit World object.
     */
    @Override
    public World getWorld() {
        return world;
    }

    /**
     * Returns the X-coordinate of the claim's minimum point.
     *
     * @return the X-coordinate as an integer.
     */
    @Override
    public int getX() {
        return (int) region.getMinimumPoint().getX();
    }

    /**
     * Returns the Y-coordinate of the claim's minimum point.
     *
     * @return the Y-coordinate as an integer.
     */
    @Override
    public int getY() {
        return (int) region.getMinimumPoint().getY();
    }

    /**
     * Returns the Z-coordinate of the claim's minimum point.
     *
     * @return the Z-coordinate as an integer.
     */
    @Override
    public int getZ() {
        return (int) region.getMinimumPoint().getZ();
    }

    /**
     * Determines whether this claim is an admin claim.
     * <p>
     * A claim is considered an admin claim if it has no owners.
     * </p>
     *
     * @return {@code true} if the region has no owners, {@code false} otherwise.
     */
    @Override
    public boolean isAdminClaim() {
        // If the region has no owners, treat it as an admin claim.
        return region.getOwners().getUniqueIds().isEmpty();
    }

    /**
     * Returns the child claims of this claim.
     * <p>
     * WorldGuard does not support child claims in this implementation,
     * so an empty list is returned.
     * </p>
     *
     * @return an empty iterable.
     */
    @Override
    public Iterable<IClaim> getChildren() {
        // WorldGuard does not support child claims; return an empty list.
        return Collections.emptyList();
    }

    /**
     * Indicates whether this claim is considered "wilderness".
     * <p>
     * In this implementation, WorldGuard regions are never considered wilderness.
     * </p>
     *
     * @return {@code false} always.
     */
    @Override
    public boolean isWilderness() {
        // For our purposes, assume WorldGuard regions are never "wilderness".
        return false;
    }

    /**
     * Indicates whether this claim is a subclaim.
     * <p>
     * Not supported in this implementation.
     * </p>
     *
     * @return {@code false}.
     */
    @Override
    public boolean isSubClaim() {
        // Not supported.
        return false;
    }

    /**
     * Indicates whether this claim is a parent claim.
     * <p>
     * Every WorldGuard region is treated as a parent claim in this implementation.
     * </p>
     *
     * @return {@code true} always.
     */
    @Override
    public boolean isParentClaim() {
        // Every region here is treated as a parent claim.
        return true;
    }

    /**
     * Returns the parent claim of this claim.
     * <p>
     * Not supported in this implementation.
     * </p>
     *
     * @return {@code null}.
     */
    @Override
    public IClaim getParent() {
        // Not supported.
        return null;
    }

    /**
     * Removes the specified player's permissions from this claim.
     * <p>
     * This is done by removing the player's name from the region's members list.
     * </p>
     *
     * @param player the UUID of the player whose permissions should be removed.
     */
    @Override
    public void dropPlayerPermissions(UUID player) {
        // Remove the player's name from the region's members list.
        String name = Bukkit.getOfflinePlayer(player).getName();
        if (name != null) {
            region.getMembers().removePlayer(name);
        }
    }

    /**
     * Adds the specified permission for the given player to this claim.
     * <p>
     * For this implementation, all permissions are granted by adding the player's name
     * to the region's members list.
     * </p>
     *
     * @param player     the UUID of the player.
     * @param permission the claim permission to add.
     */
    @Override
    public void addPlayerPermissions(UUID player, ClaimPermission permission) {
        // For this implementation, all permissions are granted by adding the player to the members.
        String name = Bukkit.getOfflinePlayer(player).getName();
        if (name != null) {
            region.getMembers().addPlayer(name);
        }
    }

    /**
     * Clears all player permissions from this claim.
     * <p>
     * This is done by clearing the entire members list of the region.
     * </p>
     */
    @Override
    public void clearPlayerPermissions() {
        // Clear all members from the region.
        region.getMembers().getPlayers().clear();
    }

    /**
     * Removes a manager from this claim.
     * <p>
     * Not supported in this implementation.
     * </p>
     *
     * @param player the UUID of the player to remove as manager.
     */
    @Override
    public void removeManager(UUID player) {
        // Not supported in this implementation.
    }

    /**
     * Adds a manager to this claim.
     * <p>
     * Not supported in this implementation.
     * </p>
     *
     * @param player the UUID of the player to add as manager.
     */
    @Override
    public void addManager(UUID player) {
        // Not supported in this implementation.
    }

    /**
     * Clears all managers from this claim.
     * <p>
     * Not supported in this implementation.
     * </p>
     */
    @Override
    public void clearManagers() {
        // Not supported in this implementation.
    }

    /**
     * Returns the owner of this claim.
     * <p>
     * If the region has no owners, this method returns the constant {@link #SERVER_UUID}
     * to indicate an admin claim. Otherwise, it returns the first owner's UUID.
     * </p>
     *
     * @return the owner's UUID, or {@code SERVER_UUID} if no owners exist.
     */
    @Override
    public UUID getOwner() {
        // If no owners, then this is an admin claim. Return the constant SERVER_UUID.
        if (isAdminClaim()) {
            return SERVER_UUID;
        }
        // Otherwise, return the first UUID in the owner set.
        return region.getOwners().getUniqueIds().iterator().next();
    }

    /**
     * Returns the owner's name of this claim.
     * <p>
     * If this is an admin claim, returns "SERVER". Otherwise, it attempts to look up the owner's name
     * via Bukkit's OfflinePlayer; if not found, returns "Unknown".
     * </p>
     *
     * @return the owner's name as a String.
     */
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

    /**
     * Sets whether this claim should inherit permissions from its parent.
     * <p>
     * Not supported in this implementation.
     * </p>
     *
     * @param inherit if {@code true}, the claim should inherit permissions (ignored).
     */
    @Override
    public void setInheritPermissions(boolean inherit) {
        // Not supported.
    }
}
