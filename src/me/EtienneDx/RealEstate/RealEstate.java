package me.EtienneDx.RealEstate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import me.EtienneDx.RealEstate.Transactions.BoughtTransaction;
import me.EtienneDx.RealEstate.Transactions.ClaimLease;
import me.EtienneDx.RealEstate.Transactions.ClaimRent;
import me.EtienneDx.RealEstate.Transactions.ClaimSell;
import me.EtienneDx.RealEstate.Transactions.ExitOffer;
import me.EtienneDx.RealEstate.Transactions.Transaction;
import me.EtienneDx.RealEstate.Transactions.TransactionsStore;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class RealEstate extends JavaPlugin
{
	public Logger log;
    public Config config;
    BukkitCommandManager manager;
	public final static String pluginDirPath = "plugins" + File.separator + "RealEstate" + File.separator;
    public static boolean vaultPresent = false;
    public static Economy econ = null;
    public static Permission perms = null;
    public static Essentials ess = null;
    
    public static RealEstate instance = null;
    
    public static TransactionsStore transactionsStore = null;
	
	@SuppressWarnings("deprecation")
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
        if((ess = (Essentials)getServer().getPluginManager().getPlugin("Essentials")) != null)
        {
        	this.log.info("Found Essentials, using version " + ess.getDescription().getVersion());
        }
        this.config = new Config();
        this.config.loadConfig();// loads config or default
        this.config.saveConfig();// save eventual default

        ConfigurationSerialization.registerClass(ClaimSell.class);
        ConfigurationSerialization.registerClass(ClaimRent.class);
        ConfigurationSerialization.registerClass(ClaimLease.class);
        ConfigurationSerialization.registerClass(ExitOffer.class);
        
        RealEstate.transactionsStore = new TransactionsStore();
        
        new REListener().registerEvents();
        
        manager = new BukkitCommandManager(this);
        manager.enableUnstableAPI("help");
        registerConditions();
        manager.registerCommand(new RECommand());
        
        GriefPrevention.addonPlugins.add(new GP_RealEstateHook());
	}

    private void registerConditions()
    {
        manager.getCommandConditions().addCondition("inClaim", (context) -> {
        	if(context.getIssuer().isPlayer() && 
        			GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null) != null)
        	{
        		return;
        	}
        	throw new ConditionFailedException("You must stand inside of a claim to use this command!");
        });
        manager.getCommandConditions().addCondition("inPendingTransactionClaim", (context) -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException("Only Players can perform this command!");
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException("You must stand inside of a claim to use this command!");
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null)
        	{
        		throw new ConditionFailedException("This claim is neither to rent or to lease!");
        	}
        	else if(tr instanceof BoughtTransaction && ((BoughtTransaction)tr).getBuyer() != null)
        	{
        		throw new ConditionFailedException("This claim already has a buyer!");
        	}
        });
        manager.getCommandConditions().addCondition("inBoughtClaim", (context) -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException("Only Players can perform this command!");
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException("You must stand inside of a claim to use this command!");
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null || !(tr instanceof BoughtTransaction))
        	{
        		throw new ConditionFailedException("This claim is neither to rent or to lease!");
        	}
        });
        manager.getCommandConditions().addCondition("partOfBoughtTransaction", context -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException("Only Players can perform this command!");
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException("You must stand inside of a claim to use this command!");
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null)
        	{
        		throw new ConditionFailedException("This claim is neither to sell, rent or lease!");
        	}
        	if(!(tr instanceof BoughtTransaction))
        	{
            	throw new ConditionFailedException("This command only applies to rented or leased claims!");
        	}
        	if((((BoughtTransaction)tr).buyer != null && ((BoughtTransaction)tr).buyer.equals(context.getIssuer().getPlayer().getUniqueId())) || 
        			(tr.getOwner() != null && tr.getOwner().equals(context.getIssuer().getPlayer().getUniqueId())))
        	{
        		return;
        	}
        	throw new ConditionFailedException("You are not part of this transaction!");
        });
        manager.getCommandConditions().addCondition("partOfRent", context -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException("Only Players can perform this command!");
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException("You must stand inside of a claim to use this command!");
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null)
        	{
        		throw new ConditionFailedException("This claim is neither to sell, rent or lease!");
        	}
        	if(!(tr instanceof ClaimRent))
        	{
            	throw new ConditionFailedException("This command only applies to rented claims!");
        	}
        	if((((ClaimRent)tr).buyer != null && ((ClaimRent)tr).buyer.equals(context.getIssuer().getPlayer().getUniqueId())) || 
        			tr.getOwner().equals(context.getIssuer().getPlayer().getUniqueId()))
        	{
        		return;
        	}
        	throw new ConditionFailedException("You are not part of this transaction!");
        });
        manager.getCommandConditions().addCondition(Double.class, "positiveDouble", (c, exec, value) -> {
        	if(value > 0) return;
        	throw new ConditionFailedException("The value must be greater than zero!");
        });
	}

	public void addLogEntry(String entry)
    {
        try
        {
            File logFile = new File(this.config.logFilePath);
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
