package me.EtienneDx.RealEstate;

import java.util.regex.Matcher;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import me.EtienneDx.AnnotationConfig.AnnotationConfig;
import me.EtienneDx.AnnotationConfig.ConfigField;
import me.EtienneDx.AnnotationConfig.ConfigFile;
import net.md_5.bungee.api.ChatColor;

/**
 * The Messages class holds all configurable message templates for the RealEstate plugin.
 * <p>
 * These messages use placeholders (e.g. {0}, {1}) that are replaced at runtime and
 * support Minecraft color codes via dollar signs ($). The messages are loaded from a YAML
 * configuration file, which can be edited with a YAML editor.
 * </p>
 */
@ConfigFile(header = "Use a YAML editor like NotepadPlusPlus to edit this file.\nAfter editing, back up your changes before reloading the server in case you made a syntax error.\nUse dollar signs ($) for formatting codes, which are documented here: http://minecraft.gamepedia.com/Formatting_codes.\nYou can use {0}, {1} to include the different values indicated in the comments")
public class Messages extends AnnotationConfig {

    /**
     * The plugin description file.
     */
    public PluginDescriptionFile pdf;
    
    /**
     * Constructs a new Messages instance and initializes the plugin description file.
     */
    public Messages() {
        this.pdf = RealEstate.instance.getDescription();
    }

    //=====================================================================
    // Configurable Keywords and Messages
    //=====================================================================

    /**
     * Keyword indicating that a feature is enabled.
     */
    @ConfigField(name = "RealEstate.Keywords.Enabled", comment = "Keywords used within other messages but with a longer text at the end just because i need to test some stuff")
    public String keywordEnabled = "enabled";

    /**
     * Keyword indicating that a feature is disabled.
     */
    @ConfigField(name = "RealEstate.Keywords.Disabled")
    public String keywordDisabled = "disabled";

    /**
     * Keyword for a claim.
     */
    @ConfigField(name = "RealEstate.Keywords.Claim")
    public String keywordClaim = "claim";

    /**
     * Keyword for a subclaim.
     */
    @ConfigField(name = "RealEstate.Keywords.Subclaim")
    public String keywordSubclaim = "subclaim";

    /**
     * Prefix used for admin claims.
     */
    @ConfigField(name = "RealEstate.Keywords.AdminClaimPrefix")
    public String keywordAdminClaimPrefix = "an admin";

    /**
     * Prefix used for claims.
     */
    @ConfigField(name = "RealEstate.Keywords.ClaimPrefix")
    public String keywordClaimPrefix = "a";

    /**
     * Keyword representing the server.
     */
    @ConfigField(name = "RealEstate.Keywords.TheServer")
    public String keywordTheServer = "The server";

    /**
     * Message displayed when no transaction is found.
     */
    @ConfigField(name = "RealEstate.NoTransactionFound")
    public String msgNoTransactionFound = "$cNo transaction found!";

    /**
     * Message displayed when no transaction is found at the player's location.
     */
    @ConfigField(name = "RealEstate.NoTransactionFoundHere")
    public String msgNoTransactionFoundHere = "$cNo transaction found at your location!";

    /**
     * Message indicating that the page number must be a positive option.
     */
    @ConfigField(name = "RealEstate.PageMustBePositive")
    public String msgPageMustBePositive = "$cPage must be a positive option";

    /**
     * Message displayed when the requested page does not exist.
     */
    @ConfigField(name = "RealEstate.PageNotExists")
    public String msgPageNotExists = "$cThis page does not exist!";

    /**
     * Message indicating that automatic rent renewal is now active or inactive.
     * <p>
     * Placeholders: {0} - enabled/disabled; {1} - type of claim.
     * </p>
     */
    @ConfigField(name = "RealEstate.RenewRentNow", comment = "0: enabled/disabled; 1: type of claim")
    public String msgRenewRentNow = "$bAutomatic renew is now $a{0} $bfor this {1}";

    /**
     * Message indicating the current status of automatic rent renewal.
     * <p>
     * Placeholders: {0} - enabled/disabled; {1} - type of claim.
     * </p>
     */
    @ConfigField(name = "RealEstate.RenewRentCurrently", comment = "0: enabled/disabled; 1: type of claim")
    public String msgRenewRentCurrently = "$bAutomatic renew is currently $a{0} $bfor this {1}";

    /**
     * Error message when a player is not standing within a claim.
     */
    @ConfigField(name = "RealEstate.Errors.OutOfClaim")
    public String msgErrorOutOfClaim = "$cYou must stand inside of a claim to use this command!";

    /**
     * Error message when the command is only for players.
     */
    @ConfigField(name = "RealEstate.Errors.PlayerOnlyCmd")
    public String msgErrorPlayerOnly = "$cOnly Players can perform this command!";

    /**
     * Error message when there is no ongoing transaction for a claim.
     */
    @ConfigField(name = "RealEstate.Errors.NoOngoingTransaction")
    public String msgErrorNoOngoingTransaction = "$cThis claim has no ongoing transactions!";

    /**
     * Error message when a claim is neither available for rent nor lease.
     */
    @ConfigField(name = "RealEstate.Errors.NotRentNorLease")
    public String msgErrorNotRentNorLease = "$cThis claim is neither to rent or to lease!";

    /**
     * Error message when a claim already has a buyer.
     */
    @ConfigField(name = "RealEstate.Errors.AlreadyBought")
    public String msgErrorAlreadyBought = "$cThis claim already has a buyer!";

    /**
     * Error message when the player is not part of the current transaction.
     */
    @ConfigField(name = "RealEstate.Errors.NotPartOfTransaction")
    public String msgErrorNotPartOfTransaction = "$cYou are not part of this transaction!";

    /**
     * Error message when the command only applies to rented claims.
     */
    @ConfigField(name = "RealEstate.Errors.RentOnly")
    public String msgErrorRentOnly = "$cThis command only applies to rented claims!";

    /**
     * Error message when the command only applies to auctioned claims.
     */
    @ConfigField(name = "RealEstate.Errors.AuctionOnly")
    public String msgErrorAuctionOnly = "$cThis command only applies to auctioned claims!";

    /**
     * Error message when a numeric value is not greater than zero.
     */
    @ConfigField(name = "RealEstate.Errors.ValueGreaterThanZero")
    public String msgErrorValueGreaterThanZero = "$cThe value must be greater than zero!";

    /**
     * Error message when an invalid option is provided.
     */
    @ConfigField(name = "RealEstate.Errors.InvalidOption")
    public String msgErrorInvalidOption = "$cInvalid option provided!";

