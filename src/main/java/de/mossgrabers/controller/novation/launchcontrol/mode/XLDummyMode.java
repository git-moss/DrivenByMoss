package de.mossgrabers.controller.novation.launchcontrol.mode;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.DummyMode;

import java.util.List;


/**
 * Mode that does nothing. Additionally, turns off the knob LEDs.
 *
 * @author Jürgen Moßgraber
 */
public class XLDummyMode extends DummyMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration> implements IXLMode
{

    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode, each control gets
     *            assigned an empty parameter
     */
    public XLDummyMode (final LaunchControlXLControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (surface, model, controls);
    }


    /** {@inheritDoc} */
    @Override
    public void setKnobColor (final int row, final int column, final int value)
    {
        this.surface.setKnobLEDColor (row, column, 0, 0);
    }
}
