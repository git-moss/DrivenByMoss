// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode;

import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.controller.novation.slmkiii.view.DrumView;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Mode for selecting the sequencer resolution.
 *
 * @author Jürgen Moßgraber
 */
public class SequencerResolutionMode extends BaseMode<IItem>
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

        final DrumView drumView = (DrumView) this.surface.getViewManager ().get (Views.DRUM);
        drumView.getClip ().setStepLength (Resolution.getValueAt (index));
        this.surface.getModeManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final DrumView drumView = (DrumView) this.surface.getViewManager ().get (Views.DRUM);
        final int match = Resolution.getMatch (drumView.getClip ().getStepLength ());
        final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
        return match == index ? SLMkIIIColorManager.SLMKIII_PINK : SLMkIIIColorManager.SLMKIII_DARK_GREY;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();

        final DrumView drumView = (DrumView) this.surface.getViewManager ().get (Views.DRUM);

        final int match = Resolution.getMatch (drumView.getClip ().getStepLength ());

        for (int i = 0; i < 8; i++)
        {
            d.setCell (3, i, Resolution.getNameAt (i));
            d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_PINK);
            d.setPropertyValue (i, 1, match == i ? 1 : 0);
        }

        d.setCell (0, 8, "Sequencer");
        d.setCell (1, 8, "Resolutin");

        this.setButtonInfo (d);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return SLMkIIIColorManager.SLMKIII_PINK;
    }
}