// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Editing of accent parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkersMode extends BaseMode
{
    private static final String [] EDIT_MENU = new String []
    {
        "Add",
        "",
        "",
        "",
        "",
        "",
        "",
        "Launch",
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MarkersMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final IMarker marker = this.model.getMarkerBank ().getItem (index);
        if (marker.doesExist ())
            marker.launch (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        switch (index)
        {
            case 0:
                this.model.getMarkerBank ().addMarker ();
                break;
            case 7:
                // Toggle launch
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
        final Display d = this.surface.getDisplay ().clear ();

        final boolean canEditMarkers = this.model.getHost ().canEditMarkers ();
        final IMarkerBank markerBank = this.model.getMarkerBank ();

        d.setCell (2, 0, "Markers:");

        for (int i = 0; i < 8; i++)
        {
            if (canEditMarkers)
                d.setCell (0, i, EDIT_MENU[i]);

            final IMarker marker = markerBank.getItem (i);
            if (marker.doesExist ())
                d.setCell (3, i, StringUtils.shortenAndFixASCII (marker.getName (), 8));
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final boolean canEditMarkers = this.model.getHost ().canEditMarkers ();
        final IMarkerBank markerBank = this.model.getMarkerBank ();
        for (int i = 0; i < 8; i++)
        {
            final IMarker marker = markerBank.getItem (i);
            message.addOptionElement ("", canEditMarkers ? EDIT_MENU[i] : "", false, null, i == 0 ? "Markers" : "", marker.doesExist () ? marker.getName (12) : "", false, marker.getColor (), false);
        }
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final IMarkerBank markerBank = this.model.getMarkerBank ();
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (20 + i, markerBank.getItem (i).doesExist () ? AbstractMode.BUTTON_COLOR_ON : AbstractMode.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final boolean canEditMarkers = this.model.getHost ().canEditMarkers ();
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (102 + i, canEditMarkers && !EDIT_MENU[i].isEmpty () ? AbstractMode.BUTTON_COLOR2_ON : AbstractMode.BUTTON_COLOR_OFF);
    }
}
