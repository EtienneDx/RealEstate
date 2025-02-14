package me.EtienneDx.RealEstate;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import me.EtienneDx.AnnotationConfig.AnnotationConfig;
import me.EtienneDx.AnnotationConfig.ConfigField;
import me.EtienneDx.AnnotationConfig.ConfigFile;

@ConfigFile(header = "RealEstate wiki and newest versions are available at http://www.github.com/msburgess3200/RealEstate")
public class Config extends AnnotationConfig {
    public PluginDescriptionFile pdf;

    public final String configFilePath = RealEstate.pluginDirPath + "config.yml";
    public final String logFilePath = RealEstate.pluginDirPath + "GriefProtection_RealEstate.log";

    @ConfigField(name="RealEstate.Keywords.ChatPrefix", comment="What is displayed before any chat message")
    public String chatPrefix = "$f[$6RealEstate$f] ";

    @ConfigField(name="RealEstate.Keywords.SignsHeader", comment = "What is displayed in top of the signs")
    public String cfgSignsHeader = "$6[RealEstate]";

    @ConfigField(name="RealEstate.Keywords.Sell", comment = "List of all possible signs headers to sell a claim")
    public List<String> cfgSellKeywords = Arrays.asList("[sell]", "[sell claim]", "[sc]", "[re]", "[realestate]");
    @ConfigField(name="RealEstate.Keywords.Rent", comment = "List of all possible signs headers to rent a claim")
    public List<String> cfgRentKeywords = Arrays.asList("[rent]", "[rent claim]", "[rc]");
    @ConfigField(name="RealEstate.Keywords.ContainerRent", comment = "List of all possible signs headers to rent container access only")
    public List<String> cfgContainerRentKeywords = Arrays.asList("[container rent]", "[crent]");
    @ConfigField(name="RealEstate.Keywords.Lease", comment = "List of all possible signs headers to lease a claim")
    public List<String> cfgLeaseKeywords = Arrays.asList("[lease]", "[lease claim]", "[lc]");
    @ConfigField(name="RealEstate.Keywords.Auction", comment = "List of all possible signs headers to auction a claim")
    public List<String> cfgAuctionKeywords = Arrays.asList("[auction]", "[auction claim]", "[ac]");

    @ConfigField(name="RealEstate.Keywords.Replace.Sell", comment = "What is displayed on signs for properties to sell")
    public String cfgReplaceSell = "FOR SALE";
    @ConfigField(name="RealEstate.Keywords.Replace.Rent", comment = "What is displayed on signs for properties to rent")
    public String cfgReplaceRent = "FOR RENT";
    @ConfigField(name="RealEstate.Keywords.Replace.Lease", comment = "What is displayed on signs for properties to lease")
    public String cfgReplaceLease = "FOR LEASE";
    @ConfigField(name="RealEstate.Keywords.Replace.Auction", comment = "What is displayed on signs for properties to auction")
    public String cfgReplaceAuction = "FOR AUCTION";
    @ConfigField(name="RealEstate.Keywords.Replace.Ongoing.Rent", comment = "What is displayed on the first line of the sign once someone rents a claim.")
    public String cfgReplaceOngoingRent = "[Rented]";
    @ConfigField(name="RealEstate.Keywords.Replace.ContainerRent", comment = "What is displayed on the third line of the sign when renting container access only.")
    public String cfgContainerRentLine = ChatColor.BLUE + "Containers only";

    @ConfigField(name="RealEstate.Rules.Sell", comment = "Is selling claims enabled?")
    public boolean cfgEnableSell = true;
    @ConfigField(name="RealEstate.Rules.Rent", comment = "Is renting claims enabled?")
    public boolean cfgEnableRent = true;
    @ConfigField(name="RealEstate.Rules.Lease", comment = "Is leasing claims enabled?")
    public boolean cfgEnableLease = true;
    @ConfigField(name="RealEstate.Rules.Auction", comment = "Is auctioning claims enabled?")
    public boolean cfgEnableAuction = true;

    @ConfigField(name="RealEstate.Rules.CancelAuction", comment = "Can an auctioneer cancel his auction if he already received offers?")
    public boolean cfgCancelAuction = false;
    @ConfigField(name="RealEstate.Rules.DisableOutbidSelf", comment = "Can an auctioneer outbid himself?")
    public boolean cfgDisableOutbidSelf = false;

    @ConfigField(name="RealEstate.Rules.AutomaticRenew", comment = "Can players renting claims enable automatic renew of their contracts?")
    public boolean cfgEnableAutoRenew = true;
    @ConfigField(name="RealEstate.Rules.DestroySigns.Rent", comment = "Should the rent signs get destroyed once the claim is rented?")
    public boolean cfgDestroyRentSigns = false;
    @ConfigField(name="RealEstate.Rules.DestroySigns.Lease", comment = "Should the lease signs get destroyed once the claim is leased?")
    public boolean cfgDestroyLeaseSigns = true;

    @ConfigField(name="RealEstate.Rules.TransferClaimBlocks", comment = "Are the claim blocks transferred to the new owner on purchase or should the buyer provide them?")
    public boolean cfgTransferClaimBlocks = true;

