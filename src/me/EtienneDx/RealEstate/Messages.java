package me.EtienneDx.RealEstate;

import java.util.regex.Matcher;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import me.EtienneDx.AnnotationConfig.AnnotationConfig;
import me.EtienneDx.AnnotationConfig.ConfigField;
import me.EtienneDx.AnnotationConfig.ConfigFile;
import net.md_5.bungee.api.ChatColor;

@ConfigFile(header = "Use a YAML editor like NotepadPlusPlus to edit this file.  \nAfter editing, back up your changes before reloading the server in case you made a syntax error.  \nUse dollar signs ($) for formatting codes, which are documented here: http://minecraft.gamepedia.com/Formatting_codes.\n You can use {0}, {1} to include the different values indicated in the comments")
public class Messages extends AnnotationConfig
{
    public PluginDescriptionFile pdf;

    @ConfigField(name="RealEstate.Keywords.Enabled", comment = "Keywords used within other messages but with a longer text at the end just because i need to test some stuff")
    public String keywordEnabled = "enabled";

    @ConfigField(name="RealEstate.Keywords.Disabled")
    public String keywordDisabled = "disabled";

    @ConfigField(name="RealEstate.Keywords.Claim")
    public String keywordClaim = "claim";

    @ConfigField(name="RealEstate.Keywords.Subclaim")
    public String keywordSubclaim = "subclaim";

    @ConfigField(name="RealEstate.Keywords.AdminClaimPrefix")
    public String keywordAdminClaimPrefix = "an admin";

    @ConfigField(name="RealEstate.Keywords.ClaimPrefix")
    public String keywordClaimPrefix = "a";

    @ConfigField(name="RealEstate.Keywords.TheServer")
    public String keywordTheServer = "The server";

    @ConfigField(name="RealEstate.NoTransactionFound")
    public String msgNoTransactionFound = "$cNo transaction found!";

    @ConfigField(name="RealEstate.NoTransactionFoundHere")
    public String msgNoTransactionFoundHere = "$cNo transaction found at your location!";

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

    @ConfigField(name="RealEstate.Errors.ClaimInTransaction.CantOwner")
    public String msgErrorClaimInTransactionCantOwner = "$cThis claim is currently involved in a transaction, you can't modify it!";

    @ConfigField(name="RealEstate.Errors.ClaimInTransaction.CantEdit")
    public String msgErrorClaimInTransactionCantEdit = "$cThis claim is currently involved in a transaction, you can't edit it!";

    @ConfigField(name="RealEstate.Errors.ClaimInTransaction.CantAccess")
    public String msgErrorClaimInTransactionCantAccess = "$cThis claim is currently involved in a transaction, you can't access it!";

    @ConfigField(name="RealEstate.Errors.ClaimInTransaction.CantBuild")
    public String msgErrorClaimInTransactionCantBuild = "$cThis claim is currently involved in a transaction, you can't build on it!";

    @ConfigField(name="RealEstate.Errors.ClaimInTransaction.CantInventory")
    public String msgErrorClaimInTransactionCantInventory = "$cThis claim is currently involved in a transaction, you can't access its containers!";

    @ConfigField(name="RealEstate.Errors.ClaimInTransaction.CantManage")
    public String msgErrorClaimInTransactionCantManage = "$cThis claim is currently involved in a transaction, you can't manage it!";

    @ConfigField(name="RealEstate.Errors.ClaimInTransaction.Subclaim")
    public String msgErrorSubclaimInTransaction = "$cA subclaim is currently involved in a transaction, you can't edit or manage the parent claim!";
    
    @ConfigField(name="RealEstate.Errors.Command.Usage", comment = "0: command usage")
    public String msgErrorCommandUsage = "$cUsage: {0}";

    @ConfigField(name="RealEstate.Errors.BuyerOnly")
    public String msgErrorBuyerOnly = "$cOnly the buyer can perform this command!";

    @ConfigField(name="RealEstate.Errors.Unexpected")
    public String msgErrorUnexpected = "$cAn unexpected error has occured!";

