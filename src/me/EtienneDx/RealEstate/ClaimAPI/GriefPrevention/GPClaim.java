package me.EtienneDx.RealEstate.ClaimAPI.GriefPrevention;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.ryanhamshire.GriefPrevention.Claim;

/**
 * GPClaim is an implementation of the {@link IClaim} interface for GriefPrevention claims.
 * It wraps a GriefPrevention {@link Claim} object and provides methods to access claim properties,
 * as well as to manage permissions and claim relationships.
 */
public class GPClaim implements IClaim {

    private Claim claim;

    /**
     * Constructs a new GPClaim that wraps the given GriefPrevention claim.
     *
     * @param claim the GriefPrevention claim to wrap
     */
    public GPClaim(Claim claim) {
        this.claim = claim;
    }

    /**
     * Returns the underlying GriefPrevention claim.
     *
     * @return the GriefPrevention {@link Claim} object
     */
    public Claim getClaim() {
        return claim;
    }

    /**
     * Returns the unique identifier of this claim.
     *
     * @return a string representing the claim's unique ID
     */
    @Override
    public String getId() {
        return claim.getID().toString();
    }

    /**
     * Returns the area of this claim.
     *
     * @return the area (in blocks) of the claim
     */
    @Override
    public int getArea() {
        return claim.getArea();
    }

    /**
     * Returns the world in which this claim is located.
     *
     * @return the {@link World} object for the claim's world
     */
    @Override
    public World getWorld() {
        return claim.getLesserBoundaryCorner().getWorld();
    }

    /**
     * Returns the X-coordinate of the claim's lesser boundary corner.
     *
     * @return the X-coordinate
     */
    @Override
    public int getX() {
        return claim.getLesserBoundaryCorner().getBlockX();
    }

    /**
     * Returns the Y-coordinate of the claim's lesser boundary corner.
     *
     * @return the Y-coordinate
     */
    @Override
    public int getY() {
        return claim.getLesserBoundaryCorner().getBlockY();
    }

    /**
     * Returns the Z-coordinate of the claim's lesser boundary corner.
     *
     * @return the Z-coordinate
     */
    @Override
    public int getZ() {
        return claim.getLesserBoundaryCorner().getBlockZ();
    }

    /**
     * Determines if this claim is an admin claim.
     *
     * @return true if the claim is an admin claim, false otherwise
     */
    @Override
    public boolean isAdminClaim() {
        return claim.isAdminClaim();
    }

    /**
     * Returns an {@link Iterable} over the child claims of this claim.
     *
     * @return an Iterable of {@link IClaim} objects representing child claims
     */
    @Override
    public Iterable<IClaim> getChildren() {
        return new Iterable<IClaim>() {
            @Override
            public Iterator<IClaim> iterator() {
                return new Iterator<IClaim>() {
                    private Iterator<Claim> iterator = claim.children.iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public IClaim next() {
                        return new GPClaim(iterator.next());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    /**
     * Indicates whether this claim represents wilderness.
     * For GriefPrevention claims, this always returns false.
     *
     * @return false
     */
    @Override
    public boolean isWilderness() {
        return false;
    }

    /**
     * Determines if this claim is a subclaim.
     *
     * @return true if the claim has a parent claim, false otherwise
     */
    @Override
    public boolean isSubClaim() {
        return claim.parent != null;
    }

    /**
     * Determines if this claim is a parent claim.
     *
     * @return true if the claim does not have a parent, false otherwise
     */
    @Override
    public boolean isParentClaim() {
        return claim.parent == null;
    }

    /**
     * Returns the parent claim of this claim.
     *
     * @return a new GPClaim representing the parent, or null if this is a parent claim
     */
    @Override
    public IClaim getParent() {
        return isSubClaim() ? new GPClaim(claim.parent) : null;
    }

    /**
     * Removes all player permissions for the specified player from this claim.
     *
     * @param player the UUID of the player whose permissions will be removed
     */
    @Override
    public void dropPlayerPermissions(UUID player) {
        claim.dropPermission(player.toString());
    }

    /**
     * Adds player permissions to this claim based on the specified {@link ClaimPermission}.
     *
     * @param player     the UUID of the player to add permissions for
     * @param permission the {@link ClaimPermission} to add
     */
    @Override
    public void addPlayerPermissions(UUID player, ClaimPermission permission) {
        me.ryanhamshire.GriefPrevention.ClaimPermission gpPermission = null;
        switch (permission) {
            case BUILD:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Build;
                break;
            case CONTAINER:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Inventory;
                break;
            case MANAGE:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Manage;
                break;
            case ACCESS:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Access;
                break;
            case EDIT:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Edit;
                break;
        }
        claim.setPermission(player.toString(), gpPermission);
    }

    /**
     * Removes the specified player from the list of managers for this claim.
     *
     * @param player the UUID of the player to remove as a manager
     */
    @Override
    public void removeManager(UUID player) {
        claim.managers.remove(player.toString());
    }

    /**
     * Returns the UUID of the owner of this claim.
     *
     * @return the owner's UUID
     */
    @Override
    public UUID getOwner() {
        return claim.ownerID;
    }

    /**
     * Returns the name of the owner of this claim.
     * If the owner is not available, a default server keyword is returned.
     *
     * @return the owner's name, or a default value if not available
     */
    @Override
    public String getOwnerName() {
        return getOwner() != null ? Bukkit.getPlayer(getOwner()).getName() : RealEstate.instance.messages.keywordTheServer;
    }

    /**
     * Sets whether this claim should inherit permissions from its parent.
     *
     * @param inherit true if the claim should inherit permissions, false otherwise
     */
    @Override
    public void setInheritPermissions(boolean inherit) {
        claim.setSubclaimRestrictions(!inherit);
    }

    /**
     * Clears all player permissions for this claim.
     */
    @Override
    public void clearPlayerPermissions() {
        claim.clearPermissions();
    }

    /**
     * Adds the specified player as a manager for this claim.
     *
     * @param player the UUID of the player to add as a manager
     */
    @Override
    public void addManager(UUID player) {
        claim.managers.add(player.toString());
    }

    /**
     * Clears all managers from this claim.
     */
    @Override
    public void clearManagers() {
        claim.managers.clear();
    }
}
