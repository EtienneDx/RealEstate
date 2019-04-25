package me.EtienneDx.RealEstate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.Claim;

public class TransactionsStore
{
    public final String dataFilePath = RealEstate.pluginDirPath + "transactions.data";
    
    public HashMap<String, ClaimSell> claimSell;
    
    public void loadData()
    {
    	claimSell = new HashMap<>();
    	
    	FileConfiguration config = YamlConfiguration.loadConfiguration(new File(this.dataFilePath));
    	for(String key : config.getKeys(false))
    	{
    		if(key.startsWith("Sell."))
    		{
    			ClaimSell cs = (ClaimSell)config.get(key);
    			claimSell.put(key.substring(5), cs);
    		}
    	}
    }
    
    public void saveData()
    {
    	YamlConfiguration config = new YamlConfiguration();
        for (ClaimSell cs : claimSell.values())
            config.set("Sell." + cs.claimId, cs);
        try
        {
			config.save(new File(this.dataFilePath));
		}
        catch (IOException e)
        {
			RealEstate.instance.log.info("Unable to write to the data file at \"" + this.dataFilePath + "\"");
		}
    }

	public void sell(Claim claim, Player player, double price, Sign sign)
	{
		ClaimSell cs = new ClaimSell(claim, player, price, sign);
		claimSell.put(claim.getID().toString(), cs);
		cs.updateSign();
		saveData();
	}
	
	public boolean anyTransaction(Claim claim)
	{
		return claimSell.containsKey(claim.getID().toString());
	}
}