    @ConfigField(name="RealEstate.Errors.InvalidNumber", comment = "0: number")
    public String msgErrorInvalidNumber = "$c{0} is not a valid number!";

    @ConfigField(name="RealEstate.Errors.NegativeNumber", comment = "0: number")
    public String msgErrorNegativeNumber = "$c{0} is a negative number!";

    @ConfigField(name="RealEstate.Errors.NegativePrice", comment = "0: price")
    public String msgErrorNegativePrice = "$cThe price must be greater than zero!";

    @ConfigField(name="RealEstate.Errors.NonIntegerPrice", comment = "0: price")
    public String msgErrorNonIntegerPrice = "$cThe price must be an integer!";

    @ConfigField(name="RealEstate.Errors.InvalidDuration", comment = "0: duration, 1: example of duration format, 2: example, 3: example")
    public String msgErrorInvalidDuration = "$c{0} is not a valid duration! Durations must be in the format $a{1}$c or $a{2}$c or $a{3}$c!";

    @ConfigField(name="RealEstate.Errors.NoMoneySelf")
    public String msgErrorNoMoneySelf = "$cYou don't have enough money to make this transaction!";

    @ConfigField(name="RealEstate.Errors.NoMoneyOther", comment = "0: Other player")
    public String msgErrorNoMoneyOther = "$c{0} doesn't have enough money to make this transaction!";

    @ConfigField(name="RealEstate.Errors.NoWithdrawSelf")
    public String msgErrorNoWithdrawSelf = "$cCould not withdraw the money!";

    @ConfigField(name="RealEstate.Errors.NoWithdrawOther", comment = "0: Other player")
    public String msgErrorNoWithdrawOther = "$cCould not withdraw the money from {0}!";

    @ConfigField(name="RealEstate.Errors.NoDepositSelf", comment = "0: Other player")
    public String msgErrorNoDepositSelf = "$cCould not deposit the money to you, refunding {0}!";

    @ConfigField(name="RealEstate.Errors.NoDepositOther", comment = "0: Other player")
    public String msgErrorNoDepositOther = "$cCould not deposit the money to {0}, refunding you!";

    @ConfigField(name="RealEstate.Errors.CantCancelAlreadyLeased", comment = "0: claim type")
    public String msgErrorCantCancelAlreadyLeased = "$cThis {0} is currently being leased, you can't cancel the transaction!";

    @ConfigField(name="RealEstate.Errors.CantCancelAlreadyRented", comment = "0: claim type")
    public String msgErrorCantCancelAlreadyRented = "$cThis {0} is currently being rented, you can't cancel the transaction!";

    @ConfigField(name="RealEstate.Errors.AutoRenew.Disabled")
    public String msgErrorAutoRenewDisabled = "$cAutomatic renew is disabled!";

    @ConfigField(name="RealEstate.Errors.ExitOffer.AlreadyExists")
    public String msgErrorExitOfferAlreadyExists = "$cThere is already an exit proposition for this transaction!";

    @ConfigField(name="RealEstate.Errors.ExitOffer.NoBuyer")
    public String msgErrorExitOfferNoBuyer = "$cNo one is engaged by this transaction yet!";

    @ConfigField(name="RealEstate.Errors.ExitOffer.None")
    public String msgErrorExitOfferNone = "$cThere is currently no exit offer for this claim!";

    @ConfigField(name="RealEstate.Errors.ExitOffer.CantAcceptSelf")
    public String msgErrorExitOfferCantAcceptSelf = "$cYou can't accept your own exit offer!";

    @ConfigField(name="RealEstate.Errors.ExitOffer.CantRefuseSelf")
    public String msgErrorExitOfferCantRefuseSelf = "$cYou can't refuse your own exit offer!";

    @ConfigField(name="RealEstate.Errors.ExitOffer.CantCancelOther")
    public String msgErrorExitOfferCantCancelOther = "$cOnly the player who created this exit proposition may cancel it!";

    @ConfigField(name="RealEstate.Errors.Sign.NotInClaim")
    public String msgErrorSignNotInClaim = "$cThe sign you placed is not inside a claim!";

