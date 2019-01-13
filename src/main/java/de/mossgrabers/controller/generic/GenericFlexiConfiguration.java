// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.CommandCategory;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.configuration.IValueObserver;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.scale.Scales;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * The configuration settings for Generic Flexi.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiConfiguration extends AbstractConfiguration
{
    private static final String [] NAMES                = FlexiCommand.getNames ();

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
        "Off",
        "CC",
        "Note",
        "Program Change",
        "Pitchbend"
    };

    /** The (CC, note or PC) number options. */
    private static final String [] OPTIONS_NUMBER       = new String [128];

    /** The midi channel options. */
    private static final String [] OPTIONS_MIDI_CHANNEL = new String [16];
    static
    {
        for (int i = 0; i < OPTIONS_NUMBER.length; i++)
            OPTIONS_NUMBER[i] = Integer.toString (i);
        for (int i = 0; i < OPTIONS_MIDI_CHANNEL.length; i++)
            OPTIONS_MIDI_CHANNEL[i] = Integer.toString (i + 1);
    }

    /** Export signal. */
    public static final Integer                      BUTTON_EXPORT         = Integer.valueOf (50);
    /** Import signal. */
    public static final Integer                      BUTTON_IMPORT         = Integer.valueOf (51);

    /** A setting of a slot has changed. */
    static final Integer                             SLOT_CHANGE           = Integer.valueOf (1000);

    /** The number of command slots. */
    public static final int                          NUM_SLOTS             = 200;

    private final IHost                              host;

    private IEnumSetting                             slotSelectionSetting;
    private IEnumSetting                             typeSetting;
    private IEnumSetting                             numberSetting;
    private IEnumSetting                             midiChannelSetting;
    private IEnumSetting                             knobModeSetting;
    private IEnumSetting                             sendValueSetting;
    private final List<IEnumSetting>                 functionSettings      = new ArrayList<> (CommandCategory.values ().length);
    private final Map<CommandCategory, IEnumSetting> functionSettingsMap   = new EnumMap<> (CommandCategory.class);
    private IEnumSetting                             learnTypeSetting;
    private IEnumSetting                             learnNumberSetting;
    private IEnumSetting                             learnMidiChannelSetting;

    private CommandSlot []                           commandSlots          = new CommandSlot [NUM_SLOTS];

    private IValueObserver<FlexiCommand>             commandObserver;
    private String                                   filename;
    private Object                                   syncMapUpdate         = new Object ();
    private int []                                   keyMap;
    private int                                      selectedSlot          = 0;
    private String                                   learnTypeValue        = null;
    private String                                   learnNumberValue      = null;
    private String                                   learnMidiChannelValue = null;
    private AtomicBoolean                            doNotFire             = new AtomicBoolean (false);
    private AtomicBoolean                            commandIsUpdating     = new AtomicBoolean (false);


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param host
     */
    public GenericFlexiConfiguration (final IValueChanger valueChanger, final IHost host)
    {
        super (valueChanger);
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        String category = "Slot";

        final String [] slotEntries = new String [NUM_SLOTS];
        for (int i = 0; i < NUM_SLOTS; i++)
        {
            this.commandSlots[i] = new CommandSlot ();
            slotEntries[i] = Integer.toString (i + 1);
        }

        this.slotSelectionSetting = settingsUI.getEnumSetting ("Selected:", category, slotEntries, slotEntries[0]);
        this.slotSelectionSetting.addValueObserver (this::selectSlot);

        // Selected Slot - MIDI trigger

        category = "Selected Slot - MIDI trigger";

        this.typeSetting = settingsUI.getEnumSetting ("Type:", category, OPTIONS_TYPE, OPTIONS_TYPE[0]);
        this.numberSetting = settingsUI.getEnumSetting ("Number:", category, OPTIONS_NUMBER, OPTIONS_NUMBER[0]);
        this.midiChannelSetting = settingsUI.getEnumSetting ("Midi Channel:", category, OPTIONS_MIDI_CHANNEL, OPTIONS_MIDI_CHANNEL[0]);
        this.knobModeSetting = settingsUI.getEnumSetting ("Knob Mode:", category, OPTIONS_KNOBMODE, OPTIONS_KNOBMODE[0]);
        this.sendValueSetting = settingsUI.getEnumSetting ("Send value to device:", category, AbstractConfiguration.ON_OFF_OPTIONS, AbstractConfiguration.ON_OFF_OPTIONS[1]);

        // Selected Slot - Function

        category = "Selected Slot - Function";

        final CommandCategory [] values = CommandCategory.values ();
        for (int i = 0; i < values.length; i++)
        {
            final IEnumSetting fs = createFunctionSetting (values[i].getName (), category, settingsUI);
            this.functionSettings.add (fs);
            this.functionSettingsMap.put (values[i], fs);
            final int index = i;
            fs.addValueObserver (value -> this.handleFunctionChange (index, value));
        }

        // The MIDI learn section

        category = "Use a knob/fader/button to set, then click add...";

        this.learnTypeSetting = settingsUI.getEnumSetting ("Type:", category, OPTIONS_TYPE, OPTIONS_TYPE[0]);
        this.learnNumberSetting = settingsUI.getEnumSetting ("Number:", category, OPTIONS_NUMBER, OPTIONS_NUMBER[0]);
        this.learnMidiChannelSetting = settingsUI.getEnumSetting ("Midi channel:", category, OPTIONS_MIDI_CHANNEL, OPTIONS_MIDI_CHANNEL[0]);
        this.learnTypeSetting.setEnabled (false);
        this.learnNumberSetting.setEnabled (false);
        this.learnMidiChannelSetting.setEnabled (false);

        settingsUI.getSignalSetting (" ", category, "Set").addValueObserver ( (Void) -> {
            if (this.learnTypeValue == null)
                return;
            this.typeSetting.set (this.learnTypeValue);
            this.numberSetting.set (this.learnNumberValue);
            this.midiChannelSetting.set (this.learnMidiChannelValue);
        });

        // Ex-/Import section

        category = "Ex-/Import";

        // The Setlist file to auto-load
        final IStringSetting fileSetting = settingsUI.getStringSetting ("Filename to ex-/import:", category, -1, "");
        fileSetting.addValueObserver (value -> this.filename = value);

        if (!GraphicsEnvironment.isHeadless ())
        {
            settingsUI.getSignalSetting (" ", category, "Select").addValueObserver ( (Void) -> {
                final FileDialog fileDialog = new FileDialog ((Frame) null);
                fileDialog.setVisible (true);
                final String fn = fileDialog.getFile ();
                if (fn == null)
                    return;
                final File file = new File (fileDialog.getDirectory (), fn);
                fileSetting.set (file.getAbsolutePath ());
            });
        }

        settingsUI.getSignalSetting ("  ", category, "Export").addValueObserver ( (Void) -> this.notifyObservers (BUTTON_EXPORT));
        settingsUI.getSignalSetting ("   ", category, "Import").addValueObserver ( (Void) -> this.notifyObservers (BUTTON_IMPORT));

        this.learnTypeSetting.set (OPTIONS_TYPE[0]);

        this.typeSetting.addValueObserver (value -> {
            final int index = AbstractConfiguration.lookupIndex (OPTIONS_TYPE, value);
            this.getSelectedSlot ().setType (index - 1);
            this.sendValueSetting.setVisible (index == CommandSlot.TYPE_CC);
            this.clearNoteMap ();
            this.updateVisibility (!OPTIONS_TYPE[0].equals (value));
        });
        this.numberSetting.addValueObserver (value -> {
            this.getSelectedSlot ().setNumber (AbstractConfiguration.lookupIndex (OPTIONS_NUMBER, value));
            this.clearNoteMap ();
        });
        this.midiChannelSetting.addValueObserver (value -> {
            this.getSelectedSlot ().setMidiChannel (AbstractConfiguration.lookupIndex (OPTIONS_MIDI_CHANNEL, value));
            this.clearNoteMap ();
        });
        this.knobModeSetting.addValueObserver (value -> {
            this.getSelectedSlot ().setKnobMode (AbstractConfiguration.lookupIndex (OPTIONS_KNOBMODE, value));
            this.fixKnobMode ();
        });
        this.sendValueSetting.addValueObserver (value -> this.getSelectedSlot ().setSendValue (AbstractConfiguration.lookupIndex (AbstractConfiguration.ON_OFF_OPTIONS, value) > 0));

        this.host.scheduleTask ( () -> {
            if (this.filename == null || this.filename.isEmpty ())
                return;
            final File file = new File (this.filename);
            if (!file.exists ())
                return;
            try
            {
                this.host.println ("Auto loaded: " + this.filename);
                this.importFrom (file);
            }
            catch (final IOException ex)
            {
                this.host.showNotification ("Error reading file: " + ex.getMessage ());
            }
        }, 2000);
    }


    /**
     * Handles changing the function selection by the user.
     *
     * @param index The index of the changed function
     * @param value The new value
     */
    private void handleFunctionChange (final int index, final String value)
    {
        if (this.commandIsUpdating.get ())
            return;

        if (this.doNotFire.get ())
        {
            this.doNotFire.set (false);
            return;
        }

        final CommandSlot selectedSlot = this.getSelectedSlot ();
        final FlexiCommand oldCommand = selectedSlot.getCommand ();
        final FlexiCommand newCommand = FlexiCommand.lookupByName (value);
        selectedSlot.setCommand (newCommand);

        this.fixKnobMode ();
        this.notifyCommandObserver ();

        final CommandCategory oldCategory = oldCommand.getCategory ();
        if (oldCategory != null && oldCategory != newCommand.getCategory ())
        {
            this.doNotFire.set (true);
            this.functionSettingsMap.get (oldCategory).set (FlexiCommand.OFF.getName ());
        }
    }


    /**
     * Always set the knob mode to absolute for trigger commands.
     */
    private void fixKnobMode ()
    {
        final CommandSlot slot = this.getSelectedSlot ();
        if (slot.getCommand ().isTrigger () && slot.getKnobMode () > 0)
            this.knobModeSetting.set (OPTIONS_KNOBMODE[0]);
    }


    private CommandSlot getSelectedSlot ()
    {
        return this.commandSlots[this.selectedSlot];
    }


    /**
     * Set a received CC value.
     *
     * @param type The CC, Note or Program Change
     * @param number The number
     * @param midiChannel The midi channel
     */
    public void setLearnValues (final String type, final int number, final int midiChannel)
    {
        this.learnTypeValue = type;
        this.learnNumberValue = Integer.toString (number);
        this.learnMidiChannelValue = Integer.toString (midiChannel + 1);

        this.learnTypeSetting.set (type);
        this.learnNumberSetting.set (this.learnNumberValue);
        this.learnMidiChannelSetting.set (this.learnMidiChannelValue);
    }


    /**
     * Get a matching configured slot command, if available.
     *
     * @param type The type
     * @param number The number
     * @param midiChannel The midi channel
     * @return The slot index or -1 if not found
     */
    public int getSlotCommand (final int type, final int number, final int midiChannel)
    {
        for (int i = 0; i < this.commandSlots.length; i++)
        {
            final CommandSlot slot = this.commandSlots[i];
            if (slot.getCommand () != FlexiCommand.OFF && slot.getType () == type && slot.getMidiChannel () == midiChannel)
            {
                if (type == CommandSlot.TYPE_PITCH_BEND || slot.getNumber () == number)
                    return i;
            }
        }
        return -1;
    }


    /**
     * Get a key translation map which blocks the notes that are mapped to a command.
     *
     * @return The key translation map
     */
    public int [] getNoteMap ()
    {
        synchronized (this.syncMapUpdate)
        {
            if (this.keyMap == null)
            {
                this.keyMap = Scales.getIdentityMatrix ();
                for (final CommandSlot slot: this.commandSlots)
                {
                    if (slot.getCommand () != FlexiCommand.OFF && slot.getType () == CommandSlot.TYPE_NOTE)
                        this.keyMap[slot.getNumber ()] = -1;
                }
            }
            return this.keyMap;
        }
    }


    /**
     * Clear the note map.
     */
    public void clearNoteMap ()
    {
        synchronized (this.syncMapUpdate)
        {
            this.keyMap = null;
        }
        this.notifyObservers (SLOT_CHANGE);
    }


    /**
     * Get all command slots.
     *
     * @return The slots
     */
    public CommandSlot [] getCommandSlots ()
    {
        return this.commandSlots;
    }


    /**
     * Get all commands which are used in a slot.
     *
     * @return The commands
     */
    public Set<FlexiCommand> getMappedCommands ()
    {
        final Set<FlexiCommand> commands = new HashSet<> ();
        for (final CommandSlot commandSlot: this.commandSlots)
        {
            final FlexiCommand cmd = commandSlot.getCommand ();
            if (cmd != null)
                commands.add (cmd);
        }
        return commands;
    }


    /**
     * Get the file name.
     *
     * @return The file name
     */
    public String getFilename ()
    {
        return this.filename;
    }


    /**
     * Export the configuration to the given file.
     *
     * @param exportFile Where to export to
     * @throws IOException Could not save the file
     */
    public void exportTo (final File exportFile) throws IOException
    {
        final Properties props = new Properties ();
        for (int i = 0; i < this.commandSlots.length; i++)
        {
            final String slotName = "SLOT" + i + "_";
            final CommandSlot slot = this.commandSlots[i];
            props.put (slotName + "TYPE", Integer.toString (slot.getType ()));
            props.put (slotName + "NUMBER", Integer.toString (slot.getNumber ()));
            props.put (slotName + "MIDI_CHANNEL", Integer.toString (slot.getMidiChannel ()));
            props.put (slotName + "KNOB_MODE", Integer.toString (slot.getKnobMode ()));
            props.put (slotName + "COMMAND", slot.getCommand ().getName ());
            props.put (slotName + "SEND_VALUE", Boolean.toString (slot.isSendValue ()));
        }
        try (final Writer writer = new FileWriter (exportFile))
        {
            props.store (writer, "Generic Flexi");
        }
    }


    /**
     * Import the configuration from the given file.
     *
     * @param importFile Where to import from
     * @throws IOException Could not save the file
     */
    public void importFrom (final File importFile) throws IOException
    {
        try
        {
            final Properties props = new Properties ();
            try (final Reader reader = new FileReader (importFile))
            {
                props.load (reader);
            }

            for (int i = 0; i < this.commandSlots.length; i++)
            {
                final String slotName = "SLOT" + i + "_";
                final CommandSlot slot = this.commandSlots[i];

                final FlexiCommand command = FlexiCommand.lookupByName (props.getProperty (slotName + "COMMAND"));
                int type = Integer.parseInt (props.getProperty (slotName + "TYPE"));

                // For backwards compatibility
                if (command == FlexiCommand.OFF)
                    type = CommandSlot.TYPE_OFF;

                slot.setType (type);
                slot.setNumber (Integer.parseInt (props.getProperty (slotName + "NUMBER")));
                slot.setMidiChannel (Integer.parseInt (props.getProperty (slotName + "MIDI_CHANNEL")));
                slot.setKnobMode (Integer.parseInt (props.getProperty (slotName + "KNOB_MODE")));
                slot.setCommand (command);
                slot.setSendValue (Boolean.parseBoolean (props.getProperty (slotName + "SEND_VALUE")));
            }
        }
        catch (final IOException | NumberFormatException ex)
        {
            this.host.error ("Could not import from file.", ex);
            this.host.showNotification ("Could not import from file. Check Script Console for detailed error.");
            return;
        }

        this.clearNoteMap ();

        this.slotSelectionSetting.set ("1");
        this.selectSlot ("1");
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


    private void selectSlot (final String value)
    {
        this.selectedSlot = Integer.parseInt (value) - 1;
        final CommandSlot slot = this.commandSlots[this.selectedSlot];

        this.setType (slot.getType ());
        this.setNumber (slot.getNumber ());
        this.setMidiChannel (slot.getMidiChannel ());
        this.setKnobMode (slot.getKnobMode ());
        this.setSendValue (slot.isSendValue ());
        this.setCommand (slot.getCommand ());
    }


    private void updateVisibility (final boolean visible)
    {
        this.numberSetting.setVisible (visible);
        this.midiChannelSetting.setVisible (visible);
        this.knobModeSetting.setVisible (visible);
        this.sendValueSetting.setVisible (visible);
        for (final IEnumSetting fs: this.functionSettings)
            fs.setVisible (visible);
    }


    /**
     * Set the type.
     *
     * @param value The index
     */
    private void setType (final int value)
    {
        this.typeSetting.set (OPTIONS_TYPE[value + 1]);
    }


    /**
     * Set the number.
     *
     * @param value The number
     */
    private void setNumber (final int value)
    {
        this.numberSetting.set (OPTIONS_NUMBER[value]);
    }


    /**
     * Set the midi channel.
     *
     * @param value The index
     */
    private void setMidiChannel (final int value)
    {
        this.midiChannelSetting.set (OPTIONS_MIDI_CHANNEL[value]);
    }


    /**
     * Set the knob mode.
     *
     * @param value The index
     */
    private void setKnobMode (final int value)
    {
        this.knobModeSetting.set (OPTIONS_KNOBMODE[value]);
    }


    /**
     * Set the send value.
     *
     * @param value The boolean
     */
    private void setSendValue (final boolean value)
    {
        this.sendValueSetting.set (AbstractConfiguration.ON_OFF_OPTIONS[value ? 1 : 0]);
    }


    /**
     * Set the command.
     *
     * @param value The command name
     */
    private void setCommand (final FlexiCommand value)
    {
        final CommandCategory category = value.getCategory ();
        final CommandCategory [] values = CommandCategory.values ();
        this.commandIsUpdating.set (true);
        for (int i = 0; i < values.length; i++)
            this.functionSettings.get (i).set (category == values[i] ? value.getName () : FlexiCommand.OFF.getName ());
        this.host.scheduleTask ( () -> {
            this.commandIsUpdating.set (false);
        }, 600);
    }


    private void notifyCommandObserver ()
    {
        if (this.commandObserver != null)
            this.commandObserver.update (this.getSelectedSlot ().getCommand ());
    }


    private static IEnumSetting createFunctionSetting (final String functionCategory, final String settingCategory, final ISettingsUI settingsUI)
    {
        final List<String> functionsNames = new ArrayList<> ();
        functionsNames.add (FlexiCommand.OFF.getName ());
        for (final String name: NAMES)
        {
            if (name.startsWith (functionCategory))
                functionsNames.add (name);
        }
        final String [] array = functionsNames.toArray (new String [functionsNames.size ()]);
        return settingsUI.getEnumSetting (functionCategory + ":", settingCategory, array, array[0]);
    }
}
