// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.utils.FlexiHandlerException;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The handler for groove commands.
 *
 * @author Jürgen Moßgraber
 */
public class GrooveHandler extends AbstractHandler
{
    private final IGroove groove;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param absoluteLowResValueChanger The default absolute value changer in low res mode
     * @param signedBitRelativeValueChanger The signed bit relative value changer
     * @param signedBit2RelativeValueChanger The signed bit relative value changer
     * @param offsetBinaryRelativeValueChanger The offset binary relative value changer
     */
    public GrooveHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger signedBit2RelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, signedBit2RelativeValueChanger, offsetBinaryRelativeValueChanger);

        this.groove = this.model.getGroove ();
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.GROOVE_ACTIVE,
            FlexiCommand.GROOVE_SHUFFLE_AMOUNT,
            FlexiCommand.GROOVE_ACCENT_AMOUNT,
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        switch (command)
        {
            case GROOVE_ACTIVE:
                return toMidiValue (this.groove.getParameter (GrooveParameterID.ENABLED).getValue () > 0);

            case GROOVE_SHUFFLE_AMOUNT:
                return this.groove.getParameter (GrooveParameterID.SHUFFLE_AMOUNT).getValue ();

            case GROOVE_ACCENT_AMOUNT:
                return this.groove.getParameter (GrooveParameterID.ACCENT_AMOUNT).getValue ();

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            case GROOVE_ACTIVE:
                if (isButtonPressed)
                {
                    final IParameter parameter = this.groove.getParameter (GrooveParameterID.ENABLED);
                    parameter.setValue (parameter.getValue () == 0 ? this.model.getValueChanger ().getUpperBound () - 1 : 0);
                }
                break;

            case GROOVE_SHUFFLE_AMOUNT:
                this.changeValue (this.groove.getParameter (GrooveParameterID.SHUFFLE_AMOUNT), knobMode, value);
                break;

            case GROOVE_ACCENT_AMOUNT:
                this.changeValue (this.groove.getParameter (GrooveParameterID.ACCENT_AMOUNT), knobMode, value);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void changeValue (final IParameter parameter, final KnobMode knobMode, final MidiValue value)
    {
        final int val = value.getValue ();
        if (isAbsolute (knobMode))
            parameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            parameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }
}
