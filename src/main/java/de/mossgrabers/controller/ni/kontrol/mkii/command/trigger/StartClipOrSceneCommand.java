// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.command.trigger;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.clip.StartClipCommand;
import de.mossgrabers.framework.command.trigger.clip.StartSceneCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to start the currently selected clip or scene depending on the configuration setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StartClipOrSceneCommand extends AbstractTriggerCommand<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    private final StartClipCommand<KontrolProtocolControlSurface, KontrolProtocolConfiguration>  clipCommand;
    private final StartSceneCommand<KontrolProtocolControlSurface, KontrolProtocolConfiguration> sceneCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StartClipOrSceneCommand (final IModel model, final KontrolProtocolControlSurface surface)
    {
        super (model, surface);

        this.clipCommand = new StartClipCommand<> (model, surface);
        this.sceneCommand = new StartSceneCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.getModeManager ().isActive (Modes.VOLUME))
        {
            if (this.surface.getConfiguration ().isFlipClipSceneNavigation ())
                this.sceneCommand.execute (event, velocity);
            else
                this.clipCommand.execute (event, velocity);
            return;
        }

        // Parameters mode
        if (event == ButtonEvent.DOWN)
            this.model.getCursorDevice ().toggleWindowOpen ();
    }
}
