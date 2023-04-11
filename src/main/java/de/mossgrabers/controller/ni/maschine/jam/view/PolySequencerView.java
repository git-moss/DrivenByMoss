// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractPolySequencerView;


/**
 * The Poly Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class PolySequencerView extends AbstractPolySequencerView<MaschineJamControlSurface, MaschineJamConfiguration> implements IViewNavigation
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PolySequencerView (final MaschineJamControlSurface surface, final IModel model)
    {
        super (surface, model, true);
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
}