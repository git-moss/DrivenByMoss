// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.mode;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mixes colors mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScaleMode extends AbstractMode<Kontrol2ControlSurface, Kontrol2Configuration>
{
    final Scales scales;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ScaleMode (final Kontrol2ControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = true;
        this.scales = this.model.getScales ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Kontrol2Display d = (Kontrol2Display) this.surface.getDisplay ();

        d.clear ();
        d.setCell (0, 0, "SCALE");

        final Scales scales = this.model.getScales ();

        d.setCell (0, 1, "SCALE").setCell (1, 1, scales.getScale ().getName ().toUpperCase ());
        d.setCell (0, 2, "BASE").setCell (1, 2, Scales.BASES[scales.getScaleOffset ()]);
        d.setCell (0, 3, "CHROMATC").setCell (1, 3, scales.isChromatic () ? "On" : "Off");

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
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

            case 2:
                if (isInc)
                    this.scales.setChromatic (true);
                else
                    this.scales.setChromatic (false);
                this.updateScalePreferences ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    private void updateScalePreferences ()
    {
        final Kontrol2Configuration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES[this.scales.getScaleOffset ()]);
        config.setScaleInKey (!this.scales.isChromatic ());
    }
}