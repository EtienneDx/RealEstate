package me.EtienneDx.RealEstate.ClaimAPI.GriefPrevention;

import java.util.UUID;

import org.bukkit.Location;

import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.IClaimAPI;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;
import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Claim;

/**
 * Implementation of the {@link IClaimAPI} interface for GriefPrevention.
 * <p>
 * This class provides methods to interact with GriefPrevention claims,
 * including retrieving a claim at a given location, saving claim data,
 * accessing player claim data, changing claim ownership, and registering
 * claim-related events.
 * </p>
 */
public class GriefPreventionAPI implements IClaimAPI {

    /**
     * Default constructor for GriefPreventionAPI.
     * <p>
     * No additional initialization is required.
     * </p>
     */
    public GriefPreventionAPI() {
        // Default constructor.
    }

    @Override
    public IClaim getClaimAt(Location location) {
        Claim gpclaim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        if (gpclaim == null) {
            return null;
        }
        return new GPClaim(gpclaim);
    }

    @Override
    public void saveClaim(IClaim claim) {
        if (claim instanceof GPClaim)
            GriefPrevention.instance.dataStore.saveClaim(((GPClaim) claim).getClaim());
    }

    @Override
    public IPlayerData getPlayerData(UUID player) {
        return new GPPlayerData(GriefPrevention.instance.dataStore.getPlayerData(player));
    }

    @Override
    public void changeClaimOwner(IClaim claim, UUID newOwner) {
        if (claim instanceof GPClaim)
            GriefPrevention.instance.dataStore.changeClaimOwner(((GPClaim) claim).getClaim(), newOwner);
    }

    @Override
    public void registerEvents() {
        new ClaimPermissionListener().registerEvents();
    }

    /**
     * Not supported for GriefPrevention.
     * <p>
     * GriefPrevention claims are identified internally by a {@code long}, and this
     * codebase only looks claims up by location (see {@link #getClaimAt(Location)}) or by
     * an already-resolved {@link IClaim}, so there is no existing facility to resolve a
     * claim from a {@link UUID} alone. GriefPrevention does not ship a map integration
     * that would need this hook, so this always returns {@code null}.
     * </p>
     *
     * @param claimUniqueId unused
     * @return always {@code null}
     */
    @Override
    public Transaction getTransaction(UUID claimUniqueId) {
        return null;
    }
}