    /**
     * Error message when the claim is involved in a transaction and cannot be modified.
     */
    @ConfigField(name = "RealEstate.Errors.ClaimInTransaction.CantOwner")
    public String msgErrorClaimInTransactionCantOwner = "$cThis claim is currently involved in a transaction, you can't modify it!";

    /**
     * Error message when editing is not permitted due to an ongoing transaction.
     */
    @ConfigField(name = "RealEstate.Errors.ClaimInTransaction.CantEdit")
    public String msgErrorClaimInTransactionCantEdit = "$cThis claim is currently involved in a transaction, you can't edit it!";

    /**
     * Error message when access is not permitted due to an ongoing transaction.
     */
    @ConfigField(name = "RealEstate.Errors.ClaimInTransaction.CantAccess")
    public String msgErrorClaimInTransactionCantAccess = "$cThis claim is currently involved in a transaction, you can't access it!";

    /**
     * Error message when building is not permitted on a claim in transaction.
     */
    @ConfigField(name = "RealEstate.Errors.ClaimInTransaction.CantBuild")
    public String msgErrorClaimInTransactionCantBuild = "$cThis claim is currently involved in a transaction, you can't build on it!";

    /**
     * Error message when container access is not allowed due to a transaction.
     */
    @ConfigField(name = "RealEstate.Errors.ClaimInTransaction.CantInventory")
    public String msgErrorClaimInTransactionCantInventory = "$cThis claim is currently involved in a transaction, you can't access its containers!";

    /**
     * Error message when management actions are blocked by a transaction.
     */
    @ConfigField(name = "RealEstate.Errors.ClaimInTransaction.CantManage")
    public String msgErrorClaimInTransactionCantManage = "$cThis claim is currently involved in a transaction, you can't manage it!";

    /**
     * Error message when a subclaim is involved in a transaction.
     */
    @ConfigField(name = "RealEstate.Errors.ClaimInTransaction.Subclaim")
    public String msgErrorSubclaimInTransaction = "$cA subclaim is currently involved in a transaction, you can't edit or manage the parent claim!";

    /**
     * Error message for invalid command usage.
     * <p>
     * Placeholder: {0} - correct command usage.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Command.Usage", comment = "0: command usage")
    public String msgErrorCommandUsage = "$cUsage: {0}";

    /**
     * Error message when a command is restricted to the buyer only.
     */
    @ConfigField(name = "RealEstate.Errors.BuyerOnly")
    public String msgErrorBuyerOnly = "$cOnly the buyer can perform this command!";

    /**
     * Error message for unexpected errors.
     */
    @ConfigField(name = "RealEstate.Errors.Unexpected")
    public String msgErrorUnexpected = "$cAn unexpected error has occured!";

    /**
     * Error message for an invalid number.
     * <p>
     * Placeholder: {0} - the invalid number.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.InvalidNumber", comment = "0: number")
    public String msgErrorInvalidNumber = "$c{0} is not a valid number!";

    /**
     * Error message for a negative number.
     * <p>
     * Placeholder: {0} - the negative number.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.NegativeNumber", comment = "0: number")
    public String msgErrorNegativeNumber = "$c{0} is a negative number!";

    /**
     * Error message for a negative price.
     * <p>
     * Placeholder: {0} - the price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.NegativePrice", comment = "0: price")
    public String msgErrorNegativePrice = "$cThe price must be greater than zero!";

    /**
     * Error message for a negative bid step.
     * <p>
     * Placeholder: {0} - the bid step.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.NegativeBidStep", comment = "0: bid step")
    public String msgErrorNegativeBidStep = "$cThe bid step must be greater than zero!";

    /**
     * Error message when the price is not an integer.
     * <p>
     * Placeholder: {0} - the price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.NonIntegerPrice", comment = "0: price")
    public String msgErrorNonIntegerPrice = "$cThe price must be an integer!";

    /**
     * Error message for an invalid duration.
     * <p>
     * Placeholders: {0} - the invalid duration, {1}, {2}, {3} - examples of valid formats.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.InvalidDuration", comment = "0: duration, 1: example of duration format, 2: example, 3: example")
    public String msgErrorInvalidDuration = "$c{0} is not a valid duration! Durations must be in the format $a{1}$c or $a{2}$c or $a{3}$c!";

    /**
     * Error message when the player doesn't have enough money.
     */
    @ConfigField(name = "RealEstate.Errors.NoMoneySelf")
    public String msgErrorNoMoneySelf = "$cYou don't have enough money to make this transaction!";

    /**
     * Error message when another player doesn't have enough money.
     * <p>
     * Placeholder: {0} - other player's name.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.NoMoneyOther", comment = "0: Other player")
    public String msgErrorNoMoneyOther = "$c{0} doesn't have enough money to make this transaction!";

    /**
     * Error message when money cannot be withdrawn from the player.
     */
    @ConfigField(name = "RealEstate.Errors.NoWithdrawSelf")
    public String msgErrorNoWithdrawSelf = "$cCould not withdraw the money!";

    /**
     * Error message when money cannot be withdrawn from another player.
     * <p>
     * Placeholder: {0} - other player's name.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.NoWithdrawOther", comment = "0: Other player")
    public String msgErrorNoWithdrawOther = "$cCould not withdraw the money from {0}!";

    /**
     * Error message when money cannot be deposited to the player.
     * <p>
     * Placeholder: {0} - the amount.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.NoDepositSelf", comment = "0: Other player")
    public String msgErrorNoDepositSelf = "$cCould not deposit the money to you, refunding {0}!";

    /**
     * Error message when money cannot be deposited to another player.
     * <p>
     * Placeholder: {0} - other player's name.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.NoDepositOther", comment = "0: Other player")
    public String msgErrorNoDepositOther = "$cCould not deposit the money to {0}, refunding you!";

    /**
     * Error message when a leased claim cannot be cancelled.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.CantCancelAlreadyLeased", comment = "0: claim type")
    public String msgErrorCantCancelAlreadyLeased = "$cThis {0} is currently being leased, you can't cancel the transaction!";

    /**
     * Error message when a rented claim cannot be cancelled.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.CantCancelAlreadyRented", comment = "0: claim type")
    public String msgErrorCantCancelAlreadyRented = "$cThis {0} is currently being rented, you can't cancel the transaction!";

    /**
     * Error message when an auctioned claim cannot be cancelled.
     */
    @ConfigField(name = "RealEstate.Errors.CantCancelAuction")
    public String msgErrorCantCancelAuction = "$cThis claim is currently being auctioned, you can't cancel the transaction!";

