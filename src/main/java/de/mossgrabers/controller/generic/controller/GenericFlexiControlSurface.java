// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

import de.mossgrabers.controller.generic.CommandSlot;
import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.flexihandler.BrowserHandler;
import de.mossgrabers.controller.generic.flexihandler.ClipHandler;
import de.mossgrabers.controller.generic.flexihandler.DeviceHandler;
import de.mossgrabers.controller.generic.flexihandler.FxTrackHandler;
import de.mossgrabers.controller.generic.flexihandler.GlobalHandler;
import de.mossgrabers.controller.generic.flexihandler.IFlexiCommandHandler;
import de.mossgrabers.controller.generic.flexihandler.LayoutHandler;
import de.mossgrabers.controller.generic.flexihandler.MarkerHandler;
import de.mossgrabers.controller.generic.flexihandler.MasterHandler;
import de.mossgrabers.controller.generic.flexihandler.MidiCCHandler;
import de.mossgrabers.controller.generic.flexihandler.ModesHandler;
import de.mossgrabers.controller.generic.flexihandler.SceneHandler;
import de.mossgrabers.controller.generic.flexihandler.TrackHandler;
import de.mossgrabers.controller.generic.flexihandler.TransportHandler;
import de.mossgrabers.controller.generic.flexihandler.UserHandler;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.controller.valuechanger.Relative2ValueChanger;
import de.mossgrabers.framework.controller.valuechanger.Relative3ValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.StringUtils;

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
    private final IValueChanger                           relative2ValueChanger = new Relative2ValueChanger (128, 6, 1);
    private final IValueChanger                           relative3ValueChanger = new Relative3ValueChanger (128, 6, 1);

    private final IModel                                  model;
    private final int []                                  valueCache            = new int [GenericFlexiConfiguration.NUM_SLOTS];
    private final Map<FlexiCommand, IFlexiCommandHandler> handlers              = new EnumMap<> (FlexiCommand.class);

    private boolean                                       isShiftPressed        = false;
    private boolean                                       isUpdatingValue       = false;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public GenericFlexiControlSurface (final IHost host, final IModel model, final ColorManager colorManager, final GenericFlexiConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, null, 10, 10);

        this.model = model;

        this.registerHandler (new GlobalHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new TransportHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new LayoutHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new TrackHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new FxTrackHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new MasterHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new DeviceHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new BrowserHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new SceneHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new ClipHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new MarkerHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        this.registerHandler (new ModesHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger, host));
        this.registerHandler (new MidiCCHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        if (host.hasUserParameters ())
            this.registerHandler (new UserHandler (model, this, configuration, this.relative2ValueChanger, this.relative3ValueChanger));

        Arrays.fill (this.valueCache, -1);

        this.configuration.addSettingObserver (GenericFlexiConfiguration.BUTTON_EXPORT, this::importFile);
        this.configuration.addSettingObserver (GenericFlexiConfiguration.BUTTON_IMPORT, this::exportFile);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        if (this.isUpdatingValue)
            return;

        final CommandSlot [] slots = this.configuration.getCommandSlots ();
        for (int i = 0; i < slots.length; i++)
        {
            final FlexiCommand command = slots[i].getCommand ();
            if (command == FlexiCommand.OFF || !slots[i].isSendValue ())
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
     * Set the knob speed on all value changers.
     *
     * @param isSlow True to set to slow otherwise fast
     */
    public void setKnobSpeed (final boolean isSlow)
    {
        this.model.getValueChanger ().setSpeed (isSlow);
        this.relative2ValueChanger.setSpeed (isSlow);
        this.relative3ValueChanger.setSpeed (isSlow);
    }


    /**
     * Update all knob speeds from the configuration settings.
     */
    public void updateKnobSpeeds ()
    {
        final double fraction = 128 * this.configuration.getKnobSpeedNormal () / 100.0;
        this.model.getValueChanger ().setFractionValue (fraction);
        this.relative2ValueChanger.setFractionValue (fraction);
        this.relative3ValueChanger.setFractionValue (fraction);

        final double slowFraction = 128 * this.configuration.getKnobSpeedSlow () / 100.0;
        this.model.getValueChanger ().setSlowFractionValue (slowFraction);
        this.relative2ValueChanger.setSlowFractionValue (slowFraction);
        this.relative3ValueChanger.setSlowFractionValue (slowFraction);
    }


    /**
     * Activate a new mode.
     *
     * @param modeID The ID of the new mode
     */
    public void activateMode (final Modes modeID)
    {
        final String modeName = this.modeManager.getMode (modeID).getName ();

        if (!this.modeManager.isActiveMode (modeID))
        {
            this.modeManager.setActiveMode (modeID);
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

        int slotIndex = -1;
        int value = data2;

        switch (code)
        {
            // Note on/off
            case 0x90:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_NOTE + 1], data1, channel);
                slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_NOTE, data1, channel);
                break;

            // Program Change
            case 0xC0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_PROGRAM_CHANGE + 1], data1, channel);
                slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_PROGRAM_CHANGE, data1, channel);
                value = 127;
                break;

            // CC
            case 0xB0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_CC + 1], data1, channel);
                slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_CC, data1, channel);
                break;

            // Pitchbend
            case 0xE0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_PITCH_BEND + 1], data1, channel);
                slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_PITCH_BEND, data1, channel);
                break;

            default:
                // Not used
                break;
        }

        if (slotIndex != -1)
            this.handleCommand (slotIndex, value);
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
     * Export all settings to a file.
     */
    private void exportFile ()
    {
        final File file = this.getFile ();
        if (file == null)
            return;
        if (!file.exists ())
        {
            this.host.showNotification ("The entered file does not exist.");
            return;
        }
        try
        {
            this.configuration.importFrom (file);
            this.host.showNotification ("Imported from: " + file);
        }
        catch (final IOException ex)
        {
            this.host.showNotification ("Error reading file: " + ex.getMessage ());
        }
    }


    /**
     * Import all settings from a file.
     */
    private void importFile ()
    {
        final File file = this.getFile ();
        if (file == null)
            return;
        try
        {
            this.configuration.exportTo (file);
            this.host.showNotification ("Exported to: " + file);
        }
        catch (final IOException ex)
        {
            this.host.showNotification ("Error writing file: " + ex.getMessage ());
        }
    }


    /**
     * Get a the im-/export file.
     *
     * @return The file or null
     */
    private File getFile ()
    {
        final String filename = this.configuration.getFilename ();
        if (filename == null || filename.trim ().isEmpty ())
        {
            this.host.showNotification ("Please enter a filename first.");
            return null;
        }
        return new File (filename);
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


    /**
     * Register a flexi command handler.
     *
     * @param handler The handler to register
     */
    private void registerHandler (final IFlexiCommandHandler handler)
    {
        Arrays.asList (handler.getSupportedCommands ()).forEach (command -> this.handlers.put (command, handler));
    }
}