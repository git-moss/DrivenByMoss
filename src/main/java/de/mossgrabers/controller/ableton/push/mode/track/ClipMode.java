// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.ColorSelectMode;
import de.mossgrabers.framework.view.ColorView;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Mode for editing the parameters of a clip.
 *
 * @author Jürgen Moßgraber
 */
public class ClipMode extends AbstractTrackMode
{
    private static final String PLEASE_SELECT_A_CLIP_PUSH1 = "      Pleaseselect a clip.";
    private static final String PLEASE_SELECT_A_CLIP_PUSH2 = "Please select a clip.";

    private boolean             displayMidiNotes           = false;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ClipMode (final PushControlSurface surface, final IModel model)
    {
        super ("Clip", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        if (index == 7 && isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            final IClip clip = this.model.getCursorClip ();
            if (clip.doesExist ())
                clip.resetAccent ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (!this.increaseKnobMovement ())
            return;

        final IClip clip = this.model.getCursorClip ();
        if (!clip.doesExist ())
            return;

        final boolean shiftPressed = this.surface.isShiftPressed ();

        switch (index)
        {
            case 0:
                clip.changePlayStart (value, shiftPressed);
                break;
            case 1:
                clip.changePlayEnd (value, shiftPressed);
                break;
            case 2:
                clip.changeLoopStart (value, shiftPressed);
                break;
            case 3:
                clip.changeLoopLength (value, shiftPressed);
                break;
            case 4:
                clip.setLoopEnabled (value <= 61);
                break;
            case 6:
                clip.setShuffleEnabled (value <= 61);
                break;
            case 7:
                clip.changeAccent (value, shiftPressed);
                break;
            default:
                // Intentionally empty
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final IClip clip = this.model.getCursorClip ();
        if (!clip.doesExist ())
        {
            display.notify (PLEASE_SELECT_A_CLIP_PUSH1);
            return;
        }

        display.setCell (0, 0, "PlayStrt").setCell (1, 0, this.formatMeasures (clip.getPlayStart (), 1));
        display.setCell (0, 1, "Play End").setCell (1, 1, this.formatMeasures (clip.getPlayEnd (), 1));
        display.setCell (0, 2, "LoopStrt").setCell (1, 2, this.formatMeasures (clip.getLoopStart (), 1));
        display.setCell (0, 3, "LopLngth").setCell (1, 3, this.formatMeasures (clip.getLoopLength (), 0));
        display.setCell (0, 4, "Loop").setCell (1, 4, clip.isLoopEnabled () ? "On" : "Off");
        display.setCell (0, 6, "Shuffle").setCell (1, 6, clip.isShuffleEnabled () ? "On" : "Off");
        display.setCell (0, 7, "Accent").setCell (1, 7, clip.getFormattedAccent ());
        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        if (this.displayMidiNotes)
        {
            final IView activeView = this.surface.getViewManager ().getActive ();
            INoteClip clip;
            if (activeView instanceof final AbstractSequencerView<?, ?> sequencerView)
                clip = sequencerView.getClip ();
            else
                clip = this.model.getNoteClip (8, 128);
            if (!clip.doesExist ())
            {
                display.addEmptyElement ();
                display.notify (PLEASE_SELECT_A_CLIP_PUSH2);
                return;
            }

            display.setMidiClipElement (clip, this.model.getTransport ().getQuartersPerMeasure ());
            return;
        }

        final IClip clip = this.model.getCursorClip ();
        if (!clip.doesExist ())
        {
            display.addEmptyElement ();
            display.notify (PLEASE_SELECT_A_CLIP_PUSH2);
            return;
        }

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack t0 = tb.getItem (0);
        final ITrack t1 = tb.getItem (1);
        final ITrack t2 = tb.getItem (2);
        final ITrack t3 = tb.getItem (3);
        final ITrack t4 = tb.getItem (4);
        final ITrack t5 = tb.getItem (5);
        final ITrack t6 = tb.getItem (6);
        final ITrack t7 = tb.getItem (7);

        final boolean canPin = this.model.getHost ().supports (Capability.HAS_PINNING);
        final boolean isPinned = canPin && clip instanceof final INoteClip noteClip && noteClip.isPinned ();

        display.addParameterElement (canPin ? "Pin clip" : "", isPinned, t0.getName (), this.updateType (t0), t0.getColor (), t0.isSelected (), "Play Start", -1, this.formatMeasures (clip.getPlayStart (), 1), this.isKnobTouched (0), -1);
        display.addParameterElement ("", false, t1.getName (), this.updateType (t1), t1.getColor (), t1.isSelected (), "Play End", -1, this.formatMeasures (clip.getPlayEnd (), 1), this.isKnobTouched (1), -1);
        display.addParameterElement ("", false, t2.getName (), this.updateType (t2), t2.getColor (), t2.isSelected (), "Loop Start", -1, this.formatMeasures (clip.getLoopStart (), 1), this.isKnobTouched (2), -1);
        display.addParameterElement ("", false, t3.getName (), this.updateType (t3), t3.getColor (), t3.isSelected (), "Loop Lngth", -1, this.formatMeasures (clip.getLoopLength (), 0), this.isKnobTouched (3), -1);
        display.addParameterElement ("", false, t4.getName (), this.updateType (t4), t4.getColor (), t4.isSelected (), "Loop", -1, clip.isLoopEnabled () ? "On" : "Off", this.isKnobTouched (4), -1);
        display.addParameterElement ("", false, t5.getName (), this.updateType (t5), t5.getColor (), t5.isSelected (), "", -1, "", false, -1);
        display.addParameterElement ("", false, t6.getName (), this.updateType (t6), t6.getColor (), t6.isSelected (), "Shuffle", -1, clip.isShuffleEnabled () ? "On" : "Off", this.isKnobTouched (6), -1);
        display.addParameterElement ("Select color", false, t7.getName (), this.updateType (t7), t7.getColor (), t7.isSelected (), "Accent", -1, clip.getFormattedAccent (), this.isKnobTouched (7), -1);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.displayMidiNotes)
        {
            this.displayMidiNotes = false;
            return;
        }

        if (index == 0)
        {
            final IClip clip = this.model.getCursorClip ();
            if (clip instanceof final INoteClip noteClip)
                noteClip.togglePinned ();
            return;
        }

        if (index == 7)
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            ((ColorView<?, ?>) viewManager.get (Views.COLOR)).setMode (ColorSelectMode.MODE_CLIP);
            viewManager.setActive (Views.COLOR);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0)
            {
                if (!this.model.getHost ().supports (Capability.HAS_PINNING))
                    return PushColorManager.PUSH2_COLOR2_BLACK;
                final IClip clip = this.model.getCursorClip ();
                final boolean isPinned = clip instanceof final INoteClip noteClip && noteClip.isPinned ();
                return isPinned ? PushColorManager.PUSH2_COLOR2_GREEN : PushColorManager.PUSH2_COLOR2_WHITE;
            }
            if (index == 7)
                return this.displayMidiNotes ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH2_COLOR2_WHITE;
            return PushColorManager.PUSH2_COLOR_BLACK;
        }

        return super.getButtonColor (buttonID);
    }


    /**
     * Toggles the clip parameter with the piano roll display.
     */
    public void togglePianoRoll ()
    {
        this.displayMidiNotes = !this.displayMidiNotes;
    }


    private String formatMeasures (final double time, final int startOffset)
    {
        return StringUtils.formatMeasures (this.model.getTransport ().getQuartersPerMeasure (), time, startOffset, false);
    }
}