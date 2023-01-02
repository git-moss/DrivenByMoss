// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

import de.mossgrabers.framework.observer.IObserverManagement;


/**
 * Implementation for a note repeat.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface INoteRepeat extends IObserverManagement
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
     * Set de-/active.
     *
     * @param active True to activate
     */
    void setActive (boolean active);


    /**
     * Set the note repeat period.
     *
     * @param period The period
     */
    void setPeriod (double period);


    /**
     * Get the note repeat period.
     *
     * @return The period
     */
    double getPeriod ();


    /**
     * Get the note length.
     *
     * @param length The length
     */
    void setNoteLength (double length);


    /**
     * Get the note length.
     *
     * @return The length
     */
    double getNoteLength ();


    /**
     * Check if shuffle is active.
     *
     * @return True if active
     */
    boolean isShuffle ();


    /**
     * Toggle if shuffle is active.
     *
     * for which to toggle the note repeat shuffle
     */
    void toggleShuffle ();


    /**
     * Check if use pressure as the velocity for new notes is active.
     *
     * @return True if active
     */
    boolean usePressure ();


    /**
     * Use the pressure as the velocity for new notes.
     *
     * for which to toggle use pressure
     */
    void toggleUsePressure ();


    /**
     * Get the arpeggiator octaves.
     *
     * @return The octaves (0-8)
     */
    int getOctaves ();


    /**
     * Set the arpeggiator octaves.
     *
     * @param octaves The octaves (0-8)
     */
    void setOctaves (int octaves);


    /**
     * Get the arpeggiator mode.
     *
     * @return The mode
     */
    ArpeggiatorMode getMode ();


    /**
     * Set the arpeggiator mode.
     *
     * @param mode The mode
     */
    void setMode (ArpeggiatorMode mode);


    /**
     * Is the note repeat arpeggiator free running?
     *
     *
     * @return True if free running
     */
    boolean isFreeRunning ();


    /**
     * Toggle the free running state.
     */
    void toggleIsFreeRunning ();


    /**
     * Toggle if note latch is active.
     */
    void toggleLatchActive ();


    /**
     * True if note latch is enabled.
     *
     * @return True if note latch is enabled
     */
    boolean isLatchActive ();


    /**
     * Set de-/active note latch.
     *
     * @param active True to activate
     */
    void setLatchActive (boolean active);
}