    /**
     * Error message when the player cannot be reimbursed.
     * <p>
     * Placeholder: {0} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.CouldntReimburseSelf", comment = "0: formatted price")
    public String msgErrorCouldntReimburseSelf = "$cCould not reimburse you, refunding {0}!";

    /**
     * Error message when another player cannot be reimbursed.
     * <p>
     * Placeholder: {0} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.CouldntReimburseOther", comment = "0: formatted price")
    public String msgErrorCouldntReimburseOther = "$cCould not reimburse {0} to another player, the action has been cancelled!";

    /**
     * Error message instructing the player to contact an admin.
     */
    @ConfigField(name = "RealEstate.Errors.ContactAdmin")
    public String msgErrorContactAdmin = "$cAn unexpected error occured, please contact an admin to resolve this issue!";

    /**
     * Error message when automatic renew is disabled.
     */
    @ConfigField(name = "RealEstate.Errors.AutoRenew.Disabled")
    public String msgErrorAutoRenewDisabled = "$cAutomatic renew is disabled!";

    /**
     * Error message when an exit offer already exists.
     */
    @ConfigField(name = "RealEstate.Errors.ExitOffer.AlreadyExists")
    public String msgErrorExitOfferAlreadyExists = "$cThere is already an exit proposition for this transaction!";

    /**
     * Error message when there is no buyer engaged in the transaction.
     */
    @ConfigField(name = "RealEstate.Errors.ExitOffer.NoBuyer")
    public String msgErrorExitOfferNoBuyer = "$cNo one is engaged by this transaction yet!";

    /**
     * Error message when there is no exit offer for the claim.
     */
    @ConfigField(name = "RealEstate.Errors.ExitOffer.None")
    public String msgErrorExitOfferNone = "$cThere is currently no exit offer for this claim!";

    /**
     * Error message when the player tries to accept their own exit offer.
     */
    @ConfigField(name = "RealEstate.Errors.ExitOffer.CantAcceptSelf")
    public String msgErrorExitOfferCantAcceptSelf = "$cYou can't accept your own exit offer!";

    /**
     * Error message when the player tries to refuse their own exit offer.
     */
    @ConfigField(name = "RealEstate.Errors.ExitOffer.CantRefuseSelf")
    public String msgErrorExitOfferCantRefuseSelf = "$cYou can't refuse your own exit offer!";

    /**
     * Error message when someone other than the offer creator tries to cancel the exit offer.
     */
    @ConfigField(name = "RealEstate.Errors.ExitOffer.CantCancelOther")
    public String msgErrorExitOfferCantCancelOther = "$cOnly the player who created this exit proposition may cancel it!";

    /**
     * Error message when the sign is not inside a claim.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NotInClaim")
    public String msgErrorSignNotInClaim = "$cThe sign you placed is not inside a claim!";

    /**
     * Error message when the claim already has an ongoing transaction.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.OngoingTransaction")
    public String msgErrorSignOngoingTransaction = "$cThis claim already has an ongoing transaction!";

    /**
     * Error message when the parent claim already has an ongoing transaction.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.ParentOngoingTransaction")
    public String msgErrorSignParentOngoingTransaction = "$cThis claim's parent already has an ongoing transaction!";

    /**
     * Error message when a subclaim has an ongoing transaction.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.SubclaimOngoingTransaction")
    public String msgErrorSignSubclaimOngoingTransaction = "$cThis claim has subclaims with ongoing transactions!";

    /**
     * Error message when selling is disabled.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.SellingDisabled")
    public String msgErrorSignSellingDisabled = "$cSelling is disabled!";

    /**
     * Error message when leasing is disabled.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.LeasingDisabled")
    public String msgErrorSignLeasingDisabled = "$cLeasing is disabled!";

    /**
     * Error message when renting is disabled.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.RentingDisabled")
    public String msgErrorSignRentingDisabled = "$cRenting is disabled!";

    /**
     * Error message when auctioning is disabled.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.AuctionDisabled")
    public String msgErrorSignAuctionDisabled = "$cAuctioning is disabled!";

    /**
     * Error message when the player lacks permission to sell a claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoSellPermission", comment = "0: claim type")
    public String msgErrorSignNoSellPermission = "$cYou don't have permission to sell this {0}!";

    /**
     * Error message when the player lacks permission to lease a claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoLeasePermission", comment = "0: claim type")
    public String msgErrorSignNoLeasePermission = "$cYou don't have permission to lease this {0}!";

    /**
     * Error message when the player lacks permission to rent a claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoRentPermission", comment = "0: claim type")
    public String msgErrorSignNoRentPermission = "$cYou don't have permission to rent this {0}!";

    /**
     * Error message when the player lacks permission to auction a claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoAuctionPermission", comment = "0: claim type")
    public String msgErrorSignNoAuctionPermission = "$cYou don't have permission to auction this {0}!";

    /**
     * Error message when the player lacks permission to sell an admin claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoAdminSellPermission", comment = "0: claim type")
    public String msgErrorSignNoAdminSellPermission = "$cYou don't have permission to sell this admin {0}!";

    /**
     * Error message when the player lacks permission to lease an admin claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoAdminLeasePermission", comment = "0: claim type")
    public String msgErrorSignNoAdminLeasePermission = "$cYou don't have permission to lease this admin {0}!";

    /**
     * Error message when the player lacks permission to rent an admin claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoAdminRentPermission", comment = "0: claim type")
    public String msgErrorSignNoAdminRentPermission = "$cYou don't have permission to rent this admin {0}!";

    /**
     * Error message when the player lacks permission to auction an admin claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoAdminAuctionPermission", comment = "0: claim type")
    public String msgErrorSignNoAdminAuctionPermission = "$cYou don't have permission to auction this admin {0}!";

    /**
     * Error message when the player attempts to modify a claim they do not own.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NotOwner", comment = "0: claim type")
    public String msgErrorSignNotOwner = "$cYou can only sell/rent/lease {0} you own!";

    /**
     * Error message when the sign is destroyed by someone who is not the author.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NotAuthor")
    public String msgErrorSignNotAuthor = "$cOnly the author of the sell/rent/lease sign is allowed to destroy it!";

    /**
     * Error message when the sign can only be destroyed by an admin.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NotAdmin")
    public String msgErrorSignNotAdmin = "$cOnly an admin is allowed to destroy this sign!";

    /**
     * Error message when no transaction is associated with the sign.
     */
    @ConfigField(name = "RealEstate.Errors.Sign.NoTransaction")
    public String msgErrorSignNoTransaction = "$cThis claim is no longer for rent, sell or lease, sorry...";

