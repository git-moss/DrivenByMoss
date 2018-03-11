// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc.protocol;

import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.EmptyTrackData;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.osc.OSCColors;
import de.mossgrabers.osc.OSCConfiguration;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Writes the changed DAW stati as OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCWriter
{
    private static final ITrack  EMPTY_TRACK = new EmptyTrackData ();

    private OSCModel             model;
    private Map<String, Object>  oldValues   = new HashMap<> ();
    private List<OscMessageData> messages    = new ArrayList<> ();
    private OSCConfiguration     configuration;
    private OscConnection        udpServer;


    /**
     * Constructor.
     *
     * @param model The model
     * @param configuration The configuration
     * @param oscModule The UDP server to send to
     */
    public OSCWriter (final OSCModel model, final OSCConfiguration configuration, final OscModule oscModule)
    {
        this.model = model;
        this.configuration = configuration;

        // TODO Fix required: Can only be called in init but needs to listen to host and port
        // changes
        this.udpServer = oscModule.connectToUdpServer (this.configuration.getSendHost (), this.configuration.getSendPort (), oscModule.createAddressSpace ());
    }


    /**
     * Flush out all values.
     *
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    public void flush (final boolean dump)
    {
        if (this.udpServer == null)
            return;

        //
        // Transport
        //

        final ITransport trans = this.model.getTransport ();
        this.sendOSC ("/play", trans.isPlaying (), dump);
        this.sendOSC ("/record", trans.isRecording (), dump);
        this.sendOSC ("/overdub", trans.isArrangerOverdub (), dump);
        this.sendOSC ("/overdub/launcher", trans.isLauncherOverdub (), dump);
        this.sendOSC ("/repeat", trans.isLoop (), dump);
        this.sendOSC ("/punchIn", trans.isPunchInEnabled (), dump);
        this.sendOSC ("/punchOut", trans.isPunchOutEnabled (), dump);
        this.sendOSC ("/click", trans.isMetronomeOn (), dump);
        this.sendOSC ("/click/ticks", trans.isMetronomeTicksOn (), dump);
        this.sendOSC ("/click/volume", trans.getMetronomeVolume (), dump);
        this.sendOSC ("/click/volumeStr", trans.getMetronomeVolumeStr (), dump);
        this.sendOSC ("/preroll", trans.getPreroll (), dump);
        this.sendOSC ("/tempo/raw", trans.getTempo (), dump);
        this.sendOSC ("/crossfade", trans.getCrossfade (), dump);
        this.sendOSC ("/autowrite", trans.isWritingArrangerAutomation (), dump);
        this.sendOSC ("/autowrite/launcher", trans.isWritingClipLauncherAutomation (), dump);
        this.sendOSC ("/automationWriteMode", trans.getAutomationWriteMode (), dump);
        this.sendOSC ("/time/str", trans.getPositionText (), dump);
        this.sendOSC ("/time/signature", trans.getNumerator () + " / " + trans.getDenominator (), dump);
        this.sendOSC ("/beat/str", trans.getBeatText (), dump);

        //
        // Frames
        //

        final IApplication app = this.model.getApplication ();
        this.sendOSC ("/layout", app.getPanelLayout ().toLowerCase (), dump);

        final IArranger arrange = this.model.getArranger ();
        this.sendOSC ("/arranger/cueMarkerVisibility", arrange.areCueMarkersVisible (), dump);
        this.sendOSC ("/arranger/playbackFollow", arrange.isPlaybackFollowEnabled (), dump);
        this.sendOSC ("/arranger/trackRowHeight", arrange.hasDoubleRowTrackHeight (), dump);
        this.sendOSC ("/arranger/clipLauncherSectionVisibility", arrange.isClipLauncherVisible (), dump);
        this.sendOSC ("/arranger/timeLineVisibility", arrange.isTimelineVisible (), dump);
        this.sendOSC ("/arranger/ioSectionVisibility", arrange.isIoSectionVisible (), dump);
        this.sendOSC ("/arranger/effectTracksVisibility", arrange.areEffectTracksVisible (), dump);

        final IMixer mix = this.model.getMixer ();
        this.sendOSC ("/mixer/clipLauncherSectionVisibility", mix.isClipLauncherSectionVisible (), dump);
        this.sendOSC ("/mixer/crossFadeSectionVisibility", mix.isCrossFadeSectionVisible (), dump);
        this.sendOSC ("/mixer/deviceSectionVisibility", mix.isDeviceSectionVisible (), dump);
        this.sendOSC ("/mixer/sendsSectionVisibility", mix.isSendSectionVisible (), dump);
        this.sendOSC ("/mixer/ioSectionVisibility", mix.isIoSectionVisible (), dump);
        this.sendOSC ("/mixer/meterSectionVisibility", mix.isMeterSectionVisible (), dump);

        //
        // Project
        //

        this.sendOSC ("/project/name", this.model.getProject ().getName (), dump);
        this.sendOSC ("/project/engine", app.isEngineActive (), dump);

        //
        // Master-/Track(-commands)
        //

        final IChannelBank trackBank = this.model.getCurrentTrackBank ();
        for (int i = 0; i < trackBank.getNumTracks (); i++)
            this.flushTrack ("/track/" + (i + 1) + "/", trackBank.getTrack (i), dump);
        this.flushTrack ("/master/", this.model.getMasterTrack (), dump);
        final ITrack selectedTrack = trackBank.getSelectedTrack ();
        this.flushTrack ("/track/selected/", selectedTrack == null ? EMPTY_TRACK : selectedTrack, dump);
        this.sendOSC ("/track/toggleBank", this.model.isEffectTrackBankActive () ? 1 : 0, dump);

        //
        // Scenes
        //

        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < sceneBank.getNumScenes (); i++)
            this.flushScene ("/scene/" + (i + 1) + "/", sceneBank.getScene (i), dump);

        //
        // Device / Primary Device
        //
        final ICursorDevice cd = this.model.getCursorDevice ();
        this.flushDevice ("/device/", cd, dump);
        if (cd.hasDrumPads ())
        {
            for (int i = 0; i < cd.getNumDrumPads (); i++)
                this.flushDeviceLayers ("/device/drumpad/" + (i + 1) + "/", cd.getLayerOrDrumPad (i), dump);
        }
        for (int i = 0; i < cd.getNumLayers (); i++)
            this.flushDeviceLayers ("/device/layer/" + (i + 1) + "/", cd.getLayerOrDrumPad (i), dump);
        this.flushDevice ("/primary/", this.model.getPrimaryDevice (), dump);

        //
        // Browser
        //

        this.flushBrowser ("/browser/", this.model.getBrowser (), dump);

        //
        // Notes
        //

        this.flushNotes ("/vkb_midi/note/", dump);

        // Send all collected messages

        try
        {
            int pos = 0;
            this.udpServer.startBundle ();
            for (final OscMessageData message: this.messages)
            {
                final String address = message.getAddress ();
                final Object [] values = message.getValues ();
                this.udpServer.sendMessage (address, values);
                pos++;
                if (pos > 1000)
                {
                    pos = 0;
                    this.udpServer.endBundle ();
                    this.udpServer.startBundle ();
                }
            }
            this.udpServer.endBundle ();
        }
        catch (final IOException ex)
        {
            this.model.getHost ().error ("Could not send UDP message.", ex);
        }

        this.messages.clear ();
    }


    private void flushTrack (final String trackAddress, final ITrack track, final boolean dump)
    {
        this.sendOSC (trackAddress + "exists", track.doesExist (), dump);
        this.sendOSC (trackAddress + "type", track.getType (), dump);
        this.sendOSC (trackAddress + "activated", track.isActivated (), dump);
        this.sendOSC (trackAddress + "selected", track.isSelected (), dump);
        this.sendOSC (trackAddress + "isGroup", track.isGroup (), dump);
        this.sendOSC (trackAddress + "name", track.getName (), dump);
        this.sendOSC (trackAddress + "volumeStr", track.getVolumeStr (), dump);
        this.sendOSC (trackAddress + "volume", track.getVolume (), dump);
        this.sendOSC (trackAddress + "panStr", track.getPanStr (), dump);
        this.sendOSC (trackAddress + "pan", track.getPan (), dump);
        this.sendOSC (trackAddress + "mute", track.isMute (), dump);
        this.sendOSC (trackAddress + "solo", track.isSolo (), dump);
        this.sendOSC (trackAddress + "recarm", track.isRecArm (), dump);
        this.sendOSC (trackAddress + "monitor", track.isMonitor (), dump);
        this.sendOSC (trackAddress + "autoMonitor", track.isAutoMonitor (), dump);
        this.sendOSC (trackAddress + "canHoldNotes", track.canHoldNotes (), dump);
        this.sendOSC (trackAddress + "canHoldAudioData", track.canHoldAudioData (), dump);
        this.sendOSC (trackAddress + "position", track.getPosition (), dump);

        for (int i = 0; i < track.getNumSends (); i++)
            this.flushParameterData (trackAddress + "send/" + (i + 1) + "/", track.getSend (i), dump);

        for (int i = 0; i < track.getNumSlots (); i++)
        {
            final ISlot slot = track.getSlot (i);
            final String clipAddress = trackAddress + "clip/" + (i + 1) + "/";
            this.sendOSC (clipAddress + "name", slot.getName (), dump);
            this.sendOSC (clipAddress + "isSelected", slot.isSelected (), dump);
            this.sendOSC (clipAddress + "hasContent", slot.hasContent (), dump);
            this.sendOSC (clipAddress + "isPlaying", slot.isPlaying (), dump);
            this.sendOSC (clipAddress + "isRecording", slot.isRecording (), dump);
            this.sendOSC (clipAddress + "isPlayingQueued", slot.isPlayingQueued (), dump);
            this.sendOSC (clipAddress + "isRecordingQueued", slot.isRecordingQueued (), dump);

            final double [] color = slot.getColor ();
            this.sendOSCColor (clipAddress + "color", color[0], color[1], color[2], dump);
        }

        final double [] color = track.getColor ();
        this.sendOSCColor (trackAddress + "color", color[0], color[1], color[2], dump);

        final String crossfadeMode = track.getCrossfadeMode ();
        this.sendOSC (trackAddress + "crossfadeMode/A", "A".equals (crossfadeMode), dump);
        this.sendOSC (trackAddress + "crossfadeMode/B", "B".equals (crossfadeMode), dump);
        this.sendOSC (trackAddress + "crossfadeMode/AB", "AB".equals (crossfadeMode), dump);

        if (this.configuration.isEnableVUMeters ())
            this.sendOSC (trackAddress + "vu", track.getVu (), dump);
    }


    private void flushScene (final String sceneAddress, final IScene scene, final boolean dump)
    {
        this.sendOSC (sceneAddress + "exists", scene.doesExist (), dump);
        this.sendOSC (sceneAddress + "name", scene.getName (), dump);
        this.sendOSC (sceneAddress + "selected", scene.isSelected (), dump);
    }


    private void flushDevice (final String deviceAddress, final ICursorDevice device, final boolean dump)
    {
        this.sendOSC (deviceAddress + "name", device.getName (), dump);
        this.sendOSC (deviceAddress + "bypass", !device.isEnabled (), dump);
        this.sendOSC (deviceAddress + "expand", device.isExpanded (), dump);
        this.sendOSC (deviceAddress + "window", device.isWindowOpen (), dump);
        final int positionInBank = device.getPositionInBank ();
        for (int i = 0; i < device.getNumDevices (); i++)
        {
            final int oneplus = i + 1;
            this.sendOSC (deviceAddress + "sibling/" + oneplus + "/name", device.getSiblingDeviceName (i), dump);
            this.sendOSC (deviceAddress + "sibling/" + oneplus + "/selected", i == positionInBank, dump);

        }
        for (int i = 0; i < device.getNumParameters (); i++)
        {
            final int oneplus = i + 1;
            this.flushParameterData (deviceAddress + "param/" + oneplus + "/", device.getFXParam (i), dump);
        }
        final String [] parameterPageNames = device.getParameterPageNames ();
        final int selectedParameterPage = device.getSelectedParameterPage ();

        final int page = Math.min (Math.max (0, selectedParameterPage), parameterPageNames.length - 1);
        final int start = page / 8 * 8;

        for (int i = 0; i < 8; i++)
        {
            final int index = start + i;
            final String pageName = index < parameterPageNames.length ? parameterPageNames[index] : "";

            final int oneplus = i + 1;
            this.sendOSC (deviceAddress + "page/" + oneplus + "/", pageName, dump);
            this.sendOSC (deviceAddress + "page/" + oneplus + "/selected", page == index, dump);
        }
    }


    private void flushBrowser (final String browserAddress, final IBrowser browser, final boolean dump)
    {
        this.sendOSC (browserAddress + "isActive", browser.isActive (), dump);
        this.sendOSC (browserAddress + "tab", browser.getSelectedContentType (), dump);

        IBrowserColumn column;
        // Filter Columns
        for (int i = 0; i < browser.getFilterColumnCount (); i++)
        {
            final String filterAddress = browserAddress + "filter/" + (i + 1) + "/";
            column = browser.getFilterColumn (i);
            this.sendOSC (filterAddress + "exists", column.doesExist (), dump);
            this.sendOSC (filterAddress + "name", column.getName (), dump);
            this.sendOSC (filterAddress + "wildcard", column.getWildcard (), dump);
            final IBrowserColumnItem [] items = column.getItems ();
            for (int j = 0; j < items.length; j++)
            {
                this.sendOSC (filterAddress + "item/" + (j + 1) + "/exists", items[j].doesExist (), dump);
                this.sendOSC (filterAddress + "item/" + (j + 1) + "/name", items[j].getName (), dump);
                this.sendOSC (filterAddress + "item/" + (j + 1) + "/hits", items[j].getHitCount (), dump);
                this.sendOSC (filterAddress + "item/" + (j + 1) + "/isSelected", items[j].isSelected (), dump);
            }
        }

        // Presets
        final String presetAddress = browserAddress + "result/";
        final IBrowserColumnItem [] items = browser.getResultColumnItems ();
        for (int i = 0; i < items.length; i++)
        {
            this.sendOSC (presetAddress + (i + 1) + "/exists", items[i].doesExist (), dump);
            this.sendOSC (presetAddress + (i + 1) + "/name", items[i].getName (), dump);
            this.sendOSC (presetAddress + (i + 1) + "/hits", items[i].getHitCount (), dump);
            this.sendOSC (presetAddress + (i + 1) + "/isSelected", items[i].isSelected (), dump);
        }
    }


    private void flushDeviceLayers (final String deviceAddress, final IChannel device, final boolean dump)
    {
        this.sendOSC (deviceAddress + "exists", device.doesExist (), dump);
        this.sendOSC (deviceAddress + "activated", device.isActivated (), dump);
        this.sendOSC (deviceAddress + "selected", device.isSelected (), dump);
        this.sendOSC (deviceAddress + "name", device.getName (), dump);
        this.sendOSC (deviceAddress + "volumeStr", device.getVolumeStr (), dump);
        this.sendOSC (deviceAddress + "volume", device.getVolume (), dump);
        this.sendOSC (deviceAddress + "panStr", device.getPanStr (), dump);
        this.sendOSC (deviceAddress + "pan", device.getPan (), dump);
        this.sendOSC (deviceAddress + "mute", device.isMute (), dump);
        this.sendOSC (deviceAddress + "solo", device.isSolo (), dump);

        for (int i = 0; i < device.getNumSends (); i++)
            this.flushParameterData (deviceAddress + "send/" + (i + 1) + "/", device.getSend (i), dump);

        if (this.configuration.isEnableVUMeters ())
            this.sendOSC (deviceAddress + "vu", device.getVu (), dump);

        final double [] color = device.getColor ();
        this.sendOSCColor (deviceAddress + "color", color[0], color[1], color[2], dump);
    }


    private void flushParameterData (final String fxAddress, final IParameter fxParam, final boolean dump)
    {
        final boolean isSend = fxParam instanceof ISend;

        this.sendOSC (fxAddress + "name", fxParam.getName (), dump);
        this.sendOSC (fxAddress + (isSend ? "volumeStr" : "valueStr"), fxParam.getDisplayedValue (), dump);
        this.sendOSC (fxAddress + (isSend ? "volume" : "value"), fxParam.getValue (), dump);
        this.sendOSC (fxAddress + "modulatedValue", fxParam.getModulatedValue (), dump);
    }


    private void flushNotes (final String noteAddress, final boolean dump)
    {
        for (int i = 0; i < 127; i++)
        {
            final double [] color = this.getNoteColor (i);
            this.sendOSCColor (noteAddress + i + "/color", color[0], color[1], color[2], dump);
        }
    }


    private double [] getNoteColor (final int note)
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        if (!isKeyboardEnabled)
            return OSCColors.getColor (Scales.SCALE_COLOR_OFF);

        if (!this.model.isKeyPressed (note))
        {
            final Scales scales = this.model.getScales ();
            final String color = scales.getColor (this.model.getKeyTranslationMatrix (), note);
            return OSCColors.getColor (color);
        }

        final boolean isRecording = this.model.hasRecordingState ();
        return isRecording ? OSCColors.COLOR_RED : OSCColors.COLOR_GREEN;
    }


    private void sendOSC (final String address, final boolean value, final boolean dump)
    {
        this.sendOSC (address, Boolean.valueOf (value), dump);
    }


    private void sendOSC (final String address, final double value, final boolean dump)
    {
        // Using float here since Double seems to be always received as 0 in Max.
        this.sendOSC (address, Float.valueOf ((float) value), dump);
    }


    private void sendOSC (final String address, final String value, final boolean dump)
    {
        this.sendOSC (address, (Object) StringUtils.fixASCII (value), dump);
    }


    private void sendOSC (final String address, final Object value, final boolean dump)
    {
        if (!dump)
        {
            final Object object = this.oldValues.get (address);
            if (object != null && object.equals (value) || object == null && value == null)
                return;
        }

        this.oldValues.put (address, value);
        this.messages.add (new OscMessageData (address, Collections.singletonList (convertBooleanToInt (value))));
    }


    private void sendOSCColor (final String address, final double red, final double green, final double blue, final boolean dump)
    {
        final int r = (int) Math.round (red * 255.0);
        final int g = (int) Math.round (green * 255.0);
        final int b = (int) Math.round (blue * 255.0);
        this.sendOSC (address, "rgb(" + r + "," + g + "," + b + ")", dump);
    }


    private static Object convertBooleanToInt (final Object value)
    {
        return value instanceof Boolean ? Integer.valueOf (((Boolean) value).booleanValue () ? 1 : 0) : value;
    }
}
