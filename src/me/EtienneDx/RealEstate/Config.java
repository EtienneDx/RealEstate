package me.EtienneDx.RealEstate;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import me.EtienneDx.AnnotationConfig.AnnotationConfig;
import me.EtienneDx.AnnotationConfig.ConfigField;
import me.EtienneDx.AnnotationConfig.ConfigFile;

/**
 * Represents the configuration settings for the RealEstate plugin.
 * <p>
 * This class uses the AnnotationConfig system to load and save configurable options
 * such as keywords for signs, rules for transactions, default prices, durations,
 * messaging settings, and database settings.
 * </p>
 */
@ConfigFile(header = "RealEstate wiki and newest versions are available at http://www.github.com/msburgess3200/RealEstate")
public class Config extends AnnotationConfig {

    /**
     * The plugin description file.
     */
    public PluginDescriptionFile pdf;

    /**
     * The file path for the configuration file.
     */
    public final String configFilePath = RealEstate.pluginDirPath + "config.yml";

    /**
     * The file path for the log file.
     */
    public final String logFilePath = RealEstate.pluginDirPath + "GriefProtection_RealEstate.log";

    // Keyword configurations

    /**
     * The prefix displayed before any chat message.
     */
    @ConfigField(name="RealEstate.Keywords.ChatPrefix", comment="What is displayed before any chat message")
    public String chatPrefix = "$f[$6RealEstate$f] ";

    /**
     * The header displayed on the top of the signs.
     */
    @ConfigField(name="RealEstate.Keywords.SignsHeader", comment = "What is displayed in top of the signs")
    public String cfgSignsHeader = "$6[RealEstate]";

    /**
     * A list of possible sign headers used to indicate a claim is for sale.
     */
    @ConfigField(name="RealEstate.Keywords.Sell", comment = "List of all possible signs headers to sell a claim")
    public List<String> cfgSellKeywords = Arrays.asList("[sell]", "[sell claim]", "[sc]", "[re]", "[realestate]");

    /**
     * A list of possible sign headers used to indicate a claim is for rent.
     */
    @ConfigField(name="RealEstate.Keywords.Rent", comment = "List of all possible signs headers to rent a claim")
    public List<String> cfgRentKeywords = Arrays.asList("[rent]", "[rent claim]", "[rc]");

    /**
     * A list of possible sign headers used for renting container access only.
     */
    @ConfigField(name="RealEstate.Keywords.ContainerRent", comment = "List of all possible signs headers to rent container access only")
    public List<String> cfgContainerRentKeywords = Arrays.asList("[container rent]", "[crent]");

    /**
     * A list of possible sign headers used to indicate a claim is for lease.
     */
    @ConfigField(name="RealEstate.Keywords.Lease", comment = "List of all possible signs headers to lease a claim")
    public List<String> cfgLeaseKeywords = Arrays.asList("[lease]", "[lease claim]", "[lc]");

    /**
     * A list of possible sign headers used to indicate a claim is for auction.
     */
    @ConfigField(name="RealEstate.Keywords.Auction", comment = "List of all possible signs headers to auction a claim")
    public List<String> cfgAuctionKeywords = Arrays.asList("[auction]", "[auction claim]", "[ac]");

    // Replacement texts for signs

    /**
     * The text displayed on signs for properties to sell.
     */
    @ConfigField(name="RealEstate.Keywords.Replace.Sell", comment = "What is displayed on signs for properties to sell")
    public String cfgReplaceSell = "FOR SALE";

    /**
     * The text displayed on signs for properties to rent.
     */
    @ConfigField(name="RealEstate.Keywords.Replace.Rent", comment = "What is displayed on signs for properties to rent")
    public String cfgReplaceRent = "FOR RENT";

    /**
     * The text displayed on signs for properties to lease.
     */
    @ConfigField(name="RealEstate.Keywords.Replace.Lease", comment = "What is displayed on signs for properties to lease")
    public String cfgReplaceLease = "FOR LEASE";

    /**
     * The text displayed on signs for properties to auction.
     */
    @ConfigField(name="RealEstate.Keywords.Replace.Auction", comment = "What is displayed on signs for properties to auction")
    public String cfgReplaceAuction = "FOR AUCTION";

    /**
     * The text displayed on the first line of a sign once a claim is rented.
     */
    @ConfigField(name="RealEstate.Keywords.Replace.Ongoing.Rent", comment = "What is displayed on the first line of the sign once someone rents a claim.")
    public String cfgReplaceOngoingRent = "[Rented]";

    /**
     * The text displayed on the third line of a sign when renting container access only.
     */
    @ConfigField(name="RealEstate.Keywords.Replace.ContainerRent", comment = "What is displayed on the third line of the sign when renting container access only.")
    public String cfgContainerRentLine = ChatColor.BLUE + "Containers only";

    // Rule configurations

    /**
     * Whether selling claims is enabled.
     */
    @ConfigField(name="RealEstate.Rules.Sell", comment = "Is selling claims enabled?")
    public boolean cfgEnableSell = true;

