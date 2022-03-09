package me.EtienneDx.RealEstate.Transactions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.EtienneDx.RealEstate.Messages;
import me.EtienneDx.RealEstate.RealEstate;
import me.EtienneDx.RealEstate.Utils;
import me.EtienneDx.RealEstate.ClaimAPI.IClaim;
import net.md_5.bungee.api.ChatColor;

public class ClaimAuction extends ClaimTransaction {

    public UUID buyer = null;
	public LocalDateTime endDate = null;
    public double bidStep = 1;

    public ClaimAuction(Map<String, Object> map) {
        super(map);
		if(map.get("buyer") != null)
			buyer = UUID.fromString((String)map.get("buyer"));
        if(map.get("startDate") != null)
			endDate = LocalDateTime.parse((String) map.get("endDate"), DateTimeFormatter.ISO_DATE_TIME);
        bidStep = (double) map.get("bidStep");
    }

    public ClaimAuction(IClaim claim, Player player, double price, Location sign, LocalDateTime endDate, double bidStep) {
        super(claim, player, price, sign);
        this.endDate = endDate;
        this.bidStep = bidStep;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
		if(buyer != null)
			map.put("buyer", buyer.toString());
        if(endDate != null)
			map.put("endDate", endDate.format(DateTimeFormatter.ISO_DATE_TIME));
        map.put("bidStep", bidStep);
		return map;
    }

    @Override
    public boolean update() {
        int days = Period.between(endDate.toLocalDate(), LocalDate.now()).getDays();
        Duration hours = Duration.between(endDate.toLocalTime(), LocalTime.now());
        if(hours.isNegative() && !hours.isZero())
        {
            hours = hours.plusHours(24);
            days--;
        }
        if(days < 0)
        {
            // TODO finish auction
            return true;
        }
        else
        {
            // update sign
            if(sign.getBlock().getState() instanceof Sign)
            {
                Sign s = (Sign) sign.getBlock().getState();
                s.setLine(0, Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));
                s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.config.cfgReplaceAuction);
                String remaining = Utils.getTime(days, hours, false);
                s.setLine(2, Messages.getMessage(RealEstate.instance.messages.msgSignAuctionRemainingTime, false, remaining));
                if(buyer != null)
                {
                    String name = Bukkit.getOfflinePlayer(buyer).getName();
                    s.setLine(3, Messages.getMessage(RealEstate.instance.messages.msgSignAuctionHighestBidder, false, name, RealEstate.econ.format(price)));
                }
                else
                {
                    s.setLine(3, Messages.getMessage(RealEstate.instance.messages.msgSignAuctionNoBider, false));
                }
                s.update(true);
            }
            else
            {
                RealEstate.transactionsStore.cancelTransaction(this);
            }
            return false;
        }
    }

    @Override
    public boolean tryCancelTransaction(Player p, boolean force) {
        if(buyer == null || force || p.hasPermission("realestate.admin"))
        {
            RealEstate.transactionsStore.cancelTransaction(this);
            return true;
        }
        else if(p != null)
        {
            p.sendMessage(Messages.getMessage(RealEstate.instance.messages.msgErrorCantCancelAuction));
        }
        return false;
    }

    @Override
    public void interact(Player player) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign);// getting by id creates errors for subclaims
		if(claim == null || claim.isWilderness())
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimDoesNotExist);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		String claimTypeDisplay = claim.isParentClaim() ? 
			RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;
		
        if (owner != null && owner.equals(player.getUniqueId()))
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyOwner, claimTypeDisplay);
            return;
        }
        if(claim.isParentClaim() && owner != null && !owner.equals(claim.getOwner()))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNotAuctionedByOwner, claimTypeDisplay);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
        bid(player, price + bidStep);
    }

    public void bid(Player player, double price) {
        IClaim claim = RealEstate.claimAPI.getClaimAt(sign);// getting by id creates errors for subclaims
		if(claim == null || claim.isWilderness())
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimDoesNotExist);
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
        String claimType = claim.isParentClaim() ? "claim" : "subclaim";
		String claimTypeDisplay = claim.isParentClaim() ? 
			RealEstate.instance.messages.keywordClaim : RealEstate.instance.messages.keywordSubclaim;

		if(!player.hasPermission("realestate." + claimType + ".bid"))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimNoAuctionPermission, claimTypeDisplay);
            return;
		}
		if(RealEstate.instance.config.cfgDisableOutbidSelf && player.getUniqueId().equals(buyer))
		{
			Messages.sendMessage(player, RealEstate.instance.messages.msgErrorClaimAlreadyHighestBidder, claimTypeDisplay);
            return;
		}
        if(RealEstate.econ.getBalance(player) < price)
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNoMoneySelf, RealEstate.econ.format(price));
            return;
        }
        if(!Utils.makePayment(null, player.getUniqueId(), price, false, false))
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorNoMoneySelf, RealEstate.econ.format(price));
            return;
        }
        if(buyer != null && !Utils.makePayment(buyer, null, this.price, false, false))
        {
            Messages.sendMessage(player, RealEstate.instance.messages.msgErrorCouldntReimburseOther, RealEstate.econ.format(this.price));
            
            if(!Utils.makePayment(player.getUniqueId(), null, price, false, false))
            {
                RealEstate.instance.log.warning("Couldn't reimburse " + player.getName() + " for " + RealEstate.econ.format(price) + ", the money has been lost!");
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorCouldntReimburseSelf, RealEstate.econ.format(price));
                Messages.sendMessage(player, RealEstate.instance.messages.msgErrorContactAdmin);
            }
            
            return;
        }
        buyer = player.getUniqueId();
        this.price = price;
    }

    @Override
    public void preview(Player player) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void msgInfo(CommandSender cs) {
        // TODO Auto-generated method stub
        
    }
}
