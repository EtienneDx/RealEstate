package me.EtienneDx.RealEstate.ClaimAPI.WorldGuard;

import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;

public class WGPlayerData implements IPlayerData {

    // Since WorldGuard does not supply claim block data,
    // we return zero or do nothing in our dummy implementation.

    @Override
    public int getAccruedClaimBlocks() {
        return 0;
    }

    @Override
    public int getBonusClaimBlocks() {
        return 0;
    }

    @Override
    public void setAccruedClaimBlocks(int accruedClaimBlocks) {
        // No-op.
    }

    @Override
    public void setBonusClaimBlocks(int bonusClaimBlocks) {
        // No-op.
    }

    @Override
    public int getRemainingClaimBlocks() {
        return 0;
    }
}
