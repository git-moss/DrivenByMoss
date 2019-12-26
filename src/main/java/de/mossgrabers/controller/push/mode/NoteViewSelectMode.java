// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Mode to select a view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteViewSelectMode extends BaseMode
{
    /** The views to choose from. */
    private static final Views [] VIEWS     =
    {
        Views.PLAY,
        Views.PIANO,
        Views.DRUM64,
        null,
        null,
        null,
        Views.CLIP,
        Views.PRG_CHANGE
    };

    /** More views to choose from. */
    private static final Views [] VIEWS_TOP =
    {
        Views.SEQUENCER,
        Views.POLY_SEQUENCER,
        Views.RAINDROPS,
        Views.DRUM,
        Views.DRUM4,
        Views.DRUM8,
        null,
        null
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteViewSelectMode (final PushControlSurface surface, final IModel model)
    {
        super ("Note View Select", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.activateView (VIEWS[index]);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.activateView (VIEWS_TOP[index]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        display.setBlock (1, 0, "Sequence:");
        display.setBlock (2, 0, "Play:");
        display.setBlock (2, 3, "Tools:");

        for (int i = 0; i < VIEWS.length; i++)
        {
            if (VIEWS[i] != null)
            {
                final View view = viewManager.getView (VIEWS[i]);
                display.setCell (3, i, view == null ? "" : (viewManager.isActiveView (VIEWS[i]) ? Push1Display.SELECT_ARROW : "") + StringUtils.optimizeName (view.getName (), 8));
            }
            if (VIEWS_TOP[i] != null)
            {
                final View view = viewManager.getView (VIEWS_TOP[i]);
                display.setCell (0, i, view == null ? "" : (viewManager.isActiveView (VIEWS_TOP[i]) ? Push1Display.SELECT_ARROW : "") + StringUtils.optimizeName (view.getName (), 8));
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        for (int i = 0; i < VIEWS.length; i++)
        {
            String menuBottomName = "";
            if (VIEWS[i] != null)
            {
                final View view = viewManager.getView (VIEWS[i]);
                if (view != null)
                    menuBottomName = view.getName ();
            }
            final String menuTopName = VIEWS_TOP[i] == null ? "" : viewManager.getView (VIEWS_TOP[i]).getName ();
            final boolean isMenuBottomSelected = VIEWS[i] != null && viewManager.isActiveView (VIEWS[i]);
            final boolean isMenuTopSelected = VIEWS_TOP[i] != null && viewManager.isActiveView (VIEWS_TOP[i]);
            String titleBottom = "";
            String titleTop = "";
            if (i == 0)
            {
                titleTop = "Sequence";
                titleBottom = "Play";
            }
            else if (i == 6)
                titleBottom = "Tools";
            display.addOptionElement (titleTop, menuTopName, isMenuTopSelected, titleBottom, menuBottomName, isMenuBottomSelected, false);
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            if (VIEWS[index] == null)
                return AbstractMode.BUTTON_COLOR_OFF;
            return viewManager.isActiveView (VIEWS[index]) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            if (VIEWS_TOP[index] == null)
                return AbstractMode.BUTTON_COLOR_OFF;
            return viewManager.isActiveView (VIEWS_TOP[index]) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON;
        }

        return AbstractMode.BUTTON_COLOR_OFF;
    }


    private void activateView (final Views viewID)
    {
        if (viewID == null)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.getView (viewID) == null)
            return;
        viewManager.setActiveView (viewID);

        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack != null)
            viewManager.setPreferredView (selectedTrack.getPosition (), viewID);
        this.surface.getModeManager ().restoreMode ();
    }
}