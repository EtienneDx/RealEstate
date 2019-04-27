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
    //public List<String> cfgSigns;

    public List<String> cfgSellKeywords;
    public List<String> cfgRentKeywords;
    public List<String> cfgLeaseKeywords;

    public String cfgReplaceSell;
    public String cfgReplaceRent;
    public String cfgReplaceLease;
    
    public boolean cfgEnableSell;
    public boolean cfgEnableRent;
    public boolean cfgEnableLease;

    public boolean cfgEnableAutoRenew;
    
    public boolean cfgTransferClaimBlocks;

    public boolean cfgMessageOwner;
    public boolean cfgMessageBuyer;
    public boolean cfgBroadcastSell;

    public double cfgPriceSellPerBlock;
    public double cfgPriceRentPerBlock;
    public double cfgPriceLeasePerBlock;

    public String cfgRentTime;
    public String cfgLeaseTime;
    
    public int cfgLeasePayments;
    
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
    	this.cfgSignsHeader = config.getString("RealEstate.Keywords.SignsHeader", ChatColor.GOLD + "[RealEstate]");
    	//this.cfgSigns = getConfigList(config, "RealEstate.Keywords.Signs", Arrays.asList("[re]", "[realestate]"));

    	this.cfgSellKeywords = getConfigList(config, "RealEstate.Keywords.Sell", Arrays.asList("[sell]", "[sellclaim]", "[sc]", "[re]", "[realestate]"));
    	this.cfgRentKeywords = getConfigList(config, "RealEstate.Keywords.Rent", Arrays.asList("[rent]", "[rent claim]", "[rc]"));
    	this.cfgLeaseKeywords = getConfigList(config, "RealEstate.Keywords.Lease", Arrays.asList("[lease]", "[lease claim]", "[lc]"));

    	this.cfgReplaceSell = config.getString("RealEstate.Keywords.Replace.Sell", "FOR SALE");
    	this.cfgReplaceRent = config.getString("RealEstate.Keywords.Replace.Rent", "FOR RENT");
    	this.cfgReplaceLease = config.getString("RealEstate.Keywords.Replace.Lease", "FOR LEASE");

    	this.cfgEnableSell = config.getBoolean("RealEstate.Rules.Sell", true);
    	this.cfgEnableRent = config.getBoolean("RealEstate.Rules.Rent", true);
    	this.cfgEnableLease = config.getBoolean("RealEstate.Rules.Lease", true);

    	this.cfgEnableAutoRenew = config.getBoolean("RealEstate.Rules.AutomaticRenew", true);

    	this.cfgTransferClaimBlocks = config.getBoolean("RealEstate.Rules.TransferClaimBlocks", true);

        this.cfgMessageOwner = config.getBoolean("RealEstate.Messaging.MessageOwner", true);
        this.cfgMessageBuyer = config.getBoolean("RealEstate.Messaging.MessageBuyer", true);
        this.cfgBroadcastSell = config.getBoolean("RealEstate.Messaging.BroadcastSell", true);

        this.cfgPriceSellPerBlock = config.getDouble("RealEstate.Default.PricesPerBlock.Sell", 5.0);
        this.cfgPriceRentPerBlock = config.getDouble("RealEstate.Default.PricesPerBlock.Rent", 2.0);
        this.cfgPriceLeasePerBlock = config.getDouble("RealEstate.Default.PricesPerBlock.Lease", 2.0);
        
        this.cfgRentTime = config.getString("RealEstate.Default.Duration.Rent", "7D");
        this.cfgLeaseTime = config.getString("RealEstate.Default.Duration.Lease", "7D");
        
        this.cfgLeasePayments = config.getInt("RealEstate.Default.PaymentsCount.Lease", 5);
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
    	//outConfig.set("RealEstate.Keywords.Signs", this.cfgSigns);
    	
    	outConfig.set("RealEstate.Keywords.Sell", this.cfgSellKeywords);
    	outConfig.set("RealEstate.Keywords.Rent", this.cfgRentKeywords);
    	outConfig.set("RealEstate.Keywords.Lease", this.cfgLeaseKeywords);

    	outConfig.set("RealEstate.Keywords.Replace.Sell", this.cfgReplaceSell);
    	outConfig.set("RealEstate.Keywords.Replace.Rent", this.cfgReplaceRent);
    	outConfig.set("RealEstate.Keywords.Replace.Lease", this.cfgReplaceLease);

    	outConfig.set("RealEstate.Rules.Sell", this.cfgEnableSell);
    	outConfig.set("RealEstate.Rules.Rent", this.cfgEnableRent);
    	outConfig.set("RealEstate.Rules.Lease", this.cfgEnableLease);

    	outConfig.set("RealEstate.Rules.AutomaticRenew", this.cfgEnableAutoRenew);

    	outConfig.set("RealEstate.Rules.TransferClaimBlocks", this.cfgTransferClaimBlocks);

    	outConfig.set("RealEstate.Messaging.MessageOwner", this.cfgMessageOwner);
    	outConfig.set("RealEstate.Messaging.MessageBuyer", this.cfgMessageBuyer);
    	outConfig.set("RealEstate.Messaging.BroadcastSell", this.cfgBroadcastSell);

    	outConfig.set("RealEstate.Default.PricePerBlock.Sell", this.cfgPriceSellPerBlock);
    	outConfig.set("RealEstate.Default.PricePerBlock.Rent", this.cfgPriceRentPerBlock);
    	outConfig.set("RealEstate.Default.PricePerBlock.Lease", this.cfgPriceLeasePerBlock);

    	outConfig.set("RealEstate.Default.Duration.Rent", this.cfgRentTime);
    	outConfig.set("RealEstate.Default.Duration.Lease", this.cfgLeaseTime);
    	
    	outConfig.set("RealEstate.Default.PaymentsCount.Lease", this.cfgLeasePayments);
    	
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
