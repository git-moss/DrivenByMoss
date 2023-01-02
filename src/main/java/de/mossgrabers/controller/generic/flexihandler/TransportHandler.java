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
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The handler for transport commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportHandler extends AbstractHandler
{
    private final WindCommand<GenericFlexiControlSurface, GenericFlexiConfiguration> rwdCommand;
    private final WindCommand<GenericFlexiControlSurface, GenericFlexiConfiguration> ffwdCommand;
    private final PlayCommand<GenericFlexiControlSurface, GenericFlexiConfiguration> playCommand;


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
    public TransportHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);

        this.rwdCommand = new WindCommand<> (this.model, surface, false);
        this.ffwdCommand = new WindCommand<> (this.model, surface, true);
        this.playCommand = new PlayCommand<> (this.model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.TRANSPORT_PLAY,
            FlexiCommand.TRANSPORT_STOP,
            FlexiCommand.TRANSPORT_RESTART,
            FlexiCommand.TRANSPORT_REWIND,
            FlexiCommand.TRANSPORT_FAST_FORWARD,
            FlexiCommand.TRANSPORT_TOGGLE_REPEAT,
            FlexiCommand.TRANSPORT_TOGGLE_METRONOME,
            FlexiCommand.TRANSPORT_SET_METRONOME_VOLUME,
            FlexiCommand.TRANSPORT_TOGGLE_METRONOME_IN_PREROLL,
            FlexiCommand.TRANSPORT_TOGGLE_PUNCH_IN,
            FlexiCommand.TRANSPORT_TOGGLE_PUNCH_OUT,
            FlexiCommand.TRANSPORT_TOGGLE_RECORD,
            FlexiCommand.TRANSPORT_TOGGLE_ARRANGER_OVERDUB,
            FlexiCommand.TRANSPORT_TOGGLE_CLIP_OVERDUB,
            FlexiCommand.TRANSPORT_TOGGLE_ARRANGER_AUTOMATION_WRITE,
            FlexiCommand.TRANSPORT_TOGGLE_CLIP_AUTOMATION_WRITE,
            FlexiCommand.TRANSPORT_SET_WRITE_MODE_LATCH,
            FlexiCommand.TRANSPORT_SET_WRITE_MODE_TOUCH,
            FlexiCommand.TRANSPORT_SET_WRITE_MODE_WRITE,
            FlexiCommand.TRANSPORT_SET_TEMPO,
            FlexiCommand.TRANSPORT_TAP_TEMPO,
            FlexiCommand.TRANSPORT_MOVE_PLAY_CURSOR
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final ITransport transport = this.model.getTransport ();

        switch (command)
        {
            case TRANSPORT_PLAY:
                return transport.isPlaying () ? 127 : 0;

            case TRANSPORT_STOP:
                return transport.isPlaying () ? 0 : 127;

            case TRANSPORT_TOGGLE_REPEAT:
                return transport.isLoop () ? 127 : 0;

            case TRANSPORT_TOGGLE_METRONOME:
                return transport.isMetronomeOn () ? 127 : 0;

            case TRANSPORT_SET_METRONOME_VOLUME:
                return transport.getMetronomeVolume ();

            case TRANSPORT_TOGGLE_METRONOME_IN_PREROLL:
                return transport.isPrerollMetronomeEnabled () ? 127 : 0;

            case TRANSPORT_TOGGLE_PUNCH_IN:
                return transport.isPunchInEnabled () ? 127 : 0;

            case TRANSPORT_TOGGLE_PUNCH_OUT:
                return transport.isPunchOutEnabled () ? 127 : 0;

            case TRANSPORT_TOGGLE_RECORD:
                return transport.isRecording () ? 127 : 0;

            case TRANSPORT_TOGGLE_ARRANGER_OVERDUB:
                return transport.isArrangerOverdub () ? 127 : 0;

            case TRANSPORT_TOGGLE_CLIP_OVERDUB:
                return transport.isLauncherOverdub () ? 127 : 0;

            case TRANSPORT_TOGGLE_ARRANGER_AUTOMATION_WRITE:
                return transport.isWritingArrangerAutomation () ? 127 : 0;

            case TRANSPORT_TOGGLE_CLIP_AUTOMATION_WRITE:
                return transport.isWritingClipLauncherAutomation () ? 127 : 0;

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
            // Transport: Play
            case TRANSPORT_PLAY:
                this.playCommand.execute (isButtonPressed ? ButtonEvent.DOWN : ButtonEvent.UP, isButtonPressed ? 127 : 0);
                break;

            // Transport: Stop
            case TRANSPORT_STOP:
                if (isButtonPressed)
                    this.model.getTransport ().stop ();
                break;
            // Transport: Restart
            case TRANSPORT_RESTART:
                if (isButtonPressed)
                    this.model.getTransport ().restart ();
                break;

            case TRANSPORT_REWIND:
                this.rwdCommand.execute (isButtonPressed ? ButtonEvent.DOWN : ButtonEvent.UP, isButtonPressed ? 127 : 0);
                break;

            case TRANSPORT_FAST_FORWARD:
                this.ffwdCommand.execute (isButtonPressed ? ButtonEvent.DOWN : ButtonEvent.UP, isButtonPressed ? 127 : 0);
                break;

            // Transport: Toggle Repeat
            case TRANSPORT_TOGGLE_REPEAT:
                if (isButtonPressed)
                    this.model.getTransport ().toggleLoop ();
                break;

            // Transport: Toggle Metronome
            case TRANSPORT_TOGGLE_METRONOME:
                if (isButtonPressed)
                    this.model.getTransport ().toggleMetronome ();
                break;

            // Transport: Set Metronome Volume
            case TRANSPORT_SET_METRONOME_VOLUME:
                this.handleMetronomeVolume (knobMode, value);
                break;

            // Transport: Toggle Metronome in Pre-roll
            case TRANSPORT_TOGGLE_METRONOME_IN_PREROLL:
                if (isButtonPressed)
                    this.model.getTransport ().togglePrerollMetronome ();
                break;

            // Transport: Toggle Punch In
            case TRANSPORT_TOGGLE_PUNCH_IN:
                if (isButtonPressed)
                    this.model.getTransport ().togglePunchIn ();
                break;

            // Transport: Toggle Punch Out
            case TRANSPORT_TOGGLE_PUNCH_OUT:
                if (isButtonPressed)
                    this.model.getTransport ().togglePunchOut ();
                break;

            // Transport: Toggle Record
            case TRANSPORT_TOGGLE_RECORD:
                if (isButtonPressed)
                    this.model.getTransport ().startRecording ();
                break;

            // Transport: Toggle Arranger Overdub
            case TRANSPORT_TOGGLE_ARRANGER_OVERDUB:
                if (isButtonPressed)
                    this.model.getTransport ().toggleOverdub ();
                break;

            // Transport: Toggle Clip Overdub
            case TRANSPORT_TOGGLE_CLIP_OVERDUB:
                if (isButtonPressed)
                    this.model.getTransport ().toggleLauncherOverdub ();
                break;

            // Transport: Toggle Arranger Automation Write
            case TRANSPORT_TOGGLE_ARRANGER_AUTOMATION_WRITE:
                if (isButtonPressed)
                    this.model.getTransport ().toggleWriteArrangerAutomation ();
                break;

            // Transport: Toggle Clip Automation Write
            case TRANSPORT_TOGGLE_CLIP_AUTOMATION_WRITE:
                if (isButtonPressed)
                    this.model.getTransport ().toggleWriteClipLauncherAutomation ();
                break;

            // Transport: Set Write Mode: Latch
            case TRANSPORT_SET_WRITE_MODE_LATCH:
                if (isButtonPressed)
                    this.model.getTransport ().setAutomationWriteMode (AutomationMode.LATCH);
                break;

            // Transport: Set Write Mode: Touch
            case TRANSPORT_SET_WRITE_MODE_TOUCH:
                if (isButtonPressed)
                    this.model.getTransport ().setAutomationWriteMode (AutomationMode.TOUCH);
                break;

            // Transport: Set Write Mode: Write
            case TRANSPORT_SET_WRITE_MODE_WRITE:
                if (isButtonPressed)
                    this.model.getTransport ().setAutomationWriteMode (AutomationMode.WRITE);
                break;

            // Transport: Set Tempo
            case TRANSPORT_SET_TEMPO:
                this.handleTempo (knobMode, value);
                break;

            // Transport: Tap Tempo
            case TRANSPORT_TAP_TEMPO:
                if (isButtonPressed)
                    this.model.getTransport ().tapTempo ();
                break;

            // Transport: Move Play Cursor
            case TRANSPORT_MOVE_PLAY_CURSOR:
                this.handlePlayCursor (knobMode, value);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void handlePlayCursor (final KnobMode knobMode, final MidiValue value)
    {
        final ITransport transport = this.model.getTransport ();
        // Only relative modes are supported
        if (!isAbsolute (knobMode))
            transport.changePosition (this.isIncrease (knobMode, value), this.surface.isKnobSensitivitySlow ());
    }


    private void handleTempo (final KnobMode knobMode, final MidiValue value)
    {
        final ITransport transport = this.model.getTransport ();
        if (isAbsolute (knobMode))
        {
            final int val = value.getValue ();
            transport.setTempo (transport.scaleTempo (val, value.isHighRes () ? 16384 : 128));
        }
        else
            transport.changeTempo (this.isIncrease (knobMode, value), this.surface.isKnobSensitivitySlow ());
    }


    private void handleMetronomeVolume (final KnobMode knobMode, final MidiValue value)
    {
        final ITransport transport = this.model.getTransport ();
        final IParameter metronomeVolumeParameter = transport.getMetronomeVolumeParameter ();
        final int val = value.getValue ();
        if (isAbsolute (knobMode))
            metronomeVolumeParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            metronomeVolumeParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }
}
