// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.protocol;

import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.OSCControlSurface;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlParser;
import de.mossgrabers.framework.osc.IOpenSoundControlConfiguration;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.utils.KeyManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parser for OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCParser extends AbstractOpenSoundControlParser
{
    private static final String     PART_INDICATE     = "indicate";
    private static final String     PART_VOLUME       = "volume";
    private static final String     PART_RESET        = "reset";
    private static final String     PART_TOUCH        = "touched";

    private static final Pattern    RGB_COLOR_PATTERN = Pattern.compile ("(rgb|RGB)\\((\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?)\\)");

    private final OSCControlSurface surface;
    private final Display           display;
    private final KeyManager        keyManager;


    /**
     * Constructor.
     *
     * @param host The host
     * @param surface The control surface
     * @param model The model
     * @param configuration The configuration
     * @param writer The OSC writer
     * @param midiInput The midi input
     * @param keyManager The key manager
     */
    public OSCParser (final IHost host, final OSCControlSurface surface, final IModel model, final IOpenSoundControlConfiguration configuration, final IOpenSoundControlWriter writer, final IMidiInput midiInput, final KeyManager keyManager)
    {
        super (host, model, midiInput, configuration, writer);

        this.surface = surface;
        this.keyManager = keyManager;
        this.display = new DummyDisplay (host);

        this.model.getCurrentTrackBank ().setIndication (true);
        this.surface.setKeyTranslationTable (model.getScales ().getNoteMatrix ());
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final IOpenSoundControlMessage message)
    {
        this.logMessage (message);

        final LinkedList<String> oscParts = parseAddress (message);
        if (oscParts.isEmpty ())
            return;

        final Object [] values = message.getValues ();
        final Object value = values == null || values.length == 0 ? null : values[0];
        final int numValue = !(value instanceof Number) ? -1 : ((Number) value).intValue ();

        final String command = oscParts.removeFirst ();

        if (this.parseTransportCommands (command, oscParts, value, numValue))
            return;

        if (this.parseGlobalCommands (command))
            return;

        if (this.parsePanelCommands (command, oscParts, value == null ? null : value.toString ()))
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        switch (command)
        {
            //
            // Project
            //

            case "project":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Project subcommand.");
                    return;
                }
                final String subCommand = oscParts.get (0);
                final IProject project = this.model.getProject ();
                switch (subCommand)
                {
                    case "+":
                        project.next ();
                        break;
                    case "-":
                        project.previous ();
                        break;
                    case "engine":
                        if (numValue >= 0)
                            this.model.getApplication ().setEngineActive (numValue > 0);
                        else
                            this.model.getApplication ().toggleEngineActive ();
                        break;
                    case "save":
                        project.save ();
                        break;
                    default:
                        this.host.error ("Unknown Project subcommand: " + subCommand);
                        break;
                }
                break;

            //
            // Scenes
            //

            case "scene":
                final String p = oscParts.removeFirst ();
                final ISceneBank sceneBank = tb.getSceneBank ();
                switch (p)
                {
                    case "bank":
                        if (oscParts.isEmpty ())
                        {
                            this.host.error ("Missing Scene subcommand.");
                            return;
                        }
                        final String subCommand2 = oscParts.get (0);
                        switch (subCommand2)
                        {
                            case "+":
                                if (value == null || numValue > 0)
                                    sceneBank.scrollPageForwards ();
                                break;
                            case "-":
                                if (value == null || numValue > 0)
                                    sceneBank.scrollPageBackwards ();
                                break;
                            default:
                                this.host.error ("Unknown Scene subcommand: " + subCommand2);
                                break;
                        }
                        break;
                    case "+":
                        if (value == null || numValue > 0)
                            sceneBank.scrollForwards ();
                        break;
                    case "-":
                        if (value == null || numValue > 0)
                            sceneBank.scrollBackwards ();
                        break;
                    case "create":
                        this.model.getProject ().createSceneFromPlayingLauncherClips ();
                        break;
                    default:
                        if (oscParts.isEmpty ())
                        {
                            this.host.error ("Missing Scene index.");
                            return;
                        }
                        final int scene = Integer.parseInt (p);
                        final String sceneCommand = oscParts.removeFirst ();
                        switch (sceneCommand)
                        {
                            case "launch":
                                sceneBank.getItem (scene - 1).launch ();
                                break;
                            default:
                                this.host.error ("Unknown Scene subcommand: " + sceneCommand);
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
                    this.host.error ("Missing Track index or command.");
                    return;
                }
                try
                {
                    final int trackNo = Integer.parseInt (oscParts.get (0));
                    oscParts.removeFirst ();
                    this.parseTrackValue (tb.getItem (trackNo - 1), oscParts, value);
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
                this.parseDeviceValue (this.model.getInstrumentDevice (), oscParts, value);
                break;

            //
            // Marker
            //

            case "marker":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Marker index or command.");
                    return;
                }
                try
                {
                    final int markerNo = Integer.parseInt (oscParts.get (0));
                    oscParts.removeFirst ();
                    this.parseMarkerValue (this.model.getMarkerBank ().getItem (markerNo - 1), oscParts);
                }
                catch (final NumberFormatException ex)
                {
                    this.parseMarker (oscParts);
                }
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

            default:
                this.host.println ("Unknown OSC Command: " + message.getAddress () + " " + value);
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
                switch (cmd)
                {
                    case PART_VOLUME:
                        this.transport.setMetronomeVolume (numValue);
                        break;
                    case "ticks":
                        if (value == null || numValue > 0)
                            this.transport.toggleMetronomeTicks ();
                        break;
                    case "preroll":
                        if (value == null || numValue > 0)
                            this.transport.togglePrerollMetronome ();
                        break;
                    default:
                        this.host.error ("Unknown Click subcommand: " + cmd);
                        break;
                }
                return true;

            case "quantize":
                this.clip.quantize (1);
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
                        this.host.error ("Unknown Tempo subcommand: " + tempoCommand);
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
                        this.host.error ("Unknown Position subcommand: " + positionCommand);
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
                this.transport.setPrerollAsBars (numValue);
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
                    this.host.error ("Missing Panel subcommand.");
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
                        this.host.error ("Unknown Panel subcommand: " + subCommand);
                }
                return true;

            case "arranger":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Arranger subcommand.");
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
                        this.host.error ("Unknown Arranger subcommand: " + subCommand2);
                }
                return true;

            case "mixer":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Mixer subcommand.");
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
                        this.host.error ("Unknown Mixer subcommand: " + subCommand3);
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
            this.host.error ("Missing Track command.");
            return;
        }

        final ITrackBank tb = this.model.getCurrentTrackBank ();

        final String command = oscParts.removeFirst ();
        switch (command)
        {
            case PART_INDICATE:
            {
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Indicate subcommand.");
                    return;
                }
                final boolean isTrue = numValue > 0;
                final String subCommand = oscParts.removeFirst ();
                switch (subCommand)
                {
                    case PART_VOLUME:
                        for (int i = 0; i < tb.getPageSize (); i++)
                            tb.getItem (i).setVolumeIndication (isTrue);
                        break;
                    case "pan":
                        for (int i = 0; i < tb.getPageSize (); i++)
                            tb.getItem (i).setPanIndication (isTrue);
                        break;
                    case "send":
                        if (!this.model.isEffectTrackBankActive ())
                        {
                            final int sendIndex = Integer.parseInt (oscParts.get (0));
                            for (int i = 0; i < tb.getPageSize (); i++)
                                tb.getItem (i).getSendBank ().getItem (sendIndex - 1).setIndication (isTrue);
                        }
                        break;
                    default:
                        this.host.error ("Unknown Indicate subcommand: " + subCommand);
                        break;
                }
                break;
            }

            case "bank":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Track Bank subcommand.");
                    return;
                }
                final String subCommand = oscParts.removeFirst ();
                switch (subCommand)
                {
                    case "page":
                        if (oscParts.isEmpty ())
                        {
                            this.host.error ("Missing Track Bank Page subcommand.");
                            return;
                        }
                        if ("+".equals (oscParts.removeFirst ()))
                        {
                            if (!tb.canScrollForwards ())
                                return;
                            tb.scrollPageForwards ();
                            this.host.scheduleTask ( () -> tb.getItem (0).select (), 75);
                        }
                        else // "-"
                        {
                            if (!tb.canScrollBackwards ())
                                return;
                            tb.scrollPageBackwards ();
                            this.host.scheduleTask ( () -> tb.getItem (7).select (), 75);
                        }
                        break;
                    case "+":
                        tb.scrollForwards ();
                        break;
                    case "-":
                        tb.scrollBackwards ();
                        break;
                    default:
                        this.host.error ("Unknown Track Bank subcommand: " + subCommand);
                        break;
                }
                break;

            case "+":
            {
                final ITrack sel = tb.getSelectedItem ();
                final int index = sel == null ? 0 : sel.getIndex () + 1;
                if (index == tb.getPageSize ())
                {
                    if (!tb.canScrollForwards ())
                        return;
                    tb.scrollPageForwards ();
                    this.host.scheduleTask ( () -> tb.getItem (0).select (), 75);
                    return;
                }
                tb.getItem (index).select ();
                break;
            }

            case "-":
            {
                final ITrack sel = tb.getSelectedItem ();
                final int index = sel == null ? 0 : sel.getIndex () - 1;
                if (index == -1)
                {
                    if (!tb.canScrollBackwards ())
                        return;
                    tb.scrollPageBackwards ();
                    this.host.scheduleTask ( () -> tb.getItem (7).select (), 75);
                    return;
                }
                tb.getItem (index).select ();
                break;
            }

            case "add":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Add subcommand.");
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
                        this.host.error ("Unknown Add subcommand: " + subCommand2);
                        break;
                }
                break;

            case "stop":
                this.model.getCurrentTrackBank ().stop ();
                break;

            case "vu":
                ((OSCConfiguration) this.configuration).setVUMetersEnabled (numValue > 0);
                break;

            case "toggleBank":
            {
                if (this.model.getEffectTrackBank () == null)
                    return;

                this.model.toggleCurrentTrackBank ();
                final ITrackBank tbNew = this.model.getCurrentTrackBank ();
                // Make sure a track is selected
                final ITrackBank tbOther = this.model.isEffectTrackBankActive () ? this.model.getTrackBank () : this.model.getEffectTrackBank ();
                final ITrack selectedTrack = tbNew.getSelectedItem ();
                if (selectedTrack == null)
                    tbNew.getItem (0).select ();
                // Move the indication to the other bank
                for (int i = 0; i < tbNew.getPageSize (); i++)
                {
                    final ITrack otherTrack = tbOther.getItem (i);
                    otherTrack.setVolumeIndication (false);
                    otherTrack.setPanIndication (false);
                    final ITrack track = tbNew.getItem (i);
                    track.setVolumeIndication (true);
                    track.setPanIndication (true);
                }
                break;
            }

            case "parent":
            {
                tb.selectParent ();
                break;
            }

            case "selected":
                final ITrack selectedTrack = tb.getSelectedItem ();
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
            this.host.error ("Missing Track command.");
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
                    track.select ();
                break;

            case PART_VOLUME:
                if (parts.isEmpty ())
                    track.setVolume (numValue);
                else if (PART_INDICATE.equals (parts.get (0)))
                    track.setVolumeIndication (numValue > 0);
                else if (PART_RESET.equals (parts.get (0)))
                    track.resetVolume ();
                else if (PART_TOUCH.equals (parts.get (0)))
                    track.touchVolume (numValue > 0);
                break;

            case "pan":
                if (parts.isEmpty ())
                    track.setPan (numValue);
                else if (PART_INDICATE.equals (parts.get (0)))
                    track.setPanIndication (numValue > 0);
                else if (PART_RESET.equals (parts.get (0)))
                    track.resetPan ();
                else if (PART_TOUCH.equals (parts.get (0)))
                    track.touchPan (numValue > 0);
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
                    this.host.error ("Missing Clip subcommand.");
                    return;
                }
                this.parseClipValue (track, parts, value);
                break;

            case "enter":
                track.enter ();
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


    private void parseClipValue (final ITrack track, final LinkedList<String> parts, final Object value)
    {
        final String cmd = parts.removeFirst ();
        try
        {
            final int clipNo = Integer.parseInt (cmd);
            if (parts.isEmpty ())
            {
                this.host.error ("Missing Clip subcommand.");
                return;
            }
            final String clipCommand = parts.removeFirst ();
            final ISlot slot = track.getSlotBank ().getItem (clipNo - 1);
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
                case "remove":
                    slot.remove ();
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
    }


    private void parseSendValue (final ITrack track, final int sendIndex, final LinkedList<String> parts, final Object value)
    {
        if (parts.isEmpty ())
        {
            this.host.error ("Missing Send subcommand.");
            return;
        }

        final double numValue = value instanceof Number ? ((Number) value).doubleValue () : -1;

        final String command = parts.removeFirst ();
        switch (command)
        {
            case PART_VOLUME:
                final ISend send = track.getSendBank ().getItem (sendIndex);
                if (send != null)
                {
                    if (parts.isEmpty ())
                        send.setValue (numValue);
                    else if (PART_INDICATE.equals (parts.get (0)))
                        send.setIndication (numValue > 0);
                    else if (PART_TOUCH.equals (parts.get (0)))
                        send.touchValue (numValue > 0);
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
            this.host.error ("Missing Device subcommand.");
            return;
        }

        final int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        final String command = oscParts.removeFirst ();
        final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
        switch (command)
        {
            case "page":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Device Page subcommand.");
                    return;
                }
                final int bankNo = Integer.parseInt (oscParts.removeFirst ());
                final String subCommand = oscParts.removeFirst ();
                switch (subCommand)
                {
                    case "selected":
                        if (numValue > 0)
                            cursorDevice.getParameterPageBank ().selectPage (bankNo - 1);
                        break;

                    default:
                        this.host.println ("Unknown Device Device Page Parameter: " + subCommand);
                        break;
                }
                break;

            case "sibling":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Device Sibling subcommand.");
                    return;
                }
                final int siblingNo = Integer.parseInt (oscParts.removeFirst ());
                final String subCommand2 = oscParts.removeFirst ();
                switch (subCommand2)
                {
                    case "selected":
                        if (numValue > 0)
                            deviceBank.getItem (siblingNo - 1).select ();
                        break;

                    default:
                        this.host.println ("Unknown Device Device Sibling Parameter: " + subCommand2);
                        break;
                }
                break;

            case "bank":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Device Bank subcommand.");
                    return;
                }
                final String subCommand3 = oscParts.removeFirst ();
                switch (subCommand3)
                {
                    case "page":
                        if (oscParts.isEmpty ())
                        {
                            this.host.error ("Missing Device Bank Page subcommand.");
                            return;
                        }
                        if ("+".equals (oscParts.removeFirst ()))
                        {
                            deviceBank.scrollPageForwards ();
                            this.host.scheduleTask ( () -> deviceBank.getItem (0).select (), 75);
                        }
                        else // "-"
                        {
                            deviceBank.scrollPageBackwards ();
                            this.host.scheduleTask ( () -> deviceBank.getItem (deviceBank.getPageSize () - 1).select (), 75);
                        }
                        break;
                    default:
                        this.host.error ("Unknown Device Bank subcommand: " + subCommand3);
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
                    this.host.error ("Missing Device Indicate subcommand.");
                    return;
                }
                switch (oscParts.removeFirst ())
                {
                    case "param":
                        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
                        for (int i = 0; i < parameterBank.getPageSize (); i++)
                            parameterBank.getItem (i).setIndication (numValue > 0);
                        break;

                    default:
                        this.host.println ("Unknown Device Indicate Parameter: " + command);
                        break;
                }
                break;

            case "param":
                if (oscParts.isEmpty ())
                {
                    this.host.error ("Missing Device Param subcommand.");
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
                                cursorDevice.getParameterBank ().scrollPageForwards ();
                                break;
                            case "-":
                                cursorDevice.getParameterBank ().scrollPageBackwards ();
                                break;

                            case "bank":
                                if (oscParts.isEmpty ())
                                {
                                    this.host.error ("Missing Device Param Bank subcommand.");
                                    return;
                                }
                                final String subCommand4 = oscParts.removeFirst ();
                                switch (subCommand4)
                                {
                                    case "page":
                                        if (oscParts.isEmpty ())
                                        {
                                            this.host.error ("Missing Device Param Bank Page subcommand.");
                                            return;
                                        }
                                        if ("+".equals (oscParts.removeFirst ()))
                                            cursorDevice.getParameterPageBank ().scrollForwards ();
                                        else // "-"
                                            cursorDevice.getParameterPageBank ().scrollBackwards ();
                                        break;
                                    default:
                                        this.host.error ("Unknown Device Param Bank subcommand: " + subCommand4);
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
        final String command = parts.removeFirst ();
        try
        {
            final int layerNo;
            if ("selected".equals (command))
            {
                final IChannel selectedLayerOrDrumPad = cursorDevice.getLayerOrDrumPadBank ().getSelectedItem ();
                layerNo = selectedLayerOrDrumPad == null ? -1 : selectedLayerOrDrumPad.getIndex ();
            }
            else
            {
                layerNo = Integer.parseInt (command) - 1;
            }
            this.parseDeviceLayerValue (cursorDevice, layerNo, parts, value);
        }
        catch (final NumberFormatException ex)
        {
            switch (command)
            {
                case "parent":
                    if (cursorDevice.doesExist ())
                    {
                        cursorDevice.selectParent ();
                        cursorDevice.selectChannel ();
                    }
                    break;

                case "+":
                    cursorDevice.getLayerOrDrumPadBank ().selectNextItem ();
                    break;

                case "-":
                    cursorDevice.getLayerOrDrumPadBank ().selectPreviousItem ();
                    break;

                case "page":
                    if (parts.isEmpty ())
                    {
                        this.host.println ("Missing Layer/Drumpad Page subcommand: " + command);
                        return;
                    }
                    if ("+".equals (parts.get (0)))
                        cursorDevice.getLayerOrDrumPadBank ().selectNextPage ();
                    else
                        cursorDevice.getLayerOrDrumPadBank ().selectPreviousPage ();
                    break;

                default:
                    this.host.println ("Unknown Layour/Drum command: " + command);
                    break;
            }
        }
    }


    private void parseMarker (final LinkedList<String> parts)
    {
        if (parts.isEmpty ())
        {
            this.host.println ("Missing Marker command.");
            return;
        }

        final IMarkerBank markerBank = this.model.getMarkerBank ();

        final String command = parts.removeFirst ();
        switch (command)
        {
            case "bank":
                if (parts.isEmpty ())
                {
                    this.host.error ("Missing Marker Bank subcommand.");
                    return;
                }
                final String subCommand = parts.removeFirst ();
                switch (subCommand)
                {
                    case "+":
                        markerBank.scrollPageForwards ();
                        this.host.scheduleTask ( () -> markerBank.getItem (0).select (), 75);
                        break;
                    case "-":
                        markerBank.scrollPageBackwards ();
                        this.host.scheduleTask ( () -> markerBank.getItem (markerBank.getPageSize () - 1).select (), 75);
                        break;
                    default:
                        this.host.error ("Unknown Marker Bank subcommand: " + subCommand);
                        break;
                }
                break;
            default:
                this.host.println ("Unknown Marker Command: " + command);
                break;
        }
    }


    private void parseMarkerValue (final IMarker marker, final LinkedList<String> parts)
    {
        if (parts.isEmpty ())
        {
            this.host.error ("Missing Marker command.");
            return;
        }

        final String command = parts.removeFirst ();
        switch (command)
        {
            case "launch":
                marker.launch (true);
                break;
            default:
                this.host.println ("Unknown Marker Command: " + command);
                break;
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


    private void parseDeviceLayerValue (final ICursorDevice cursorDevice, final int layerIndex, final LinkedList<String> parts, final Object value)
    {
        if (parts.isEmpty ())
        {
            this.host.println ("Missing Device Layer command.");
            return;
        }
        final String command = parts.removeFirst ();
        final int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        final IChannel layer = cursorDevice.getLayerOrDrumPadBank ().getItem (layerIndex);
        switch (command)
        {
            case "select":
                cursorDevice.getLayerOrDrumPadBank ().getItem (layerIndex).select ();
                break;

            case PART_VOLUME:
                if (parts.isEmpty ())
                    layer.setVolume (numValue);
                else if (PART_TOUCH.equals (parts.get (0)))
                    layer.touchVolume (numValue > 0);
                break;

            case "pan":
                if (parts.isEmpty ())
                    layer.setPan (numValue);
                else if (PART_TOUCH.equals (parts.get (0)))
                    layer.touchPan (numValue > 0);
                break;

            case "mute":
                if (numValue < 0)
                    layer.toggleMute ();
                else
                    layer.setMute (numValue > 0);
                break;

            case "solo":
                if (numValue < 0)
                    layer.toggleSolo ();
                else
                    layer.setSolo (numValue > 0);
                break;

            case "send":
                final int sendNo = Integer.parseInt (parts.removeFirst ()) - 1;
                if (parts.isEmpty ())
                    layer.getSendBank ().getItem (sendNo).setValue (numValue);
                else if (PART_TOUCH.equals (parts.get (0)))
                    layer.getSendBank ().getItem (sendNo).touchValue (numValue > 0);
                break;

            case "enter":
                layer.enter ();
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
                    cursorDevice.getParameterBank ().getItem (fxparamIndex).setValue (numValue);
                break;

            case PART_INDICATE:
                if (parts.size () == 1 && value != null)
                    cursorDevice.getParameterBank ().getItem (fxparamIndex).setIndication (numValue > 0);
                break;

            case PART_RESET:
                cursorDevice.getParameterBank ().getItem (fxparamIndex).resetValue ();
                break;

            case PART_TOUCH:
                cursorDevice.getParameterBank ().getItem (fxparamIndex).touchValue (numValue > 0);
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
        final OSCConfiguration conf = (OSCConfiguration) this.configuration;
        try
        {
            midiChannel = Math.max (Math.min (0, Integer.parseInt (command) - 1), 15);
        }
        catch (final NumberFormatException ex)
        {
            switch (command)
            {
                case "velocity":
                    conf.setAccentEnabled (numValue > 0);
                    if (numValue > 0)
                        conf.setAccentValue (numValue);
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
                            this.surface.setKeyTranslationTable (this.model.getScales ().getNoteMatrix ());
                            this.display.notify (this.scales.getRangeText ());
                        }
                        break;

                    case "-":
                        if (value == null || numValue > 0)
                        {
                            this.scales.decOctave ();
                            this.surface.setKeyTranslationTable (this.model.getScales ().getNoteMatrix ());
                            this.display.notify (this.scales.getRangeText ());
                        }
                        break;

                    default:
                        final int note = Integer.parseInt (n);
                        if (numValue > 0)
                            numValue = conf.isAccentActive () ? conf.getFixedAccentValue () : numValue;
                        final int [] keyTranslationMatrix = this.surface.getKeyTranslationTable ();
                        final int data0 = keyTranslationMatrix[note];
                        if (data0 >= 0)
                            this.midiInput.sendRawMidiEvent (0x90 + midiChannel, data0, numValue);

                        // Mark selected notes
                        for (int i = 0; i < 128; i++)
                        {
                            if (keyTranslationMatrix[note] == keyTranslationMatrix[i])
                                this.keyManager.setKeyPressed (i, numValue);
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
                            this.display.notify (this.scales.getDrumRangeText ());
                        }
                        break;

                    case "-":
                        if (numValue != 0)
                        {
                            this.scales.decDrumOctave ();
                            this.display.notify (this.scales.getDrumRangeText ());
                        }
                        break;

                    default:
                        final int note = Integer.parseInt (n);
                        if (numValue > 0)
                            numValue = conf.isAccentActive () ? conf.getFixedAccentValue () : numValue;
                        final int data0 = this.model.getScales ().getDrumMatrix ()[note];
                        if (data0 >= 0)
                            this.midiInput.sendRawMidiEvent (0x90 + midiChannel, data0, numValue);
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
                this.midiInput.sendRawMidiEvent (0xB0 + midiChannel, cc, numValue);
                break;

            case "aftertouch":
                if (numValue > 0)
                    numValue = conf.isAccentActive () ? conf.getFixedAccentValue () : numValue;
                if (parts.isEmpty ())
                {
                    this.midiInput.sendRawMidiEvent (0xD0 + midiChannel, 0, numValue);
                    return;
                }
                final int note = Integer.parseInt (parts.removeFirst ());
                this.midiInput.sendRawMidiEvent (0xA0 + midiChannel, this.surface.getKeyTranslationTable ()[note], numValue);
                break;

            case "pitchbend":
                this.midiInput.sendRawMidiEvent (0xE0 + midiChannel, 0, numValue);
                break;

            default:
                this.host.println ("Unknown Midi Parameter:" + subCommand);
                break;
        }
    }


    private static LinkedList<String> parseAddress (final IOpenSoundControlMessage message)
    {
        final LinkedList<String> oscParts = new LinkedList<> ();
        Collections.addAll (oscParts, message.getAddress ().split ("/"));

        // Remove first empty element
        oscParts.removeFirst ();
        return oscParts;
    }
}