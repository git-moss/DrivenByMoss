// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.track;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.CursorClipProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;
import de.mossgrabers.push.view.ColorView;
import de.mossgrabers.push.view.ColorView.SelectMode;
import de.mossgrabers.push.view.DrumView;
import de.mossgrabers.push.view.Views;


/**
 * Mode for editing the parameters of a clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ClipMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ClipMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (index == 7 && isTouched && this.surface.isDeletePressed ())
            this.getClip ().resetAccent ();
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final CursorClipProxy clip = this.getClip ();
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
        final CursorClipProxy clip = this.getClip ();
        d.setCell (0, 0, "PlayStrt").setCell (1, 0, this.formatMeasures (clip.getPlayStart ()));
        d.setCell (0, 1, "Play End").setCell (1, 1, this.formatMeasures (clip.getPlayEnd ()));
        d.setCell (0, 2, "LoopStrt").setCell (1, 2, this.formatMeasures (clip.getLoopStart ()));
        d.setCell (0, 3, "LopLngth").setCell (1, 3, this.formatMeasures (clip.getLoopLength ()));
        d.setCell (0, 4, "Loop").setCell (1, 4, clip.isLoopEnabled () ? "On" : "Off").clearCell (0, 5).clearCell (1, 5);
        d.setCell (0, 6, "Shuffle").setCell (1, 6, clip.isShuffleEnabled () ? "On" : "Off");
        d.setCell (0, 7, "Accent").setCell (1, 7, clip.getFormattedAccent ()).done (0).done (1).clearRow (2).done (2);
        this.drawRow4 ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final CursorClipProxy clip = this.getClip ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();

        final TrackData t0 = tb.getTrack (0);
        final TrackData t1 = tb.getTrack (1);
        final TrackData t2 = tb.getTrack (2);
        final TrackData t3 = tb.getTrack (3);
        final TrackData t4 = tb.getTrack (4);
        final TrackData t5 = tb.getTrack (5);
        final TrackData t6 = tb.getTrack (6);
        final TrackData t7 = tb.getTrack (7);

        message.addParameterElement ("", false, t0.getName (), getChannelType (t0), tb.getTrackColorEntry (0), t0.isSelected (), "Play Start", -1, this.formatMeasures (clip.getPlayStart ()), this.isKnobTouched[0], -1);
        message.addParameterElement ("", false, t1.getName (), getChannelType (t1), tb.getTrackColorEntry (1), t1.isSelected (), "Play End", -1, this.formatMeasures (clip.getPlayEnd ()), this.isKnobTouched[1], -1);
        message.addParameterElement ("", false, t2.getName (), getChannelType (t2), tb.getTrackColorEntry (2), t2.isSelected (), "Loop Start", -1, this.formatMeasures (clip.getLoopStart ()), this.isKnobTouched[2], -1);
        message.addParameterElement ("", false, t3.getName (), getChannelType (t3), tb.getTrackColorEntry (3), t3.isSelected (), "Loop Lngth", -1, this.formatMeasures (clip.getLoopLength ()), this.isKnobTouched[3], -1);
        message.addParameterElement ("", false, t4.getName (), getChannelType (t4), tb.getTrackColorEntry (4), t4.isSelected (), "Loop", -1, clip.isLoopEnabled () ? "On" : "Off", this.isKnobTouched[4], -1);
        message.addParameterElement ("", false, t5.getName (), getChannelType (t5), tb.getTrackColorEntry (5), t5.isSelected (), "", -1, "", false, -1);
        message.addParameterElement ("", false, t6.getName (), getChannelType (t6), tb.getTrackColorEntry (6), t6.isSelected (), "Shuffle", -1, clip.isShuffleEnabled () ? "On" : "Off", this.isKnobTouched[6], -1);
        message.addParameterElement ("Select color", false, t7.getName (), getChannelType (t7), tb.getTrackColorEntry (7), t7.isSelected (), "Accent", -1, clip.getFormattedAccent (), this.isKnobTouched[7], -1);
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (index != 7)
            return;
        final ViewManager viewManager = this.surface.getViewManager ();
        ((ColorView) viewManager.getView (Views.VIEW_COLOR)).setMode (SelectMode.MODE_CLIP);
        viewManager.setActiveView (Views.VIEW_COLOR);
    }


    private static ChannelType getChannelType (final TrackData t)
    {
        final String type = t.getType ();
        return type.length () == 0 ? null : ChannelType.valueOf (type.toUpperCase ());
    }


    private CursorClipProxy getClip ()
    {
        return ((DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM)).getClip ();
    }


    private String formatMeasures (final double time)
    {
        final int quartersPerMeasure = this.model.getQuartersPerMeasure ();
        final int measure = (int) Math.floor (time / quartersPerMeasure);
        double t = time - measure * quartersPerMeasure;
        final int quarters = (int) Math.floor (t); // :1
        t = t - quarters; // *1
        final int eights = (int) Math.floor (t / 0.25);
        return measure + "." + quarters + "." + eights;
    }
}