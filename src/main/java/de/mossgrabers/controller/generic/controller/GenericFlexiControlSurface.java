// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

import de.mossgrabers.controller.generic.CommandSlot;
import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.flexihandler.IFlexiCommandHandler;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.nativefiledialogs.FileFilter;
import de.mossgrabers.nativefiledialogs.NativeFileDialogs;
import de.mossgrabers.nativefiledialogs.NativeFileDialogsFactory;
import de.mossgrabers.nativefiledialogs.PlatformNotSupported;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;


/**
 * The Generic Flexi.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiControlSurface extends AbstractControlSurface<GenericFlexiConfiguration>
{
    private static final FileFilter []                    FILE_FILTERS    =
    {
        new FileFilter ("Configuration", "properties"),
        new FileFilter ("All files", "*")
    };

    private final int []                                  valueCache      = new int [GenericFlexiConfiguration.NUM_SLOTS];
    private final Map<FlexiCommand, IFlexiCommandHandler> handlers        = new EnumMap<> (FlexiCommand.class);
    private NativeFileDialogs                             dialogs;

    private boolean                                       isShiftPressed  = false;
    private boolean                                       isUpdatingValue = false;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
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
     * Register a flexi command handler.
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
            if (command == FlexiCommand.OFF || !slots[i].isSendValue ())
                continue;

            if (this.isUpdatingValue && !(slots[i].getCommand ().isTrigger () && slots[i].isSendValueWhenReceived ()))
                continue;

            final int value = this.getCommandValue (command);
            if (this.valueCache[i] == value)
                continue;
            this.valueCache[i] = value;
            this.reflectValue (slots[i], value);
        }
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
            case 0x90:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_NOTE + 1], data1, channel);
                this.handleCommand (this.configuration.getSlotCommand (CommandSlot.TYPE_NOTE, data1, channel), data2);
                break;

            // Program Change
            case 0xC0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_PROGRAM_CHANGE + 1], data1, channel);
                final int slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_PROGRAM_CHANGE, data1, channel);
                if (slotIndex < 0)
                    return;
                final CommandSlot commandSlot = this.configuration.getCommandSlots ()[slotIndex];
                if (commandSlot.getCommand ().isTrigger ())
                {
                    this.handleCommand (slotIndex, 127);
                    this.handleCommand (slotIndex, 0);
                }
                else
                {
                    // Note: there is no data2 value for PC
                    this.handleCommand (slotIndex, data1);
                }
                break;

            // CC
            case 0xB0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_CC + 1], data1, channel);
                this.handleCommand (this.configuration.getSlotCommand (CommandSlot.TYPE_CC, data1, channel), data2);
                break;

            // Pitchbend
            case 0xE0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_PITCH_BEND + 1], data1, channel);
                this.handleCommand (this.configuration.getSlotCommand (CommandSlot.TYPE_PITCH_BEND, data1, channel), data2);
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Handle sysex for MMC commands.
     *
     * @param dataStr The sysex data
     */
    private void handleSysEx (final String dataStr)
    {
        final int [] data = StringUtils.fromHexStr (dataStr);
        if (data.length != 6 || data[0] != 0xF0 || data[1] != 0x7F || data[3] != 0x06 || data[5] != 0xF7)
            return;

        // This is not (fully) correct but at least supports 16 device IDs (and 7F for ignore)
        final int channel = data[2] % 16;
        final int number = data[4];

        this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_MMC + 1], number, channel);
        final int slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_MMC, number, channel);
        if (slotIndex == -1)
            return;
        this.handleCommand (slotIndex, 127);
        this.handleCommand (slotIndex, 0);
    }


    /**
     * Load all settings from a file.
     */
    public void loadAndSelectFile ()
    {
        final String filename = this.getFileName (false);
        if (filename != null)
            this.host.showNotification (this.loadFile (filename));
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
        String filename = this.getFileName (true);
        if (filename == null)
            return;

        try
        {
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
    private String getFileName (final boolean isNew)
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
                return null;

            filename = fn.getAbsolutePath ();
            this.configuration.setFilename (filename);
            return filename;
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not create file dialog.", ex);
            return null;
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
        return this.handlers.get (command).getCommandValue (command);
    }


    /**
     * Handle a command.
     *
     * @param slotIndex The slot index where the command is stored
     * @param value The received parameter value to handle
     */
    private void handleCommand (final int slotIndex, final int value)
    {
        if (slotIndex < 0)
            return;

        final CommandSlot commandSlot = this.configuration.getCommandSlots ()[slotIndex];
        final FlexiCommand command = commandSlot.getCommand ();
        if (command == FlexiCommand.OFF)
            return;

        this.isUpdatingValue = true;
        this.handlers.get (command).handle (command, commandSlot.getKnobMode (), value);

        this.host.scheduleTask ( () -> {
            this.valueCache[slotIndex] = this.getCommandValue (command);
            this.isUpdatingValue = false;
        }, 400);
    }


    /**
     * Send back the current value type of a command slot to the device.
     *
     * @param slot The slot
     * @param value The value to reflect
     */
    private void reflectValue (final CommandSlot slot, final int value)
    {
        switch (slot.getType ())
        {
            case CommandSlot.TYPE_CC:
                if (value >= 0 && value <= 127)
                    this.getMidiOutput ().sendCCEx (slot.getMidiChannel (), slot.getNumber (), value);
                break;

            case CommandSlot.TYPE_PITCH_BEND:
                if (value >= 0 && value <= 127)
                    this.getMidiOutput ().sendPitchbend (slot.getMidiChannel (), 0, value);
                break;

            default:
                // Other types not supported
                break;
        }
    }
}