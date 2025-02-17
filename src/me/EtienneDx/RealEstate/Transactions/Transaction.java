package me.EtienneDx.RealEstate.Transactions;

import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a transaction for a real estate claim.
 * <p>
 * Transactions may include operations such as selling, renting, leasing, or auctioning claims.
 * This interface defines the core methods that any transaction type must implement.
 * </p>
 */
public interface Transaction {

    /**
     * Returns the block that acts as the physical holder (e.g., a sign) for this transaction.
     *
     * @return the Block representing the transaction holder, or {@code null} if none exists
     */
    public Block getHolder();

    /**
     * Retrieves the UUID of the owner associated with this transaction.
     *
     * @return the owner's UUID
     */
    public UUID getOwner();

    /**
     * Sets the owner of this transaction to a new UUID.
     *
     * @param newOwner the new owner's UUID
     */
    public void setOwner(UUID newOwner);

    /**
     * Processes a player's interaction with the transaction.
     * <p>
     * This method is called when a player interacts with the transaction's associated block or sign.
     * </p>
     *
     * @param player the player interacting with the transaction
     */
    public void interact(Player player);

    /**
     * Provides a detailed preview of the transaction information to a player.
     * <p>
     * Typically, this displays details such as price, claim location, and other relevant transaction data.
     * </p>
     *
     * @param player the player for whom the preview is generated
     */
    public void preview(Player player);

    /**
     * Updates the current state of the transaction.
     * <p>
     * This method is usually called periodically to update transaction details (e.g., sign text) or to finalize the transaction.
     * </p>
     *
     * @return {@code true} if the transaction has ended or been finalized; {@code false} otherwise
     */
    public boolean update();

    /**
     * Attempts to cancel the transaction.
     * <p>
     * This method checks if the transaction can be cancelled under current conditions.
     * </p>
     *
     * @param p the player requesting cancellation
     * @return {@code true} if the transaction was successfully cancelled; {@code false} otherwise
     */
    public boolean tryCancelTransaction(Player p);

    /**
     * Attempts to cancel the transaction with an option to force the cancellation.
     * <p>
     * When {@code force} is {@code true}, the cancellation is performed regardless of normal conditions.
     * </p>
     *
     * @param p the player requesting cancellation
     * @param force if {@code true}, forces the cancellation of the transaction
     * @return {@code true} if the transaction was successfully cancelled; {@code false} otherwise
     */
    public boolean tryCancelTransaction(Player p, boolean force);

    /**
     * Sends transaction information to the specified command sender.
     * <p>
     * This method is typically used to provide a summary of the transaction via command output.
     * </p>
     *
     * @param cs the CommandSender (player or console) to receive the transaction info
     */
    public void msgInfo(CommandSender cs);
}
