// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc.protocol;

import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.MasterTrackProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
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
    private static final Pattern   RGB_COLOR_PATTERN = Pattern.compile ("(rgb|RGB)\\((\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?)\\)");

    private final OSCModel         model;
    private final ITransport       transport;
    private final MasterTrackProxy masterTrack;
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

        this.display = new DummyDisplay (host);

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
        final LinkedList<String> oscParts = new LinkedList<> ();
        Collections.addAll (oscParts, message.getAddressPattern ().split ("/"));

        // Remove first empty element
        oscParts.removeFirst ();
        if (oscParts.isEmpty ())
            return;

        final List<Object> values = message.getArguments ();
        Object value = values.isEmpty () ? null : values.get (0);
        final int numValue = value == null || !(value instanceof Number) ? -1 : ((Number) value).intValue ();

        switch (oscParts.removeFirst ())
        {
            //
            // Global
            //

            case "refresh":
                this.writer.flush (true);
                break;

            case "undo":
                this.model.getApplication ().undo ();
                break;

            case "redo":
                this.model.getApplication ().redo ();
                break;

            //
            // Transport
            //

            case "play":
                if (value == null || numValue > 0 && !this.transport.isPlaying ())
                    this.transport.play ();
                break;

            case "stop":
                if (value == null || numValue > 0 && this.transport.isPlaying ())
                    this.transport.play ();
                break;

            case "restart":
                if (value == null || numValue > 0)
                    this.transport.restart ();
                break;

            case "record":
                if (value == null || numValue > 0)
                    this.transport.record ();
                break;

            case "overdub":
                if (value != null && numValue == 0)
                    return;
                if (!oscParts.isEmpty () && "launcher".equals (oscParts.get (0)))
                    this.transport.toggleLauncherOverdub ();
                else
                    this.transport.toggleOverdub ();
                break;

            case "repeat":
                if (value == null)
                    this.transport.toggleLoop ();
                else
                    this.transport.setLoop (numValue > 0);
                break;

            case "punchIn":
                if (value == null)
                    this.transport.togglePunchIn ();
                break;

            case "punchOut":
                if (value == null)
                    this.transport.togglePunchOut ();
                break;

            case "click":
                if (value == null)
                    this.transport.toggleMetronome ();
                else
                    this.transport.setMetronome (numValue > 0);
                break;

            case "quantize":
                this.model.getClip ().quantize (1);
                break;

            case "tempo":
                switch (oscParts.get (0))
                {
                    case "raw":
                        if (value instanceof Number)
                            this.transport.setTempo (((Number) value).doubleValue ());
                        break;
                    case "tap":
                        this.transport.tapTempo ();
                        break;
                    case "+":
                        if (value == null)
                            value = Integer.valueOf (1);
                        if (value instanceof Number)
                            this.transport.setTempo (this.transport.getTempo () + ((Number) value).doubleValue ());
                        break;
                    case "-":
                        if (value == null)
                            value = Integer.valueOf (1);
                        if (value instanceof Number)
                            this.transport.setTempo (this.transport.getTempo () - ((Number) value).doubleValue ());
                        break;
                }
                break;

            case "time":
                if (value instanceof Number)
                    this.transport.setPosition (((Number) value).doubleValue ());
                break;

            case "position":
                switch (oscParts.get (0))
                {
                    case "+":
                        this.transport.changePosition (true, true);
                        break;
                    case "-":
                        this.transport.changePosition (false, true);
                        break;
                    case "++":
                        this.transport.changePosition (true, false);
                        break;
                    case "--":
                        this.transport.changePosition (false, false);
                        break;
                }
                break;

            case "crossfade":
                this.transport.setCrossfade (numValue);
                break;

            case "autowrite":
                if (!oscParts.isEmpty () && "launcher".equals (oscParts.get (0)))
                    this.transport.toggleWriteClipLauncherAutomation ();
                else
                    this.transport.toggleWriteArrangerAutomation ();
                break;

            case "automationWriteMode":
                if (!oscParts.isEmpty ())
                    this.transport.setAutomationWriteMode (oscParts.get (0));
                break;

            case "preroll":
                switch (numValue)
                {
                    case 0:
                        this.transport.setPreroll (ITransport.PREROLL_NONE);
                        break;
                    case 1:
                        this.transport.setPreroll (ITransport.PREROLL_1_BAR);
                        break;
                    case 2:
                        this.transport.setPreroll (ITransport.PREROLL_2_BARS);
                        break;
                    case 4:
                        this.transport.setPreroll (ITransport.PREROLL_4_BARS);
                        break;
                }
                break;

            //
            // Frames
            //

            case "layout":
                if (!oscParts.isEmpty ())
                    this.model.getApplication ().setPanelLayout (oscParts.get (0).toUpperCase ());
                break;

            case "panel":
                final IApplication app = this.model.getApplication ();
                switch (oscParts.get (0))
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
                }
                break;

            case "arranger":
                final IArranger arrange = this.model.getArranger ();
                switch (oscParts.get (0))
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
                }
                break;

            case "mixer":
                final IMixer mix = this.model.getMixer ();
                switch (oscParts.get (0))
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
                }
                break;

            //
            // Project
            //

            case "project":
                switch (oscParts.get (0))
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
                        switch (oscParts.removeFirst ())
                        {
                            case "+":
                                if (value == null || numValue > 0)
                                    this.model.getCurrentTrackBank ().scrollScenesPageDown ();
                                break;
                            case "-":
                                if (value == null || numValue > 0)
                                    this.model.getCurrentTrackBank ().scrollScenesPageUp ();
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
                    default:
                        final int scene = Integer.parseInt (p);
                        switch (oscParts.removeFirst ())
                        {
                            case "launch":
                                this.model.getCurrentTrackBank ().launchScene (scene - 1);
                                break;
                        }
                        break;
                }
                break;

            //
            // Master-/Track(-commands)
            //

            case "track":
                try
                {
                    final int trackNo = Integer.parseInt (oscParts.get (0));
                    oscParts.removeFirst ();
                    this.parseTrackValue (trackNo - 1, oscParts, value);
                }
                catch (final NumberFormatException ex)
                {
                    this.parseTrackCommands (oscParts, value);
                }
                break;

            case "master":
                this.parseTrackValue (-1, oscParts, value);
                break;

            //
            // Device
            //

            case "device":
            {
                final CursorDeviceProxy cd = this.model.getCursorDevice ();
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
                    return;
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
                this.host.println ("Unhandled OSC Command: " + message.getAddressPattern () + " " + value);
                break;
        }
    }


    private void parseTrackCommands (final LinkedList<String> parts, final Object value)
    {
        final String p = parts.removeFirst ();
        switch (p)
        {
            case "indicate":
            {
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final boolean isTrue = value != null && value instanceof Number && ((Number) value).intValue () > 0;
                switch (parts.removeFirst ())
                {
                    case "volume":
                        for (int i = 0; i < tb.getNumTracks (); i++)
                            tb.setVolumeIndication (i, isTrue);
                        break;
                    case "pan":
                        for (int i = 0; i < tb.getNumTracks (); i++)
                            tb.setPanIndication (i, isTrue);
                        break;
                    case "send":
                        if (tb instanceof TrackBankProxy)
                        {
                            final int sendIndex = Integer.parseInt (parts.get (0));
                            for (int i = 0; i < tb.getNumTracks (); i++)
                                ((TrackBankProxy) tb).setSendIndication (i, sendIndex - 1, isTrue);
                        }
                        break;
                }
                break;
            }

            case "bank":
                switch (parts.removeFirst ())
                {
                    case "page":
                        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                        if ("+".equals (parts.removeFirst ()))
                        {
                            if (!tb.canScrollTracksDown ())
                                return;
                            tb.scrollTracksPageDown ();
                            this.host.scheduleTask ( () -> this.selectTrack (0), 75);
                        }
                        else // "-"
                        {
                            if (!tb.canScrollTracksUp ())
                                return;
                            tb.scrollTracksPageUp ();
                            this.host.scheduleTask ( () -> this.selectTrack (7), 75);
                        }
                        break;

                    case "+":
                        this.model.getCurrentTrackBank ().scrollTracksDown ();
                        break;

                    case "-":
                        this.model.getCurrentTrackBank ().scrollTracksUp ();
                        break;
                }
                break;

            case "+":
            {
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final TrackData sel = tb.getSelectedTrack ();
                final int index = sel == null ? 0 : sel.getIndex () + 1;
                if (index == tb.getNumTracks ())
                {
                    if (!tb.canScrollTracksDown ())
                        return;
                    tb.scrollTracksPageDown ();
                    this.host.scheduleTask ( () -> this.selectTrack (0), 75);
                }
                this.selectTrack (index);
                break;
            }

            case "-":
            {
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final TrackData sel = tb.getSelectedTrack ();
                final int index = sel == null ? 0 : sel.getIndex () - 1;
                if (index == -1)
                {
                    if (!tb.canScrollTracksUp ())
                        return;
                    tb.scrollTracksPageUp ();
                    this.host.scheduleTask ( () -> this.selectTrack (7), 75);
                    return;
                }
                this.selectTrack (index);
                break;
            }

            case "add":
                switch (parts.get (0))
                {
                    case "audio":
                        this.model.getApplication ().addAudioTrack ();
                        break;
                    case "effect":
                        this.model.getApplication ().addEffectTrack ();
                        break;
                    case "instrument":
                        this.model.getApplication ().addInstrumentTrack ();
                        break;
                }
                break;

            case "stop":
                this.model.getCurrentTrackBank ().stop ();
                break;

            case "vu":
                this.configuration.setVUMetersEnabled (value != null && value instanceof Number && ((Number) value).intValue () > 0);
                break;

            case "toggleBank":
            {
                this.model.toggleCurrentTrackBank ();
                // Make sure a track is selected
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final AbstractTrackBankProxy tbOther = this.model.isEffectTrackBankActive () ? this.model.getTrackBank () : this.model.getEffectTrackBank ();
                final TrackData track = tb.getSelectedTrack ();
                if (track == null)
                    this.selectTrack (0);
                // Move the indication to the other bank
                for (int i = 0; i < tb.getNumTracks (); i++)
                {
                    tbOther.setVolumeIndication (i, false);
                    tbOther.setPanIndication (i, false);
                    tb.setVolumeIndication (i, true);
                    tb.setPanIndication (i, true);
                }
                break;
            }

            case "parent":
            {
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                if (tb instanceof TrackBankProxy)
                    ((TrackBankProxy) tb).selectParent ();
                break;
            }

            default:
                this.host.println ("Unhandled Track Command: " + p);
                break;
        }
    }


    private void parseTrackValue (final int trackIndex, final LinkedList<String> parts, final Object value)
    {
        final double numValue = value instanceof Number ? ((Number) value).doubleValue () : -1;
        final int intValue = value instanceof Number ? ((Number) value).intValue () : -1;
        String p = parts.removeFirst ();
        switch (p)
        {
            case "activated":
                if (trackIndex == -1)
                    this.masterTrack.setIsActivated (intValue > 0);
                else
                    this.model.getCurrentTrackBank ().setIsActivated (trackIndex, intValue > 0);
                break;

            case "crossfadeMode":
                if (trackIndex >= 0 && numValue == 1)
                    this.model.getCurrentTrackBank ().setCrossfadeMode (trackIndex, parts.removeFirst ());
                break;

            case "select":
                if (intValue == 0)
                    return;
                if (trackIndex == -1)
                    this.masterTrack.select ();
                else
                    this.selectTrack (trackIndex);
                break;

            case "volume":
                if (parts.isEmpty ())
                {
                    if (trackIndex == -1)
                        this.masterTrack.setVolume (numValue);
                    else
                        this.model.getCurrentTrackBank ().setVolume (trackIndex, numValue);
                }
                else if ("indicate".equals (parts.get (0)))
                {
                    if (trackIndex == -1)
                        this.masterTrack.setVolumeIndication (numValue > 0);
                    else
                        this.model.getCurrentTrackBank ().setVolumeIndication (trackIndex, numValue > 0);
                }
                break;

            case "pan":
                if (parts.isEmpty ())
                {
                    if (trackIndex == -1)
                        this.masterTrack.setPan (numValue);
                    else
                        this.model.getCurrentTrackBank ().setPan (trackIndex, numValue);
                }
                else if ("indicate".equals (parts.get (0)))
                {
                    if (trackIndex == -1)
                        this.masterTrack.setPanIndication (numValue > 0);
                    else
                        this.model.getCurrentTrackBank ().setPanIndication (trackIndex, numValue > 0);
                }
                break;

            case "mute":
                if (trackIndex == -1)
                {
                    if (numValue < 0)
                        this.masterTrack.toggleMute ();
                    else
                        this.masterTrack.setMute (numValue > 0);
                }
                else
                {
                    if (numValue < 0)
                        this.model.getCurrentTrackBank ().toggleMute (trackIndex);
                    else
                        this.model.getCurrentTrackBank ().setMute (trackIndex, numValue > 0);
                }
                break;

            case "solo":
                if (trackIndex == -1)
                {
                    if (numValue < 0)
                        this.masterTrack.toggleSolo ();
                    else
                        this.masterTrack.setSolo (numValue > 0);
                }
                else
                {
                    if (numValue < 0)
                        this.model.getCurrentTrackBank ().toggleSolo (trackIndex);
                    else
                        this.model.getCurrentTrackBank ().setSolo (trackIndex, numValue > 0);
                }
                break;

            case "recarm":
                if (trackIndex == -1)
                {
                    if (numValue < 0)
                        this.masterTrack.toggleArm ();
                    else
                        this.masterTrack.setArm (numValue > 0);
                }
                else
                {
                    if (numValue < 0)
                        this.model.getCurrentTrackBank ().toggleArm (trackIndex);
                    else
                        this.model.getCurrentTrackBank ().setArm (trackIndex, numValue > 0);
                }
                break;

            case "monitor":
                final boolean isAuto = !parts.isEmpty () && "auto".equals (parts.get (0));
                if (trackIndex == -1)
                {
                    if (numValue < 0)
                        if (isAuto)
                            this.masterTrack.toggleAutoMonitor ();
                        else
                            this.masterTrack.toggleMonitor ();
                    else if (isAuto)
                        this.masterTrack.setAutoMonitor (numValue > 0);
                    else
                        this.masterTrack.setMonitor (numValue > 0);
                }
                else
                {
                    if (numValue < 0)
                        if (isAuto)
                            this.model.getCurrentTrackBank ().toggleAutoMonitor (trackIndex);
                        else
                            this.model.getCurrentTrackBank ().toggleMonitor (trackIndex);
                    else if (isAuto)
                        this.model.getCurrentTrackBank ().setAutoMonitor (trackIndex, numValue > 0);
                    else
                        this.model.getCurrentTrackBank ().setMonitor (trackIndex, numValue > 0);
                }
                break;

            case "send":
                final int sendNo = Integer.parseInt (parts.removeFirst ());
                this.parseSendValue (trackIndex, sendNo - 1, parts, value);
                break;

            case "clip":
                p = parts.removeFirst ();
                try
                {
                    final int clipNo = Integer.parseInt (p);
                    p = parts.removeFirst ();
                    switch (p)
                    {
                        case "select":
                            this.model.getCurrentTrackBank ().selectClip (trackIndex, clipNo - 1);
                            break;
                        case "launch":
                            this.model.getCurrentTrackBank ().launchClip (trackIndex, clipNo - 1);
                            break;
                        case "record":
                            this.model.getCurrentTrackBank ().recordClip (trackIndex, clipNo - 1);
                            break;
                        case "color":
                            final Matcher matcher = RGB_COLOR_PATTERN.matcher (value.toString ());
                            if (!matcher.matches ())
                                return;
                            final int count = matcher.groupCount ();
                            if (count != 7)
                                return;
                            final double red = Double.parseDouble (matcher.group (2));
                            final double green = Double.parseDouble (matcher.group (4));
                            final double blue = Double.parseDouble (matcher.group (6));
                            this.model.getCurrentTrackBank ().getTrack (trackIndex).getSlots ()[clipNo - 1].setColor (red, green, blue);
                            break;
                        default:
                            this.host.println ("Unhandled clip parameter: " + p);
                            break;
                    }
                }
                catch (final NumberFormatException ex)
                {
                    switch (p)
                    {
                        case "stop":
                            this.model.getCurrentTrackBank ().stop (trackIndex);
                            break;
                        case "returntoarrangement":
                            this.model.getCurrentTrackBank ().returnToArrangement (trackIndex);
                            break;
                        default:
                            this.host.println ("Unhandled clip command: " + p);
                            break;
                    }
                }
                break;

            case "enter":
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                // TODO API extension required - selectChildren() should be available for Track as
                // well
                if (tb instanceof TrackBankProxy)
                    ((TrackBankProxy) tb).selectChildren ();
                break;

            case "color":
                final Matcher matcher = RGB_COLOR_PATTERN.matcher (value.toString ());
                if (!matcher.matches ())
                    return;
                final int count = matcher.groupCount ();
                if (count != 7)
                    return;
                final double red = Double.parseDouble (matcher.group (2));
                final double green = Double.parseDouble (matcher.group (4));
                final double blue = Double.parseDouble (matcher.group (6));
                this.model.getCurrentTrackBank ().setTrackColor (trackIndex, red, green, blue);
                break;

            default:
                this.host.println ("Unhandled Track Parameter: " + p);
                break;
        }
    }


    private void parseSendValue (final int trackIndex, final int sendIndex, final LinkedList<String> parts, final Object value)
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        if (!(tb instanceof TrackBankProxy))
            return;

        final double numValue = value instanceof Number ? ((Number) value).doubleValue () : -1;

        final String p = parts.removeFirst ();
        switch (p)
        {
            case "volume":
                if (parts.isEmpty ())
                    ((TrackBankProxy) tb).setSend (trackIndex, sendIndex, numValue);
                else if ("indicate".equals (parts.get (0)))
                    ((TrackBankProxy) tb).setSendIndication (trackIndex, sendIndex, numValue > 0);
                break;

            default:
                this.host.println ("Unhandled Send Parameter value: " + p);
                break;
        }
    }


    private void parseDeviceValue (final CursorDeviceProxy cursorDevice, final LinkedList<String> parts, final Object value)
    {
        final int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        final String p = parts.removeFirst ();
        switch (p)
        {
            case "bypass":
                cursorDevice.toggleEnabledState ();
                break;

            case "window":
                cursorDevice.toggleWindowOpen ();
                break;

            case "indicate":
                switch (parts.removeFirst ())
                {
                    case "param":
                        for (int i = 0; i < cursorDevice.getNumParameters (); i++)
                            cursorDevice.indicateParameter (i, numValue > 0);
                        break;
                }
                break;

            case "param":
                final String part = parts.removeFirst ();
                try
                {
                    final int paramNo = Integer.parseInt (part);
                    this.parseFXParamValue (cursorDevice, paramNo - 1, parts, value);
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

            case "layer":
                try
                {
                    final int layerNo = Integer.parseInt (parts.get (0));
                    parts.removeFirst ();
                    this.parseDeviceLayerValue (cursorDevice, layerNo - 1, parts, value);
                }
                catch (final NumberFormatException ex)
                {
                    switch (parts.removeFirst ())
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
                            if ("+".equals (parts.get (0)))
                                cursorDevice.nextLayerOrDrumPadBank ();
                            else
                                cursorDevice.previousLayerOrDrumPadBank ();
                            break;
                    }
                }
                break;

            default:
                this.host.println ("Unhandled Device Parameter: " + p);
                break;
        }
    }


    private void parseBrowser (final LinkedList<String> parts)
    {
        final IBrowser browser = this.model.getBrowser ();

        final String p = parts.removeFirst ();
        switch (p)
        {
            case "preset":
                browser.browseForPresets ();
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
                int column = Integer.parseInt (parts.removeFirst ());
                if (column < 1 || column > 6)
                    return;
                column = column - 1;
                if (!browser.isActive ())
                    return;
                if ("+".equals (parts.removeFirst ()))
                    browser.selectNextFilterItem (column);
                else
                    browser.selectPreviousFilterItem (column);
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
                this.host.println ("Unhandled Browser Command: " + p);
                break;
        }
    }


    private void parseDeviceLayerValue (final CursorDeviceProxy cursorDevice, final int layer, final LinkedList<String> parts, final Object value)
    {
        final String p = parts.removeFirst ();
        final int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        switch (p)
        {
            case "select":
                cursorDevice.selectLayer (layer);
                break;

            case "volume":
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
                this.host.println ("Unhandled Device Layer Parameter: " + p);
                break;
        }
    }


    private void parseFXParamValue (final CursorDeviceProxy cursorDevice, final int fxparamIndex, final LinkedList<String> parts, final Object value)
    {
        final int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        switch (parts.get (0))
        {
            case "value":
                if (parts.size () == 1 && value != null)
                    cursorDevice.setParameter (fxparamIndex, numValue);
                break;

            case "indicate":
                if (parts.size () == 1 && value != null)
                    cursorDevice.indicateParameter (fxparamIndex, numValue > 0);
                break;

            default:
                this.host.println ("Unhandled FX Parameter value:" + parts.get (0));
                break;
        }
    }


    private void parseMidi (final LinkedList<String> parts, final Object value)
    {
        int numValue = value instanceof Number ? ((Number) value).intValue () : -1;
        final String path2 = parts.removeFirst ();
        int midiChannel;
        try
        {
            midiChannel = Integer.parseInt (path2);
        }
        catch (final NumberFormatException ex)
        {
            switch (path2)
            {
                case "velocity":
                    this.configuration.setAccentEnabled (numValue > 0);
                    if (numValue > 0)
                        this.configuration.setAccentValue (numValue);
                    break;

                default:
                    this.host.println ("Unhandled Midi Parameter:" + path2);
                    break;
            }
            return;
        }

        final String p = parts.removeFirst ();
        switch (p)
        {
            case "note":
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
                final int cc = Integer.parseInt (parts.removeFirst ());
                this.noteInput.sendRawMidiEvent (0xB0 + midiChannel, cc, numValue);
                break;

            case "aftertouch":
                final int note = Integer.parseInt (parts.removeFirst ());
                if (numValue > 0)
                    numValue = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : numValue;
                this.noteInput.sendRawMidiEvent (0xA0 + midiChannel, this.model.getKeyTranslationMatrix ()[note], numValue);
                break;

            case "pitchbend":
                this.noteInput.sendRawMidiEvent (0xE0 + midiChannel, 0, numValue);
                break;

            default:
                this.host.println ("Unhandled Midi Parameter:" + p);
                break;
        }
    }


    private void selectTrack (final int index)
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        tb.select (index);
        tb.makeVisible (index);
    }
}