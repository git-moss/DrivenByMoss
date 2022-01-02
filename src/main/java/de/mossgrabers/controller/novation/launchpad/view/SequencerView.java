// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;


/**
 * The sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends AbstractNoteSequencerView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SequencerView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Sequencer", surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (!this.isActive ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;

        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;

        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        return scene == 7 - this.selectedResolutionIndex ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.UP : ButtonID.DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, channel, step, note, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step, row, note, velocity);
    }
}
