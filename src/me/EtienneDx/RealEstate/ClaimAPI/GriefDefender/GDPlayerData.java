package me.EtienneDx.RealEstate.ClaimAPI.GriefDefender;

import com.griefdefender.api.data.PlayerData;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;

/**
 * GDPlayerData is an implementation of the {@link IPlayerData} interface for GriefDefender.
 * It wraps a GriefDefender {@link PlayerData} instance to provide player claim block information.
 */
public class GDPlayerData implements IPlayerData {

    /**
     * The underlying GriefDefender PlayerData instance.
     */
    private PlayerData playerData;

    /**
     * Constructs a new GDPlayerData instance using the specified PlayerData.
     *
     * @param playerData the GriefDefender PlayerData instance to wrap
     */
    public GDPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    /**
     * Retrieves the number of accrued claim blocks for the player.
     *
     * @return the number of accrued claim blocks
     */
    @Override
    public int getAccruedClaimBlocks() {
        return playerData.getAccruedClaimBlocks();
    }

    /**
     * Retrieves the number of bonus claim blocks for the player.
     *
     * @return the number of bonus claim blocks
     */
    @Override
    public int getBonusClaimBlocks() {
        return playerData.getBonusClaimBlocks();
    }

    /**
     * Sets the number of accrued claim blocks for the player.
     *
     * @param accruedClaimBlocks the new number of accrued claim blocks
     */
    @Override
    public void setAccruedClaimBlocks(int accruedClaimBlocks) {
        playerData.setAccruedClaimBlocks(accruedClaimBlocks);
    }

    /**
     * Sets the number of bonus claim blocks for the player.
     *
     * @param bonusClaimBlocks the new number of bonus claim blocks
     */
    @Override
    public void setBonusClaimBlocks(int bonusClaimBlocks) {
        playerData.setBonusClaimBlocks(bonusClaimBlocks);
    }

    /**
     * Retrieves the number of remaining claim blocks available to the player.
     *
     * @return the number of remaining claim blocks
     */
    @Override
    public int getRemainingClaimBlocks() {
        return playerData.getRemainingClaimBlocks();
    }
}
