// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrum64View;


/**
 * The Drum 64 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Drum64View extends AbstractDrum64View<MaschineJamControlSurface, MaschineJamConfiguration> implements IViewNavigation
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum64View (final MaschineJamControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.isButtonCombination (ButtonID.BROWSE))
        {
            final IDrumDevice primary = this.model.getDrumDevice (64);
            if (primary.hasDrumPads ())
                this.model.getBrowser ().replace (primary.getDrumPadBank ().getItem (playedPad));
            return;
        }

        super.handleButtonCombinations (playedPad);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        switch (buttonID)
        {
            case ARROW_LEFT, ARROW_RIGHT:
                // Not used
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
        switch (direction)
        {
            case UP:
                return this.isOctaveUpButtonOn ();
            case DOWN:
                return this.isOctaveDownButtonOn ();
            case LEFT, RIGHT:
            default:
                return false;
        }
    }
}