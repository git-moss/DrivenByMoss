// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrolosc.protocol;

import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.kontrolosc.KontrolOSCConfiguration;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscMessage;
import com.bitwig.extension.api.opensoundcontrol.OscMethodCallback;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Parser for OSC message from the NI Host.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCParser implements OscMethodCallback
{
    private static final int []  DOUBLE_TRUE = new int []
    {
        1,
        1
    };

    private final OSCModel       model;
    private final ITransport     transport;
    private final ControllerHost host;
    private final OSCWriter      writer;
    private final boolean        logEnabled;
    private final String         daw;
    private boolean              is16;


    /**
     * Constructor.
     * 
     * @param is16 If true use 1.6 protocol otherwise 1.5
     * @param logEnabled Enable message logging
     * @param host The host
     * @param writer The OSC writer
     * @param configuration The configuration
     * @param model The model
     */
    public OSCParser (final boolean is16, final boolean logEnabled, final ControllerHost host, final OSCWriter writer, final KontrolOSCConfiguration configuration, final OSCModel model)
    {
        this.is16 = is16;
        this.logEnabled = logEnabled;
        this.host = host;
        this.writer = writer;
        this.model = model;

        this.daw = is16 ? "dawctrl" : "live";

        this.transport = this.model.getTransport ();

        this.model.getCurrentTrackBank ().setIndication (true);
        this.model.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final OscConnection source, final OscMessage message)
    {
        final LinkedList<String> oscParts = parseAddress (message);
        if (oscParts.isEmpty ())
            return;

        this.logMessage (message, this.logEnabled, false);

        final String command = oscParts.removeFirst ();
        if ("script".equals (command))
            this.parseHostCommands (oscParts);
        else if (this.daw.equals (command))
            this.parseDAWCommands (message, oscParts, message.getArguments ());
        else
            this.host.println ("Unknown OSC Command: " + message.getAddressPattern ());
    }


    private void parseHostCommands (final LinkedList<String> oscParts)
    {
        if (oscParts.isEmpty ())
        {
            this.host.errorln ("Missing Script subcommand.");
            return;
        }
        final String subCommand = oscParts.get (0);
        switch (subCommand)
        {
            case "ping": // 1.5
                this.writer.fastSendOSC ("/script/pong");
                break;

            case "init": // 1.5
                this.host.println ("Init received...");
                this.writer.sendFrequentProperties (true);
                break;

            default:
                this.host.errorln ("Unknown Script subcommand: " + subCommand);
                break;
        }
    }


    private void parseDAWCommands (final OscMessage message, final LinkedList<String> oscParts, final List<Object> values)
    {
        if (oscParts.isEmpty ())
        {
            this.host.errorln ("Missing DAW subcommand.");
            return;
        }

        final String subCommand = oscParts.removeFirst ();

        if (this.parseTransportCommands (subCommand, oscParts, values))
            return;

        if (this.parseGlobalCommands (subCommand))
            return;

        switch (subCommand)
        {
            case "ping": // 1.6
                this.writer.fastSendOSC ("/dawctrl/pong");
                break;

            case "init": // 1.6
                this.host.println ("Init received...");
                this.writer.sendFrequentProperties (true);
                break;

            case "volume": // 1.5
            case "pan":
            case "arm":
            case "mute":
            case "solo":
                this.parseTrackCommands (subCommand, values);
                break;

            case "track": // 1.x
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Track command.");
                    return;
                }
                this.parseTrackCommands (oscParts.removeFirst (), values);
                break;

            case "return": // 1.5
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Track Return command.");
                    return;
                }
                if ("view".equals (oscParts.removeFirst ())) // 1.5
                {
                    final int trackIndex = toIntValue (values);
                    final IChannelBank effectTrackBank = this.model.getEffectTrackBank ();
                    if (effectTrackBank != null)
                        effectTrackBank.getTrack (trackIndex).selectAndMakeVisible ();
                }
                break;

            case "master": // 1.5
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Master command.");
                    return;
                }
                if ("view".equals (oscParts.removeFirst ())) // 1.5
                    this.model.getMasterTrack ().selectAndMakeVisible ();
                break;

            case "scene": // 1.x
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Scene command.");
                    return;
                }
                this.parseSceneCommands (oscParts.removeFirst (), values);
                break;

            default:
                this.host.println ("Unknown DAW Subcommand: " + message.getAddressPattern ());
                break;
        }
    }


    private boolean parseGlobalCommands (final String command)
    {
        switch (command)
        {
            case "undo": // 1.x
                this.model.getApplication ().undo ();
                this.sendOSC ("undo_redo", DOUBLE_TRUE);
                return true;

            case "redo": // 1.x
                this.model.getApplication ().redo ();
                this.sendOSC ("undo_redo", DOUBLE_TRUE);
                return true;

            default:
                return false;
        }
    }


    private boolean parseTransportCommands (final String command, final LinkedList<String> oscParts, final List<Object> values)
    {
        final int numValue = toIntValue (values);

        switch (command)
        {
            case "play": // 1.x
                if (oscParts.isEmpty ())
                {
                    if (!this.transport.isPlaying ())
                        this.transport.play ();
                    return true;
                }

                final String subCommand = oscParts.remove ();
                switch (subCommand)
                {
                    case "scene": // 1.6
                        this.model.getSceneBank ().launchScene (toIntValue (0, values));
                        break;

                    case "clipslot": // 1.5
                    case "clip": // 1.6
                        final IChannelBank tb = this.model.getCurrentTrackBank ();
                        if (tb != null)
                        {
                            final int trackIndex = toIntValue (0, values);
                            final int sceneIndex = toIntValue (1, values);
                            tb.getTrack (trackIndex).getSlot (sceneIndex).launch ();
                        }
                        break;

                    default:
                        this.host.errorln ("Unknown Play sub-command: " + subCommand);
                        break;
                }

                return true;

            case "record": // 1.x
                if (numValue > 0)
                    this.transport.record ();
                return true;

            case "session_record": // 1.x
                this.transport.setLauncherOverdub (numValue > 0);
                return true;

            case "automation": // 1.6
                if (numValue == 0 && this.transport.isWritingArrangerAutomation ())
                {
                    this.transport.toggleWriteArrangerAutomation ();
                    return true;
                }
                if (numValue == 1 && !this.transport.isWritingArrangerAutomation ())
                {
                    this.transport.toggleWriteArrangerAutomation ();
                    return true;
                }
                return true;

            case "session_automation_record": // 1.5
                if (numValue == 0 && this.transport.isWritingClipLauncherAutomation ())
                {
                    this.transport.toggleWriteClipLauncherAutomation ();
                    return true;
                }
                if (numValue == 1 && !this.transport.isWritingClipLauncherAutomation ())
                {
                    this.transport.toggleWriteClipLauncherAutomation ();
                    return true;
                }
                return true;

            case "stop": // 1.x
                if (oscParts.isEmpty ())
                {
                    if (this.transport.isPlaying ())
                        this.transport.play ();
                    else
                        this.transport.stopAndRewind ();
                    return true;
                }

                final String cmd = oscParts.remove ();
                switch (cmd)
                {
                    case "all_clips": // 1.6
                        this.model.getCurrentTrackBank ().stop ();
                        break;

                    case "track": // 1.6
                        this.model.getCurrentTrackBank ().getTrack (toIntValue (0, values)).stop ();
                        break;

                    case "clip": // 1.6
                        // Since you cannot run multiple clips on a track, it does the same as
                        // "track".
                        this.model.getCurrentTrackBank ().getTrack (toIntValue (0, values)).stop ();
                        break;

                    default:
                        this.host.errorln ("Unknown Stop sub-command: " + cmd);
                        break;
                }

                return true;

            case "loop": // 1.x
                this.transport.setLoop (numValue > 0);
                return true;

            case "metronome": // 1.x
                this.transport.setMetronome (numValue > 0);
                return true;

            case "tap_tempo": // 1.x
                this.transport.tapTempo ();
                return true;

            case "clip": // 1.x
                if (!oscParts.isEmpty () && oscParts.poll ().equals ("quantize"))
                    this.model.getClip ().quantize (1);
                return true;

            case "scrub": // 1.6
                // Scrub transport by the specified amount (currently the agent will send only -4.0f
                // or 4.0f, depending on the direction of the scrubbing).
                this.transport.changePosition (numValue > 0);
                return true;

            case "midi_arm_exclusive": // 1.6
                // Ignore, is handled by the DAW
                return true;

            default:
                return false;
        }
    }


    private void parseTrackCommands (final String command, final List<Object> values)
    {
        switch (command)
        {
            case "info": // 1.x
                final int [] vs = toIntValues (values);
                if (vs.length != 2)
                    return;
                this.writer.sendTrackInfo (vs[0], vs[1]);
                break;

            case "view": // 1.x
                final ITrackBank tb = this.model.getTrackBank ();
                if (this.is16)
                {
                    // 1.6
                    final int trackPosition = toIntValue (1, values);
                    if (trackPosition < 0)
                    {
                        // Normal channels are negative in reverse order
                        final int trackIndex = tb.getTrackCount () + trackPosition;
                        tb.getTrack (trackIndex).selectAndMakeVisible ();
                    }
                    else
                    {
                        // Send channels are positive, highest channel is master track
                        final IChannelBank tbe = this.model.getEffectTrackBank ();
                        if (tbe != null && trackPosition < tbe.getTrackCount ())
                            tbe.getTrack (trackPosition).selectAndMakeVisible ();
                        else
                            this.model.getMasterTrack ().selectAndMakeVisible ();
                    }
                }
                else
                {
                    // 1.5
                    final int trackIndex = toIntValue (values);
                    tb.getTrack (trackIndex).selectAndMakeVisible ();
                }
                break;

            case "arm": // 1.x
                this.getTrack (values).setRecArm (toIntValue (2, values) > 0);
                break;

            case "mute": // 1.x
                this.getTrack (values).setMute (toIntValue (2, values) > 0);
                break;

            case "solo": // 1.x
                this.getTrack (values).setSolo (toIntValue (2, values) > 0);
                break;

            case "volume": // 1.x
                this.getTrack (values).setVolume (this.model.getValueChanger ().fromNormalizedValue (toNumValue (2, values).doubleValue ()));
                break;

            case "pan": // 1.x
                this.getTrack (values).setPan (this.model.getValueChanger ().fromNormalizedValue ((toNumValue (2, values).doubleValue () + 1.0) / 2.0));
                break;

            default:
                this.host.println ("Unknown Track Command: " + command);
                break;
        }
    }


    private void parseSceneCommands (final String command, final List<Object> values)
    {
        switch (command)
        {
            case "view": // 1.6
                final IChannelBank tb = this.model.getCurrentTrackBank ();
                if (tb != null)
                {
                    final int sceneIndex = toIntValue (values);
                    tb.getSelectedTrack ().getSlot (sceneIndex).select ();
                    this.sendOSC ("scene", new int []
                    {
                        sceneIndex
                    });
                }
                break;

            default:
                this.host.println ("Unknown Scene Command: " + command);
                break;
        }
    }


    private ITrack getTrack (final List<Object> values)
    {
        return this.writer.getTrack (toIntValue (0, values), toIntValue (1, values));
    }


    private void sendOSC (final String command, final int [] numbers)
    {
        this.writer.fastSendOSC (new StringBuilder ("/").append (this.daw).append ('/').append (command).toString (), numbers);
    }


    private static LinkedList<String> parseAddress (final OscMessage message)
    {
        final LinkedList<String> oscParts = new LinkedList<> ();
        Collections.addAll (oscParts, message.getAddressPattern ().split ("/"));

        // Remove first empty element
        oscParts.removeFirst ();
        return oscParts;
    }


    private static int toIntValue (final List<Object> values)
    {
        return toNumValue (0, values).intValue ();
    }


    private static int toIntValue (final int index, final List<Object> values)
    {
        return toNumValue (index, values).intValue ();
    }


    private static Number toNumValue (final int index, final List<Object> values)
    {
        return index < values.size () ? toNumber (values.get (index)) : Integer.valueOf (-1);
    }


    private static int [] toIntValues (final List<Object> values)
    {
        final int size = values.size ();
        final int [] result = new int [size];
        for (int i = 0; i < size; i++)
            result[i] = toNumber (values.get (i)).intValue ();
        return result;
    }


    private static Number toNumber (final Object value)
    {
        return value == null || !(value instanceof Number) ? Integer.valueOf (-1) : (Number) value;
    }


    private void logMessage (final OscMessage message, final boolean log, final boolean logPing)
    {
        if (!log)
            return;
        final String address = message.getAddressPattern ();
        if (logPing || !address.contains ("ping"))
            this.model.getHost ().println ("Received: " + address + " " + message.getArguments ().toString ());
    }
}