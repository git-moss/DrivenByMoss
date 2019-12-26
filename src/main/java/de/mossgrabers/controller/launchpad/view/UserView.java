// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Edit user parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserView extends AbstractFaderView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public UserView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
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
        this.model.getUserParameterBank ().getItem (index).setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        return this.model.getUserParameterBank ().getItem (index).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IParameterBank userParameterBank = this.model.getUserParameterBank ();
        for (int i = 0; i < 8; i++)
            this.surface.setFaderValue (i, userParameterBank.getItem (i).getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event)
    {
        if (!ButtonID.isSceneButton (buttonID))
            return;
        final IParameterBank userParameterBank = this.model.getUserParameterBank ();
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        userParameterBank.scrollTo (index * userParameterBank.getPageSize ());
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final IParameterBank userParameterBank = this.model.getUserParameterBank ();
        final int page = userParameterBank.getScrollPosition () / userParameterBank.getPageSize ();
        return page == scene ? LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }
}