// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

import java.util.Locale;


/**
 * Possible values for automation modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum AutomationMode
{
    /**
     * Existing envelopes are applied, but on-screen controls do not move. Fader movements are not
     * recorded. E.g., adjusting a track’s Volume fader in this mode will raise or lower the volume
     * for the whole track relative to the envelope.
     */
    TRIM_READ("Trim/Read"),
    /**
     * Envelopes are applied and moves controls for armed items, but does not write or remember any
     * changes made to them.
     */
    READ("Read"),
    /**
     * Similar to Latch, but stops making changes to envelope points when you stop adjusting them.
     */
    TOUCH("Touch"),
    /**
     * Writes and remembers any changes made and creates new points on existing track envelopes.
     * Changes commence when you first adjust a setting, and continue to be remembered until
     * playback stops.
     */
    LATCH("Latch"),
    /**
     * Enables you to try out changes to parameter values (e.g. volume level or pan position)
     * without actually writing them to envelopes. When you are happy with your parameter settings
     * you can then use an action to write them to your envelope(s). This option will be considered
     * in a section of its own, after this section.
     */
    LATCH_PREVIEW("Latch Preview"),
    /**
     * Writes and remembers current settings as edit points, along with any changes made during
     * playback. Previously written envelopes will be over written.
     */
    WRITE("Write");


    private final String label;


    /**
     * Constructor.
     *
     * @param label The label to display for the mode
     */
    private AutomationMode (final String label)
    {
        this.label = label;
    }


    /**
     * The label to display for the mode.
     *
     * @return The label
     */
    public String getLabel ()
    {
        return this.label;
    }


    /**
     * Get the identifier (the name in lower case).
     *
     * @return The identifier
     */
    public String getIdentifier ()
    {
        return this.name ().toLowerCase (Locale.US);
    }


    /**
     * Lookup an automation mode.
     *
     * @param identifier The identifier (the name in lower case).
     * @return The automation mode
     */
    public static AutomationMode lookup (final String identifier)
    {
        for (final AutomationMode mode: values ())
        {
            if (mode.name ().equalsIgnoreCase (identifier))
                return mode;
        }
        return AutomationMode.TRIM_READ;
    }
}