    @ConfigField(name="RealEstate.Rules.UseCurrencySymbol", comment = "Should the signs display the prices with a currency symbol instead of the full currency name?")
    public boolean cfgUseCurrencySymbol = false;
    @ConfigField(name="RealEstate.Rules.CurrencySymbol", comment = "In case UseCurrencySymbol is true, what symbol should be used?")
    public String cfgCurrencySymbol = "$";
    @ConfigField(name="RealEstate.Rules.UseDecimalCurrency", comment = "Allow players to use decimal currency e.g. $10.15")
    public boolean cfgUseDecimalCurrency = true;

    @ConfigField(name="RealEstate.Messaging.MessageOwner", comment = "Should the owner get messaged once one of his claim is rented/leased/bought and on end of contracts?")
    public boolean cfgMessageOwner = true;
    @ConfigField(name="RealEstate.Messaging.MessageBuyer", comment = "Should the buyer get messaged once one of his claim is rented/leased/bought and on end of contracts?")
    public boolean cfgMessageBuyer = true;
    @ConfigField(name="RealEstate.Messaging.BroadcastSell", comment = "Should a message get broadcasted when a player puts a claim for sale/rent/lease?")
    public boolean cfgBroadcastSell = true;
    @ConfigField(name="RealEstate.Messaging.MailOffline", comment = "Should offline owners/buyers receive mails (using the Essentials plugin) when they're offline?")
    public boolean cfgMailOffline = true;

    @ConfigField(name="RealEstate.Default.PricesPerBlock.Sell", comment = "The default price per block when selling a claim")
    public double cfgPriceSellPerBlock = 5.0;
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Rent", comment = "The default price per block when renting a claim")
    public double cfgPriceRentPerBlock = 2.0;
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Lease", comment = "The default price per block when leasing a claim")
    public double cfgPriceLeasePerBlock = 2.0;
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Auction", comment = "The default price per block when auctioning a claim")
    public double cfgPriceAuctionPerBlock = 1.0;
    @ConfigField(name="RealEstate.Default.Prices.AuctionBidStep", comment = "The default bid step when auctioning a claim")
    public double cfgPriceAuctionBidStep = 2.0;

    @ConfigField(name="RealEstate.Default.Duration.Rent", comment = "How long is a rent period by default")
    public String cfgRentTime = "7D";
    @ConfigField(name="RealEstate.Default.Duration.Lease", comment = "How long is a lease period by default")
    public String cfgLeaseTime = "7D";
    @ConfigField(name="RealEstate.Default.Duration.Auction", comment = "How long is an auction period by default")
    public String cfgAuctionTime = "7D";

    @ConfigField(name="RealEstate.Default.LeasePaymentsCount", comment = "How many lease periods are required before the buyer gets the claim's ownership by default")
    public int cfgLeasePayments = 5;
    
    @ConfigField(name="RealEstate.Settings.PageSize", comment = "How many Real Estate offers should be shown per page using the '/re list' command")
    public int cfgPageSize = 8;
    
    @ConfigField(name="RealEstate.Settings.MessagesFiles", comment="Language file to be used. See the languages directory for available files.")
    public String languageFile = "en-us.yml";
    
    @ConfigField(name="RealEstate.Settings.Database.DatabaseType", comment="Database type: MySQL, YML, or SQLite")
    public String databaseType = "YML";
    
    @ConfigField(name="RealEstate.Settings.Database.SQLite.Database", comment="SQLite database name")
    public String sqliteDatabase = "RealEstate.db";
    
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Host", comment="MySQL database host")
    public String mysqlHost = "localhost";
    
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Port", comment="MySQL database port")
    public int mysqlPort = 3306;
    
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Database", comment="MySQL database name")
    public String mysqlDatabase = "RealEstate";
    
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Username", comment="MySQL database username")
    public String mysqlUsername = "";
    
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Password", comment="MySQL database password")
    public String mysqlPassword = "";

    @ConfigField(name="RealEstate.Settings.Database.MySQL.Prefix", comment="MySQL database table prefix")
    public String mysqlPrefix = "realestate_";
    
    @ConfigField(name="RealEstate.Settings.Database.MySQL.UseSSL", comment="MySQL database use SSL")
    public boolean mysqlUseSSL = false;
    
    @ConfigField(name="RealEstate.Settings.Database.MySQL.AutoReconnect", comment="MySQL database auto reconnect")
    public boolean mysqlAutoReconnect = true;
    
    public Config() {
        this.pdf = RealEstate.instance.getDescription();
    }
    
    public String getString(List<String> li) {
        return String.join(";", li);
    }
    
    public List<String> getList(String str) {
        return Arrays.asList(str.split(";"));
    }
    
    List<String> getConfigList(YamlConfiguration config, String path, List<String> defVal) {
        config.addDefault(path, defVal);
        List<String> ret = config.getStringList(path);
        ret.replaceAll(String::toLowerCase);
        return ret;
    }
    
    @Override
    public void loadConfig() {
        this.loadConfig(this.configFilePath);
    }
}
