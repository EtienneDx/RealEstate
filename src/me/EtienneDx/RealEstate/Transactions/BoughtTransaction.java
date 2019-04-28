package me.EtienneDx.RealEstate.Transactions;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.Claim;

public abstract class BoughtTransaction extends ClaimTransaction
{
	public UUID buyer = null;
	public ExitOffer exitOffer = null;
	
	public BoughtTransaction(Map<String, Object> map)
	{
		super(map);
		if(map.get("buyer") != null)
			buyer = UUID.fromString((String)map.get("buyer"));
		if(map.get("exitOffer") != null)
			exitOffer = (ExitOffer) map.get("exitOffer");
	}
	
	public BoughtTransaction(Claim claim, Player player, double price, Location sign)
	{
		super(claim, player, price, sign);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = super.serialize();
		if(buyer != null)
			map.put("buyer", buyer.toString());
		if(exitOffer != null)
			map.put("exitOffer", exitOffer);
		
		return map;
	}
	
	public UUID getBuyer()
	{
		return buyer;
	}
}
