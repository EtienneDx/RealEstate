package me.EtienneDx.RealEstate.ClaimAPI.GriefPrevention;

import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;
import me.ryanhamshire.GriefPrevention.PlayerData;

/**
 * GPPlayerData is an implementation of the {@link IPlayerData} interface for GriefPrevention.
 * It wraps a GriefPrevention {@link PlayerData} object and provides access to a player's claim block data.
 */
public class GPPlayerData implements IPlayerData {

    private PlayerData playerData;

    /**
     * Constructs a new GPPlayerData instance that wraps the provided PlayerData.
     *
     * @param playerData the GriefPrevention PlayerData instance to wrap
     */
    public GPPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    /**
     * Retrieves the number of accrued claim blocks for the player.
     *
     * @return the accrued claim blocks
     */
    @Override
    public int getAccruedClaimBlocks() {
        return playerData.getAccruedClaimBlocks();
    }

    /**
     * Retrieves the number of bonus claim blocks for the player.
     *
     * @return the bonus claim blocks
     */
    @Override
    public int getBonusClaimBlocks() {
        return playerData.getBonusClaimBlocks();
    }

    /**
     * Sets the number of accrued claim blocks for the player.
     *
     * @param accruedClaimBlocks the new value for accrued claim blocks
     */
    @Override
    public void setAccruedClaimBlocks(int accruedClaimBlocks) {
        playerData.setAccruedClaimBlocks(accruedClaimBlocks);
    }

    /**
     * Sets the number of bonus claim blocks for the player.
     *
     * @param bonusClaimBlocks the new value for bonus claim blocks
     */
    @Override
    public void setBonusClaimBlocks(int bonusClaimBlocks) {
        playerData.setBonusClaimBlocks(bonusClaimBlocks);
    }

    /**
     * Retrieves the number of remaining claim blocks available for the player.
     *
     * @return the remaining claim blocks
     */
    @Override
    public int getRemainingClaimBlocks() {
        return playerData.getRemainingClaimBlocks();
    }
}
