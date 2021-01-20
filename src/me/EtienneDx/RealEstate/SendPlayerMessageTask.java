package me.EtienneDx.RealEstate;

import org.bukkit.command.CommandSender;

class SendPlayerMessageTask implements Runnable
{
	private CommandSender player;
	private String message;


	public SendPlayerMessageTask(CommandSender player, String message)
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