    /**
     * Whether renting claims is enabled.
     */
    @ConfigField(name="RealEstate.Rules.Rent", comment = "Is renting claims enabled?")
    public boolean cfgEnableRent = true;

    /**
     * Whether leasing claims is enabled.
     */
    @ConfigField(name="RealEstate.Rules.Lease", comment = "Is leasing claims enabled?")
    public boolean cfgEnableLease = true;

    /**
     * Whether auctioning claims is enabled.
     */
    @ConfigField(name="RealEstate.Rules.Auction", comment = "Is auctioning claims enabled?")
    public boolean cfgEnableAuction = true;

    /**
     * Whether an auctioneer can cancel their auction after receiving offers.
     */
    @ConfigField(name="RealEstate.Rules.CancelAuction", comment = "Can an auctioneer cancel his auction if he already received offers?")
    public boolean cfgCancelAuction = false;

    /**
     * Whether an auctioneer can outbid themselves.
     */
    @ConfigField(name="RealEstate.Rules.DisableOutbidSelf", comment = "Can an auctioneer outbid himself?")
    public boolean cfgDisableOutbidSelf = false;

    /**
     * Whether players renting claims can enable automatic renew.
     */
    @ConfigField(name="RealEstate.Rules.AutomaticRenew", comment = "Can players renting claims enable automatic renew of their contracts?")
    public boolean cfgEnableAutoRenew = true;

    /**
     * Whether rent signs should be destroyed once the claim is rented.
     */
    @ConfigField(name="RealEstate.Rules.DestroySigns.Rent", comment = "Should the rent signs get destroyed once the claim is rented?")
    public boolean cfgDestroyRentSigns = false;

    /**
     * Whether lease signs should be destroyed once the claim is leased.
     */
    @ConfigField(name="RealEstate.Rules.DestroySigns.Lease", comment = "Should the lease signs get destroyed once the claim is leased?")
    public boolean cfgDestroyLeaseSigns = true;

    /**
     * Whether claim blocks are transferred to the new owner on purchase.
     */
    @ConfigField(name="RealEstate.Rules.TransferClaimBlocks", comment = "Are the claim blocks transferred to the new owner on purchase or should the buyer provide them?")
    public boolean cfgTransferClaimBlocks = true;

    /**
     * Whether to display prices with a currency symbol on signs.
     */
    @ConfigField(name="RealEstate.Rules.UseCurrencySymbol", comment = "Should the signs display the prices with a currency symbol instead of the full currency name?")
    public boolean cfgUseCurrencySymbol = false;

    /**
     * The currency symbol to use if UseCurrencySymbol is enabled.
     */
    @ConfigField(name="RealEstate.Rules.CurrencySymbol", comment = "In case UseCurrencySymbol is true, what symbol should be used?")
    public String cfgCurrencySymbol = "$";

    /**
     * Whether decimal currency values are allowed.
     */
    @ConfigField(name="RealEstate.Rules.UseDecimalCurrency", comment = "Allow players to use decimal currency e.g. $10.15")
    public boolean cfgUseDecimalCurrency = true;

    // Messaging configurations

    /**
     * Whether the owner should be messaged when a claim is rented, leased, or bought.
     */
    @ConfigField(name="RealEstate.Messaging.MessageOwner", comment = "Should the owner get messaged once one of his claim is rented/leased/bought and on end of contracts?")
    public boolean cfgMessageOwner = true;

    /**
     * Whether the buyer should be messaged when a claim is rented, leased, or bought.
     */
    @ConfigField(name="RealEstate.Messaging.MessageBuyer", comment = "Should the buyer get messaged once one of his claim is rented/leased/bought and on end of contracts?")
    public boolean cfgMessageBuyer = true;

    /**
     * Whether a message should be broadcast when a claim is put up for sale, rent, or lease.
     */
    @ConfigField(name="RealEstate.Messaging.BroadcastSell", comment = "Should a message get broadcasted when a player puts a claim for sale/rent/lease?")
    public boolean cfgBroadcastSell = true;

    /**
     * Whether offline owners/buyers should receive mail notifications.
     */
    @ConfigField(name="RealEstate.Messaging.MailOffline", comment = "Should offline owners/buyers receive mails (using the Essentials plugin) when they're offline?")
    public boolean cfgMailOffline = true;

    // Default pricing and duration configurations

    /**
     * The default price per block when selling a claim.
     */
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Sell", comment = "The default price per block when selling a claim")
    public double cfgPriceSellPerBlock = 5.0;

    /**
     * The default price per block when renting a claim.
     */
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Rent", comment = "The default price per block when renting a claim")
    public double cfgPriceRentPerBlock = 2.0;

    /**
     * The default price per block when leasing a claim.
     */
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Lease", comment = "The default price per block when leasing a claim")
    public double cfgPriceLeasePerBlock = 2.0;

    /**
     * The default price per block when auctioning a claim.
     */
    @ConfigField(name="RealEstate.Default.PricesPerBlock.Auction", comment = "The default price per block when auctioning a claim")
    public double cfgPriceAuctionPerBlock = 1.0;

