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

    @ConfigField(name="RealEstate.Keywords.Enabled", comment = "Keywords used within other messages")
    public String keywordEnabled = "enabled";

    @ConfigField(name="RealEstate.Keywords.Disabled")
    public String keywordDisabled = "disabled";

    @ConfigField(name="RealEstate.Keywords.Claim")
    public String keywordClaim = "claim";

    @ConfigField(name="RealEstate.Keywords.Subclaim")
    public String keywordSubclaim = "subclaim";

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
    
    @ConfigField(name="RealEstate.Errors.Command.Usage", comment = "0: command usage")
    public String msgErrorCommandUsage = "$cUsage: {0}";

    @ConfigField(name="RealEstate.Errors.BuyerOnly")
    public String msgErrorBuyerOnly = "$cOnly the buyer can perform this command!";

    @ConfigField(name="RealEstate.Errors.InvalidNumber", comment = "0: number")
    public String msgErrorInvalidNumber = "$c{0} is not a valid number!";

    @ConfigField(name="RealEstate.Errors.NegativeNumber", comment = "0: number")
    public String msgErrorNegativeNumber = "$c{0} is a negative number!";

    @ConfigField(name="RealEstate.Errors.NegativePrice", comment = "0: price")
    public String msgErrorNegativePrice = "$cThe price must be greater than zero!";

    @ConfigField(name="RealEstate.Errors.NonIntegerPrice", comment = "0: price")
    public String msgErrorNonIntegerPrice = "$cThe price must be an integer!";

    @ConfigField(name="RealEstate.Errors.InvalidDuration", comment = "0: duration, 1: example of duration format, 2: example, 3: example")
    public String msgErrorInvalidDuration = "$c{0} is not a valid duration! Durations must be in the format {1} or {2} or {3}!";

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

    @ConfigField(name="RealEstate.Info.ExitOffer.None")
    public String msgInfoExitOfferNone = "$bThere is currently no exit offer for this claim!";

    @ConfigField(name="RealEstate.Info.ExitOffer.MadeByStatus", comment = "0: formatted price")
    public String msgInfoExitOfferMadeByStatus = "$bYou offered to exit the contract for $a{0}, but your offer hasn't been accepted or denied yet...";

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

    @ConfigField(name="RealEstate.Info.Claim.BuyerBought", comment = "1: claim type, 2: formatted price")
    public String msgInfoClaimBuyerSold = "$bYou have bought the {1} for $a{2}";

    @ConfigField(name="RealEstate.Info.Claim.BuyerLeaseStarted", comment = "1: claim type, 2: formatted price, 3: payments left")
    public String msgInfoClaimBuyerLeaseStarted = "$bYou have leased the {1} for $a{2} with $a{3} $bpayments left";

    @ConfigField(name="RealEstate.Info.Claim.BuyerRented", comment = "1: claim type, 2: formatted price")
    public String msgInfoClaimBuyerRented = "$bYou have rented the {1} for $a{2}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.Header")
    public String msgInfoClaimInfoLeaseHeader = "$9-----= $f[$6RealEstate Lease Info$f]$9 =-----";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.GeneralNoBuyer", comment = "0: claim type, 1: payments left, 2: formatted price, 3: frequency")
    public String msgInfoClaimInfoGeneralLeaseNoBuyer = "$bThis {0} is for lease for $a{1} $bpayments of $a{2} each. Payments are due every $a{3}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.GeneralBuyer", comment = "0: claim type, 1: buyer name, 2: formatted price, 3: payments left, 4: next payment due, 5: frequency")
    public String msgInfoClaimInfoGeneralLeaseBuyer = "$bThis {0} is currently leased by $a{1}$b for $a{2}$b. There is $a{3} $bpayments left. Next payment is in $a{4}$b. Payments are due every $a{5}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Lease.Oneline", comment = "0: claim area, 1: location, 2: payments left, 3: period, 4: formatted price")
    public String msgInfoClaimInfoLeaseOneline = "$2{0} $bblocks to $2Lease $bat $2{1} $bfor $a{2} periods of $a{3}$b, each period costs $a{4}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.Header")
    public String msgInfoClaimInfoRentHeader = "$9-----= $f[$6RealEstate Rent Info$f]$9 =-----";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.GeneralNoBuyer", comment = "0: claim type, 1: formatted price, 2: duration")
    public String msgInfoClaimInfoGeneralRentNoBuyer = "$bThis {0} is for rent for $a{1}$b per $a{3}.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.GeneralBuyer", comment = "0: claim type, 1: buyer name, 2: formatted price, 3: time left in current period, 5: duration of a period")
    public String msgInfoClaimInfoGeneralRentBuyer = "$bThis {0} is currently rented by $a{1}$b for $a{2}$b. The {0} is rented until $a{3}$b. The rent period is $a{5}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.MaxPeriod", comment = "0: max periods")
    public String msgInfoClaimInfoRentMaxPeriod = "$bIt can be rented for a maximum of $a{0}$b periods.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.RemainingPeriods", comment = "0: periods left")
    public String msgInfoClaimInfoRentRemainingPeriods = "$bThe contract will end after another $a{0}$b periods.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.AutoRenew", comment = "0: enabled / disabled")
    public String msgInfoClaimInfoRentAutoRenew = "$bAutomatic renew is currently $a{0}$b.";

    @ConfigField(name="RealEstate.Info.Claim.Info.Rent.Oneline", comment = "0: claim area, 1: location, 2: formatted price, 3: duration")
    public String msgInfoClaimInfoRentOneline = "$2{0} $bblocks to $2Rent $bat $2{1} $bfor $a{2}$b per $a{3}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Owner", comment = "0: owner name")
    public String msgInfoClaimInfoOwner = "$bThe current owner is $a{0}";

    @ConfigField(name="RealEstate.Info.Claim.Info.MainOwner", comment = "0: owner name")
    public String msgInfoClaimInfoMainOwner = "$bThe main claim's owner is $a{0}";

    @ConfigField(name="RealEstate.Info.Claim.Info.Note")
    public String msgInfoClaimInfoNote = "$dNote: You will only get access to this subclaim.";

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
        String message = getMessage(msgTemplate, args);
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
