package me.EtienneDx.RealEstate;

import me.EtienneDx.AnnotationConfig.AnnotationConfig;
import me.EtienneDx.AnnotationConfig.ConfigField;
import me.EtienneDx.AnnotationConfig.ConfigFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Arrays;
import java.util.List;

@ConfigFile(header = "Use a YAML editor like NotepadPlusPlus to edit this file.  \nAfter editing, back up your changes before reloading the server in case you made a syntax error.  \nUse dollar signs ($) for formatting codes, which are documented here: http://minecraft.gamepedia.com/Formatting_codes")
public class Messages extends AnnotationConfig
{
    public PluginDescriptionFile pdf;

    @ConfigField(name="RealEstate.Messages.NoTransactionFound")
    public String msgNoTransactionFound = "$cNo transaction found at your location!";

    @ConfigField(name="RealEstate.Messages.PageMustBePositive")
    public String msgPageMustBePositive = "$cPage must be a positive option";

    @ConfigField(name="RealEstate.Messages.PageNotExists")
    public String msgPageNotExists = "$cThis page does not exist!";

    @ConfigField(name="RealEstate.Messages.RenewRentNow", comment = "0: enabled/disabled; 1: type of claim")
    public String msgRenewRentNow = "$bAutomatic renew is now $a{0} $bfor this {1}";

    @ConfigField(name="RealEstate.Messages.RenewRentCurrently", comment = "0: enabled/disabled; 1: type of claim")
    public String msgRenewRentCurrently = "$bAutomatic renew is currently $a{0} $bfor this {1}";

    public Messages()
    {
        this.pdf = RealEstate.instance.getDescription();
    }

    @Override
    public void loadConfig()
    {
        this.loadConfig(RealEstate.messagesFilePath);
    }

    synchronized public String getMessage(String msgTemplate, String... args) {
        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            msgTemplate = msgTemplate.replace("{" + i + "}", param);
        }

        return msgTemplate.replace('$', (char) 0x00A7);
    }
    //sends a color-coded message to a player
    public static void sendMessage(Player player, String msgTemplate, String... args) {
        sendMessage(player, msgTemplate, 0, args);
    }

    //sends a color-coded message to a player
    public static void sendMessage(Player player, String msgTemplate, long delayInTicks, String... args) {
        String message = RealEstate.instance.messages.getMessage(msgTemplate, args);
        sendMessage(player, message, delayInTicks);
    }

    //sends a color-coded message to a player
    public static void sendMessage(Player player, String message) {
        if (message == null || message.length() == 0) return;

        if (player == null) {
            RealEstate.instance.log.info(message);
        } else {
            player.sendMessage(RealEstate.instance.config.chatPrefix + message.replace('$', (char) 0x00A7));
        }
    }

    public static void sendMessage(Player player, String message, long delayInTicks) {
        SendPlayerMessageTask task = new SendPlayerMessageTask(player, message);

        if (delayInTicks > 0) {
            RealEstate.instance.getServer().getScheduler().runTaskLater(RealEstate.instance, task, delayInTicks);
        } else {
            task.run();
        }
    }

}