package me.EtienneDx.RealEstate;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.EtienneDx.RealEstate.Transactions.BoughtTransaction;
import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.IAddonPlugin;

public class GP_RealEstateHook implements IAddonPlugin
{
	@Override
	public String allowEdit(Claim claim, Player player)
	{
		Transaction b = RealEstate.transactionsStore.getTransaction(claim);
		if(b != null && player.getUniqueId().equals(b.getOwner()) && b instanceof BoughtTransaction)
		{
			if(((BoughtTransaction)b).getBuyer() != null)
				return "This claim is currently involved in a transaction, you can't edit it!";
		}
		return null;
	}

	@Override
	public String allowBuild(Claim claim, Player player, Material material)
	{
		Transaction b = RealEstate.transactionsStore.getTransaction(claim);
		if(b != null && player.getUniqueId().equals(b.getOwner()) && b instanceof BoughtTransaction)// ??
		{
			if(((BoughtTransaction)b).getBuyer() != null)
				return "This claim is currently involved in a transaction, you can't build on it!";
		}
		return null;
	}

	@Override
	public String allowAccess(Claim claim, Player player)
	{
		Transaction b = RealEstate.transactionsStore.getTransaction(claim);
		if(b != null && player.getUniqueId().equals(b.getOwner()) && b instanceof BoughtTransaction)
		{
			if(((BoughtTransaction)b).getBuyer() != null)
				return "This claim is currently involved in a transaction, you can't access it!";
		}
		return null;
	}

	@Override
	public String allowContainers(Claim claim, Player player)
	{
		Transaction b = RealEstate.transactionsStore.getTransaction(claim);
		if(b != null && player.getUniqueId().equals(b.getOwner()) && b instanceof BoughtTransaction)
		{
			if(((BoughtTransaction)b).getBuyer() != null)
				return "This claim is currently involved in a transaction, you can't access it's containers!";
		}
		return null;
	}

	@Override
	public String allowGrantPermission(Claim claim, Player player)
	{
		Transaction b = RealEstate.transactionsStore.getTransaction(claim);
		if(b != null && b instanceof BoughtTransaction)
		{
			if(((BoughtTransaction)b).getBuyer() != null && !((BoughtTransaction)b).getBuyer().equals(player.getUniqueId()))
				return "This claim is currently involved in a transaction, you can't change any permission!";
		}
		return null;
	}

	@Override
	public String mayResizeClaim(Claim claim, Player player, int newx1, int newx2, int newy1, int newy2, int newz1,
			int newz2)
	{
		if(RealEstate.transactionsStore.anyTransaction(claim))
		{
			return "This claim is currently involved in a transaction, you can't resize it!";
		}
		return null;
	}

	@Override
	public String mayAbandonClaim(Claim claim, Player player)
	{
		if(RealEstate.transactionsStore.anyTransaction(claim))
		{
			return "This claim is currently involved in a transaction, you can't abandon it!";
		}
		return null;
	}
}
