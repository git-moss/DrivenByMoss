// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
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
public class MarkerMode extends BaseMode<IMarker>
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
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final IMarker marker = this.bank.getItem (index);
        if (!marker.doesExist ())
            return;

        if (this.isButtonCombination (ButtonID.DELETE))
        {
            marker.removeMarker ();
            return;
        }

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
    public void selectNextItem ()
    {
        // Since markers do not have a selected state we can only scroll the page
        this.bank.selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Since markers do not have a selected state we can only scroll the page
        this.bank.selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        display.setCell (2, 0, "Markers:");

        for (int i = 0; i < 8; i++)
        {
            if (i == 0)
                display.setBlock (0, i, EDIT_MENU[i]);
            if (i == 6)
                display.setCell (0, i, (!this.actionModeLaunch ? Push1Display.SELECT_ARROW : "") + EDIT_MENU[i]);
            if (i == 7)
                display.setCell (0, i, (this.actionModeLaunch ? Push1Display.SELECT_ARROW : "") + EDIT_MENU[i]);

            final IMarker marker = this.bank.getItem (i);
            if (marker.doesExist ())
                display.setCell (3, i, StringUtils.shortenAndFixASCII (getMarkerName (marker), 8));
        }

        display.setCell (0, 5, "Action:");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        for (int i = 0; i < 8; i++)
        {
            final IMarker marker = this.bank.getItem (i);
            final String menuTopName = EDIT_MENU[i];
            final String headerBottomName = i == 0 ? "Markers" : "";
            final String headerTopName = i == 6 ? "Action" : "";
            final boolean isMenuTopSelected = i == 6 && !this.actionModeLaunch || i == 7 && this.actionModeLaunch;
            final String menuBottomName = StringUtils.shortenAndFixASCII (getMarkerName (marker), 12);
            display.addOptionElement (headerTopName, menuTopName, isMenuTopSelected, null, headerBottomName, menuBottomName, false, marker.getColor (), false);
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
            return EDIT_MENU[index].isEmpty () ? AbstractFeatureGroup.BUTTON_COLOR_OFF : AbstractMode.BUTTON_COLOR2_ON;

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    private static String getMarkerName (final IMarker marker)
    {
        if (!marker.doesExist ())
            return "";
        final String name = marker.getName ();
        if (name.isBlank ())
            return "Mark " + marker.getPosition ();
        return name;
    }
}
