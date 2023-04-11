// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.pitchbend;

import de.mossgrabers.framework.command.core.AbstractPitchbendCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IMode;


/**
 * Command to delegate the moves of a knob/fader row to the active mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class FaderRowModeCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractPitchbendCommand<S, C>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public FaderRowModeCommand (final int index, final IModel model, final S surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void onPitchbend (final int data1, final int data2)
    {
        final IMode m = this.surface.getModeManager ().getActive ();
        if (m != null)
            m.onKnobValue (this.index, data2 * 128 + data1);
    }
}
