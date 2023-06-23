// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.utils.FlexiHandlerException;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The handler for track remote parameter commands.
 *
 * @author Jürgen Moßgraber
 */
public class TrackRemotesHandler extends AbstractHandler
{
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
    public TrackRemotesHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.TRACK_SET_PARAMETER_1,
            FlexiCommand.TRACK_SET_PARAMETER_2,
            FlexiCommand.TRACK_SET_PARAMETER_3,
            FlexiCommand.TRACK_SET_PARAMETER_4,
            FlexiCommand.TRACK_SET_PARAMETER_5,
            FlexiCommand.TRACK_SET_PARAMETER_6,
            FlexiCommand.TRACK_SET_PARAMETER_7,
            FlexiCommand.TRACK_SET_PARAMETER_8,
            FlexiCommand.TRACK_SELECT_PREVIOUS_PAGE,
            FlexiCommand.TRACK_SELECT_NEXT_PAGE,
            FlexiCommand.TRACK_TOGGLE_PARAMETER_1,
            FlexiCommand.TRACK_TOGGLE_PARAMETER_2,
            FlexiCommand.TRACK_TOGGLE_PARAMETER_3,
            FlexiCommand.TRACK_TOGGLE_PARAMETER_4,
            FlexiCommand.TRACK_TOGGLE_PARAMETER_5,
            FlexiCommand.TRACK_TOGGLE_PARAMETER_6,
            FlexiCommand.TRACK_TOGGLE_PARAMETER_7,
            FlexiCommand.TRACK_TOGGLE_PARAMETER_8,
            FlexiCommand.TRACK_RESET_PARAMETER_1,
            FlexiCommand.TRACK_RESET_PARAMETER_2,
            FlexiCommand.TRACK_RESET_PARAMETER_3,
            FlexiCommand.TRACK_RESET_PARAMETER_4,
            FlexiCommand.TRACK_RESET_PARAMETER_5,
            FlexiCommand.TRACK_RESET_PARAMETER_6,
            FlexiCommand.TRACK_RESET_PARAMETER_7,
            FlexiCommand.TRACK_RESET_PARAMETER_8
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final IParameterBank trackParameterBank = this.model.getCursorTrack ().getParameterBank ();
        if (trackParameterBank == null)
            return -1;

        switch (command)
        {
            case TRACK_SET_PARAMETER_1, TRACK_SET_PARAMETER_2, TRACK_SET_PARAMETER_3, TRACK_SET_PARAMETER_4, TRACK_SET_PARAMETER_5, TRACK_SET_PARAMETER_6, TRACK_SET_PARAMETER_7, TRACK_SET_PARAMETER_8:
                return trackParameterBank.getItem (command.ordinal () - FlexiCommand.TRACK_SET_PARAMETER_1.ordinal ()).getValue ();

            case TRACK_TOGGLE_PARAMETER_1, TRACK_TOGGLE_PARAMETER_2, TRACK_TOGGLE_PARAMETER_3, TRACK_TOGGLE_PARAMETER_4, TRACK_TOGGLE_PARAMETER_5, TRACK_TOGGLE_PARAMETER_6, TRACK_TOGGLE_PARAMETER_7, TRACK_TOGGLE_PARAMETER_8:
                final int value = trackParameterBank.getItem (command.ordinal () - FlexiCommand.TRACK_TOGGLE_PARAMETER_1.ordinal ()).getValue ();
                return toMidiValue (value > 0);

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final IParameterBank trackParameterBank = this.model.getCursorTrack ().getParameterBank ();
        if (trackParameterBank == null)
            return;

        switch (command)
        {
            case TRACK_SET_PARAMETER_1, TRACK_SET_PARAMETER_2, TRACK_SET_PARAMETER_3, TRACK_SET_PARAMETER_4, TRACK_SET_PARAMETER_5, TRACK_SET_PARAMETER_6, TRACK_SET_PARAMETER_7, TRACK_SET_PARAMETER_8:
                final IParameter trackParam = trackParameterBank.getItem (command.ordinal () - FlexiCommand.TRACK_SET_PARAMETER_1.ordinal ());
                final int val = value.getValue ();
                if (isAbsolute (knobMode))
                    trackParam.setValue (this.getAbsoluteValueChanger (value), val);
                else
                    trackParam.changeValue (this.getRelativeValueChanger (knobMode), val);
                break;

            case TRACK_RESET_PARAMETER_1, TRACK_RESET_PARAMETER_2, TRACK_RESET_PARAMETER_3, TRACK_RESET_PARAMETER_4, TRACK_RESET_PARAMETER_5, TRACK_RESET_PARAMETER_6, TRACK_RESET_PARAMETER_7, TRACK_RESET_PARAMETER_8:
                if (this.isButtonPressed (knobMode, value))
                    trackParameterBank.getItem (command.ordinal () - FlexiCommand.TRACK_RESET_PARAMETER_1.ordinal ()).resetValue ();
                break;

            case TRACK_TOGGLE_PARAMETER_1, TRACK_TOGGLE_PARAMETER_2, TRACK_TOGGLE_PARAMETER_3, TRACK_TOGGLE_PARAMETER_4, TRACK_TOGGLE_PARAMETER_5, TRACK_TOGGLE_PARAMETER_6, TRACK_TOGGLE_PARAMETER_7, TRACK_TOGGLE_PARAMETER_8:
                final IParameter trackToggleParam = trackParameterBank.getItem (command.ordinal () - FlexiCommand.TRACK_TOGGLE_PARAMETER_1.ordinal ());
                if (this.isButtonPressed (knobMode, value))
                {
                    final int v = trackToggleParam.getValue ();
                    trackToggleParam.setValue (v > 0 ? 0 : this.model.getValueChanger ().getUpperBound () - 1);
                }
                break;

            case TRACK_SELECT_PREVIOUS_PAGE:
                trackParameterBank.scrollBackwards ();
                this.mvHelper.notifySelectedTrackParameterPage ();
                break;

            case TRACK_SELECT_NEXT_PAGE:
                trackParameterBank.scrollForwards ();
                this.mvHelper.notifySelectedTrackParameterPage ();
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }
}
