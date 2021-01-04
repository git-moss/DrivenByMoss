// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


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
    private ShiftMode shiftMode;


    private enum ShiftMode
    {
        NO_SHIFT,
        SHIFTED,
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
        if (event != ButtonEvent.DOWN)
            return;

        final C configuration = this.surface.getConfiguration ();
        final AbstractConfiguration.RecordFunction recordMode = this.isShifted () ? configuration.getShiftedRecordButtonFunction () : configuration.getRecordButtonFunction ();
        switch (recordMode)
        {
            case RECORD_ARRANGER:
                this.model.getTransport ().record ();
                break;
            case RECORD_CLIP:
                final ISlot slot = this.model.getSelectedSlot ();
                if (slot == null)
                    return;
                if (!slot.isRecording ())
                    slot.record ();
                slot.launch ();
                break;
            case NEW_CLIP:
                new NewCommand<> (this.model, this.surface).executeNormal (ButtonEvent.DOWN);
                break;
            case TOGGLE_ARRANGER_OVERDUB:
                this.model.getTransport ().toggleOverdub ();
                break;
            case TOGGLE_CLIP_OVERDUB:
                this.model.getTransport ().toggleLauncherOverdub ();
                break;
            case TOGGLE_REC_ARM:
                final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
                if (selectedTrack != null)
                    selectedTrack.toggleRecArm ();
                break;
            default:
                // Intentionally empty
                break;
        }
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
}
