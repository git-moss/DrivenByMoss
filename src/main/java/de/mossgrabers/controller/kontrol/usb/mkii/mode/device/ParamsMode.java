// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.mode.device;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Edit parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParamsMode extends AbstractMode<Kontrol2ControlSurface, Kontrol2Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ParamsMode (final Kontrol2ControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        // TODO Implement parameter display
        // final Kontrol2Display d = (Kontrol2Display) this.surface.getDisplay ();
        // d.clear ();
        //
        // if (this.model.hasSelectedDevice ())
        // {
        // final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        // d.setCell (0, 0, cursorDevice.getName (8).toUpperCase ()).setCell (1, 0,
        // cursorDevice.getSelectedParameterPageName ().toUpperCase ());
        //
        // for (int i = 0; i < 8; i++)
        // {
        // final IParameter p = cursorDevice.getFXParam (i);
        // final String name = p.getName (8).toUpperCase ();
        // if (!name.isEmpty ())
        // d.setCell (0, 1 + i, name).setCell (1, 1 + i, checkForUpperCase (p.getDisplayedValue
        // (8)));
        //
        // d.setBar (1 + i, this.surface.isPressed (Kontrol2ControlSurface.TOUCH_ENCODER_1 + i) &&
        // p.doesExist (), p.getValue ());
        // }
        // }
        // else
        // {
        // d.setCell (0, 3, " PLEASE").setCell (0, 4, "SELECT A").setCell (0, 5, "DEVICE").allDone
        // ();
        // }
        // d.allDone ();
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
}