package me.EtienneDx.RealEstate.ClaimAPI;

/**
 * The IPlayerData interface defines methods to retrieve and modify a player's claim block data.
 */
public interface IPlayerData {

    /**
     * Retrieves the number of accrued claim blocks for the player.
     *
     * @return the number of accrued claim blocks
     */
    public int getAccruedClaimBlocks();

    /**
     * Retrieves the number of bonus claim blocks for the player.
     *
     * @return the number of bonus claim blocks
     */
    public int getBonusClaimBlocks();

    /**
     * Sets the number of accrued claim blocks for the player.
     *
     * @param accruedClaimBlocks the new number of accrued claim blocks
     */
    public void setAccruedClaimBlocks(int accruedClaimBlocks);

    /**
     * Sets the number of bonus claim blocks for the player.
     *
     * @param bonusClaimBlocks the new number of bonus claim blocks
     */
    public void setBonusClaimBlocks(int bonusClaimBlocks);

    /**
     * Retrieves the total number of claim blocks remaining for the player.
     *
     * @return the number of remaining claim blocks
     */
    public int getRemainingClaimBlocks();
}
