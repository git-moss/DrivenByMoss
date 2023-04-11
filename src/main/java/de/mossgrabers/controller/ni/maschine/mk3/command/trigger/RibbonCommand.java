// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.core.RibbonMode;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.command.continuous.TouchstripCommand;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;


/**
 * Command for toggling the ribbon mode.
 *
 * @author Jürgen Moßgraber
 */
public class RibbonCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    private final List<RibbonMode> modes;
    private RibbonMode             activeMode;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modes The modes to toggle
     */
    public RibbonCommand (final IModel model, final MaschineControlSurface surface, final List<RibbonMode> modes)
    {
        super (model, surface);

        this.modes = modes;
        this.activeMode = modes.get (0);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final MaschineConfiguration configuration = this.surface.getConfiguration ();
        final RibbonMode ribbonMode = configuration.getRibbonMode ();

        RibbonMode m = this.activeMode;

        // If the current mode is part of these modes select the next one
        int index = this.modes.indexOf (ribbonMode);
        if (index >= 0)
        {
            index++;
            if (index >= this.modes.size ())
                index = 0;
            m = this.modes.get (index);
        }
        this.activeMode = m;

        this.surface.getDisplay ().notify (m.getName ());
        configuration.setRibbonMode (m);

        ((TouchstripCommand) this.surface.getContinuous (ContinuousID.CROSSFADER).getTouchCommand ()).resetRibbonValue (m);
    }
}
