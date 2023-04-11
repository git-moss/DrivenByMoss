// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

import java.util.Locale;


/**
 * Possible values for post recording actions.
 *
 * @author Jürgen Moßgraber
 */
public enum PostRecordingAction
{
    /** No post recording action. */
    OFF("Off"),
    /** Play recorded. */
    PLAY_RECORDED("Read"),
    /** Record into next free slot. */
    RECORD_NEXT_FREE_SLOT("Record into next free slot"),
    /** . */
    STOP("Stop"),
    /** . */
    RETURN_TO_ARRANGEMENT("Return to arrangement"),
    /** . */
    RETURN_TO_PREVIOUS_CLIP("Return to last clip"),
    /** . */
    PLAY_RANDOM("Play random");


    private final String label;


    /**
     * Constructor.
     *
     * @param label The label to display for the mode
     */
    private PostRecordingAction (final String label)
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
    public static PostRecordingAction lookup (final String identifier)
    {
        for (final PostRecordingAction mode: values ())
        {
            if (mode.name ().equalsIgnoreCase (identifier))
                return mode;
        }
        return PostRecordingAction.OFF;
    }
}