    /**
     * Error message when the claim does not exist.
     */
    @ConfigField(name = "RealEstate.Errors.Claim.DoesNotExist")
    public String msgErrorClaimDoesNotExist = "$cThis claim does not exist!";

    /**
     * Error message when an auctioned claim does not exist.
     */
    @ConfigField(name = "RealEstate.Errors.Claim.DoesNotExistAuction")
    public String msgErrorClaimDoesNotExistAuction = "$cThis auctioned claim does not exist!";

    /**
     * Error message when the player is already the owner of the claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.AlreadyOwner", comment = "0: claim type")
    public String msgErrorClaimAlreadyOwner = "$cYou are already the owner of this {0}!";

    /**
     * Error message when the claim is not sold by its owner.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NotSoldByOwner", comment = "0: claim type")
    public String msgErrorClaimNotSoldByOwner = "$cThis {0} is not sold by its owner!";

    /**
     * Error message when the claim is not leased by its owner.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NotLeasedByOwner", comment = "0: claim type")
    public String msgErrorClaimNotLeasedByOwner = "$cThis {0} is not leased by its owner!";

    /**
     * Error message when the claim is not rented by its owner.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NotRentedByOwner", comment = "0: claim type")
    public String msgErrorClaimNotRentedByOwner = "$cThis {0} is not rented by its owner!";

    /**
     * Error message when the claim is not auctioned by its owner.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NotAuctionedByOwner", comment = "0: claim type")
    public String msgErrorClaimNotAuctionedByOwner = "$cThis {0} is not auctioned by its owner!";

    /**
     * Error message when the player does not have permission to buy a claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NoBuyPermission", comment = "0: claim type")
    public String msgErrorClaimNoBuyPermission = "$cYou don't have permission to buy this {0}!";

    /**
     * Error message when the player does not have permission to lease a claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NoLeasePermission", comment = "0: claim type")
    public String msgErrorClaimNoLeasePermission = "$cYou don't have permission to lease this {0}!";

    /**
     * Error message when the player does not have permission to rent a claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NoRentPermission", comment = "0: claim type")
    public String msgErrorClaimNoRentPermission = "$cYou don't have permission to rent this {0}!";

    /**
     * Error message when the player does not have permission to auction a claim.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NoAuctionPermission", comment = "0: claim type")
    public String msgErrorClaimNoAuctionPermission = "$cYou don't have permission to auction this {0}!";

    /**
     * Error message when the claim is already leased.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.AlreadyLeased", comment = "0: claim type")
    public String msgErrorClaimAlreadyLeased = "$cThis {0} is already leased!";

    /**
     * Error message when the claim is already rented.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.AlreadyRented", comment = "0: claim type")
    public String msgErrorClaimAlreadyRented = "$cThis {0} is already rented!";

    /**
     * Error message when the player is already the highest bidder.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.AlreadyHighestBidder", comment = "0: claim type")
    public String msgErrorClaimAlreadyHighestBidder = "$cYou are already the highest bidder of this {0}!";

    /**
     * Error message when the player does not have permission to view claim information.
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NoInfoPermission")
    public String msgErrorClaimNoInfoPermission = "$cYou don't have permission to view this real estate informations!";

    /**
     * Error message when the player does not have enough claim blocks.
     * <p>
     * Placeholders: {0} - required area, {1} - remaining claim blocks, {2} - missing claim blocks.
     * </p>
     */
    @ConfigField(name = "RealEstate.Errors.Claim.NoClaimBlocks", comment = "0: area; 1: claim blocks remaining; 2: missing claim blocks")
    public String msgErrorClaimNoClaimBlocks = "$cYou don't have enough claim blocks! You need $a{2}$c more claim blocks to claim this area. The claim requires $a{0}$c claim blocks, you only have $a{1}$c claim blocks left.";

    /**
     * Error message when the auction payment to the owner fails.
     */
    @ConfigField(name = "RealEstate.Errors.Auction.CouldntPayOwner")
    public String msgErrorAuctionCouldntPayOwner = "$cCouldn't pay the owner of this auction! The auction is being cancelled.";

    /**
     * Error message when the auction payment from the owner fails.
     */
    @ConfigField(name = "RealEstate.Errors.Auction.CouldntReceiveOwner")
    public String msgErrorAuctionCouldntReceiveOwner = "$cCouldn't receive the payment of this auction! The auction is being cancelled.";

