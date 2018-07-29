// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.configuration.IValueObserver;
import de.mossgrabers.framework.controller.IValueChanger;
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
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


/**
 * The configuration settings for Generic Flexi.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiConfiguration extends AbstractConfiguration
{
    /** Export signal. */
    public static final Integer BUTTON_EXPORT = Integer.valueOf (50);
    /** Import signal. */
    public static final Integer BUTTON_IMPORT = Integer.valueOf (51);

    /** The number of command slots. */
    public static final int     NUM_SLOTS     = 200;

    private IEnumSetting        addTypeSetting;
    private IEnumSetting        addNumberSetting;
    private IEnumSetting        addMidiChannelSetting;

    private CommandSlot []      commandSlots  = new CommandSlot [NUM_SLOTS];

    private String              filename;
    private int                 addTypeValue;
    private int                 addNumberValue;
    private int                 addMidiChannel;

    private Object              syncMapUpdate = new Object ();
    private int []              keyMap;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public GenericFlexiConfiguration (final IValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        String category = "Ex-/Import";

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

        category = "Use a knob/fader/button to set, then click add...";

        this.addTypeSetting = settingsUI.getEnumSetting ("Type:", category, CommandSlot.OPTIONS_TYPE, CommandSlot.OPTIONS_TYPE[0]);
        this.addTypeSetting.addValueObserver (value -> this.addTypeValue = lookupIndex (CommandSlot.OPTIONS_TYPE, value));

        this.addNumberSetting = settingsUI.getEnumSetting ("Number:", category, CommandSlot.OPTIONS_NUMBER, CommandSlot.OPTIONS_NUMBER[0]);
        this.addNumberSetting.addValueObserver (value -> this.addNumberValue = Integer.parseInt (value));

        this.addMidiChannelSetting = settingsUI.getEnumSetting ("Midi channel:", category, CommandSlot.OPTIONS_MIDI_CHANNEL, CommandSlot.OPTIONS_MIDI_CHANNEL[0]);
        this.addMidiChannelSetting.addValueObserver (value -> this.addMidiChannel = lookupIndex (CommandSlot.OPTIONS_MIDI_CHANNEL, value));

        settingsUI.getSignalSetting (" ", category, "Add").addValueObserver ( (Void) -> {
            final CommandSlot slot = this.findEmptySlot ();
            if (slot == null)
                return;
            slot.set (this.addTypeValue, this.addNumberValue, this.addMidiChannel);
            slot.setVisibility (true);
        });

        for (int i = 0; i < NUM_SLOTS; i++)
        {
            this.commandSlots[i] = new CommandSlot ("Slot " + (i + 1), settingsUI);
            this.commandSlots[i].addTypeValueObserver (v -> {
                synchronized (this.syncMapUpdate)
                {
                    this.keyMap = null;
                }
            });
        }
    }


    private CommandSlot findEmptySlot ()
    {
        for (final CommandSlot commandSlot: this.commandSlots)
        {
            if (commandSlot.getCommand () == FlexiCommand.OFF)
                return commandSlot;
        }
        return null;
    }


    /**
     * Set a received CC value.
     *
     * @param type The CC, Note or Program Change
     * @param number The number
     * @param midiChannel The midi channel
     */
    public void setAddValues (final String type, final int number, final int midiChannel)
    {
        this.addTypeSetting.set (type);
        this.addNumberSetting.set (Integer.toString (number));
        this.addMidiChannelSetting.set (Integer.toString (midiChannel + 1));
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
            if (slot.getCommand () != FlexiCommand.OFF && slot.getType () == type && slot.getMidiChannel () == midiChannel && slot.getNumber () == number)
                return i;
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

        // Collect all command slot commands
        final Set<FlexiCommand> commands = new HashSet<> ();
        for (int i = 0; i < this.commandSlots.length; i++)
        {
            final FlexiCommand cmd = this.commandSlots[i].getCommand ();
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
        final Properties props = new Properties ();
        try (final Reader reader = new FileReader (importFile))
        {
            props.load (reader);
        }

        for (int i = 0; i < this.commandSlots.length; i++)
        {
            final String slotName = "SLOT" + i + "_";
            final CommandSlot slot = this.commandSlots[i];
            slot.setType (props.getProperty (slotName + "TYPE"));
            slot.setNumber (props.getProperty (slotName + "NUMBER"));
            slot.setMidiChannel (props.getProperty (slotName + "MIDI_CHANNEL"));
            slot.setKnobMode (props.getProperty (slotName + "KNOB_MODE"));
            slot.setCommand (props.getProperty (slotName + "COMMAND"));
            slot.setSendValue (props.getProperty (slotName + "SEND_VALUE"));
        }
    }


    /**
     * Sets the command observer.
     *
     * @param observer The observer
     */
    public void setCommandObserver (final IValueObserver<FlexiCommand> observer)
    {
        for (int i = 0; i < this.commandSlots.length; i++)
            this.commandSlots[i].setCommandObserver (observer);
    }
}
