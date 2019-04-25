package me.EtienneDx.RealEstate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

public class DataStore
{
    public PluginDescriptionFile pdf;

    public final String configFilePath = RealEstate.pluginDirPath + "config.yml";
    public final String logFilePath = RealEstate.pluginDirPath + "GriefProtection_RealEstate.log";
    public final String chatPrefix = "[" + ChatColor.GOLD + "RealEstate" + ChatColor.WHITE + "] ";
    
    public String cfgSignsHeader;
    public List<String> cfgSigns;

    public List<String> cfgSellKeywords;
    public List<String> cfgLeaseKeywords;

    public String cfgReplaceSell;
    public String cfgReplaceLease;
    
    public boolean cfgEnableSell;
    public boolean cfgEnableLease;
    
    public boolean cfgTransferClaimBlocks;
    
    public boolean cfgMessageOwner;
    public boolean cfgBroadcastSell;

    public double cfgPriceSellPerBlock;
    public double cfgPriceLeasePerBlock;
    
    public DataStore()
    {
        this.pdf = RealEstate.instance.getDescription();
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
    	List<String> ret = config.getStringList(path);
    	ret.replaceAll(String::toLowerCase);
    	return ret;
    }
    
    public void loadConfig(YamlConfiguration config)
    {
    	this.cfgSignsHeader = config.getString("RealEstate.Keywords.SignsHeader", "[RealEstate]");
    	this.cfgSigns = getConfigList(config, "RealEstate.Keywords.Signs", Arrays.asList("[re]", "[realestate]"));

    	this.cfgSellKeywords = getConfigList(config, "RealEstate.Keywords.Sell", Arrays.asList("sell", "selling", "for sale"));
    	this.cfgLeaseKeywords = getConfigList(config, "RealEstate.Keywords.Lease", Arrays.asList("rent", "renting", "for rent", "lease", "for lease"));

    	this.cfgReplaceSell = config.getString("RealEstate.Keywords.Replace.Sell", "FOR SALE");
    	this.cfgReplaceLease = config.getString("RealEstate.Keywords.Replace.Lease", "FOR LEASE");

    	this.cfgEnableSell = config.getBoolean("RealEstate.Rules.Sell", true);
    	this.cfgEnableLease = config.getBoolean("RealEstate.Rules.Lease", true);

    	this.cfgTransferClaimBlocks = config.getBoolean("RealEstate.Rules.TransferClaimBlocks", true);

        this.cfgMessageOwner = config.getBoolean("RealEstate.Messaging.MessageOwner", true);
        this.cfgBroadcastSell = config.getBoolean("RealEstate.Messaging.BroadcastSell", true);

        this.cfgPriceSellPerBlock = config.getDouble("RealEstate.DefaultPricesPerBlock.Sell", 5.0);
        this.cfgPriceLeasePerBlock = config.getDouble("RealEstate.DefaultPricesPerBlock.Lease", 2.0);
    }
    
    public void loadConfig()
    {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.configFilePath));
        this.loadConfig(config);
    }
    
    public void saveConfig()
    {
    	FileConfiguration outConfig = new YamlConfiguration();
    	outConfig.set("RealEstate.Keywords.SignsHeader", this.cfgSignsHeader);
    	outConfig.set("RealEstate.Keywords.Signs", this.cfgSigns);
    	
    	outConfig.set("RealEstate.Keywords.Sell", this.cfgSellKeywords);
    	outConfig.set("RealEstate.Keywords.Lease", this.cfgLeaseKeywords);

    	outConfig.set("RealEstate.Keywords.Replace.Sell", this.cfgReplaceSell);
    	outConfig.set("RealEstate.Keywords.Replace.Lease", this.cfgReplaceLease);

    	outConfig.set("RealEstate.Rules.Sell", this.cfgEnableSell);
    	outConfig.set("RealEstate.Rules.Lease", this.cfgEnableLease);

    	outConfig.set("RealEstate.Rules.TransferClaimBlocks", this.cfgTransferClaimBlocks);

    	outConfig.set("RealEstate.Messaging.MessageOwner", this.cfgMessageOwner);
    	outConfig.set("RealEstate.Messaging.BroadcastSell", this.cfgBroadcastSell);

    	outConfig.set("RealEstate.DefaultPricePerBlock.Sell", this.cfgPriceSellPerBlock);
    	outConfig.set("RealEstate.DefaultPricePerBlock.Lease", this.cfgPriceLeasePerBlock);
    	
    	try
        {
            outConfig.save(this.configFilePath);
        }
        catch (IOException exception)
        {
            RealEstate.instance.log.info("Unable to write to the configuration file at \"" + this.configFilePath + "\"");
        }
    }
}
