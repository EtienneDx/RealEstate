package me.EtienneDx.RealEstate.ClaimAPI;

public interface IPlayerData
{
    public int getAccruedClaimBlocks();

    public int getBonusClaimBlocks();

    public void setAccruedClaimBlocks(int accruedClaimBlocks);
    
    public void setBonusClaimBlocks(int bonusClaimBlocks);

    public int getRemainingClaimBlocks();
}
