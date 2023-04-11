package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.view.ShuffleView;


/**
 * Displays the current shuffle value of the DAW as a 3 digit number on the grid.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadShuffleView extends ShuffleView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param textColor1 The color of the 1st and 3rd digit
     * @param textColor2 The color of 2nd digit
     * @param backgroundColor The background color
     */
    public LaunchpadShuffleView (final LaunchpadControlSurface surface, final IModel model, final int textColor1, final int textColor2, final int backgroundColor)
    {
        super (surface, model, textColor1, textColor2, backgroundColor);
    }


    /** {@inheritDoc}} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            return;

        // Toggle shuffle on/off
        if (note == 36)
        {
            final IGroove groove = this.model.getGroove ();
            final IParameter enabledParameter = groove.getParameter (GrooveParameterID.ENABLED);
            if (enabledParameter != null)
            {
                enabledParameter.setNormalizedValue (enabledParameter.getValue () == 0 ? 1 : 0);
                return;
            }
        }

        // Shuffle rate
        if (note == 38 || note == 39)
        {
            final IGroove groove = this.model.getGroove ();
            final IParameter rateParameter = groove.getParameter (GrooveParameterID.SHUFFLE_RATE);
            if (rateParameter != null)
            {
                rateParameter.setNormalizedValue (rateParameter.getValue () == 0 ? 1 : 0);
                return;
            }
        }

        this.surface.getViewManager ().restore ();
    }


    /** {@index} */
    @Override
    protected void fillBottom ()
    {
        super.fillBottom ();

        final IGroove groove = this.model.getGroove ();
        final IParameter enabledParameter = groove.getParameter (GrooveParameterID.ENABLED);
        if (enabledParameter == null || !enabledParameter.doesExist ())
            return;

        this.padGrid.lightEx (0, 7, enabledParameter.getValue () == 0 ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI);

        final IParameter rateParameter = groove.getParameter (GrooveParameterID.SHUFFLE_RATE);
        if (rateParameter == null || !rateParameter.doesExist ())
            return;
        final boolean isEight = rateParameter.getValue () == 0;
        this.padGrid.lightEx (2, 7, isEight ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_HI);
        this.padGrid.lightEx (3, 7, !isEight ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_HI);
    }
}
