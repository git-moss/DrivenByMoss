// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.controller;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.mode.AbstractElectraOneMode;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * A control surface which supports the Electra.One controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOneControlSurface extends AbstractControlSurface<ElectraOneConfiguration>
{
    /** The MIDI CC of the first control on a page. */
    public static final int                ELECTRA_CTRL_1    = 10;
    /** The MIDI CC of the first button on a page. */
    public static final int                ELECTRA_ROW_1     = 70;

    // Controller events to subscribe to: page, pots (= knob touch) and buttons
    private static final int               SUBSCRIBED_EVENTS = 0x01 | 0x08 | 0x20;

    /** The IDs for the continuous elements. */
    public static final List<ContinuousID> KNOB_IDS          = new ArrayList<> ();
    static
    {
        KNOB_IDS.addAll (ContinuousID.createSequentialList (ContinuousID.VOLUME_KNOB1, 6));
        KNOB_IDS.addAll (ContinuousID.createSequentialList (ContinuousID.PAN_KNOB1, 6));
        KNOB_IDS.addAll (ContinuousID.createSequentialList (ContinuousID.FADER1, 6));
        KNOB_IDS.addAll (ContinuousID.createSequentialList (ContinuousID.KNOB1, 6));
        KNOB_IDS.addAll (ContinuousID.createSequentialList (ContinuousID.PARAM_KNOB1, 6));
        KNOB_IDS.addAll (ContinuousID.createSequentialList (ContinuousID.DEVICE_KNOB1, 6));
    }

    private static final ButtonID []     BUTTON_ROW_IDS                    =
    {
        ButtonID.ROW1_1,
        ButtonID.ROW2_1,
        ButtonID.ROW3_1,
        ButtonID.ROW4_1,
        ButtonID.ROW5_1,
        ButtonID.ROW6_1
    };

    private static final ContinuousID [] CTRL_ROW_IDS                      =
    {
        ContinuousID.VOLUME_KNOB1,
        ContinuousID.PAN_KNOB1,
        ContinuousID.FADER1,
        ContinuousID.KNOB1,
        ContinuousID.PARAM_KNOB1,
        ContinuousID.DEVICE_KNOB1,
    };

    // Sysex

    private static final byte []         SYSEX_HDR_BYTE                    =
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x45
    };

    private static final int []          SYSEX_HDR_INT                     =
    {
        0xF0,
        0x00,
        0x21,
        0x45
    };

    // @formatter:off
    
    private static final byte []         SYSEX_INFO_DEVICE                 = { 0x02, 0x7F };
    private static final byte []         SYSEX_INFO_PRESET_LIST            = { 0x02, 0x04 };
    private static final byte []         SYSEX_RUNTIME_EXECUTE_LUA         = { 0x08, 0x0D };
    private static final byte []         SYSEX_RUNTIME_SWITCH_PRESET       = { 0x09, 0x08 };
    private static final byte []         SYSEX_RUNTIME_SWITCH_PAGE         = { 0x09, 0x0A };
    private static final byte []         SYSEX_RUNTIME_CONTROL_UPDATE      = { 0x14, 0x07 };
    private static final byte []         SYSEX_RUNTIME_VALUE_LABEL_UPDATE  = { 0x14, 0x0E };
    private static final byte []         SYSEX_RUNTIME_SUBSCRIBE_EVENTS    = { 0x14, 0x79 };
    private static final byte []         SYSEX_RUNTIME_SET_REPAINT_ENABLED = { 0x7F, 0x7A };
    private static final byte []         SYSEX_RUNTIME_ENABLE_LOGGER       = { 0x7F, 0x7D };
    
    private static final int []          TOUCH_PATTERN_SHIFT         = new int [] { 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2 };
    private static final int []          TOUCH_PATTERN_MIXER         = new int [] { 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final int []          TOUCH_PATTERN_SENDS         = new int [] { 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final int []          TOUCH_PATTERN_DEVICES       = new int [] { 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
    private static final int []          TOUCH_PATTERN_EQ            = new int [] { 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0 };
    private static final int []          TOUCH_PATTERN_TRANSPORT     = new int [] { 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0 };
    private static final int []          TOUCH_PATTERN_SESSION       = new int [] { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0 };
    private static final int []          TOUCH_PATTERN_NATIVE_DEVICE = new int [] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 };

    // @formatter:on

    private static final int             CMD_START_POS                     = SYSEX_HDR_INT.length;
    private static final int             SUB_CMD_START_POS                 = SYSEX_HDR_INT.length + 1;

    // Command categories
    private static final int             CMD_INFO                          = 0x01;
    private static final int             CMD_CONTROLLER                    = 0x7E;
    private static final int             CMD_SYSTEM_CALL                   = 0x7F;

    // IDs for runtime commands
    private static final int             EVENT_PRESET_SWITCH               = 0x02;
    private static final int             EVENT_PAGE_SWITCH                 = 0x06;
    private static final int             EVENT_POT_TOUCH                   = 0x0A;

    // IDs for system commands
    private static final int             SYSTEM_CALL_LOGGING               = 0x00;

    // IDs for information commands
    private static final int             INFO_PRESET_LIST                  = 0x04;
    private static final int             INFO_DEVICE                       = 0x7F;

    private static final Modes []        MODES                             =
    {
        Modes.VOLUME,
        Modes.SEND,
        Modes.DEVICE_PARAMS,
        Modes.EQ_DEVICE_PARAMS,
        Modes.TRANSPORT,
        Modes.SESSION
    };

    private static final String          SET_GROUP_TITLE                   = "sgt(%s,\"%s\")";

    private final List<int []>           sysexChunks                       = new ArrayList<> ();
    private final IMidiInput             ctrlInput;
    private final IMidiOutput            ctrlOutput;
    private final ObjectMapper           mapper                            = new ObjectMapper ();
    private final Map<String, Integer>   presetBanks                       = new HashMap<> ();
    private final Map<String, Integer>   presetIndices                     = new HashMap<> ();
    private int                          bankIndex                         = -1;
    private int                          presetIndex                       = -1;
    private boolean                      isOnline                          = false;
    private int []                       knobStates                        = new int [12];
    private boolean                      isShiftPressed;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param ctrlOutput The control MIDI output
     * @param ctrlInput The control MIDI input
     */
    public ElectraOneControlSurface (final IHost host, final ColorManager colorManager, final ElectraOneConfiguration configuration, final IMidiOutput output, final IMidiInput input, final IMidiInput ctrlInput, final IMidiOutput ctrlOutput)
    {
        super (host, configuration, colorManager, output, input, null, 100, 200);

        this.ctrlInput = ctrlInput;
        this.ctrlOutput = ctrlOutput;

        this.ctrlInput.setSysexCallback (this::handleSysEx);
    }


    /**
     * Set the label of a group element.
     *
     * @param groupID The element starting from 1, increasing from left to right, top to bottom
     * @param label The label to set
     */
    public void updateGroupLabel (final int groupID, final String label)
    {
        this.sendLua (String.format (SET_GROUP_TITLE, Integer.toString (groupID), label));
    }


    /**
     * Update a value of an element on the Electra.One.
     *
     * @param midiCC The midiCC of the element
     * @param value The value
     */
    public void updateValue (final int midiCC, final int value)
    {
        this.output.sendCCEx (15, midiCC, value);
    }


    /**
     * Sets name, color and visibility of an element on the Electra.One.
     *
     * @param controlID The element starting from 1, increasing from left to right, top to bottom
     * @param name The name to set
     * @param color The color to set
     * @param visibility The visibility to set
     */
    public void updateLabel (final int controlID, final String name, final ColorEx color, final Boolean visibility)
    {
        final StringWriter writer = new StringWriter ();
        try (final JsonGenerator generator = this.mapper.createGenerator (writer))
        {
            generator.writeStartObject ();
            if (name != null)
                generator.writeStringField ("name", name);
            if (color != null)
                generator.writeStringField ("color", StringUtils.formatColor (color));
            if (visibility != null)
                generator.writeBooleanField ("visible", visibility.booleanValue ());
            generator.writeEndObject ();
        }
        catch (final IOException ex)
        {
            this.host.error (name, ex);
            return;
        }

        this.sendText (createCommand (controlID, SYSEX_RUNTIME_CONTROL_UPDATE), writer.toString ());
    }


    /**
     * Sets the label (description) of a value of an element on the Electra.One.
     *
     * @param controlID The element starting from 1, increasing from left to right, top to bottom
     * @param label The label to set
     */
    public void updateValueLabel (final int controlID, final String label)
    {
        final byte [] cmd = createCommand (controlID, SYSEX_RUNTIME_VALUE_LABEL_UPDATE);
        final byte [] command = new byte [cmd.length + 1];
        System.arraycopy (cmd, 0, command, 0, cmd.length);
        command[cmd.length] = 0;
        this.sendText (command, label);
    }


    /**
     * Send the current logging setting to the device.
     */
    public void setLoggingEnabled ()
    {
        final byte [] content = new byte []
        {
            (byte) (this.configuration.isLogToConsoleEnabled () ? 0x01 : 0x00),
            0x00 // reserved
        };

        this.sendSysex (SYSEX_RUNTIME_ENABLE_LOGGER, content);
    }


    /**
     * Enable or disable the display repaint process on the device.
     *
     * @param enable True to enable
     */
    public void setRepaintEnabled (final boolean enable)
    {
        final byte [] content = new byte []
        {
            (byte) (enable ? 0x01 : 0x00),
            0x00 // reserved
        };

        this.sendSysex (SYSEX_RUNTIME_SET_REPAINT_ENABLED, content);
    }


    /**
     * Send the current logging setting to the device.
     */
    public void requestDeviceInfo ()
    {
        this.sendSysex (SYSEX_INFO_DEVICE, new byte [0]);
    }


    /**
     * Request the list of presets installed in the device.
     */
    private void requestPresetList ()
    {
        this.sendSysex (SYSEX_INFO_PRESET_LIST, new byte [0]);
    }


    /**
     * Select the DrivenByMoss preset on the device.
     */
    public void selectDrivenByMossPreset ()
    {
        this.selectPreset (this.bankIndex, this.presetIndex);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return this.isShiftPressed;
    }


    /**
     * Select a preset on the device.
     *
     * @param bank The index of the bank (0-11)
     * @param preset The index of the preset (0-11)
     */
    private void selectPreset (final int bank, final int preset)
    {
        this.host.println (String.format ("Selecting preset: %d-%d", Integer.valueOf (bank), Integer.valueOf (preset)));
        this.sendSysex (SYSEX_RUNTIME_SWITCH_PRESET, new byte []
        {
            (byte) bank,
            (byte) preset
        });
    }


    /**
     * Select a page of the active preset on the device.
     *
     * @param page The index of the preset page (0-11)
     */
    private void selectPage (final int page)
    {
        this.host.println (String.format ("Selecting page: %d", Integer.valueOf (page)));
        this.sendSysex (SYSEX_RUNTIME_SWITCH_PAGE, new byte []
        {
            (byte) page
        });
    }


    /**
     * Send LUA code for execution to the Electra.One.
     *
     * @param code The code to send
     */
    private void sendLua (final String code)
    {
        this.sendText (SYSEX_RUNTIME_EXECUTE_LUA, code);
    }


    /**
     * Send a JSON or LUA string to the CTRL output. Removes and/or replaces non-ASCII characters.
     *
     * @param command The command bytes
     * @param text The JSON to send
     */
    private void sendText (final byte [] command, final String text)
    {
        this.sendSysex (command, StringUtils.fixASCII (text).getBytes ());
    }


    /**
     * Send a byte array to the CTRL output of the Electra.One.
     *
     * @param command The command bytes
     * @param content The content bytes
     */
    private void sendSysex (final byte [] command, final byte [] content)
    {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream ())
        {
            out.write (SYSEX_HDR_BYTE);
            out.write (command);
            out.write (content);
            out.write ((byte) 0xF7);
            this.ctrlOutput.sendSysex (out.toByteArray ());
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not schedule JSON command.", ex);
        }
    }


    /**
     * Handle incoming system exclusive data. Messages are split up in chunks of 1024 bytes! This
     * method concatenates and stores the parts until the full message is received and then hands it
     * to the processing.
     *
     * @param dataStr The data
     */
    private void handleSysEx (final String dataStr)
    {
        final int [] data = StringUtils.fromHexStr (dataStr);

        int [] fullData = null;

        synchronized (this.sysexChunks)
        {
            if (data[0] == 0xF0 && !this.sysexChunks.isEmpty ())
            {
                this.host.error ("Unsound sysex message without ending F7 received.");
                this.sysexChunks.clear ();
            }
            this.sysexChunks.add (data);

            if (data[data.length - 1] == 0xF7)
            {
                fullData = this.concatChunks ();
                this.sysexChunks.clear ();
            }
        }

        if (fullData != null)
            this.processSysEx (fullData);
    }


    private int [] concatChunks ()
    {
        if (this.sysexChunks.size () == 1)
            return this.sysexChunks.get (0);

        // Determine the total length of the resulting array
        int totalLength = 0;
        for (final int [] array: this.sysexChunks)
            totalLength += array.length;

        // Create a new array to hold the concatenated arrays
        final int [] result = new int [totalLength];

        // Copy the arrays into the result array
        int offset = 0;
        for (final int [] array: this.sysexChunks)
        {
            System.arraycopy (array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }


    private void processSysEx (final int [] data)
    {
        if (Arrays.compareUnsigned (SYSEX_HDR_INT, 0, SYSEX_HDR_INT.length, data, 0, SYSEX_HDR_INT.length) != 0)
            return;

        final int subCmdID = data[SUB_CMD_START_POS];

        switch (data[CMD_START_POS])
        {
            case CMD_INFO:
                this.handleSysexCommandsInfo (subCmdID, data);
                break;

            case CMD_CONTROLLER:
                this.handleSysexCommandsController (subCmdID, data);
                break;

            case CMD_SYSTEM_CALL:
                if (subCmdID == SYSTEM_CALL_LOGGING)
                    this.logMessage (data, 6);
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Handle all sysex messages for controller commands.
     *
     * @param commandID The information command ID
     * @param data The information data
     */
    private void handleSysexCommandsController (final int commandID, final int [] data)
    {
        switch (commandID)
        {
            // Take the extension on-/offline depending on which template is selected
            case EVENT_PRESET_SWITCH:
                this.handleOnlineStatus (data[SUB_CMD_START_POS + 1], data[SUB_CMD_START_POS + 2]);
                break;

            // Change modes if extension is online
            case EVENT_PAGE_SWITCH:
                if (this.isOnline)
                {
                    final int page = data[SUB_CMD_START_POS + 1];
                    if (page >= 0 && page < MODES.length)
                    {
                        this.host.println ("Switching to mode: " + MODES[page].name ());
                        this.getModeManager ().setActive (MODES[page]);
                    }
                }
                break;

            case EVENT_POT_TOUCH:
                final IMode active = this.getModeManager ().getActive ();
                if (active instanceof final AbstractElectraOneMode electraMode)
                {
                    final int potID = data[SUB_CMD_START_POS + 1];
                    final int controlID = (data[SUB_CMD_START_POS + 3] << 7) + data[SUB_CMD_START_POS + 2];
                    if (potID < 0 || potID >= 12)
                    {
                        this.host.error ("Touch event with knob ID outside of range: " + potID);
                        return;
                    }

                    this.knobStates[potID] = data[SUB_CMD_START_POS + 4];
                    electraMode.setEditing (controlID, this.knobStates[potID] > 0);

                    this.matchStates ();

                    // TODO remove
                    final StringBuilder sb = new StringBuilder ();
                    for (int i = 0; i < 12; i++)
                    {
                        if (i == 6)
                            sb.append ('\n');
                        sb.append (this.knobStates[i]).append (' ');
                    }
                    this.host.println (sb.toString ());
                }
                break;

            default:
                // Ignore
                break;
        }
    }


    private void matchStates ()
    {
        if (compareWithIgnore (this.knobStates, TOUCH_PATTERN_SHIFT))
        {
            this.updateShift (true);
            return;
        }

        if (Arrays.equals (this.knobStates, TOUCH_PATTERN_MIXER))
            this.selectPage (0);
        else if (Arrays.equals (this.knobStates, TOUCH_PATTERN_SENDS))
            this.selectPage (1);
        else if (Arrays.equals (this.knobStates, TOUCH_PATTERN_DEVICES))
            this.selectPage (2);
        else if (Arrays.equals (this.knobStates, TOUCH_PATTERN_EQ))
            this.selectPage (3);
        else if (Arrays.equals (this.knobStates, TOUCH_PATTERN_TRANSPORT))
            this.selectPage (4);
        else if (Arrays.equals (this.knobStates, TOUCH_PATTERN_SESSION))
            this.selectPage (5);
        else if (Arrays.equals (this.knobStates, TOUCH_PATTERN_NATIVE_DEVICE))
            this.switchToSpecificDevicePreset ();
        else
        {
            this.updateShift (false);
            return;
        }

        // Prevent hanging states
        Arrays.fill (this.knobStates, 0);
    }


    private void switchToSpecificDevicePreset ()
    {
        // If the DrivenByMoss preset is not selected, jump back to it
        if (!this.isOnline)
        {
            this.selectDrivenByMossPreset ();
            return;
        }

        // Check if there is a preset with the name of the cursor device, if none is found selected
        // the device mode
        if (this.modeManager.getActive () instanceof AbstractElectraOneMode electraMode)
        {
            final Optional<String> deviceNameOpt = electraMode.getActiveDeviceName ();
            if (deviceNameOpt.isPresent ())
            {
                final String deviceName = deviceNameOpt.get ();
                final Integer bank = this.presetBanks.get (deviceName);
                if (bank == null)
                {
                    this.host.println (String.format ("No specific preset found for '%s', switching to device mode.", deviceName));
                    this.selectPage (2);
                }
                else
                {
                    final Integer slot = this.presetIndices.get (deviceName);
                    this.host.println (String.format ("Found preset found for '%s' at %d-%d.", deviceName, bank, slot));
                    this.selectPreset (bank.intValue (), slot.intValue ());
                }

                // Prevent hanging states
                Arrays.fill (this.knobStates, 0);
            }
        }
    }


    private void updateShift (final boolean isShift)
    {
        this.isShiftPressed = isShift;

        // TODO remove
        this.host.println ("Shift: " + this.isShiftPressed);
        this.setKnobSensitivityIsSlow (this.isShiftPressed);
    }


    /**
     * Compares the two given arrays. If the pattern array contains a 2 at a position it is ignored.
     *
     * @param knobStates The state array
     * @param touchPattern The pattern array
     * @return True if pattern matches
     */
    private static boolean compareWithIgnore (final int [] knobStates, final int [] touchPattern)
    {
        for (int i = 0; i < touchPattern.length; i++)
        {
            if (touchPattern[i] != 2 && touchPattern[i] != knobStates[i])
                return false;
        }
        return true;
    }


    /**
     * Handle all sysex messages for info commands.
     *
     * @param commandID The information command ID
     * @param data The information data
     */
    private void handleSysexCommandsInfo (final int commandID, final int [] data)
    {
        final JsonNode content = this.getContent (data);
        switch (commandID)
        {
            case INFO_DEVICE:
                this.checkFirmwareResult (content);

                this.sendSysex (SYSEX_RUNTIME_SUBSCRIBE_EVENTS, new byte []
                {
                    SUBSCRIBED_EVENTS
                });

                this.requestPresetList ();
                break;

            case INFO_PRESET_LIST:
                this.findAndSelectDrivenByMossPreset (content);
                this.selectDrivenByMossPreset ();
                break;

            default:
                // Ignore
                break;
        }
    }


    /**
     * Check if the Firmware on the device is valid.
     *
     * @param root The JSON with the content
     */
    private void checkFirmwareResult (final JsonNode root)
    {
        final int version = root.get ("versionSeq").asInt ();
        final String versionText = root.get ("versionText").asText ();
        if (version < 300000000)
            throw new FrameworkException ("Firmware must be at least 3.0 but is " + versionText);

        this.host.println ("Firmware: " + versionText);
    }


    /**
     * Put the extension in online/offline mode depending on the selected preset.
     *
     * @param bank The index of the bank (0-11)
     * @param preset The index of the preset (0-11)
     */
    private void handleOnlineStatus (final int bank, final int preset)
    {
        if (this.bankIndex == bank && this.presetIndex == preset)
            this.setOnline ();
        else
        {
            this.host.println ("Going offline...");
            this.isOnline = false;
            this.unbindAllInputControls ();
        }
    }


    /**
     * Set the extension online.
     */
    private void setOnline ()
    {
        this.host.println ("Going online...");
        this.isOnline = true;
        this.rebindAllInputControls ();

        final IMode active = this.getModeManager ().getActive ();
        if (active != null)
            active.onDeactivate ();

        // Switching templates always selects 1st page
        this.getModeManager ().setActive (Modes.VOLUME);

        // Refresh all control UIs
        this.forceFlush ();
    }


    /**
     * Find the DrivenByMoss template in the template list and store the bank and preset index.
     *
     * @param root The JSON structure containing the preset list information
     */
    private void findAndSelectDrivenByMossPreset (final JsonNode root)
    {
        for (final JsonNode preset: root.get ("presets"))
        {
            final String presetName = preset.get ("name").asText ();
            final int bankNumber = preset.get ("bankNumber").asInt ();
            final int slot = preset.get ("slot").asInt ();

            if ("DrivenByMoss".equals (presetName))
            {
                this.bankIndex = bankNumber;
                this.presetIndex = slot;
                this.host.println (String.format ("DrivenByMoss template at: %d-%d", Integer.valueOf (this.bankIndex), Integer.valueOf (this.presetIndex)));
            }
            else
            {
                this.presetBanks.put (presetName, Integer.valueOf (bankNumber));
                this.presetIndices.put (presetName, Integer.valueOf (slot));
            }
        }

        if (this.bankIndex == -1)
            throw new FrameworkException ("The DrivenByMoss template is not installed on the Electra.One.");
    }


    /**
     * Log an information message to the console if logging is enabled.
     *
     * @param data The data to log
     * @param contentStart The start of the text message to log
     */
    private void logMessage (final int [] data, final int contentStart)
    {
        if (this.configuration.isLogToConsoleEnabled ())
            this.host.println (new String (data, contentStart, data.length - contentStart - 1));
    }


    /**
     * Get and parse the JSON content of an information message
     *
     * @param data The data of the information message
     * @return The root node of the JSON structure
     */
    private JsonNode getContent (final int [] data)
    {
        final String content = StringUtils.integerArrayToString (SUB_CMD_START_POS + 1, data.length - SUB_CMD_START_POS - 2, data);
        try
        {
            return this.mapper.readValue (content, JsonNode.class);
        }
        catch (final JsonProcessingException ex)
        {
            throw new FrameworkException ("Could not parse JSON information.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        // Only send MIDI to the device if the DrivenByMoss template is selected
        if (this.isOnline)
            super.setTrigger (bindType, channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        // Not used
    }


    /**
     * Is the DrivenByMoss preset selected?
     *
     * @return True if selected
     */
    public boolean isOnline ()
    {
        return this.isOnline;
    }


    /**
     * Get the button ID for an element on the page.
     *
     * @param row The row of the button
     * @param column The column of the button
     * @return The ID
     */
    public static ButtonID getButtonID (final int row, final int column)
    {
        return ButtonID.get (BUTTON_ROW_IDS[row], column);
    }


    /**
     * Get the continuous ID for an element on the page.
     *
     * @param row The row of the control
     * @param column The column of the control
     * @return The ID
     */
    public static ContinuousID getContinuousID (final int row, final int column)
    {
        return ContinuousID.get (CTRL_ROW_IDS[row], column);
    }


    private static final byte [] createCommand (final int controlID, final byte [] commandID)
    {
        final byte [] command = new byte [4];
        command[0] = commandID[0];
        command[1] = commandID[1];
        command[2] = (byte) (controlID & 0x7F);
        command[3] = (byte) (controlID >> 7);
        return command;
    }
}