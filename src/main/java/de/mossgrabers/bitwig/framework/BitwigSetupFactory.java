// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.daw.ModelImpl;
import de.mossgrabers.bitwig.framework.midi.MidiDeviceImpl;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DataSetup;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.scale.Scales;

import com.bitwig.extension.controller.api.ControllerHost;

import java.util.Arrays;
import java.util.List;


/**
 * Factory for creating Bitwig objects.
 *
 * @author Jürgen Moßgraber
 */
public class BitwigSetupFactory implements ISetupFactory
{
    private final ControllerHost               controllerHost;

    private static final List<ArpeggiatorMode> ARP_MODES = Arrays.asList (ArpeggiatorMode.values ());


    /**
     * Constructor.
     *
     * @param controllerHost The DAW host
     */
    public BitwigSetupFactory (final ControllerHost controllerHost)
    {
        this.controllerHost = controllerHost;
    }


    /** {@inheritDoc} */
    @Override
    public IModel createModel (final Configuration configuration, final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final ModelSetup modelSetup)
    {
        final DataSetup dataSetup = new DataSetup (new HostImpl (this.controllerHost), valueChanger, colorManager);
        return new ModelImpl (modelSetup, dataSetup, this.controllerHost, scales);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiAccess createMidiAccess ()
    {
        return new MidiDeviceImpl (this.controllerHost);
    }


    /** {@inheritDoc} */
    @Override
    public List<ArpeggiatorMode> getArpeggiatorModes ()
    {
        return ARP_MODES;
    }
}
