// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc.protocol;

import de.mossgrabers.framework.bitwig.daw.HostImpl;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.osc.OSCConfiguration;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscMessage;
import com.bitwig.extension.api.opensoundcontrol.OscMethodCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parser for OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCParser implements OscMethodCallback
{
    private static final String    PART_INDICATE     = "indicate";
    private static final String    PART_VOLUME       = "volume";
    private static final String    PART_RESET        = "reset";

    private static final Pattern   RGB_COLOR_PATTERN = Pattern.compile ("(rgb|RGB)\\((\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?)\\)");

    private final OSCModel         model;
    private final ITransport       transport;
    private final IMasterTrack     masterTrack;
    private final Scales           scales;
    private final MidiIn           port;
    private final NoteInput        noteInput;
    private final ControllerHost   host;
    private final OSCConfiguration configuration;
    private final Display          display;
    private final OSCWriter        writer;


    /**
     * Constructor.
     *
     * @param host The host
     * @param writer The OSC writer
     * @param configuration The configuration
     * @param model The model
     */
    public OSCParser (final ControllerHost host, final OSCWriter writer, final OSCConfiguration configuration, final OSCModel model)
    {
        this.host = host;
        this.writer = writer;
        this.configuration = configuration;
        this.model = model;

        this.display = new DummyDisplay (new HostImpl (host));

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();
        this.scales = this.model.getScales ();

        this.model.getCurrentTrackBank ().setIndication (true);
        this.model.updateNoteMapping ();

        this.port = host.getMidiInPort (0);
        this.noteInput = this.port.createNoteInput ("OSC Midi");
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final OscConnection source, final OscMessage message)
    {
        final LinkedList<String> oscParts = parseAddress (message);
        if (oscParts.isEmpty ())
            return;

        final List<Object> values = message.getArguments ();
        final Object value = values.isEmpty () ? null : values.get (0);
        final int numValue = value == null || !(value instanceof Number) ? -1 : ((Number) value).intValue ();

        final String command = oscParts.removeFirst ();

        if (this.parseTransportCommands (command, oscParts, value, numValue))
            return;

        if (this.parseGlobalCommands (command))
            return;

        if (this.parsePanelCommands (command, oscParts, value == null ? null : value.toString ()))
            return;

        switch (command)
        {
            //
            // Project
            //

            case "project":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Project subcommand.");
                    return;
                }
                final String subCommand = oscParts.get (0);
                switch (subCommand)
                {
                    case "+":
                        this.model.getProject ().next ();
                        break;
                    case "-":
                        this.model.getProject ().previous ();
                        break;
                    case "engine":
                        if (numValue >= 0)
                            this.model.getApplication ().setEngineActive (numValue > 0);
                        else
                            this.model.getApplication ().toggleEngineActive ();
                        break;
                    default:
                        this.host.errorln ("Unknown Project subcommand: " + subCommand);
                        break;
                }
                break;

            //
            // Scenes
            //

            case "scene":
                final String p = oscParts.removeFirst ();
                switch (p)
                {
                    case "bank":
                        if (oscParts.isEmpty ())
                        {
                            this.host.errorln ("Missing Scene subcommand.");
                            return;
                        }
                        final String subCommand2 = oscParts.get (0);
                        switch (subCommand2)
                        {
                            case "+":
                                if (value == null || numValue > 0)
                                    this.model.getCurrentTrackBank ().scrollScenesPageDown ();
                                break;
                            case "-":
                                if (value == null || numValue > 0)
                                    this.model.getCurrentTrackBank ().scrollScenesPageUp ();
                                break;
                            default:
                                this.host.errorln ("Unknown Scene subcommand: " + subCommand2);
                                break;
                        }
                        break;
                    case "+":
                        if (value == null || numValue > 0)
                            this.model.getCurrentTrackBank ().scrollScenesDown ();
                        break;
                    case "-":
                        if (value == null || numValue > 0)
                            this.model.getCurrentTrackBank ().scrollScenesUp ();
                        break;
                    case "create":
                        this.model.getProject ().createSceneFromPlayingLauncherClips ();
                        break;
                    default:
                        if (oscParts.isEmpty ())
                        {
                            this.host.errorln ("Missing Scene index.");
                            return;
                        }
                        final int scene = Integer.parseInt (p);
                        final String sceneCommand = oscParts.removeFirst ();
                        switch (sceneCommand)
                        {
                            case "launch":
                                this.model.getCurrentTrackBank ().launchScene (scene - 1);
                                break;
                            default:
                                this.host.errorln ("Unknown Scene subcommand: " + sceneCommand);
                                break;
                        }
                        break;
                }
                break;

            //
            // Master-/Track(-commands)
            //

            case "track":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Track index or command.");
                    return;
                }
                try
                {
                    final int trackNo = Integer.parseInt (oscParts.get (0));
                    oscParts.removeFirst ();
                    this.parseTrackValue (this.model.getCurrentTrackBank ().getTrack (trackNo - 1), oscParts, value);
                }
                catch (final NumberFormatException ex)
                {
                    this.parseTrackCommands (oscParts, value, numValue);
                }
                break;

            case "master":
                this.parseTrackValue (this.masterTrack, oscParts, value);
                break;

            //
            // Device
            //

            case "device":
            {
                final ICursorDevice cd = this.model.getCursorDevice ();
                this.parseDeviceValue (cd, oscParts, value);
                break;
            }

            case "primary":
                this.parseDeviceValue (this.model.getPrimaryDevice (), oscParts, value);
                break;

            //
            // Browser
            //

            case "browser":
                this.parseBrowser (oscParts);
                break;

            //
            // Keyboard
            //

            case "vkb_midi":
                this.parseMidi (oscParts, value);
                break;

            //
            // Actions
            //

            case "action":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Action command ID.");
                    return;
                }
                final String cmd = oscParts.get (0).replace ('-', ' ');
                try
                {
                    this.model.getApplication ().invokeAction (cmd);
                }
                catch (final RuntimeException ex)
                {
                    this.host.errorln ("Could not execute action: " + cmd);
                }
                break;

            default:
                this.host.println ("Unknown OSC Command: " + message.getAddressPattern () + " " + value);
                break;
        }
    }


    private boolean parseTransportCommands (final String command, final LinkedList<String> oscParts, final Object value, final int numValue)
    {
        switch (command)
        {
            case "play":
                if (value == null || numValue > 0 && !this.transport.isPlaying ())
                    this.transport.play ();
                return true;

            case "stop":
                if (value == null || numValue > 0 && this.transport.isPlaying ())
                    this.transport.play ();
                return true;

            case "restart":
                if (value == null || numValue > 0)
                    this.transport.restart ();
                return true;

            case "record":
                if (value == null || numValue > 0)
                    this.transport.record ();
                return true;

            case "overdub":
                if (value != null && numValue == 0)
                    return true;
                if (!oscParts.isEmpty () && "launcher".equals (oscParts.get (0)))
                    this.transport.toggleLauncherOverdub ();
                else
                    this.transport.toggleOverdub ();
                return true;

            case "repeat":
                if (value == null)
                    this.transport.toggleLoop ();
                else
                    this.transport.setLoop (numValue > 0);
                return true;

            case "punchIn":
                if (value == null)
                    this.transport.togglePunchIn ();
                else
                    this.transport.setPunchIn (numValue > 0);
                return true;

            case "punchOut":
                if (value == null)
                    this.transport.togglePunchOut ();
                else
                    this.transport.setPunchOut (numValue > 0);
                return true;

            case "click":
                if (oscParts.isEmpty ())
                {
                    if (value == null)
                        this.transport.toggleMetronome ();
                    else
                        this.transport.setMetronome (numValue > 0);
                    return true;
                }
                final String cmd = oscParts.get (0);
                if (PART_VOLUME.equals (cmd))
                    this.transport.setMetronomeVolume (numValue);
                else if ("ticks".equals (cmd))
                {
                    if (value == null || numValue > 0)
                        this.transport.toggleMetronomeTicks ();
                }
                return true;

            case "quantize":
                this.model.getClip ().quantize (1);
                return true;

            case "tempo":
                final String tempoCommand = oscParts.get (0);
                switch (tempoCommand)
                {
                    case "raw":
                        if (value instanceof Number)
                            this.transport.setTempo (((Number) value).doubleValue ());
                        return true;
                    case "tap":
                        if (value == null || numValue > 0)
                            this.transport.tapTempo ();
                        return true;
                    case "+":
                        if (value == null || numValue > 0)
                        {
                            final double v = value == null ? 1.0 : ((Number) value).doubleValue ();
                            if (value instanceof Number)
                                this.transport.setTempo (this.transport.getTempo () + v);
                        }
                        return true;
                    case "-":
                        if (value == null || numValue > 0)
                        {
                            final double v2 = value == null ? 1.0 : ((Number) value).doubleValue ();
                            if (value instanceof Number)
                                this.transport.setTempo (this.transport.getTempo () - v2);
                        }
                        return true;
                    default:
                        this.host.errorln ("Unknown Tempo subcommand: " + tempoCommand);
                }
                return true;

            case "time":
                if (value instanceof Number)
                    this.transport.setPosition (((Number) value).doubleValue ());
                return true;

            case "position":
                if (oscParts.isEmpty ())
                {
                    if (value != null)
                        this.transport.changePosition (numValue >= 0, Math.abs (numValue) <= 1);
                    return true;
                }
                final String positionCommand = oscParts.get (0);
                switch (positionCommand)
                {
                    case "+":
                        this.transport.changePosition (true, true);
                        return true;
                    case "-":
                        this.transport.changePosition (false, true);
                        return true;
                    case "++":
                        this.transport.changePosition (true, false);
                        return true;
                    case "--":
                        this.transport.changePosition (false, false);
                        return true;
                    case "start":
                        this.transport.setPosition (0);
                        return true;
                    default:
                        this.host.errorln ("Unknown Position subcommand: " + positionCommand);
                }
                return true;

            case "crossfade":
                if (numValue >= 0)
                    this.transport.setCrossfade (numValue);
                return true;

            case "autowrite":
                if (value != null && numValue == 0)
                    return true;
                if (!oscParts.isEmpty () && "launcher".equals (oscParts.get (0)))
                    this.transport.toggleWriteClipLauncherAutomation ();
                else
                    this.transport.toggleWriteArrangerAutomation ();
                return true;

            case "automationWriteMode":
                if (value != null)
                    this.transport.setAutomationWriteMode (value.toString ());
                return true;

            case "preroll":
                switch (numValue)
                {
                    case 0:
                        this.transport.setPreroll (ITransport.PREROLL_NONE);
                        return true;
                    case 1:
                        this.transport.setPreroll (ITransport.PREROLL_1_BAR);
                        return true;
                    case 2:
                        this.transport.setPreroll (ITransport.PREROLL_2_BARS);
                        return true;
                    case 4:
                        this.transport.setPreroll (ITransport.PREROLL_4_BARS);
                        return true;
                    default:
                        this.host.errorln ("Unknown Preroll length: " + numValue);
                }
                return true;

            default:
                return false;
        }
    }


    private boolean parseGlobalCommands (final String command)
    {
        switch (command)
        {
            case "refresh":
                this.writer.flush (true);
                return true;

            case "undo":
                this.model.getApplication ().undo ();
                return true;

            case "redo":
                this.model.getApplication ().redo ();
                return true;

            default:
                return false;
        }
    }


    private boolean parsePanelCommands (final String command, final LinkedList<String> oscParts, final String value)
    {
        switch (command)
        {
            case "layout":
                if (value != null)
                    this.model.getApplication ().setPanelLayout (value.toUpperCase ());
                return true;

            case "panel":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Panel subcommand.");
                    return true;
                }
                final IApplication app = this.model.getApplication ();
                final String subCommand = oscParts.get (0);
                switch (subCommand)
                {
                    case "noteEditor":
                        app.toggleNoteEditor ();
                        break;
                    case "automationEditor":
                        app.toggleAutomationEditor ();
                        break;
                    case "devices":
                        app.toggleDevices ();
                        break;
                    case "mixer":
                        app.toggleMixer ();
                        break;
                    case "fullscreen":
                        app.toggleFullScreen ();
                        break;
                    default:
                        this.host.errorln ("Unknown Panel subcommand: " + subCommand);
                }
                return true;

            case "arranger":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Arranger subcommand.");
                    return true;
                }
                final IArranger arrange = this.model.getArranger ();
                final String subCommand2 = oscParts.get (0);
                switch (subCommand2)
                {
                    case "cueMarkerVisibility":
                        arrange.toggleCueMarkerVisibility ();
                        break;
                    case "playbackFollow":
                        arrange.togglePlaybackFollow ();
                        break;
                    case "trackRowHeight":
                        arrange.toggleTrackRowHeight ();
                        break;
                    case "clipLauncherSectionVisibility":
                        arrange.toggleClipLauncher ();
                        break;
                    case "timeLineVisibility":
                        arrange.toggleTimeLine ();
                        break;
                    case "ioSectionVisibility":
                        arrange.toggleIoSection ();
                        break;
                    case "effectTracksVisibility":
                        arrange.toggleEffectTracks ();
                        break;
                    default:
                        this.host.errorln ("Unknown Arranger subcommand: " + subCommand2);
                }
                return true;

            case "mixer":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Mixer subcommand.");
                    return true;
                }
                final IMixer mix = this.model.getMixer ();
                final String subCommand3 = oscParts.get (0);
                switch (subCommand3)
                {
                    case "clipLauncherSectionVisibility":
                        mix.toggleClipLauncherSectionVisibility ();
                        break;
                    case "crossFadeSectionVisibility":
                        mix.toggleCrossFadeSectionVisibility ();
                        break;
                    case "deviceSectionVisibility":
                        mix.toggleDeviceSectionVisibility ();
                        break;
                    case "sendsSectionVisibility":
                        mix.toggleSendsSectionVisibility ();
                        break;
                    case "ioSectionVisibility":
                        mix.toggleIoSectionVisibility ();
                        break;
                    case "meterSectionVisibility":
                        mix.toggleMeterSectionVisibility ();
                        break;
                    default:
                        this.host.errorln ("Unknown Mixer subcommand: " + subCommand3);
                        break;
                }
                return true;

            default:
                return false;
        }
    }


    private void parseTrackCommands (final LinkedList<String> oscParts, final Object value, final int numValue)
    {
        if (oscParts.isEmpty ())
        {
            this.host.errorln ("Missing Track command.");
            return;
        }

        final IChannelBank tb = this.model.getCurrentTrackBank ();

        final String command = oscParts.removeFirst ();
        switch (command)
        {
            case PART_INDICATE:
            {
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Indicate subcommand.");
                    return;
                }
                final boolean isTrue = numValue > 0;
                final String subCommand = oscParts.removeFirst ();
                switch (subCommand)
                {
                    case PART_VOLUME:
                        for (int i = 0; i < tb.getNumTracks (); i++)
                            tb.getTrack (i).setVolumeIndication (isTrue);
                        break;
                    case "pan":
                        for (int i = 0; i < tb.getNumTracks (); i++)
                            tb.getTrack (i).setPanIndication (isTrue);
                        break;
                    case "send":
                        if (tb instanceof ITrackBank)
                        {
                            final int sendIndex = Integer.parseInt (oscParts.get (0));
                            for (int i = 0; i < tb.getNumTracks (); i++)
                                tb.getTrack (i).getSend (sendIndex - 1).setIndication (isTrue);
                        }
                        break;
                    default:
                        this.host.errorln ("Unknown Indicate subcommand: " + subCommand);
                        break;
                }
                break;
            }

            case "bank":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Track Bank subcommand.");
                    return;
                }
                final String subCommand = oscParts.removeFirst ();
                switch (subCommand)
                {
                    case "page":
                        if (oscParts.isEmpty ())
                        {
                            this.host.errorln ("Missing Track Bank Page subcommand.");
                            return;
                        }
                        if ("+".equals (oscParts.removeFirst ()))
                        {
                            if (!tb.canScrollTracksDown ())
                                return;
                            tb.scrollTracksPageDown ();
                            this.host.scheduleTask ( () -> tb.getTrack (0).selectAndMakeVisible (), 75);
                        }
                        else // "-"
                        {
                            if (!tb.canScrollTracksUp ())
                                return;
                            tb.scrollTracksPageUp ();
                            this.host.scheduleTask ( () -> tb.getTrack (7).selectAndMakeVisible (), 75);
                        }
                        break;
                    case "+":
                        tb.scrollTracksDown ();
                        break;
                    case "-":
                        tb.scrollTracksUp ();
                        break;
                    default:
                        this.host.errorln ("Unknown Track Bank subcommand: " + subCommand);
                        break;
                }
                break;

            case "+":
            {
                final ITrack sel = tb.getSelectedTrack ();
                final int index = sel == null ? 0 : sel.getIndex () + 1;
                if (index == tb.getNumTracks ())
                {
                    if (!tb.canScrollTracksDown ())
                        return;
                    tb.scrollTracksPageDown ();
                    this.host.scheduleTask ( () -> tb.getTrack (0).selectAndMakeVisible (), 75);
                }
                tb.getTrack (index).selectAndMakeVisible ();
                break;
            }

            case "-":
            {
                final ITrack sel = tb.getSelectedTrack ();
                final int index = sel == null ? 0 : sel.getIndex () - 1;
                if (index == -1)
                {
                    if (!tb.canScrollTracksUp ())
                        return;
                    tb.scrollTracksPageUp ();
                    this.host.scheduleTask ( () -> tb.getTrack (7).selectAndMakeVisible (), 75);
                    return;
                }
                tb.getTrack (index).selectAndMakeVisible ();
                break;
            }

            case "add":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Add subcommand.");
                    return;
                }
                final String subCommand2 = oscParts.removeFirst ();
                final IApplication application = this.model.getApplication ();
                switch (subCommand2)
                {
                    case "audio":
                        application.addAudioTrack ();
                        break;
                    case "effect":
                        application.addEffectTrack ();
                        break;
                    case "instrument":
                        application.addInstrumentTrack ();
                        break;
                    default:
                        this.host.errorln ("Unknown Add subcommand: " + subCommand2);
                        break;
                }
                break;

            case "stop":
                this.model.getCurrentTrackBank ().stop ();
                break;

            case "vu":
                this.configuration.setVUMetersEnabled (numValue > 0);
                break;

            case "toggleBank":
            {
                this.model.toggleCurrentTrackBank ();
                final IChannelBank tbNew = this.model.getCurrentTrackBank ();
                // Make sure a track is selected
                final IChannelBank tbOther = this.model.isEffectTrackBankActive () ? this.model.getTrackBank () : this.model.getEffectTrackBank ();
                final ITrack selectedTrack = tbNew.getSelectedTrack ();
                if (selectedTrack == null)
                    tbNew.getTrack (0).selectAndMakeVisible ();
                // Move the indication to the other bank
                for (int i = 0; i < tbNew.getNumTracks (); i++)
                {
                    final ITrack otherTrack = tbOther.getTrack (i);
                    otherTrack.setVolumeIndication (false);
                    otherTrack.setPanIndication (false);
                    final ITrack track = tbNew.getTrack (i);
                    track.setVolumeIndication (true);
                    track.setPanIndication (true);
                }
                break;
            }

            case "parent":
            {
                if (tb instanceof ITrackBank)
                    ((ITrackBank) tb).selectParent ();
                break;
            }

            case "selected":
                final ITrack selectedTrack = tb.getSelectedTrack ();
                if (selectedTrack != null)
                    this.parseTrackValue (selectedTrack, oscParts, value);
                break;

            default:
                this.host.println ("Unknown Track Command: " + command);
                break;
        }
    }


    private void parseTrackValue (final ITrack track, final LinkedList<String> parts, final Object value)
    {
        final double numValue = value instanceof Number ? ((Number) value).doubleValue () : -1;
        final int intValue = value instanceof Number ? ((Number) value).intValue () : -1;

        if (parts.isEmpty ())
        {
            this.host.errorln ("Missing Track command.");
            return;
        }

        final String command = parts.removeFirst ();
        switch (command)
        {
            case "activated":
                track.setIsActivated (intValue > 0);
                break;

            case "crossfadeMode":
                if (numValue == 1)
                    track.setCrossfadeMode (parts.removeFirst ());
                break;

            case "selected":
                if (intValue > 0)
                    track.selectAndMakeVisible ();
                break;

            case PART_VOLUME:
                if (parts.isEmpty ())
                    track.setVolume (numValue);
                else if (PART_INDICATE.equals (parts.get (0)))
                    track.setVolumeIndication (numValue > 0);
                else if (PART_RESET.equals (parts.get (0)))
                    track.resetVolume ();
                break;

            case "pan":
                if (parts.isEmpty ())
                    track.setPan (numValue);
                else if (PART_INDICATE.equals (parts.get (0)))
                    track.setPanIndication (numValue > 0);
                else if (PART_RESET.equals (parts.get (0)))
                    track.resetPan ();
                break;

            case "mute":
                if (numValue < 0)
                    track.toggleMute ();
                else
                    track.setMute (numValue > 0);
                break;

            case "solo":
                if (numValue < 0)
                    track.toggleSolo ();
                else
                    track.setSolo (numValue > 0);
                break;

            case "recarm":
                if (numValue < 0)
                    track.toggleRecArm ();
                else
                    track.setRecArm (numValue > 0);
                break;

            case "monitor":
                if (numValue < 0)
                    track.toggleMonitor ();
                else
                    track.setMonitor (numValue > 0);
                break;

            case "autoMonitor":
                if (numValue < 0)
                    track.toggleAutoMonitor ();
                else
                    track.setAutoMonitor (numValue > 0);
                break;

            case "send":
                final int sendNo = Integer.parseInt (parts.removeFirst ());
                this.parseSendValue (track, sendNo - 1, parts, value);
                break;

            case "clip":
                if (parts.isEmpty ())
                {
                    this.host.errorln ("Missing Clip subcommand.");
                    return;
                }
                final String cmd = parts.removeFirst ();
                try
                {
                    final int clipNo = Integer.parseInt (cmd);
                    if (parts.isEmpty ())
                    {
                        this.host.errorln ("Missing Clip subcommand.");
                        return;
                    }
                    final String clipCommand = parts.removeFirst ();
                    final ISlot slot = track.getSlot (clipNo - 1);
                    switch (clipCommand)
                    {
                        case "select":
                            slot.select ();
                            break;
                        case "launch":
                            slot.launch ();
                            break;
                        case "record":
                            slot.record ();
                            break;
                        case "color":
                            final Matcher matcher = RGB_COLOR_PATTERN.matcher (value.toString ());
                            if (!matcher.matches ())
                                return;
                            final int count = matcher.groupCount ();
                            if (count != 7)
                                return;
                            slot.setColor (Double.parseDouble (matcher.group (2)) / 255.0, Double.parseDouble (matcher.group (4)) / 255.0, Double.parseDouble (matcher.group (6)) / 255.0);
                            break;
                        default:
                            this.host.println ("Unknown Clip subcommand: " + clipCommand);
                            break;
                    }
                }
                catch (final NumberFormatException ex)
                {
                    switch (cmd)
                    {
                        case "stop":
                            track.stop ();
                            break;
                        case "returntoarrangement":
                            track.returnToArrangement ();
                            break;
                        default:
                            this.host.println ("Unknown Clip command: " + cmd);
                            break;
                    }
                }
                break;

            case "enter":
                final IChannelBank tb = this.model.getCurrentTrackBank ();
                if (tb instanceof ITrackBank)
                {
                    track.select ();
                    ((ITrackBank) tb).selectChildren ();
                }
                break;

            case "color":
                final Matcher matcher = RGB_COLOR_PATTERN.matcher (value.toString ());
                if (!matcher.matches ())
                    return;
                final int count = matcher.groupCount ();
                if (count == 7)
                    track.setColor (Double.parseDouble (matcher.group (2)) / 255.0, Double.parseDouble (matcher.group (4)) / 255.0, Double.parseDouble (matcher.group (6)) / 255.0);
                break;

            default:
                this.host.println ("Unknown Track Parameter: " + command);
                break;
        }
    }


    private void parseSendValue (final ITrack track, final int sendIndex, final LinkedList<String> parts, final Object value)
    {
        if (parts.isEmpty ())
        {
            this.host.errorln ("Missing Send subcommand.");
            return;
        }

        final double numValue = value instanceof Number ? ((Number) value).doubleValue () : -1;

        final String command = parts.removeFirst ();
        switch (command)
        {
            case PART_VOLUME:
                final ISend send = track.getSend (sendIndex);
                if (send != null)
                {
                    if (parts.isEmpty ())
                        send.setValue (numValue);
                    else if (PART_INDICATE.equals (parts.get (0)))
                        send.setIndication (numValue > 0);
                }
                break;

            default:
                this.host.println ("Unknown Send Parameter value: " + command);
                break;
        }
    }


    private void parseDeviceValue (final ICursorDevice cursorDevice, final LinkedList<String> oscParts, final Object value)
    {
        if (oscParts.isEmpty ())
        {
            this.host.errorln ("Missing Device subcommand.");
            return;
        }

        final int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        final String command = oscParts.removeFirst ();
        switch (command)
        {
            case "page":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Device Page subcommand.");
                    return;
                }
                final int bankNo = Integer.parseInt (oscParts.removeFirst ());
                final String subCommand = oscParts.removeFirst ();
                switch (subCommand)
                {
                    case "selected":
                        if (numValue > 0)
                        {
                            final String [] parameterPageNames = cursorDevice.getParameterPageNames ();
                            final int selectedParameterPage = cursorDevice.getSelectedParameterPage ();
                            final int page = Math.min (Math.max (0, selectedParameterPage), parameterPageNames.length - 1);
                            final int start = page / 8 * 8;
                            final int bankPage = start + bankNo - 1;
                            if (bankPage < parameterPageNames.length)
                                cursorDevice.setSelectedParameterPage (bankPage);
                        }
                        break;

                    default:
                        this.host.println ("Unknown Device Device Page Parameter: " + subCommand);
                        break;
                }
                break;

            case "sibling":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Device Sibling subcommand.");
                    return;
                }
                final int siblingNo = Integer.parseInt (oscParts.removeFirst ());
                final String subCommand2 = oscParts.removeFirst ();
                switch (subCommand2)
                {
                    case "selected":
                        if (numValue > 0)
                            cursorDevice.selectSibling (siblingNo - 1);
                        break;

                    default:
                        this.host.println ("Unknown Device Device Sibling Parameter: " + subCommand2);
                        break;
                }
                break;

            case "bank":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Device Bank subcommand.");
                    return;
                }
                final String subCommand3 = oscParts.removeFirst ();
                switch (subCommand3)
                {
                    case "page":
                        if (oscParts.isEmpty ())
                        {
                            this.host.errorln ("Missing Device Bank Page subcommand.");
                            return;
                        }
                        if ("+".equals (oscParts.removeFirst ()))
                        {
                            cursorDevice.selectNextBank ();
                            this.host.scheduleTask ( () -> cursorDevice.selectSibling (0), 75);
                        }
                        else // "-"
                        {
                            cursorDevice.selectPreviousBank ();
                            this.host.scheduleTask ( () -> cursorDevice.selectSibling (7), 75);
                        }
                        break;
                    default:
                        this.host.errorln ("Unknown Device Bank subcommand: " + subCommand3);
                        break;
                }
                break;

            case "expand":
                cursorDevice.toggleExpanded ();
                break;

            case "bypass":
                cursorDevice.toggleEnabledState ();
                break;

            case "window":
                cursorDevice.toggleWindowOpen ();
                break;

            case PART_INDICATE:
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Device Indicate subcommand.");
                    return;
                }
                switch (oscParts.removeFirst ())
                {
                    case "param":
                        for (int i = 0; i < cursorDevice.getNumParameters (); i++)
                            cursorDevice.indicateParameter (i, numValue > 0);
                        break;

                    default:
                        this.host.println ("Unknown Device Indicate Parameter: " + command);
                        break;
                }
                break;

            case "param":
                if (oscParts.isEmpty ())
                {
                    this.host.errorln ("Missing Device Param subcommand.");
                    return;
                }
                final String part = oscParts.removeFirst ();
                try
                {
                    final int paramNo = Integer.parseInt (part);
                    this.parseFXParamValue (cursorDevice, paramNo - 1, oscParts, value);
                }
                catch (final NumberFormatException ex)
                {
                    if (value == null || numValue > 0)
                    {
                        switch (part)
                        {
                            case "+":
                                cursorDevice.nextParameterPage ();
                                break;
                            case "-":
                                cursorDevice.previousParameterPage ();
                                break;

                            case "bank":
                                if (oscParts.isEmpty ())
                                {
                                    this.host.errorln ("Missing Device Param Bank subcommand.");
                                    return;
                                }
                                final String subCommand4 = oscParts.removeFirst ();
                                switch (subCommand4)
                                {
                                    case "page":
                                        if (oscParts.isEmpty ())
                                        {
                                            this.host.errorln ("Missing Device Param Bank Page subcommand.");
                                            return;
                                        }
                                        if ("+".equals (oscParts.removeFirst ()))
                                            cursorDevice.nextParameterPageBank ();
                                        else // "-"
                                            cursorDevice.previousParameterPageBank ();
                                        break;
                                    default:
                                        this.host.errorln ("Unknown Device Param Bank subcommand: " + subCommand4);
                                        break;
                                }
                                break;

                            default:
                                this.host.println ("Unknown Device Param Parameter: " + command);
                                break;
                        }
                    }
                }
                break;

            case "+":
                if (value == null || numValue > 0)
                    cursorDevice.selectNext ();
                break;

            case "-":
                if (value == null || numValue > 0)
                    cursorDevice.selectPrevious ();
                break;

            case "drumpad":
                if (cursorDevice.hasDrumPads ())
                    this.parseLayerOrDrumpad (cursorDevice, oscParts, value);
                break;

            case "layer":
                this.parseLayerOrDrumpad (cursorDevice, oscParts, value);
                break;

            default:
                this.host.println ("Unknown Device command: " + command);
                break;
        }
    }


    private void parseLayerOrDrumpad (final ICursorDevice cursorDevice, final LinkedList<String> parts, final Object value)
    {
        if (parts.isEmpty ())
        {
            this.host.println ("Missing Layer/Drumpad command.");
            return;
        }
        try
        {
            final int layerNo = Integer.parseInt (parts.get (0));
            parts.removeFirst ();
            this.parseDeviceLayerValue (cursorDevice, layerNo - 1, parts, value);
        }
        catch (final NumberFormatException ex)
        {
            final String command = parts.removeFirst ();
            switch (command)
            {
                case "parent":
                    if (cursorDevice.isNested ())
                    {
                        cursorDevice.selectParent ();
                        cursorDevice.selectChannel ();
                    }
                    break;

                case "+":
                    cursorDevice.nextLayerOrDrumPad ();
                    break;

                case "-":
                    cursorDevice.previousLayerOrDrumPad ();
                    break;

                case "page":
                    if (parts.isEmpty ())
                    {
                        this.host.println ("Missing Layer/Drumpad Page subcommand: " + command);
                        return;
                    }
                    if ("+".equals (parts.get (0)))
                        cursorDevice.nextLayerOrDrumPadBank ();
                    else
                        cursorDevice.previousLayerOrDrumPadBank ();
                    break;

                default:
                    this.host.println ("Unknown Layour/Drum command: " + command);
                    break;
            }
        }
    }


    private void parseBrowser (final LinkedList<String> parts)
    {
        if (parts.isEmpty ())
        {
            this.host.println ("Missing Browser command.");
            return;
        }

        final IBrowser browser = this.model.getBrowser ();

        final String command = parts.removeFirst ();
        switch (command)
        {
            case "preset":
                browser.browseForPresets ();
                break;

            case "tab":
                if (parts.isEmpty ())
                {
                    this.host.println ("Missing Browser Tab subcommand.");
                    return;
                }
                if (!browser.isActive ())
                    return;
                final String subCmd = parts.removeFirst ();
                if ("+".equals (subCmd))
                    browser.nextContentType ();
                else if ("-".equals (subCmd))
                    browser.previousContentType ();
                break;

            case "device":
                final String insertLocation = parts.isEmpty () ? null : parts.removeFirst ();
                if (insertLocation == null || "after".equals (insertLocation))
                    browser.browseToInsertAfterDevice ();
                else
                    browser.browseToInsertBeforeDevice ();
                break;

            case "commit":
                browser.stopBrowsing (true);
                break;

            case "cancel":
                browser.stopBrowsing (false);
                break;

            case "filter":
                if (parts.isEmpty ())
                {
                    this.host.println ("Missing Browser Filter row.");
                    return;
                }
                int column = Integer.parseInt (parts.removeFirst ());
                if (column < 1 || column > 6)
                    return;
                column = column - 1;
                if (!browser.isActive ())
                    return;
                if (parts.isEmpty ())
                {
                    this.host.println ("Missing Browser Filter command.");
                    return;
                }
                final String cmd = parts.removeFirst ();
                if ("+".equals (cmd))
                    browser.selectNextFilterItem (column);
                else if ("-".equals (cmd))
                    browser.selectPreviousFilterItem (column);
                else if (PART_RESET.equals (cmd))
                    browser.getFilterColumn (column).resetFilter ();
                break;

            case "result":
                if (!browser.isActive ())
                    return;
                final String direction = parts.isEmpty () ? "+" : parts.removeFirst ();
                if ("+".equals (direction))
                    browser.selectNextResult ();
                else
                    browser.selectPreviousResult ();
                break;

            default:
                this.host.println ("Unknown Browser Command: " + command);
                break;
        }
    }


    private void parseDeviceLayerValue (final ICursorDevice cursorDevice, final int layer, final LinkedList<String> parts, final Object value)
    {
        if (parts.isEmpty ())
        {
            this.host.println ("Missing Device Layer command.");
            return;
        }
        final String command = parts.removeFirst ();
        final int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        switch (command)
        {
            case "select":
                cursorDevice.selectLayer (layer);
                break;

            case PART_VOLUME:
                cursorDevice.setLayerOrDrumPadVolume (layer, numValue);
                break;

            case "pan":
                cursorDevice.setLayerOrDrumPadPan (layer, numValue);
                break;

            case "mute":
                if (numValue < 0)
                    cursorDevice.toggleLayerOrDrumPadMute (layer);
                else
                    cursorDevice.setLayerOrDrumPadMute (layer, numValue > 0);
                break;

            case "solo":
                if (numValue < 0)
                    cursorDevice.toggleLayerOrDrumPadSolo (layer);
                else
                    cursorDevice.setLayerOrDrumPadSolo (layer, numValue > 0);
                break;

            case "send":
                final int sendNo = Integer.parseInt (parts.removeFirst ());
                cursorDevice.setLayerOrDrumPadSend (layer, sendNo - 1, numValue);
                break;

            case "enter":
                cursorDevice.enterLayerOrDrumPad (layer);
                cursorDevice.selectFirstDeviceInLayerOrDrumPad (layer);
                break;

            default:
                this.host.println ("Unknown Device Layer command: " + command);
                break;
        }
    }


    private void parseFXParamValue (final ICursorDevice cursorDevice, final int fxparamIndex, final LinkedList<String> parts, final Object value)
    {
        if (parts.isEmpty ())
        {
            this.host.println ("Missing FX Parameter command.");
            return;
        }
        final int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        final String command = parts.get (0);
        switch (command)
        {
            case "value":
                if (parts.size () == 1 && value != null)
                    cursorDevice.setParameter (fxparamIndex, numValue);
                break;

            case PART_INDICATE:
                if (parts.size () == 1 && value != null)
                    cursorDevice.indicateParameter (fxparamIndex, numValue > 0);
                break;

            case PART_RESET:
                cursorDevice.resetParameter (fxparamIndex);
                break;

            default:
                this.host.println ("Unknown FX Parameter value:" + command);
                break;
        }
    }


    private void parseMidi (final LinkedList<String> parts, final Object value)
    {
        if (parts.isEmpty ())
        {
            this.host.println ("Missing Midi command.");
            return;
        }
        int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        final String command = parts.removeFirst ();
        int midiChannel;
        try
        {
            midiChannel = Integer.parseInt (command);
        }
        catch (final NumberFormatException ex)
        {
            switch (command)
            {
                case "velocity":
                    this.configuration.setAccentEnabled (numValue > 0);
                    if (numValue > 0)
                        this.configuration.setAccentValue (numValue);
                    break;

                default:
                    this.host.println ("Unknown Midi command:" + command);
                    break;
            }
            return;
        }

        if (parts.isEmpty ())
        {
            this.host.println ("Missing Midi subcommand.");
            return;
        }
        final String subCommand = parts.removeFirst ();
        switch (subCommand)
        {
            case "note":
                if (parts.isEmpty ())
                {
                    this.host.println ("Missing Midi Note subcommand.");
                    return;
                }
                String n = parts.removeFirst ();
                switch (n)
                {
                    case "+":
                        if (value == null || numValue > 0)
                        {
                            this.scales.incOctave ();
                            this.model.updateNoteMapping ();
                            this.display.notify (this.scales.getRangeText ());
                        }
                        break;

                    case "-":
                        if (value == null || numValue > 0)
                        {
                            this.scales.decOctave ();
                            this.model.updateNoteMapping ();
                            this.display.notify (this.scales.getRangeText ());
                        }
                        break;

                    default:
                        final int note = Integer.parseInt (n);
                        if (numValue > 0)
                            numValue = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : numValue;
                        final int data0 = this.model.getKeyTranslationMatrix ()[note];
                        if (data0 >= 0)
                            this.noteInput.sendRawMidiEvent (0x90 + midiChannel, data0, numValue);

                        // Mark selected notes
                        final int [] keyTranslationMatrix = this.model.getKeyTranslationMatrix ();
                        for (int i = 0; i < 128; i++)
                        {
                            if (keyTranslationMatrix[note] == keyTranslationMatrix[i])
                                this.model.setKeyPressed (i, numValue);
                        }
                }
                break;

            case "drum":
                if (parts.isEmpty ())
                {
                    this.host.println ("Missing Midi Drum subcommand.");
                    return;
                }
                n = parts.removeFirst ();
                switch (n)
                {
                    case "+":
                        if (numValue != 0)
                        {
                            this.scales.incDrumOctave ();
                            this.model.updateNoteMapping ();
                            this.display.notify (this.scales.getDrumRangeText ());
                        }
                        break;

                    case "-":
                        if (numValue != 0)
                        {
                            this.scales.decDrumOctave ();
                            this.model.updateNoteMapping ();
                            this.display.notify (this.scales.getDrumRangeText ());
                        }
                        break;

                    default:
                        final int note = Integer.parseInt (n);
                        if (numValue > 0)
                            numValue = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : numValue;
                        final int data0 = this.model.getDrumTranslationMatrix ()[note];
                        if (data0 >= 0)
                            this.noteInput.sendRawMidiEvent (0x90 + midiChannel, data0, numValue);
                        break;
                }
                break;

            case "cc":
                if (parts.isEmpty ())
                {
                    this.host.println ("Missing Midi CC value.");
                    return;
                }
                final int cc = Integer.parseInt (parts.removeFirst ());
                this.noteInput.sendRawMidiEvent (0xB0 + midiChannel, cc, numValue);
                break;

            case "aftertouch":
                if (numValue > 0)
                    numValue = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : numValue;
                if (parts.isEmpty ())
                {
                    this.noteInput.sendRawMidiEvent (0xD0 + midiChannel, 0, numValue);
                    return;
                }
                final int note = Integer.parseInt (parts.removeFirst ());
                this.noteInput.sendRawMidiEvent (0xA0 + midiChannel, this.model.getKeyTranslationMatrix ()[note], numValue);
                break;

            case "pitchbend":
                this.noteInput.sendRawMidiEvent (0xE0 + midiChannel, 0, numValue);
                break;

            default:
                this.host.println ("Unknown Midi Parameter:" + subCommand);
                break;
        }
    }


    private static LinkedList<String> parseAddress (final OscMessage message)
    {
        final LinkedList<String> oscParts = new LinkedList<> ();
        Collections.addAll (oscParts, message.getAddressPattern ().split ("/"));

        // Remove first empty element
        oscParts.removeFirst ();
        return oscParts;
    }

}