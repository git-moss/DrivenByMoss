// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.mode;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisColorManager;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.device.ProjectParamsMode;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;


/**
 * The track mode for the Exquis.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisProjectTrackParameterMode extends ProjectParamsMode<ExquisControlSurface, ExquisConfiguration> implements IExquisMode
{
    private static final int [] PARAM_COLORS =
    {
        DAWColor.DAW_COLOR_RED.ordinal (),
        DAWColor.DAW_COLOR_ORANGE.ordinal (),
        DAWColor.DAW_COLOR_LIGHT_ORANGE.ordinal (),
        DAWColor.DAW_COLOR_GREEN.ordinal ()
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isProjectParameterMode True to use this mode for project parameters otherwise track
     *            parameters
     */
    public ExquisProjectTrackParameterMode (final ExquisControlSurface surface, final IModel model, final boolean isProjectParameterMode)
    {
        super (surface, model, false, null, () -> false, false);

        this.setControls (ExquisControlSurface.KNOBS);

        this.isProjectMode = isProjectParameterMode;
        if (this.isProjectMode)
        {
            this.projectParameterProvider = new ExquisFourKnobProvider (surface, new BankParameterProvider (model.getProject ().getParameterBank ()));
            this.setParameterProvider (this.projectParameterProvider);
        }
        else
        {
            this.trackParameterProvider = new ExquisFourKnobProvider (surface, new BankParameterProvider (model.getCursorTrack ().getParameterBank ()));
            this.setParameterProvider (this.trackParameterProvider);
        }
    }


    /**
     * Check if the project or track parameters are active.
     *
     * @return True if the project parameters are active
     */
    public boolean areProjectParametersActive ()
    {
        return this.isProjectMode;
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
        ((ExquisFourKnobProvider) (this.areProjectParametersActive () ? this.projectParameterProvider : this.trackParameterProvider)).toggle ();
        this.bindControls ();
    }


    /**
     * Check if parameters 1..4 or 5..8 are currently bound.
     *
     * @return True if parameters 1..4 are bound
     */
    public boolean are1To4Bound ()
    {
        return ((ExquisFourKnobProvider) (this.areProjectParametersActive () ? this.projectParameterProvider : this.trackParameterProvider)).are1To4Bound ();
    }
}
