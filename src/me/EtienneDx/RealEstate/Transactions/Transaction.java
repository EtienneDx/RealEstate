package me.EtienneDx.RealEstate.Transactions;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface Transaction
{
	public Block getHolder();
	public UUID getOwner();
	public void setOwner(UUID newOwner);
	public void interact(Player player);
	public void preview(Player player);
	public void update();
	public boolean tryCancelTransaction(Player p);
}
