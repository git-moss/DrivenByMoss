// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * The interface to a marker (on a timeline).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMarker extends IItem
{
    /**
     * Get the color of the marker.
     *
     * @return The color in RGB
     */
    double [] getColor ();


    /**
     * Launches the marker.
     * 
     * @param quantized Specified if the marker should be launched quantized or immediately
     */
    void launch (boolean quantized);


    /**
     * Removes the marker.
     */
    void removeMarker ();
}