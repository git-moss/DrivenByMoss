// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.mode;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisColorManager;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;


/**
 * The track mode for the Exquis.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisParameterMode extends ParameterMode<ExquisControlSurface, ExquisConfiguration> implements IExquisMode
{
    private static final int []          PARAM_COLORS =
    {

        DAWColor.DAW_COLOR_RED.ordinal (),
        DAWColor.DAW_COLOR_ORANGE.ordinal (),
        DAWColor.DAW_COLOR_LIGHT_ORANGE.ordinal (),
        DAWColor.DAW_COLOR_GREEN.ordinal ()
    };

    private final ExquisFourKnobProvider exquisParameterProvider;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ExquisParameterMode (final ExquisControlSurface surface, final IModel model)
    {
        super (surface, model, false);

        this.setControls (ExquisControlSurface.KNOBS);

        this.exquisParameterProvider = new ExquisFourKnobProvider (surface, new BankParameterProvider (this.cursorDevice.getParameterBank ()));
        this.setParameterProvider (this.exquisParameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobColor (final int index)
    {
        return ExquisColorManager.FIRST_DAW_COLOR_INDEX + PARAM_COLORS[index];
    }


    /** {@inheritDoc} */
    @Override
    public void toggleParameters ()
    {
        this.exquisParameterProvider.toggle ();
        this.bindControls ();
    }


    /**
     * Check if parameters 1..4 or 5..8 are currently bound.
     *
     * @return True if parameters 1..4 are bound
     */
    public boolean are1To4Bound ()
    {
        return this.exquisParameterProvider.are1To4Bound ();
    }
}
