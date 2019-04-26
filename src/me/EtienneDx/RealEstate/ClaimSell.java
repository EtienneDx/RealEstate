package me.EtienneDx.RealEstate;

import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;

public class ClaimSell extends ClaimTransaction
{
	public ClaimSell(Claim claim, Player player, double price, Location sign)
	{
		super(claim, player, price, sign);
	}

	@Override
	public void update()
	{
		if(sign.getBlock().getState() instanceof Sign)
		{
			Sign s = (Sign) sign.getBlock().getState();
			s.setLine(0, RealEstate.instance.dataStore.cfgSignsHeader);
			s.setLine(1, ChatColor.DARK_GREEN + RealEstate.instance.dataStore.cfgReplaceSell);
			s.setLine(2, owner != null ? Bukkit.getOfflinePlayer(owner).getName() : "SERVER");
			s.setLine(3, price + " " + RealEstate.econ.currencyNamePlural());
			s.update(true);
		}
		else
		{
			RealEstate.transactionsStore.cancelTransaction(this);
		}
	}

	@Override
	public void interact(Player player)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);// getting by id creates errors for subclaims
		if(claim == null)
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "This claim does not exist!");
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		String claimType = claim.parent == null ? "claim" : "subclaim";
		
		if (owner.equals(player.getUniqueId()))
        {
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You already own this " + claimType + "!");
            return;
        }
		if(claim.parent == null && !owner.equals(claim.ownerID))
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + Bukkit.getPlayer(owner).getDisplayName() + 
            		" does not have the right to sell this " + claimType + "!");
            RealEstate.transactionsStore.cancelTransaction(claim);
            return;
		}
		if(!player.hasPermission("realestate." + claimType + ".buy"))
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You do not have the permission to purchase " + 
            		claimType + "s!");
            return;
		}
		// for real claims, you may need to have enough claim blocks in reserve to purchase it (if transferClaimBlocks is false)
		if(claimType.equalsIgnoreCase("claim") && !RealEstate.instance.dataStore.cfgTransferClaimBlocks && 
				GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getRemainingClaimBlocks() < claim.getArea())
		{
            player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + 
            		"You don't have enough claim blocks to purchase this claim, you need to get " + ChatColor.DARK_GREEN + 
            		(claim.getArea() - GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getRemainingClaimBlocks()) + 
            		ChatColor.RED + " more blocks!");
            return;			
		}
		// the player has the right to buy, let's make the payment
		
		if(Utils.makePayment(owner, player.getUniqueId(), price, false, true))// if payment succeed
		{
			// blocks transfer :
			// if transfert is true, the seller will lose the blocks he had
			// and the buyer will get them
			// (that means the buyer will keep the same amount of remaining blocks after the transaction)
			if(claimType.equalsIgnoreCase("claim") && RealEstate.instance.dataStore.cfgTransferClaimBlocks)
			{
				PlayerData buyerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
				PlayerData sellerData = GriefPrevention.instance.dataStore.getPlayerData(owner);
				
				// the seller has to provide the blocks
				sellerData.setBonusClaimBlocks(sellerData.getBonusClaimBlocks() - claim.getArea());
				if (sellerData.getBonusClaimBlocks() < 0)// can't have negative bonus claim blocks, so if need be, we take into the accrued 
		        {
		            sellerData.setAccruedClaimBlocks(sellerData.getAccruedClaimBlocks() + sellerData.getBonusClaimBlocks());
		            sellerData.setBonusClaimBlocks(0);
		        }
				
				// the buyer receive them
				buyerData.setBonusClaimBlocks(buyerData.getBonusClaimBlocks() + claim.getArea());
			}
			
			// start to change owner
			if(claim.parent == null)
				for(Claim child : claim.children)
				{
					child.clearPermissions();
					child.managers.clear();
				}
			claim.clearPermissions();
			
			try
			{
				if(claim.parent == null)
					GriefPrevention.instance.dataStore.changeClaimOwner(claim, player.getUniqueId());
				else
				{
					claim.setPermission(player.getUniqueId().toString(), ClaimPermission.Build);
				}
			}
			catch (Exception e)// error occurs when trying to change subclaim owner
			{
				e.printStackTrace();
				return;
			}
			GriefPrevention.instance.dataStore.saveClaim(claim);
			
			if(claim.parent != null || claim.ownerID.equals(player.getUniqueId()))
			{
				player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + "You have successfully purchased this " + claimType + 
						" for " + ChatColor.GREEN + price + RealEstate.econ.currencyNamePlural());
                RealEstate.instance.addLogEntry(
                        "[" + RealEstate.transactionsStore.dateFormat.format(RealEstate.transactionsStore.date) + "] " + player.getName() + 
                        " has purchased a " + claimType + " at " +
                                "[" + player.getLocation().getWorld() + ", " +
                                "X: " + player.getLocation().getBlockX() + ", " +
                                "Y: " + player.getLocation().getBlockY() + ", " +
                                "Z: " + player.getLocation().getBlockZ() + "] " +
                                "Price: " + price + " " + RealEstate.econ.currencyNamePlural());
                
                if(RealEstate.instance.dataStore.cfgMessageOwner)
                {
                	OfflinePlayer oldOwner = Bukkit.getOfflinePlayer(owner);
                	if(oldOwner.isOnline())
                	{
                		((Player) oldOwner).sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.AQUA + player.getDisplayName() + 
                				" has purchased your " + claimType + " at " +
                                "[" + player.getLocation().getWorld().getName() + ", " +
                                "X: " + player.getLocation().getBlockX() + ", " +
                                "Y: " + player.getLocation().getBlockY() + ", " +
                                "Z: " + player.getLocation().getBlockZ() + "] for " +
                                price + " " + RealEstate.econ.currencyNamePlural());
                	}
                }
			}
            else
            {
                player.sendMessage(RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "Cannot purchase claim!");
                return;
            }
			RealEstate.transactionsStore.cancelTransaction(claim);
		}
	}
	
	@Override
	public void preview(Player player)
	{
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(sign, false, null);
		String msg = "";
		if(player.hasPermission("realestate.info"))
		{
			String claimType = claim.parent == null ? "claim" : "subclaim";
			msg = ChatColor.BLUE + "-----= " + ChatColor.WHITE + "[" + ChatColor.GOLD + "RealEstate Sale Info" + ChatColor.WHITE + "]" + 
					ChatColor.BLUE + " =-----\n";
			msg += ChatColor.AQUA + "This " + claimType + " is for sale for " +
					ChatColor.GREEN + price + " " + RealEstate.econ.currencyNamePlural() + "\n";
			if(claimType.equalsIgnoreCase("claim"))
			{
				msg += ChatColor.AQUA + "The current owner is: " + ChatColor.GREEN + claim.getOwnerName();
            }
            else
            {
            	msg += ChatColor.AQUA + "The main claim owner is: " + ChatColor.GREEN + claim.getOwnerName() + "\n";
            	msg += ChatColor.LIGHT_PURPLE + "Note: " + ChatColor.AQUA + "You will only buy access to this subclaim!";
            }
		}
		else
		{
			msg = RealEstate.instance.dataStore.chatPrefix + ChatColor.RED + "You don't have the permission to view real estate informations!";
		}
		player.sendMessage(msg);
	}
}
