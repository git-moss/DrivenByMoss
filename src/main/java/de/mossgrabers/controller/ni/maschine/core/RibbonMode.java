// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.core;

import java.util.HashMap;
import java.util.Map;


/**
 * The configuration option for the ribbon touch strip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum RibbonMode
{
    /** Use ribbon for pitch bend down. */
    PITCH_DOWN("Pitch Down"),
    /** Use ribbon for pitch bend up. */
    PITCH_UP("Pitch Up"),
    /** Use ribbon for pitch bend down/up. */
    PITCH_DOWN_UP("Pitch Down/Up"),
    /** Use ribbon for MIDI CC 1. */
    CC_1("Modulation (CC 1)"),
    /** Use ribbon for MIDI CC 11. */
    CC_11("Expression (CC 11)"),
    /** Use ribbon for master volume. */
    MASTER_VOLUME("Master Volume"),
    /** Use ribbon for note repeat period. */
    NOTE_REPEAT_PERIOD("Note Repeat: Period"),
    /** Use ribbon for note repeat length. */
    NOTE_REPEAT_LENGTH("Note Repeat: Length");


    private static final String []               NAMES;
    private static final Map<String, RibbonMode> NAME_MODE_MAP;

    static
    {
        final RibbonMode [] values = RibbonMode.values ();
        NAMES = new String [values.length];
        NAME_MODE_MAP = new HashMap<> (values.length);
        for (int i = 0; i < values.length; i++)
        {
            NAMES[i] = values[i].getName ();
            NAME_MODE_MAP.put (NAMES[i], values[i]);
        }
    }

    private final String name;


    /**
     * Constructor.
     *
     * @param name The name of the category
     */
    private RibbonMode (final String name)
    {
        this.name = name;
    }


    /**
     * Get the name of the command.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the names of all commands.
     *
     * @return The names
     */
    public static String [] getNames ()
    {
        return NAMES;
    }


    /**
     * Lookup an arpeggiator mode.
     *
     * @param name The name of the mode
     * @return The mode
     */
    public static RibbonMode lookupByName (final String name)
    {
        return NAME_MODE_MAP.getOrDefault (name, RibbonMode.PITCH_DOWN);
    }
}
