// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Mode to select a view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionViewSelectMode extends BaseMode<IItem>
{
    /** The views to choose from. */
    private static final Views []  VIEWS      =
    {
        Views.SESSION,
        Views.SESSION,
        Views.SCENE_PLAY,
        null,
        null
    };

    /** The views to choose from. */
    private static final String [] VIEW_NAMES =
    {
        "Clips",
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
        super ("Session View", surface, model);
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
            case 0, 1:
                configuration.setFlipSession (index == 1);
                this.activateView (VIEWS[index]);
                break;

            case 2:
                configuration.setSceneView ();
                this.surface.getModeManager ().restore ();
                break;

            // TODO fix
            case 5, 7:
                this.surface.getModeManager ().restore ();
                configuration.toggleScenesClipMode ();
                break;

            case 6:
                this.surface.getModeManager ().setActive (Modes.MARKERS);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        display.setBlock (1, 0, "Session view (pads):");
        for (int i = 0; i < VIEWS.length; i++)
        {
            if (VIEWS[i] != null)
                display.setCell (3, i, (this.isSelected (viewManager, i) ? Push1Display.SELECT_ARROW : "") + VIEW_NAMES[i]);
        }
        display.setBlock (1, 3, "Session mode (display):");
        display.setCell (3, 6, "Markers");
        display.setCell (3, 7, (this.surface.getConfiguration ().isScenesClipViewSelected () ? Push1Display.SELECT_ARROW : "") + " Clips");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        for (int i = 0; i < VIEWS.length; i++)
        {
            final boolean isMenuBottomSelected = VIEWS[i] != null && this.isSelected (viewManager, i);
            display.addOptionElement (i == 0 ? "Session configuration" : "", "", false, i == 0 ? "Pads are" : "", VIEW_NAMES[i], isMenuBottomSelected, false);
        }
        final boolean scenesClipViewSelected = this.surface.getConfiguration ().isScenesClipViewSelected ();
        display.addOptionElement ("", "", false, "Display shows", "Scenes/Clips", scenesClipViewSelected, false);
        display.addOptionElement ("", "", false, "", "Markers", this.surface.getModeManager ().isActiveIgnoreTemporary (Modes.MARKERS), false);
        display.addOptionElement ("", "", false, "", "Mixer", !scenesClipViewSelected, false);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            if (index < VIEWS.length)
            {
                if (VIEWS[index] == null)
                    return AbstractFeatureGroup.BUTTON_COLOR_OFF;
                final ViewManager viewManager = this.surface.getViewManager ();
                return this.isSelected (viewManager, index) ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
            }

            if (index == 6)
                return AbstractFeatureGroup.BUTTON_COLOR_ON;
            if (index == 7)
                return this.surface.getConfiguration ().isScenesClipViewSelected () ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;
        }

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    private void activateView (final Views viewID)
    {
        if (viewID == null)
            return;
        this.surface.getViewManager ().setActive (viewID);
        this.surface.getModeManager ().restore ();
    }


    private boolean isSelected (final ViewManager viewManager, final int index)
    {
        final boolean activeView = viewManager.isActive (VIEWS[index]);
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