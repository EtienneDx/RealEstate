package me.EtienneDx.RealEstate.Transactions;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.EtienneDx.RealEstate.Messages;
import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.Utils;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;

public class TransactionsStore {

    public final String dataFilePath = RealEstate.pluginDirPath + "transactions.yml";
    // Make these public so that other classes can reference them if needed.
    public DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public Date date = new Date();

    public HashMap<String, ClaimSell> claimSell;
    public HashMap<String, ClaimRent> claimRent;
    public HashMap<String, ClaimLease> claimLease;
    public HashMap<String, ClaimAuction> claimAuction;
    
    public enum StorageType { FILE, MYSQL, SQLITE }
    private StorageType storageType;
    private Connection dbConnection = null;

    public TransactionsStore() {
        String storageStr = RealEstate.instance.config.databaseType.toLowerCase();
        switch(storageStr) {
            case "mysql":
                storageType = StorageType.MYSQL;
                break;
            case "sqlite":
                storageType = StorageType.SQLITE;
                break;
            case "yml":
                storageType = StorageType.FILE;
                break;
            default:
                RealEstate.instance.log.log(Level.SEVERE, "Invalid storage type in config.yml. Using default file storage!");
                storageType = StorageType.FILE;
                break;
        }
        
        claimSell = new HashMap<>();
        claimRent = new HashMap<>();
        claimLease = new HashMap<>();
        claimAuction = new HashMap<>();
        
        if(storageType == StorageType.FILE) {
            loadDataFromFile();
        } else {
            setupDatabase();
            loadDataFromDatabase();
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<ClaimRent> it1 = claimRent.values().iterator();
                while(it1.hasNext()) {
                    if(it1.next().update())
                        it1.remove();
                }
                Iterator<ClaimLease> it2 = claimLease.values().iterator();
                while(it2.hasNext()) {
                    if(it2.next().update())
                        it2.remove();
                }
                Iterator<ClaimAuction> it3 = claimAuction.values().iterator();
                while(it3.hasNext()) {
                    if(it3.next().update())
                        it3.remove();
                }
                saveData();
            }
        }.runTaskTimer(RealEstate.instance, 1200L, 1200L);
    }
    
    /* FILE-BASED METHODS */
    public void loadDataFromFile() {
        File file = new File(this.dataFilePath);
        if(file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            try {
                RealEstate.instance.addLogEntry(new String(Files.readAllBytes(FileSystems.getDefault().getPath(this.dataFilePath))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ConfigurationSection sell = config.getConfigurationSection("Sell");
            ConfigurationSection rent = config.getConfigurationSection("Rent");
            ConfigurationSection lease = config.getConfigurationSection("Lease");
            ConfigurationSection auction = config.getConfigurationSection("Auction");
            if(sell != null) {
                for(String key : sell.getKeys(false)) {
                    ClaimSell cs = (ClaimSell) sell.get(key);
                    claimSell.put(key, cs);
                }
            }
            if(rent != null) {
                for(String key : rent.getKeys(false)) {
                    ClaimRent cr = (ClaimRent) rent.get(key);
                    claimRent.put(key, cr);
                }
            }
            if(lease != null) {
                for(String key : lease.getKeys(false)) {
                    ClaimLease cl = (ClaimLease) lease.get(key);
                    claimLease.put(key, cl);
                }
            }
            if(auction != null) {
                for(String key : auction.getKeys(false)) {
                    ClaimAuction ca = (ClaimAuction) auction.get(key);
                    claimAuction.put(key, ca);
                }
            }
        }
    }
    
    public void saveDataToFile() {
        YamlConfiguration config = new YamlConfiguration();
        for (ClaimSell cs : claimSell.values())
            config.set("Sell." + cs.getClaim().getId(), cs);
        for (ClaimRent cr : claimRent.values())
            config.set("Rent." + cr.getClaim().getId(), cr);
        for (ClaimLease cl : claimLease.values())
            config.set("Lease." + cl.getClaim().getId(), cl);
        for (ClaimAuction ca : claimAuction.values())
            config.set("Auction." + ca.getClaim().getId(), ca);
        try {
            config.save(new File(this.dataFilePath));
        } catch (IOException e) {
            RealEstate.instance.log.info("Unable to write to the data file at \"" + this.dataFilePath + "\"");
        }
    }
    
    /* DATABASE METHODS using explicit columns */
    private void setupDatabase() {
        try {
            if(storageType == StorageType.MYSQL) {
                String host = RealEstate.instance.config.mysqlHost;
                int port = RealEstate.instance.config.mysqlPort;
                String database = RealEstate.instance.config.mysqlDatabase;
                String username = RealEstate.instance.config.mysqlUsername;
                String password = RealEstate.instance.config.mysqlPassword;
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                		+ "?allowPublicKeyRetrieval=true"
                        + "&autoReconnect=" + RealEstate.instance.config.mysqlAutoReconnect
                        + "&useSSL=" + RealEstate.instance.config.mysqlUseSSL;
                dbConnection = DriverManager.getConnection(url, username, password);
            } else if(storageType == StorageType.SQLITE) {
                String sqliteFile = RealEstate.instance.config.sqliteDatabase;
                String url = "jdbc:sqlite:" + RealEstate.pluginDirPath + sqliteFile;
                dbConnection = DriverManager.getConnection(url);
            }
            
            String prefix = RealEstate.instance.config.mysqlPrefix;
            // ClaimSell table:
            String createClaimSell = "CREATE TABLE IF NOT EXISTS " + prefix + "ClaimSell ("
                    + " claimId VARCHAR(255) NOT NULL PRIMARY KEY, "
                    + " owner CHAR(36) NOT NULL, "
                    + " price DECIMAL(10,2), "
                    + " signWorld VARCHAR(255), "
                    + " signX DOUBLE, "
                    + " signY DOUBLE, "
                    + " signZ DOUBLE, "
                    + " signPitch DOUBLE, "
                    + " signYaw DOUBLE"
                    + ");";
            try(PreparedStatement ps = dbConnection.prepareStatement(createClaimSell)) {
                ps.executeUpdate();
            }
            // ClaimRent table:
            String createClaimRent = "CREATE TABLE IF NOT EXISTS " + prefix + "ClaimRent ("
                    + " claimId VARCHAR(255) NOT NULL PRIMARY KEY, "
                    + " owner CHAR(36) NOT NULL, "
                    + " duration INT, "
                    + " buildTrust BOOLEAN, "
                    + " price DECIMAL(10,2), "
                    + " autoRenew BOOLEAN, "
                    + " startDate DATETIME, "
                    + " buyer CHAR(36), "
                    + " signWorld VARCHAR(255), "
                    + " signX DOUBLE, "
                    + " signY DOUBLE, "
                    + " signZ DOUBLE, "
                    + " signPitch DOUBLE, "
                    + " signYaw DOUBLE"
                    + ");";
            try(PreparedStatement ps = dbConnection.prepareStatement(createClaimRent)) {
                ps.executeUpdate();
            }
            // ClaimLease table:
            String createClaimLease = "CREATE TABLE IF NOT EXISTS " + prefix + "ClaimLease ("
                    + " claimId VARCHAR(255) NOT NULL PRIMARY KEY, "
                    + " owner CHAR(36) NOT NULL, "
                    + " price DECIMAL(10,2), "
                    + " paymentsLeft INT, "
                    + " frequency INT, "
                    + " signWorld VARCHAR(255), "
                    + " signX DOUBLE, "
                    + " signY DOUBLE, "
                    + " signZ DOUBLE, "
                    + " signPitch DOUBLE, "
                    + " signYaw DOUBLE,"
                    + " buyer CHAR(36)"
                    + ");";
            try(PreparedStatement ps = dbConnection.prepareStatement(createClaimLease)) {
                ps.executeUpdate();
            }
            // ClaimAuction table:
            String createClaimAuction = "CREATE TABLE IF NOT EXISTS " + prefix + "ClaimAuction ("
                    + " claimId VARCHAR(255) NOT NULL PRIMARY KEY, "
                    + " owner CHAR(36), "
                    + " bidStep DECIMAL(10,2), "
                    + " endDate DATETIME, "
                    + " price DECIMAL(10,2), "
                    + " signWorld VARCHAR(255), "
                    + " signX DOUBLE, "
                    + " signY DOUBLE, "
                    + " signZ DOUBLE, "
                    + " signPitch DOUBLE, "
                    + " signYaw DOUBLE,"
                    + " buyer CHAR(36)"
                    + ");";
            try(PreparedStatement ps = dbConnection.prepareStatement(createClaimAuction)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadDataFromDatabase() {
        String prefix = RealEstate.instance.config.mysqlPrefix;
        try {
            // --- Load ClaimSell ---
            String querySell = "SELECT * FROM " + prefix + "ClaimSell;";
            try (PreparedStatement ps = dbConnection.prepareStatement(querySell);
                 ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    String claimId = rs.getString("claimId");
                    double price = rs.getDouble("price");
                    String signWorld = rs.getString("signWorld");
                    double signX = rs.getDouble("signX");
                    double signY = rs.getDouble("signY");
                    double signZ = rs.getDouble("signZ");
                    double signPitch = rs.getDouble("signPitch");
                    double signYaw = rs.getDouble("signYaw");
                    World world = Bukkit.getWorld(signWorld);
                    if(world == null) continue;
                    Location sign = new Location(world, signX, signY, signZ, (float) signYaw, (float) signPitch);
                    IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
                    if(claim == null) continue;
                    // Determine owner: if the stored value is "SERVER" or empty, set ownerPlayer to null (i.e. admin claim)
            	    String ownerStr = rs.getString("owner");
            	    Player ownerPlayer = null;
            	    if(ownerStr != null && !ownerStr.isEmpty() && !ownerStr.equalsIgnoreCase("SERVER")){
            	        ownerPlayer = Bukkit.getOfflinePlayer(UUID.fromString(ownerStr)).getPlayer();
            	    }
                    ClaimSell cs = new ClaimSell(claim, ownerPlayer, price, sign);
                    claimSell.put(claimId, cs);
                }
            }
            // --- Load ClaimRent ---
            String queryRent = "SELECT * FROM " + prefix + "ClaimRent;";
            try (PreparedStatement ps = dbConnection.prepareStatement(queryRent);
                 ResultSet rs = ps.executeQuery()) {
            	while(rs.next()){
            	    String claimId = rs.getString("claimId");
            	    int duration = rs.getInt("duration");
            	    boolean buildTrust = rs.getBoolean("buildTrust");
            	    double price = rs.getDouble("price");
            	    boolean autoRenew = rs.getBoolean("autoRenew");
            	    java.sql.Timestamp ts = rs.getTimestamp("startDate");
            	    Date startDateDate = (ts != null) ? new Date(ts.getTime()) : null;
            	    // Convert Date to LocalDateTime using the system default zone
            	    LocalDateTime startDate = (startDateDate != null)
            	            ? LocalDateTime.ofInstant(startDateDate.toInstant(), java.time.ZoneId.systemDefault())
            	            : null;
            	    String signWorld = rs.getString("signWorld");
            	    double signX = rs.getDouble("signX");
            	    double signY = rs.getDouble("signY");
            	    double signZ = rs.getDouble("signZ");
            	    double signPitch = rs.getDouble("signPitch");
            	    double signYaw = rs.getDouble("signYaw");
            	    World world = Bukkit.getWorld(signWorld);
            	    if(world == null) continue;
            	    Location sign = new Location(world, signX, signY, signZ, (float) signYaw, (float) signPitch);
            	    IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
            	    if(claim == null) continue;
            	    
            	    // Determine owner: if the stored value is "SERVER" or empty, set ownerPlayer to null (i.e. admin claim)
            	    String ownerStr = rs.getString("owner");
            	    Player ownerPlayer = null;
            	    if(ownerStr != null && !ownerStr.isEmpty() && !ownerStr.equalsIgnoreCase("SERVER")){
            	        ownerPlayer = Bukkit.getOfflinePlayer(UUID.fromString(ownerStr)).getPlayer();
            	    }
            	    
            	    ClaimRent cr = new ClaimRent(claim, ownerPlayer, price, sign, duration, buildTrust);
            	    cr.autoRenew = autoRenew;
            	    cr.startDate = startDate;
            	    
            	    String buyerStr = rs.getString("buyer");
            	    if(buyerStr != null && !buyerStr.isEmpty()) {
            	        cr.buyer = UUID.fromString(buyerStr);
            	    }
            	    claimRent.put(claimId, cr);
            	}

            }
            // --- Load ClaimLease ---
            String queryLease = "SELECT * FROM " + prefix + "ClaimLease;";
            try (PreparedStatement ps = dbConnection.prepareStatement(queryLease);
                 ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    String claimId = rs.getString("claimId");
                    String ownerStr = rs.getString("owner");
                    double price = rs.getDouble("price");
                    int paymentsLeft = rs.getInt("paymentsLeft");
                    int frequency = rs.getInt("frequency");
                    String signWorld = rs.getString("signWorld");
                    double signX = rs.getDouble("signX");
                    double signY = rs.getDouble("signY");
                    double signZ = rs.getDouble("signZ");
                    double signPitch = rs.getDouble("signPitch");
                    double signYaw = rs.getDouble("signYaw");
                    World world = Bukkit.getWorld(signWorld);
                    if(world == null) continue;
                    Location sign = new Location(world, signX, signY, signZ, (float) signYaw, (float) signPitch);
                    IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
                    if(claim == null) continue;
                    Player ownerPlayer = Bukkit.getOfflinePlayer(UUID.fromString(ownerStr)).getPlayer();
                    ClaimLease cl = new ClaimLease(claim, ownerPlayer, price, sign, frequency, paymentsLeft);
                    String buyerStr = rs.getString("buyer");
                    if(buyerStr != null && !buyerStr.isEmpty()) {
                    	cl.buyer = UUID.fromString(buyerStr);
                    }
                    claimLease.put(claimId, cl);
                }
            }
            // --- Load ClaimAuction ---
            String queryAuction = "SELECT * FROM " + prefix + "ClaimAuction;";
            try (PreparedStatement ps = dbConnection.prepareStatement(queryAuction);
                 ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    String claimId = rs.getString("claimId");
                    String ownerStr = rs.getString("owner");
                    double bidStep = rs.getDouble("bidStep");
                    java.sql.Timestamp ts = rs.getTimestamp("endDate");
                    Date endDateDate = (ts != null) ? new Date(ts.getTime()) : null;
                    String signWorld = rs.getString("signWorld");
                    double signX = rs.getDouble("signX");
                    double signY = rs.getDouble("signY");
                    double signZ = rs.getDouble("signZ");
                    double signPitch = rs.getDouble("signPitch");
                    double signYaw = rs.getDouble("signYaw");
                    double price = rs.getDouble("price");
                    World world = Bukkit.getWorld(signWorld);
                    if(world == null) continue;
                    Location sign = new Location(world, signX, signY, signZ, (float) signYaw, (float) signPitch);
                    IClaim claim = RealEstate.claimAPI.getClaimAt(sign);
                    if(claim == null) continue;
                    Player ownerPlayer = (ownerStr != null && !ownerStr.isEmpty()) ? Bukkit.getOfflinePlayer(UUID.fromString(ownerStr)).getPlayer() : null;
                    // ClaimAuction constructor uses LocalDateTime for endDate.
                    LocalDateTime endDate = (endDateDate != null) ? LocalDateTime.ofInstant(endDateDate.toInstant(), java.time.ZoneId.systemDefault()) : null;
                    ClaimAuction ca = new ClaimAuction(claim, ownerPlayer, price, sign, endDate, bidStep);
                    String buyerStr = rs.getString("buyer");
                    if(buyerStr != null && !buyerStr.isEmpty()) {
                    	ca.buyer = UUID.fromString(buyerStr);
                    }
                    claimAuction.put(claimId, ca);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void saveDataToDatabase() {
        String prefix = RealEstate.instance.config.mysqlPrefix;
        try {
            // --- Save ClaimSell ---
            String deleteSell = "DELETE FROM " + prefix + "ClaimSell;";
            try (PreparedStatement ps = dbConnection.prepareStatement(deleteSell)) {
                ps.executeUpdate();
            }
            String insertSell = "INSERT INTO " + prefix + "ClaimSell (claimId, owner, price, signWorld, signX, signY, signZ, signPitch, signYaw) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement ps = dbConnection.prepareStatement(insertSell)) {
                for (ClaimSell cs : claimSell.values()) {
                    ps.setString(1, cs.getClaim().getId());
                    ps.setString(2, cs.getClaim().isAdminClaim() ? "SERVER" : (cs.getOwnerUUID() != null ? cs.getOwnerUUID().toString() : ""));
                    ps.setDouble(3, cs.getPrice());
                    ps.setString(4, cs.getSign().getWorld().getName());
                    ps.setDouble(5, cs.getSign().getX());
                    ps.setDouble(6, cs.getSign().getY());
                    ps.setDouble(7, cs.getSign().getZ());
                    ps.setDouble(8, cs.getSign().getPitch());
                    ps.setDouble(9, cs.getSign().getYaw());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            // --- Save ClaimRent ---
            String deleteRent = "DELETE FROM " + prefix + "ClaimRent;";
            try (PreparedStatement ps = dbConnection.prepareStatement(deleteRent)) {
                ps.executeUpdate();
            }
            String insertRent = "INSERT INTO " + prefix + "ClaimRent (claimId, owner, duration, buildTrust, price, autoRenew, startDate, buyer, signWorld, signX, signY, signZ, signPitch, signYaw) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement ps = dbConnection.prepareStatement(insertRent)) {
                for (ClaimRent cr : claimRent.values()) {
                    ps.setString(1, cr.getClaim().getId());
                    // If the claim is an admin claim, set owner to "SERVER"
                    ps.setString(2, cr.getClaim().isAdminClaim() ? "SERVER" : (cr.getOwnerUUID() != null ? cr.getOwnerUUID().toString() : ""));
                    ps.setInt(3, cr.duration);
                    ps.setBoolean(4, cr.buildTrust);
                    ps.setDouble(5, cr.getPrice());
                    ps.setBoolean(6, cr.autoRenew);
                    if(cr.startDate != null)
                        ps.setTimestamp(7, new java.sql.Timestamp(java.sql.Timestamp.valueOf(cr.startDate).getTime()));
                    else
                        ps.setTimestamp(7, null);
                    ps.setString(8, cr.buyer != null ? cr.buyer.toString() : null);
                    ps.setString(9, cr.getSign().getWorld().getName());
                    ps.setDouble(10, cr.getSign().getX());
                    ps.setDouble(11, cr.getSign().getY());
                    ps.setDouble(12, cr.getSign().getZ());
                    ps.setDouble(13, cr.getSign().getPitch());
                    ps.setDouble(14, cr.getSign().getYaw());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            // --- Save ClaimLease ---
            String deleteLease = "DELETE FROM " + prefix + "ClaimLease;";
            try (PreparedStatement ps = dbConnection.prepareStatement(deleteLease)) {
                ps.executeUpdate();
            }
            String insertLease = "INSERT INTO " + prefix + "ClaimLease (claimId, owner, price, paymentsLeft, frequency, signWorld, signX, signY, signZ, signPitch, signYaw) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement ps = dbConnection.prepareStatement(insertLease)) {
                for (ClaimLease cl : claimLease.values()) {
                    ps.setString(1, cl.getClaim().getId());
                    ps.setString(2, cl.getClaim().isAdminClaim() ? "SERVER" : (cl.getOwnerUUID() != null ? cl.getOwnerUUID().toString() : ""));
                    ps.setDouble(3, cl.getPrice());
                    ps.setInt(4, cl.getPaymentsLeft());
                    ps.setInt(5, cl.getFrequency());
                    ps.setString(6, cl.getSign().getWorld().getName());
                    ps.setDouble(7, cl.getSign().getX());
                    ps.setDouble(8, cl.getSign().getY());
                    ps.setDouble(9, cl.getSign().getZ());
                    ps.setDouble(10, cl.getSign().getPitch());
                    ps.setDouble(11, cl.getSign().getYaw());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            // --- Save ClaimAuction ---
            String deleteAuction = "DELETE FROM " + prefix + "ClaimAuction;";
            try (PreparedStatement ps = dbConnection.prepareStatement(deleteAuction)) {
                ps.executeUpdate();
            }
            String insertAuction = "INSERT INTO " + prefix + "ClaimAuction (claimId, owner, bidStep, endDate, price, signWorld, signX, signY, signZ, signPitch, signYaw) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement ps = dbConnection.prepareStatement(insertAuction)) {
                for (ClaimAuction ca : claimAuction.values()) {
                    ps.setString(1, ca.getClaim().getId());
                    ps.setString(2, ca.getClaim().isAdminClaim() ? "SERVER" : (ca.getOwnerUUID() != null ? ca.getOwnerUUID().toString() : ""));
                    ps.setDouble(3, ca.getBidStep());
                    if(ca.getEndDate() != null)
                        ps.setTimestamp(4, new java.sql.Timestamp(java.sql.Timestamp.valueOf(ca.getEndDate()).getTime()));
                    else
                        ps.setTimestamp(4, null);
                    ps.setDouble(5, ca.getPrice());
                    ps.setString(6, ca.getSign().getWorld().getName());
                    ps.setDouble(7, ca.getSign().getX());
                    ps.setDouble(8, ca.getSign().getY());
                    ps.setDouble(9, ca.getSign().getZ());
                    ps.setDouble(10, ca.getSign().getPitch());
                    ps.setDouble(11, ca.getSign().getYaw());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void saveData() {
        if(storageType == StorageType.FILE)
            saveDataToFile();
        else
            saveDataToDatabase();
    }
    
    public boolean anyTransaction(IClaim claim) {
        return claim != null &&
               !claim.isWilderness() &&
               (claimSell.containsKey(claim.getId()) ||
                claimRent.containsKey(claim.getId()) ||
                claimLease.containsKey(claim.getId()) ||
                claimAuction.containsKey(claim.getId()));
    }
    
    public Transaction getTransaction(IClaim claim) {
        if(claimSell.containsKey(claim.getId()))
            return claimSell.get(claim.getId());
        if(claimRent.containsKey(claim.getId()))
            return claimRent.get(claim.getId());
        if(claimLease.containsKey(claim.getId()))
            return claimLease.get(claim.getId());
        if(claimAuction.containsKey(claim.getId()))
            return claimAuction.get(claim.getId());
        return null;
    }
    
    public void cancelTransaction(IClaim claim) {
        if(anyTransaction(claim)) {
            Transaction tr = getTransaction(claim);
            cancelTransaction(tr);
        }
        saveData();
    }
    
    public void cancelTransaction(Transaction tr) {
        if(tr.getHolder() != null)
            tr.getHolder().breakNaturally();
        if(tr instanceof ClaimSell)
            claimSell.remove(((ClaimSell) tr).getClaim().getId());
        if(tr instanceof ClaimRent)
            claimRent.remove(((ClaimRent) tr).getClaim().getId());
        if(tr instanceof ClaimLease)
            claimLease.remove(((ClaimLease) tr).getClaim().getId());
        if(tr instanceof ClaimAuction)
            claimAuction.remove(((ClaimAuction) tr).getClaim().getId());
        saveData();
    }
    
    public boolean canCancelTransaction(Transaction tr) {
        // For auction, rent, lease we check if buyer is null (or if cancellation is forced)
        return tr instanceof ClaimSell ||
               (tr instanceof ClaimAuction && (((ClaimAuction)tr).getBuyer() == null || RealEstate.instance.config.cfgCancelAuction)) ||
               (tr instanceof ClaimRent && ((ClaimRent)tr).getBuyer() == null) ||
               (tr instanceof ClaimLease && ((ClaimLease)tr).getBuyer() == null);
    }
    
    // Transaction creation methods:
    public void sell(IClaim claim, Player player, double price, Location sign) {
        ClaimSell cs = new ClaimSell(claim, claim.isAdminClaim() ? null : player, price, sign);
        claimSell.put(claim.getId(), cs);
        // Delay the update by one tick so that the sign's state is ready
        Bukkit.getScheduler().runTaskLater(RealEstate.instance, () -> cs.update(), 1L);
        saveData();
        RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " +
            (player == null ? "The Server" : player.getName()) +
            " has made " + (claim.isAdminClaim() ? "an admin" : "a") +
            " " + (claim.isParentClaim() ? "claim" : "subclaim") + " for sale at " +
            "[" + claim.getWorld() + ", X: " + claim.getX() + ", Y: " + claim.getY() + ", Z: " + claim.getZ() + "] " +
            "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
        String claimPrefix = claim.isAdminClaim() ? RealEstate.instance.messages.keywordAdminClaimPrefix :
                              RealEstate.instance.messages.keywordClaimPrefix;
        String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim :
                                  RealEstate.instance.messages.keywordSubclaim;
        if (player != null) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimCreatedSell,
                claimPrefix, claimTypeDisplay, RealEstate.econ.format(price));
        }
        if (RealEstate.instance.config.cfgBroadcastSell) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (p != player) {
                    Messages.sendMessage(p, RealEstate.instance.messages.msgInfoClaimCreatedSellBroadcast,
                        player == null ? RealEstate.instance.messages.keywordTheServer : player.getDisplayName(),
                        claimPrefix, claimTypeDisplay, RealEstate.econ.format(price));
                }
            }
        }
    }


    
    public void rent(IClaim claim, Player player, double price, Location sign, int duration, boolean buildTrust) {
        ClaimRent cr = new ClaimRent(claim, claim.isAdminClaim() ? null : player, price, sign, duration, buildTrust);
        claimRent.put(claim.getId(), cr);
        // Immediately update the sign (using a slight delay if needed)
        Bukkit.getScheduler().runTaskLater(RealEstate.instance, () -> cr.update(), 1L);
        saveData();
        RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " +
            (player == null ? "The Server" : player.getName()) +
            " has made " + (claim.isAdminClaim() ? "an admin" : "a") +
            " " + (claim.isParentClaim() ? "claim" : "subclaim") + " for rent at " +
            "[" + claim.getWorld() + ", X: " + claim.getX() + ", Y: " + claim.getY() + ", Z: " + claim.getZ() + "] " +
            "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
        String claimPrefix = claim.isAdminClaim() ? RealEstate.instance.messages.keywordAdminClaimPrefix :
                              RealEstate.instance.messages.keywordClaimPrefix;
        String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim :
                                  RealEstate.instance.messages.keywordSubclaim;
        if(player != null) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimCreatedRent,
                claimPrefix, claimTypeDisplay, RealEstate.econ.format(price),
                Utils.getTime(duration, null, false));
        }
        if(RealEstate.instance.config.cfgBroadcastSell) {
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p != player) {
                    Messages.sendMessage(p, RealEstate.instance.messages.msgInfoClaimCreatedRentBroadcast,
                        player == null ? RealEstate.instance.messages.keywordTheServer : player.getDisplayName(),
                        claimPrefix, claimTypeDisplay, RealEstate.econ.format(price),
                        Utils.getTime(duration, null, false));
                }
            }
        }
    }
    
    public void lease(IClaim claim, Player player, double price, Location sign, int frequency, int paymentsCount) {
        ClaimLease cl = new ClaimLease(claim, claim.isAdminClaim() ? null : player, price, sign, frequency, paymentsCount);
        claimLease.put(claim.getId(), cl);
        // Immediately update the sign (using a slight delay if needed)
        Bukkit.getScheduler().runTaskLater(RealEstate.instance, () -> cl.update(), 1L);
        saveData();
        RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " +
            (player == null ? "The Server" : player.getName()) +
            " has made " + (claim.isAdminClaim() ? "an admin" : "a") +
            " " + (claim.isParentClaim() ? "claim" : "subclaim") + " for lease at " +
            "[" + claim.getWorld() + ", X: " + claim.getX() + ", Y: " + claim.getY() + ", Z: " + claim.getZ() + "] " +
            "Payments Count: " + paymentsCount + " Price: " + price + " " + RealEstate.econ.currencyNamePlural());
        String claimPrefix = claim.isAdminClaim() ? RealEstate.instance.messages.keywordAdminClaimPrefix :
                              RealEstate.instance.messages.keywordClaimPrefix;
        String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim :
                                  RealEstate.instance.messages.keywordSubclaim;
        if(player != null) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimCreatedLease,
                claimPrefix, claimTypeDisplay, RealEstate.econ.format(price),
                String.valueOf(paymentsCount), Utils.getTime(frequency, null, false));
        }
        if(RealEstate.instance.config.cfgBroadcastSell) {
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p != player) {
                    Messages.sendMessage(p, RealEstate.instance.messages.msgInfoClaimCreatedLeaseBroadcast,
                        player == null ? RealEstate.instance.messages.keywordTheServer : player.getDisplayName(),
                        claimPrefix, claimTypeDisplay, RealEstate.econ.format(price),
                        String.valueOf(paymentsCount), Utils.getTime(frequency, null, false));
                }
            }
        }
    }
    
    public void auction(IClaim claim, Player player, double price, Location sign, int duration, double bidStep) {
        LocalDateTime endDate = LocalDateTime.now().plusDays(duration);
        // Use the Auction constructor with a LocalDateTime endDate.
        ClaimAuction ca = new ClaimAuction(claim, claim.isAdminClaim() ? null : player, price, sign, endDate, bidStep);
        claimAuction.put(claim.getId(), ca);
        // Immediately update the sign (using a slight delay if needed)
        Bukkit.getScheduler().runTaskLater(RealEstate.instance, () -> ca.update(), 1L);
        saveData();
        RealEstate.instance.addLogEntry("[" + this.dateFormat.format(this.date) + "] " +
            (player == null ? "The Server" : player.getName()) +
            " has made " + (claim.isAdminClaim() ? "an admin" : "a") +
            " " + (claim.isParentClaim() ? "claim" : "subclaim") + " for auction at " +
            "[" + claim.getWorld() + ", X: " + claim.getX() + ", Y: " + claim.getY() + ", Z: " + claim.getZ() + "] " +
            "Price: " + price + " " + RealEstate.econ.currencyNamePlural() +
            " Bid Step: " + bidStep + " " + RealEstate.econ.currencyNamePlural() +
            " End Date: " + endDate.toString());
        String claimPrefix = claim.isAdminClaim() ? RealEstate.instance.messages.keywordAdminClaimPrefix :
                              RealEstate.instance.messages.keywordClaimPrefix;
        String claimTypeDisplay = claim.isParentClaim() ? RealEstate.instance.messages.keywordClaim :
                                  RealEstate.instance.messages.keywordSubclaim;
        if(player != null) {
            Messages.sendMessage(player, RealEstate.instance.messages.msgInfoClaimCreatedAuction,
                claimPrefix, claimTypeDisplay, RealEstate.econ.format(price),
                RealEstate.econ.format(bidStep), Utils.getTime(duration, null, false));
        }
        if(RealEstate.instance.config.cfgBroadcastSell) {
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p != player) {
                    Messages.sendMessage(p, RealEstate.instance.messages.msgInfoClaimCreatedAuctionBroadcast,
                        player == null ? RealEstate.instance.messages.keywordTheServer : player.getDisplayName(),
                        claimPrefix, claimTypeDisplay, RealEstate.econ.format(price),
                        RealEstate.econ.format(bidStep), Utils.getTime(duration, null, false));
                }
            }
        }
    }
    
    public Transaction getTransaction(Player player) {
        if(player == null) return null;
        IClaim c = RealEstate.claimAPI.getClaimAt(player.getLocation());
        return getTransaction(c);
    }
}
