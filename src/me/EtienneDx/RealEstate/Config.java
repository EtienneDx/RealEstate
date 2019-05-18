package me.EtienneDx.RealEstate;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import me.EtienneDx.AnnotationConfig.AnnotationConfig;
import me.EtienneDx.AnnotationConfig.ConfigField;
import me.EtienneDx.AnnotationConfig.ConfigFile;

@ConfigFile(header = "RealEstate wiki and newest versions are available at http://www.github.com/EtienneDx/RealEstate")
public class Config extends AnnotationConfig
{
    public PluginDescriptionFile pdf;

    public final String configFilePath = RealEstate.pluginDirPath + "config.yml";
    public final String logFilePath = RealEstate.pluginDirPath + "GriefProtection_RealEstate.log";
    public final String chatPrefix = "[" + ChatColor.GOLD + "RealEstate" + ChatColor.WHITE + "] ";
    
    @ConfigField(name="RealEstate.Keywords.SignsHeader", comment = "What is displayed in top of the signs")
    public String cfgSignsHeader = ChatColor.GOLD + "[RealEstate]";
    //public List<String> cfgSigns;

    @ConfigField(name="RealEstate.Keywords.Sell", comment = "List of all possible possible signs headers to sell a claim")
    public List<String> cfgSellKeywords = Arrays.asList("[sell]", "[sell claim]", "[sc]", "[re]", "[realestate]");
    @ConfigField(name="RealEstate.Keywords.Rent", comment = "List of all possible possible signs headers to rent a claim")
    public List<String> cfgRentKeywords = Arrays.asList("[rent]", "[rent claim]", "[rc]");
    @ConfigField(name="RealEstate.Keywords.Lease", comment = "List of all possible possible signs headers to lease a claim")
    public List<String> cfgLeaseKeywords = Arrays.asList("[lease]", "[lease claim]", "[lc]");

    @ConfigField(name="RealEstate.Keywords.Replace.Sell", comment = "What is displayed on signs for preoperties to sell")
    public String cfgReplaceSell = "FOR SALE";
    @ConfigField(name="RealEstate.Keywords.Replace.Rent", comment = "What is displayed on signs for preoperties to rent")
    public String cfgReplaceRent = "FOR RENT";
    @ConfigField(name="RealEstate.Keywords.Replace.Lease", comment = "What is displayed on signs for preoperties to lease")
    public String cfgReplaceLease = "FOR LEASE";

    @ConfigField(name="RealEstate.Rules.Sell", comment = "Is selling claims enabled?")
    public boolean cfgEnableSell = true;
    @ConfigField(name="RealEstate.Rules.Rent", comment = "Is renting claims enabled?")
    public boolean cfgEnableRent = true;
    @ConfigField(name="RealEstate.Rules.Lease", comment = "Is leasing claims enabled?")
    public boolean cfgEnableLease = true;

    @ConfigField(name="RealEstate.Rules.AutomaticRenew", comment = "Can players renting claims enable automatic renew of their contracts?")
    public boolean cfgEnableAutoRenew = true;
    @ConfigField(name="RealEstate.Rules.RentPeriods", comment = "Can a rent contract last multiple periods?")
    public boolean cfgEnableRentPeriod = true;
    @ConfigField(name="RealEstate.Rules.DestroySigns.Rent", comment = "Should the rent signs get destroyed once the claim is rented?")
    public boolean cfgDestroyRentSigns = false;
    @ConfigField(name="RealEstate.Rules.DestroySigns.Lease", comment = "Should the lease signs get destroyed once the claim is leased?")
    public boolean cfgDestroyLeaseSigns = true;

    @ConfigField(name="RealEstate.Rules.TransferClaimBlocks", comment = "Are the claim blocks transfered to the new owner on purchase or should the buyer provide them?")
    public boolean cfgTransferClaimBlocks = true;

    @ConfigField(name="RealEstate.Rules.UseCurrencySymbol", comment = "Should the signs display the prices with a currency symbol instead of the full currency name?")
    public boolean cfgUseCurrencySymbol = false;
    @ConfigField(name="RealEstate.Rules.CurrencySymbol", comment = "In case UseCurrencySymbol is true, what symbol should be used?")
    public String cfgCurrencySymbol = "$";

    @ConfigField(name="RealEstate.Messaging.MessageOwner", comment = "Should the owner get messaged once one of his claim is rented/leased/bought and on end of contracts?")
    public boolean cfgMessageOwner = true;
    @ConfigField(name="RealEstate.Messaging.MessageBuyer", comment = "Should the buyer get messaged once one of his claim is rented/leased/bought and on end of contracts?")
    public boolean cfgMessageBuyer = true;
    @ConfigField(name="RealEstate.Messaging.BroadcastSell", comment = "Should a message get broadcasted when a player put a claim for rent/lease/sell?")
    public boolean cfgBroadcastSell = true;
    @ConfigField(name="RealEstate.Messaging.MailOffline", comment = "Should offline owner/buyers receive mails (using the Essentials plugin) when they're offline?")
    public boolean cfgMailOffline = true;

    @ConfigField(name="RealEstate.Default.PricesPerBlock.Sell", comment = "Chat is the default price per block when selling a claim")
    public double cfgPriceSellPerBlock = 5.0;
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Rent", comment = "Chat is the default price per block when renting a claim")
    public double cfgPriceRentPerBlock = 2.0;
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Lease", comment = "Chat is the default price per block when leasing a claim")
    public double cfgPriceLeasePerBlock = 2.0;

