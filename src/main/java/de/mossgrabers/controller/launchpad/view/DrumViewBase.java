// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The base class for drum views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class DrumViewBase extends AbstractDrumView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    protected int soundOffset;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numSequencerLines The number of rows to use for the sequencer
     * @param numPlayLines The number of rows to use for playing
     */
    public DrumViewBase (final String name, final LaunchpadControlSurface surface, final IModel model, final int numSequencerLines, final int numPlayLines)
    {
        super (name, surface, model, numSequencerLines, numPlayLines, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID))
            return;

        if (this.surface.isShiftPressed ())
        {
            if (event != ButtonEvent.DOWN || !this.isActive ())
                return;

            final ITrackBank tb = this.model.getCurrentTrackBank ();
            final ITrack selectedTrack = tb.getSelectedItem ();
            if (selectedTrack != null)
            {
                final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
                this.onLowerScene (index);
            }
            return;
        }

        super.onButton (buttonID, event, velocity);
    }


    /**
     * Handle the functionality in sub-classes.
     *
     * @param index The scene index
     */
    protected void onLowerScene (final int index)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return 0;

        final int scene = ordinal - ButtonID.SCENE1.ordinal ();

        if (this.surface.isShiftPressed ())
        {
            if (ordinal <= ButtonID.SCENE4.ordinal ())
                return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
            return this.updateLowerSceneButtons (scene);
        }

        if (!this.isActive ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        return scene == 7 - this.selectedResolutionIndex ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
    }


    /**
     * Update the lower scene button LEDs.
     *
     * @param scene The scene
     * @return The color
     */
    protected int updateLowerSceneButtons (final int scene)
    {
        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }
}
