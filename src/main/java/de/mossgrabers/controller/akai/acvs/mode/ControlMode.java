// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.mode;

import de.mossgrabers.controller.akai.acvs.ACVSConfiguration;
import de.mossgrabers.controller.akai.acvs.ACVSDevice;
import de.mossgrabers.controller.akai.acvs.controller.ACVSColorManager;
import de.mossgrabers.controller.akai.acvs.controller.ACVSControlSurface;
import de.mossgrabers.controller.akai.acvs.controller.ACVSDisplay;
import de.mossgrabers.controller.akai.acvs.controller.ScreenItem;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.LaunchQuantization;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * There are no real modes in the ACVS protocol which can be controlled. Therefore, everything is in
 * one mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlMode extends AbstractParameterMode<ACVSControlSurface, ACVSConfiguration, IChannel>
{
    private static final Map<Modes, Integer> MODE_COLORS = new EnumMap<> (Modes.class);
    static
    {
        MODE_COLORS.put (Modes.MUTE, Integer.valueOf (ACVSColorManager.COLOR_ORANGE));
        MODE_COLORS.put (Modes.SOLO, Integer.valueOf (ACVSColorManager.COLOR_BLUE));
        MODE_COLORS.put (Modes.REC_ARM, Integer.valueOf (ACVSColorManager.COLOR_RED));
        MODE_COLORS.put (Modes.STOP_CLIP, Integer.valueOf (ACVSColorManager.COLOR_GREEN));
        MODE_COLORS.put (Modes.CROSSFADE_MODE_A, Integer.valueOf (ACVSColorManager.COLOR_LIGHT_ORANGE));
        MODE_COLORS.put (Modes.CROSSFADE_MODE_B, Integer.valueOf (ACVSColorManager.COLOR_RED));
    }

    private int currentMaxScene = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ControlMode (final ACVSControlSurface surface, final IModel model)
    {
        super ("Control", surface, model);

        final ACVSConfiguration configuration = this.surface.getConfiguration ();
        if (configuration.isActiveACVSDevice (ACVSDevice.FORCE) || configuration.isActiveACVSDevice (ACVSDevice.MPC_X))
        {
            final List<ContinuousID> knobs = ContinuousID.createSequentialList (ContinuousID.VOLUME_KNOB1, 8);
            knobs.addAll (ContinuousID.createSequentialList (ContinuousID.PARAM_KNOB1, 8));
            this.setControls (knobs);

            final VolumeParameterProvider provider1 = new VolumeParameterProvider (model.getTrackBank ());
            final BankParameterProvider provider2 = new BankParameterProvider (model.getCursorDevice ().getParameterBank ());
            this.setParameterProvider (new CombinedParameterProvider (provider1, provider2));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ACVSDisplay d = (ACVSDisplay) this.surface.getDisplay ();
        final ACVSConfiguration configuration = this.surface.getConfiguration ();
        final boolean isForce = configuration.isActiveACVSDevice (ACVSDevice.FORCE);
        final boolean isMPC = !isForce;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ICursorDevice device = this.model.getCursorDevice ();

        final int maxScene = calcMaxScene (this.model.getSceneBank ());
        d.setScreenItem (ScreenItem.TRACK_NUMBER_OF_SCENES, maxScene);

        // Maximum number of sends
        d.setScreenItem (ScreenItem.TRACK_SENDS_NO, calcNumberOfSends (tb));

        for (int i = 0; i < 8; i++)
        {
            // Send track data
            final ITrack track = tb.getItem (i);
            sendTrackData (d, i, track);
            this.sendClipData (d, isMPC, i, track.getSlotBank ());
        }

        this.sendSceneData (d, isMPC);

        // Set device data
        final boolean exists = device.doesExist ();
        final Optional<String> selectedPage = device.getParameterPageBank ().getSelectedItem ();
        d.setRow (ACVSDisplay.ITEM_ID_DEVICE_BANK_NAME, exists ? selectedPage.orElse ("") : "");
        d.setRow (ACVSDisplay.ITEM_ID_DEVICE_NAME, exists ? device.getName () : "");
        d.setScreenItem (ScreenItem.DEVICE_COUNT, device.doesExist () ? device.getDeviceBank ().getItemCount () : 0);
        final int devicePosition = device.getPosition ();
        d.setScreenItem (ScreenItem.CURRENT_DEVICE_INDEX, devicePosition >= 0 ? devicePosition : 0);
        d.setScreenItem (ScreenItem.CURRENT_DEVICE_ACTIVE, device.isEnabled () ? 127 : 0);

        final IParameterBank parameterBank = device.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (i);
            d.setRow (ACVSDisplay.ITEM_ID_DEVICE_PARAMETER_NAME_FIRST + i, param.getName ());
            d.setRow (ACVSDisplay.ITEM_ID_DEVICE_PARAMETER_VALUE_FIRST + i, param.getDisplayedValue ());
            d.setScreenItem (ScreenItem.get (ScreenItem.DEVICE_PARAM1_ENABLED, i), param.doesExist () ? 127 : 0);
            d.setScreenItem (ScreenItem.get (ScreenItem.DEVICE_PARAM1_VALUE, i), param.getValue ());

            // Labels for Q-Link knobs

            // Q-Link rows 3 + 4 are device parameters
            d.setScreenItem (ScreenItem.get (ScreenItem.KNOBSTYLE1_COLOR, 8 + i), param.doesExist () ? 1 : 0);
            d.setScreenItem (ScreenItem.get (ScreenItem.KNOB_VALUE1, 8 + i), param.getValue ());
            d.setRow (ACVSDisplay.ITEM_ID_DEVICE_PARAM_NAME1 + 8 + i, param.getName ());
            d.setRow (ACVSDisplay.ITEM_ID_DEVICE_PARAM_VALUE1 + 8 + i, param.getDisplayedValue ());

            // Q-Link rows 1 + 2 are track levels
            final ITrack track = tb.getItem (i);
            d.setScreenItem (ScreenItem.get (ScreenItem.KNOBSTYLE1_COLOR, i), track.doesExist () ? 1 : 0);
            d.setScreenItem (ScreenItem.get (ScreenItem.KNOB_VALUE1, i), track.getVolume ());
            d.setRow (ACVSDisplay.ITEM_ID_DEVICE_PARAM_NAME1 + i, track.getName ());
            d.setRow (ACVSDisplay.ITEM_ID_DEVICE_PARAM_VALUE1 + i, track.getVolumeStr ());
        }

        // Set transport data
        final ITransport transport = this.model.getTransport ();
        d.setRow (ACVSDisplay.ITEM_ID_TEMPO, transport.getTempoParameter ().getDisplayedValue ());
        d.setRow (ACVSDisplay.ITEM_ID_ARRANGEMENT_POSITION, transport.getBeatText ().split (":")[0].replace ('.', ':'));
        d.setRow (ACVSDisplay.ITEM_ID_LOOP_START, transport.getLoopStartBeatText ().split (":")[0].replace ('.', ':'));
        d.setRow (ACVSDisplay.ITEM_ID_LOOP_LENGTH, transport.getLoopLengthBeatText ().split (":")[0].replace ('.', ':'));

        this.sendAdditionalMPCParameters (d);
        if (!isMPC)
            this.sendAdditionalForceParameters (d);

        d.allDone ();

        if (this.currentMaxScene != maxScene)
        {
            this.currentMaxScene = maxScene;
            this.surface.forceFlush ();
        }
    }


    /**
     * Send the track data.
     *
     * @param d The display
     * @param trackIndex The index of the track to which the clips belong
     * @param track The track
     */
    private static void sendTrackData (final ACVSDisplay d, final int trackIndex, final ITrack track)
    {
        d.setRow (ACVSDisplay.ITEM_ID_TRACK_HEADER_FIRST + trackIndex, track.getName ());
        d.setColor (ACVSDisplay.ITEM_ID_TRACK_HEADER_FIRST + trackIndex, track.getColor ());
        d.setRow (ACVSDisplay.ITEM_ID_TRACK_FADER_LEVEL_FIRST + trackIndex, track.getVolumeStr ());

        // ITEM_ID_TRACK_PEAK_LEVEL_FIRST - not showing up

        d.setRow (ACVSDisplay.ITEM_ID_TRACK_PAN_FIRST + trackIndex, track.getPanStr ());

        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_SELECT, trackIndex), track.isSelected () ? 127 : 0);
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_TYPE, trackIndex), getTrackType (track));
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_SOLO, trackIndex), track.isSolo () ? 127 : 0);
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_MUTE, trackIndex), track.isMute () ? 127 : 0);
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_SOLOMUTE, trackIndex), track.isMutedBySolo () ? 127 : 0);

        // TRACK1_CUE - not supported

        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_CROSSFADER, trackIndex), getCrossfade (track.getCrossfadeParameter ().getValue ()));
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_REC_ARM, trackIndex), track.isRecArm () ? 127 : 0);
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_LEVEL, trackIndex), track.getVolume ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_PAN, trackIndex), track.getPan ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_VU_LEFT, trackIndex), track.getVuLeft ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_VU_RIGHT, trackIndex), track.getVuRight ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_VU_PEAK_LEFT, trackIndex), track.getVuPeakLeft ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_VU_PEAK_RIGHT, trackIndex), track.getVuPeakRight ());

        final ISendBank sendBank = track.getSendBank ();
        final ISend send1 = sendBank.getItem (0);
        final ISend send2 = sendBank.getItem (1);
        final ISend send3 = sendBank.getItem (2);
        final ISend send4 = sendBank.getItem (3);
        d.setRow (ACVSDisplay.ITEM_ID_TRACK_SEND1_FIRST + trackIndex, send1.getName ());
        d.setRow (ACVSDisplay.ITEM_ID_TRACK_SEND2_FIRST + trackIndex, send2.getName ());
        d.setRow (ACVSDisplay.ITEM_ID_TRACK_SEND3_FIRST + trackIndex, send3.getName ());
        d.setRow (ACVSDisplay.ITEM_ID_TRACK_SEND4_FIRST + trackIndex, send4.getName ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_SEND1_LEVEL, trackIndex), send1.getValue ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_SEND2_LEVEL, trackIndex), send2.getValue ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_SEND3_LEVEL, trackIndex), send3.getValue ());
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_SEND4_LEVEL, trackIndex), send4.getValue ());
    }


    /**
     * Send clip data.
     *
     * @param d The display
     * @param isMPC Is it MPC or Force?
     * @param trackIndex The index of the track to which the clips belong
     * @param slotBank The bank containing the clips
     */
    private void sendClipData (final ACVSDisplay d, final boolean isMPC, final int trackIndex, final ISlotBank slotBank)
    {
        // Clips on the screen
        for (int clipIndex = 0; clipIndex < 8; clipIndex++)
        {
            final ISlot slot = slotBank.getItem (clipIndex);
            final int position = clipIndex * 8 + trackIndex;

            final int slotState = convertSlotState (slot);

            d.setRow (ACVSDisplay.ITEM_ID_CLIPS_FIRST + position, slot.getName ());
            d.setScreenItem (ScreenItem.get (ScreenItem.CLIP1_STATE, position), slotState);

            // Clip colors can alternatively set with CC from the color palette:
            // <pre>d.setScreenItem (ScreenItem.get (ScreenItem.CLIP1_COLOR, position),
            // slotColor);</pre>
            d.setColor (ACVSDisplay.ITEM_ID_CLIPS_FIRST + position, slot.getColor ());
        }

        // Progress currently not supported in the API
        d.setScreenItem (ScreenItem.get (ScreenItem.TRACK1_CLIP_PROGRESS, trackIndex), 0);

        // Set pad colors and state representing clips
        if (!this.surface.getConfiguration ().isLaunchClips ())
            return;

        for (int clipIndex = 0; clipIndex < 8; clipIndex++)
        {
            final ISlot slot = slotBank.getItem (clipIndex);
            final int position = clipIndex * 8 + trackIndex;
            final int slotState = convertSlotState (slot);
            final int slotColor = this.convertSlotStateToColor (slot);

            if (isMPC)
            {
                d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_STATE, position), slotState);
                d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_COLOR, position), slotColor);
            }
            else
            {
                d.setScreenItem (ScreenItem.get (ScreenItem.FORCE_PAD1_STATE, position), slotState);
                d.setScreenItem (ScreenItem.get (ScreenItem.FORCE_PAD1_COLOR, position), slotColor);
            }
        }
    }


    /**
     * Set pad colors and state representing scenes.
     *
     * @param d The display
     * @param isMPC Is it MPC or Force?
     */
    private void sendSceneData (final ACVSDisplay d, final boolean isMPC)
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();

        for (int sceneIndex = 0; sceneIndex < 8; sceneIndex++)
        {
            // Scenes on the screen
            final IScene scene = sceneBank.getItem (sceneIndex);
            d.setRow (ACVSDisplay.ITEM_ID_SCENES_FIRST + sceneIndex, scene.getName ());
            d.setColor (ACVSDisplay.ITEM_ID_SCENES_FIRST + sceneIndex, scene.getColor ());
            d.setScreenItem (ScreenItem.get (ScreenItem.SCENE1_SELECT, sceneIndex), scene.isSelected () ? 127 : 0);
        }

        if (isMPC)
        {
            // Scene on pads
            if (this.surface.getConfiguration ().isLaunchClips ())
                return;

            for (int sceneIndex = 0; sceneIndex < 8; sceneIndex++)
            {
                final IScene scene = sceneBank.getItem (sceneIndex);
                final int sceneColor = scene.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorID (scene.getColor ())) : ACVSColorManager.COLOR_BLACK;
                final int offset = sceneIndex < 4 ? 0 : 4;
                d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_STATE, offset + sceneIndex), 2);
                d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_COLOR, offset + sceneIndex), sceneColor);
            }

            for (int padIndex = 0; padIndex < 4; padIndex++)
            {
                d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_STATE, 16 + padIndex), 0);
                d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_COLOR, 16 + padIndex), ACVSColorManager.COLOR_BLACK);
                d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_STATE, 24 + padIndex), 0);
                d.setScreenItem (ScreenItem.get (ScreenItem.MPC_PAD1_COLOR, 24 + padIndex), ACVSColorManager.COLOR_BLACK);
            }
            return;
        }

        for (int sceneIndex = 0; sceneIndex < 8; sceneIndex++)
        {
            final IScene scene = sceneBank.getItem (sceneIndex);
            int color = 0;
            if (scene.doesExist ())
                color = scene.isSelected () ? 1 : 2;
            d.setScreenItem (ScreenItem.get (ScreenItem.FORCE_SCENE1, sceneIndex), color);
        }
    }


    /**
     * Send additional parameters specific to the MPC models.
     *
     * @param d The display
     */
    private void sendAdditionalMPCParameters (final ACVSDisplay d)
    {
        final ITransport transport = this.model.getTransport ();
        final IApplication application = this.model.getApplication ();
        final IArranger arranger = this.model.getArranger ();
        final ICursorDevice device = this.model.getCursorDevice ();

        d.setScreenItem (ScreenItem.MPC_METRONOME, transport.isMetronomeOn () ? 127 : 0);

        // ScreenItem.MPC_CAPTURE_MIDI - not supported
        // ScreenItem.MPC_ABLETON_LINK - not supported

        final boolean isOverdub = this.surface.isShiftPressed () ? transport.isLauncherOverdub () : transport.isArrangerOverdub ();
        d.setScreenItem (ScreenItem.MPC_ARRANGE_OVERDUB, isOverdub ? 127 : 0);
        final boolean isAutomation = this.surface.isShiftPressed () ? transport.isWritingClipLauncherAutomation () : transport.isWritingArrangerAutomation ();
        d.setScreenItem (ScreenItem.MPC_ARRANGER_AUTOMATION_ARM, isAutomation ? 127 : 0);
        d.setScreenItem (ScreenItem.MPC_LOOP_SWITCH, transport.isLoop () ? 127 : 0);
        d.setScreenItem (ScreenItem.MPC_LAUNCH_QUANTIZE, convertLaunchQuantization (transport.getDefaultLaunchQuantization ()));
        d.setScreenItem (ScreenItem.MPC_ARRANGEMENT_SESSION, application.isArrangeLayout () ? 1 : 0);
        d.setScreenItem (ScreenItem.MPC_FOLLOW, arranger.isPlaybackFollowEnabled () ? 127 : 0);

        // ScreenItem.MPC_CLIP_DEVICE_VIEW - not supported

        d.setScreenItem (ScreenItem.MPC_DEVICE_LOCK, device.isPinned () ? 127 : 0);
        d.setScreenItem (ScreenItem.MPC_DETAIL_VIEW, device.isExpanded () ? 127 : 0);
        d.setScreenItem (ScreenItem.MPC_QUANTIZE_INTERVAL, Math.max (0, Math.min (7, 10 - this.surface.getConfiguration ().getQuantizeAmount () / 10)));
        d.setScreenItem (ScreenItem.MPC_CLIP_SCENE_LAUNCH, this.surface.getConfiguration ().isLaunchClips () ? 0 : 1);
    }


    /**
     * Send additional parameters specific to the Force model.
     *
     * @param d The display
     */
    private void sendAdditionalForceParameters (final ACVSDisplay d)
    {
        final Modes activeMode = this.surface.getTrackModeManager ().getActiveID ();
        final int modeColor = MODE_COLORS.getOrDefault (activeMode, Integer.valueOf (ACVSColorManager.COLOR_BLACK)).intValue ();

        for (int trackIndex = 0; trackIndex < 8; trackIndex++)
        {
            final ITrack track = this.model.getTrackBank ().getItem (trackIndex);

            int color;
            if (this.surface.isShiftPressed ())
            {
                if (LaunchQuantization.values ()[trackIndex] == this.model.getTransport ().getDefaultLaunchQuantization ())
                    color = ACVSColorManager.COLOR_SILVER;
                else
                    color = ACVSColorManager.COLOR_BLACK;
            }
            else
            {
                if (!track.doesExist ())
                    color = ACVSColorManager.COLOR_BLACK;
                else
                    color = track.isSelected () ? ACVSColorManager.COLOR_SILVER : this.colorManager.getColorIndex (DAWColor.getColorID (track.getColor ()));
            }

            d.setScreenItem (ScreenItem.get (ScreenItem.FORCE_TRACK1_COLOR, trackIndex), color);

            switch (activeMode)
            {
                case CROSSFADE_MODE_A:
                    final double value1 = this.model.getValueChanger ().toNormalizedValue (track.getCrossfadeParameter ().getValue ());
                    color = value1 < 0.1 ? modeColor : ACVSColorManager.COLOR_BLACK;
                    break;
                case CROSSFADE_MODE_B:
                    final double value2 = this.model.getValueChanger ().toNormalizedValue (track.getCrossfadeParameter ().getValue ());
                    color = value2 > 0.9 ? modeColor : ACVSColorManager.COLOR_BLACK;
                    break;
                case MUTE:
                    color = track.isMute () ? modeColor : ACVSColorManager.COLOR_BLACK;
                    break;
                case SOLO:
                    color = track.isSolo () ? modeColor : ACVSColorManager.COLOR_BLACK;
                    break;
                case REC_ARM:
                    color = track.isRecArm () ? modeColor : ACVSColorManager.COLOR_BLACK;
                    break;
                case STOP_CLIP:
                default:
                    color = modeColor;
                    break;
            }
            d.setScreenItem (ScreenItem.get (ScreenItem.FORCE_TRACK1_ASSIGN, trackIndex), color);
        }
    }


    /**
     * Convert the crossfade constant to the matching ACVS index.
     *
     * @param value The crossfade value
     * @return The ACVS crossfade value
     */
    private static int getCrossfade (final int value)
    {
        // A
        if (value == 0)
            return 1;

        // B
        if (value == 127)
            return 2;

        return 0;
    }


    /**
     * Convert the launch quantization constants to the matching ACVS index.
     *
     * @param launchQuantization The launch quantization constant
     * @return The index
     */
    private static int convertLaunchQuantization (final LaunchQuantization launchQuantization)
    {
        switch (launchQuantization)
        {
            default:
            case RES_NONE:
                return 0;
            case RES_1_16:
                return 11;
            case RES_1_8:
                return 9;
            case RES_1_4:
                return 7;
            case RES_1_2:
                return 5;
            case RES_1:
                return 4;
            case RES_2:
                return 3;
            case RES_4:
                return 2;
            case RES_8:
                return 1;
        }
    }


    /**
     * Get the track type ID.
     *
     * @param track The track
     * @return The ID
     */
    private static int getTrackType (final ITrack track)
    {
        if (!track.doesExist ())
            return 0;

        switch (track.getType ())
        {
            case AUDIO:
                return 6;

            case INSTRUMENT:
                return track.hasDrumDevice () ? 2 : 4;

            case HYBRID:
                return 4;

            case GROUP, GROUP_OPEN:
                return 7;

            case EFFECT:
                return 8;

            case MASTER:
                return 9;

            default:
                return 0;
        }
    }


    /**
     * Convert the state of a slot to the matching ACVS index.
     *
     * @param slot The slot
     * @return The index
     */
    private static int convertSlotState (final ISlot slot)
    {
        if (!slot.doesExist ())
            return 0;

        if (!slot.hasContent ())
            return 1;

        if (slot.isStopQueued ())
            return slot.isRecording () ? 8 : 5;

        if (slot.isRecording ())
            return 7;

        if (slot.isRecordingQueued ())
            return 6;

        if (slot.isPlaying ())
            return 4;

        if (slot.isPlayingQueued ())
            return 3;

        return 2;
    }


    /**
     * Look up color indices from the pre-configured color palette.
     *
     * @param slot The slot for which to get the color index
     * @return The color index
     */
    private int convertSlotStateToColor (final ISlot slot)
    {
        if (!slot.doesExist () || !slot.hasContent ())
            return ACVSColorManager.COLOR_BLACK;

        final ColorEx color = slot.getColor ();
        return this.colorManager.getColorIndex (DAWColor.getColorID (color));
    }


    /**
     * Calculate the maximum number of sends.
     *
     * @param tb The track bank
     * @return The maximum number of sends
     */
    private static int calcNumberOfSends (final ITrackBank tb)
    {
        int numSends = 0;
        for (int i = 0; i < 4; i++)
        {
            if (!tb.canEditSend (i))
                break;
            numSends = i + 1;
        }
        return numSends;
    }


    /**
     * Calculate the maximum number of scenes in the page.
     *
     * @param sceneBank The scene bank
     * @return The maximum index + 1
     */
    private static int calcMaxScene (final ISceneBank sceneBank)
    {
        int maxScene = 0;
        for (int i = 0; i < 8; i++)
        {
            final IScene scene = sceneBank.getItem (i);
            if (scene.doesExist ())
                maxScene = i + 1;
        }
        return maxScene;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getTrackBank ().selectPreviousPage ();
        else
            this.model.getTrackBank ().scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getTrackBank ().selectNextPage ();
        else
            this.model.getTrackBank ().scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.model.getTrackBank ().canScrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.model.getTrackBank ().canScrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return this.model.getTrackBank ().canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return this.model.getTrackBank ().canScrollPageForwards ();
    }
}
