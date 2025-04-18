// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.flexihandler.IFlexiCommandHandler;
import de.mossgrabers.controller.generic.flexihandler.utils.CommandSlot;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.nativefiledialogs.FileFilter;
import de.mossgrabers.nativefiledialogs.NativeFileDialogs;
import de.mossgrabers.nativefiledialogs.NativeFileDialogsFactory;
import de.mossgrabers.nativefiledialogs.PlatformNotSupported;


/**
 * The Generic Flexi.
 *
 * @author Jürgen Moßgraber
 */
public class GenericFlexiControlSurface extends AbstractControlSurface<GenericFlexiConfiguration>
{
    private static final FileFilter []                    FILE_FILTERS          =
    {
        new FileFilter ("Configuration", "properties"),
        new FileFilter ("All files", "*")
    };

    private final int []                                  valueCache            = new int [GenericFlexiConfiguration.NUM_SLOTS];
    private final Map<FlexiCommand, IFlexiCommandHandler> handlers              = new EnumMap<> (FlexiCommand.class);
    private NativeFileDialogs                             dialogs;

    private long                                          lastReceived          = 0;
    private int                                           lastCCReceived        = -1;
    private final int []                                  lastCCValues          = new int [128];

    private boolean                                       isShiftPressed        = false;
    private boolean                                       isUpdatingValue       = false;
    private int                                           functionLayer         = 0;
    private int                                           previousFunctionLayer = 0;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     */
    public GenericFlexiControlSurface (final IHost host, final GenericFlexiConfiguration configuration, final ColorManager colorManager, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, null, 10, 10);

        try
        {
            this.dialogs = NativeFileDialogsFactory.create (null);
        }
        catch (final PlatformNotSupported ex)
        {
            this.host.error ("Could not create dialogs instance.", ex);
        }

        Arrays.fill (this.valueCache, -1);

        this.configuration.addSettingObserver (GenericFlexiConfiguration.BUTTON_SAVE, this::saveFile);
        this.configuration.addSettingObserver (GenericFlexiConfiguration.BUTTON_LOAD, this::loadAndSelectFile);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Register a Flexi command handler.
     *
     * @param handler The handler to register
     */
    public void registerHandler (final IFlexiCommandHandler handler)
    {
        Arrays.asList (handler.getSupportedCommands ()).forEach (command -> this.handlers.put (command, handler));
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        final CommandSlot [] slots = this.configuration.getCommandSlots ();
        for (int i = 0; i < slots.length; i++)
        {
            final FlexiCommand command = slots[i].getCommand ();
            if (command != FlexiCommand.OFF && slots[i].isSendValue ())
                this.flushValue (i, slots[i]);
        }
    }


    private void flushValue (final int index, final CommandSlot slot)
    {
        final FlexiCommand command = slot.getCommand ();
        if (this.isUpdatingValue && !(command.isTrigger () && slot.isSendValueWhenReceived ()))
            return;

        final int value = this.getCommandValue (command);

        synchronized (this.valueCache)
        {
            if (this.valueCache[index] == value)
                return;
            this.valueCache[index] = value;
        }

        this.reflectValue (slot, value);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return this.isShiftPressed;
    }


    /**
     * Activate a new mode.
     *
     * @param modeID The ID of the new mode
     */
    public void activateMode (final Modes modeID)
    {
        final String modeName = this.modeManager.get (modeID).getName ();

        if (!this.modeManager.isActive (modeID))
        {
            this.modeManager.setActive (modeID);
            this.host.showNotification (modeName);
        }
        if (!modeName.equals (this.configuration.getSelectedModeName ()))
            this.configuration.setSelectedMode (modeName);
    }


    /**
     * Set the shift button pressed state.
     *
     * @param isShiftPressed The state
     */
    public void setShiftPressed (final boolean isShiftPressed)
    {
        this.isShiftPressed = isShiftPressed;
    }