    /**
     * Message indicating that no exit offer is available for the claim.
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.None")
    public String msgInfoExitOfferNone = "$bThere is currently no exit offer for this claim!";

    /**
     * Message indicating the status of an exit offer created by the player.
     * <p>
     * Placeholder: {0} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.MadeByStatus", comment = "0: formatted price")
    public String msgInfoExitOfferMadeByStatus = "$bYou offered to exit the contract for $a{0}$b, but your offer hasn't been accepted or denied yet...";

    /**
     * Message indicating the status of an exit offer made to the player.
     * <p>
     * Placeholders: {0} - player who made the offer; {1} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.MadeToStatus", comment = "0: player who made the offer; 1: formatted price")
    public String msgInfoExitOfferMadeToStatus = "$a{0} $boffered to exit the contract for $a{1}";

    /**
     * Message instructing the player on how to cancel their exit offer.
     * <p>
     * Placeholder: {0} - cancel command.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.Cancel", comment = "0: cancel command")
    public String msgInfoExitOfferCancel = "$bTo cancel your offer, use $d{0}";

    /**
     * Message instructing the player on how to accept an exit offer.
     * <p>
     * Placeholder: {0} - accept command.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.Accept", comment = "0: accept command")
    public String msgInfoExitOfferAccept = "$bTo accept this offer, use $d{0}";

    /**
     * Message instructing the player on how to reject an exit offer.
     * <p>
     * Placeholder: {0} - reject command.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.Reject", comment = "0: reject command")
    public String msgInfoExitOfferReject = "$bTo reject this offer, use $d{0}";

    /**
     * Message confirming that the exit offer was created by the player.
     * <p>
     * Placeholder: {0} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.CreatedBySelf", comment = "0: formatted price")
    public String msgInfoExitOfferCreatedBySelf = "$bThe offer has been successfully created for $a{0}";

    /**
     * Broadcast message when another player creates an exit offer.
     * <p>
     * Placeholders: {0} - player name; {1} - claim type; {2} - formatted price; {3} - claim location.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.CreatedByOther", comment = "0: player name, 1: claim type, 2: formatted price, 3: claim location")
    public String msgInfoExitOfferCreatedByOther = "$a{0} $bhas created an offer to exit the transaction for the {1} at $a{3} $bfor $a{2}";

    /**
     * Message confirming that the exit offer was accepted by the player.
     * <p>
     * Placeholders: {0} - claim type; {1} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.AcceptedBySelf", comment = "0: claim type, 1:formatted price")
    public String msgInfoExitOfferAcceptedBySelf = "$bThe {0} is no longer rented or leased, you have been charged $a{1}";

    /**
     * Broadcast message when another player accepts the exit offer.
     * <p>
     * Placeholders: {0} - player name; {1} - claim type; {2} - formatted price; {3} - claim location.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.AcceptedByOther", comment = "0: player name, 1: claim type, 2: formatted price, 3: claim location")
    public String msgInfoExitOfferAcceptedByOther = "$a{0} $bhas accepted the offer to exit the transaction for the {1} at $a{3} $bfor $a{2}. It is no longer leased or rented.";

    /**
     * Message indicating that the exit offer has been rejected.
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.RejectedBySelf")
    public String msgInfoExitOfferRejectedBySelf = "$bThe exit offer has been refused.";

    /**
     * Broadcast message when another player rejects the exit offer.
     * <p>
     * Placeholders: {0} - player name; {1} - claim type; {2} - claim location.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.RejectedByOther", comment = "0: player name, 1: claim type, 2: claim location")
    public String msgInfoExitOfferRejectedByOther = "$a{0} $bhas refused the offer to exit the transaction for the {1} at $a{2}";

    /**
     * Message confirming that the exit offer has been cancelled by the player.
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.CancelledBySelf")
    public String msgInfoExitOfferCancelledBySelf = "$bThe exit offer has been cancelled.";

    /**
     * Broadcast message when another player cancels the exit offer.
     * <p>
     * Placeholders: {0} - player name; {1} - claim type; {2} - claim location.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.ExitOffer.CancelledByOther", comment = "0: player name, 1: claim type, 2: claim location")
    public String msgInfoExitOfferCancelledByOther = "$a{0} $bhas cancelled the offer to exit the transaction for the {1} at $a{2}";

    /**
     * Broadcast message when a claim is bought by a new owner.
     * <p>
     * Placeholders: {0} - buyer name; {1} - claim type; {2} - formatted price; {3} - claim location.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.OwnerSold", comment = "0: buyer name, 1: claim type, 2: formatted price, 3: claim location")
    public String msgInfoClaimOwnerSold = "$a{0} $bhas bought the {1} at $a{3} $bfor $a{2}";

    /**
     * Broadcast message when a claim lease has started.
     * <p>
     * Placeholders: {0} - buyer name; {1} - claim type; {2} - formatted price; {3} - claim location; {4} - payments left.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.OwnerLeaseStarted", comment = "0: buyer name, 1: claim type, 2: formatted price, 3: claim location, 4: payments left")
    public String msgInfoClaimOwnerLeaseStarted = "$a{0} $bhas leased the {1} at $a{3} $bfor $a{2} with $a{4} $bpayments left";

    /**
     * Broadcast message when a claim is rented.
     * <p>
     * Placeholders: {0} - buyer name; {1} - claim type; {2} - formatted price; {3} - claim location.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.OwnerRented", comment = "0: buyer name, 1: claim type, 2: formatted price, 3: claim location")
    public String msgInfoClaimOwnerRented = "$a{0} $bhas rented the {1} at $a{3} $bfor $a{2}";

    /**
     * Message for when a buyer successfully purchases a claim.
     * <p>
     * Placeholders: {0} - claim type; {1} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.BuyerBought", comment = "0: claim type, 1: formatted price")
    public String msgInfoClaimBuyerSold = "$bYou have bought the {0} for $a{1}";

    /**
     * Message for when a buyer starts leasing a claim.
     * <p>
     * Placeholders: {0} - claim type; {1} - formatted price; {2} - payments left.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.BuyerLeaseStarted", comment = "0: claim type, 1: formatted price, 2: payments left")
    public String msgInfoClaimBuyerLeaseStarted = "$bYou have leased the {0} for $a{1} with $a{2} $bpayments left";

    /**
     * Message for when a buyer rents a claim.
     * <p>
     * Placeholders: {0} - claim type; {1} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.BuyerRented", comment = "0: claim type, 1: formatted price")
    public String msgInfoClaimBuyerRented = "$bYou have rented the {0} for $a{1}";

    /**
     * Header for the lease information display.
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Lease.Header")
    public String msgInfoClaimInfoLeaseHeader = "$9-----= $f[$6RealEstate Lease Info$f]$9 =-----";

    /**
     * General lease information message when no buyer is present.
     * <p>
     * Placeholders: {0} - claim type; {1} - payments left; {2} - formatted price; {3} - frequency.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Lease.GeneralNoBuyer", comment = "0: claim type, 1: payments left, 2: formatted price, 3: frequency")
    public String msgInfoClaimInfoGeneralLeaseNoBuyer = "$bThis {0} is for lease for $a{1} $bpayments of $a{2} each. Payments are due every $a{3}";

    /**
     * General lease information message when a buyer is present.
     * <p>
     * Placeholders: {0} - claim type; {1} - buyer name; {2} - formatted price; {3} - payments left; {4} - time until next payment; {5} - frequency.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Lease.GeneralBuyer", comment = "0: claim type, 1: buyer name, 2: formatted price, 3: payments left, 4: next payment due, 5: frequency")
    public String msgInfoClaimInfoGeneralLeaseBuyer = "$bThis {0} is currently leased by $a{1}$b for $a{2}$b. There is $a{3} $bpayments left. Next payment is in $a{4}$b. Payments are due every $a{5}";

    /**
     * One-line lease information display.
     * <p>
     * Placeholders: {0} - claim area; {1} - location; {2} - payments left; {3} - period; {4} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Lease.Oneline", comment = "0: claim area, 1: location, 2: payments left, 3: period, 4: formatted price")
    public String msgInfoClaimInfoLeaseOneline = "$2{0} $bblocks to $2Lease $bat $2{1} $bfor $a{2} periods of $a{3}$b, each period costs $a{4}";

    /**
     * Lease payment information message for the buyer.
     * <p>
     * Placeholders: {0} - claim type; {1} - location; {2} - formatted price; {3} - payments left.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Lease.PaymentBuyer", comment = "0: claim type, 1: location, 2: formatted price, 3: payments left")
    public String msgInfoClaimInfoLeasePaymentBuyer = "$bPaid lease for the {0} at $a{1} $bfor $a{2}$b. There are $a{3} $bpayments left.";

    /**
     * Lease payment information message for the owner.
     * <p>
     * Placeholders: {0} - buyer name; {1} - claim type; {2} - location; {3} - formatted price; {4} - payments left.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Lease.PaymentOwner", comment = "0: player name, 1: claim type, 2: location, 3: formatted price, 4: payments left")
    public String msgInfoClaimInfoLeasePaymentOwner = "$a{0} $bpaid lease for the {1} at $a{2} $bfor $a{3}$b. There are $a{4} $bpayments left.";

    /**
     * Lease payment message for the buyer when it is the final payment.
     * <p>
     * Placeholders: {0} - claim type; {1} - location; {2} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Lease.PaymentBuyerFinal", comment = "0: claim type, 1: location, 2: formatted price")
    public String msgInfoClaimInfoLeasePaymentBuyerFinal = "$bPaid final lease for the {0} at $a{1} $bfor $a{2}$b. The {0} is now your property.";

    /**
     * Lease payment message for the owner when it is the final payment.
     * <p>
     * Placeholders: {0} - buyer name; {1} - claim type; {2} - location; {3} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Lease.PaymentOwnerFinal", comment = "0: player name, 1: claim type, 2: location, 3: formatted price")
    public String msgInfoClaimInfoLeasePaymentOwnerFinal = "$a{0} $bpaid final lease for the {1} at $a{2} $bfor $a{3}$b. The {1} is now $a{0}$b's property.";

    /**
	 * Lease payment cancellation message for the buyer.
	 * <p>
	 * Placeholders: {0} - claim type; {1} - location; {2} - formatted price.
	 * </p>
	 */
    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.PaymentBuyerCancelled", comment = "0: claim type, 1: location, 2: formatted price")
    public String msgInfoClaimInfoLeasePaymentBuyerCancelled = "$bCouldn't pay the lease for the {0} at $a{1} $bfor $a{2}$b. The lease has been cancelled.";
    
