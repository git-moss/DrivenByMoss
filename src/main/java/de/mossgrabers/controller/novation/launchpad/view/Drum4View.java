// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.view.AbstractDrum4View;


/**
 * The 4 lane drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Drum4View extends AbstractDrum4View<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum4View (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleNoteAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity, final int accentVelocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.UP : ButtonID.DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, channel, step, note, velocity, isUpPressed);
            return true;
        }

        return super.handleNoteAreaButtonCombinations (clip, channel, step, row, note, velocity, accentVelocity);
    }
}