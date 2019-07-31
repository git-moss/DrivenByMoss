// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.protocol;

import de.mossgrabers.controller.osc.OSCColors;
import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ILayerBank;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.empty.EmptyLayer;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlWriter;
import de.mossgrabers.framework.osc.IOpenSoundControlClient;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.KeyManager;


/**
 * Writes the changed DAW stati as OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCWriter extends AbstractOpenSoundControlWriter
{
    private KeyManager keyManager;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param oscClient The OSC client to write to
     * @param keyManager The model
     * @param configuration The configuration
     */
    public OSCWriter (final IHost host, final IModel model, final IOpenSoundControlClient oscClient, final KeyManager keyManager, final OSCConfiguration configuration)
    {
        super (host, model, oscClient, configuration);
        this.keyManager = keyManager;
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        if (!this.isConnected ())
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
        this.sendOSC ("/click/preroll", trans.isPrerollMetronomeEnabled (), dump);
        this.sendOSC ("/preroll", trans.getPrerollAsBars (), dump);
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
        // Markers
        //
        final IMarkerBank markerBank = this.model.getMarkerBank ();
        for (int i = 0; i < markerBank.getPageSize (); i++)
            this.flushMarker ("/marker/" + (i + 1) + "/", markerBank.getItem (i), dump);

        //
        // Project
        //

        this.sendOSC ("/project/name", this.model.getProject ().getName (), dump);
        this.sendOSC ("/project/engine", app.isEngineActive (), dump);

        //
        // Master-/Track(-commands)
        //

        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        for (int i = 0; i < trackBank.getPageSize (); i++)
            this.flushTrack ("/track/" + (i + 1) + "/", trackBank.getItem (i), dump);
        this.flushTrack ("/master/", this.model.getMasterTrack (), dump);
        final ITrack selectedTrack = trackBank.getSelectedItem ();
        this.flushTrack ("/track/selected/", selectedTrack == null ? EmptyTrack.INSTANCE : selectedTrack, dump);
        this.sendOSC ("/track/toggleBank", this.model.isEffectTrackBankActive () ? 1 : 0, dump);

        //
        // Scenes
        //

        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < sceneBank.getPageSize (); i++)
            this.flushScene ("/scene/" + (i + 1) + "/", sceneBank.getItem (i), dump);

        //
        // Device / Primary Device
        //
        final ICursorDevice cd = this.model.getCursorDevice ();
        this.flushDevice ("/device/", cd, dump);
        if (cd.hasDrumPads ())
        {
            final IDrumPadBank drumPadBank = cd.getDrumPadBank ();
            for (int i = 0; i < drumPadBank.getPageSize (); i++)
                this.flushDeviceLayer ("/device/drumpad/" + (i + 1) + "/", drumPadBank.getItem (i), dump);
        }
        final ILayerBank layerBank = cd.getLayerBank ();
        for (int i = 0; i < layerBank.getPageSize (); i++)
            this.flushDeviceLayer ("/device/layer/" + (i + 1) + "/", layerBank.getItem (i), dump);
        final ILayer selectedLayer = layerBank.getSelectedItem ();
        this.flushDeviceLayer ("/device/layer/selected/", selectedLayer == null ? EmptyLayer.INSTANCE : selectedLayer, dump);

        this.flushDevice ("/primary/", this.model.getInstrumentDevice (), dump);

        //
        // Browser
        //

        this.flushBrowser ("/browser/", this.model.getBrowser (), dump);

        //
        // Notes
        //

        this.flushNotes ("/vkb_midi/note/", dump);

        this.flush ();
    }


    /**
     * Flush all data of a marker.
     *
     * @param markerAddress The start address for the marker
     * @param marker The marker
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushMarker (final String markerAddress, final IMarker marker, final boolean dump)
    {
        this.sendOSC (markerAddress + "exists", marker.doesExist (), dump);
        this.sendOSC (markerAddress + "name", marker.getName (), dump);
        final double [] color = marker.getColor ();
        this.sendOSCColor (markerAddress + "color", color[0], color[1], color[2], dump);
    }


    /**
     * Flush all data of a track.
     *
     * @param trackAddress The start address for the track
     * @param track The track
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushTrack (final String trackAddress, final ITrack track, final boolean dump)
    {
        this.sendOSC (trackAddress + "exists", track.doesExist (), dump);
        final ChannelType type = track.getType ();
        this.sendOSC (trackAddress + "type", type == null ? null : type.name ().toLowerCase (), dump);
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

        final ISendBank sendBank = track.getSendBank ();
        for (int i = 0; i < sendBank.getPageSize (); i++)
            this.flushParameterData (trackAddress + "send/" + (i + 1) + "/", sendBank.getItem (i), dump);

        final ISlotBank slotBank = track.getSlotBank ();
        for (int i = 0; i < slotBank.getPageSize (); i++)
        {
            final ISlot slot = slotBank.getItem (i);
            final String clipAddress = trackAddress + "clip/" + (i + 1) + "/";
            this.sendOSC (clipAddress + "name", slot.getName (), dump);
            this.sendOSC (clipAddress + "isSelected", slot.isSelected (), dump);
            this.sendOSC (clipAddress + "hasContent", slot.hasContent (), dump);
            this.sendOSC (clipAddress + "isPlaying", slot.isPlaying (), dump);
            this.sendOSC (clipAddress + "isRecording", slot.isRecording (), dump);
            this.sendOSC (clipAddress + "isPlayingQueued", slot.isPlayingQueued (), dump);
            this.sendOSC (clipAddress + "isRecordingQueued", slot.isRecordingQueued (), dump);
            this.sendOSC (clipAddress + "isStopQueued", slot.isStopQueued (), dump);

            final double [] color = slot.getColor ();
            this.sendOSCColor (clipAddress + "color", color[0], color[1], color[2], dump);
        }

        final double [] color = track.getColor ();
        this.sendOSCColor (trackAddress + "color", color[0], color[1], color[2], dump);

        final String crossfadeMode = track.getCrossfadeMode ();
        this.sendOSC (trackAddress + "crossfadeMode/A", "A".equals (crossfadeMode), dump);
        this.sendOSC (trackAddress + "crossfadeMode/B", "B".equals (crossfadeMode), dump);
        this.sendOSC (trackAddress + "crossfadeMode/AB", "AB".equals (crossfadeMode), dump);

        this.sendOSC (trackAddress + "vu", ((OSCConfiguration) this.configuration).isEnableVUMeters () ? track.getVu () : 0, dump);
    }


    /**
     * Flush all data of a scene.
     *
     * @param sceneAddress The start address for the scene
     * @param scene The scene
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushScene (final String sceneAddress, final IScene scene, final boolean dump)
    {
        this.sendOSC (sceneAddress + "exists", scene.doesExist (), dump);
        this.sendOSC (sceneAddress + "name", scene.getName (), dump);
        this.sendOSC (sceneAddress + "selected", scene.isSelected (), dump);
    }


    /**
     * Flush all data of a device.
     *
     * @param deviceAddress The start address for the device
     * @param device The device
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushDevice (final String deviceAddress, final ICursorDevice device, final boolean dump)
    {
        this.sendOSC (deviceAddress + "exists", device.doesExist (), dump);
        this.sendOSC (deviceAddress + "name", device.getName (), dump);
        this.sendOSC (deviceAddress + "bypass", !device.isEnabled (), dump);
        this.sendOSC (deviceAddress + "expand", device.isExpanded (), dump);
        this.sendOSC (deviceAddress + "window", device.isWindowOpen (), dump);
        final int positionInBank = device.getIndex ();
        final IDeviceBank deviceBank = device.getDeviceBank ();
        for (int i = 0; i < deviceBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            this.sendOSC (deviceAddress + "sibling/" + oneplus + "/name", deviceBank.getItem (i).getName (), dump);
            this.sendOSC (deviceAddress + "sibling/" + oneplus + "/selected", i == positionInBank, dump);

        }
        final IParameterBank parameterBank = device.getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            this.flushParameterData (deviceAddress + "param/" + oneplus + "/", parameterBank.getItem (i), dump);
        }

        final IParameterPageBank parameterPageBank = device.getParameterPageBank ();
        final int selectedParameterPage = parameterPageBank.getSelectedItemIndex ();
        for (int i = 0; i < parameterPageBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            this.sendOSC (deviceAddress + "page/" + oneplus + "/", parameterPageBank.getItem (i), dump);
            this.sendOSC (deviceAddress + "page/" + oneplus + "/selected", selectedParameterPage == i, dump);
        }
        this.sendOSC (deviceAddress + "page/selected/name", parameterPageBank.getSelectedItem (), dump);
    }


    /**
     * Flush all data of the browser.
     *
     * @param browserAddress The start address for the browser
     * @param browser The browser
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
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


    /**
     * Flush all data of a device layer.
     *
     * @param deviceAddress The start address for the device
     * @param channel The channel of the layer
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushDeviceLayer (final String deviceAddress, final IChannel channel, final boolean dump)
    {
        if (channel == null)
            return;

        this.sendOSC (deviceAddress + "exists", channel.doesExist (), dump);
        this.sendOSC (deviceAddress + "activated", channel.isActivated (), dump);
        this.sendOSC (deviceAddress + "selected", channel.isSelected (), dump);
        this.sendOSC (deviceAddress + "name", channel.getName (), dump);
        this.sendOSC (deviceAddress + "volumeStr", channel.getVolumeStr (), dump);
        this.sendOSC (deviceAddress + "volume", channel.getVolume (), dump);
        this.sendOSC (deviceAddress + "panStr", channel.getPanStr (), dump);
        this.sendOSC (deviceAddress + "pan", channel.getPan (), dump);
        this.sendOSC (deviceAddress + "mute", channel.isMute (), dump);
        this.sendOSC (deviceAddress + "solo", channel.isSolo (), dump);

        final ISendBank sendBank = channel.getSendBank ();
        for (int i = 0; i < sendBank.getPageSize (); i++)
            this.flushParameterData (deviceAddress + "send/" + (i + 1) + "/", sendBank.getItem (i), dump);

        if (((OSCConfiguration) this.configuration).isEnableVUMeters ())
            this.sendOSC (deviceAddress + "vu", channel.getVu (), dump);

        final double [] color = channel.getColor ();
        this.sendOSCColor (deviceAddress + "color", color[0], color[1], color[2], dump);
    }


    /**
     * Flush all data of a parameter.
     *
     * @param fxAddress The start address for the effect
     * @param fxParam The parameter
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushParameterData (final String fxAddress, final IParameter fxParam, final boolean dump)
    {
        final boolean isSend = fxParam instanceof ISend;

        this.sendOSC (fxAddress + "name", fxParam.getName (), dump);
        this.sendOSC (fxAddress + (isSend ? "volumeStr" : "valueStr"), fxParam.getDisplayedValue (), dump);
        this.sendOSC (fxAddress + (isSend ? "volume" : "value"), fxParam.getValue (), dump);
        this.sendOSC (fxAddress + "modulatedValue", fxParam.getModulatedValue (), dump);
    }


    /**
     * Flush all notes.
     *
     * @param noteAddress The start address for the note
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushNotes (final String noteAddress, final boolean dump)
    {
        for (int i = 0; i < 127; i++)
        {
            final double [] color = this.getNoteColor (i);
            this.sendOSCColor (noteAddress + i + "/color", color[0], color[1], color[2], dump);
        }
    }


    /**
     * Get the color for a note.
     *
     * @param note The note
     * @return The color
     */
    private double [] getNoteColor (final int note)
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        if (!isKeyboardEnabled)
            return OSCColors.getColor (Scales.SCALE_COLOR_OFF);

        if (!this.keyManager.isKeyPressed (note))
            return OSCColors.getColor (this.keyManager.getColor (note));

        final boolean isRecording = this.model.hasRecordingState ();
        return isRecording ? OSCColors.COLOR_RED : OSCColors.COLOR_GREEN;
    }
}
