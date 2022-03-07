package me.EtienneDx.RealEstate.ClaimAPI.GriefDefender;

import com.griefdefender.api.data.PlayerData;

import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;

public class GDPlayerData implements IPlayerData {

    private PlayerData playerData;

    public GDPlayerData(PlayerData playerData) {
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
