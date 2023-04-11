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
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.EqualizerBandType;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The handler for equalizer commands.
 *
 * @author Jürgen Moßgraber
 */
public class EqHandler extends AbstractHandler
{
    private boolean eqRequested = false;


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
    public EqHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.EQ_SET_TYPE_1,
            FlexiCommand.EQ_SET_TYPE_2,
            FlexiCommand.EQ_SET_TYPE_3,
            FlexiCommand.EQ_SET_TYPE_4,
            FlexiCommand.EQ_SET_TYPE_5,
            FlexiCommand.EQ_SET_TYPE_6,
            FlexiCommand.EQ_SET_TYPE_7,
            FlexiCommand.EQ_SET_TYPE_8,
            FlexiCommand.EQ_SET_FREQ_1,
            FlexiCommand.EQ_SET_FREQ_2,
            FlexiCommand.EQ_SET_FREQ_3,
            FlexiCommand.EQ_SET_FREQ_4,
            FlexiCommand.EQ_SET_FREQ_5,
            FlexiCommand.EQ_SET_FREQ_6,
            FlexiCommand.EQ_SET_FREQ_7,
            FlexiCommand.EQ_SET_FREQ_8,
            FlexiCommand.EQ_SET_GAIN_1,
            FlexiCommand.EQ_SET_GAIN_2,
            FlexiCommand.EQ_SET_GAIN_3,
            FlexiCommand.EQ_SET_GAIN_4,
            FlexiCommand.EQ_SET_GAIN_5,
            FlexiCommand.EQ_SET_GAIN_6,
            FlexiCommand.EQ_SET_GAIN_7,
            FlexiCommand.EQ_SET_GAIN_8,
            FlexiCommand.EQ_SET_Q_1,
            FlexiCommand.EQ_SET_Q_2,
            FlexiCommand.EQ_SET_Q_3,
            FlexiCommand.EQ_SET_Q_4,
            FlexiCommand.EQ_SET_Q_5,
            FlexiCommand.EQ_SET_Q_6,
            FlexiCommand.EQ_SET_Q_7,
            FlexiCommand.EQ_SET_Q_8
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final IEqualizerDevice equalizer = (IEqualizerDevice) this.model.getSpecificDevice (DeviceID.EQ);

