// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.controller;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;


/**
 * A control surface which supports the Electra.One controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class ElectraOneControlSurface extends AbstractControlSurface<ElectraOneConfiguration>
{
    public static final int       ELECTRA_ONE_VOLUME1         = 10;
    public static final int       ELECTRA_ONE_MASTER_VOLUME   = 15;

    public static final int       ELECTRA_ONE_PAN1            = 20;
    public static final int       ELECTRA_ONE_PLAY_POSITION   = 25;

    public static final int       ELECTRA_ONE_ARM1            = 30;
    public static final int       ELECTRA_ONE_NEXT_TRACK_PAGE = 35;

    public static final int       ELECTRA_ONE_MUTE1           = 40;
    public static final int       ELECTRA_ONE_PREV_TRACK_PAGE = 45;

    public static final int       ELECTRA_ONE_SOLO1           = 50;
    public static final int       ELECTRA_ONE_RECORD          = 55;

    public static final int       ELECTRA_ONE_SELECT1         = 60;
    public static final int       ELECTRA_ONE_PLAY            = 65;

    public static final int       ELECTRA_ONE_SEND1           = 10;
    public static final int       ELECTRA_ONE_SEND2           = 20;
    public static final int       ELECTRA_ONE_SEND3           = 30;
    public static final int       ELECTRA_ONE_SEND4           = 40;
    public static final int       ELECTRA_ONE_SEND5           = 50;
    public static final int       ELECTRA_ONE_SEND6           = 60;
    public static final int       ELECTRA_ONE_CUE_VOLUME      = 15;

    // Sysex

    private static final byte []  SYSEX_HDR                   =
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x45
    };

    private static final byte []  SYSEX_UPDATE_ELEMENT        =
    {
        0x14,
        0x07
    };

    private static final byte []  SYSEX_EXECUTE_LUA           =
    {
        0x08,
        0x0D
    };

    private static final int []   SYSEX_LOGGING               =
    {
        0xF0,
        0x00,
        0x21,
        0x45,
        0x7F,
        0x00
    };

    private static final String   LOG_PAGE_CHANGE             = "displayPage: page shown: page=";
    private static final Modes [] MODES                       =
    {
        Modes.VOLUME,
        Modes.SEND
    };

    private final IMidiInput      ctrlInput;
    private final IMidiOutput     ctrlOutput;


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
        super (host, configuration, colorManager, output, input, null, 1000, 1000);

        this.ctrlInput = ctrlInput;
        this.ctrlOutput = ctrlOutput;

        this.ctrlInput.setSysexCallback (this::handleSysEx);
    }


    /**
     * Set the title of a group element.
     *
     * @param groupID The element starting from 1, increasing from left to right, top to bottom
     * @param cache The cache to use
     * @param title The title to set
     */
    public void updateGroupTitle (final int groupID, final String [] cache, final String title)
    {
        if (title.equals (cache[groupID]))
            return;
        cache[groupID] = title;
        this.sendLua ("setGroupTitle(" + groupID + ",\"" + title + "\")");
    }


    /**
     * Sets name, color and visibility of an element on the Electra.One.
     *
     * @param controlID The element starting from 1, increasing from left to right, top to bottom
     * @param cache The message is only send if the parameters are different from the previous call
     * @param name The name to set
     * @param color The color to set
     * @param visibility The visibility to set
     */
    public void updateElement (final int controlID, final String [] cache, final String name, final ColorEx color, final Boolean visibility)
    {
        final ObjectMapper mapper = new ObjectMapper ();

        final StringWriter writer = new StringWriter ();
        try (final JsonGenerator generator = mapper.createGenerator (writer))
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

        final String json = writer.toString ();

        if (json.equals (cache[controlID]))
            return;
        cache[controlID] = json;

        final byte [] command = new byte [4];
        command[0] = SYSEX_UPDATE_ELEMENT[0];
        command[1] = SYSEX_UPDATE_ELEMENT[1];
        command[2] = (byte) (controlID & 0x7F);
        command[3] = (byte) (controlID >> 7);
        this.sendText (command, json);
    }


    /**
     * Send LUA code for execution to the Electra.One.
     *
     * @param elementCache The message is only send if the code is different from the previous call
     * @param code The code to send
     */
    private void sendLua (final String code)
    {
        this.sendText (SYSEX_EXECUTE_LUA, code);
    }


    /**
     * Send a JSON or LUA string to the CTRL output.
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
            out.write (SYSEX_HDR);
            out.write (command);
            out.write (content);
            out.write ((byte) 0xF7);
            this.ctrlOutput.sendSysex (out.toByteArray ());
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not send JSON command.", ex);
        }
    }


    /**
     * Handle incoming system exclusive data.
     *
     * @param data The data
     */
    private void handleSysEx (final String data)
    {
        final int [] byteData = StringUtils.fromHexStr (data);
        final DeviceInquiry deviceInquiry = new DeviceInquiry (byteData);
        if (deviceInquiry.isValid ())
        {
            // TODO
            return;
        }

        if (Arrays.compareUnsigned (SYSEX_LOGGING, 0, SYSEX_LOGGING.length, byteData, 0, SYSEX_LOGGING.length) == 0)
        {
            final String message = new String (byteData, SYSEX_LOGGING.length, byteData.length - SYSEX_LOGGING.length - 1);
            if (this.configuration.isLogToConsoleEnabled ())
                this.host.println (message);

            // Bad hack for missing page change event, replace when it becomes available
            if (message.startsWith (LOG_PAGE_CHANGE))
            {
                final String rest = message.substring (LOG_PAGE_CHANGE.length ());
                final int pos = rest.indexOf (',');
                if (pos > 0)
                {
                    final int page = Integer.parseInt (rest.substring (0, pos));
                    if (page < MODES.length)
                        this.getModeManager ().setActive (MODES[page]);
                }
            }
        }
    }
}