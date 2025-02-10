package me.EtienneDx.RealEstate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
	public Messages messages;
    BukkitCommandManager manager;
	public final static String pluginDirPath = "plugins" + File.separator + "RealEstate" + File.separator;
	final static String languagesDirectory = RealEstate.pluginDirPath + "languages";
    public static boolean vaultPresent = false;
    public static Economy econ = null;
    public static Permission perms = null;
    public static Essentials ess = null;
    
    public static RealEstate instance = null;
    
    public static TransactionsStore transactionsStore = null;
	
	//@SuppressWarnings("deprecation")
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

		this.messages = new Messages();
		this.messages.loadConfig();// loads customizable messages or defaults
		this.messages.saveConfig();// save eventual default
		this.log.info("Customizable messages loaded.");

        ConfigurationSerialization.registerClass(ClaimSell.class);
        ConfigurationSerialization.registerClass(ClaimRent.class);
        ConfigurationSerialization.registerClass(ClaimLease.class);
        ConfigurationSerialization.registerClass(ExitOffer.class);
        
        RealEstate.transactionsStore = new TransactionsStore();
        
        new REListener().registerEvents();
        new ClaimPermissionListener().registerEvents();
        
        manager = new BukkitCommandManager(this);
        
        registerConditions();
        manager.registerCommand(new RECommand());

		copyResourcesIntoPluginDirectory();
	}

    private void registerConditions()
    {
        manager.getCommandConditions().addCondition("inClaim", (context) -> {
        	if(context.getIssuer().isPlayer() && 
        			GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null) != null)
        	{
        		return;
        	}
        	throw new ConditionFailedException(Messages.getMessage(messages.msgErrorOutOfClaim));
        });
        manager.getCommandConditions().addCondition("claimHasTransaction", (context) -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorPlayerOnly));
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorOutOfClaim));
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorNoOngoingTransaction));
        	}
        });
        manager.getCommandConditions().addCondition("inPendingTransactionClaim", (context) -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorPlayerOnly));
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorOutOfClaim));
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorNotRentNorLease));
        	}
        	else if(tr instanceof BoughtTransaction && ((BoughtTransaction)tr).getBuyer() != null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorAlreadyBought));
        	}
        });
        manager.getCommandConditions().addCondition("inBoughtClaim", (context) -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorPlayerOnly));
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorOutOfClaim));
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null || !(tr instanceof BoughtTransaction))
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorNotRentNorLease));
        	}
        });
        manager.getCommandConditions().addCondition("partOfBoughtTransaction", context -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorPlayerOnly));
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorOutOfClaim));
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorNoOngoingTransaction));
        	}
        	if(!(tr instanceof BoughtTransaction))
        	{
            	throw new ConditionFailedException(Messages.getMessage(messages.msgErrorNotRentNorLease));
        	}
        	if((((BoughtTransaction)tr).buyer != null && ((BoughtTransaction)tr).buyer.equals(context.getIssuer().getPlayer().getUniqueId())) || 
        			(tr.getOwner() != null && (tr.getOwner().equals(context.getIssuer().getPlayer().getUniqueId()))) || 
        			(c.isAdminClaim() && RealEstate.perms.has(context.getIssuer().getPlayer(), "realestate.admin")))
        	{
        		return;
        	}
        	throw new ConditionFailedException(Messages.getMessage(messages.msgErrorNotPartOfTransaction));
        });
        manager.getCommandConditions().addCondition("partOfRent", context -> {
        	if(!context.getIssuer().isPlayer())
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorPlayerOnly));
        	}
        	Claim c = GriefPrevention.instance.dataStore.getClaimAt(context.getIssuer().getPlayer().getLocation(), false, null);
        	if(c == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorOutOfClaim));
        	}
        	Transaction tr = transactionsStore.getTransaction(c);
        	if(tr == null)
        	{
        		throw new ConditionFailedException(Messages.getMessage(messages.msgErrorNoOngoingTransaction));
        	}
        	if(!(tr instanceof ClaimRent))
        	{
            	throw new ConditionFailedException(Messages.getMessage(messages.msgErrorRentOnly));
        	}
        	if((((ClaimRent)tr).buyer != null && ((ClaimRent)tr).buyer.equals(context.getIssuer().getPlayer().getUniqueId())) || 
        			(tr.getOwner() != null && (tr.getOwner().equals(context.getIssuer().getPlayer().getUniqueId()))) || 
        			(c.isAdminClaim() && RealEstate.perms.has(context.getIssuer().getPlayer(), "realestate.admin")))
        	{
        		return;
        	}
        	throw new ConditionFailedException(Messages.getMessage(messages.msgErrorNotPartOfTransaction));
        });
        manager.getCommandConditions().addCondition(Double.class, "positiveDouble", (c, exec, value) -> {
        	if(value > 0) return;
        	throw new ConditionFailedException(Messages.getMessage(messages.msgErrorValueGreaterThanZero));
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

	private void copyResourcesIntoPluginDirectory()
	{
		Path pluginPath = Paths.get(RealEstate.pluginDirPath);
		File pluginDirectory = pluginPath.toFile();
		if(!pluginDirectory.exists())
		{
			pluginDirectory.mkdirs();
		}
		// for each file in the resource folder
		FileSystem fileSystem = null;
		try
		{
			URI uri = RealEstate.class.getResource("/resources").toURI();
			Path myPath;

			if (uri.getScheme().equals("jar"))
			{
				fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				myPath = fileSystem.getPath("/resources");
			}
			else
			{
				myPath = Paths.get(uri);
			}
			
			try(Stream<Path> walk = Files.walk(myPath, 3))
			{
				Iterator<Path> it = walk.iterator();
				it.next();// skip first
				for (; it.hasNext();)
				{
					Path path = it.next();
					Path targetPath = pluginPath.resolve(path.toString().substring(11));
					this.log.info(path.toString());
					this.log.info(targetPath.toString());
					if(!targetPath.toFile().exists())
					{
						Files.createDirectories(targetPath.getParent());
						try(InputStream s = RealEstate.class.getResourceAsStream(path.toString()))
						{
							if(s.available() > 0)
							{
								Files.copy(s, targetPath);
							}
						}
						catch(NoSuchFileException e)
						{
							// ignore
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			this.log.warning("Couldn't copy resources to plugin directory...");
			e.printStackTrace();
		}
		if(fileSystem != null)
		{
			try
			{
				fileSystem.close();
			}
			catch(IOException e)
			{
				// do nothing in case of error
			}
		}
	}
}
