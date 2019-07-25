// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.display.DisplayModel;
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
        Views.VIEW_PLAY,
        Views.VIEW_PIANO,
        Views.VIEW_DRUM64,
        null,
        null,
        null,
        Views.VIEW_CLIP,
        Views.VIEW_PRG_CHANGE
    };

    /** More views to choose from. */
    private static final Views [] VIEWS_TOP =
    {
        Views.VIEW_SEQUENCER,
        Views.VIEW_POLY_SEQUENCER,
        Views.VIEW_RAINDROPS,
        Views.VIEW_DRUM,
        Views.VIEW_DRUM4,
        Views.VIEW_DRUM8,
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
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ViewManager viewManager = this.surface.getViewManager ();

        d.clear ();
        d.setBlock (1, 0, "Sequence:");
        d.setBlock (2, 0, "Play:");
        d.setBlock (2, 3, "Tools:");

        for (int i = 0; i < VIEWS.length; i++)
        {
            if (VIEWS[i] != null)
            {
                final View view = viewManager.getView (VIEWS[i]);
                d.setCell (3, i, view == null ? "" : (viewManager.isActiveView (VIEWS[i]) ? PushDisplay.SELECT_ARROW : "") + StringUtils.optimizeName (view.getName (), 8));
            }
            if (VIEWS_TOP[i] != null)
            {
                final View view = viewManager.getView (VIEWS_TOP[i]);
                d.setCell (0, i, view == null ? "" : (viewManager.isActiveView (VIEWS_TOP[i]) ? PushDisplay.SELECT_ARROW : "") + StringUtils.optimizeName (view.getName (), 8));
            }
        }
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
            message.addOptionElement (titleTop, menuTopName, isMenuTopSelected, titleBottom, menuBottomName, isMenuBottomSelected, false);
        }
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final ViewManager viewManager = this.surface.getViewManager ();
        for (int i = 0; i < 8; i++)
            this.surface.updateTrigger (20 + i, colorManager.getColor (VIEWS[i] == null ? AbstractMode.BUTTON_COLOR_OFF : viewManager.isActiveView (VIEWS[i]) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final ViewManager viewManager = this.surface.getViewManager ();
        for (int i = 0; i < 8; i++)
            this.surface.updateTrigger (102 + i, colorManager.getColor (VIEWS_TOP[i] == null ? AbstractMode.BUTTON_COLOR_OFF : viewManager.isActiveView (VIEWS_TOP[i]) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
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