// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.view.IMaschineView;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineView, IViewNavigation
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final MaschineJamControlSurface surface, final IModel model)
    {
        super (Views.NAME_DRUM, surface, model, 4, 4, true);
    }


    /** {@inheritDoc} */
    @Override
    public void changeOption (final EncoderMode temporaryEncoderMode, final int control)
    {
        this.keyManager.clearPressedKeys ();

        final boolean increase = this.model.getValueChanger ().isIncrease (control);

        switch (temporaryEncoderMode)
        {
            case TEMPORARY_PERFORM:
                // Not used
                break;

            case TEMPORARY_NOTES:
                // Not used
                break;

            case TEMPORARY_LOCK:
                // Not used
                break;

            case TEMPORARY_TUNE:
                if (increase)
                    this.onOctaveUp (ButtonEvent.DOWN);
                else
                    this.onOctaveDown (ButtonEvent.DOWN);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        switch (buttonID)
        {
            case ARROW_LEFT:
                this.onLeft (event);
                break;
            case ARROW_RIGHT:
                this.onRight (event);
                break;

            case ARROW_UP:
                this.onOctaveUp (event);
                break;
            case ARROW_DOWN:
                this.onOctaveDown (event);
                break;

            default:
                super.onButton (buttonID, event, velocity);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void drawPages (final INoteClip clip, final boolean isActive)
    {
        final boolean isAccentActive = this.configuration.isAccentActive ();
        if (isAccentActive)
        {
            int selectedPad = 15 - this.configuration.getFixedAccentValue () / 8;
            final int selY = selectedPad / 4;
            final int selX = selectedPad % 4;
            selectedPad = selY * 4 + 3 - selX;

            final IPadGrid padGrid = this.surface.getPadGrid ();
            for (int pad = 0; pad < 16; pad++)
            {
                final int x = this.playColumns + pad % this.playColumns;
                final int y = this.sequencerLines + pad / this.playColumns;
                padGrid.lightEx (x, y, pad == selectedPad ? MaschineColorManager.COLOR_GREEN : MaschineColorManager.COLOR_LIME_LO);
            }

            return;
        }

        super.drawPages (clip, isActive);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleLoopArea (final int pad, final int velocity)
    {
        final boolean isAccentActive = this.configuration.isAccentActive ();
        if (isAccentActive)
        {
            if (velocity == 0)
                return;
            final int selPad = (3 - pad / 4) * 4 + pad % 4;
            this.configuration.setFixedAccentValue ((selPad + 1) * 8 - 1);
            return;
        }

        super.handleLoopArea (pad, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScroll (final Direction direction)
    {
        final INoteClip clip = this.getClip ();
        switch (direction)
        {
            case LEFT:
                return clip.canScrollStepsBackwards ();
            case RIGHT:
                return clip.canScrollStepsForwards ();
            case UP:
                return this.isOctaveUpButtonOn ();
            case DOWN:
                return this.isOctaveDownButtonOn ();
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (INoteClip clip, int channel, int step, int note, int velocity)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();
        if (isSelectPressed)
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, channel, step, note, velocity, isSelectPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step, note, velocity);
    }
}