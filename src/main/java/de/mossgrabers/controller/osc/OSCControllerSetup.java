// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc;

import de.mossgrabers.controller.osc.protocol.OSCParser;
import de.mossgrabers.controller.osc.protocol.OSCWriter;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.KeyManager;


/**
 * Support for the Open Sound Control (OSC) protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCControllerSetup extends AbstractControllerSetup<IControlSurface<OSCConfiguration>, OSCConfiguration>
{
    private OSCWriter  writer;
    private KeyManager keyManager;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public OSCControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);

        this.colorManager = new ColorManager ();
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new OSCConfiguration (this.valueChanger);
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
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, 8, 8, 8, 16, 16, false, 8, 8, 8, 16);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.keyManager = new KeyManager (this.model, this.getSurface ().getPadGrid ());
        final ITrackBank tb = this.model.getTrackBank ();
        for (int i = 0; i < tb.getPageSize (); i++)
            tb.getItem (i).addNoteObserver (this.keyManager);
        tb.addSelectionObserver ( (final int index, final boolean isSelected) -> this.keyManager.clearPressedKeys ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("OSC");

        final OSCControlSurface surface = new OSCControlSurface (this.model.getHost (), this.configuration, this.colorManager, input);
        this.surfaces.add (surface);

        // Send OSC messages
        final IOpenSoundControlServer oscServer = this.host.connectToOSCServer (this.configuration.getSendHost (), this.configuration.getSendPort ());
        this.writer = new OSCWriter (this.host, this.model, oscServer, this.keyManager, this.configuration);

        // Receive OSC messages
        this.host.createOSCServer (new OSCParser (this.host, surface, this.model, this.configuration, this.writer, input, this.keyManager), this.configuration.getReceivePort ());
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Initial flush of the whole DAW state
        this.host.scheduleTask ( () -> this.writer.flush (true), 1000);
    }
}
