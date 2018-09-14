// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IValueObserver;


/**
 * A command slot, which contains a button, knob or slider configuration triggered from CC, a note
 * or program change.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CommandSlot
{
    private static final String [] OPTIONS_KNOBMODE     = new String []
    {
        "Absolute",
        "Relative (1-64 increments, 127-65 decrements)",
        "Relative (65-127 increments, 63-0 decrements)",
        "Relative (1-63 increments, 65-127 decrements)"
    };

    /** The types. */
    public static final String []  OPTIONS_TYPE         = new String []
    {
        "CC",
        "Note",
        "Program Change"
    };

    /** The CC type. */
    public static final int        TYPE_CC              = 0;
    /** The note type. */
    public static final int        TYPE_NOTE            = 1;
    /** The program change type. */
    public static final int        TYPE_PROGRAM_CHANGE  = 2;

    /** The (CC, note or PC) number options. */
    public static final String []  OPTIONS_NUMBER       = new String [128];

    /** The midi channel options. */
    public static final String []  OPTIONS_MIDI_CHANNEL = new String [16];
    static
    {
        for (int i = 0; i < OPTIONS_NUMBER.length; i++)
            OPTIONS_NUMBER[i] = Integer.toString (i);
        for (int i = 0; i < OPTIONS_MIDI_CHANNEL.length; i++)
            OPTIONS_MIDI_CHANNEL[i] = Integer.toString (i + 1);
    }

    private static final String []       NAMES       = FlexiCommand.getNames ();

    private final IEnumSetting           typeSetting;
    private final IEnumSetting           numberSetting;
    private final IEnumSetting           midiChannelSetting;
    private final IEnumSetting           functionSetting;
    private final IEnumSetting           knobModeSetting;
    private final IEnumSetting           sendValueSetting;

    private int                          type        = 0;
    private int                          number      = 0;
    private int                          midiChannel = 0;
    private int                          knobMode    = 0;
    private FlexiCommand                 command     = FlexiCommand.OFF;
    private boolean                      sendValue;

    private IValueObserver<FlexiCommand> commandObserver;


    /**
     * Constructor.
     *
     * @param category The settings category
     * @param settingsUI The settings UI
     */
    public CommandSlot (final String category, final ISettingsUI settingsUI)
    {
        this.typeSetting = settingsUI.getEnumSetting ("Type:", category, OPTIONS_TYPE, OPTIONS_TYPE[0]);
        this.numberSetting = settingsUI.getEnumSetting ("Number:", category, OPTIONS_NUMBER, OPTIONS_NUMBER[0]);
        this.midiChannelSetting = settingsUI.getEnumSetting ("Midi Channel:", category, OPTIONS_MIDI_CHANNEL, OPTIONS_MIDI_CHANNEL[0]);
        this.knobModeSetting = settingsUI.getEnumSetting ("Knob Mode:", category, OPTIONS_KNOBMODE, OPTIONS_KNOBMODE[0]);
        this.functionSetting = settingsUI.getEnumSetting ("Function:", category, NAMES, NAMES[0]);
        this.sendValueSetting = settingsUI.getEnumSetting ("Send value to device:", category, AbstractConfiguration.ON_OFF_OPTIONS, AbstractConfiguration.ON_OFF_OPTIONS[1]);

        this.numberSetting.addValueObserver (value -> this.number = AbstractConfiguration.lookupIndex (OPTIONS_NUMBER, value));
        this.midiChannelSetting.addValueObserver (value -> this.midiChannel = AbstractConfiguration.lookupIndex (OPTIONS_MIDI_CHANNEL, value));

        this.knobModeSetting.addValueObserver (value -> {
            this.knobMode = AbstractConfiguration.lookupIndex (OPTIONS_KNOBMODE, value);
            this.fixKnobMode ();
        });

        this.typeSetting.addValueObserver (value -> {
            this.type = AbstractConfiguration.lookupIndex (OPTIONS_TYPE, value);
            this.sendValueSetting.setVisible (this.type == TYPE_CC);
        });

        this.functionSetting.addValueObserver (value -> {
            this.setVisibility (!NAMES[0].equals (value));
            this.command = FlexiCommand.lookupByName (value);
            this.fixKnobMode ();
            if (this.commandObserver != null)
                this.commandObserver.update (this.command);
        });

        this.sendValueSetting.addValueObserver (value -> this.sendValue = AbstractConfiguration.lookupIndex (AbstractConfiguration.ON_OFF_OPTIONS, value) > 0);
    }


    /**
     * Always set the knob mode to absolute for trigger commands.
     */
    protected void fixKnobMode ()
    {
        if (this.command.isTrigger () && this.knobMode > 0)
        {
            this.knobMode = 0;
            this.knobModeSetting.set (OPTIONS_KNOBMODE[0]);
        }
    }


    /**
     * Shows or hides the setting widgets for a command slot.
     *
     * @param visible
     */
    public void setVisibility (final boolean visible)
    {
        this.typeSetting.setVisible (visible);
        this.numberSetting.setVisible (visible);
        this.functionSetting.setVisible (visible);
        this.midiChannelSetting.setVisible (visible);
        this.knobModeSetting.setVisible (visible);
        this.sendValueSetting.setVisible (visible);
    }


    /**
     * Get the configured command.
     *
     * @return The command
     */
    public FlexiCommand getCommand ()
    {
        return this.command;
    }


    /**
     * Get the configured midi channel.
     *
     * @return The midi channel
     */
    public int getMidiChannel ()
    {
        return this.midiChannel;
    }


    /**
     * Get the configured type.
     *
     * @return The type
     */
    public int getType ()
    {
        return this.type;
    }


    /**
     * Get the configured number.
     *
     * @return The number
     */
    public int getNumber ()
    {
        return this.number;
    }


    /**
     * Get the knob mode (absolute or relative 1-3).
     *
     * @return The knob mode
     */
    public int getKnobMode ()
    {
        return this.knobMode;
    }


    /**
     * Add a type observer.
     *
     * @param observer The observer
     */
    public void addTypeValueObserver (final IValueObserver<String> observer)
    {
        this.typeSetting.addValueObserver (observer);
    }


    /**
     * Sets the command observer.
     *
     * @param observer The observer
     */
    public void setCommandObserver (final IValueObserver<FlexiCommand> observer)
    {
        this.commandObserver = observer;
    }


    /**
     * Should the value send back to the device?
     *
     * @return True to send back
     */
    public boolean isSendValue ()
    {
        return this.sendValue;
    }


    /**
     * Set the widgets of a command slot.
     *
     * @param type The type
     * @param number The number
     * @param midiChannel The midi channel
     */
    public void set (final int type, final int number, final int midiChannel)
    {
        this.typeSetting.set (OPTIONS_TYPE[type]);
        this.numberSetting.set (OPTIONS_NUMBER[number]);
        this.midiChannelSetting.set (OPTIONS_MIDI_CHANNEL[midiChannel]);
    }


    /**
     * Set the type.
     *
     * @param value The index as string
     */
    public void setType (final String value)
    {
        this.typeSetting.set (OPTIONS_TYPE[Integer.parseInt (value)]);
    }


    /**
     * Set the number.
     *
     * @param value The number as string
     */
    public void setNumber (final String value)
    {
        this.numberSetting.set (OPTIONS_NUMBER[Integer.parseInt (value)]);
    }


    /**
     * Set the midi channel.
     *
     * @param value The index as string
     */
    public void setMidiChannel (final String value)
    {
        this.midiChannelSetting.set (OPTIONS_MIDI_CHANNEL[Integer.parseInt (value)]);
    }


    /**
     * Set the knob mode.
     *
     * @param value The index as string
     */
    public void setKnobMode (final String value)
    {
        this.knobModeSetting.set (OPTIONS_KNOBMODE[Integer.parseInt (value)]);
    }


    /**
     * Set the command.
     *
     * @param value The command name
     */
    public void setCommand (final String value)
    {
        this.functionSetting.set (value);
    }


    /**
     * Set the send value.
     *
     * @param value The boolean as a string
     */
    public void setSendValue (final String value)
    {
        this.sendValueSetting.set (AbstractConfiguration.ON_OFF_OPTIONS[Boolean.parseBoolean (value) ? 1 : 0]);
    }
}
