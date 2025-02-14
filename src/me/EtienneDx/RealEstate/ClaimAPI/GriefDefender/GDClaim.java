package me.EtienneDx.RealEstate.ClaimAPI.GriefDefender;

import java.util.Iterator;
import java.util.UUID;

import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustType;
import com.griefdefender.api.claim.TrustTypes;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;

public class GDClaim implements IClaim{

    private Claim claim;

    public GDClaim(Claim claim) {
        this.claim = claim;
    }

    public Claim getClaim() {
        return claim;
    }

    @Override
    public String getId() {
        return claim.getUniqueId().toString();
    }

    @Override
    public int getArea() {
        return claim.getArea();
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(claim.getWorldUniqueId());
    }

    @Override
    public int getX() {
        return claim.getLesserBoundaryCorner().getX();
    }

    @Override
    public int getY() {
        return claim.getLesserBoundaryCorner().getY();
    }

    @Override
    public int getZ() {
        return claim.getLesserBoundaryCorner().getZ();
    }

    @Override
    public boolean isAdminClaim() {
        return claim.isAdminClaim();
    }

    @Override
    public Iterable<IClaim> getChildren() {
        return new Iterable<IClaim>() {
            @Override
            public Iterator<IClaim> iterator() {
                return new Iterator<IClaim>() {
                    private Iterator<Claim> it = claim.getChildren(true).iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public IClaim next() {
                        return new GDClaim(it.next());
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }
        };
    }

    @Override
    public boolean isWilderness() {
        return claim.isWilderness();
    }

    @Override
    public boolean isSubClaim() {
        return claim.getParent() != null && !claim.getParent().isWilderness();
    }

    @Override
    public boolean isParentClaim() {
        return claim.getParent() == null || claim.getParent().isWilderness();
    }

    @Override
    public IClaim getParent() {
        return isParentClaim() ? null : new GDClaim(claim.getParent());
    }

    @Override
    public void dropPlayerPermissions(UUID player) {
        claim.removeUserTrust(player, TrustTypes.NONE);
    }

    @Override
    public void addPlayerPermissions(UUID player, ClaimPermission permission) {
        TrustType trust = TrustTypes.NONE;
        switch (permission) {
            case ACCESS:
                trust = TrustTypes.ACCESSOR;
                break;
            case BUILD:
                trust = TrustTypes.BUILDER;
                break;
            case CONTAINER:
                trust = TrustTypes.CONTAINER;
                break;
            case EDIT:
                trust = TrustTypes.MANAGER;
                break;
            case MANAGE:
                trust = TrustTypes.MANAGER;
                break;
        }

        claim.addUserTrust(player, trust);
    }

    @Override
    public void clearPlayerPermissions() {
        claim.removeAllUserTrusts();
    }

    @Override
    public void removeManager(UUID player) {
        // No equivalent in GD
    }

    @Override
    public void addManager(UUID player) {
        // No equivalent in GD
    }

    @Override
    public void clearManagers() {
        // No equivalent in GD
    }

    @Override
    public UUID getOwner() {
        return claim.getOwnerUniqueId();
    }

    @Override
    public String getOwnerName() {
        return claim.getOwnerName();
    }

    @Override
    public void setInheritPermissions(boolean inherit) {
        claim.getData().setInheritParent(inherit);
    }
    
}
