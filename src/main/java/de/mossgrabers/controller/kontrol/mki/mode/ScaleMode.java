// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.mode;

import de.mossgrabers.controller.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1Colors;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;


/**
 * Scales mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScaleMode extends AbstractKontrol1Mode
{
    final Scales scales;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ScaleMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super ("Scales", surface, model);
        this.isTemporary = true;
        this.scales = this.model.getScales ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Kontrol1Display d = (Kontrol1Display) this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 0, "SCALE");
        d.setCell (0, 1, "SCALE").setCell (1, 1, this.scales.getScale ().getName ().toUpperCase ());
        d.setCell (0, 2, "BASE").setCell (1, 2, Scales.BASES[this.scales.getScaleOffset ()]);
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
                this.updateScalePreferences ();
                break;

            case 1:
                if (isInc)
                    this.scales.setScaleOffset (this.scales.getScaleOffset () + 1);
                else
                    this.scales.setScaleOffset (this.scales.getScaleOffset () - 1);
                this.updateScalePreferences ();
                break;

            default:
                // Not used
                break;
        }
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
        this.surface.getModeManager ().restoreMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void onEnter ()
    {
        this.surface.getModeManager ().restoreMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final boolean canScrollLeft = this.scales.hasPrevScale ();
        final boolean canScrollRight = this.scales.hasNextScale ();
        final boolean canScrollUp = this.scales.hasNextScaleOffset ();
        final boolean canScrollDown = this.scales.hasPrevScaleOffset ();

        this.surface.updateTrigger (Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT, canScrollLeft ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateTrigger (Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT, canScrollRight ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateTrigger (Kontrol1ControlSurface.BUTTON_NAVIGATE_UP, canScrollUp ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateTrigger (Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN, canScrollDown ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);

        this.surface.updateTrigger (Kontrol1ControlSurface.BUTTON_BACK, Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateTrigger (Kontrol1ControlSurface.BUTTON_ENTER, Kontrol1Colors.BUTTON_STATE_ON);

        this.surface.updateTrigger (Kontrol1ControlSurface.BUTTON_BROWSE, Kontrol1Colors.BUTTON_STATE_ON);

    }


    private void updateScalePreferences ()
    {
        final Kontrol1Configuration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES[this.scales.getScaleOffset ()]);
    }
}