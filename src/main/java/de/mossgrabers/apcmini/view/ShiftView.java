// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.view;

import de.mossgrabers.apcmini.APCminiConfiguration;
import de.mossgrabers.apcmini.controller.APCminiColors;
import de.mossgrabers.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.apcmini.mode.Modes;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.trigger.PlayCommand;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.ViewManager;


/**
 * The Shift view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftView extends AbstractView<APCminiControlSurface, APCminiConfiguration> implements SceneView, APCminiView
{
    private static final int []                                    TRANSLATE =
    {
        0,
        2,
        4,
        6,
        1,
        3,
        5,
        -1,
        -1,
        10,
        8,
        -1,
        11,
        9,
        7,
        -1
    };

    final PlayCommand<APCminiControlSurface, APCminiConfiguration> playCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final APCminiControlSurface surface, final Model model)
    {
        super ("Shift", surface, model);

        this.scales = this.model.getScales ();
        this.playCommand = new PlayCommand<> (this.model, this.surface);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Draw the keyboard
        final int scaleOffset = this.scales.getScaleOffset ();
        // 0'C', 1'G', 2'D', 3'A', 4'E', 5'B', 6'F', 7'Bb', 8'Eb', 9'Ab', 10'Db', 11'Gb'
        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 7; i < 64; i++)
            padGrid.light (36 + i, APCminiColors.APC_COLOR_BLACK);
        padGrid.light (36 + 0, scaleOffset == 0 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 1, scaleOffset == 2 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 2, scaleOffset == 4 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 3, scaleOffset == 6 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 4, scaleOffset == 1 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 5, scaleOffset == 3 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 6, scaleOffset == 5 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 9, scaleOffset == 10 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_RED);
        padGrid.light (36 + 10, scaleOffset == 8 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_RED);
        padGrid.light (36 + 12, scaleOffset == 11 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_RED);
        padGrid.light (36 + 13, scaleOffset == 9 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_RED);
        padGrid.light (36 + 14, scaleOffset == 7 ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_RED);

        // Device Parameters up/down
        padGrid.light (36 + 24, APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 25, APCminiColors.APC_COLOR_YELLOW);
        // Device up/down
        padGrid.light (36 + 32, APCminiColors.APC_COLOR_GREEN);
        padGrid.light (36 + 33, APCminiColors.APC_COLOR_GREEN);

        // Change the scale
        padGrid.light (36 + 35, APCminiColors.APC_COLOR_RED);
        padGrid.light (36 + 36, APCminiColors.APC_COLOR_RED);
        padGrid.light (36 + 27, APCminiColors.APC_COLOR_GREEN);

        // Draw the view selection: Session, Note, Drum, Sequencer
        final Integer previousViewId = this.surface.getViewManager ().getPreviousViewId ();
        padGrid.light (36 + 56, previousViewId == Views.VIEW_SESSION ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 57, previousViewId == Views.VIEW_PLAY ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 58, previousViewId == Views.VIEW_DRUM ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 59, previousViewId == Views.VIEW_SEQUENCER ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 60, previousViewId == Views.VIEW_RAINDROPS ? APCminiColors.APC_COLOR_GREEN : APCminiColors.APC_COLOR_YELLOW);

        // Draw transport
        final TransportProxy transport = this.model.getTransport ();
        padGrid.light (36 + 63, transport.isPlaying () ? APCminiColors.APC_COLOR_GREEN_BLINK : APCminiColors.APC_COLOR_GREEN);
        padGrid.light (36 + 55, transport.isRecording () ? APCminiColors.APC_COLOR_RED_BLINK : APCminiColors.APC_COLOR_RED);
        padGrid.light (36 + 47, APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 39, APCminiColors.APC_COLOR_YELLOW);

        padGrid.light (36 + 62, APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 54, transport.isLauncherOverdub () ? APCminiColors.APC_COLOR_RED_BLINK : APCminiColors.APC_COLOR_RED);
        padGrid.light (36 + 46, APCminiColors.APC_COLOR_YELLOW);
        padGrid.light (36 + 38, APCminiColors.APC_COLOR_YELLOW);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        switch (note)
        {
            // Flip views
            case 56:
                this.surface.getViewManager ().setPreviousView (Views.VIEW_SESSION);
                this.surface.getDisplay ().notify ("Session");
                break;
            case 57:
                this.surface.getViewManager ().setPreviousView (Views.VIEW_PLAY);
                this.surface.getDisplay ().notify ("Play");
                break;
            case 58:
                this.surface.getViewManager ().setPreviousView (Views.VIEW_DRUM);
                this.surface.getDisplay ().notify ("Drum");
                break;
            case 59:
                this.surface.getViewManager ().setPreviousView (Views.VIEW_SEQUENCER);
                this.surface.getDisplay ().notify ("Sequencer");
                break;
            case 60:
                this.surface.getViewManager ().setPreviousView (Views.VIEW_RAINDROPS);
                this.surface.getDisplay ().notify ("Raindrops");
                break;

            // Last row transport
            case 63:
                this.playCommand.executeNormal (ButtonEvent.DOWN);
                this.surface.getDisplay ().notify ("Start/Stop");
                break;
            case 55:
                this.model.getTransport ().record ();
                this.surface.getDisplay ().notify ("Record");
                break;
            case 47:
                this.model.getTransport ().toggleLoop ();
                this.surface.getDisplay ().notify ("Toggle Loop");
                break;
            case 39:
                this.model.getTransport ().toggleMetronome ();
                this.surface.getDisplay ().notify ("Toggle Click");
                break;

            // Navigation
            case 62:
                this.onNew ();
                this.surface.getDisplay ().notify ("New clip");
                break;
            case 54:
                this.model.getTransport ().toggleLauncherOverdub ();
                this.surface.getDisplay ().notify ("Toggle Launcher Overdub");
                break;
            case 46:
                // We can use any cursor clip, e.g. the one of the drum view
                final DrumView view = (DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM);
                view.getClip ().quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
                this.surface.getDisplay ().notify ("Quantize");
                break;
            case 38:
                this.model.getApplication ().undo ();
                this.surface.getDisplay ().notify ("Undo");
                break;

            // Device Parameters up/down
            case 24:
                cursorDevice.previousParameterPage ();
                this.surface.getDisplay ().notify ("Bank: " + cursorDevice.getSelectedParameterPageName ());
                break;
            case 25:
                cursorDevice.nextParameterPage ();
                this.surface.getDisplay ().notify ("Bank: " + cursorDevice.getSelectedParameterPageName ());
                break;

            // Device up/down
            case 32:
                cursorDevice.selectPrevious ();
                this.surface.getDisplay ().notify ("Device: " + cursorDevice.getName ());
                break;
            case 33:
                cursorDevice.selectNext ();
                this.surface.getDisplay ().notify ("Device: " + cursorDevice.getName ());
                break;

            // Change the scale
            case 35:
                this.scales.prevScale ();
                final String name = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (name);
                this.surface.getDisplay ().notify (name);
                break;
            case 36:
                this.scales.nextScale ();
                final String name2 = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (name2);
                this.surface.getDisplay ().notify (name2);
                break;
            case 27:
                this.scales.toggleChromatic ();
                this.surface.getDisplay ().notify (this.scales.isChromatic () ? "Chromatc" : "In Key");
                break;

            // Scale Base note selection
            default:
                if (note > 15)
                    return;
                final int pos = TRANSLATE[note];
                if (pos == -1)
                    return;
                this.scales.setScaleOffset (pos);
                this.surface.getConfiguration ().setScaleBase (Scales.BASES[pos]);
                this.surface.getDisplay ().notify (Scales.BASES[pos]);
                this.surface.getViewManager ().getActiveView ().updateNoteMapping ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (index)
        {
            case 0:
                this.model.getCurrentTrackBank ().scrollScenesPageUp ();
                break;
            case 1:
                this.model.getCurrentTrackBank ().scrollScenesPageDown ();
                break;
            case 2:
                this.model.getCurrentTrackBank ().scrollTracksPageUp ();
                break;
            case 3:
                this.model.getCurrentTrackBank ().scrollTracksPageDown ();
                break;

            case 4:
                this.surface.getModeManager ().setActiveMode (Modes.MODE_VOLUME);
                this.surface.getConfiguration ().setFaderCtrl ("Volume");
                this.surface.getDisplay ().notify ("Volume");
                break;

            case 5:
                this.surface.getModeManager ().setActiveMode (Modes.MODE_PAN);
                this.surface.getConfiguration ().setFaderCtrl ("Pan");
                this.surface.getDisplay ().notify ("Pan");
                break;

            case 6:
                if (this.model.isEffectTrackBankActive ())
                    return;
                final ModeManager modeManager = this.surface.getModeManager ();
                Integer mode = Integer.valueOf (modeManager.getActiveModeId ().intValue () + 1);
                // Wrap
                if (!Modes.isSendMode (mode))
                    mode = Modes.MODE_SEND1;
                // Check if Send channel exists
                final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
                if (Modes.isSendMode (mode) && fxTrackBank != null && !fxTrackBank.getTrack (mode.intValue () - Modes.MODE_SEND1.intValue ()).doesExist ())
                    mode = Modes.MODE_SEND1;
                modeManager.setActiveMode (mode);
                final String name = "Send " + (mode.intValue () - Modes.MODE_SEND1.intValue () + 1);
                this.surface.getConfiguration ().setFaderCtrl (name);
                this.surface.getDisplay ().notify (name);
                break;

            case 7:
                if (this.surface.getModeManager ().isActiveMode (Modes.MODE_DEVICE))
                {
                    this.model.getBrowser ().browseForPresets ();
                    final ViewManager viewManager = this.surface.getViewManager ();
                    final Integer previousViewId = viewManager.getPreviousViewId ();
                    viewManager.setActiveView (Views.VIEW_BROWSER);
                    viewManager.setPreviousView (previousViewId);
                }
                else
                {
                    this.surface.getModeManager ().setActiveMode (Modes.MODE_DEVICE);
                    this.surface.getConfiguration ().setFaderCtrl ("Device");
                    this.surface.getDisplay ().notify ("Device");
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (index)
        {
            case 5:
                this.model.toggleCurrentTrackBank ();
                final boolean isEffectTrackBank = this.model.isEffectTrackBankActive ();
                if (isEffectTrackBank)
                {
                    // No Sends on effect tracks
                    final ModeManager modeManager = this.surface.getModeManager ();
                    if (Modes.isSendMode (modeManager.getActiveModeId ()))
                        modeManager.setActiveMode (Modes.MODE_VOLUME);
                }
                this.surface.getDisplay ().notify (isEffectTrackBank ? "Effect Tracks" : "Instrument/Audio Tracks");
                break;
            case 6:
                this.model.getCursorDevice ().toggleWindowOpen ();
                break;
            case 7:
                this.model.getCurrentTrackBank ().stop ();
                break;
            default:
                this.surface.setTrackState (index);
                this.surface.getConfiguration ().setSoftKeys (APCminiConfiguration.SOFT_KEYS_OPTIONS[index]);
                this.surface.getDisplay ().notify (APCminiConfiguration.SOFT_KEYS_OPTIONS[index]);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        // Draw the track states on the scene buttons
        final int trackState = this.surface.getTrackState ();
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1, trackState == APCminiControlSurface.TRACK_STATE_CLIP_STOP ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON2, trackState == APCminiControlSurface.TRACK_STATE_SOLO ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON3, trackState == APCminiControlSurface.TRACK_STATE_REC_ARM ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON4, trackState == APCminiControlSurface.TRACK_STATE_MUTE ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON5, trackState == APCminiControlSurface.TRACK_STATE_SELECT ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON6, this.model.isEffectTrackBankActive () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON7, APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON8, APCminiControlSurface.APC_BUTTON_STATE_OFF);

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1, tb.canScrollScenesUp () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON2, tb.canScrollScenesDown () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON3, tb.canScrollTracksUp () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON4, tb.canScrollTracksDown () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);

        final Integer mode = this.surface.getModeManager ().getActiveModeId ();
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON5, Modes.MODE_VOLUME.equals (mode) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON6, Modes.MODE_PAN.equals (mode) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON7, Modes.isSendMode (mode) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON8, Modes.MODE_DEVICE.equals (mode) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);

    }


    private void onNew ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData t = tb.getSelectedTrack ();
        if (t != null)
        {
            int trackIndex = t.getIndex ();
            final SlotData [] slotIndexes = tb.getSelectedSlots (trackIndex);
            final int slotIndex = slotIndexes.length == 0 ? 0 : slotIndexes[0].getIndex ();
            for (int i = 0; i < 8; i++)
            {
                final int sIndex = (slotIndex + i) % 8;
                final SlotData s = t.getSlots ()[sIndex];
                if (s.hasContent ())
                    continue;
                tb.createClip (trackIndex, sIndex, (int) Math.pow (2, this.surface.getConfiguration ().getNewClipLength ()));
                if (slotIndex != sIndex)
                    tb.selectClip (trackIndex, sIndex);
                tb.launchClip (trackIndex, sIndex);
                this.model.getTransport ().setLauncherOverdub (true);
                return;
            }
        }
        this.surface.getDisplay ().notify ("In the current selected grid view there is no empty slot. Please scroll down.");
    }
}