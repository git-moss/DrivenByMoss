// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Selects the next mode from a list. If the last element is reached it wraps around to the first.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeMultiSelectCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private List<Integer> modeIds = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modeIds The list with IDs of the modes to select
     */
    public ModeMultiSelectCommand (final Model model, final S surface, final Integer... modeIds)
    {
        super (model, surface);

        this.modeIds.addAll (Arrays.asList (modeIds));
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final Integer activeModeId = this.surface.getModeManager ().getActiveModeId ();
        int index = this.modeIds.indexOf (activeModeId) + 1;
        if (index < 0 || index >= this.modeIds.size ())
            index = 0;
        this.surface.getModeManager ().setActiveMode (this.modeIds.get (index));
    }
}
