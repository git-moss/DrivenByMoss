// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.IItem;


/**
 * Interface to a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IParameter extends IItem
{
    /**
     * De-/Increase the value by the given amount. Scaled by the range given in the constructor.
     *
     * @param increment The amount of the increment (negative to decrease)
     */
    void inc (double increment);


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
    void setValue (int value);


    /**
     * Sets the value.
     *
     * @param valueChanger The value-changer to use
     * @param value The new value to set
     */
    void setValue (IValueChanger valueChanger, int value);


    /**
     * Sets the value.
     *
     * @param value The new value to set in the range of [0..1]
     */
    void setNormalizedValue (double value);


    /**
     * Sets the value. Ignores take over.
     *
     * @param value The new value to set
     */
    void setValueImmediatly (int value);


    /**
     * Change the value.
     *
     * @param value The control value
     */
    void changeValue (int value);


    /**
     * Change the value.
     *
     * @param valueChanger The value-changer to use
     * @param value The control value
     */
    void changeValue (IValueChanger valueChanger, int value);


    /**
     * Reset the value to its default value.
     */
    void resetValue ();


    /**
     * Signal that the value fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchValue (boolean isBeingTouched);


    /**
     * Get the value which is modulated. If it is currently not modulated it is identical to
     * getValue.
     *
     * @return The modulated value
     */
    int getModulatedValue ();


    /**
     * Specifies if this value should be indicated as mapped in the DAW, which is visually shown as
     * colored dots or tinting on the parameter controls.
     *
     * @param enable True in case visual indications should be shown
     */
    void setIndication (boolean enable);
}