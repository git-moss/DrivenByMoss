// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.mode.device;

import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1Display;
import de.mossgrabers.controller.ni.kontrol.mki.mode.AbstractKontrol1Mode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;


/**
 * Edit parameters mode.
 *
 * @author Jürgen Moßgraber
 */
public class ParamsMode extends AbstractKontrol1Mode<IParameter>
{
    private static final Set<Character> ILLEGAL_LOWER_CHARS = Set.of (Character.valueOf ('a'), Character.valueOf ('e'), Character.valueOf ('g'), Character.valueOf ('j'), Character.valueOf ('k'), Character.valueOf ('p'), Character.valueOf ('q'), Character.valueOf ('r'), Character.valueOf ('s'), Character.valueOf ('x'), Character.valueOf ('y'), Character.valueOf ('z'));


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ParamsMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super (Modes.NAME_PARAMETERS, surface, model, model.getCursorDevice ().getParameterBank ());

        this.setParameterProvider (new BankParameterProvider (this.bank));
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
            d.setCell (0, 0, cursorDevice.getName (8).toUpperCase (Locale.US));

            final Optional<String> selectedItem = cursorDevice.getParameterPageBank ().getSelectedItem ();
            if (selectedItem.isPresent ())
                d.setCell (1, 0, selectedItem.get ().toUpperCase (Locale.US));

            for (int i = 0; i < 8; i++)
            {
                final IParameter p = this.bank.getItem (i);
                final String name = StringUtils.shortenAndFixASCII (p.getName (8), 8).toUpperCase (Locale.US);
                if (!name.isEmpty ())
                    d.setCell (0, 1 + i, name).setCell (1, 1 + i, checkForUpperCase (StringUtils.shortenAndFixASCII (p.getDisplayedValue (8), 8)));

                d.setBar (1 + i, this.surface.getContinuous (ContinuousID.get (ContinuousID.KNOB1, i)).isTouched () && p.doesExist (), p.getValue ());
            }
        }
        else
            d.setCell (0, 3, "  PLEASE").setCell (0, 4, "SELECT A").setCell (0, 5, "DEVICE").allDone ();
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();

        switch (buttonID)
        {
            case MUTE:
                return cursorDevice.isEnabled () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON;
            case SOLO:
                return cursorDevice.isParameterPageSectionVisible () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON;
            case BROWSE:
                return ColorManager.BUTTON_STATE_ON;
            default:
                return ColorManager.BUTTON_STATE_OFF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().getParameterPageBank ().scrollBackwards ();
        else
            this.bank.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().getParameterPageBank ().scrollForwards ();
        else
            this.bank.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().getDeviceBank ().selectPreviousPage ();
        else
            this.model.getCursorDevice ().selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.surface.isShiftPressed ())
            this.model.getCursorDevice ().getDeviceBank ().selectNextPage ();
        else
            this.model.getCursorDevice ().selectNext ();
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
                return displayedValue.toUpperCase (Locale.US);
        }
        return displayedValue;
    }
}