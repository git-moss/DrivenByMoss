// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.osc.mkii;

import de.mossgrabers.controller.kontrol.osc.mkii.protocol.KontrolOSCParser;
import de.mossgrabers.controller.kontrol.osc.mkii.protocol.KontrolOSCWriter;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.DummyControlSurface;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.framework.scale.Scales;


/**
 * Support for the Open Sound Control (OSC) protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolOSCControllerSetup extends AbstractControllerSetup<IControlSurface<KontrolOSCConfiguration>, KontrolOSCConfiguration>
{
    /** Global protocol version switch. */
    public static final boolean IS_16 = false;

    private KontrolOSCWriter    writer;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public KontrolOSCControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);

        this.colorManager = new ColorManager ();
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new KontrolOSCConfiguration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.writer.flush (false);
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
        ms.setNumTracks (128);
        ms.setNumScenes (128);
        ms.setNumSends (0);
        ms.setNumFilterColumnEntries (0);
        ms.setNumResults (0);
        ms.setNumDeviceLayers (0);
        ms.setNumDevicesInBank (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumMarkers (0);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        this.factory.createMidiAccess ().createInput ("Kontrol OSC Midi");

        final DummyControlSurface<KontrolOSCConfiguration> surface = new DummyControlSurface<> (this.model.getHost (), this.colorManager, this.configuration);
        this.surfaces.add (surface);

        // Send OSC messages
        final IOpenSoundControlServer oscServer = this.host.connectToOSCServer (this.configuration.getSendHost (), this.configuration.getSendPort ());
        this.writer = new KontrolOSCWriter (this.host, this.model, oscServer, IS_16, this.configuration);

        // Receive OSC messages
        this.host.createOSCServer (new KontrolOSCParser (this.host, surface, this.model, this.configuration, this.writer, IS_16), this.configuration.getReceivePort ());
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Initial flush of the whole DAW state
        this.host.scheduleTask ( () -> this.writer.flush (true), 1000);
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.writer.shutdown ();

        super.exit ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        // Unused
    }
}
