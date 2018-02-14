// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.pitchbend;

import de.mossgrabers.framework.command.core.AbstractPitchbendCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;


/**
 * Command to handle pitchbend for the session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PitchbendSessionCommand extends AbstractPitchbendCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PitchbendSessionCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void onPitchbend (final int channel, final int data1, final int data2)
    {
        final int value = this.surface.isShiftPressed () ? 63 : data2;
        this.model.getTransport ().setCrossfade (this.model.getValueChanger ().toDAWValue (value));
        this.surface.setRibbonValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateValue ()
    {
        this.surface.setRibbonValue (this.model.getValueChanger ().toMidiValue (this.model.getTransport ().getCrossfade ()));
    }
}
