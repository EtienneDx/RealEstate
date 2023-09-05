package me.EtienneDx.RealEstate;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.ChatColor;

public class RealEstateSign 
{
	private Sign sign;
	private SignSide front;
	private SignSide back;

	public RealEstateSign(Sign sign) {
		this.sign = sign;
		this.front = sign.getSide(Side.FRONT);
		this.back = sign.getSide(Side.BACK);
	}

	public void setLine(int line, String value) {
		front.setLine(line, value);
		back.setLine(line, value);
	}

	public void update(boolean force) {
		sign.update(force);
	}

	public boolean isRealEstateSign() {
		String header = ChatColor.stripColor(Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));

		SignSide front = sign.getSide(Side.FRONT);
		SignSide back = sign.getSide(Side.BACK);

		if(ChatColor.stripColor(front.getLine(0)).equalsIgnoreCase(header)) {
				return true;
		}

		if(ChatColor.stripColor(back.getLine(0)).equalsIgnoreCase(header)) {
				return true;
		}

		return false;
	}

}