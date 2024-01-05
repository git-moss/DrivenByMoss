// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumXoXView;


/**
 * The Drum XoX view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumXoXView extends AbstractDrumXoXView<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumXoXView (final PushControlSurface surface, final IModel model)
    {
        super (Views.NAME_DRUM_XOX, surface, model, 8);
    }
}