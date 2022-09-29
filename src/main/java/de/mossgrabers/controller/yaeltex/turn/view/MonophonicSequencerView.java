// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.view;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.sequencer.AbstractMonophonicSequencerView;


/**
 * The monophonic sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MonophonicSequencerView extends AbstractMonophonicSequencerView<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public MonophonicSequencerView (final YaeltexTurnControlSurface surface, final IModel model)
    {
        super ("Monophonic Sequencer", surface, model, 4, 8, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.isActive () || velocity == 0)
            return;

        final int index = note - 36;
        final int x = index % this.numSequencerColumns;
        final int y = this.numSequencerRows - 1 - index / this.numSequencerColumns;
        final int step = y * this.numSequencerColumns + x;

        // TODO How to calc the length?
        // this.getClip ().getStepLength () * step
    }
}
