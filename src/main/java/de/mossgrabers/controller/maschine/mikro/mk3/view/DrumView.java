// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.view;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration> implements PadButtons
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 0, 4);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (!this.isActive ())
            return;

        switch (index)
        {
            case 0:
                this.onOctaveDown (event);
                break;
            case 1:
                this.onOctaveUp (event);
                break;
            case 2:
                this.onOctaveDown (event);
                break;
            case 3:
                this.onOctaveUp (event);
                break;
            default:
                // Not used
                break;
        }
    }


    protected void updateScale ()
    {
        this.surface.getDisplay ().notify (this.scales.getScale ().getName ());
        this.updateNoteMapping ();
        final MaschineMikroMk3Configuration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
    }
}