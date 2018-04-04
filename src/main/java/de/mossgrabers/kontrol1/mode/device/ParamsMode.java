// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.mode.device;

import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.kontrol1.Kontrol1Configuration;
import de.mossgrabers.kontrol1.controller.Kontrol1ControlSurface;
import de.mossgrabers.kontrol1.controller.Kontrol1Display;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Edit parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParamsMode extends AbstractMode<Kontrol1ControlSurface, Kontrol1Configuration>
{
    private final static Set<Character> ILLEGAL_LOWER_CHARS = new HashSet<> ();
    static
    {
        Collections.addAll (ILLEGAL_LOWER_CHARS, Character.valueOf ('a'), Character.valueOf ('e'), Character.valueOf ('g'), Character.valueOf ('j'), Character.valueOf ('k'), Character.valueOf ('p'), Character.valueOf ('q'), Character.valueOf ('r'), Character.valueOf ('s'), Character.valueOf ('x'), Character.valueOf ('y'), Character.valueOf ('z'));
    }


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ParamsMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Kontrol1Display d = (Kontrol1Display) this.surface.getDisplay ();
        d.clear ();

        if (this.model.hasSelectedDevice ())
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            d.setCell (0, 0, cursorDevice.getName (8).toUpperCase ()).setCell (1, 0, cursorDevice.getSelectedParameterPageName ().toUpperCase ());

            for (int i = 0; i < 8; i++)
            {
                final IParameter p = cursorDevice.getFXParam (i);
                final String name = p.getName (8).toUpperCase ();
                if (!name.isEmpty ())
                    d.setCell (0, 1 + i, name).setCell (1, 1 + i, checkForUpperCase (p.getDisplayedValue (8)));

                d.setBar (1 + i, this.surface.isPressed (Kontrol1ControlSurface.TOUCH_ENCODER_1 + i) && p.doesExist (), p.getValue ());
            }
        }
        else
        {
            d.setCell (0, 3, "  PLEASE").setCell (0, 4, "SELECT A").setCell (0, 5, "DEVICE").allDone ();
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCursorDevice ().changeParameter (index, value);
    }


    /**
     * Move to the previous parameter page.
     */
    public void previousPage ()
    {
        this.model.getCursorDevice ().previousParameterPage ();
    }


    /**
     * Move to the next parameter page.
     */
    public void nextPage ()
    {
        this.model.getCursorDevice ().nextParameterPage ();
    }


    /**
     * Is there a previous page?
     *
     * @return True if there is
     */
    public boolean canSelectPreviousPage ()
    {
        return this.model.getCursorDevice ().hasPreviousParameterPage ();
    }


    /**
     * Is there a next page?
     *
     * @return True if there is
     */
    public boolean canSelectNextPage ()
    {
        return this.model.getCursorDevice ().hasNextParameterPage ();
    }


    /**
     * Select the previous device or parameter page depending on the mode.
     */
    public void selectPreviousPage ()
    {
        this.model.getCursorDevice ().previousParameterPage ();
    }


    /**
     * Select the next device or parameter page depending on the mode.
     */
    public void selectNextPage ()
    {
        this.model.getCursorDevice ().nextParameterPage ();
    }


    /**
     * Select the previous device bank or parameter page bank depending on the mode.
     */
    public void selectPreviousPageBank ()
    {
        this.model.getCursorDevice ().previousParameterPageBank ();
    }


    /**
     * Select the next device bank or parameter page bank depending on the mode.
     */
    public void selectNextPageBank ()
    {
        this.model.getCursorDevice ().nextParameterPageBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    private static String checkForUpperCase (final String displayedValue)
    {
        for (int i = 0; i < displayedValue.length (); i++)
        {
            if (ILLEGAL_LOWER_CHARS.contains (Character.valueOf (displayedValue.charAt (i))))
                return displayedValue.toUpperCase ();
        }
        return displayedValue;
    }
}