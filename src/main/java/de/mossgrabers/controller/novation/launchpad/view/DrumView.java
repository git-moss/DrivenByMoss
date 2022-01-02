// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private boolean extraButtonsOn     = false;
    private boolean noteRepeatPeriodOn = false;
    private boolean noteRepeatLengthOn = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 4, 4, true);

        this.buttonSelect = ButtonID.PAD13;
        this.buttonMute = ButtonID.PAD14;
        this.buttonSolo = ButtonID.PAD15;
        this.buttonBrowse = ButtonID.PAD16;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return 0;

        final int scene = ordinal - ButtonID.SCENE1.ordinal ();

        if (ButtonID.isSceneButton (buttonID))
        {
            final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
            if (this.noteRepeatPeriodOn)
            {
                final int periodIndex = Resolution.getMatch (noteRepeat.getPeriod ());
                return buttonID == ButtonID.get (ButtonID.SCENE1, 7 - periodIndex) ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO;
            }

            if (this.noteRepeatLengthOn)
            {
                final int lengthIndex = Resolution.getMatch (noteRepeat.getNoteLength ());
                return buttonID == ButtonID.get (ButtonID.SCENE1, 7 - lengthIndex) ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO;
            }
        }

        if (!this.isActive ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        return scene == 7 - this.selectedResolutionIndex ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.isActive ())
            return;

        if (this.noteRepeatPeriodOn)
        {
            this.setPeriod (7 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ()));
            return;
        }

        if (this.noteRepeatLengthOn)
        {
            this.setNoteLength (7 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ()));
            return;
        }

        super.onButton (buttonID, event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleLoopArea (final int pad, final int velocity)
    {
        if (pad == 15)
        {
            if (velocity > 0)
                this.extraButtonsOn = !this.extraButtonsOn;
            return;
        }

        if (!this.extraButtonsOn || pad < 8)
        {
            super.handleLoopArea (pad, velocity);
            return;
        }

        if (velocity == 0)
            return;

        final LaunchpadConfiguration configuration = this.surface.getConfiguration ();

        switch (pad)
        {
            case 12:
                configuration.toggleNoteRepeatActive ();
                break;
            case 13:
                this.noteRepeatPeriodOn = !this.noteRepeatPeriodOn;
                if (this.noteRepeatPeriodOn)
                    this.noteRepeatLengthOn = false;
                break;
            case 14:
                this.noteRepeatLengthOn = !this.noteRepeatLengthOn;
                if (this.noteRepeatLengthOn)
                    this.noteRepeatPeriodOn = false;
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void drawPages (final INoteClip clip, final boolean isActive)
    {
        super.drawPages (clip, isActive);

        // Draw the extra buttons

        final IPadGrid padGrid = this.surface.getPadGrid ();

        if (this.extraButtonsOn)
        {
            padGrid.lightEx (4, 6, this.isSelectTrigger () ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);
            padGrid.lightEx (5, 6, this.isMuteTrigger () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO);
            padGrid.lightEx (6, 6, this.isSoloTrigger () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO);
            padGrid.lightEx (7, 6, this.isBrowseTrigger () ? LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_LO);

            final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();

            padGrid.lightEx (4, 7, noteRepeat.isActive () ? LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID_LO);
            padGrid.lightEx (5, 7, this.noteRepeatPeriodOn ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);
            padGrid.lightEx (6, 7, this.noteRepeatLengthOn ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);
        }

        padGrid.lightEx (7, 7, this.extraButtonsOn ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO);
    }


    /** {@inheritDoc} */
    @Override
    protected int getNumberOfAvailablePages ()
    {
        // Remove the last 8 buttons so we can use it for something else if extra buttons are active
        return super.getNumberOfAvailablePages () - (this.extraButtonsOn ? 8 : 1);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int note, final int velocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.UP : ButtonID.DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, channel, step, note, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step, note, velocity);
    }


    private void setPeriod (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatPeriod (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Period: " + Resolution.getNameAt (index)), 100);
    }


    private void setNoteLength (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatLength (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Length: " + Resolution.getNameAt (index)), 100);
    }
}