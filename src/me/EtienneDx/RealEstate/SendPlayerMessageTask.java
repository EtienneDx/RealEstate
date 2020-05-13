package me.EtienneDx.RealEstate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

class SendPlayerMessageTask implements Runnable
{
	private Player player;
	private ChatColor color;
	private String message;


	public SendPlayerMessageTask(Player player, ChatColor color, String message)
	{
		this.player = player;
		this.color = color;
		this.message = message;
	}

	@Override
	public void run()
	{
		if(player == null)
		{
			RealEstate.instance.log.info(color + message);
		    return;
		}
		Config.sendMessage(this.player, this.color, this.message);
	}
}