    /**
	 * Lease payment cancellation message for the owner.
	 * <p>
	 * Placeholders: {0} - player name; {1} - claim type; {2} - location; {3} - formatted price.
	 * </p>
	 */
    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.PaymentOwnerCancelled", comment = "0: player name, 1: claim type, 2: location, 3: formatted price")
    public String msgInfoClaimInfoLeasePaymentOwnerCancelled = "$a{0} $bcouldn't pay the lease for the {1} at $a{2} $bfor $a{3}$b. The lease has been cancelled.";
    
    /**
     * Rent information header.
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.Header")
    public String msgInfoClaimInfoRentHeader = "$9-----= $f[$6RealEstate Rent Info$f]$9 =-----";

    /**
     * General rent information message when no buyer is present.
     * <p>
     * Placeholders: {0} - claim type; {1} - formatted price; {2} - duration.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.GeneralNoBuyer", comment = "0: claim type, 1: formatted price, 2: duration")
    public String msgInfoClaimInfoGeneralRentNoBuyer = "$bThis {0} is for rent for $a{1}$b per $a{2}$b.";

    /**
     * General rent information message when a buyer is present.
     * <p>
     * Placeholders: {0} - claim type; {1} - buyer name; {2} - formatted price; {3} - time remaining; {4} - period duration.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.GeneralBuyer", comment = "0: claim type, 1: buyer name, 2: formatted price, 3: time left in current period, 4: duration of a period")
    public String msgInfoClaimInfoGeneralRentBuyer = "$bThis {0} is currently rented by $a{1}$b for $a{2}$b. The {0} is rented for another $a{3}$b. The rent period is $a{4}";

    /**
     * Message indicating the current automatic renew status for rent.
     * <p>
     * Placeholder: {0} - enabled/disabled.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.AutoRenew", comment = "0: enabled / disabled")
    public String msgInfoClaimInfoRentAutoRenew = "$bAutomatic renew is currently $a{0}$b.";

    /**
     * One-line rent information message.
     * <p>
     * Placeholders: {0} - claim area; {1} - location; {2} - formatted price; {3} - duration.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.Oneline", comment = "0: claim area, 1: location, 2: formatted price, 3: duration")
    public String msgInfoClaimInfoRentOneline = "$2{0} $bblocks to $2Rent $bat $2{1} $bfor $a{2}$b per $a{3}";

    /**
     * Rent payment information message for the buyer.
     * <p>
     * Placeholders: {0} - claim type; {1} - location; {2} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.PaymentBuyer", comment = "0: claim type, 1: location, 2: formatted price")
    public String msgInfoClaimInfoRentPaymentBuyer = "$bPaid rent for the {0} at $a{1} $bfor $a{2}$b.";

    /**
     * Rent payment information message for the owner.
     * <p>
     * Placeholders: {0} - buyer name; {1} - claim type; {2} - location; {3} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.PaymentOwner", comment = "0: player name, 1: claim type, 2: location, 3: formatted price")
    public String msgInfoClaimInfoRentPaymentOwner = "$a{0} $bpaid rent for the {1} at $a{2} $bfor $a{3}$b.";

    /**
     * Rent payment cancellation message for the buyer.
     * <p>
     * Placeholders: {0} - claim type; {1} - location; {2} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.PaymentBuyerCancelled", comment = "0: claim type, 1: location, 2: formatted price")
    public String msgInfoClaimInfoRentPaymentBuyerCancelled = "$bCouldn't pay the rent for the {0} at $a{1} $bfor $a{2}$b. The rent has been cancelled.";

    /**
     * Rent payment cancellation message for the owner.
     * <p>
     * Placeholders: {0} - buyer name; {1} - claim type; {2} - location; {3} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.PaymentOwnerCancelled", comment = "0: player name, 1: claim type, 2: location, 3: formatted price")
    public String msgInfoClaimInfoRentPaymentOwnerCancelled = "$a{0} $bcouldn't pay the rent for the {1} at $a{2} $bfor $a{3}$b. The rent has been cancelled.";

    /**
     * Rent cancellation message.
     * <p>
     * Placeholders: {0} - claim type; {1} - location.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Rent.RentCancelled", comment = "0: claim type, 1: location")
    public String msgInfoClaimInfoRentCancelled = "$bThe rent for the {0} at $a{1} $bis now over, your access has been revoked.";

    /**
     * Header for the sale information display.
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Sell.Header")
    public String msgInfoClaimInfoSellHeader = "$9-----= $f[$6RealEstate Sale Info$f]$9 =-----";

    /**
     * General sale information message.
     * <p>
     * Placeholders: {0} - claim type; {1} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Sell.General", comment = "0: claim type, 1: formatted price")
    public String msgInfoClaimInfoSellGeneral = "$bThis {0} is for sale for $a{1}";

    /**
     * One-line sale information message.
     * <p>
     * Placeholders: {0} - claim area; {1} - location; {2} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Sell.Oneline", comment = "0: claim area, 1: location, 2: formatted price")
    public String msgInfoClaimInfoSellOneline = "$2{0} $bblocks to $2Sell $bat $2{1} $bfor $a{2}";

    /**
     * Header for the auction information display.
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Auction.Header")
    public String msgInfoClaimInfoAuctionHeader = "$9-----= $f[$6RealEstate Auction Info$f]$9 =-----";

    /**
     * Auction information message when no bidder is present.
     * <p>
     * Placeholders: {0} - claim type; {1} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Auction.NoBidder", comment = "0: claim type, 1: formatted price")
    public String msgInfoClaimInfoAuctionNoBidder = "$bThis {0} is currently being auctioned for $a{1}$b.";

    /**
     * Auction information message showing the highest bidder.
     * <p>
     * Placeholders: {0} - claim type; {1} - bidder name; {2} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Auction.HighestBidder", comment = "0: claim type, 1: bidder name, 2: formatted price")
    public String msgInfoClaimInfoAuctionHighestBidder = "$bThis {0} is currently being auctioned. The highest bidder is $a{1}$b for $a{2}$b.";

    /**
     * Auction information message displaying the remaining time.
     * <p>
     * Placeholder: {0} - formatted time remaining.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Auction.TimeRemaining", comment = "0: time remaining")
    public String msgInfoClaimInfoAuctionTimeRemaining = "$bThe auction will end in $a{0}$b.";

    /**
     * Auction information message displaying the bid step.
     * <p>
     * Placeholder: {0} - bid step.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Auction.BidStep", comment = "0: Bid Step")
    public String msgInfoClaimInfoAuctionBidStep = "$bThe bid step is $a{0}$b.";

    /**
     * One-line auction information message.
     * <p>
     * Placeholders: {0} - claim area; {1} - location; {2} - formatted price; {3} - time remaining; {4} - bid step.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Auction.Oneline", comment = "0: claim area, 1: location, 2: formatted price, 3: time remaining, 4: bid step")
    public String msgInfoClaimInfoAuctionOneline = "$2{0} $bblocks to $2Auction $bat $2{1}$b. Current highest bid is $a{2}$b. The auction will end in $a{3}$b. The bid step is $a{4}";

    /**
     * Auction information message when the auction has ended.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Auction.Ended", comment = "0: claim type")
    public String msgInfoClaimInfoAuctionEnded = "$bThe auction for the {0} has ended.";

    /**
     * Auction information message when the auction is cancelled.
     * <p>
     * Placeholder: {0} - claim type.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Auction.Cancelled", comment = "0: claim type")
    public String msgInfoClaimInfoAuctionCancelled = "$bThe auction for the {0} has been cancelled. You have been reimbursed.";

    /**
     * Message displaying the current owner of the claim.
     * <p>
     * Placeholder: {0} - owner name.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Owner", comment = "0: owner name")
    public String msgInfoClaimInfoOwner = "$bThe current owner is $a{0}";

    /**
     * Message displaying the main claim's owner.
     * <p>
     * Placeholder: {0} - owner name.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.MainOwner", comment = "0: owner name")
    public String msgInfoClaimInfoMainOwner = "$bThe main claim's owner is $a{0}";

    /**
     * A note regarding subclaim access.
     */
    @ConfigField(name = "RealEstate.Info.Claim.Info.Note")
    public String msgInfoClaimInfoNote = "$dNote: You will only get access to this subclaim.";

