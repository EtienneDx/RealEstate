package me.EtienneDx.RealEstate.ClaimAPI.GriefPrevention;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.ryanhamshire.GriefPrevention.Claim;

public class GPClaim implements IClaim
{
    private Claim claim;

    public GPClaim(Claim claim)
    {
        this.claim = claim;
    }

    public Claim getClaim()
    {
        return claim;
    }

    @Override
    public String getId()
    {
        return claim.getID().toString();
    }

    @Override
    public int getArea() {
        return claim.getArea();
    }

    @Override
    public World getWorld() {
        return claim.getLesserBoundaryCorner().getWorld();
    }

    @Override
    public int getX() {
        return claim.getLesserBoundaryCorner().getBlockX();
    }

    @Override
    public int getY() {
        return claim.getLesserBoundaryCorner().getBlockY();
    }

    @Override
    public int getZ() {
        return claim.getLesserBoundaryCorner().getBlockZ();
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
                    private Iterator<Claim> iterator = claim.children.iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public IClaim next() {
                        return new GPClaim(iterator.next());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    @Override
    public boolean isWilderness() {
        return false;
    }

    @Override
    public boolean isSubClaim() {
        return claim.parent != null;
    }

    @Override
    public boolean isParentClaim() {
        return claim.parent == null;
    }

    @Override
    public IClaim getParent() {
        return isSubClaim() ? new GPClaim(claim.parent) : null;
    }

    @Override
    public void dropPlayerPermissions(UUID player) {
        claim.dropPermission(player.toString());
    }

    @Override
    public void addPlayerPermissions(UUID player, ClaimPermission permission) {
        me.ryanhamshire.GriefPrevention.ClaimPermission gpPermission = null;
        switch (permission) {
            case BUILD:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Build;
                break;
            case CONTAINER:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Inventory;
                break;
            case MANAGE:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Manage;
                break;
            case ACCESS:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Access;
                break;
            case EDIT:
                gpPermission = me.ryanhamshire.GriefPrevention.ClaimPermission.Edit;
                break;
        }
        claim.setPermission(player.toString(), gpPermission);
    }

    @Override
    public void removeManager(UUID player) {
        claim.managers.remove(player.toString());
    }

    @Override
    public UUID getOwner() {
        return claim.ownerID;
    }

    @Override
    public String getOwnerName() {
        return getOwner() != null ? Bukkit.getPlayer(getOwner()).getName() : RealEstate.instance.messages.keywordTheServer;
    }

    @Override
    public void setInheritPermissions(boolean inherit) {
        claim.setSubclaimRestrictions(!inherit);
    }

    @Override
    public void clearPlayerPermissions() {
        claim.clearPermissions();
    }

    @Override
    public void addManager(UUID player) {
        claim.managers.add(player.toString());
    }

    @Override
    public void clearManagers() {
        claim.managers.clear();
    }
    
}