    @ConfigField(name="RealEstate.Errors.Sign.OngoingTransaction")
    public String msgErrorSignOngoingTransaction = "$cThis claim already has an ongoing transaction!";

    @ConfigField(name="RealEstate.Errors.Sign.ParentOngoingTransaction")
    public String msgErrorSignParentOngoingTransaction = "$cThis claim's parent already has an ongoing transaction!";

    @ConfigField(name="RealEstate.Errors.Sign.SubclaimOngoingTransaction")
    public String msgErrorSignSubclaimOngoingTransaction = "$cThis claim has subclaims with ongoing transactions!";

    @ConfigField(name="RealEstate.Errors.Sign.SellingDisabled")
    public String msgErrorSignSellingDisabled = "$cSelling is disabled!";

    @ConfigField(name="RealEstate.Errors.Sign.LeasingDisabled")
    public String msgErrorSignLeasingDisabled = "$cLeasing is disabled!";

    @ConfigField(name="RealEstate.Errors.Sign.RentingDisabled")
    public String msgErrorSignRentingDisabled = "$cRenting is disabled!";

    @ConfigField(name="RealEstate.Errors.Sign.NoSellPermission", comment = "0: claim type")
    public String msgErrorSignNoSellPermission = "$cYou don't have permission to sell this {0}!";

    @ConfigField(name="RealEstate.Errors.Sign.NoLeasePermission", comment = "0: claim type")
    public String msgErrorSignNoLeasePermission = "$cYou don't have permission to lease this {0}!";

    @ConfigField(name="RealEstate.Errors.Sign.NoRentPermission", comment = "0: claim type")
    public String msgErrorSignNoRentPermission = "$cYou don't have permission to rent this {0}!";

    @ConfigField(name="RealEstate.Errors.Sign.NoAdminSellPermission", comment = "0: claim type")
    public String msgErrorSignNoAdminSellPermission = "$cYou don't have permission to sell this admin {0}!";

    @ConfigField(name="RealEstate.Errors.Sign.NoAdminLeasePermission", comment = "0: claim type")
    public String msgErrorSignNoAdminLeasePermission = "$cYou don't have permission to lease this admin {0}!";

    @ConfigField(name="RealEstate.Errors.Sign.NoAdminRentPermission", comment = "0: claim type")
    public String msgErrorSignNoAdminRentPermission = "$cYou don't have permission to rent this admin {0}!";

    @ConfigField(name="RealEstate.Errors.Sign.NotOwner", comment = "0: claim type")
    public String msgErrorSignNotOwner = "$cYou can only sell/rent/lease {0} you own!";

    @ConfigField(name="RealEstate.Errors.Sign.NotAuthor")
    public String msgErrorSignNotAuthor = "$cOnly the author of the sell/rent/lease sign is allowed to destroy it!";

    @ConfigField(name="RealEstate.Errors.Sign.NotAdmin")
    public String msgErrorSignNotAdmin = "$cOnly an admin is allowed to destroy this sign!";

    @ConfigField(name="RealEstate.Errors.Sign.NoTransaction")
    public String msgErrorSignNoTransaction = "$cThis claim is no longer for rent, sell or lease, sorry...";

    @ConfigField(name="RealEstate.Errors.Claim.DoesNotExist")
    public String msgErrorClaimDoesNotExist = "$cThis claim does not exist!";

    @ConfigField(name="RealEstate.Errors.Claim.AlreadyOwner", comment = "0: claim type")
    public String msgErrorClaimAlreadyOwner = "$cYou are already the owner of this {0}!";

    @ConfigField(name="RealEstate.Errors.Claim.NotSoldByOwner", comment = "0: claim type")
    public String msgErrorClaimNotSoldByOwner = "$cThis {0} is not sold by its owner!";

    @ConfigField(name="RealEstate.Errors.Claim.NotLeasedByOwner", comment = "0: claim type")
    public String msgErrorClaimNotLeasedByOwner = "$cThis {0} is not leased by its owner!";

