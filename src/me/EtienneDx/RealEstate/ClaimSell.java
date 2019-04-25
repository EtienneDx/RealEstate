package me.EtienneDx.RealEstate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.Claim;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class ClaimSell implements ConfigurationSerializable, Transaction
{
	long claimId;
	private UUID owner = null;
	private double price;
	Sign sign = null;
	
	public ClaimSell(Map<String, Object> map)
	{
		this.claimId = (long) map.get("claimId");
		if(map.get("owner") != null)
			this.owner = UUID.fromString((String) map.get("owner"));
		this.price = (double) map.get("price");
		if(map.get("signLocation") != null)
			this.sign = (Sign)((Location)map.get("signLocation")).getBlock().getState();
	}

	public ClaimSell(Claim claim, Player player, double price, Sign sign)
	{
		this.claimId = claim.getID();
		this.owner = player != null ? player.getUniqueId() : null;
		this.price = price;
		this.sign = sign;
	}
	
	public void updateSign()
	{
		if(sign != null && sign.isPlaced())
		{
			sign.setLine(0, RealEstate.instance.dataStore.cfgSignsHeader);
			sign.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.dataStore.cfgReplaceSell);
			sign.setLine(2, owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "SERVER");
			sign.setLine(3, price + " " + RealEstate.econ.currencyNamePlural());
		}
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

	@Override
	public Block getHolder()
	{
		return (Block) sign;
	}

	@Override
	public UUID getOwner()
	{
		return owner;
	}
}