    /**
     * Message confirming successful creation of a sale.
     * <p>
     * Placeholders: {0} - claim prefix; {1} - claim type; {2} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Created.Sell", comment = "0: claim prefix, 1: claim type, 2: formatted price")
    public String msgInfoClaimCreatedSell = "$bYou have successfully created {0} {1} sale for $a{2}";

    /**
     * Message confirming successful creation of a lease.
     * <p>
     * Placeholders: {0} - claim prefix; {1} - claim type; {2} - formatted price; {3} - payments count; {4} - frequency.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Created.Lease", comment = "0: claim prefix, 1: claim type, 2: formatted price, 3: payments count, 4: frequency")
    public String msgInfoClaimCreatedLease = "$bYou have successfully created {0} {1} lease for $a{3}$b payments of $a{2}$b each. Payments are due every $a{4}";

    /**
     * Message confirming successful creation of a rent offer.
     * <p>
     * Placeholders: {0} - claim prefix; {1} - claim type; {2} - formatted price; {3} - duration.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Created.Rent", comment = "0: claim prefix, 1: claim type, 2: formatted price, 3: duration")
    public String msgInfoClaimCreatedRent = "$bYou have successfully created {0} {1} rent for $a{2}$b per $a{3}";

    /**
     * Message confirming successful creation of an auction.
     * <p>
     * Placeholders: {0} - claim prefix; {1} - claim type; {2} - formatted price; {3} - formatted bid step; {4} - time remaining.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Created.Auction", comment = "0: claim prefix, 1: claim type, 2: formatted price, 3: formatted bid step, 4: time remaining")
    public String msgInfoClaimCreatedAuction = "$bYou have successfully created {0} {1} auction for $a{2}$b. The bid step is $a{3}$b. The auction will end in $a{4}";

    /**
     * Broadcast message when a sale is created.
     * <p>
     * Placeholders: {0} - player name; {1} - claim prefix; {2} - claim type; {3} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Created.SellBroadcast", comment = "0: player name, 1: claim prefix, 2: claim type, 3: formatted price")
    public String msgInfoClaimCreatedSellBroadcast = "$a{0} $bhas created {1} {2} sale for $a{3}";

    /**
     * Broadcast message when a lease is created.
     * <p>
     * Placeholders: {0} - player name; {1} - claim prefix; {2} - claim type; {3} - formatted price; {4} - payments count; {5} - frequency.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Created.LeaseBroadcast", comment = "0: player name, 1: claim prefix, 2: claim type, 3: formatted price, 4: payments count, 5: frequency")
    public String msgInfoClaimCreatedLeaseBroadcast = "$a{0} $bhas created {1} {2} lease for $a{4}$b payments of $a{3}$b each. Payments are due every $a{5}";

    /**
     * Broadcast message when a rent offer is created.
     * <p>
     * Placeholders: {0} - player name; {1} - claim prefix; {2} - claim type; {3} - formatted price; {4} - duration.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Created.RentBroadcast", comment = "0: player name, 1: claim prefix, 2: claim type, 3: formatted price, 4: duration")
    public String msgInfoClaimCreatedRentBroadcast = "$a{0} $bhas created {1} {2} rent for $a{3}$b per $a{4}";

    /**
     * Broadcast message when an auction is created.
     * <p>
     * Placeholders: {0} - player name; {1} - claim prefix; {2} - claim type; {3} - formatted price; {4} - formatted bid step; {5} - time remaining.
     * </p>
     */
    @ConfigField(name = "RealEstate.Info.Claim.Created.AuctionBroadcast", comment = "0: player name, 1: claim prefix, 2: claim type, 3: formatted price, 4: formatted bid step, 5: time remaining")
    public String msgInfoClaimCreatedAuctionBroadcast = "$a{0} $bhas created {1} {2} auction for $a{3}$b. The bid step is $a{4}$b. The auction will end in $a{5}";

