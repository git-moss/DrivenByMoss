// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Editing of accent parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerMode extends BaseMode
{
    private static final String [] EDIT_MENU        =
    {
        "Add Marker",
        "",
        "",
        "",
        "",
        "",
        "Select",
        "Launch",
    };

    private final boolean          canEditMarkers;
    private boolean                actionModeLaunch = true;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MarkerMode (final PushControlSurface surface, final IModel model)
    {
        super ("Marker", surface, model, model.getMarkerBank ());

        this.canEditMarkers = model.getHost ().supports (Capability.MARKERS);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final IMarker marker = (IMarker) this.bank.getItem (index);
        if (!marker.doesExist ())
            return;

        if (this.actionModeLaunch)
            marker.launch (true);
        else
            marker.select ();
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
                ((IMarkerBank) this.bank).addMarker ();
                break;
            case 6:
                this.actionModeLaunch = false;
                break;
            case 7:
                this.actionModeLaunch = true;
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
        display.setCell (2, 0, "Markers:");

        for (int i = 0; i < 8; i++)
        {
            if (i == 6)
                display.setCell (0, i, (!this.actionModeLaunch ? Push1Display.SELECT_ARROW : "") + EDIT_MENU[i]);
            if (i == 7)
                display.setCell (0, i, (this.actionModeLaunch ? Push1Display.SELECT_ARROW : "") + EDIT_MENU[i]);

            final IMarker marker = (IMarker) this.bank.getItem (i);
            if (marker.doesExist ())
                display.setCell (3, i, StringUtils.shortenAndFixASCII (marker.getName (), 8));
        }

        display.setCell (0, 5, "Action:");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        for (int i = 0; i < 8; i++)
        {
            final IMarker marker = (IMarker) this.bank.getItem (i);
            final String menuTopName = this.canEditMarkers || i > 0 ? EDIT_MENU[i] : "";
            final String headerBottomName = i == 0 ? "Markers" : "";
            final String headerTopName = i == 6 ? "Action" : "";
            final boolean isMenuTopSelected = i == 6 && !this.actionModeLaunch || i == 7 && this.actionModeLaunch;
            display.addOptionElement (headerTopName, menuTopName, isMenuTopSelected, null, headerBottomName, marker.doesExist () ? marker.getName (12) : "", false, marker.getColor (), false);
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
            return this.bank.getItem (index).doesExist () ? AbstractFeatureGroup.BUTTON_COLOR_ON : AbstractFeatureGroup.BUTTON_COLOR_OFF;

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
            return (this.canEditMarkers || index > 0) && !EDIT_MENU[index].isEmpty () ? AbstractMode.BUTTON_COLOR2_ON : AbstractFeatureGroup.BUTTON_COLOR_OFF;

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }
}
