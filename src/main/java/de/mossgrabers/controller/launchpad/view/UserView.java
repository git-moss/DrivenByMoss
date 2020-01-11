// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Edit user parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserView extends AbstractFaderView
{
    private final IParameterBank userParameterBank;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public UserView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);

        this.userParameterBank = this.model.getUserParameterBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        this.surface.setupFader (index, LaunchpadColorManager.DAW_INDICATOR_COLORS[index], false);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.userParameterBank.getItem (index).setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        return this.userParameterBank.getItem (index).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        for (int i = 0; i < 8; i++)
            this.surface.setFaderValue (i, this.userParameterBank.getItem (i).getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event)
    {
        if (!ButtonID.isSceneButton (buttonID))
            return;
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        this.userParameterBank.scrollTo (index * this.userParameterBank.getPageSize ());
        this.bindCurrentPage ();
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final int page = this.userParameterBank.getScrollPosition () / this.userParameterBank.getPageSize ();
        return page == scene ? LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.bindCurrentPage ();
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
                continuous.bind (this.userParameterBank.getItem (i));
        }
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

        super.onDeactivate ();
    }
}