    /**
     * Header for the transaction list.
     * <p>
     * Placeholders: {0} - list type (e.g., RE Offers, Sell Offers, Rent Offers, Lease Offers); {1} - current page number; {2} - total page count.
     * </p>
     */
    @ConfigField(name = "RealEstate.List.Header", comment = "0: RE Offers|Sell Offers|Rent Offers|Lease Offers; 1: Page number; 2: Page count")
    public String msgListTransactionsHeader = "$1----= $f[ $6{0} page $2 {1} $6/ $2{2} $f] $1=----";
    
    /**
     * Message indicating how to view the next page of transactions.
     * <p>
     * Placeholders: {0} - list type; {1} - next page number.
     * </p>
     */
    @ConfigField(name = "RealEstate.List.NextPage", comment = "0: all|sell|rent|lease; 1: next page number")
    public String msgListNextPage = "$6To see the next page, type $a/re list {0} {1}";

    /**
     * Sign message displaying the highest bidder in an auction.
     * <p>
     * Placeholders: {0} - bidder name; {1} - formatted price.
     * </p>
     */
    @ConfigField(name = "RealEstate.Sign.Auction.HighestBidder", comment = "0: player name, 1: formatted price")
    public String msgSignAuctionHighestBidder = "$b{0}: $a{1}";

    /**
     * Sign message displayed when no bidder is present.
     */
    @ConfigField(name = "RealEstate.Sign.Auction.NoBider")
    public String msgSignAuctionNoBider = "$bNo bidder";

    /**
     * Sign message showing the remaining time in an auction.
     * <p>
     * Placeholder: {0} - formatted time remaining.
     * </p>
     */
    @ConfigField(name = "RealEstate.Sign.Auction.RemainingTime", comment = "0: formatted time")
    public String msgSignAuctionRemainingTime = "$b$a{0}";

    /**
     * Sign message indicating that the auction has ended.
     */
    @ConfigField(name = "RealEstate.Sign.Auction.Ended")
    public String msgSignAuctionEnded = "$bAuction ended";

    /**
     * Sign message indicating that the auction was won.
     */
    @ConfigField(name = "RealEstate.Sign.Auction.Won", comment = "next line: winner")
    public String msgSignAuctionWon = "$bAuction won by";

    /**
     * Loads the language configuration file.
     */
    @Override
    public void loadConfig() {
        this.loadConfig(RealEstate.languagesDirectory + "/" + RealEstate.instance.config.languageFile);
    }

    /**
     * Retrieves a formatted message with placeholders replaced.
     *
     * @param msgTemplate the message template.
     * @param args the values to replace placeholders.
     * @return the formatted message with the default chat prefix.
     */
    public static String getMessage(String msgTemplate, String... args) {
        return getMessage(msgTemplate, true, args);
    }

    /**
     * Retrieves a formatted message.
     *
     * @param msgTemplate the message template.
     * @param withPrefix if true, prepends the chat prefix.
     * @param args the values to replace placeholders.
     * @return the formatted message.
     */
    public static String getMessage(String msgTemplate, boolean withPrefix, String... args) {
        if (withPrefix) {
            msgTemplate = RealEstate.instance.config.chatPrefix + msgTemplate;
        }
        msgTemplate = msgTemplate.replace('$', ChatColor.COLOR_CHAR);
        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            msgTemplate = msgTemplate.replaceAll("\\{" + i + "\\}", Matcher.quoteReplacement(param));
        }
        return msgTemplate;
    }

    /**
     * Sends a formatted message to a CommandSender.
     *
     * @param player the recipient.
     * @param msgTemplate the message template.
     * @param args the values for placeholders.
     */
    public static void sendMessage(CommandSender player, String msgTemplate, String... args) {
        sendMessage(player, msgTemplate, 0, args);
    }

    /**
     * Sends a formatted message to a CommandSender after a delay.
     *
     * @param player the recipient.
     * @param msgTemplate the message template.
     * @param delayInTicks delay in ticks.
     * @param args the values for placeholders.
     */
    public static void sendMessage(CommandSender player, String msgTemplate, long delayInTicks, String... args) {
        String message = getMessage(msgTemplate, args);
        sendMessage(player, message, delayInTicks);
    }

    /**
     * Sends a pre-formatted message to a CommandSender.
     *
     * @param player the recipient.
     * @param message the formatted message.
     */
    public static void sendMessage(CommandSender player, String message) {
        sendMessage(player, getMessage(message), 0);
    }

    /**
     * Sends a message to a CommandSender with an option to process color codes.
     *
     * @param player the recipient.
     * @param message the message.
     * @param fixColors if true, converts formatting codes.
     */
    public static void sendMessage(CommandSender player, String message, Boolean fixColors) {
        sendMessage(player, fixColors ? getMessage(message) : message, 0);
    }

    /**
     * Schedules a message to be sent to a CommandSender after a delay.
     *
     * @param player the recipient.
     * @param message the message.
     * @param delayInTicks delay in ticks.
     */
    public static void sendMessage(CommandSender player, String message, long delayInTicks) {
        SendPlayerMessageTask task = new SendPlayerMessageTask(player, message);
        if (delayInTicks > 0) {
            RealEstate.instance.getServer().getScheduler().runTaskLater(RealEstate.instance, task, delayInTicks);
        } else {
            task.run();
        }
    }
}
