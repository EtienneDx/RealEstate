package me.EtienneDx.RealEstate;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class REListener implements Listener
{
	void registerEvents()
	{
		PluginManager pm = RealEstate.instance.getServer().getPluginManager();
		
		pm.registerEvents(this, RealEstate.instance);
	}
}