    /**
     * The default auction bid step when auctioning a claim.
     */
    @ConfigField(name="RealEstate.Default.Prices.AuctionBidStep", comment = "The default bid step when auctioning a claim")
    public double cfgPriceAuctionBidStep = 2.0;

    /**
     * The default duration of a rent period.
     */
    @ConfigField(name="RealEstate.Default.Duration.Rent", comment = "How long is a rent period by default")
    public String cfgRentTime = "7D";

    /**
     * The default duration of a lease period.
     */
    @ConfigField(name="RealEstate.Default.Duration.Lease", comment = "How long is a lease period by default")
    public String cfgLeaseTime = "7D";

    /**
     * The default duration of an auction period.
     */
    @ConfigField(name="RealEstate.Default.Duration.Auction", comment = "How long is an auction period by default")
    public String cfgAuctionTime = "7D";

    /**
     * The default number of lease payments required before the buyer gains ownership.
     */
    @ConfigField(name="RealEstate.Default.LeasePaymentsCount", comment = "How many lease periods are required before the buyer gets the claim's ownership by default")
    public int cfgLeasePayments = 5;
    
    /**
     * The number of offers to display per page in the '/re list' command.
     */
    @ConfigField(name="RealEstate.Settings.PageSize", comment = "How many Real Estate offers should be shown per page using the '/re list' command")
    public int cfgPageSize = 8;
    
    /**
     * The language file to be used (found in the languages directory).
     */
    @ConfigField(name="RealEstate.Settings.MessagesFiles", comment="Language file to be used. See the languages directory for available files.")
    public String languageFile = "en-us.yml";
    
    /**
     * The type of database to use: MySQL, YML, or SQLite.
     */
    @ConfigField(name="RealEstate.Settings.Database.DatabaseType", comment="Database type: MySQL, YML, or SQLite")
    public String databaseType = "YML";
    
    /**
     * The SQLite database name.
     */
    @ConfigField(name="RealEstate.Settings.Database.SQLite.Database", comment="SQLite database name")
    public String sqliteDatabase = "RealEstate.db";
    
    /**
     * The MySQL database host.
     */
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Host", comment="MySQL database host")
    public String mysqlHost = "localhost";
    
    /**
     * The MySQL database port.
     */
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Port", comment="MySQL database port")
    public int mysqlPort = 3306;
    
    /**
     * The MySQL database name.
     */
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Database", comment="MySQL database name")
    public String mysqlDatabase = "RealEstate";
    
    /**
     * The MySQL database username.
     */
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Username", comment="MySQL database username")
    public String mysqlUsername = "";
    
    /**
     * The MySQL database password.
     */
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Password", comment="MySQL database password")
    public String mysqlPassword = "";

    /**
     * The MySQL table prefix.
     */
    @ConfigField(name="RealEstate.Settings.Database.MySQL.Prefix", comment="MySQL database table prefix")
    public String mysqlPrefix = "realestate_";
    
    /**
     * Whether to use SSL for the MySQL connection.
     */
    @ConfigField(name="RealEstate.Settings.Database.MySQL.UseSSL", comment="MySQL database use SSL")
    public boolean mysqlUseSSL = false;
    
    /**
     * Whether the MySQL connection should automatically reconnect.
     */
    @ConfigField(name="RealEstate.Settings.Database.MySQL.AutoReconnect", comment="MySQL database auto reconnect")
    public boolean mysqlAutoReconnect = true;
    
    /**
     * Constructs a new configuration instance.
     * <p>
     * Initializes the plugin description file from the RealEstate instance.
     * </p>
     */
    public Config() {
        this.pdf = RealEstate.instance.getDescription();
    }
    
    /**
     * Converts a list of strings into a single string with each element separated by a semicolon.
     *
     * @param li the list of strings
     * @return the concatenated string
     */
    public String getString(List<String> li) {
        return String.join(";", li);
    }
    
    /**
     * Splits a string into a list using semicolon as the delimiter.
     *
     * @param str the string to split
     * @return a list of strings
     */
    public List<String> getList(String str) {
        return Arrays.asList(str.split(";"));
    }
    
    /**
     * Retrieves a list of strings from the provided YamlConfiguration at the specified path.
     * <p>
     * Adds a default value if the path is not present and converts all entries to lowercase.
     * </p>
     *
     * @param config the YamlConfiguration instance
     * @param path the configuration path
     * @param defVal the default list of strings
     * @return the list of strings from the configuration
     */
    List<String> getConfigList(YamlConfiguration config, String path, List<String> defVal) {
        config.addDefault(path, defVal);
        List<String> ret = config.getStringList(path);
        ret.replaceAll(String::toLowerCase);
        return ret;
    }
    
    /**
     * Loads the configuration from the file specified by {@code configFilePath}.
     */
    @Override
    public void loadConfig() {
        this.loadConfig(this.configFilePath);
    }
}
