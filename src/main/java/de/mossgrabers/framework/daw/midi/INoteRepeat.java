// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Implementation for a note repeat.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface INoteRepeat
{
    /**
     * Toggle if note repeat is active.
     */
    void toggleActive ();


    /**
     * True if note repeat is enabled.
     *
     * @return True if note repeat is enabled
     */
    boolean isActive ();


    /**
     * Set the note repeat length.
     *
     * @param length The length
     */
    void setPeriod (double length);


    /**
     * Get the note repeat length.
     *
     * @return The length
     */
    double getPeriod ();
}
