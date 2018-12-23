// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.midi.mkii.view;

import de.mossgrabers.controller.kontrol.midi.mkii.KontrolMkIIConfiguration;
import de.mossgrabers.controller.kontrol.midi.mkii.controller.KontrolMkIIControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractView;


/**
 * The view for controlling the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlView extends AbstractView<KontrolMkIIControlSurface, KontrolMkIIConfiguration>
{
    private static final int [] IDENTITY_MAP = Scales.getIdentityMatrix ();


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final KontrolMkIIControlSurface surface, final IModel model)
    {
        super ("Control", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (IDENTITY_MAP);
    }
}