// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for the Maschine Jam Solo button.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamSoloCommand extends AbstractTriggerCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamSoloCommand (final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP && this.surface.isPressed (ButtonID.SELECT))
            this.model.getProject ().clearSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
        {
            final MaschineJamConfiguration configuration = this.surface.getConfiguration ();
            configuration.nextNewClipLength ();
            this.mvHelper.delayDisplay ( () -> AbstractConfiguration.getNewClipLengthValue (configuration.getNewClipLength ()));
        }
    }
}
