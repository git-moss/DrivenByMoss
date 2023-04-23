// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.AbstractDrum64View;


/**
 * The Drum 64 view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumView64 extends AbstractDrum64View<FireControlSurface, FireConfiguration> implements IFireView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView64 (final FireControlSurface surface, final IModel model)
    {
        super (surface, model, 16, 4);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.surface.isPressed (ButtonID.SCENE3))
            this.handleSoloButton (playedPad);
        else if (this.surface.isPressed (ButtonID.SCENE4))
            this.handleMuteButton (playedPad);

        super.handleButtonCombinations (playedPad);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case SCENE3:
                return this.surface.isPressed (ButtonID.SCENE3) ? 2 : 1;
            case SCENE4:
                return this.surface.isPressed (ButtonID.SCENE4) ? 2 : 1;

            case SCENE1, SCENE2:
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        switch (index)
        {
            case 2:
                return 4;

            case 3:
                return 1;

            case 0, 1:
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        if (this.model.getValueChanger ().isIncrease (value))
            this.scales.incDrumOctave ();
        else
            this.scales.decDrumOctave ();
        this.model.getDrumDevice ().getDrumPadBank ().scrollTo (this.scales.getDrumOffset (), false);
        this.updateNoteMapping ();
    }
}
