// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode;

import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.controller.slmkiii.view.DrumView;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.Views;


/**
 * Mode for selecting the sequencer resolution.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerResolutionMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SequencerResolutionMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Sequencer Resolution", surface, model);

        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final DrumView drumView = (DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM);
        drumView.getClip ().setStepLength (AbstractSequencerView.RESOLUTIONS[index]);
        this.surface.getModeManager ().restoreMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final DrumView drumView = (DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM);
        final double stepLength = drumView.getClip ().getStepLength ();
        for (int i = 0; i < 8; i++)
            this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, Math.abs (stepLength - AbstractSequencerView.RESOLUTIONS[i]) < 0.001 ? SLMkIIIColors.SLMKIII_PINK : SLMkIIIColors.SLMKIII_DARK_GREY);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();

        final DrumView drumView = (DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM);
        final double stepLength = drumView.getClip ().getStepLength ();

        for (int i = 0; i < 8; i++)
        {
            d.setCell (3, i, AbstractSequencerView.RESOLUTION_TEXTS[i]);
            d.setPropertyColor (i, 2, SLMkIIIColors.SLMKIII_PINK);
            d.setPropertyValue (i, 1, Math.abs (stepLength - AbstractSequencerView.RESOLUTIONS[i]) < 0.001 ? 1 : 0);
        }

        d.setCell (0, 8, "Sequencer");
        d.setCell (1, 8, "Resoltion");
        d.setPropertyColor (8, 0, SLMkIIIColors.SLMKIII_PINK);

        this.setButtonInfo (d);

        d.allDone ();
    }
}