package me.EtienneDx.RealEstate;

import java.util.UUID;

import org.bukkit.block.Block;

public interface Transaction
{
	public Block getHolder();
	public UUID getOwner();
}