    @ConfigField(name="RealEstate.Default.Duration.Rent", comment = "How long is a rent period by default")
    public String cfgRentTime = "7D";
    @ConfigField(name="RealEstate.Default.Duration.Lease", comment = "How long is a lease period by default")
    public String cfgLeaseTime = "7D";

    @ConfigField(name="RealEstate.Default.LeasePaymentsCount", comment = "How many lease periods are required before the buyer gets the claim's ownership by default")
    public int cfgLeasePayments = 5;
    
    public Config()
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
    
    /*public void loadConfig(YamlConfiguration config)
    {
    	this.cfgSignsHeader = config.getString("RealEstate.Keywords.SignsHeader", ChatColor.GOLD + "[RealEstate]");
    	//this.cfgSigns = getConfigList(config, "RealEstate.Keywords.Signs", Arrays.asList("[re]", "[realestate]"));

    	this.cfgSellKeywords = getConfigList(config, "RealEstate.Keywords.Sell", Arrays.asList("[sell]", "[sell claim]", "[sc]", "[re]", "[realestate]"));
    	this.cfgRentKeywords = getConfigList(config, "RealEstate.Keywords.Rent", Arrays.asList("[rent]", "[rent claim]", "[rc]"));
    	this.cfgLeaseKeywords = getConfigList(config, "RealEstate.Keywords.Lease", Arrays.asList("[lease]", "[lease claim]", "[lc]"));

    	this.cfgReplaceSell = config.getString("RealEstate.Keywords.Replace.Sell", "FOR SALE");
    	this.cfgReplaceRent = config.getString("RealEstate.Keywords.Replace.Rent", "FOR RENT");
    	this.cfgReplaceLease = config.getString("RealEstate.Keywords.Replace.Lease", "FOR LEASE");

    	this.cfgEnableSell = config.getBoolean("RealEstate.Rules.Sell", true);
    	this.cfgEnableRent = config.getBoolean("RealEstate.Rules.Rent", true);
    	this.cfgEnableLease = config.getBoolean("RealEstate.Rules.Lease", true);

    	this.cfgEnableAutoRenew = config.getBoolean("RealEstate.Rules.AutomaticRenew", true);
    	this.cfgEnableRentPeriod = config.getBoolean("RealEstate.Rules.RentPeriods", true);
    	this.cfgDestroyRentSigns = config.getBoolean("RealEstate.Rules.DestroySigns.Rent", false);
    	this.cfgDestroyLeaseSigns = config.getBoolean("RealEstate.Rules.DestroySigns.Lease", false);

    	this.cfgTransferClaimBlocks = config.getBoolean("RealEstate.Rules.TransferClaimBlocks", true);

        this.cfgUseCurrencySymbol = config.getBoolean("RealEstate.Rules.UseCurrencySymbol", false);
        this.cfgCurrencySymbol = config.getString("RealEstate.Rules.CurrencySymbol", "$");

        this.cfgMessageOwner = config.getBoolean("RealEstate.Messaging.MessageOwner", true);
        this.cfgMessageBuyer = config.getBoolean("RealEstate.Messaging.MessageBuyer", true);
        this.cfgMailOffline = config.getBoolean("RealEstate.Messaging.MailOffline", true);
        this.cfgBroadcastSell = config.getBoolean("RealEstate.Messaging.BroadcastSell", true);

        this.cfgPriceSellPerBlock = config.getDouble("RealEstate.Default.PricesPerBlock.Sell", 5.0);
        this.cfgPriceRentPerBlock = config.getDouble("RealEstate.Default.PricesPerBlock.Rent", 2.0);
        this.cfgPriceLeasePerBlock = config.getDouble("RealEstate.Default.PricesPerBlock.Lease", 2.0);
        
        this.cfgRentTime = config.getString("RealEstate.Default.Duration.Rent", "7D");
        this.cfgLeaseTime = config.getString("RealEstate.Default.Duration.Lease", "7D");
        
        this.cfgLeasePayments = config.getInt("RealEstate.Default.PaymentsCount.Lease", 5);
    }*/
    
    @Override
    public void loadConfig()
    {
        //YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.configFilePath));
        this.loadConfig(this.configFilePath);
    }
    
    /*public void saveConfig()
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
    	outConfig.set("RealEstate.Rules.RentPeriods", this.cfgEnableRentPeriod);
    	outConfig.set("RealEstate.Rules.DestroySigns.Rent", this.cfgDestroyRentSigns);
    	outConfig.set("RealEstate.Rules.DestroySigns.Lease", this.cfgDestroyLeaseSigns);

    	outConfig.set("RealEstate.Rules.TransferClaimBlocks", this.cfgTransferClaimBlocks);

    	outConfig.set("RealEstate.Rules.UseCurrencySymbol", this.cfgUseCurrencySymbol);
    	outConfig.set("RealEstate.Rules.CurrencySymbol", this.cfgCurrencySymbol);

    	outConfig.set("RealEstate.Messaging.MessageOwner", this.cfgMessageOwner);
    	outConfig.set("RealEstate.Messaging.MessageBuyer", this.cfgMessageBuyer);
    	outConfig.set("RealEstate.Messaging.MailOffline", this.cfgMailOffline);
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
    }*/
}
