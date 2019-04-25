package me.EtienneDx.RealEstate;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TransactionsStore
{
    public final String dataFilePath = RealEstate.pluginDirPath + "transactions.data";
    
    public ArrayList<ClaimSell> claimSell;
    
    public TransactionsStore()
    {
    	claimSell = new ArrayList<>();
    	
    	FileConfiguration config = YamlConfiguration.loadConfiguration(new File(this.dataFilePath));
    	for(String key : config.getKeys(false))
    	{
    		if(key.startsWith("Sell."))
    		{
    			ClaimSell cs = (ClaimSell)config.get(key);
    			claimSell.add(cs);
    		}
    	}
    }
    
    
}
