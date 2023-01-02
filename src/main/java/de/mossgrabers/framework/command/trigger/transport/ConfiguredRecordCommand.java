// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Command handle the record and restart (Shift+Record) button.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ConfiguredRecordCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final ShiftMode shiftMode;


    private enum ShiftMode
    {
        /** Use the primary commands. */
        NO_SHIFT,
        /** Use the secondary commands. */
        SHIFTED,
        /** Use the primary or secondary commands depending on the Shift button state. */
        CALC_SHIFT
    }


    /**
     * Constructor. The Shift state is calculated from the SHIFT button
     *
     * @param model The model
     * @param surface The surface
     */
    public ConfiguredRecordCommand (final IModel model, final S surface)
    {
        super (model, surface);

        this.shiftMode = ShiftMode.CALC_SHIFT;
    }


    /**
     * Constructor.
     *
     * @param isShifted True for the shifted record button otherwise the record button
     * @param model The model
     * @param surface The surface
     */
    public ConfiguredRecordCommand (final boolean isShifted, final IModel model, final S surface)
    {
        super (model, surface);

        this.shiftMode = isShifted ? ShiftMode.SHIFTED : ShiftMode.NO_SHIFT;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final C configuration = this.surface.getConfiguration ();
        final AbstractConfiguration.RecordFunction recordMode = this.isShifted () ? configuration.getShiftedRecordButtonFunction () : configuration.getRecordButtonFunction ();
        final ITransport transport = this.model.getTransport ();
        switch (recordMode)
        {
            case RECORD_ARRANGER:
                transport.startRecording ();
                break;
            case RECORD_ARRANGER_AND_ENABLE_AUTOMATION:
                if (!transport.isWritingArrangerAutomation ())
                    transport.toggleWriteArrangerAutomation ();
                transport.startRecording ();
                break;
            case RECORD_CLIP:
                this.createClip ();
                break;
            case RECORD_CLIP_AND_ENABLE_AUTOMATION:
                this.createClip ();
                if (!transport.isWritingClipLauncherAutomation ())
                    transport.toggleWriteClipLauncherAutomation ();
                break;
            case NEW_CLIP:
                new NewCommand<> (this.model, this.surface).execute ();
                break;
            case NEW_CLIP_AND_ENABLE_AUTOMATION:
                new NewCommand<> (this.model, this.surface).execute ();
                if (!transport.isWritingClipLauncherAutomation ())
                    transport.toggleWriteClipLauncherAutomation ();
                break;
            case TOGGLE_ARRANGER_OVERDUB:
                transport.toggleOverdub ();
                break;
            case TOGGLE_CLIP_OVERDUB:
                transport.toggleLauncherOverdub ();
                break;
            case TOGGLE_REC_ARM:
                final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
                if (selectedTrack.isPresent ())
                    selectedTrack.get ().toggleRecArm ();
                break;
            default:
                // Intentionally empty
                break;
        }
    }


    private void createClip ()
    {
        final Optional<ISlot> slot = this.model.getSelectedSlot ();
        if (slot.isEmpty ())
            return;
        final ISlot s = slot.get ();
        if (!s.isRecording ())
            s.startRecording ();
        s.launch ();
    }


    private boolean isShifted ()
    {
        switch (this.shiftMode)
        {
            case SHIFTED:
                return true;
            case NO_SHIFT:
                return false;
            default:
                return this.surface.isShiftPressed ();
        }
    }


    /**
     * Returns true if the record button should be lit depending on the selected function.
     *
     * @return True if lit
     */
    public boolean isLit ()
    {
        final C configuration = this.surface.getConfiguration ();
        final AbstractConfiguration.RecordFunction recordMode = this.isShifted () ? configuration.getShiftedRecordButtonFunction () : configuration.getRecordButtonFunction ();
        switch (recordMode)
        {
            case RECORD_ARRANGER, RECORD_ARRANGER_AND_ENABLE_AUTOMATION:
                return this.model.getTransport ().isRecording ();

            case NEW_CLIP, NEW_CLIP_AND_ENABLE_AUTOMATION, RECORD_CLIP, RECORD_CLIP_AND_ENABLE_AUTOMATION:
                final Optional<ISlot> slot = this.model.getSelectedSlot ();
                if (slot.isEmpty ())
                    return false;
                final ISlot s = slot.get ();
                return s.isRecording ();

            case TOGGLE_ARRANGER_OVERDUB:
                return this.model.getTransport ().isArrangerOverdub ();

            case TOGGLE_CLIP_OVERDUB:
                return this.model.getTransport ().isLauncherOverdub ();

            case TOGGLE_REC_ARM:
                final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
                return selectedTrack.isPresent () && selectedTrack.get ().isRecArm ();

            default:
                return false;
        }
    }
}
