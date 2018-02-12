// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.ObserverManagement;


/**
 * Interface to a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IParameter extends ObserverManagement
{
    /**
     * De-/Increase the value by the given amount. Scaled by the range given in the constructor.
     *
     * @param increment The amount of the increment (negative to decrease)
     */
    void inc (double increment);


    /**
     * True if the parameter does exist.
     *
     * @return True if the parameter does exist.
     */
    boolean doesExist ();


    /**
     * Get the name of the parameter.
     *
     * @return The name
     */
    String getName ();


    /**
     * Get the name of the parameter.
     *
     * @param limit Limit the text to this length
     * @return The name
     */
    String getName (int limit);


    /**
     * Get the value formatted for display.
     *
     * @return The formatted value
     */
    String getDisplayedValue ();


    /**
     * Get the value formatted for display.
     *
     * @param limit Limit the text to this length
     * @return The formatted value
     */
    String getDisplayedValue (int limit);


    /**
     * Get the value.
     *
     * @return The value
     */
    int getValue ();


    /**
     * Sets the value.
     *
     * @param value The new value to set
     */
    void setValue (double value);


    /**
     * Get the value which is modulated. If it is currently not modulated it is identical to
     * getValue.
     *
     * @return The modulated value
     */
    int getModulatedValue ();


    /**
     * Specifies if this value should be indicated as mapped in Bitwig Studio, which is visually
     * shown as colored dots or tinting on the parameter controls.
     *
     * @param enable True in case visual indications should be shown in Bitwig Studio
     */
    void setIndication (boolean enable);
}