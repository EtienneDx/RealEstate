package me.EtienneDx.RealEstate.ClaimAPI;

import java.util.UUID;
import org.bukkit.World;

/**
 * Represents a claim in the RealEstate plugin.
 * <p>
 * An IClaim provides methods to access details about a claim,
 * such as its location, area, ownership, and permissions management.
 * </p>
 */
public interface IClaim {

    /**
     * Retrieves the unique identifier for this claim.
     *
     * @return the claim's unique ID as a String
     */
    public String getId();

    /**
     * Retrieves the area of this claim in blocks.
     *
     * @return the area of the claim
     */
    public int getArea();

    /**
     * Retrieves the Bukkit {@link World} in which this claim is located.
     *
     * @return the world of the claim
     */
    public World getWorld();

    /**
     * Retrieves the X coordinate of this claim's location.
     *
     * @return the X coordinate
     */
    public int getX();

    /**
     * Retrieves the Y coordinate of this claim's location.
     *
     * @return the Y coordinate
     */
    public int getY();

    /**
     * Retrieves the Z coordinate of this claim's location.
     *
     * @return the Z coordinate
     */
    public int getZ();

    /**
     * Determines if this claim is an admin claim.
     *
     * @return {@code true} if this is an admin claim, {@code false} otherwise
     */
    public boolean isAdminClaim();

    /**
     * Retrieves an iterable collection of child claims.
     *
     * @return an {@link Iterable} of child claims
     */
    public Iterable<IClaim> getChildren();

    /**
     * Checks if this claim represents wilderness.
     *
     * @return {@code true} if this is a wilderness claim, {@code false} otherwise
     */
    public boolean isWilderness();

    /**
     * Determines if this claim is a subclaim.
     *
     * @return {@code true} if this is a subclaim, {@code false} otherwise
     */
    public boolean isSubClaim();

    /**
     * Determines if this claim is a parent claim.
     *
     * @return {@code true} if this is a parent claim, {@code false} otherwise
     */
    public boolean isParentClaim();

    /**
     * Retrieves the parent claim of this claim, if any.
     *
     * @return the parent claim, or {@code null} if there is no parent
     */
    public IClaim getParent();

    /**
     * Removes all permissions for the specified player from this claim.
     *
     * @param player the UUID of the player whose permissions should be removed
     */
    public void dropPlayerPermissions(UUID player);

    /**
     * Grants a specific permission to a player for this claim.
     *
     * @param player the UUID of the player to grant permission to
     * @param permission the {@link ClaimPermission} to grant
     */
    public void addPlayerPermissions(UUID player, ClaimPermission permission);

    /**
     * Clears all player-specific permissions from this claim.
     */
    public void clearPlayerPermissions();

    /**
     * Removes the specified player from the list of managers for this claim.
     *
     * @param player the UUID of the manager to remove
     */
    public void removeManager(UUID player);

    /**
     * Adds the specified player as a manager for this claim.
     *
     * @param player the UUID of the manager to add
     */
    public void addManager(UUID player);

    /**
     * Clears all managers from this claim.
     */
    public void clearManagers();

    /**
     * Retrieves the owner of this claim.
     *
     * @return the UUID of the claim's owner
     */
    public UUID getOwner();

    /**
     * Retrieves the name of the claim's owner.
     *
     * @return the owner's name as a String
     */
    public String getOwnerName();

    /**
     * Sets whether this claim should inherit permissions from its parent claim.
     *
     * @param inherit {@code true} to enable inheritance; {@code false} to disable it
     */
    public void setInheritPermissions(boolean inherit);
}
