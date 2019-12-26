// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;


/**
 * The 4 lane drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView4 extends DrumViewBase
{
    private static final int NUM_DISPLAY_COLS = 16;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView4 (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Drum 4", surface, model, 2, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.isActive () || velocity == 0)
            return;

        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        final int sound = y % 4 + this.soundOffset;
        final int col = 8 * (1 - y / 4) + x;

        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        this.getClip ().toggleStep (editMidiChannel, col, this.scales.getDrumOffset () + this.selectedPad + sound, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        if (!this.isActive ())
        {
            padGrid.turnOff ();
            return;
        }

        // Clip length/loop area
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % DrumView4.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        for (int sound = 0; sound < 4; sound++)
        {
            for (int col = 0; col < DrumView4.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (editMidiChannel, col, offsetY + this.selectedPad + sound + this.soundOffset).getState ();
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                int y = col / 8;
                if (col < 8)
                    y += 5;
                y += sound;
                padGrid.lightEx (x, 8 - y, isSet > 0 ? hilite ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : hilite ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (EMPTY_TABLE));
    }


    /** {@inheritDoc} */
    @Override
    protected int updateLowerSceneButtons (final int scene)
    {
        if (this.isActive ())
        {
            if (scene == 7 && this.soundOffset == 0 || scene == 6 && this.soundOffset == 4 || scene == 5 && this.soundOffset == 8 || scene == 4 && this.soundOffset == 12)
                return LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW;
            return LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
        }
        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    protected void onLowerScene (final int index)
    {
        if (!this.isActive ())
            return;

        // 7, 6, 5, 4
        this.soundOffset = 4 * (7 - index);
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }
}