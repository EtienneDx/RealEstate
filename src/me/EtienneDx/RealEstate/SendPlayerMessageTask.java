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
        if (message == null || message.length() == 0) return;

        if (player == null) {
            RealEstate.instance.log.info(message);
        } else {
            player.sendMessage(message);
        }
	}
}
