package me.EtienneDx.RealEstate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class ClaimSell implements ConfigurationSerializable
{
	private long claimId;
	private UUID owner = null;
	private double price;
	private Sign sign = null;
	
	public ClaimSell(Map<String, Object> map)
	{
		this.claimId = (long) map.get("claimId");
		if(map.get("owner") != null)
			this.owner = UUID.fromString((String) map.get("owner"));
		this.price = (double) map.get("price");
		if(map.get("signLocation") != null)
			this.sign = (Sign)((Location)map.get("signLocation")).getBlock().getState();
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
			map.put("signLocation", sign.getLocation());
		
		return map;
	}
}
