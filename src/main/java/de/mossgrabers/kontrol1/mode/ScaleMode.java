// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.kontrol1.Kontrol1Configuration;
import de.mossgrabers.kontrol1.controller.Kontrol1ControlSurface;
import de.mossgrabers.kontrol1.controller.Kontrol1Display;


/**
 * Mixes colors mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScaleMode extends AbstractMode<Kontrol1ControlSurface, Kontrol1Configuration>
{
    final Scales scales;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ScaleMode (final Kontrol1ControlSurface surface, final Model model)
    {
        super (surface, model);
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
        final Kontrol1Configuration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES[this.scales.getScaleOffset ()]);
        config.setScaleInKey (!this.scales.isChromatic ());
    }
}