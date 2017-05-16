package de.mossgrabers.launchpad.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * The base class for track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AbstractTrackMode extends AbstractMode<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public AbstractTrackMode (final LaunchpadControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
