package me.EtienneDx.RealEstate;

import org.bukkit.entity.Player;

class SendPlayerMessageTask implements Runnable
{
	private Player player;
	private String message;


	public SendPlayerMessageTask(Player player, String message)
	{
		this.player = player;
		this.message = message;
	}

	@Override
	public void run()
	{
		if(player == null)
		{
			RealEstate.instance.log.info(message);
		    return;
		}
		Messages.sendMessage(this.player, this.message);
	}
}
