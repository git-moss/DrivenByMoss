// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.Pair;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.ChannelData;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;

import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;


/**
 * The Drum 64 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView64 extends AbstractView<PushControlSurface, PushConfiguration> implements SceneView, TransposeView
{
    private static final int    DRUM_START_KEY = 36;
    private static final int    GRID_COLUMNS   = 8;

    private static final int [] DRUM_MATRIX    =
    {
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            26,
            27,
            28,
            29,
            30,
            31,
            32,
            33,
            34,
            35,
            36,
            37,
            38,
            39,
            40,
            41,
            42,
            43,
            44,
            45,
            46,
            47,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            57,
            58,
            59,
            60,
            61,
            62,
            63
    };
    private int                 offsetY;
    private int                 selectedPad    = 0;
    private int []              pressedKeys    = new int [128];
    private int                 halfColumns;
    private int                 playLines;
    private int                 drumOctave;
    private CursorDeviceProxy   primaryDevice;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView64 (final PushControlSurface surface, final Model model)
    {
        super ("Drum 64", surface, model);

        this.offsetY = DrumView64.DRUM_START_KEY;

        this.canScrollUp = false;
        this.canScrollDown = false;

        this.scales = this.model.getScales ();
        this.noteMap = Scales.getEmptyMatrix ();

        this.halfColumns = 8;
        this.playLines = 8;

        this.drumOctave = 0;

        final TrackBankProxy tb = model.getTrackBank ();
        // Light notes send from the sequencer
        tb.addNoteObserver ( (note, velocity) -> this.pressedKeys[note] = velocity);
        tb.addTrackSelectionObserver ( (final int index, final boolean isSelected) -> this.clearPressedKeys ());

        final CursorDevice cd = tb.getCursorTrack ().createCursorDevice ("64_DRUM_PADS", "64 Drum Pads", 0, CursorDeviceFollowMode.FIRST_INSTRUMENT);
        this.primaryDevice = new CursorDeviceProxy (model.getHost (), cd, this.model.getValueChanger (), 0, 0, 0, 64, 64);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.model.canSelectedTrackHoldNotes ())
            return;

        if (!this.surface.isShiftPressed ())
            return;

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (index >= 4)
            return;

        final Pair<Integer, String> drumMode = DrumViewBase.getDrumModes ().get (Integer.valueOf (index));
        final Integer viewID = drumMode.getKey ();
        viewManager.setPreferredView (selectedTrack.getPosition (), viewID);
        this.surface.getViewManager ().setActiveView (viewID);
        this.surface.getDisplay ().notify (drumMode.getValue (), true, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.primaryDevice.enableObservers (true);
        this.primaryDevice.setDrumPadIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.primaryDevice.enableObservers (false);
        this.primaryDevice.setDrumPadIndication (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - 36;
        final int x = index % DrumView64.GRID_COLUMNS;
        final int y = index / DrumView64.GRID_COLUMNS;

        if (x < this.halfColumns)
        {
            // halfColumns x playLines Drum Pad Grid

            this.selectedPad = this.halfColumns * y + x;
            final int playedPad = velocity == 0 ? -1 : this.selectedPad;

            // Mark selected note
            this.pressedKeys[this.offsetY + this.selectedPad] = velocity;

            if (playedPad < 0)
                return;

            this.handleButtonCombinations (playedPad);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            this.surface.getPadGrid ().turnOff ();
            return;
        }

        // halfColumns x playLines Drum Pad Grid
        final boolean hasDrumPads = this.primaryDevice.hasDrumPads ();
        boolean isSoloed = false;
        if (hasDrumPads)
        {
            for (int i = 0; i < this.halfColumns * this.playLines; i++)
            {
                if (this.primaryDevice.getDrumPad (i).isSolo ())
                {
                    isSoloed = true;
                    break;
                }
            }
        }
        final boolean isRecording = this.model.hasRecordingState ();
        for (int y = 0; y < this.playLines; y++)
        {
            for (int x = 0; x < this.halfColumns; x++)
            {
                final int index = this.halfColumns * y + x;
                this.surface.getPadGrid ().lightEx (x, 7 - y, this.getPadColor (index, this.primaryDevice, isSoloed, isRecording));
            }
        }
    }


    private String getPadColor (final int index, final CursorDeviceProxy primary, final boolean isSoloed, final boolean isRecording)
    {
        // Playing note?
        if (this.pressedKeys[this.offsetY + index] > 0)
            return isRecording ? AbstractDrumView.COLOR_PAD_RECORD : AbstractDrumView.COLOR_PAD_PLAY;
        // Selected?
        if (this.selectedPad == index)
            return AbstractDrumView.COLOR_PAD_SELECTED;

        // Exists and active?
        final ChannelData drumPad = primary.getDrumPad (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return this.surface.getConfiguration ().isTurnOffEmptyDrumPads () ? AbstractDrumView.COLOR_PAD_OFF : AbstractDrumView.COLOR_PAD_NO_CONTENT;
        // Muted or soloed?
        if (drumPad.isMute () || isSoloed && !drumPad.isSolo ())
            return AbstractDrumView.COLOR_PAD_MUTED;

        return this.getPadContentColor (drumPad);
    }


    protected String getPadContentColor (final ChannelData drumPad)
    {
        return BitwigColors.getColorIndex (drumPad.getColor ());
    }


    private void clearPressedKeys ()
    {
        for (int i = 0; i < 128; i++)
            this.pressedKeys[i] = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        final boolean turnOn = this.model.canSelectedTrackHoldNotes () && !this.surface.isSelectPressed () && !this.surface.isDeletePressed () && !this.surface.isMutePressed () && !this.surface.isSoloPressed ();
        this.noteMap = turnOn ? this.getDrumMatrix () : Scales.getEmptyMatrix ();
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (this.noteMap));
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        this.clearPressedKeys ();
        final int oldDrumOctave = this.drumOctave;
        this.drumOctave = Math.max (-2, this.drumOctave - 1);
        this.offsetY = DrumView64.DRUM_START_KEY + this.drumOctave * 16;
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.getDrumRangeText (), true, true);

        if (oldDrumOctave != this.drumOctave)
        {
            // TODO Bugfix required: scrollChannelsUp scrolls the whole bank
            for (int i = 0; i < 16; i++)
                this.primaryDevice.scrollDrumPadsUp ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        this.clearPressedKeys ();
        final int oldDrumOctave = this.drumOctave;
        this.drumOctave = Math.min (1, this.drumOctave + 1);
        this.offsetY = DrumView64.DRUM_START_KEY + this.drumOctave * 16;
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.getDrumRangeText (), true, true);
        if (oldDrumOctave != this.drumOctave)
        {
            // TODO Bugfix required: scrollChannelsUp scrolls the whole bank
            for (int i = 0; i < 16; i++)
                this.primaryDevice.scrollDrumPadsDown ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, this.drumOctave < 1 ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, this.drumOctave > Scales.DRUM_OCTAVE_LOWER ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
    }


    private void handleButtonCombinations (final int playedPad)
    {
        if (this.surface.isDeletePressed ())
        {
            // Delete all of the notes on that "pad"
            this.handleDeleteButton (playedPad);
            return;
        }

        if (this.surface.isMutePressed ())
        {
            // Mute that "pad"
            this.handleMuteButton (playedPad);
            return;
        }

        if (this.surface.isSoloPressed ())
        {
            // Solo that "pad"
            this.handleSoloButton (playedPad);
            return;
        }

        if (this.surface.isSelectPressed () || this.surface.getConfiguration ().isAutoSelectDrum ())
        {
            // Also select the matching device layer channel of the pad
            this.handleSelectButton (playedPad);
        }
    }


    private void handleDeleteButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
        ((DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM)).getClip ().clearRow (this.offsetY + playedPad);
    }


    private void handleMuteButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getMuteButtonId ());
        this.primaryDevice.toggleLayerOrDrumPadMute (playedPad);
    }


    private void handleSoloButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getSoloButtonId ());
        this.primaryDevice.toggleLayerOrDrumPadSolo (playedPad);
    }


    private void handleSelectButton (final int playedPad)
    {
        if (!this.primaryDevice.hasDrumPads ())
            return;

        // Do not reselect
        if (this.primaryDevice.getDrumPad (playedPad).isSelected ())
            return;

        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (cd.isNested ())
            cd.selectParent ();

        this.surface.getModeManager ().setActiveMode (Modes.MODE_DEVICE_LAYER);
        this.primaryDevice.selectDrumPad (playedPad);
    }


    private int [] getDrumMatrix ()
    {
        final int [] matrix = DrumView64.DRUM_MATRIX;
        this.noteMap = Scales.getEmptyMatrix ();
        for (int i = 0; i < 64; i++)
        {
            final int n = matrix[i] == -1 ? -1 : matrix[i] + DrumView64.DRUM_START_KEY + this.drumOctave * 64;
            this.noteMap[DrumView64.DRUM_START_KEY + i] = n < 0 || n > 127 ? -1 : n;
        }
        return this.noteMap;
    }


    private String getDrumRangeText ()
    {
        final int s = DrumView64.DRUM_START_KEY + this.drumOctave * 64;
        return Scales.formatDrumNote (s) + " to " + Scales.formatDrumNote (s + 63);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int red = isPush2 ? PushColors.PUSH2_COLOR_SCENE_RED : PushColors.PUSH1_COLOR_SCENE_RED;
        final int orange = isPush2 ? PushColors.PUSH2_COLOR_SCENE_ORANGE : PushColors.PUSH1_COLOR_SCENE_ORANGE;
        final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;

        if (this.surface.isShiftPressed ())
        {
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1, off);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE2, off);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE3, off);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE4, off);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE5, red);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE6, orange);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE7, orange);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE8, orange);
            return;
        }

        for (int i = PushControlSurface.PUSH_BUTTON_SCENE1; i <= PushControlSurface.PUSH_BUTTON_SCENE8; i++)
            this.surface.updateButton (i, off);
    }
}