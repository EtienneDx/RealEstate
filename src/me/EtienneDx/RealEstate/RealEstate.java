package me.EtienneDx.RealEstate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class RealEstate extends JavaPlugin
{
	Logger log;
    DataStore dataStore;
	public final static String pluginDirPath = "plugins" + File.separator + "RealEstate" + File.separator;
    public static boolean vaultPresent = false;
    public static Economy econ = null;
    public static Permission perms = null;
    
    public static RealEstate instance = null;
    
    public static TransactionsStore transactionsStore = null;
	
	public void onEnable()
	{
		RealEstate.instance = this;
        this.log = getLogger();
        
        if (checkVault())
        {
            this.log.info("Vault has been detected and enabled.");
            if (setupEconomy())
            {
                this.log.info("Vault is using " + econ.getName() + " as the economy plugin.");
            }
            else
            {
                this.log.warning("No compatible economy plugin detected [Vault].");
                this.log.warning("Disabling plugin.");
                getPluginLoader().disablePlugin(this);
                return;
            }
            if (setupPermissions())
            {
                this.log.info("Vault is using " + perms.getName() + " for the permissions.");
            }
            else
            {
                this.log.warning("No compatible permissions plugin detected [Vault].");
                this.log.warning("Disabling plugin.");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }
        this.dataStore = new DataStore();
        this.dataStore.loadConfig();// loads config or default
        this.dataStore.saveConfig();// save eventual default

        ConfigurationSerialization.registerClass(ClaimSell.class);
        ConfigurationSerialization.registerClass(ClaimRent.class);
        
        RealEstate.transactionsStore = new TransactionsStore();
        
        new REListener().registerEvents();
	}

    public void addLogEntry(String entry)
    {
        try
        {
            File logFile = new File(this.dataStore.logFilePath);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileWriter fw = new FileWriter(logFile, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(entry);
            pw.flush();
            pw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean checkVault()
    {
        vaultPresent = getServer().getPluginManager().getPlugin("Vault") != null;
        return vaultPresent;
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = (Economy)rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = (Permission)rsp.getProvider();
        return perms != null;
    }
}
