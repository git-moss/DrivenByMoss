// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.definition.LaunchpadProControllerDefinition;
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
    protected void delayedUpdateArrowButtons ()
    {
        this.surface.setTrigger (this.surface.getTriggerId (ButtonID.SESSION), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setTrigger (this.surface.getTriggerId (ButtonID.NOTE), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setTrigger (this.surface.getTriggerId (ButtonID.DEVICE), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_USER, this.model.getHost ().hasUserParameters () ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        this.surface.setupFader (index, LaunchpadColors.DAW_INDICATOR_COLORS[index], false);
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
    public void onScene (final int scene, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final IParameterBank userParameterBank = this.model.getUserParameterBank ();
        userParameterBank.scrollTo (scene * userParameterBank.getPageSize ());
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final IParameterBank userParameterBank = this.model.getUserParameterBank ();
        final int page = userParameterBank.getScrollPosition () / userParameterBank.getPageSize ();

        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, page == 0 ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, page == 1 ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, page == 2 ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, page == 3 ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, page == 4 ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, page == 5 ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, page == 6 ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        this.surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, page == 7 ? LaunchpadColors.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }
}