    @ConfigField(name="RealEstate.Errors.Claim.NotRentedByOwner", comment = "0: claim type")
    public String msgErrorClaimNotRentedByOwner = "$cThis {0} is not rented by its owner!";

    @ConfigField(name="RealEstate.Errors.Claim.NoBuyPermission", comment = "0: claim type")
    public String msgErrorClaimNoBuyPermission = "$cYou don't have permission to buy this {0}!";

    @ConfigField(name="RealEstate.Errors.Claim.NoLeasePermission", comment = "0: claim type")
    public String msgErrorClaimNoLeasePermission = "$cYou don't have permission to lease this {0}!";

    @ConfigField(name="RealEstate.Errors.Claim.NoRentPermission", comment = "0: claim type")
    public String msgErrorClaimNoRentPermission = "$cYou don't have permission to rent this {0}!";

    @ConfigField(name="RealEstate.Errors.Claim.AlreadyLeased", comment = "0: claim type")
    public String msgErrorClaimAlreadyLeased = "$cThis {0} is already leased!";

    @ConfigField(name="RealEstate.Errors.Claim.AlreadyRented", comment = "0: claim type")
    public String msgErrorClaimAlreadyRented = "$cThis {0} is already rented!";

    @ConfigField(name="RealEstate.Errors.Claim.NoInfoPermission")
    public String msgErrorClaimNoInfoPermission = "$cYou don't have permission to view this real estate informations!";

    @ConfigField(name="RealEstate.Errors.Claim.NoClaimBlocks", comment = "0: area; 1: claim blocks remaining; 2: missing claim blocks")
    public String msgErrorClaimNoClaimBlocks = "$cYou don't have enough claim blocks! You need $a{2}$c more claim blocks to claim this area. The claim requires $a{0}$c claim blocks, you only have $a{1}$c claim blocks left.";

    @ConfigField(name="RealEstate.Info.ExitOffer.None")
    public String msgInfoExitOfferNone = "$bThere is currently no exit offer for this claim!";

    @ConfigField(name="RealEstate.Info.ExitOffer.MadeByStatus", comment = "0: formatted price")
    public String msgInfoExitOfferMadeByStatus = "$bYou offered to exit the contract for $a{0}$b, but your offer hasn't been accepted or denied yet...";

    @ConfigField(name="RealEstate.Info.ExitOffer.MadeToStatus", comment = "0: player who made the offer; 1: formatted price")
    public String msgInfoExitOfferMadeToStatus = "$a{0} $boffered to exit the contract for $a{1}";

    @ConfigField(name="RealEstate.Info.ExitOffer.Cancel", comment = "0: cancel command")
    public String msgInfoExitOfferCancel = "$bTo cancel your offer, use $d{0}";

    @ConfigField(name="RealEstate.Info.ExitOffer.Accept", comment = "0: accept command")
    public String msgInfoExitOfferAccept = "$bTo accept this offer, use $d{0}";

    @ConfigField(name="RealEstate.Info.ExitOffer.Reject", comment = "0: reject command")
    public String msgInfoExitOfferReject = "$bTo reject this offer, use $d{0}";

    @ConfigField(name="RealEstate.Info.ExitOffer.CreatedBySelf", comment = "0: formatted price")
    public String msgInfoExitOfferCreatedBySelf = "$bThe offer has been successfully created for $a{0}";

    @ConfigField(name="RealEstate.Info.ExitOffer.CreatedByOther", comment = "0: player name, 1: claim type, 2: formatted price, 3: claim location")
    public String msgInfoExitOfferCreatedByOther = "$a{0} $bhas created an offer to exit the transaction for the {1} at $a{3} $bfor $a{2}";

    @ConfigField(name="RealEstate.Info.ExitOffer.AcceptedBySelf", comment = "0: claim type, 1:formatted price")
    public String msgInfoExitOfferAcceptedBySelf = "$bThe {0} is no longer rented or leased, you have been charged $a{1}";