    /**
     * Update the key translation table.
     */
    public void updateKeyTranslation ()
    {
        this.setKeyTranslationTable (this.configuration.getNoteMap ());
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        final int channel = status & 0xF;

        switch (code)
        {
            // Note on/off
            case MidiConstants.CMD_NOTE_OFF, MidiConstants.CMD_NOTE_ON:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE.get (CommandSlot.TYPE_NOTE + 1), data1, channel, false);
                final MidiValue midiValue = MidiValue.get (code == MidiConstants.CMD_NOTE_OFF ? 0 : data2, false);
                this.handleCommand (this.processFunctionLayer (this.configuration.getSlotCommands (CommandSlot.TYPE_NOTE, data1, channel), midiValue), midiValue);
                break;

            case MidiConstants.CMD_PROGRAM_CHANGE:
                this.handleProgramChange (channel, data1);
                break;

            case MidiConstants.CMD_CC:
                this.handleCC (channel, data1, data2);
                break;

            case MidiConstants.CMD_PITCHBEND:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE.get (CommandSlot.TYPE_PITCH_BEND + 1), data1, channel, true);
                final MidiValue value = MidiValue.get (data1 + data2 * 128, true);
                this.handleCommand (this.processFunctionLayer (this.configuration.getSlotCommands (CommandSlot.TYPE_PITCH_BEND, data1, channel), value), value);
                break;

