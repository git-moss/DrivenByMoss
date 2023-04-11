// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.AbstractDrum64View;


/**
 * The Drum 64 view.
 *
 * @author Jürgen Moßgraber
 */
public class Drum64View extends AbstractDrum64View<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum64View (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleDeleteButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (ButtonID.DELETE);
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        this.model.getNoteClip (8, 128).clearRow (editMidiChannel, this.offsetY + playedPad);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        return ColorManager.BUTTON_STATE_OFF;
    }
}