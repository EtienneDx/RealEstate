package me.EtienneDx.RealEstate.ClaimAPI.GriefPrevention;

import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;
import me.ryanhamshire.GriefPrevention.PlayerData;

public class GPPlayerData implements IPlayerData
{
    private PlayerData playerData;

    public GPPlayerData(PlayerData playerData)
    {
        this.playerData = playerData;
    }

    @Override
    public int getAccruedClaimBlocks() {
        return playerData.getAccruedClaimBlocks();
    }

    @Override
    public int getBonusClaimBlocks() {
        return playerData.getBonusClaimBlocks();
    }

    @Override
    public void setAccruedClaimBlocks(int accruedClaimBlocks) {
        playerData.setAccruedClaimBlocks(accruedClaimBlocks);
    }

    @Override
    public void setBonusClaimBlocks(int bonusClaimBlocks) {
        playerData.setBonusClaimBlocks(bonusClaimBlocks);
    }

    @Override
    public int getRemainingClaimBlocks() {
        return playerData.getRemainingClaimBlocks();
    }
    
}
