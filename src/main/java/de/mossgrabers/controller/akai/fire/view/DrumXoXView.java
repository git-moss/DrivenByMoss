// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.mode.FireLayerMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumXoXView;


/**
 * The Drum XoX view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumXoXView extends AbstractDrumXoXView<FireControlSurface, FireConfiguration> implements IFireView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumXoXView (final FireControlSurface surface, final IModel model)
    {
        super (Views.NAME_DRUM, surface, model, 16);

        this.buttonSelect = ButtonID.ALT;
    }


    /** {@inheritDoc} */
    @Override
    protected IDrumDevice getDrumDevice ()
    {
        return this.model.getDrumDevice (16);
    }


    /** {@inheritDoc} */
    @Override
    protected void selectDrumPad (final int index)
    {
        super.selectDrumPad (index);

        if (this.surface.getModeManager ().getActive () instanceof final FireLayerMode fireLayerMode)
            fireLayerMode.parametersAdjusted ();
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        final IDrumDevice drumDevice = this.getDrumDevice ();
        if (!drumDevice.hasDrumPads () || this.blockSelectKnob)
            return;

        final boolean isUp = this.model.getValueChanger ().isIncrease (value);

        // Change note repeat if active and a pad is held
        if (this.configuration.isNoteRepeatActive ())
        {
            boolean isDrumPadPressed = false;
            for (int i = 0; i < 16; i++)
            {
                if (this.surface.isPressed (ButtonID.get (ButtonID.PAD33, i)))
                    isDrumPadPressed = true;
            }
            if (isDrumPadPressed)
            {
                final Resolution activePeriod = this.configuration.getNoteRepeatPeriod ();
                final Resolution sel;
                if (isUp)
                    sel = NEXT_RESOLUTION.get (activePeriod);
                else
                    sel = PREV_RESOLUTION.get (activePeriod);
                this.configuration.setNoteRepeatPeriod (sel);
                this.mvHelper.delayDisplay ( () -> "Period: " + sel.getName ());
                return;
            }
        }

        this.adjustPage (isUp, 0);
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        if (!this.isActive ())
            return 0;

        switch (index)
        {
            case 0:
                return this.isCopy ? 2 : 1;

            case 1:
                return this.isSolo ? 2 : 1;

            case 2:
                return this.isEditLoopRange () ? 2 : 0;

            default:
            case 3:
                return this.configuration.isNoteRepeatActive () ? 2 : 0;
        }
    }
}