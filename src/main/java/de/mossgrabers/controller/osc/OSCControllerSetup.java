// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc;

import de.mossgrabers.controller.osc.module.ActionModule;
import de.mossgrabers.controller.osc.module.BrowserModule;
import de.mossgrabers.controller.osc.module.ClipModule;
import de.mossgrabers.controller.osc.module.DeviceModule;
import de.mossgrabers.controller.osc.module.GlobalModule;
import de.mossgrabers.controller.osc.module.IModule;
import de.mossgrabers.controller.osc.module.LayoutModule;
import de.mossgrabers.controller.osc.module.MarkerModule;
import de.mossgrabers.controller.osc.module.MidiModule;
import de.mossgrabers.controller.osc.module.ProjectModule;
import de.mossgrabers.controller.osc.module.SceneModule;
import de.mossgrabers.controller.osc.module.TrackModule;
import de.mossgrabers.controller.osc.module.TransportModule;
import de.mossgrabers.controller.osc.protocol.OSCParser;
import de.mossgrabers.controller.osc.protocol.OSCWriter;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.osc.IOpenSoundControlClient;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.KeyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Support for the Open Sound Control (OSC) protocol.
 *
 * @author Jürgen Moßgraber
 */
public class OSCControllerSetup extends AbstractControllerSetup<IControlSurface<OSCConfiguration>, OSCConfiguration>
{
    private OSCWriter               writer;
    private KeyManager              keyManager;
    private IOpenSoundControlServer oscServer;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public OSCControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new OSCColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new OSCConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
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
        final int bankPageSize = this.configuration.getBankPageSize ();

        final ModelSetup ms = new ModelSetup ();
        ms.enableDevice (DeviceID.FIRST_INSTRUMENT);
        ms.enableDevice (DeviceID.EQ);
        ms.setHasFlatTrackList (false);
        ms.setNumTracks (bankPageSize);
        ms.setNumScenes (bankPageSize);
        ms.setHasFlatTrackList (this.configuration.isTrackNavigationFlat ());
        ms.setNumSends (bankPageSize);
        ms.setNumDevicesInBank (bankPageSize);
        ms.setNumDeviceLayers (bankPageSize);
        ms.setNumParamPages (bankPageSize);
        ms.setNumParams (bankPageSize);
        ms.setNumMarkers (bankPageSize);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.addSettingObserver (OSCConfiguration.RECEIVE_PORT, () -> {
            try
            {
                final int receivePort = this.configuration.getReceivePort ();
                if (receivePort == this.configuration.getSendPort ())
                {
                    final String message = "Could not start OSC server. OSC send and receive port must be different! Both are: " + receivePort;
                    this.host.showNotification (message);
                    this.host.println (message);
                    return;
                }
                this.oscServer.start (receivePort);
                this.host.println ("Started OSC server on port " + receivePort + ".");
            }
            catch (final IOException ex)
            {
                this.host.error ("Could not start OSC server.", ex);
            }
        });

        final ITrackBank tb = this.model.getTrackBank ();
        tb.addSelectionObserver ( (final int index, final boolean isSelected) -> this.keyManager.clearPressedKeys ());
        tb.addNoteObserver (this.keyManager);

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.configuration.addSettingObserver (OSCConfiguration.VALUE_RESOLUTION, () -> {
            switch (this.configuration.getValueResolution ())
            {
                case LOW:
                    this.valueChanger.setUpperBound (128);
                    this.valueChanger.setStepSize (1);
                    break;
                case MEDIUM:
                    this.valueChanger.setUpperBound (1024);
                    this.valueChanger.setStepSize (8);
                    break;
                case HIGH:
                    this.valueChanger.setUpperBound (16384);
                    this.valueChanger.setStepSize (128);
                    break;
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("OSC");

        final OSCControlSurface surface = new OSCControlSurface (this.host, this.configuration, this.colorManager, input);
        surface.addTextDisplay (new DummyDisplay (this.host));

        this.surfaces.add (surface);
        this.keyManager = new KeyManager (this.model, this.model.getScales (), surface.getPadGrid ());

        // Send OSC messages
        final String sendHost = this.configuration.getSendHost ();
        final int sendPort = this.configuration.getSendPort ();
        this.host.println (String.format ("Connecting to OSC server %s:%d", sendHost, Integer.valueOf (sendPort)));
        final IOpenSoundControlClient oscClient = this.host.connectToOSCServer (sendHost, sendPort);
        this.writer = new OSCWriter (this.host, this.model, oscClient, this.configuration);

        // Receive OSC messages
        final OSCParser parser = new OSCParser (this.host, surface, this.model, this.configuration, this.writer, input, this.keyManager);

        final List<IModule> modules = new ArrayList<> ();
        modules.add (new TransportModule (this.host, this.model, surface, this.writer));
        modules.add (new GlobalModule (this.host, this.model, this.writer));
        modules.add (new LayoutModule (this.host, this.model, this.writer));
        modules.add (new MarkerModule (this.host, this.model, this.writer));
        modules.add (new ProjectModule (this.host, this.model, this.writer));
        modules.add (new TrackModule (this.host, this.model, this.writer, this.configuration));
        modules.add (new SceneModule (this.host, this.model, this.writer));
        modules.add (new DeviceModule (this.host, this.model, this.writer, this.configuration));
        modules.add (new BrowserModule (this.host, this.model, this.writer));
        modules.add (new MidiModule (this.host, this.model, surface, this.writer, this.keyManager));
        modules.add (new ActionModule (this.host, this.model, this.writer, this.configuration));
        modules.add (new ClipModule (this.host, this.model, this.writer));

        modules.forEach (module -> {
            this.writer.registerModule (module);
            parser.registerModule (module);
        });

        this.oscServer = this.host.createOSCServer (parser);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Initial flush of the whole DAW state
        this.host.scheduleTask ( () -> this.writer.flush (true), 1000);
    }
}
