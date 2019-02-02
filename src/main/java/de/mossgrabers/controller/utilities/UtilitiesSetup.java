// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities;

import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.scale.Scales;


/**
 * Support for the Open Sound Control (Utilities) protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UtilitiesSetup extends AbstractControllerSetup<IControlSurface<UtilitiesConfiguration>, UtilitiesConfiguration>
{
    private static final int MAX_TRACKS = 100;

    private final AutoColor  autoColor;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public UtilitiesSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);

        this.autoColor = new AutoColor ();

        this.colorManager = new ColorManager ();
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new UtilitiesConfiguration (this.valueChanger, this.autoColor);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 0, 128, 128, 1);
        this.scales.setChromatic (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        ms.setNumDeviceLayers (0);
        ms.setNumDevicesInBank (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumFilterColumnEntries (0);
        ms.setNumMarkers (0);
        ms.setNumParams (0);
        ms.setNumResults (0);
        ms.setNumScenes (0);
        ms.setNumSends (0);

        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);

        ms.setNumTracks (MAX_TRACKS);

        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);

        this.autoColor.setTrackBank (this.model.getTrackBank ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        // Add name observers to all tracks
        this.model.getTrackBank ().addNameObserver (this.autoColor::matchTrackName);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        // Intentionally empty
    }
}
