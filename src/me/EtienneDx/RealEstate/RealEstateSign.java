package me.EtienneDx.RealEstate;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.ChatColor;

/**
 * A utility wrapper for a Bukkit {@link Sign} used by the RealEstate plugin.
 * <p>
 * This class provides methods to set sign lines, update the sign, and check whether
 * a given sign is a RealEstate sign (by comparing its header).
 * </p>
 */
public class RealEstateSign 
{
    private Sign sign;
    private SignSide front;

    /**
     * Constructs a new RealEstateSign wrapper for the provided sign.
     *
     * @param sign the sign to wrap
     */
    public RealEstateSign(Sign sign) {
        this.sign = sign;
        this.front = sign.getSide(Side.FRONT);
    }

    /**
     * Sets the text on the specified line of the sign.
     *
     * @param line  the line index to set (typically 0-3)
     * @param value the text value to display on the line
     */
    public void setLine(int line, String value) {
        front.setLine(line, value);
    }

    /**
     * Updates the sign state.
     *
     * @param force if true, forces the update even if the sign is not changed
     */
    public void update(boolean force) {
        sign.update(force);
    }

    /**
     * Checks if this sign is a RealEstate sign.
     * <p>
     * It compares the header text of the sign with the expected header defined in the configuration.
     * </p>
     *
     * @return true if the sign's header matches the RealEstate header; false otherwise
     */
    public boolean isRealEstateSign() {
        String header = ChatColor.stripColor(Messages.getMessage(RealEstate.instance.config.cfgSignsHeader, false));

        SignSide front = sign.getSide(Side.FRONT);

        if(ChatColor.stripColor(front.getLine(0)).equalsIgnoreCase(header)) {
                return true;
        }
        return false;
    }
}
