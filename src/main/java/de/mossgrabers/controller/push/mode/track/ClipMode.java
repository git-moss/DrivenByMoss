// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.view.ColorView;
import de.mossgrabers.controller.push.view.ColorView.SelectMode;
import de.mossgrabers.controller.push.view.Views;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.ViewManager;


/**
 * Mode for editing the parameters of a clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ClipMode extends AbstractTrackMode
{
    private boolean displayMidiNotes  = false;
    private boolean disableMidiEditor = true;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ClipMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (index == 7 && isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
            this.model.getCursorClip ().resetAccent ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        if (!this.increaseKnobMovement ())
            return;

        final ICursorClip clip = this.model.getCursorClip ();
        switch (index)
        {
            case 0:
                clip.changePlayStart (value);
                break;
            case 1:
                clip.changePlayEnd (value);
                break;
            case 2:
                clip.changeLoopStart (value);
                break;
            case 3:
                clip.changeLoopLength (value);
                break;
            case 4:
                clip.setLoopEnabled (value <= 61);
                break;
            case 6:
                clip.setShuffleEnabled (value <= 61);
                break;
            case 7:
                clip.changeAccent (value);
                break;
            default:
                // Intentionally empty
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ICursorClip clip = this.model.getCursorClip ();
        d.setCell (0, 0, "PlayStrt").setCell (1, 0, this.formatMeasures (clip.getPlayStart (), 1));
        d.setCell (0, 1, "Play End").setCell (1, 1, this.formatMeasures (clip.getPlayEnd (), 1));
        d.setCell (0, 2, "LoopStrt").setCell (1, 2, this.formatMeasures (clip.getLoopStart (), 1));
        d.setCell (0, 3, "LopLngth").setCell (1, 3, this.formatMeasures (clip.getLoopLength (), 0));
        d.setCell (0, 4, "Loop").setCell (1, 4, clip.isLoopEnabled () ? "On" : "Off").clearCell (0, 5).clearCell (1, 5);
        d.setCell (0, 6, "Shuffle").setCell (1, 6, clip.isShuffleEnabled () ? "On" : "Off");
        d.setCell (0, 7, "Accent").setCell (1, 7, clip.getFormattedAccent ()).done (0).done (1).clearRow (2).done (2);
        this.drawRow4 ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final ICursorClip clip = this.model.getCursorClip ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();

        if (this.displayMidiNotes)
        {
            message.setMidiClipElement (clip, this.model.getTransport ().getQuartersPerMeasure ());
            message.send ();
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

        if (this.disableMidiEditor)
            message.addParameterElement ("", false, t0.getName (), t0.getType (), t0.getColor (), t0.isSelected (), "Play Start", -1, this.formatMeasures (clip.getPlayStart (), 1), this.isKnobTouched[0], -1);
        else
            message.addParameterElement ("Piano Roll", this.displayMidiNotes, t0.getName (), t0.getType (), t0.getColor (), t0.isSelected (), "Play Start", -1, this.formatMeasures (clip.getPlayStart (), 1), this.isKnobTouched[0], -1);
        message.addParameterElement ("", false, t1.getName (), t1.getType (), t1.getColor (), t1.isSelected (), "Play End", -1, this.formatMeasures (clip.getPlayEnd (), 1), this.isKnobTouched[1], -1);
        message.addParameterElement ("", false, t2.getName (), t2.getType (), t2.getColor (), t2.isSelected (), "Loop Start", -1, this.formatMeasures (clip.getLoopStart (), 1), this.isKnobTouched[2], -1);
        message.addParameterElement ("", false, t3.getName (), t3.getType (), t3.getColor (), t3.isSelected (), "Loop Lngth", -1, this.formatMeasures (clip.getLoopLength (), 0), this.isKnobTouched[3], -1);
        message.addParameterElement ("", false, t4.getName (), t4.getType (), t4.getColor (), t4.isSelected (), "Loop", -1, clip.isLoopEnabled () ? "On" : "Off", this.isKnobTouched[4], -1);
        message.addParameterElement ("", false, t5.getName (), t5.getType (), t5.getColor (), t5.isSelected (), "", -1, "", false, -1);
        message.addParameterElement ("", false, t6.getName (), t6.getType (), t6.getColor (), t6.isSelected (), "Shuffle", -1, clip.isShuffleEnabled () ? "On" : "Off", this.isKnobTouched[6], -1);
        message.addParameterElement ("Select color", false, t7.getName (), t7.getType (), t7.getColor (), t7.isSelected (), "Accent", -1, clip.getFormattedAccent (), this.isKnobTouched[7], -1);
        message.send ();
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

        switch (index)
        {
            case 0:
                if (!this.disableMidiEditor)
                {
                    if (this.isPush2)
                        this.displayMidiNotes = !this.displayMidiNotes;
                }
                break;
            case 7:
                final ViewManager viewManager = this.surface.getViewManager ();
                ((ColorView) viewManager.getView (Views.VIEW_COLOR)).setMode (SelectMode.MODE_CLIP);
                viewManager.setActiveView (Views.VIEW_COLOR);
                break;
            default:
                // not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        if (this.disableMidiEditor)
            this.surface.updateButton (102, PushColors.PUSH2_COLOR_BLACK);
        else
            this.surface.updateButton (102, this.isPush2 && !this.displayMidiNotes ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
        this.surface.updateButton (103, PushColors.PUSH2_COLOR_BLACK);
        this.surface.updateButton (104, PushColors.PUSH2_COLOR_BLACK);
        this.surface.updateButton (105, PushColors.PUSH2_COLOR_BLACK);
        this.surface.updateButton (106, PushColors.PUSH2_COLOR_BLACK);
        this.surface.updateButton (107, PushColors.PUSH2_COLOR_BLACK);
        this.surface.updateButton (108, PushColors.PUSH2_COLOR_BLACK);
        this.surface.updateButton (109, this.displayMidiNotes ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH2_COLOR2_WHITE);
    }


    private String formatMeasures (final double time, final int startOffset)
    {
        return StringUtils.formatMeasures (this.model.getTransport ().getQuartersPerMeasure (), time, startOffset);
    }
}