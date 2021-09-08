package me.EtienneDx.RealEstate;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import me.EtienneDx.AnnotationConfig.AnnotationConfig;
import me.EtienneDx.AnnotationConfig.ConfigField;
import me.EtienneDx.AnnotationConfig.ConfigFile;

@ConfigFile(header = "Use a YAML editor like NotepadPlusPlus to edit this file.  \nAfter editing, back up your changes before reloading the server in case you made a syntax error.  \nUse dollar signs ($) for formatting codes, which are documented here: http://minecraft.gamepedia.com/Formatting_codes.\n You can use {0}, {1} to include the different values indicated in the comments")
public class Messages extends AnnotationConfig
{
    public PluginDescriptionFile pdf;

    @ConfigField(name="RealEstate.NoTransactionFound")
    public String msgNoTransactionFound = "$cNo transaction found at your location!";

    @ConfigField(name="RealEstate.PageMustBePositive")
    public String msgPageMustBePositive = "$cPage must be a positive option";

    @ConfigField(name="RealEstate.PageNotExists")
    public String msgPageNotExists = "$cThis page does not exist!";

    @ConfigField(name="RealEstate.RenewRentNow", comment = "0: enabled/disabled; 1: type of claim")
    public String msgRenewRentNow = "$bAutomatic renew is now $a{0} $bfor this {1}";

    @ConfigField(name="RealEstate.RenewRentCurrently", comment = "0: enabled/disabled; 1: type of claim")
    public String msgRenewRentCurrently = "$bAutomatic renew is currently $a{0} $bfor this {1}";
    
    @ConfigField(name="RealEstate.Errors.OutOfClaim")
    public String msgErrorOutOfClaim = "$cYou must stand inside of a claim to use this command!";
    
    @ConfigField(name="RealEstate.Errors.PlayerOnlyCmd")
    public String msgErrorPlayerOnly = "$cOnly Players can perform this command!";
    
    @ConfigField(name="RealEstate.Errors.NoOngoingTransaction")
    public String msgErrorNoOngoingTransaction = "$cThis claim has no ongoing transactions!";
    
    @ConfigField(name="RealEstate.Errors.NotRentNorLease")
    public String msgErrorNotRentNorLease = "$cThis claim is neither to rent or to lease!";
    
    @ConfigField(name="RealEstate.Errors.AlreadyBought")
    public String msgErrorAlreadyBought = "$cThis claim already has a buyer!";
    
    @ConfigField(name="RealEstate.Errors.NotPartOfTransaction")
    public String msgErrorNotPartOfTransaction = "$cYou are not part of this transaction!";
    
    @ConfigField(name="RealEstate.Errors.RentOnly")
    public String msgErrorRentOnly = "$cThis command only applies to rented claims!";
    
    @ConfigField(name="RealEstate.Errors.ValueGreaterThanZero")
    public String msgErrorValueGreaterThanZero = "$cThe value must be greater than zero!";
    
    @ConfigField(name="RealEstate.Errors.InvalidOption")
    public String msgErrorInvalidOption = "$cInvalid option provided!";
    
    @ConfigField(name="RealEstate.List.Header", comment = "0: RE Offers|Sell Offers|Rent Offers|Lease Offers; 1: Page number; 2: Page count")
    public String msgListTransactionsHeader = "$1----= $f[ $6{0} page $2 {1} $6/ $2{2} $f] $1=----";
    
    @ConfigField(name="RealEstate.List.NextPage", comment="0: all|sell|rent|lease; 1: next page number")
    public String msgListNextPage = "$6To see the next page, type $a/re list {0} {1}";

    public Messages()
    {
        this.pdf = RealEstate.instance.getDescription();
    }

    @Override
    public void loadConfig()
    {
        this.loadConfig(RealEstate.languagesDirectory + "/" + RealEstate.instance.config.languageFile);
    }

    synchronized public String getMessage(String msgTemplate, String... args) {
        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            msgTemplate = msgTemplate.replace("{" + i + "}", param);
        }

        return msgTemplate.replace('$', (char) 0x00A7);
    }
    //sends a color-coded message to a player
    public static void sendMessage(CommandSender player, String msgTemplate, String... args) {
        sendMessage(player, msgTemplate, 0, args);
    }

    //sends a color-coded message to a player
    public static void sendMessage(CommandSender player, String msgTemplate, long delayInTicks, String... args) {
        String message = RealEstate.instance.messages.getMessage(msgTemplate, args);
        sendMessage(player, message, delayInTicks);
    }

    //sends a color-coded message to a player
    public static void sendMessage(CommandSender player, String message) {
        if (message == null || message.length() == 0) return;

        if (player == null) {
            RealEstate.instance.log.info(message);
        } else {
            player.sendMessage(RealEstate.instance.config.chatPrefix + message.replace('$', (char) 0x00A7));
        }
    }

    public static void sendMessage(CommandSender player, String message, long delayInTicks) {
        SendPlayerMessageTask task = new SendPlayerMessageTask(player, message);

        if (delayInTicks > 0) {
            RealEstate.instance.getServer().getScheduler().runTaskLater(RealEstate.instance, task, delayInTicks);
        } else {
            task.run();
        }
    }

}
