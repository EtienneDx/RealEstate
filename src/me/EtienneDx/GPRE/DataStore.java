package me.EtienneDx.GPRE;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

public class DataStore
{
	RealEstate plugin;
    public PluginDescriptionFile pdf;

    public final String pluginDirPath = "plugins" + File.separator + "GriefProtection_RealEstate" + File.separator;
    public final String configFilePath = this.pluginDirPath + "config.yml";
    public final String logFilePath = this.pluginDirPath + "GriefProtection_RealEstate.log";
    public final String chatPrefix = "[" + ChatColor.GOLD + "RealEstate" + ChatColor.WHITE + "] ";
    
    public List<String> cfgSigns;

    public List<String> cfgSellKeywords;
    public List<String> cfgLeaseKeywords;

    public String cfgReplaceSell;
    public String cfgReplaceLease;
    
    public boolean cfgEnableLease;
    public boolean cfgEnableRent;
    
    public boolean cfgTransferClaimBlocks;
    
    public boolean cfgMessageOwner;
    public boolean cfgBrodcastSell;
    
    public DataStore(RealEstate plugin)
    {
        this.plugin = plugin;
        this.pdf = this.plugin.getDescription();
    }
    
    public String getString(List<String> li)
    {
    	return String.join(";", li);
    }
    
    public List<String> getList(String str)
    {
    	return Arrays.asList(str.split(";"));
    }
    
    List<String> getConfigList(YamlConfiguration config, String path, List<String> defVal)
    {
    	config.addDefault(path, defVal);
    	return config.getStringList(path);
    }
    
    public void loadConfig(YamlConfiguration config)
    {
    	this.cfgSigns = getConfigList(config, "RealEstate.Keywords.Signs", Arrays.asList("re", "realestate"));

    	this.cfgSigns = getConfigList(config, "RealEstate.Keywords.Signs", Arrays.asList("re", "realestate"));
    }
}
