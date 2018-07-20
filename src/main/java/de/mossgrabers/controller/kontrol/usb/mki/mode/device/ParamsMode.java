// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.mode.device;

import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1Display;
import de.mossgrabers.controller.kontrol.usb.mki.mode.AbstractKontrol1Mode;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Edit parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParamsMode extends AbstractKontrol1Mode
{
    private static final Set<Character> ILLEGAL_LOWER_CHARS = new HashSet<> ();
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
            d.setCell (0, 3, "  PLEASE").setCell (0, 4, "SELECT A").setCell (0, 5, "DEVICE").allDone ();
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCursorDevice ().changeParameter (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean canScrollLeft = cursorDevice.hasPreviousParameterPage ();
        final boolean canScrollRight = cursorDevice.hasNextParameterPage ();
        final boolean canScrollUp = cursorDevice.canSelectNextFX ();
        final boolean canScrollDown = cursorDevice.canSelectPreviousFX ();

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT, canScrollLeft ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT, canScrollRight ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_UP, canScrollUp ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN, canScrollDown ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_BACK, cursorDevice.isEnabled () ? Kontrol1ControlSurface.BUTTON_STATE_ON : Kontrol1ControlSurface.BUTTON_STATE_OFF);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_ENTER, cursorDevice.isParameterPageSectionVisible () ? Kontrol1ControlSurface.BUTTON_STATE_ON : Kontrol1ControlSurface.BUTTON_STATE_OFF);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_BROWSE, Kontrol1ControlSurface.BUTTON_STATE_ON);

    }


    /** {@inheritDoc} */
    @Override
    public void scrollLeft ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().previousParameterPageBank ();
        else
            this.model.getCursorDevice ().previousParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollRight ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().nextParameterPageBank ();
        else
            this.model.getCursorDevice ().nextParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollUp ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().getDeviceBank ().scrollPageForwards ();
        else
            this.model.getCursorDevice ().selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDown ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().getDeviceBank ().scrollPageBackwards ();
        else
            this.model.getCursorDevice ().selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void onMainKnobPressed ()
    {
        this.model.getCursorDevice ().toggleWindowOpen ();
    }


    /** {@inheritDoc} */
    @Override
    public void onBack ()
    {
        this.model.getCursorDevice ().toggleEnabledState ();
    }


    /** {@inheritDoc} */
    @Override
    public void onEnter ()
    {
        this.model.getCursorDevice ().toggleParameterPageSectionVisible ();
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