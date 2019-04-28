package me.EtienneDx.RealEstate.Transactions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ExitOffer implements ConfigurationSerializable
{
	public UUID offerBy;
	public double price;
	
	public ExitOffer(UUID offerBy, double price)
	{
		this.offerBy = offerBy;
		this.price = price;
	}
	
	public ExitOffer(Map<String, Object> map)
	{
		offerBy = UUID.fromString((String)map.get("offerBy"));
		price = (double)map.get("price");
	}
	
	@Override
	public Map<String, Object> serialize()
	{
		HashMap<String, Object> map = new HashMap<>();
		map.put("offerBy", offerBy.toString());
		map.put("price", price);
		return map;
	}
}
