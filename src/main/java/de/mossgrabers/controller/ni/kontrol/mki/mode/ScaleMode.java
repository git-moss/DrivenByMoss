// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.mode;

import java.util.Locale;

import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1Display;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.scale.Scales;


/**
 * Scales mode.
 *
 * @author Jürgen Moßgraber
 */
public class ScaleMode extends AbstractKontrol1Mode<IItem>
{
    private final Scales scales;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ScaleMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super ("Scales", surface, model);

        this.scales = this.model.getScales ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Kontrol1Display d = (Kontrol1Display) this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 0, "SCALE");
        d.setCell (0, 1, "SCALE").setCell (1, 1, this.scales.getScale ().getName ().toUpperCase (Locale.US));
        d.setCell (0, 2, "BASE").setCell (1, 2, Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final boolean isInc = value <= 63;

        switch (index)
        {
            case 0:
                if (isInc)
                    this.scales.nextScale ();
                else
                    this.scales.prevScale ();
                break;

            case 1:
                if (isInc)
                    this.scales.setScaleOffsetByIndex (this.scales.getScaleOffsetIndex () + 1);
                else
                    this.scales.setScaleOffsetByIndex (this.scales.getScaleOffsetIndex () - 1);
                break;

            default:
                // Not used
                return;
        }

        this.updateScalePreferences ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.scales.prevScale ();
        this.updateScalePreferences ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.scales.nextScale ();
        this.updateScalePreferences ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.scales.prevScaleOffset ();
        this.updateScalePreferences ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.scales.nextScaleOffset ();
        this.updateScalePreferences ();
    }


    /** {@inheritDoc} */
    @Override
    public void onBack ()
    {
        this.surface.getModeManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public void onEnter ()
    {
        this.surface.getModeManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case MUTE, SOLO, BROWSE:
                return ColorManager.BUTTON_STATE_ON;
            default:
                return ColorManager.BUTTON_STATE_OFF;
        }
    }


    private void updateScalePreferences ()
    {
        this.scales.updateScaleProperties (this.surface.getConfiguration ());
    }
}