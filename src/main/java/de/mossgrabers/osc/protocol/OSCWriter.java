// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc.protocol;

import de.mossgrabers.framework.daw.ApplicationProxy;
import de.mossgrabers.framework.daw.ArrangerProxy;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.MixerProxy;
import de.mossgrabers.framework.daw.SceneBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.daw.data.BrowserColumnData;
import de.mossgrabers.framework.daw.data.BrowserColumnItemData;
import de.mossgrabers.framework.daw.data.ChannelData;
import de.mossgrabers.framework.daw.data.ParameterData;
import de.mossgrabers.framework.daw.data.SceneData;
import de.mossgrabers.framework.daw.data.SendData;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.osc.OSCColors;
import de.mossgrabers.osc.OSCConfiguration;

import com.bitwig.extension.controller.api.ControllerHost;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;

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
    private static final EmptyTrackData EMPTY_TRACK = new EmptyTrackData ();

    private OSCModel                    model;
    private Map<String, Object>         oldValues   = new HashMap<> ();
    private List<OSCPacket>             messages    = new ArrayList<> ();
    private OSCConfiguration            configuration;


    /**
     * Constructor.
     *
     * @param model
     * @param configuration
     */
    public OSCWriter (final OSCModel model, final OSCConfiguration configuration)
    {
        this.model = model;
        this.configuration = configuration;
    }


    /**
     * Flush out all values.
     *
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    public void flush (final boolean dump)
    {
        //
        // Transport
        //

        final TransportProxy trans = this.model.getTransport ();
        this.sendOSC ("/play", trans.isPlaying (), dump);
        this.sendOSC ("/record", trans.isRecording (), dump);
        this.sendOSC ("/overdub", trans.isArrangerOverdub (), dump);
        this.sendOSC ("/overdub/launcher", trans.isLauncherOverdub (), dump);
        this.sendOSC ("/repeat", trans.isLoop (), dump);
        this.sendOSC ("/punchIn", trans.isPunchInEnabled (), dump);
        this.sendOSC ("/punchOut", trans.isPunchOutEnabled (), dump);
        this.sendOSC ("/click", trans.isMetronomeOn (), dump);
        this.sendOSC ("/preroll", trans.getPreroll (), dump);
        this.sendOSC ("/tempo/raw", trans.getTempo (), dump);
        this.sendOSC ("/crossfade", trans.getCrossfade (), dump);
        this.sendOSC ("/autowrite", trans.isWritingArrangerAutomation (), dump);
        this.sendOSC ("/autowrite/launcher", trans.isWritingClipLauncherAutomation (), dump);
        this.sendOSC ("/automationWriteMode", trans.getAutomationWriteMode (), dump);
        this.sendOSC ("/position", trans.getPositionText (), dump);

        //
        // Frames
        //

        final ApplicationProxy app = this.model.getApplication ();
        this.sendOSC ("/layout", app.getPanelLayout ().toLowerCase (), dump);

        final ArrangerProxy arrange = this.model.getArranger ();
        this.sendOSC ("/arranger/cueMarkerVisibility", arrange.areCueMarkersVisible (), dump);
        this.sendOSC ("/arranger/playbackFollow", arrange.isPlaybackFollowEnabled (), dump);
        this.sendOSC ("/arranger/trackRowHeight", arrange.hasDoubleRowTrackHeight (), dump);
        this.sendOSC ("/arranger/clipLauncherSectionVisibility", arrange.isClipLauncherVisible (), dump);
        this.sendOSC ("/arranger/timeLineVisibility", arrange.isTimelineVisible (), dump);
        this.sendOSC ("/arranger/ioSectionVisibility", arrange.isIoSectionVisible (), dump);
        this.sendOSC ("/arranger/effectTracksVisibility", arrange.areEffectTracksVisible (), dump);

        final MixerProxy mix = this.model.getMixer ();
        this.sendOSC ("/mixer/clipLauncherSectionVisibility", mix.isClipLauncherSectionVisible (), dump);
        this.sendOSC ("/mixer/crossFadeSectionVisibility", mix.isCrossFadeSectionVisible (), dump);
        this.sendOSC ("/mixer/deviceSectionVisibility", mix.isDeviceSectionVisible (), dump);
        this.sendOSC ("/mixer/sendsSectionVisibility", mix.isSendSectionVisible (), dump);
        this.sendOSC ("/mixer/ioSectionVisibility", mix.isIoSectionVisible (), dump);
        this.sendOSC ("/mixer/meterSectionVisibility", mix.isMeterSectionVisible (), dump);

        //
        // Project
        //

        this.sendOSC ("/project/name", app.getProjectName (), dump);
        this.sendOSC ("/project/engine", app.isEngineActive (), dump);

        //
        // Master-/Track(-commands)
        //

        final TrackBankProxy trackBank = this.model.getTrackBank ();
        for (int i = 0; i < trackBank.getNumTracks (); i++)
            this.flushTrack ("/track/" + (i + 1) + "/", trackBank.getTrack (i), dump);
        this.flushTrack ("/master/", this.model.getMasterTrack (), dump);
        final TrackData selectedTrack = trackBank.getSelectedTrack ();
        this.flushTrack ("/track/selected/", selectedTrack == null ? EMPTY_TRACK : selectedTrack, dump);
        this.flushSendNames ("/send/", dump);

        //
        // Scenes
        //

        final SceneBankProxy sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < sceneBank.getNumScenes (); i++)
            this.flushScene ("/scene/" + (i + 1) + "/", sceneBank.getScene (i), dump);

        //
        // Device / Primary Device
        //
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        this.flushDevice ("/device/", cd, dump);
        for (int i = 0; i < cd.getNumDeviceLayers (); i++)
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
        int pos = 0;
        final int size = this.messages.size ();
        final ControllerHost host = this.model.getHost ();
        final String sendHost = this.configuration.getSendHost ();
        final int sendPort = this.configuration.getSendPort ();
        while (pos < size)
        {
            final int end = pos + 1000;
            final OSCBundle oscBundle = new OSCBundle (this.messages.subList (pos, Math.min (end, size)));
            final byte [] data = oscBundle.getByteArray ();
            host.sendDatagramPacket (sendHost, sendPort, data);
            pos += 1000;
        }
        this.messages.clear ();
    }


    private void flushTrack (final String trackAddress, final TrackData track, final boolean dump)
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

        final SendData [] sends = track.getSends ();
        for (int i = 0; i < sends.length; i++)
            this.flushParameterData (trackAddress + "send/" + (i + 1) + "/", sends[i], dump);

        final SlotData [] slots = track.getSlots ();
        for (int i = 0; i < slots.length; i++)
        {
            final String clipAddress = trackAddress + "clip/" + (i + 1) + "/";
            this.sendOSC (clipAddress + "name", slots[i].getName (), dump);
            this.sendOSC (clipAddress + "isSelected", slots[i].isSelected (), dump);
            this.sendOSC (clipAddress + "hasContent", slots[i].hasContent (), dump);
            this.sendOSC (clipAddress + "isPlaying", slots[i].isPlaying (), dump);
            this.sendOSC (clipAddress + "isRecording", slots[i].isRecording (), dump);
            this.sendOSC (clipAddress + "isPlayingQueued", slots[i].isPlayingQueued (), dump);
            this.sendOSC (clipAddress + "isRecordingQueued", slots[i].isRecordingQueued (), dump);

            final double [] color = slots[i].getColor ();
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


    private void flushSendNames (final String sendAddress, final boolean dump)
    {
        final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
        final boolean isFX = this.model.isEffectTrackBankActive ();
        for (int i = 0; i < fxTrackBank.getNumSends (); i++)
        {
            final TrackData fxTrack = fxTrackBank.getTrack (i);
            final boolean isEmpty = isFX || !fxTrack.doesExist ();
            this.sendOSC (sendAddress + (i + 1) + "/name", isEmpty ? "" : fxTrack.getName (), dump);
        }
    }


    private void flushScene (final String sceneAddress, final SceneData scene, final boolean dump)
    {
        this.sendOSC (sceneAddress + "exists", scene.doesExist (), dump);
        this.sendOSC (sceneAddress + "name", scene.getName (), dump);
        this.sendOSC (sceneAddress + "selected", scene.isSelected (), dump);
    }


    private void flushDevice (final String deviceAddress, final CursorDeviceProxy device, final boolean dump)
    {
        this.sendOSC (deviceAddress + "name", device.getName (), dump);
        this.sendOSC (deviceAddress + "bypass", !device.isEnabled (), dump);
        for (int i = 0; i < device.getNumParameters (); i++)
        {
            final int oneplus = i + 1;
            this.flushParameterData (deviceAddress + "param/" + oneplus + "/", device.getFXParam (i), dump);
        }
    }


    private void flushBrowser (final String browserAddress, final BrowserProxy browser, final boolean dump)
    {
        this.sendOSC (browserAddress + "isActive", browser.isActive (), dump);

        BrowserColumnData column;
        // Filter Columns
        for (int i = 0; i < browser.getFilterColumnCount (); i++)
        {
            final String filterAddress = browserAddress + "filter/" + (i + 1) + "/";
            column = browser.getFilterColumn (i);
            this.sendOSC (filterAddress + "exists", column.doesExist (), dump);
            this.sendOSC (filterAddress + "name", column.getName (), dump);
            this.sendOSC (filterAddress + "wildcard", column.getWildcard (), dump);
            final BrowserColumnItemData [] items = column.getItems ();
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
        final BrowserColumnItemData [] items = browser.getResultColumnItems ();
        for (int i = 0; i < items.length; i++)
        {
            this.sendOSC (presetAddress + (i + 1) + "/exists", items[i].doesExist (), dump);
            this.sendOSC (presetAddress + (i + 1) + "/name", items[i].getName (), dump);
            this.sendOSC (presetAddress + (i + 1) + "/hits", items[i].getHitCount (), dump);
            this.sendOSC (presetAddress + (i + 1) + "/isSelected", items[i].isSelected (), dump);
        }
    }


    private void flushDeviceLayers (final String deviceAddress, final ChannelData device, final boolean dump)
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

        final SendData [] sends = device.getSends ();
        for (int i = 0; i < sends.length; i++)
            this.flushParameterData (deviceAddress + "send/" + (i + 1) + "/", sends[i], dump);

        if (this.configuration.isEnableVUMeters ())
            this.sendOSC (deviceAddress + "vu", device.getVu (), dump);

        final double [] color = device.getColor ();
        this.sendOSCColor (deviceAddress + "color", color[0], color[1], color[2], dump);
    }


    private void flushParameterData (final String fxAddress, final ParameterData fxParam, final boolean dump)
    {
        this.sendOSC (fxAddress + "name", fxParam.getName (), dump);
        this.sendOSC (fxAddress + "valueStr", fxParam.getDisplayedValue (), dump);
        this.sendOSC (fxAddress + "value", fxParam.getValue (), dump);
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


    private void sendOSC (final String address, final Object value, final boolean dump)
    {
        if (!dump)
        {
            final Object object = this.oldValues.get (address);
            if (object != null && object.equals (value) || object == null && value == null)
                return;
        }

        this.oldValues.put (address, value);
        this.messages.add (new OSCMessage (address, Collections.singletonList (convertBooleanToInt (value))));
    }


    private void sendOSCColor (final String address, final double red, final double green, final double blue, final boolean dump)
    {
        this.sendOSC (address, "RGB(" + red + "," + green + "," + blue + ")", dump);
    }


    private static Object convertBooleanToInt (final Object value)
    {
        return value instanceof Boolean ? Integer.valueOf (((Boolean) value).booleanValue () ? 1 : 0) : value;
    }
}
