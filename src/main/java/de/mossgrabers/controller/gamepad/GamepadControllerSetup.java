// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.gamepad;

import de.mossgrabers.controller.gamepad.controller.GamepadControlSurface;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;

import com.studiohartman.jamepad.ControllerManager;


/**
 * Support for Gamepad controllers.
 *
 * @author Jürgen Moßgraber
 */
public class GamepadControllerSetup extends AbstractControllerSetup<GamepadControlSurface, GamepadConfiguration>
{
    private final ControllerManager gamepadManager;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public GamepadControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.gamepadManager = new ControllerManager ();
        this.gamepadManager.initSDLGamepad ();

        // This is currently not used but necessary to prevent crashes with parameters
        this.valueChanger = new TwosComplementValueChanger (1024, 10);

        this.configuration = new GamepadConfiguration (host, this.valueChanger, factory.getArpeggiatorModes (), this.gamepadManager);
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableMainDrumDevice (false);
        ms.setNumTracks (1);
        ms.setNumScenes (100);
        ms.setNumSends (0);
        ms.setNumDevicesInBank (0);
        ms.setNumDeviceLayers (0);
        ms.setNumParamPages (0);
        ms.setNumParams (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumFilterColumnEntries (0);
        ms.setNumResults (0);
        ms.setNumUserPageSize (0);
        ms.setNumUserPages (0);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("Gamepad");
        this.surfaces.add (new GamepadControlSurface (this.host, this.configuration, this.colorManager, input, this.gamepadManager, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.addSettingObserver (GamepadConfiguration.SELECTED_GAMEPAD, () -> {

            this.getSurface ().selectGamepad (this.configuration.getSelectedGamepad ());

        });

        this.createNoteRepeatObservers (this.configuration, this.getSurface ());
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        super.exit ();

        this.gamepadManager.quitSDLGamepad ();
    }
}
