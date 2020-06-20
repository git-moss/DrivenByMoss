// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.view;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<MaschineControlSurface, MaschineConfiguration> implements PadButtons
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final MaschineControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 0, 4);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
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
        final MaschineConfiguration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
    }
}