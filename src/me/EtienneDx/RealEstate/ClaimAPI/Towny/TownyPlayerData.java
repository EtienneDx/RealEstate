package me.EtienneDx.RealEstate.ClaimAPI.Towny;

import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;
import java.util.UUID;

/**
 * A stub implementation of IPlayerData for Towny.
 * <p>
 * Towny does not use claim blocks so these methods return zero or do nothing.
 * </p>
 */
public class TownyPlayerData implements IPlayerData {
    
    private UUID playerUUID;
    
    /**
     * Constructs a new TownyPlayerData for the specified player UUID.
     *
     * @param playerUUID the UUID of the player
     */
    public TownyPlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
    
    /**
     * Returns the number of accrued claim blocks.
     * <p>
     * Always returns 0 as Towny does not use claim blocks.
     * </p>
     *
     * @return 0
     */
    @Override
    public int getAccruedClaimBlocks() {
        return 0;
    }

    /**
     * Returns the number of bonus claim blocks.
     * <p>
     * Always returns 0 as Towny does not use claim blocks.
     * </p>
     *
     * @return 0
     */
    @Override
    public int getBonusClaimBlocks() {
        return 0;
    }

    /**
     * Sets the accrued claim blocks.
     * <p>
     * This operation is not applicable for Towny.
     * </p>
     *
     * @param accruedClaimBlocks the number of accrued claim blocks (ignored)
     */
    @Override
    public void setAccruedClaimBlocks(int accruedClaimBlocks) {
        // Not applicable for Towny.
    }

    /**
     * Sets the bonus claim blocks.
     * <p>
     * This operation is not applicable for Towny.
     * </p>
     *
     * @param bonusClaimBlocks the number of bonus claim blocks (ignored)
     */
    @Override
    public void setBonusClaimBlocks(int bonusClaimBlocks) {
        // Not applicable for Towny.
    }

    /**
     * Returns the remaining claim blocks.
     * <p>
     * Always returns 0 as Towny does not use claim blocks.
     * </p>
     *
     * @return 0
     */
    @Override
    public int getRemainingClaimBlocks() {
        return 0;
    }
}