    @ConfigField(name="RealEstate.Info.ExitOffer.AcceptedByOther", comment = "0: player name, 1: claim type, 2: formatted price, 3: claim location")
    public String msgInfoExitOfferAcceptedByOther = "$a{0} $bhas accepted the offer to exit the transaction for the {1} at $a{3} $bfor $a{2}. It is no longer leased or rented.";

    @ConfigField(name="RealEstate.Info.ExitOffer.RejectedBySelf")
    public String msgInfoExitOfferRejectedBySelf = "$bThe exit offer has been refused.";

    @ConfigField(name="RealEstate.Info.ExitOffer.RejectedByOther", comment = "0: player name, 1: claim type, 2: claim location")
    public String msgInfoExitOfferRejectedByOther = "$a{0} $bhas refused the offer to exit the transaction for the {1} at $a{2}";

    @ConfigField(name="RealEstate.Info.ExitOffer.CancelledBySelf")
    public String msgInfoExitOfferCancelledBySelf = "$bThe exit offer has been cancelled.";

    @ConfigField(name="RealEstate.Info.ExitOffer.CancelledByOther", comment = "0: player name, 1: claim type, 2: claim location")
    public String msgInfoExitOfferCancelledByOther = "$a{0} $bhas cancelled the offer to exit the transaction for the {1} at $a{2}";

    @ConfigField(name="RealEstate.Info.Claim.OwnerSold", comment = "0: buyer name, 1: claim type, 2: formatted price, 3: claim location")
    public String msgInfoClaimOwnerSold = "$a{0} $bhas bought the {1} at $a{3} $bfor $a{2}";

    @ConfigField(name="RealEstate.Info.Claim.OwnerLeaseStarted", comment = "0: buyer name, 1: claim type, 2: formatted price, 3: claim location, 4: payments left")
    public String msgInfoClaimOwnerLeaseStarted = "$a{0} $bhas leased the {1} at $a{3} $bfor $a{2} with $a{4} $bpayments left";

    @ConfigField(name="RealEstate.Info.Claim.OwnerRented", comment = "0: buyer name, 1: claim type, 2: formatted price, 3: claim location")
    public String msgInfoClaimOwnerRented = "$a{0} $bhas rented the {1} at $a{3} $bfor $a{2}";

    @ConfigField(name="RealEstate.Info.Claim.BuyerBought", comment = "0: claim type, 1: formatted price")
    public String msgInfoClaimBuyerSold = "$bYou have bought the {0} for $a{1}";

    @ConfigField(name="RealEstate.Info.Claim.BuyerLeaseStarted", comment = "0: claim type, 1: formatted price, 2: payments left")
    public String msgInfoClaimBuyerLeaseStarted = "$bYou have leased the {0} for $a{1} with $a{2} $bpayments left";

