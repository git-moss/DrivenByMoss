// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to toggle between Play and Piano.
 *
 * @author Jürgen Moßgraber
 */
public class PlaySelectCommand extends AbstractFireViewMultiSelectCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PlaySelectCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface, Views.PLAY, Views.PIANO);
    }


    /** {@inheritDoc}} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.surface.isPressed (ButtonID.ALT))
        {
            this.model.getCursorClip ().quantize (1);
            this.surface.getDisplay ().notify ("Quantize");
            return;
        }

        final ITrack cursorTrack = this.model.getCursorTrack ();
        final boolean doesExist = cursorTrack.doesExist ();
        final int position = cursorTrack.getPosition ();

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActive (Views.SESSION, Views.MIX) && doesExist)
        {
            final Views preferredView = viewManager.getPreferredView (position);
            if (preferredView != null && this.viewIds.contains (preferredView))
            {
                viewManager.setActive (preferredView);
                return;
            }
        }

        super.executeNormal (event);

        if (doesExist)
            viewManager.setPreferredView (position, viewManager.getActiveID ());
    }


    /** {@inheritDoc}} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
            return;

        // Toggle through all record quantization settings...

        final RecordQuantization [] values = RecordQuantization.values ();
        final RecordQuantization recordQuantization = cursorTrack.getRecordQuantizationGrid ();
        int index = 0;
        for (int i = 0; i < values.length; i++)
        {
            if (recordQuantization == values[i])
            {
                index = i + 1;
                if (index >= values.length)
                    index = 0;
                break;
            }
        }
        cursorTrack.setRecordQuantizationGrid (values[index]);
        this.surface.getDisplay ().notify ("Rec. Quant.: " + values[index].getName ());
    }
}
