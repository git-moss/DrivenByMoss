// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.controller.push.view.Views;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;


/**
 * Mode to select a view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionViewSelectMode extends BaseMode
{
    /** The views to choose from. */
    private static final Integer [] VIEWS      =
    {
        Views.VIEW_SESSION,
        Views.VIEW_SESSION,
        Views.VIEW_SCENE_PLAY,
        null,
        null
    };

    /** The views to choose from. */
    private static final String []  VIEW_NAMES =
    {
        "Session",
        "Flipped",
        "Scenes",
        "",
        ""
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SessionViewSelectMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final PushConfiguration configuration = this.surface.getConfiguration ();

        switch (index)
        {
            case 0:
            case 1:
                configuration.setFlipSession (index == 1);
                this.activateView (VIEWS[index]);
                break;

            case 2:
                configuration.setSceneView ();
                this.surface.getModeManager ().restoreMode ();
                break;

            case 7:
                configuration.toggleScenesClipMode ();
                this.surface.getModeManager ().restoreMode ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ViewManager viewManager = this.surface.getViewManager ();
        d.clear ().setBlock (1, 0, "Session view:");
        for (int i = 0; i < VIEWS.length; i++)
        {
            if (VIEWS[i] != null)
                d.setCell (3, i, (this.isSelected (viewManager, i) ? PushDisplay.RIGHT_ARROW : "") + VIEW_NAMES[i]);
        }
        d.setBlock (1, 3, "Display scenes or");
        d.setCell (2, 6, "clips:");
        final boolean isOn = this.surface.getModeManager ().isActiveMode (Modes.MODE_SESSION);
        d.setCell (3, 7, isOn ? "  On" : "  Off");
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        for (int i = 0; i < VIEWS.length; i++)
        {
            final boolean isMenuBottomSelected = VIEWS[i] != null && this.isSelected (viewManager, i);
            message.addOptionElement ("", "", false, i == 0 ? "Session view" : "", VIEW_NAMES[i], isMenuBottomSelected, false);
        }
        final boolean isOn = this.surface.getModeManager ().isActiveMode (Modes.MODE_SESSION);
        message.addOptionElement ("", "", false, "                         Display scenes/clips", "", false, false);
        message.addOptionElement ("", "", false, "", "", false, false);
        message.addOptionElement ("", "", false, "", isOn ? "On" : "Off", isOn, false);
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final ViewManager viewManager = this.surface.getViewManager ();
        for (int i = 0; i < VIEWS.length; i++)
            this.surface.updateButton (20 + i, colorManager.getColor (VIEWS[i] == null ? AbstractMode.BUTTON_COLOR_OFF : this.isSelected (viewManager, i) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));

        this.surface.updateButton (25, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (26, AbstractMode.BUTTON_COLOR_OFF);
        final boolean isOn = this.surface.getModeManager ().isActiveMode (Modes.MODE_SESSION);
        this.surface.updateButton (27, isOn ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
    }


    private void activateView (final Integer viewID)
    {
        if (viewID == null)
            return;
        this.surface.getViewManager ().setActiveView (viewID);
        this.surface.getModeManager ().restoreMode ();
    }


    private boolean isSelected (final ViewManager viewManager, final int index)
    {
        final boolean activeView = viewManager.isActiveView (VIEWS[index]);
        switch (index)
        {
            case 0:
                return activeView && !this.surface.getConfiguration ().isFlipSession ();

            case 1:
                return activeView && this.surface.getConfiguration ().isFlipSession ();

            default:
                return activeView;
        }
    }
}