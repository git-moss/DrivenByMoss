package de.mossgrabers.controller.novation.launchcontrol.mode;

/**
 * Additional methods for XL modes.
 *
 * @author Jürgen Moßgraber
 */
public interface IXLMode
{
    /**
     * Set the color of one of the knobs depending on the current assignment in the mode.
     *
     * @param row The row of the knob
     * @param column The column of the knob
     * @param value The value of the assigned parameter
     */
    void setKnobColor (int row, int column, int value);
}
