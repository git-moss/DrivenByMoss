// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ScrollStates;


/**
 * Edit project or track parameters.
 *
 * @author Jürgen Moßgraber
 */
public class UserView extends AbstractFaderView
{
    protected IParameterBank projectParameterBank;
    protected IParameterBank trackParameterBank;
    protected boolean        isProjectMode = true;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public UserView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Project/Track Parameters", surface, model);

        this.projectParameterBank = model.getProject ().getParameterBank ();
        this.trackParameterBank = model.getCursorTrack ().getParameterBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        this.surface.setupFader (index, LaunchpadColorManager.DAW_INDICATOR_COLORS.get (index).intValue (), false);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.getBank ().getItem (index).setValueImmediatly (value);
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        return this.getBank ().getItem (index).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        for (int i = 0; i < 8; i++)
            this.surface.setFaderValue (i, this.getBank ().getItem (i).getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID))
            return;
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        this.getBank ().scrollTo (index * this.getBank ().getPageSize ());
        this.bindCurrentPage ();
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        // Scene buttons not used
        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        for (int i = 0; i < 8; i++)
        {
            final ContinuousID faderID = ContinuousID.get (ContinuousID.FADER1, i);
            final IHwContinuousControl continuous = this.surface.getContinuous (faderID);
            if (continuous != null)
                continuous.bind ((IParameter) null);
        }

        this.surface.rebindGrid ();

        super.onDeactivate ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final IParameterBank parameterBank = this.getBank ();
        scrollStates.setCanScrollLeft (parameterBank.canScrollPageBackwards ());
        scrollStates.setCanScrollRight (parameterBank.canScrollPageForwards ());
        scrollStates.setCanScrollUp (!this.isProjectMode);
        scrollStates.setCanScrollDown (this.isProjectMode);
    }


    /**
     * Update the binding to the current page.
     */
    private void bindCurrentPage ()
    {
        for (int i = 0; i < 8; i++)
        {
            final ContinuousID faderID = ContinuousID.get (ContinuousID.FADER1, i);
            final IHwContinuousControl continuous = this.surface.getContinuous (faderID);
            if (continuous != null)
                continuous.bind (this.getBank ().getItem (i));
        }
    }


    /**
     * Set the project or track parameters mode.
     *
     * @param isProjectMode
     */
    public void setMode (final boolean isProjectMode)
    {
        this.isProjectMode = isProjectMode;
        this.bindCurrentPage ();
        this.notifyPage ();
    }


    /**
     * Select the previous page.
     */
    public void selectPreviousPage ()
    {
        this.getBank ().selectPreviousPage ();
        this.notifyPage ();
    }


    /**
     * Select the next page.
     */
    public void selectNextPage ()
    {
        this.getBank ().selectNextPage ();
        this.notifyPage ();
    }


    private void notifyPage ()
    {
        if (this.isProjectMode)
            this.mvHelper.notifySelectedProjectParameterPage ();
        else
            this.mvHelper.notifySelectedTrackParameterPage ();
    }


    private IParameterBank getBank ()
    {
        return this.isProjectMode ? this.projectParameterBank : this.trackParameterBank;
    }
}