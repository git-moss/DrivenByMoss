// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.osc.mkii.protocol;

import de.mossgrabers.controller.kontrol.osc.mkii.KontrolOSCConfiguration;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.controller.DummyControlSurface;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlParser;
import de.mossgrabers.framework.osc.IOpenSoundControlConfiguration;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Collections;
import java.util.LinkedList;


/**
 * Parser for OSC message from the NI Host.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolOSCParser extends AbstractOpenSoundControlParser
{
    private static final int []                          DOUBLE_TRUE = new int []
    {
        1,
        1
    };

    private final String                                 daw;
    private boolean                                      is16;

    private DummyControlSurface<KontrolOSCConfiguration> surface;


    /**
     * Constructor.
     *
     * @param is16 If true use 1.6 protocol otherwise 1.5
     * @param host The host
     * @param surface
     * @param writer The OSC writer
     * @param configuration The configuration
     * @param model The model
     */
    public KontrolOSCParser (final IHost host, final DummyControlSurface<KontrolOSCConfiguration> surface, final IModel model, final IOpenSoundControlConfiguration configuration, final IOpenSoundControlWriter writer, final boolean is16)
    {
        super (host, model, null, configuration, writer);

        this.surface = surface;
        this.is16 = is16;
        this.daw = is16 ? "dawctrl" : "live";

        this.model.getCurrentTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final IOpenSoundControlMessage message)
    {
        final LinkedList<String> oscParts = parseAddress (message.getAddress ());
        if (oscParts.isEmpty ())
            return;

        this.logMessage (message);

        final String command = oscParts.removeFirst ();
        if ("script".equals (command))
            this.parseHostCommands (oscParts);
        else if (this.daw.equals (command))
            this.parseDAWCommands (message, oscParts, message.getValues ());
        else
            this.host.println ("Unknown OSC Command: " + message.getAddress ());
    }


    private void parseHostCommands (final LinkedList<String> oscParts)
    {
        if (oscParts.isEmpty ())
        {
            this.host.error ("Missing Script subcommand.");
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
                this.writer.flush (true);
                break;

            default:
                this.host.error ("Unknown Script subcommand: " + subCommand);
                break;
        }
    }


    private void parseDAWCommands (final IOpenSoundControlMessage message, final LinkedList<String> oscParts, final Object [] objects)
    {
        if (oscParts.isEmpty ())
        {
            this.host.error ("Missing DAW subcommand.");
            return;
        }

        final String subCommand = oscParts.removeFirst ();

        if (this.parseTransportCommands (subCommand, oscParts, objects))
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
                this.writer.flush (true);
                break;

            case "volume": // 1.5
            case "pan":
            case "arm":
            case "mute":
            case "solo":
                this.parseTrackCommands (subCommand, objects);
                break;

            case "track": // 1.x
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Track command.");
                    return;
                }
                this.parseTrackCommands (oscParts.removeFirst (), objects);
                break;

            case "return": // 1.5
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Track Return command.");
                    return;
                }
                if ("view".equals (oscParts.removeFirst ())) // 1.5
                {
                    final int trackIndex = toIntValue (objects);
                    final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
                    if (effectTrackBank != null)
                        effectTrackBank.getItem (trackIndex).select ();
                }
                break;

            case "master": // 1.5
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Master command.");
                    return;
                }
                if ("view".equals (oscParts.removeFirst ())) // 1.5
                    this.model.getMasterTrack ().select ();
                break;

            case "scene": // 1.x
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Scene command.");
                    return;
                }
                this.parseSceneCommands (oscParts.removeFirst (), objects);
                break;

            default:
                this.host.println ("Unknown DAW Subcommand: " + message.getAddress ());
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


    private boolean parseTransportCommands (final String command, final LinkedList<String> oscParts, final Object [] objects)
    {
        final int numValue = toIntValue (objects);

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
                        this.model.getSceneBank ().getItem (toIntValue (0, objects)).launch ();
                        break;

                    case "clipslot": // 1.5
                    case "clip": // 1.6
                        final ITrackBank tb = this.model.getCurrentTrackBank ();
                        if (tb != null)
                        {
                            final int trackIndex = toIntValue (0, objects);
                            final int sceneIndex = toIntValue (1, objects);
                            tb.getItem (trackIndex).getSlotBank ().getItem (sceneIndex).launch ();
                        }
                        break;

                    default:
                        this.host.error ("Unknown Play sub-command: " + subCommand);
                        break;
                }

                return true;

            case "record": // 1.x
                if (numValue > 0)
                    this.handleShiftedRecordButton ();
                return true;

            case "session_record": // 1.x
                if (numValue > 0)
                    this.handleRecordButton ();
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
                        this.model.getCurrentTrackBank ().getItem (toIntValue (0, objects)).stop ();
                        break;

                    case "clip": // 1.6
                        // Since you cannot run multiple clips on a track, it does the same as
                        // "track".
                        this.model.getCurrentTrackBank ().getItem (toIntValue (0, objects)).stop ();
                        break;

                    default:
                        this.host.error ("Unknown Stop sub-command: " + cmd);
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
                    this.model.getCursorClip (8, 8).quantize (1);
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


    private void handleShiftedRecordButton ()
    {
        this.handleRecord (((KontrolOSCConfiguration) this.configuration).getShiftedRecordButtonFunction ());
    }


    private void handleRecordButton ()
    {
        this.handleRecord (((KontrolOSCConfiguration) this.configuration).getRecordButtonFunction ());
    }


    private void handleRecord (final int recordMode)
    {
        switch (recordMode)
        {
            case KontrolOSCConfiguration.RECORD_ARRANGER:
                this.transport.record ();
                break;
            case KontrolOSCConfiguration.RECORD_CLIP:
                final ISlot selectedSlot = this.model.getSelectedSlot ();
                if (selectedSlot != null)
                    selectedSlot.record ();
                break;
            case KontrolOSCConfiguration.NEW_CLIP:
                new NewCommand<> (this.model, this.surface).executeNormal (ButtonEvent.DOWN);
                break;
            case KontrolOSCConfiguration.TOGGLE_ARRANGER_OVERDUB:
                this.transport.toggleOverdub ();
                break;
            case KontrolOSCConfiguration.TOGGLE_CLIP_OVERDUB:
                this.transport.toggleLauncherOverdub ();
                break;
            default:
                // Intentionally empty
                break;
        }
    }


    private void parseTrackCommands (final String command, final Object [] values)
    {
        switch (command)
        {
            case "info": // 1.x
                final int [] vs = toIntValues (values);
                if (vs.length != 2)
                    return;
                ((KontrolOSCWriter) this.writer).sendTrackInfo (vs[0], vs[1]);
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
                        final int trackIndex = tb.getPageSize () + trackPosition;
                        tb.getItem (trackIndex).select ();
                    }
                    else
                    {
                        // Send channels are positive, highest channel is master track
                        final ITrackBank tbe = this.model.getEffectTrackBank ();
                        if (tbe != null && trackPosition < tbe.getItemCount ())
                            tbe.getItem (trackPosition).select ();
                        else
                            this.model.getMasterTrack ().select ();
                    }
                }
                else
                {
                    // 1.5
                    final int trackIndex = toIntValue (values);
                    tb.getItem (trackIndex).select ();
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


    private void parseSceneCommands (final String command, final Object [] values)
    {
        switch (command)
        {
            case "view": // 1.6
                final ITrack selectedTrack = this.model.getSelectedTrack ();
                if (selectedTrack == null)
                    return;
                final int sceneIndex = toIntValue (values);
                selectedTrack.getSlotBank ().getItem (sceneIndex).select ();
                this.sendOSC ("scene", new int []
                {
                    sceneIndex
                });
                break;

            default:
                this.host.println ("Unknown Scene Command: " + command);
                break;
        }
    }


    private ITrack getTrack (final Object [] values)
    {
        return ((KontrolOSCWriter) this.writer).getTrack (toIntValue (0, values), toIntValue (1, values));
    }


    private void sendOSC (final String command, final int [] numbers)
    {
        this.writer.fastSendOSC (new StringBuilder ("/").append (this.daw).append ('/').append (command).toString (), numbers);
    }


    private static LinkedList<String> parseAddress (final String address)
    {
        final LinkedList<String> oscParts = new LinkedList<> ();
        Collections.addAll (oscParts, address.split ("/"));

        // Remove first empty element
        oscParts.removeFirst ();
        return oscParts;
    }


    private static int toIntValue (final Object [] objects)
    {
        return toNumValue (0, objects).intValue ();
    }


    private static int toIntValue (final int index, final Object [] values)
    {
        return toNumValue (index, values).intValue ();
    }


    private static Number toNumValue (final int index, final Object [] objects)
    {
        return objects != null && index < objects.length ? toNumber (objects[index]) : Integer.valueOf (-1);
    }


    private static int [] toIntValues (final Object [] values)
    {
        final int [] result = new int [values.length];
        for (int i = 0; i < values.length; i++)
            result[i] = toNumber (values[i]).intValue ();
        return result;
    }


    private static Number toNumber (final Object value)
    {
        return value == null || !(value instanceof Number) ? Integer.valueOf (-1) : (Number) value;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isHeartbeatMessage (final String address)
    {
        return address.contains ("ping");
    }
}