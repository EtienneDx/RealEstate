package me.EtienneDx.RealEstate.ClaimAPI.GriefDefender;

import java.util.Iterator;
import java.util.UUID;

import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustType;
import com.griefdefender.api.claim.TrustTypes;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;

/**
 * Implementation of the {@link IClaim} interface using the GriefDefender API.
 * <p>
 * This class wraps a GriefDefender {@link Claim} and provides access to claim properties
 * such as area, coordinates, owner, and permissions management.
 * </p>
 */
public class GDClaim implements IClaim {

    /**
     * The underlying GriefDefender claim.
     */
    private Claim claim;

    /**
     * Constructs a new GDClaim that wraps the given GriefDefender claim.
     *
     * @param claim the GriefDefender claim to wrap
     */
    public GDClaim(Claim claim) {
        this.claim = claim;
    }

    /**
     * Returns the underlying GriefDefender claim.
     *
     * @return the wrapped GriefDefender {@link Claim}
     */
    public Claim getClaim() {
        return claim;
    }

    /**
     * Gets the unique identifier for this claim.
     *
     * @return the claim's unique ID as a String
     */
    @Override
    public String getId() {
        return claim.getUniqueId().toString();
    }

    /**
     * Calculates the area of the claim.
     *
     * @return the area of the claim in blocks
     */
    @Override
    public int getArea() {
        return claim.getArea();
    }

    /**
     * Retrieves the Bukkit {@link World} where the claim is located.
     *
     * @return the Bukkit World corresponding to the claim's world
     */
    @Override
    public World getWorld() {
        return Bukkit.getWorld(claim.getWorldUniqueId());
    }

    /**
     * Gets the X coordinate of the claim's lesser boundary corner.
     *
     * @return the X coordinate
     */
    @Override
    public int getX() {
        return claim.getLesserBoundaryCorner().getX();
    }

    /**
     * Gets the Y coordinate of the claim's lesser boundary corner.
     *
     * @return the Y coordinate
     */
    @Override
    public int getY() {
        return claim.getLesserBoundaryCorner().getY();
    }

    /**
     * Gets the Z coordinate of the claim's lesser boundary corner.
     *
     * @return the Z coordinate
     */
    @Override
    public int getZ() {
        return claim.getLesserBoundaryCorner().getZ();
    }

    /**
     * Determines if this claim is an admin claim.
     *
     * @return {@code true} if this claim is an admin claim, {@code false} otherwise
     */
    @Override
    public boolean isAdminClaim() {
        return claim.isAdminClaim();
    }

    /**
     * Returns an iterable over the child claims.
     * <p>
     * In GriefDefender, child claims are obtained via the {@code getChildren(true)} method.
     * </p>
     *
     * @return an {@link Iterable} of {@link IClaim} representing child claims
     */
    @Override
    public Iterable<IClaim> getChildren() {
        return new Iterable<IClaim>() {
            @Override
            public Iterator<IClaim> iterator() {
                return new Iterator<IClaim>() {
                    private Iterator<Claim> it = claim.getChildren(true).iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public IClaim next() {
                        return new GDClaim(it.next());
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }
        };
    }

    /**
     * Checks if this claim represents wilderness.
     *
     * @return {@code true} if this claim is wilderness, {@code false} otherwise
     */
    @Override
    public boolean isWilderness() {
        return claim.isWilderness();
    }

    /**
     * Determines if this claim is a subclaim.
     *
     * @return {@code true} if this claim has a non-wilderness parent; {@code false} otherwise
     */
    @Override
    public boolean isSubClaim() {
        return claim.getParent() != null && !claim.getParent().isWilderness();
    }

    /**
     * Determines if this claim is a parent claim.
     *
     * @return {@code true} if this claim has no parent or its parent is wilderness
     */
    @Override
    public boolean isParentClaim() {
        return claim.getParent() == null || claim.getParent().isWilderness();
    }

    /**
     * Retrieves the parent claim of this claim, if any.
     *
     * @return the parent {@link IClaim} or {@code null} if this is a parent claim
     */
    @Override
    public IClaim getParent() {
        return isParentClaim() ? null : new GDClaim(claim.getParent());
    }

    /**
     * Drops all player permissions for the specified player in this claim.
     *
     * @param player the UUID of the player whose permissions should be dropped
     */
    @Override
    public void dropPlayerPermissions(UUID player) {
        claim.removeUserTrust(player, TrustTypes.NONE);
    }

    /**
     * Adds a specific permission for a player to this claim.
     *
     * @param player     the UUID of the player to grant permission to
     * @param permission the {@link ClaimPermission} to grant
     */
    @Override
    public void addPlayerPermissions(UUID player, ClaimPermission permission) {
        TrustType trust = TrustTypes.NONE;
        switch (permission) {
            case ACCESS:
                trust = TrustTypes.ACCESSOR;
                break;
            case BUILD:
                trust = TrustTypes.BUILDER;
                break;
            case CONTAINER:
                trust = TrustTypes.CONTAINER;
                break;
            case EDIT:
                trust = TrustTypes.MANAGER;
                break;
            case MANAGE:
                trust = TrustTypes.MANAGER;
                break;
        }
        claim.addUserTrust(player, trust);
    }

    /**
     * Clears all player permissions from this claim.
     */
    @Override
    public void clearPlayerPermissions() {
        claim.removeAllUserTrusts();
    }

    /**
     * Removes a manager from this claim.
     * <p>
     * Not supported in GriefDefender.
     * </p>
     *
     * @param player the UUID of the manager to remove
     */
    @Override
    public void removeManager(UUID player) {
        // No equivalent in GD.
    }

    /**
     * Adds a manager to this claim.
     * <p>
     * Not supported in GriefDefender.
     * </p>
     *
     * @param player the UUID of the manager to add
     */
    @Override
    public void addManager(UUID player) {
        // No equivalent in GD.
    }

    /**
     * Clears all managers from this claim.
     * <p>
     * Not supported in GriefDefender.
     * </p>
     */
    @Override
    public void clearManagers() {
        // No equivalent in GD.
    }

    /**
     * Retrieves the owner of the claim.
     *
     * @return the UUID of the claim's owner
     */
    @Override
    public UUID getOwner() {
        return claim.getOwnerUniqueId();
    }

    /**
     * Retrieves the name of the claim's owner.
     *
     * @return the owner's name as provided by GriefDefender
     */
    @Override
    public String getOwnerName() {
        return claim.getOwnerName();
    }

    /**
     * Sets whether the claim should inherit permissions from its parent claim.
     *
     * @param inherit {@code true} to inherit permissions, {@code false} otherwise
     */
    @Override
    public void setInheritPermissions(boolean inherit) {
        claim.getData().setInheritParent(inherit);
    }
}
