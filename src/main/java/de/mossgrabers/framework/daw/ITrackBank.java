// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Interface to a track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ITrackBank extends IChannelBank
{
    /**
     * Selects the first child if this is a group track.
     */
    void selectChildren ();


    /**
     * Selects the parent track if any (track must be inside a group).
     */
    void selectParent ();


    /**
     * Returns true if there is a parent track.
     *
     * @return True if there is a parent track
     */
    boolean hasParent ();
}