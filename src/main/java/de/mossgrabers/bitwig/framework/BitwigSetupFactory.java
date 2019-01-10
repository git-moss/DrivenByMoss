// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework;

import de.mossgrabers.bitwig.framework.daw.ModelImpl;
import de.mossgrabers.bitwig.framework.midi.MidiDeviceImpl;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.scale.Scales;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Factory for creating Bitwig objects.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BitwigSetupFactory implements ISetupFactory
{
    private ControllerHost host;


    /**
     * Constructor.
     *
     * @param host The DAW host
     */
    public BitwigSetupFactory (final ControllerHost host)
    {
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public IModel createModel (final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final ModelSetup modelSetup)
    {
        return new ModelImpl (this.host, colorManager, valueChanger, scales, modelSetup);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiAccess createMidiAccess ()
    {
        return new MidiDeviceImpl (this.host);
    }
}
