// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

/**
 * All categories for commands.
 *
 * @author Jürgen Moßgraber
 */
@SuppressWarnings("javadoc")
public enum CommandCategory
{
    BROWSER("Browser"),
    CLIP("Clip"),
    DEVICE("Device"),
    INSTRUMENT_DEVICE("1st Instrument Device"),
    EQ("EQ"),
    FX_TRACK("FX Track"),
    GLOBAL("Global"),
    LAYER("Layer"),
    LAYOUT("Layout"),
    MARKER("Marker"),
    MASTER("Master"),
    MIDI_CC("MIDI CC"),
    MODES("Modes"),
    NOTE_INPUT("Note Input"),
    PROJECT_REMOTES("Project Remotes"),
    SCENE("Scene"),
    TRACK("Track"),
    TRACK_REMOTES("Track Remotes"),
    TRANSPORT("Transport"),
    ACTION("Action");


    private final String name;


    /**
     * Constructor.
     *
     * @param name The name of the category
     */
    private CommandCategory (final String name)
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
}
