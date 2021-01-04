// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.command.trigger;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.command.continuous.TouchstripCommand;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for toggling the ribbon mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RibbonCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    private final int [] modes;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modes The modes to toggle
     */
    public RibbonCommand (final IModel model, final MaschineControlSurface surface, final int... modes)
    {
        super (model, surface);
        this.modes = modes;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final MaschineConfiguration configuration = this.surface.getConfiguration ();
        final int ribbonMode = configuration.getRibbonMode ();

        int m = this.modes[0];

        for (int i = 0; i < this.modes.length; i++)
        {
            if (this.modes[i] == ribbonMode)
            {
                if (i + 1 < this.modes.length)
                    m = this.modes[i + 1];
                break;
            }
        }

        this.surface.getDisplay ().notify (MaschineConfiguration.RIBBON_MODE_VALUES[m]);
        configuration.setRibbonMode (m);

        ((TouchstripCommand) this.surface.getContinuous (ContinuousID.CROSSFADER).getTouchCommand ()).resetRibbonValue (m);
    }
}
