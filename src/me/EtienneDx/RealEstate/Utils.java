package me.EtienneDx.RealEstate;

import java.time.Duration;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.EtienneDx.RealEstate.ClaimAPI.ClaimPermission;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import me.EtienneDx.RealEstate.ClaimAPI.IPlayerData;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * A utility class containing helper methods for the RealEstate plugin.
 * <p>
 * This class provides methods for processing economy transactions,
 * formatting time durations, transferring claim blocks between players,
 * and truncating strings for sign display.
 * </p>
 */
public class Utils
{
	/**
	 * Instantiates a new Utils object.
	 */
	public Utils() {}
	
    /**
     * Processes a payment transaction between two players.
     * <p>
     * The method attempts to withdraw a specified amount from the giver and deposit it to the receiver.
     * If the giver does not have sufficient funds or if any transaction fails,
     * an error message is sent (if enabled) and the transaction is aborted.
     * </p>
     *
     * @param receiver   the UUID of the player who will receive money (can be null for the server)
     * @param giver      the UUID of the player from whom money is to be withdrawn
     * @param amount     the amount of money to transfer
     * @param msgReceiver if {@code true} and the receiver is online, a message will be sent
     * @param msgGiver    if {@code true} and the giver is online, a message will be sent
     * @return {@code true} if the payment was successful; {@code false} otherwise.
     */
    public static boolean makePayment(UUID receiver, UUID giver, double amount, boolean msgReceiver, boolean msgGiver)
    {
        // seller might be null if it is the server
        OfflinePlayer giveTo = receiver != null ? Bukkit.getOfflinePlayer(receiver) : null;
        OfflinePlayer takeFrom = giver != null ? Bukkit.getOfflinePlayer(giver) : null;
        if(takeFrom != null && !RealEstate.econ.has(takeFrom, amount))
        {
            if(takeFrom.isOnline() && msgGiver)
            {
                Messages.sendMessage(takeFrom.getPlayer(), RealEstate.instance.messages.msgErrorNoMoneySelf);
            }
            if(giveTo != null && giveTo.isOnline() && msgReceiver)
            {
                Messages.sendMessage(giveTo.getPlayer(), RealEstate.instance.messages.msgErrorNoMoneyOther, takeFrom.getName());
            }
            return false;
        }
        if(takeFrom != null)
        {
            EconomyResponse resp = RealEstate.econ.withdrawPlayer(takeFrom, amount);
            if(!resp.transactionSuccess())
            {
                if(takeFrom.isOnline() && msgGiver)
                {
                    Messages.sendMessage(takeFrom.getPlayer(), RealEstate.instance.messages.msgErrorNoWithdrawSelf);
                }
                if(giveTo != null && giveTo.isOnline() && msgReceiver)
                {
                    Messages.sendMessage(giveTo.getPlayer(), RealEstate.instance.messages.msgErrorNoWithdrawOther);
                }
                return false;
            }
        }
        if(giveTo != null)
        {
            EconomyResponse resp = RealEstate.econ.depositPlayer(giveTo, amount);
            if(!resp.transactionSuccess())
            {
                if(takeFrom != null && takeFrom.isOnline() && msgGiver)
                {
                    Messages.sendMessage(giveTo.getPlayer(), RealEstate.instance.messages.msgErrorNoDepositOther, giveTo.getName());
                }
                if(takeFrom != null && giveTo != null && giveTo.isOnline() && msgReceiver)
                {
                    Messages.sendMessage(takeFrom.getPlayer(), RealEstate.instance.messages.msgErrorNoDepositSelf, takeFrom.getName());
                }
                // refund
                RealEstate.econ.depositPlayer(takeFrom, amount);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Formats a time duration as a human-readable string.
     * <p>
     * The method converts the given number of days and a Duration representing hours/minutes
     * into a string such as "1 week 3 days 5 hours 30 mins".
     * </p>
     *
     * @param days    the number of days
     * @param hours   a Duration representing additional hours and minutes; may be {@code null}
     * @param details if {@code true}, include hours and minutes even when days are present
     * @return a formatted string representing the total time.
     */
    public static String getTime(int days, Duration hours, boolean details)
    {
        String time = "";
        if(days >= 7)
        {
            time += (days / 7) + " week" + (days >= 14 ? "s" : "");
        }
        if(days % 7 > 0)
        {
            time += (time.isEmpty() ? "" : " ") + (days % 7) + " day" + (days % 7 > 1 ? "s" : "");
        }
        if((details || days < 7) && hours != null && hours.toHours() > 0)
        {
            time += (time.isEmpty() ? "" : " ") + hours.toHours() + " hour" + (hours.toHours() > 1 ? "s" : "");
        }
        if((details || days == 0) && hours != null && (time.isEmpty() || hours.toMinutes() % 60 > 0))
        {
            time += (time.isEmpty() ? "" : " ") + (hours.toMinutes() % 60) + " min" + (hours.toMinutes() % 60 > 1 ? "s" : "");
        }
        
        return time;
    }
    
    /**
     * Transfers a claim from one owner to another.
     * <p>
     * This method handles the transfer of claim blocks between the seller and buyer if claim blocks are
     * being transferred, and then updates the claim's ownership using the claim API.
     * </p>
     *
     * @param claim  the claim to be transferred
     * @param buyer  the UUID of the new owner
     * @param seller the UUID of the current owner (may be {@code null} for admin claims)
     */
    public static void transferClaim(IClaim claim, UUID buyer, UUID seller)
    {
        // blocks transfer :
        // if transfert is true, the seller will lose the blocks he had
        // and the buyer will get them
        // (that means the buyer will keep the same amount of remaining blocks after the transaction)
        if(claim.isParentClaim() && RealEstate.instance.config.cfgTransferClaimBlocks)
        {
            IPlayerData buyerData = RealEstate.claimAPI.getPlayerData(buyer);
            if(seller != null)
            {
                IPlayerData sellerData = RealEstate.claimAPI.getPlayerData(seller);
                
                // the seller has to provide the blocks
                sellerData.setBonusClaimBlocks(sellerData.getBonusClaimBlocks() - claim.getArea());
                if (sellerData.getBonusClaimBlocks() < 0)// can't have negative bonus claim blocks, so if need be, we take into the accrued 
                {
                    sellerData.setAccruedClaimBlocks(sellerData.getAccruedClaimBlocks() + sellerData.getBonusClaimBlocks());
                    sellerData.setBonusClaimBlocks(0);
                }
            }
            
            // the buyer receives them
            buyerData.setBonusClaimBlocks(buyerData.getBonusClaimBlocks() + claim.getArea());
        }
        
        // start to change owner
        if(claim.isParentClaim())
        {
            for(IClaim child : claim.getChildren())
            {
                child.clearPlayerPermissions();
                child.clearManagers();
            }
        }
        claim.clearPlayerPermissions();
        
        try
        {
            if(claim.isParentClaim())
                RealEstate.claimAPI.changeClaimOwner(claim, buyer);
            else
            {
                claim.addPlayerPermissions(buyer, ClaimPermission.BUILD);
            }
        }
        catch (Exception e)// error occurs when trying to change subclaim owner
        {
            e.printStackTrace();
            return;
        }
        RealEstate.claimAPI.saveClaim(claim);
    }
    
    /**
     * Truncates a given string to a maximum length of 16 characters.
     * <p>
     * This is used for ensuring that sign text does not exceed Minecraft's character limit.
     * </p>
     *
     * @param str the input string
     * @return the truncated string if its length exceeds 16 characters; otherwise, the original string.
     */
    public static String getSignString(String str)
    {
        if(str.length() > 16)
            str = str.substring(0, 16);
        return str;
    }
}
