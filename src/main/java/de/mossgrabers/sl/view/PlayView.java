// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.daw.data.ChannelData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.command.trigger.ButtonRowSelectCommand;
import de.mossgrabers.sl.command.trigger.P2ButtonCommand;
import de.mossgrabers.sl.controller.SLControlSurface;
import de.mossgrabers.sl.mode.Modes;
import de.mossgrabers.sl.mode.device.DeviceParamsMode;

import java.util.Arrays;


/**
 * The view for playing and sequencing.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractSequencerView<SLControlSurface, SLConfiguration> implements SLView
{
    private static final int NUM_DISPLAY_COLS = 16;
    private static final int NOTE_VELOCITY    = 127;

    private int              selectedPad;
    private boolean          isPlayMode;
    protected int []         pressedKeys;
    private TransportControl transportControl;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final SLControlSurface surface, final Model model)
    {
        super ("Play", surface, model, 128, NUM_DISPLAY_COLS);

        this.transportControl = new TransportControl (surface, model);

        this.offsetY = Scales.DRUM_NOTE_START;
        this.selectedPad = 0;

        this.pressedKeys = new int [128];
        Arrays.fill (this.pressedKeys, 0);

        this.noteMap = Scales.getEmptyMatrix ();

        this.isPlayMode = true;

        final TrackBankProxy tb = model.getTrackBank ();
        tb.addNoteObserver ( (note, velocity) -> {
            // Light notes send from the sequencer
            this.pressedKeys[note] = velocity;
        });
        tb.addTrackSelectionObserver ( (index, isSelected) -> {
            this.clearPressedKeys ();
        });
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow1 (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer activeModeId = modeManager.getActiveModeId ();
        if (activeModeId == Modes.MODE_VIEW_SELECT)
        {
            if (index == 0)
            {
                this.surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);
                if (modeManager.getPreviousModeId () == Modes.MODE_VOLUME)
                    modeManager.restoreMode ();
                else
                    modeManager.setActiveMode (Modes.MODE_TRACK);
            }
            else
                modeManager.restoreMode ();
            this.surface.turnOffTransport ();
            return;
        }

        if (activeModeId != Modes.MODE_SESSION)
            modeManager.setActiveMode (Modes.MODE_SESSION);

        this.model.getSceneBank ().launchScene (index);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow2 (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer cm = modeManager.getActiveModeId ();
        if (cm != Modes.MODE_PLAY_OPTIONS)
            modeManager.setActiveMode (Modes.MODE_PLAY_OPTIONS);

        switch (index)
        {
            // Down
            case 0:
                this.clearPressedKeys ();
                this.scales.decDrumOctave ();
                this.model.getPrimaryDevice ().scrollDrumPadsPageUp ();
                this.offsetY = Scales.DRUM_NOTE_START + this.scales.getDrumOctave () * 16;
                this.updateNoteMapping ();
                this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
                break;

            // Up
            case 1:
                this.clearPressedKeys ();
                this.scales.incDrumOctave ();
                this.model.getPrimaryDevice ().scrollDrumPadsPageDown ();
                this.offsetY = Scales.DRUM_NOTE_START + this.scales.getDrumOctave () * 16;
                this.updateNoteMapping ();
                this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
                break;

            case 2:
                this.changeResolution (0);
                this.surface.getDisplay ().notify (RESOLUTION_TEXTS[this.selectedIndex]);
                break;

            case 3:
                this.changeResolution (127);
                this.surface.getDisplay ().notify (RESOLUTION_TEXTS[this.selectedIndex]);
                break;

            case 4:
                this.changeScrollPosition (0);
                break;

            case 5:
                this.changeScrollPosition (127);
                break;

            case 6:
                break;

            // Toggle play / sequencer
            case 7:
                this.isPlayMode = !this.isPlayMode;
                this.surface.getDisplay ().notify (this.isPlayMode ? "Play/Select" : "Sequence");
                this.updateNoteMapping ();
                break;

            default:
                // Intentionally empty
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow3 (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.onSeqGridNote (44 + index, PlayView.NOTE_VELOCITY);
        else if (event == ButtonEvent.UP)
            this.onSeqGridNote (44 + index, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow4 (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.onSeqGridNote (36 + index, PlayView.NOTE_VELOCITY);
        else if (event == ButtonEvent.UP)
            this.onSeqGridNote (36 + index, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow5 (final int index, final ButtonEvent event)
    {
        this.transportControl.execute (index, event);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow1Select ()
    {
        this.surface.getModeManager ().setActiveMode (Modes.MODE_SESSION);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow2Select ()
    {
        this.surface.getModeManager ().setActiveMode (Modes.MODE_PLAY_OPTIONS);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonP1 (final boolean isUp, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final Integer activeModeId = this.surface.getModeManager ().getActiveModeId ();
        if (activeModeId == Modes.MODE_SESSION)
        {
            if (isUp)
                this.model.getSceneBank ().scrollScenesPageDown ();
            else
                this.model.getSceneBank ().scrollScenesPageUp ();
            return;
        }

        if (activeModeId == Modes.MODE_VOLUME)
        {
            new P2ButtonCommand (isUp, this.model, this.surface).execute (event);
            return;
        }

        if (activeModeId == Modes.MODE_TRACK || activeModeId == Modes.MODE_MASTER)
        {
            new ButtonRowSelectCommand<> (3, this.model, this.surface).execute (event);
            return;
        }

        if (activeModeId == Modes.MODE_PLAY_OPTIONS)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        if (isUp)
            ((DeviceParamsMode) modeManager.getMode (Modes.MODE_PARAMS)).nextPage ();
        else
            ((DeviceParamsMode) modeManager.getMode (Modes.MODE_PARAMS)).previousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        // Button row 1: Launch Scene
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_1 + i, SLControlSurface.MKII_BUTTON_STATE_OFF);

        // Button row 2: Track toggles
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_1 + i, SLControlSurface.MKII_BUTTON_STATE_OFF);

        // LED indications for device parameters
        ((DeviceParamsMode) this.surface.getModeManager ().getMode (Modes.MODE_PARAMS)).setLEDs ();

        // Transport buttons
        if (this.surface.isTransportActive ())
        {
            for (int i = 0; i < 8; i++)
                this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW3_1 + i, SLControlSurface.MKII_BUTTON_STATE_OFF);
            final TransportProxy transport = this.model.getTransport ();
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_3, !transport.isPlaying () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_4, transport.isPlaying () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_5, transport.isLoop () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_6, transport.isRecording () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        }
        else
        {
            // Draw sequencer
            this.drawDrumGrid ();
        }

        final Integer mode = this.surface.getModeManager ().getActiveModeId ();
        final boolean isSession = mode == Modes.MODE_SESSION;
        final boolean isDevice = mode == Modes.MODE_PARAMS;
        final boolean isPlayOptions = mode == Modes.MODE_PLAY_OPTIONS;
        final boolean isTrack = mode == Modes.MODE_TRACK;
        final boolean isMaster = mode == Modes.MODE_MASTER;
        final boolean isVolume = mode == Modes.MODE_VOLUME;
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL1, isSession ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL2, isDevice ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL3, isPlayOptions ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL4, isTrack || isMaster ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL6, isVolume ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL7, SLControlSurface.MKII_BUTTON_STATE_OFF);
    }


    /**
     * 'Draw' the drum grid sequencer.
     */
    public void drawDrumGrid ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            for (int i = 0; i < 8; i++)
                this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW3_1 + i, SLControlSurface.MKII_BUTTON_STATE_OFF);
            for (int i = 0; i < 8; i++)
                this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_1 + i, SLControlSurface.MKII_BUTTON_STATE_OFF);
            return;
        }

        if (this.isPlayMode)
        {
            final CursorDeviceProxy primary = this.model.getPrimaryDevice ();
            final boolean hasDrumPads = primary.hasDrumPads ();
            boolean isSoloed = false;
            if (hasDrumPads)
            {
                for (int i = 0; i < 16; i++)
                {
                    if (primary.getDrumPad (i).isSolo ())
                    {
                        isSoloed = true;
                        break;
                    }
                }
            }
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    final int index = 8 * y + x;
                    final int color = this.getPadColor (index, primary, isSoloed);
                    if (y == 0)
                        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_1 + x, color);
                    else
                        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW3_1 + x, color);
                }
            }
        }
        else
        {
            // Paint the sequencer steps
            final int step = this.clip.getCurrentStep ();
            final int hiStep = this.isInXRange (step) ? step % PlayView.NUM_DISPLAY_COLS : -1;
            for (int col = 0; col < PlayView.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = this.clip.getStep (col, this.offsetY + this.selectedPad);
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                final double y = col / 8;
                final int color = isSet > 0 ? SLControlSurface.MKII_BUTTON_STATE_ON : hilite ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
                if (y == 0)
                    this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW3_1 + x, color);
                else
                    this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_1 + x, color);
            }
        }
    }


    /**
     * Clear the pressed keys.
     */
    public void clearPressedKeys ()
    {
        for (int i = 0; i < 128; i++)
            this.pressedKeys[i] = 0;
    }


    /**
     * A button of the sequencer grid was pressed.
     *
     * @param note The note pressed
     * @param velocity The velocity
     */
    public void onSeqGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - 36;

        if (this.isPlayMode)
        {
            this.selectedPad = index; // 0-16

            // Mark selected note
            this.pressedKeys[this.offsetY + this.selectedPad] = velocity;

            this.surface.sendMidiEvent (0x90, this.noteMap[note], velocity);
        }
        else
        {
            if (velocity != 0)
                this.clip.toggleStep (index < 8 ? index + 8 : index - 8, this.offsetY + this.selectedPad, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.noteMap = this.model.canSelectedTrackHoldNotes () && this.isPlayMode ? this.scales.getDrumMatrix () : Scales.getEmptyMatrix ();
        this.surface.setKeyTranslationTable (this.noteMap);
    }


    private int getPadColor (final int index, final CursorDeviceProxy primary, final boolean isSoloed)
    {
        // Playing note?
        if (this.pressedKeys[this.offsetY + index] > 0)
            return SLControlSurface.MKII_BUTTON_STATE_ON;
        // Selected?
        if (this.selectedPad == index)
            return SLControlSurface.MKII_BUTTON_STATE_ON;
        // Exists and active?
        final ChannelData drumPad = primary.getDrumPad (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return SLControlSurface.MKII_BUTTON_STATE_OFF;
        // Muted or soloed?
        if (drumPad.isMute () || isSoloed && !drumPad.isSolo ())
            return SLControlSurface.MKII_BUTTON_STATE_OFF;
        return SLControlSurface.MKII_BUTTON_STATE_OFF;
    }


    private void changeScrollPosition (final int value)
    {
        final boolean isInc = value >= 65;
        if (isInc)
            this.clip.scrollStepsPageForward ();
        else
            this.clip.scrollStepsPageBackwards ();
    }


    private void changeResolution (final int value)
    {
        final boolean isInc = value >= 65;
        this.selectedIndex = Math.max (0, Math.min (RESOLUTIONS.length - 1, isInc ? this.selectedIndex + 1 : this.selectedIndex - 1));
        this.clip.setStepLength (RESOLUTIONS[this.selectedIndex]);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        // Intentionally empty
    }
}