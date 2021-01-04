// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IDrumPad;


/**
 * Interface to a drumpad bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IDrumPadBank extends IChannelBank<IDrumPad>
{
    /**
     * Clears mute on all drum pads.
     */
    void clearMute ();


    /**
     * Clears solo on all drum pads.
     */
    void clearSolo ();


    /**
     * Check if a pad of the drum pad bank is soloed.
     *
     * @return True if a pad is soloed
     */
    boolean hasSoloedPads ();
}