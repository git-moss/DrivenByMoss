// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.MVHelper;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.view.Views;


/**
 * Abstract implementation of a feature group.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractFeatureGroup<S extends IControlSurface<C>, C extends Configuration> implements IFeatureGroup
{
    /** Color identifier for a button which is off. */
    public static final String     BUTTON_COLOR_OFF = "BUTTON_COLOR_OFF";
    /** Color identifier for a button which is on. */
    public static final String     BUTTON_COLOR_ON  = "BUTTON_COLOR_ON";

    protected final String         name;
    protected final S              surface;
    protected final IModel         model;

    protected final ColorManager   colorManager;
    protected final MVHelper<S, C> mvHelper;


    /**
     * Constructor.
     *
     * @param name The name of the feature group
     * @param surface The surface
     * @param model The model
     */
    protected AbstractFeatureGroup (final String name, final S surface, final IModel model)
    {
        this.name = name;
        this.surface = surface;
        this.model = model;

        this.colorManager = this.model.getColorManager ();
        this.mvHelper = new MVHelper<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return this.colorManager.getColorIndex (this.getButtonColorID (buttonID));
    }


    /**
     * Get the color ID for a button, which is controlled by the feature group.
     *
     * @param buttonID The ID of the button
     * @return A color ID
     */
    protected String getButtonColorID (final ButtonID buttonID)
    {
        return BUTTON_COLOR_OFF;
    }


    /**
     * Activates the given view and stores it as the preferred view for the currently active track.
     *
     * @param viewID The ID of the view to activate and store
     */
    protected void activatePreferredView (final Views viewID)
    {
        if (viewID == null)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.get (viewID) == null)
            return;

        viewManager.setActive (viewID);
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ())
            viewManager.setPreferredView (cursorTrack.getPosition (), viewID);
    }


    /**
     * Tests if the button is pressed. If yes, the button UP event is consumed.
     *
     * @param buttonID The button to test
     * @return True if button is pressed
     */
    protected boolean isButtonCombination (final ButtonID buttonID)
    {
        if (this.surface.isPressed (buttonID))
        {
            this.surface.setTriggerConsumed (buttonID);
            return true;
        }
        return false;
    }
}
