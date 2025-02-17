package me.EtienneDx.RealEstate.ClaimAPI;

/**
 * An enumeration of the various permission types that can be applied to a claim.
 * <p>
 * These permissions define what actions a player is allowed to perform on a claim,
 * such as building, accessing containers, managing, or editing the claim.
 * </p>
 */
public enum ClaimPermission {
    /**
     * Allows a player to build on the claim.
     */
    BUILD,

    /**
     * Allows a player to access containers (e.g., chests) within the claim.
     */
    CONTAINER,

    /**
     * Allows a player to manage the claim.
     */
    MANAGE,

    /**
     * Allows a player to edit the claim.
     */
    EDIT,

    /**
     * Allows a player to access the claim.
     */
    ACCESS
}
