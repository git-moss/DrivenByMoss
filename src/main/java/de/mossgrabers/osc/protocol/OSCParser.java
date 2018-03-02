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
                    this.parseTrackValue (this.model.getCurrentTrackBank ().getTrack (trackNo - 1), oscParts, value);
                }
                catch (final NumberFormatException ex)
                {
                    this.parseTrackCommands (oscParts, value);
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
                final IChannelBank tb = this.model.getCurrentTrackBank ();
                final boolean isTrue = value != null && value instanceof Number && ((Number) value).intValue () > 0;
                switch (parts.removeFirst ())
                {
                    case "volume":
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
                            final int sendIndex = Integer.parseInt (parts.get (0));
                            for (int i = 0; i < tb.getNumTracks (); i++)
                                tb.getTrack (i).getSend (sendIndex - 1).setIndication (isTrue);
                        }
                        break;
                }
                break;
            }

            case "bank":
                switch (parts.removeFirst ())
                {
                    case "page":
                        final IChannelBank tb = this.model.getCurrentTrackBank ();
                        if ("+".equals (parts.removeFirst ()))
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
                        this.model.getCurrentTrackBank ().scrollTracksDown ();
                        break;

                    case "-":
                        this.model.getCurrentTrackBank ().scrollTracksUp ();
                        break;
                }
                break;

            case "+":
            {
                final IChannelBank tb = this.model.getCurrentTrackBank ();
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
                final IChannelBank tb = this.model.getCurrentTrackBank ();
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
                final IChannelBank tb = this.model.getCurrentTrackBank ();
                final IChannelBank tbOther = this.model.isEffectTrackBankActive () ? this.model.getTrackBank () : this.model.getEffectTrackBank ();
                final ITrack selectedTrack = tb.getSelectedTrack ();
                if (selectedTrack == null)
                    tb.getTrack (0).selectAndMakeVisible ();
                // Move the indication to the other bank
                for (int i = 0; i < tb.getNumTracks (); i++)
                {
                    final ITrack otherTrack = tbOther.getTrack (i);
                    otherTrack.setVolumeIndication (false);
                    otherTrack.setPanIndication (false);
                    final ITrack track = tb.getTrack (i);
                    track.setVolumeIndication (true);
                    track.setPanIndication (true);
                }
                break;
            }

            case "parent":
            {
                final IChannelBank tb = this.model.getCurrentTrackBank ();
                if (tb instanceof ITrackBank)
                    ((ITrackBank) tb).selectParent ();
                break;
            }

            default:
                this.host.println ("Unhandled Track Command: " + p);
                break;
        }
    }


    private void parseTrackValue (final ITrack track, final LinkedList<String> parts, final Object value)
    {
        final double numValue = value instanceof Number ? ((Number) value).doubleValue () : -1;
        final int intValue = value instanceof Number ? ((Number) value).intValue () : -1;
        String p = parts.removeFirst ();
        switch (p)
        {
            case "activated":
                track.setIsActivated (intValue > 0);
                break;

            case "crossfadeMode":
                if (numValue == 1)
                    track.setCrossfadeMode (parts.removeFirst ());
                break;

            case "select":
                if (intValue > 0)
                    track.selectAndMakeVisible ();
                break;

            case "volume":
                if (parts.isEmpty ())
                    track.setVolume (numValue);
                else if ("indicate".equals (parts.get (0)))
                    track.setVolumeIndication (numValue > 0);
                break;

            case "pan":
                if (parts.isEmpty ())
                    track.setPan (numValue);
                else if ("indicate".equals (parts.get (0)))
                    track.setPanIndication (numValue > 0);
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
                final boolean isAuto = !parts.isEmpty () && "auto".equals (parts.get (0));
                if (numValue < 0)
                {
                    if (isAuto)
                        track.toggleAutoMonitor ();
                    else
                        track.toggleMonitor ();
                }
                else if (isAuto)
                    track.setAutoMonitor (numValue > 0);
                else
                    track.setMonitor (numValue > 0);
                break;

            case "send":
                final int sendNo = Integer.parseInt (parts.removeFirst ());
                this.parseSendValue (track, sendNo - 1, parts, value);
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
                            track.getSlot (clipNo - 1).select ();
                            break;
                        case "launch":
                            track.getSlot (clipNo - 1).launch ();
                            break;
                        case "record":
                            track.getSlot (clipNo - 1).record ();
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
                            track.getSlot (clipNo - 1).setColor (red, green, blue);
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
                            track.stop ();
                            break;
                        case "returntoarrangement":
                            track.returnToArrangement ();
                            break;
                        default:
                            this.host.println ("Unhandled clip command: " + p);
                            break;
                    }
                }
                break;

            case "enter":
                final IChannelBank tb = this.model.getCurrentTrackBank ();
                // TODO API extension required - selectChildren() should be available for Track as
                // well
                if (tb instanceof ITrackBank)
                    ((ITrackBank) tb).selectChildren ();
                break;

            case "color":
                final Matcher matcher = RGB_COLOR_PATTERN.matcher (value.toString ());
                if (!matcher.matches ())
                    return;
                final int count = matcher.groupCount ();
                if (count == 7)
                    track.setColor (Double.parseDouble (matcher.group (2)), Double.parseDouble (matcher.group (4)), Double.parseDouble (matcher.group (6)));
                break;

            default:
                this.host.println ("Unhandled Track Parameter: " + p);
                break;
        }
    }


    private void parseSendValue (final ITrack track, final int sendIndex, final LinkedList<String> parts, final Object value)
    {
        final double numValue = value instanceof Number ? ((Number) value).doubleValue () : -1;

        final String p = parts.removeFirst ();
        switch (p)
        {
            case "volume":
                if (parts.isEmpty ())
                    track.getSend (sendIndex).setValue (numValue);
                else if ("indicate".equals (parts.get (0)))
                    track.getSend (sendIndex).setIndication (numValue > 0);
                break;

            default:
                this.host.println ("Unhandled Send Parameter value: " + p);
                break;
        }
    }


    private void parseDeviceValue (final ICursorDevice cursorDevice, final LinkedList<String> parts, final Object value)
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


    private void parseDeviceLayerValue (final ICursorDevice cursorDevice, final int layer, final LinkedList<String> parts, final Object value)
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


    private void parseFXParamValue (final ICursorDevice cursorDevice, final int fxparamIndex, final LinkedList<String> parts, final Object value)
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

            case "reset":
                cursorDevice.resetParameter(fxparamIndex);

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
}