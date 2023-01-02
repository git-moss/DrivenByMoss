// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.bitwig.framework.hardware.AbstractHwAbsoluteControl;
import de.mossgrabers.bitwig.framework.hardware.HwButtonImpl;
import de.mossgrabers.bitwig.framework.hardware.HwFaderImpl;
import de.mossgrabers.bitwig.framework.hardware.HwRelativeKnobImpl;
import de.mossgrabers.framework.controller.hardware.BindException;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteControl;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.MidiShortCallback;
import de.mossgrabers.framework.daw.midi.MidiSysExCallback;

import com.bitwig.extension.controller.api.AbsoluteHardwareControl;
import com.bitwig.extension.controller.api.AbsoluteHardwareValueMatcher;
import com.bitwig.extension.controller.api.ContinuousHardwareControl;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareAction;
import com.bitwig.extension.controller.api.HardwareActionMatcher;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;
import com.bitwig.extension.controller.api.RelativeHardwareValueMatcher;


/**
 * A MIDI input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiInputImpl implements IMidiInput
{
    private final MidiIn  port;
    private NoteInputImpl defaultNoteInput;


    /**
     * Constructor. Creates a default note input.
     *
     * @param portNumber The number of the MIDI input port
     * @param host The Bitwig host
     * @param name the name of the note input as it appears in the track input choosers in Bitwig
     *            Studio, set to null to not create a note input
     * @param filters a filter string formatted as hexadecimal value with `?` as wildcard. For
     *            example `80????` would match note-off on channel 1 (0). When this parameter is
     *            {@null}, a standard filter will be used to forward note-related messages on
     *            channel 1 (0).
     */
    public MidiInputImpl (final int portNumber, final ControllerHost host, final String name, final String [] filters)
    {
        this.port = host.getMidiInPort (portNumber);

        if (name != null)
            this.defaultNoteInput = new NoteInputImpl (this.port.createNoteInput (name, filters));
    }


    /** {@inheritDoc} */
    @Override
    public INoteInput createNoteInput (final String name, final String... filters)
    {
        return new NoteInputImpl (this.port.createNoteInput (name, filters));
    }


    /** {@inheritDoc} */
    @Override
    public void setMidiCallback (final MidiShortCallback callback)
    {
        this.port.setMidiCallback (callback::handleMidi);
    }


    /** {@inheritDoc} */
    @Override
    public void setSysexCallback (final MidiSysExCallback callback)
    {
        this.port.setSysexCallback (callback::handleMidi);
    }


    /** {@inheritDoc} */
    @Override
    public void sendRawMidiEvent (final int status, final int data1, final int data2)
    {
        if (this.defaultNoteInput != null)
            this.defaultNoteInput.sendRawMidiEvent (status, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public INoteInput getDefaultNoteInput ()
    {
        return this.defaultNoteInput;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwButton button, final BindType type, final int channel, final int control)
    {
        final HardwareButton hardwareButton = ((HwButtonImpl) button).getHardwareButton ();

        final AbsoluteHardwareValueMatcher pressedMatcher;
        final HardwareActionMatcher releasedMatcher;
        switch (type)
        {
            case CC:
                pressedMatcher = this.port.createAbsoluteCCValueMatcher (channel, control);
                releasedMatcher = this.port.createCCActionMatcher (channel, control, 0);
                break;

            case NOTE:
                pressedMatcher = this.port.createNoteOnVelocityValueMatcher (channel, control);
                releasedMatcher = this.port.createNoteOffActionMatcher (channel, control);
                break;

            default:
                throw new BindException (type);
        }

        final HardwareAction pressedAction = hardwareButton.pressedAction ();
        pressedAction.setPressureActionMatcher (pressedMatcher);
        pressedAction.setShouldFireEvenWhenUsedAsNoteInput (true);

        setAction (hardwareButton.releasedAction (), releasedMatcher);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwButton button, final BindType type, final int channel, final int control, final int value)
    {
        if (type != BindType.CC)
            throw new BindException (type);

        final HardwareButton hardwareButton = ((HwButtonImpl) button).getHardwareButton ();
        setAction (hardwareButton.pressedAction (), this.port.createCCActionMatcher (channel, control, value));
    }


    /** {@inheritDoc} */
    @Override
    public void unbind (final IHwButton button)
    {
        final HardwareButton hardwareButton = ((HwButtonImpl) button).getHardwareButton ();
        hardwareButton.pressedAction ().setPressureActionMatcher (null);
        hardwareButton.releasedAction ().setActionMatcher (null);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwRelativeKnob knob, final BindType type, final int channel, final int control, final RelativeEncoding encoding)
    {
        if (type != BindType.CC)
            throw new BindException (type);

        final RelativeHardwareValueMatcher valueMatcher;

        switch (encoding)
        {
            case TWOS_COMPLEMENT:
                valueMatcher = this.port.createRelative2sComplementCCValueMatcher (channel, control, 127);
                break;

            case OFFSET_BINARY:
                valueMatcher = this.port.createRelativeBinOffsetCCValueMatcher (channel, control, 127);
                break;

            case SIGNED_BIT:
                valueMatcher = this.port.createRelativeSignedBitCCValueMatcher (channel, control, 127);
                break;

            case SIGNED_BIT2:
                valueMatcher = this.port.createRelativeSignedBit2CCValueMatcher (channel, control, 127);
                break;

            default:
                // Can never been reached
                throw new BindException (type);
        }

        ((HwRelativeKnobImpl) knob).getHardwareKnob ().setAdjustValueMatcher (valueMatcher);
    }


    /** {@inheritDoc} */
    @Override
    public void unbind (final IHwRelativeKnob relativeKnob)
    {
        ((HwRelativeKnobImpl) relativeKnob).getHardwareKnob ().setAdjustValueMatcher (null);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwFader fader, final BindType type, final int channel, final int control)
    {
        this.bind (type, channel, control, ((AbstractHwAbsoluteControl<?>) fader).getHardwareControl ());
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwAbsoluteControl absoluteControl, final BindType type, final int channel, final int control)
    {
        this.bind (type, channel, control, ((AbstractHwAbsoluteControl<?>) absoluteControl).getHardwareControl ());
    }


    /** {@inheritDoc} */
    @Override
    public void unbind (final IHwAbsoluteControl absoluteControl)
    {
        ((AbstractHwAbsoluteControl<?>) absoluteControl).getHardwareControl ().setAdjustValueMatcher (null);
    }


    private void bind (final BindType type, final int channel, final int control, final AbsoluteHardwareControl hardwareControl)
    {
        final AbsoluteHardwareValueMatcher matcher;

        switch (type)
        {
            case CC:
                matcher = this.port.createAbsoluteCCValueMatcher (channel, control);
                break;
            case PITCHBEND:
                matcher = this.port.createAbsolutePitchBendValueMatcher (channel);
                break;
            default:
                throw new BindException (type);
        }

        hardwareControl.setAdjustValueMatcher (matcher);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final IHwRelativeKnob relativeKnob, final BindType type, final int channel, final int control)
    {
        final RelativeHardwareKnob hardwareControl = ((HwRelativeKnobImpl) relativeKnob).getHardwareKnob ();
        this.bindTouch (hardwareControl, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final IHwFader fader, final BindType type, final int channel, final int control)
    {
        final HardwareSlider hardwareControl = ((HwFaderImpl) fader).getHardwareControl ();
        this.bindTouch (hardwareControl, type, channel, control);
    }


    private void bindTouch (final ContinuousHardwareControl<?> hardwareControl, final BindType type, final int channel, final int control)
    {
        final HardwareActionMatcher pressedMatcher;
        final HardwareActionMatcher releasedMatcher;
        switch (type)
        {
            case CC:
                pressedMatcher = this.port.createCCActionMatcher (channel, control, 127);
                releasedMatcher = this.port.createCCActionMatcher (channel, control, 0);
                break;
            case NOTE:
                pressedMatcher = this.port.createNoteOnActionMatcher (channel, control);
                releasedMatcher = this.port.createNoteOffActionMatcher (channel, control);
                break;
            default:
                throw new BindException (type);
        }

        setAction (hardwareControl.beginTouchAction (), pressedMatcher);
        setAction (hardwareControl.endTouchAction (), releasedMatcher);
    }


    /**
     * Get the MIDI input port.
     *
     * @return The MIDI input port
     */
    public MidiIn getPort ()
    {
        return this.port;
    }


    private static void setAction (final HardwareAction action, final HardwareActionMatcher matcher)
    {
        action.setActionMatcher (matcher);
        action.setShouldFireEvenWhenUsedAsNoteInput (true);
    }
}
