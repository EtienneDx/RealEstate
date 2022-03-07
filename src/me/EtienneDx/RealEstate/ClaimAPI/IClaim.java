package me.EtienneDx.RealEstate.ClaimAPI;

import java.util.UUID;

import org.bukkit.World;

public interface IClaim
{
    public String getId();

    public int getArea();

    public World getWorld();

    public int getX();

    public int getY();

    public int getZ();

    public boolean isAdminClaim();

    public Iterable<IClaim> getChildren();

    public boolean isWilderness();

    public boolean isSubClaim();

    public boolean isParentClaim();

    public IClaim getParent();

    public void dropPlayerPermissions(UUID player);

    public void addPlayerPermissions(UUID player, ClaimPermission permission);

    public void clearPlayerPermissions();

    public void removeManager(UUID player);

    public void addManager(UUID player);

    public void clearManagers();

    public UUID getOwner();

    public String getOwnerName();

    public void setInheritPermissions(boolean inherit);
}