            default:
                // Not used
                break;
        }
    }


    private CommandSlot processFunctionLayer (final List<CommandSlot> commandSlots, final MidiValue value)
    {
        // Check for layer switcher command
        for (final CommandSlot commandSlot: commandSlots)
        {
            final FlexiCommand command = commandSlot.getCommand ();
            final int functionOrdinal = command.ordinal ();
            if (functionOrdinal >= FlexiCommand.FUNCTION_LAYER1.ordinal () && functionOrdinal <= FlexiCommand.FUNCTION_LAYER10_TEMP.ordinal ())
            {
                final int fl = commandSlot.getFunctionLayer ();
                if (fl < 0 || fl == this.functionLayer)
                {
                    this.switchFunctionLayer (commandSlot, value);
                    return null;
                }
            }
        }

        // Only accept commands for the active function layer
        for (final CommandSlot slot: commandSlots)
        {
            final int fl = slot.getFunctionLayer ();
            if (fl < 0 || fl == this.functionLayer)
                return slot;
        }

        return null;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleCC (final int channel, final int data1, final int data2)
    {
        final long now = System.currentTimeMillis ();

        // High resolution command? See MIDI 1.0 Detailed Specification 4.2, page 11
        final boolean isRelated = now - this.lastReceived < 1000;
        final boolean isHighRes = isRelated && this.lastCCReceived < 32 && this.lastCCReceived + 32 == data1;

        this.lastCCReceived = data1;
        this.lastReceived = now;
        this.lastCCValues[data1] = data2;

        this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE.get (CommandSlot.TYPE_CC + 1), data1, channel, isHighRes);

        int value = 0;
        boolean isHighResValue = false;

        CommandSlot matchedCommandSlot = null;

        // Check for high resolution related setting
        if (data1 >= 0 && data1 < 32)
        {
            for (final CommandSlot commandSlot: this.configuration.getSlotCommands (CommandSlot.TYPE_CC, data1, channel))
            {
                final int fl = commandSlot.getFunctionLayer ();
                if (fl < 0 || fl == this.functionLayer && commandSlot.getResolution ())
                {
                    matchedCommandSlot = commandSlot;
                    value = data2 * 128 + this.lastCCValues[data1 + 32];
                    isHighResValue = true;
                }
            }
        }
        else if (data1 >= 32 && data1 < 64)
        {
            final int firstCC = data1 - 32;
            for (final CommandSlot commandSlot: this.configuration.getSlotCommands (CommandSlot.TYPE_CC, firstCC, channel))
            {
                if (commandSlot.getFunctionLayer () == this.functionLayer && commandSlot.getResolution ())
                {
                    matchedCommandSlot = commandSlot;
                    value = this.lastCCValues[firstCC] * 128 + data2;
                    isHighResValue = true;
                }
            }
        }

        // No Hi-Res
        final List<CommandSlot> commandSlots = new ArrayList<> ();
        if (matchedCommandSlot == null)
        {
            for (final CommandSlot commandSlot: this.configuration.getSlotCommands (CommandSlot.TYPE_CC, data1, channel))
            {
                commandSlots.add (commandSlot);
                value = data2;
            }
        }
        else
            commandSlots.add (matchedCommandSlot);

        final MidiValue midiValue = MidiValue.get (value, isHighResValue);
        this.handleCommand (this.processFunctionLayer (commandSlots, midiValue), midiValue);
    }


    /**
     * Handle the reception of a program change message.
     *
     * @param channel The channel of the message
     * @param data1 The program change value
     */
    private void handleProgramChange (final int channel, final int data1)
    {
        this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE.get (CommandSlot.TYPE_PROGRAM_CHANGE + 1), data1, channel, false);
        final CommandSlot commandSlot = this.processFunctionLayer (this.configuration.getSlotCommands (CommandSlot.TYPE_PROGRAM_CHANGE, data1, channel), MidiValue.get (127, false));
        if (commandSlot == null)
            return;

        if (commandSlot.getCommand ().isTrigger ())
        {
            this.handleCommand (commandSlot, MidiValue.get (127, false));
            this.handleCommand (commandSlot, MidiValue.get (0, false));
        }
        else
        {
            // Note: there is no data2 value for PC
            this.handleCommand (commandSlot, MidiValue.get (data1, false));
        }
    }


    /**
     * Handle system exclusive for MMC commands.
     *
     * @param dataStr The system exclusive data
     */
    private void handleSysEx (final String dataStr)
    {
        final int [] data = StringUtils.fromHexStr (dataStr);
        if (data.length != 6 || data[0] != 0xF0 || data[1] != 0x7F || data[3] != 0x06 || data[5] != 0xF7)
            return;

        // This is not (fully) correct but at least supports 16 device IDs (and 7F for ignore)
        final int channel = data[2] % 16;
        final int number = data[4];

        this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE.get (CommandSlot.TYPE_MMC + 1), number, channel, false);
        final CommandSlot commandSlot = this.processFunctionLayer (this.configuration.getSlotCommands (CommandSlot.TYPE_MMC, number, channel), MidiValue.get (127, false));
        if (commandSlot == null)
            return;
        this.handleCommand (commandSlot, MidiValue.get (127, false));
        this.handleCommand (commandSlot, MidiValue.get (0, false));
    }


    /**
     * Load all settings from a file.
     */
    public void loadAndSelectFile ()
    {
        final Optional<String> filename = this.getFileName (false);
        if (filename.isPresent ())
            this.host.showNotification (this.loadFile (filename.get ()));
    }


    /**
     * Load all settings from a file.
     *
     * @param filename The name (full path) of the file to load
     * @return An error or success text
     */
    public String loadFile (final String filename)
    {
        if (filename == null || filename.isBlank ())
            return "No file name set.";

        final File file = new File (filename);
        if (!file.exists ())
            return "The entered file does not exist: " + file.getAbsolutePath ();

        try
        {
            this.configuration.importFrom (file);
            this.updateKeyTranslation ();
            return "Imported from: " + file;
        }
        catch (final IOException ex)
        {
            return "Error reading file: " + ex.getMessage ();
        }
    }


    /**
     * Store all settings in a file.
     */
    private void saveFile ()
    {
        final Optional<String> filenameOpt = this.getFileName (true);
        if (filenameOpt.isEmpty ())
            return;

        try
        {
            String filename = filenameOpt.get ();

            // Ensure to end with .properties
            if (!filename.endsWith (".properties"))
            {
                filename = filename + ".properties";
                this.configuration.setFilename (filename);
            }

            this.configuration.exportTo (new File (filename));
            this.host.showNotification ("Exported to: " + filename);
        }
        catch (final IOException ex)
        {
            this.host.showNotification ("Error writing file: " + ex.getMessage ());
        }
    }


    /**
     * Get a filename from the user.
     *
     * @param isNew
     * @return The filename or null if none was selected
     */
    private Optional<String> getFileName (final boolean isNew)
    {
        String filename = this.configuration.getFilename ();
        if (filename != null && !filename.isBlank ())
        {
            final File currentFolder = new File (filename);
            if (currentFolder.exists ())
                this.dialogs.setCurrentDirectory (currentFolder);
        }

        try
        {
            final File fn = isNew ? this.dialogs.selectNewFile ("Save", FILE_FILTERS) : this.dialogs.selectFile ("Load", FILE_FILTERS);
            if (fn == null)
                return Optional.empty ();

            filename = fn.getAbsolutePath ();
            this.configuration.setFilename (filename);
            return Optional.of (filename);
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not create file dialog.", ex);
            return Optional.empty ();
        }
    }


    /**
     * Get the current value of a command.
     *
     * @param command The command
     * @return The value or -1
     */
    private int getCommandValue (final FlexiCommand command)
    {
        final IFlexiCommandHandler commandHandler = this.handlers.get (command);
        if (commandHandler == null)
            return -1;
        final int value = commandHandler.getCommandValue (command);
        // Scale down to 7-bit
        return (int) Math.round (value * 127.0 / 16383.0);
    }


    /**
     * Handle a command.
     *
     * @param commandSlot The command slot to execute
     * @param value The received parameter value to handle
     */
    private void handleCommand (final CommandSlot commandSlot, final MidiValue value)
    {
        if (commandSlot == null)
            return;

        this.isUpdatingValue = true;
        final FlexiCommand command = commandSlot.getCommand ();
        final IFlexiCommandHandler commandHandler = this.handlers.get (command);
        if (commandHandler == null)
        {
            this.host.error ("No handler registered for command: " + command);
            return;
        }
        commandHandler.handle (command, commandSlot.getKnobMode (), value);

        this.host.scheduleTask ( () -> this.isUpdatingValue = false, 400);
    }


    /**
     * Send back the current value type of a command slot to the device.
     *
     * @param slot The slot
     * @param value The value to reflect
     */
    private void reflectValue (final CommandSlot slot, final int value)
    {
        if (value < 0 || value > 127)
        {
            this.host.error (String.format ("Attempt to reflect value out of range from slot command: %s Value: %d", slot.getCommand ().getName (), Integer.valueOf (value)));
            return;
        }

        final IMidiOutput output = this.getMidiOutput ();
        final int midiChannel = slot.getMidiChannel ();

        // Cannot reflect "All" setting
        if (midiChannel > 15)
            return;

        switch (slot.getType ())
        {
            case CommandSlot.TYPE_NOTE:
                output.sendNoteEx (midiChannel, slot.getNumber (), value);
                break;

            case CommandSlot.TYPE_CC:
                output.sendCCEx (midiChannel, slot.getNumber (), value);
                break;

            case CommandSlot.TYPE_PITCH_BEND:
                output.sendPitchbend (midiChannel, 0, value);
                break;

            default:
                // Other types not supported
                break;
        }
    }


    private void switchFunctionLayer (final CommandSlot commandSlot, final MidiValue value)
    {
        final FlexiCommand command = commandSlot.getCommand ();
        final KnobMode knobMode = commandSlot.getKnobMode ();

        final int oldLayer = this.functionLayer;

        switch (command)
        {
            case FUNCTION_LAYER1, FUNCTION_LAYER2, FUNCTION_LAYER3, FUNCTION_LAYER4, FUNCTION_LAYER5, FUNCTION_LAYER6, FUNCTION_LAYER7, FUNCTION_LAYER8, FUNCTION_LAYER9, FUNCTION_LAYER10:
                if (knobMode == KnobMode.ABSOLUTE_TOGGLE || knobMode == KnobMode.ABSOLUTE && value.isPositive ())
                    this.functionLayer = command.ordinal () - FlexiCommand.FUNCTION_LAYER1.ordinal ();
                break;

            case FUNCTION_LAYER2_TEMP, FUNCTION_LAYER3_TEMP, FUNCTION_LAYER4_TEMP, FUNCTION_LAYER5_TEMP, FUNCTION_LAYER6_TEMP, FUNCTION_LAYER7_TEMP, FUNCTION_LAYER8_TEMP, FUNCTION_LAYER9_TEMP, FUNCTION_LAYER10_TEMP:
                // Note: this is one off since there is no 'activate layer 1 temporarily'!
                final int layerIndex = command.ordinal () - FlexiCommand.FUNCTION_LAYER2_TEMP.ordinal () + 1;
                if (knobMode == KnobMode.ABSOLUTE_TOGGLE && this.functionLayer != layerIndex || knobMode == KnobMode.ABSOLUTE && value.isPositive ())
                {
                    this.previousFunctionLayer = oldLayer;
                    this.functionLayer = layerIndex;
                }
                else
                    this.functionLayer = this.previousFunctionLayer;
                break;

            default:
                return;
        }

        if (oldLayer != this.functionLayer)
            this.getHost ().showNotification ("Functions Layer " + (this.functionLayer + 1));
    }
}