        switch (command)
        {
            case EQ_SET_TYPE_1:
            case EQ_SET_TYPE_2:
            case EQ_SET_TYPE_3:
            case EQ_SET_TYPE_4:
            case EQ_SET_TYPE_5:
            case EQ_SET_TYPE_6:
            case EQ_SET_TYPE_7:
            case EQ_SET_TYPE_8:
                return equalizer.getTypeParameter (command.ordinal () - FlexiCommand.EQ_SET_TYPE_1.ordinal ()).getValue ();

            case EQ_SET_FREQ_1:
            case EQ_SET_FREQ_2:
            case EQ_SET_FREQ_3:
            case EQ_SET_FREQ_4:
            case EQ_SET_FREQ_5:
            case EQ_SET_FREQ_6:
            case EQ_SET_FREQ_7:
            case EQ_SET_FREQ_8:
                return equalizer.getFrequencyParameter (command.ordinal () - FlexiCommand.EQ_SET_FREQ_1.ordinal ()).getValue ();

            case EQ_SET_GAIN_1:
            case EQ_SET_GAIN_2:
            case EQ_SET_GAIN_3:
            case EQ_SET_GAIN_4:
            case EQ_SET_GAIN_5:
            case EQ_SET_GAIN_6:
            case EQ_SET_GAIN_7:
            case EQ_SET_GAIN_8:
                return equalizer.getGainParameter (command.ordinal () - FlexiCommand.EQ_SET_GAIN_1.ordinal ()).getValue ();

            case EQ_SET_Q_1:
            case EQ_SET_Q_2:
            case EQ_SET_Q_3:
            case EQ_SET_Q_4:
            case EQ_SET_Q_5:
            case EQ_SET_Q_6:
            case EQ_SET_Q_7:
            case EQ_SET_Q_8:
                return equalizer.getQParameter (command.ordinal () - FlexiCommand.EQ_SET_Q_1.ordinal ()).getValue ();

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final IEqualizerDevice equalizer = (IEqualizerDevice) this.model.getSpecificDevice (DeviceID.EQ);
        if (!equalizer.doesExist ())
        {
            final ITrack cursorTrack = this.model.getCursorTrack ();
            if (cursorTrack.doesExist () && !this.eqRequested)
            {
                cursorTrack.addEqualizerDevice ();
                this.eqRequested = true;
            }
            return;
        }
        this.eqRequested = false;

        switch (command)
        {
            case EQ_SET_TYPE_1:
            case EQ_SET_TYPE_2:
            case EQ_SET_TYPE_3:
            case EQ_SET_TYPE_4:
            case EQ_SET_TYPE_5:
            case EQ_SET_TYPE_6:
            case EQ_SET_TYPE_7:
            case EQ_SET_TYPE_8:
                final int bandIndex1 = command.ordinal () - FlexiCommand.EQ_SET_TYPE_1.ordinal ();
                final IParameter type = equalizer.getTypeParameter (bandIndex1);
                this.handleParameter (knobMode, type, value);
                return;

            case EQ_SET_FREQ_1:
            case EQ_SET_FREQ_2:
            case EQ_SET_FREQ_3:
            case EQ_SET_FREQ_4:
            case EQ_SET_FREQ_5:
            case EQ_SET_FREQ_6:
            case EQ_SET_FREQ_7:
            case EQ_SET_FREQ_8:
                final int bandIndex2 = command.ordinal () - FlexiCommand.EQ_SET_FREQ_1.ordinal ();
                ensureBandIsOn (equalizer, bandIndex2);
                final IParameter frequency = equalizer.getFrequencyParameter (bandIndex2);
                this.handleParameter (knobMode, frequency, value);
                return;

            case EQ_SET_GAIN_1:
            case EQ_SET_GAIN_2:
            case EQ_SET_GAIN_3:
            case EQ_SET_GAIN_4:
            case EQ_SET_GAIN_5:
            case EQ_SET_GAIN_6:
            case EQ_SET_GAIN_7:
            case EQ_SET_GAIN_8:
                final int bandIndex3 = command.ordinal () - FlexiCommand.EQ_SET_GAIN_1.ordinal ();
                ensureBandIsOn (equalizer, bandIndex3);
                final IParameter gain = equalizer.getGainParameter (bandIndex3);
                this.handleParameter (knobMode, gain, value);
                return;

            case EQ_SET_Q_1:
            case EQ_SET_Q_2:
            case EQ_SET_Q_3:
            case EQ_SET_Q_4:
            case EQ_SET_Q_5:
            case EQ_SET_Q_6:
            case EQ_SET_Q_7:
            case EQ_SET_Q_8:
                final int bandIndex4 = command.ordinal () - FlexiCommand.EQ_SET_Q_1.ordinal ();
                ensureBandIsOn (equalizer, bandIndex4);
                final IParameter q = equalizer.getQParameter (bandIndex4);
                this.handleParameter (knobMode, q, value);
                return;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private static void ensureBandIsOn (final IEqualizerDevice equalizer, final int bandIndex)
    {
        if (equalizer.getTypeID (bandIndex) == EqualizerBandType.OFF)
            equalizer.setType (bandIndex, EqualizerBandType.BELL);
    }


    private void handleParameter (final KnobMode knobMode, final IParameter fxParam, final MidiValue value)
    {
        final int val = value.getValue ();
        if (isAbsolute (knobMode))
            fxParam.setValue (this.getAbsoluteValueChanger (value), val);
        else
            fxParam.changeValue (this.getRelativeValueChanger (knobMode), val);
        this.mvHelper.notifyParameter (fxParam);
    }
}
