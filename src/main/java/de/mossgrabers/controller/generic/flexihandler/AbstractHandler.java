// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.MVHelper;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;


/**
 * Abstract implementation for flexi handlers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractHandler implements IFlexiCommandHandler
{
    protected static final int                                                      SCROLL_RATE     = 6;

    protected final IValueChanger                                                   absoluteLowResValueChanger;
    protected final IValueChanger                                                   signedBitRelativeValueChanger;
    protected final IValueChanger                                                   offsetBinaryRelativeValueChanger;

    protected final IModel                                                          model;
    protected final MVHelper<GenericFlexiControlSurface, GenericFlexiConfiguration> mvHelper;
    protected final GenericFlexiControlSurface                                      surface;
    protected final GenericFlexiConfiguration                                       configuration;

    private int                                                                     movementCounter = 0;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param absoluteLowResValueChanger The default absolute value changer in low res mode
     * @param signedBitRelativeValueChanger The signed bit relative value changer
     * @param offsetBinaryRelativeValueChanger The offset binary relative value changer
     */
    protected AbstractHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        this.model = model;
        this.surface = surface;
        this.configuration = configuration;
        this.mvHelper = new MVHelper<> (model, surface);
        this.absoluteLowResValueChanger = absoluteLowResValueChanger;
        this.signedBitRelativeValueChanger = signedBitRelativeValueChanger;
        this.offsetBinaryRelativeValueChanger = offsetBinaryRelativeValueChanger;
    }


    protected IValueChanger getAbsoluteValueChanger (final MidiValue value)
    {
        return value.isHighRes () ? this.model.getValueChanger () : this.absoluteLowResValueChanger;
    }


    protected IValueChanger getRelativeValueChanger (final KnobMode knobMode)
    {
        switch (knobMode)
        {
            default:
            case RELATIVE_TWOS_COMPLEMENT:
                return this.model.getValueChanger ();
            case RELATIVE_SIGNED_BIT:
                return this.signedBitRelativeValueChanger;
            case RELATIVE_OFFSET_BINARY:
                return this.offsetBinaryRelativeValueChanger;
        }
    }


    protected boolean isIncrease (final KnobMode knobMode, final MidiValue control)
    {
        return this.getRelativeValueChanger (knobMode).calcKnobChange (control.getValue ()) > 0;
    }


    /**
     * Return if the given knob mode is one of the absolute ones.
     *
     * @param knobMode The knob mode to test
     * @return True if it is an absolute mode
     */
    public static boolean isAbsolute (final KnobMode knobMode)
    {
        return knobMode == KnobMode.ABSOLUTE || knobMode == KnobMode.ABSOLUTE_TOGGLE;
    }


    /**
     * Test if a button is pressed. Can only be true for absolute modes.
     *
     * @param knobMode The knob/button mode
     * @param value The value to test
     * @return True if pressed
     */
    protected boolean isButtonPressed (final KnobMode knobMode, final MidiValue value)
    {
        return knobMode == KnobMode.ABSOLUTE_TOGGLE || knobMode == KnobMode.ABSOLUTE && value.isPositive ();
    }


    /**
     * Slows down knob movement. Increases the counter till the scroll rate.
     *
     * @return True if the knob movement should be executed otherwise false
     */
    protected boolean increaseKnobMovement ()
    {
        this.movementCounter++;
        if (this.movementCounter < SCROLL_RATE)
            return false;
        this.movementCounter = 0;
        return true;
    }
}
