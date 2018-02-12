// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.ObserverManagement;


/**
 * The intrerface to a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IChannel extends ObserverManagement
{
    /**
     * Get the index of the channel in the current bank page.
     *
     * @return The index of the channel in the current bank page
     */
    int getIndex ();


    /**
     * True if the channel is selected.
     *
     * @return True if the channel is selected.
     */
    boolean isSelected ();


    /**
     * Set the selected state of the channel.
     *
     * @param isSelected True if the channel is selected
     */
    void setSelected (boolean isSelected);


    /**
     * Returns true if the channel exits.
     *
     * @return True if the channel exits.
     */
    boolean doesExist ();


    /**
     * Returns true if the channel is activated.
     *
     * @return True if the channel is activated
     */
    boolean isActivated ();


    /**
     * Get the name of the channel.
     *
     * @return The name of the channel
     */
    String getName ();


    /**
     * Get the volume as a formatted text.
     *
     * @return The volume text
     */
    String getVolumeStr ();


    /**
     * Get the volume as a formatted text.
     *
     * @param limit Limit the text to this length
     * @return The volume text
     */
    String getVolumeStr (int limit);


    /**
     * Get the volume.
     *
     * @return The volume
     */
    int getVolume ();


    /**
     * Get the modulated volume.
     *
     * @return The modulated volume
     */
    int getModulatedVolume ();


    /**
     * Get the panorama as a formatted text
     *
     * @return The panorama text
     */
    String getPanStr ();


    /**
     * Get the panorama as a formatted text
     *
     * @param limit Limit the text to this length
     * @return The panorama text
     */
    String getPanStr (int limit);


    /**
     * Get the panorama.
     *
     * @return The panorama
     */
    int getPan ();


    /**
     * Get the modulated panorama.
     *
     * @return The modulated panorama
     */
    int getModulatedPan ();


    /**
     * Get the color of the channel.
     *
     * @return The color in RGB
     */
    double [] getColor ();


    /**
     * True if muted.
     *
     * @return True if muted.
     */
    boolean isMute ();


    /**
     * True if soloed.
     *
     * @return True if soloed.
     */
    boolean isSolo ();


    /**
     * Get the VU value.
     *
     * @return The VU value
     */
    int getVu ();


    /**
     * Get the sends of the channel.
     *
     * @return The sends
     */
    ISend [] getSends ();
}