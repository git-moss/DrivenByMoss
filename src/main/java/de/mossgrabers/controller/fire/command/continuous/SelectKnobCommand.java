// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.command.continuous;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.view.IFireView;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.view.View;


/**
 * The Select button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectKnobCommand extends AbstractContinuousCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectKnobCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (this.surface.isPressed (ButtonID.ALT))
        {
            this.handleTrackSelection (value);
            return;
        }

        final View activeView = this.surface.getViewManager ().getActiveView ();
        if (activeView instanceof IFireView)
            ((IFireView) activeView).onSelectKnobValue (value);
    }


    private void handleTrackSelection (final int value)
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        if (this.model.getValueChanger ().calcKnobSpeed (value) > 0)
        {
            if (this.surface.isPressed (ButtonID.SELECT))
            {
                if (trackBank.canScrollPageForwards ())
                {
                    trackBank.selectNextPage ();
                    return;
                }

                final int positionOfLastItem = trackBank.getPositionOfLastItem ();
                if (positionOfLastItem >= 0)
                {
                    final int index = positionOfLastItem % trackBank.getPageSize ();
                    final ITrack lastItem = trackBank.getItem (index);
                    if (!lastItem.isSelected ())
                        lastItem.select ();
                }
                return;
            }
            trackBank.selectNextItem ();
            return;
        }

        if (this.surface.isPressed (ButtonID.SELECT))
        {
            if (trackBank.canScrollPageBackwards ())
            {
                trackBank.selectPreviousPage ();
                return;
            }

            final ITrack firstItem = trackBank.getItem (0);
            if (!firstItem.isSelected ())
                firstItem.select ();
            return;
        }

        trackBank.selectPreviousItem ();
    }
}
