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
        for (int i = 0; i < 8; i++)
        {
            final TrackData t = tb.getTrack (i);

            message.addByte (DisplayMessage.GRID_ELEMENT_PARAMETERS);
            message.addString (i == 7 ? "Select color" : "");
            message.addBoolean (false);

            // Channel info
            message.addString (t.getName ());
            message.addString (t.getType ());
            message.addColor (tb.getTrackColorEntry (i));
            message.addByte (t.isSelected () ? 1 : 0);

            switch (i)
            {
                case 0:
                    message.addString ("Play Start");
                    message.addInteger (-1);
                    message.addString (this.formatMeasures (clip.getPlayStart ()));
                    message.addBoolean (this.isKnobTouched[i]);
                    break;
                case 1:
                    message.addString ("Play End");
                    message.addInteger (-1);
                    message.addString (this.formatMeasures (clip.getPlayEnd ()));
                    message.addBoolean (this.isKnobTouched[i]);
                    break;
                case 2:
                    message.addString ("Loop Start");
                    message.addInteger (-1);
                    message.addString (this.formatMeasures (clip.getLoopStart ()));
                    message.addBoolean (this.isKnobTouched[i]);
                    break;
                case 3:
                    message.addString ("Loop Lngth");
                    message.addInteger (-1);
                    message.addString (this.formatMeasures (clip.getLoopLength ()));
                    message.addBoolean (this.isKnobTouched[i]);
                    break;
                case 4:
                    message.addString ("Loop");
                    message.addInteger (-1);
                    message.addString (clip.isLoopEnabled () ? "On" : "Off");
                    message.addBoolean (this.isKnobTouched[i]);
                    break;
                case 6:
                    message.addString ("Shuffle");
                    message.addInteger (-1);
                    message.addString (clip.isShuffleEnabled () ? "On" : "Off");
                    message.addBoolean (this.isKnobTouched[i]);
                    break;
                case 7:
                    message.addString ("Accent");
                    message.addInteger (-1);
                    message.addString (clip.getFormattedAccent ());
                    message.addBoolean (this.isKnobTouched[i]);
                    break;
                default:
                    message.addString ("");
                    message.addInteger (-1);
                    message.addString ("");
                    message.addBoolean (false);
                    break;
            }
            message.addInteger (-1);
        }

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