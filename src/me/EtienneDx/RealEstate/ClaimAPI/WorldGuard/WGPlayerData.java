package me.EtienneDx.RealEstate.ClaimAPI.WorldGuard;

import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;

/**
 * A dummy implementation of {@link IPlayerData} for WorldGuard.
 * <p>
 * Since WorldGuard does not supply claim block data, this implementation always returns zero
 * and does nothing for the setter methods.
 * </p>
 */
public class WGPlayerData implements IPlayerData {

	/**
	 * Instantiates a new WGPlayerData object.
	 */
	public WGPlayerData() {}
	
    /**
     * Returns the number of accrued claim blocks for a player.
     * <p>
     * Since WorldGuard does not use claim blocks, this method returns zero.
     * </p>
     *
     * @return 0 always.
     */
    @Override
    public int getAccruedClaimBlocks() {
        return 0;
    }

    /**
     * Returns the number of bonus claim blocks for a player.
     * <p>
     * Since WorldGuard does not use claim blocks, this method returns zero.
     * </p>
     *
     * @return 0 always.
     */
    @Override
    public int getBonusClaimBlocks() {
        return 0;
    }

    /**
     * Sets the number of accrued claim blocks for a player.
     * <p>
     * This is a no-operation implementation because WorldGuard does not support claim block data.
     * </p>
     *
     * @param accruedClaimBlocks the number of accrued claim blocks (ignored)
     */
    @Override
    public void setAccruedClaimBlocks(int accruedClaimBlocks) {
        // No operation performed.
    }

    /**
     * Sets the number of bonus claim blocks for a player.
     * <p>
     * This is a no-operation implementation because WorldGuard does not support claim block data.
     * </p>
     *
     * @param bonusClaimBlocks the number of bonus claim blocks (ignored)
     */
    @Override
    public void setBonusClaimBlocks(int bonusClaimBlocks) {
        // No operation performed.
    }

    /**
     * Returns the number of remaining claim blocks for a player.
     * <p>
     * Since WorldGuard does not utilize claim blocks, this method returns zero.
     * </p>
     *
     * @return 0 always.
     */
    @Override
    public int getRemainingClaimBlocks() {
        return 0;
    }
}
