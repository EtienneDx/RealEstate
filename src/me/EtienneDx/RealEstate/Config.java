package me.EtienneDx.RealEstate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
    final static String messagesFilePath = RealEstate.pluginDirPath + "messages.yml";

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
    
    @ConfigField(name="RealEstate.Settings.PageSize", comment = "How many Real Estate offer should be shown by page using the '/re list' command")
    public int cfgPageSize = 20;

    private String[] messages;

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
    
    @Override
    public void loadConfig()
    {
        //YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(this.configFilePath));
        this.loadConfig(this.configFilePath);
    }

    public void loadMessages() {
        Messages[] messageIDs = Messages.values();
        this.messages = new String[Messages.values().length];

        HashMap<String, CustomizableMessage> defaults = new HashMap<String, CustomizableMessage>();
        // initialize defaults
        this.addDefault(defaults, Messages.NoTransactionFound, "$cNo transaction found at your location!", null);
        this.addDefault(defaults, Messages.PageMustBePositive, "$cPage must be a positive option!", null);
        this.addDefault(defaults, Messages.PageNotExists, "$cThis page does not exist!", null);
        this.addDefault(defaults, Messages.RenewRentNow, "$bAutomatic renew is now $a{0} $bfor this {1}", "0: the status; 1: a claim type");
        this.addDefault(defaults, Messages.RenewRentCurrently, "$bAutomatic renew is currently $a{0} $bfor this {1}", "0: the status; 1: a claim type");



        // load the config file
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(messagesFilePath));

        // for each message ID
        for (Messages messageID : messageIDs) {
            //get default for this message
            CustomizableMessage messageData = defaults.get(messageID.name());

            // if default is missing, log an error and use some fake data for now so that the plugin can run
            if (messageData == null) {
                RealEstate.instance.log.info("Missing message for " + messageID.name() + ".  Please contact the developer.");
                messageData = new CustomizableMessage(messageID, "Missing message!  ID: " + messageID.name() + ".  Please contact a server admin.", null);
            }

            // read the message from the file, use default if necessary
            this.messages[messageID.ordinal()] = config.getString("Messages." + messageID.name() + ".Text", messageData.text);
            config.set("Messages." + messageID.name() + ".Text", this.messages[messageID.ordinal()]);

            this.messages[messageID.ordinal()] = this.messages[messageID.ordinal()].replace('$', (char) 0x00A7);

            if (messageData.notes != null) {
                messageData.notes = config.getString("Messages." + messageID.name() + ".Notes", messageData.notes);
                config.set("Messages." + messageID.name() + ".Notes", messageData.notes);
            }
        }

        //save any changes
        try {
            config.options().header("Use a YAML editor like NotepadPlusPlus to edit this file.  \nAfter editing, back up your changes before reloading the server in case you made a syntax error.  \nUse dollar signs ($) for formatting codes, which are documented here: http://minecraft.gamepedia.com/Formatting_codes");
            config.save(messagesFilePath);
        } catch (IOException exception) {
            RealEstate.instance.log.info("Unable to write to the configuration file at \"" + messagesFilePath + "\"");
        }

        defaults.clear();
        RealEstate.instance.log.info("Customizable messages loaded.");
        System.gc();
    }

    private void addDefault(HashMap<String, CustomizableMessage> defaults,
                            Messages id, String text, String notes) {
        CustomizableMessage message = new CustomizableMessage(id, text, notes);
        defaults.put(id.name(), message);
    }

    synchronized public String getMessage(Messages messageID, String... args) {
        String message = messages[messageID.ordinal()];

        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            message = message.replace("{" + i + "}", param);
        }

        return message;
    }
    //sends a color-coded message to a player
    public static void sendMessage(Player player, ChatColor color, Messages messageID, String... args) {
        sendMessage(player, color, messageID, 0, args);
    }

    //sends a color-coded message to a player
    public static void sendMessage(Player player, ChatColor color, Messages messageID, long delayInTicks, String... args) {
        String message = RealEstate.instance.config.getMessage(messageID, args);
        sendMessage(player, color, message, delayInTicks);
    }

    //sends a color-coded message to a player
    public static void sendMessage(Player player, ChatColor color, String message) {
        if (message == null || message.length() == 0) return;

        if (player == null) {
            RealEstate.instance.log.info(color + message);
        } else {
            player.sendMessage(RealEstate.instance.config.chatPrefix + color + message);
        }
    }

    public static void sendMessage(Player player, ChatColor color, String message, long delayInTicks) {
        SendPlayerMessageTask task = new SendPlayerMessageTask(player, color, message);

        if (delayInTicks > 0) {
            RealEstate.instance.getServer().getScheduler().runTaskLater(RealEstate.instance, task, delayInTicks);
        } else {
            task.run();
        }
    }

}
