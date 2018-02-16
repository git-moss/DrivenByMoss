// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig;

import de.mossgrabers.framework.bitwig.daw.Model;
import de.mossgrabers.framework.bitwig.midi.MidiDeviceImpl;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
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
    public IModel createModel (final ColorManager colorManager, final ValueChanger valueChanger, final Scales scales, final int numTracks, final int numScenes, final int numSends, final int numFilterColumnEntries, final int numResults, final boolean hasFlatTrackList, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        return new Model (this.host, colorManager, valueChanger, scales, numTracks, numScenes, numSends, numFilterColumnEntries, numResults, hasFlatTrackList, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiAccess createMidiAccess ()
    {
        return new MidiDeviceImpl (this.host);
    }
}