    @ConfigField(name="RealEstate.Info.Claim.BuyerRented", comment = "0: claim type, 1: formatted price")
    public String msgInfoClaimBuyerRented = "$bYou have rented the {0} for $a{1}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.Header")
    public String msgInfoClaimInfoLeaseHeader = "$9-----= $f[$6RealEstate Lease Info$f]$9 =-----";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.GeneralNoBuyer", comment = "0: claim type, 1: payments left, 2: formatted price, 3: frequency")
    public String msgInfoClaimInfoGeneralLeaseNoBuyer = "$bThis {0} is for lease for $a{1} $bpayments of $a{2} each. Payments are due every $a{3}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.GeneralBuyer", comment = "0: claim type, 1: buyer name, 2: formatted price, 3: payments left, 4: next payment due, 5: frequency")
    public String msgInfoClaimInfoGeneralLeaseBuyer = "$bThis {0} is currently leased by $a{1}$b for $a{2}$b. There is $a{3} $bpayments left. Next payment is in $a{4}$b. Payments are due every $a{5}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.Oneline", comment = "0: claim area, 1: location, 2: payments left, 3: period, 4: formatted price")
    public String msgInfoClaimInfoLeaseOneline = "$2{0} $bblocks to $2Lease $bat $2{1} $bfor $a{2} periods of $a{3}$b, each period costs $a{4}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.PaymentBuyer", comment = "0: claim type, 1: location, 2: formatted price, 3: payments left")
    public String msgInfoClaimInfoLeasePaymentBuyer = "$bPaid lease for the {0} at $a{1} $bfor $a{2}$b. There are $a{3} $bpayments left.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.PaymentOwner", comment = "0: player name, 1: claim type, 2: location, 3: formatted price, 4: payments left")
    public String msgInfoClaimInfoLeasePaymentOwner = "$a{0} $bpaid lease for the {1} at $a{2} $bfor $a{3}$b. There are $a{4} $bpayments left.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.PaymentBuyerFinal", comment = "0: claim type, 1: location, 2: formatted price")
    public String msgInfoClaimInfoLeasePaymentBuyerFinal = "$bPaid final lease for the {0} at $a{1} $bfor $a{2}$b. The {0} is now your property.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.PaymentOwnerFinal", comment = "0: player name, 1: claim type, 2: location, 3: formatted price")
    public String msgInfoClaimInfoLeasePaymentOwnerFinal = "$a{0} $bpaid final lease for the {1} at $a{2} $bfor $a{3}$b. The {1} is now $a{0}$b's property.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.PaymentBuyerCancelled", comment = "0: claim type, 1: location, 2: formatted price")
    public String msgInfoClaimInfoLeasePaymentBuyerCancelled = "$bCouldn't pay the lease for the {0} at $a{1} $bfor $a{2}$b. The lease has been cancelled.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.PaymentOwnerCancelled", comment = "0: player name, 1: claim type, 2: location, 3: formatted price")
    public String msgInfoClaimInfoLeasePaymentOwnerCancelled = "$a{0} $bcouldn't pay the lease for the {1} at $a{2} $bfor $a{3}$b. The lease has been cancelled.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.Header")
    public String msgInfoClaimInfoRentHeader = "$9-----= $f[$6RealEstate Rent Info$f]$9 =-----";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.GeneralNoBuyer", comment = "0: claim type, 1: formatted price, 2: duration")
    public String msgInfoClaimInfoGeneralRentNoBuyer = "$bThis {0} is for rent for $a{1}$b per $a{2}$b.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.GeneralBuyer", comment = "0: claim type, 1: buyer name, 2: formatted price, 3: time left in current period, 4: duration of a period")
    public String msgInfoClaimInfoGeneralRentBuyer = "$bThis {0} is currently rented by $a{1}$b for $a{2}$b. The {0} is rented for another $a{3}$b. The rent period is $a{4}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.AutoRenew", comment = "0: enabled / disabled")
    public String msgInfoClaimInfoRentAutoRenew = "$bAutomatic renew is currently $a{0}$b.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.Oneline", comment = "0: claim area, 1: location, 2: formatted price, 3: duration")
    public String msgInfoClaimInfoRentOneline = "$2{0} $bblocks to $2Rent $bat $2{1} $bfor $a{2}$b per $a{3}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.PaymentBuyer", comment = "0: claim type, 1: location, 2: formatted price")
    public String msgInfoClaimInfoRentPaymentBuyer = "$bPaid rent for the {0} at $a{1} $bfor $a{2}$b.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.PaymentOwner", comment = "0: player name, 1: claim type, 2: location, 3: formatted price")
    public String msgInfoClaimInfoRentPaymentOwner = "$a{0} $bpaid rent for the {1} at $a{2} $bfor $a{3}$b.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.PaymentBuyerCancelled", comment = "0: claim type, 1: location, 2: formatted price")
    public String msgInfoClaimInfoRentPaymentBuyerCancelled = "$bCouldn't pay the rent for the {0} at $a{1} $bfor $a{2}$b. The rent has been cancelled.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.PaymentOwnerCancelled", comment = "0: player name, 1: claim type, 2: location, 3: formatted price")
    public String msgInfoClaimInfoRentPaymentOwnerCancelled = "$a{0} $bcouldn't pay the rent for the {1} at $a{2} $bfor $a{3}$b. The rent has been cancelled.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.RentCancelled", comment = "0: claim type, 1: location")
    public String msgInfoClaimInfoRentCancelled = "$bThe rent for the {0} at $a{1} $bis now over, your access has been revoked.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Sell.Header")
    public String msgInfoClaimInfoSellHeader = "$9-----= $f[$6RealEstate Sale Info$f]$9 =-----";

    @ConfigField(name="RealEstate.Info.Claim.Info.Sell.General", comment = "0: claim type, 1: formatted price")
    public String msgInfoClaimInfoSellGeneral = "$bThis {0} is for sale for $a{1}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Sell.Oneline", comment = "0: claim area, 1: location, 2: formatted price")
    public String msgInfoClaimInfoSellOneline = "$2{0} $bblocks to $2Sell $bat $2{1} $bfor $a{2}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Owner", comment = "0: owner name")
    public String msgInfoClaimInfoOwner = "$bThe current owner is $a{0}";

    @ConfigField(name="RealEstate.Info.Claim.Info.MainOwner", comment = "0: owner name")
    public String msgInfoClaimInfoMainOwner = "$bThe main claim's owner is $a{0}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Note")
    public String msgInfoClaimInfoNote = "$dNote: You will only get access to this subclaim.";

    @ConfigField(name="RealEstate.Info.Claim.Created.Sell", comment = "0: claim prefix, 1: claim type, 2: formatted price")
    public String msgInfoClaimCreatedSell = "$bYou have successfully created {0} {1} sale for $a{2}";

    @ConfigField(name="RealEstate.Info.Claim.Created.Lease", comment = "0: claim prefix, 1: claim type, 2: formatted price, 3: payments count, 4: frequency")
    public String msgInfoClaimCreatedLease = "$bYou have successfully created {0} {1} lease for $a{3}$b payments of $a{2}$b each. Payments are due every $a{4}";

    @ConfigField(name="RealEstate.Info.Claim.Created.Rent", comment = "0: claim prefix, 1: claim type, 2: formatted price, 3: duration")
    public String msgInfoClaimCreatedRent = "$bYou have successfully created {0} {1} rent for $a{2}$b per $a{3}";

    @ConfigField(name="RealEstate.Info.Claim.Created.SellBroadcast", comment = "0: player name, 1: claim prefix, 2: claim type, 3: formatted price")
    public String msgInfoClaimCreatedSellBroadcast = "$a{0} $bhas created {1} {2} sale for $a{3}";

    @ConfigField(name="RealEstate.Info.Claim.Created.LeaseBroadcast", comment = "0: player name, 1: claim prefix, 2: claim type, 3: formatted price, 4: payments count, 5: frequency")
    public String msgInfoClaimCreatedLeaseBroadcast = "$a{0} $bhas created {1} {2} lease for $a{4}$b payments of $a{3}$b each. Payments are due every $a{5}";

    @ConfigField(name="RealEstate.Info.Claim.Created.RentBroadcast", comment = "0: player name, 1: claim prefix, 2: claim type, 3: formatted price, 4: duration")
    public String msgInfoClaimCreatedRentBroadcast = "$a{0} $bhas created {1} {2} rent for $a{3}$b per $a{4}";

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

    public static String getMessage(String msgTemplate, String... args) {
        return getMessage(msgTemplate, true, args);
    }

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
    //sends a color-coded message to a player
    public static void sendMessage(CommandSender player, String msgTemplate, String... args) {
        sendMessage(player, msgTemplate, 0, args);
    }

    //sends a color-coded message to a player
    public static void sendMessage(CommandSender player, String msgTemplate, long delayInTicks, String... args) {
        String message = getMessage(msgTemplate, args);
        sendMessage(player, message, delayInTicks);
    }

    //sends a color-coded message to a player
    public static void sendMessage(CommandSender player, String message) {
        sendMessage(player, getMessage(message), 0);
    }

    //sends a color-coded message to a player
    public static void sendMessage(CommandSender player, String message, Boolean fixColors) {
        sendMessage(player, fixColors ? getMessage(message) : message, 0);
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
