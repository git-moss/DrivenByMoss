// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;


/**
 * The Arranger Loop Start mode.
 *
 * @author Jürgen Moßgraber
 */
public class LoopStartMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public LoopStartMode (final MaschineControlSurface surface, final IModel model)
    {
        super ("Loop Start", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final double speed = this.model.getValueChanger ().calcKnobChange (value);
        this.model.getTransport ().changeLoopStart (speed > 0, this.surface.isKnobSensitivitySlow ());
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final ITransport transport = this.model.getTransport ();
        d.setBlock (0, 0, "Arranger Loop");
        d.setBlock (0, 2, "Start:").setBlock (0, 3, "> " + transport.getLoopStartBeatText ());
        d.setBlock (1, 2, "Length:").setBlock (1, 3, "  " + transport.getLoopLengthBeatText ());

        d.allDone ();
    }
}