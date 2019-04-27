package me.EtienneDx.RealEstate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.Claim;

public abstract class ClaimTransaction implements ConfigurationSerializable, Transaction
{
	protected long claimId;
	protected UUID owner = null;
	protected double price;
	protected Location sign = null;
	
	public ClaimTransaction(Claim claim, Player player, double price, Location sign)
	{
		this.claimId = claim.getID().longValue();
		this.owner = player != null ? player.getUniqueId() : null;
		this.price = price;
		this.sign = sign;
	}
	
	public ClaimTransaction(Map<String, Object> map)
	{
		this.claimId = Long.valueOf(String.valueOf(map.get("claimId")));
		if(map.get("owner") != null)
			this.owner = UUID.fromString((String) map.get("owner"));
		this.price = (double) map.get("price");
		if(map.get("signLocation") != null)
			this.sign = (Location) map.get("signLocation");
	}
	
	public ClaimTransaction()
	{
		
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<>();
		
		map.put("claimId", this.claimId);
		if(owner != null)
			map.put("owner", owner.toString());
		map.put("price", this.price);
		if(sign != null)
			map.put("signLocation", sign);
		
		return map;
	}

	@Override
	public Block getHolder()
	{
		return sign.getBlock().getState() instanceof Sign ? sign.getBlock() : null;
	}

	@Override
	public UUID getOwner()
	{
		return owner;
	}
}
