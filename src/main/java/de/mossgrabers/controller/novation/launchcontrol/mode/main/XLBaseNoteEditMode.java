package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTemporaryButtonMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.StepState;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.NullParameterProvider;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.KeyManager;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.List;


/**
 * Base sequencer mode for the LauchControl XL.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class XLBaseNoteEditMode extends XLAbstractMainMode<IItem>
{
    protected final int             clipRows;
    protected final int             clipCols;
    protected final IHost           host;
    protected final ISpecificDevice firstInstrument;
    protected final Scales          scales;
    protected final KeyManager      keyManager;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     * @param controls The IDs of the knobs or faders to control this mode
     */
    protected XLBaseNoteEditMode (final String name, final LaunchControlXLControlSurface surface, final IModel model, final int clipRows, final int clipCols, final List<ContinuousID> controls)
    {
        super (name, surface, model, null, controls);

        this.host = model.getHost ();

        this.scales = model.getScales ();
        this.scales.setDrumDefaultOffset (8);
        this.keyManager = new KeyManager (model, this.scales, surface.getPadGrid ());

        this.clipRows = clipRows;
        this.clipCols = clipCols;

        this.firstInstrument = model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
        final IParameterProvider deviceParameterProvider = new BankParameterProvider (this.firstInstrument.getParameterBank ());
        this.setParameterProviders (new NullParameterProvider (24), new CombinedParameterProvider (new NullParameterProvider (16), deviceParameterProvider));

        // Force clip creation
        this.getClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Row 2 starts with index 8, row 4 with 16.
        final int row = index / 8;
        final int column = index % 8;

        final INoteClip clip = this.getClip ();
        final int channel = this.configuration.getMidiEditChannel ();
        final int noteRow = this.getNoteRow (channel, column);
        if (noteRow < 0)
            return;
        final IStepInfo stepInfo = clip.getStep (channel, column, noteRow);
        if (stepInfo.getState () == StepState.OFF)
            return;

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final double normalizedValue = valueChanger.toNormalizedValue (value);

        final IDisplay display = this.surface.getDisplay ();

        switch (row)
        {
            case 0:
                this.handleKnobRow0 (clip, channel, column, noteRow, normalizedValue);
                break;

            case 1:
                if (this.surface.isPressed (ButtonID.REC_ARM))
                {
                    if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                    {
                        clip.updateStepVelocitySpread (channel, column, noteRow, normalizedValue);
                        this.surface.getDisplay ().notify (String.format ("Velocity Spread: %d%%", Integer.valueOf ((int) Math.round (normalizedValue * 100))));
                    }
                    return;
                }

                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                {
                    final int v = (int) Math.round ((value - 64) / 4.26);
                    clip.updateStepRepeatCount (channel, column, noteRow, v);
                    display.notify ("Repeat Count: " + stepInfo.getFormattedRepeatCount ());
                }
                break;

            case 2:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                {
                    final double pan = normalizedValue * 2.0 - 1.0;
                    clip.updateStepPan (channel, column, noteRow, pan);
                    display.notify ("Panorama: " + StringUtils.formatPercentage (pan));
                }
                break;

            // This is triggered from the faders mode
            case 3:
                clip.updateStepVelocity (channel, column, noteRow, normalizedValue);
                display.notify ("Velocity: " + StringUtils.formatPercentage (normalizedValue));
                break;

            default:
                return;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;

        final int channel = this.configuration.getMidiEditChannel ();
        final INoteClip clip = this.getClip ();
        final int noteRow = this.getNoteRow (channel, column);
        final IStepInfo stepInfo = noteRow < 0 ? null : clip.getStep (channel, column, noteRow);
        final IValueChanger valueChanger = this.model.getValueChanger ();

        switch (row)
        {
            case 0:
                if (stepInfo == null || stepInfo.getState () == StepState.OFF)
                    return 0;
                return this.getKnobValueRow0 (noteRow, stepInfo);

            case 1:
                if (stepInfo == null || stepInfo.getState () == StepState.OFF)
                    return 0;
                if (this.surface.isPressed (ButtonID.REC_ARM))
                {
                    if (this.host.supports (Capability.NOTE_EDIT_VELOCITY_SPREAD))
                        return valueChanger.fromNormalizedValue (stepInfo.getVelocitySpread ());
                    return 0;
                }
                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                {
                    final int repeatCount = stepInfo.getRepeatCount ();
                    return Math.min (127, (repeatCount + 128) / 2);
                }
                return 0;

            case 2:
                if (this.configuration.isDeviceActive ())
                    return this.firstInstrument.getParameterBank ().getItem (column).getValue ();

                if (stepInfo == null || stepInfo.getState () == StepState.OFF)
                    return 0;

                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                    return valueChanger.fromNormalizedValue ((stepInfo.getPan () + 1.0) / 2.0);
                return 0;

            case 3:
                if (stepInfo == null || stepInfo.getState () == StepState.OFF)
                    return 0;
                return (int) (stepInfo.getVelocity () * 127);

            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setKnobColor (final int row, final int column, final int value)
    {
        int green = 0;
        int red = 0;
        switch (row)
        {
            // Chance in green
            case 0:
                green = value == 0 ? 0 : value / 42 + 1;
                break;

            // Repeat Count intensity in red
            case 1:
                red = value == 0 ? 0 : value / 42 + 1;
                break;

            // Panorama in amber or Device parameters yellowish intensity in red
            case 2:
                green = value == 0 ? 0 : value / 42 + 1;
                if (this.configuration.isDeviceActive ())
                    red = green == 0 ? 0 : 1;
                else
                    red = green;
                break;

            default:
                // Not used
                return;
        }
        this.surface.setKnobLEDColor (row, column, green, red);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleRow2 (final int index, final ButtonEvent event)
    {
        final ModeManager trackButtonModeManager = this.surface.getTrackButtonModeManager ();

        switch (index)
        {
            // Device button
            case 0:
                if (event == ButtonEvent.DOWN)
                {
                    trackButtonModeManager.setTemporary (Modes.INSTRUMENT_DEVICE_PARAMS);
                    return;
                }

                if (event == ButtonEvent.UP)
                {
                    if (!((XLTemporaryButtonMode) trackButtonModeManager.get (Modes.INSTRUMENT_DEVICE_PARAMS)).hasBeenUsed ())
                        this.toggleDeviceActive ();
                    trackButtonModeManager.restore ();
                }
                break;

            // Mute button
            case 1:
                final Modes activeIDIgnoreTemporary = trackButtonModeManager.getActiveIDIgnoreTemporary ();
                final Modes modeID = activeIDIgnoreTemporary == Modes.DEVICE_LAYER_MUTE ? getSequencerMode () : Modes.DEVICE_LAYER_MUTE;
                this.alternativeModeSelect (event, modeID, Modes.CONFIGURATION);
                break;

            // Solo button
            case 2:
                final Modes activeIDIgnoreTemporary2 = trackButtonModeManager.getActiveIDIgnoreTemporary ();
                final Modes modeID2 = activeIDIgnoreTemporary2 == Modes.DEVICE_LAYER_SOLO ? getSequencerMode () : Modes.DEVICE_LAYER_SOLO;
                this.alternativeModeSelect (event, modeID2, Modes.CLIP);
                break;

            // Record Arm button
            case 3:
                final Modes activeIDIgnoreTemporary3 = trackButtonModeManager.getActiveIDIgnoreTemporary ();
                final Modes modeID3 = activeIDIgnoreTemporary3 == Modes.LOOP_LENGTH ? getSequencerMode () : Modes.LOOP_LENGTH;
                this.alternativeModeSelect (event, modeID3, Modes.TRANSPORT);
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Get the mode for the sequencer button row.
     *
     * @return The mode
     */
    protected abstract Modes getSequencerMode ();


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ModeManager trackModeManager = this.surface.getTrackButtonModeManager ();
        switch (buttonID)
        {
            case DEVICE:
                return this.surface.getConfiguration ().isDeviceActive () ? 127 : 0;

            case MUTE:
                return trackModeManager.isActive (Modes.DEVICE_LAYER_MUTE) ? 127 : 0;

            case SOLO:
                return trackModeManager.isActive (Modes.DEVICE_LAYER_SOLO) ? 127 : 0;

            case REC_ARM:
                return trackModeManager.isActive (Modes.LOOP_LENGTH) ? 127 : 0;

            case ROW1_1, ROW1_2, ROW1_3, ROW1_4, ROW1_5, ROW1_6, ROW1_7, ROW1_8:
                final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
                if (this.surface.isPressed (ButtonID.REC_ARM))
                    return super.getTransportButtonColor (index);
                return this.getFirstRowColor (index);

            default:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        }
    }


    /**
     * Get the clip.
     *
     * @return The clip
     */
    public final INoteClip getClip ()
    {
        return this.model.getNoteClip (this.clipCols, this.clipRows);
    }


    /**
     * Handle the 1st row knobs.
     *
     * @param clip The clip which contains the note to edit
     * @param channel The MIDI channel
     * @param column The column of the step
     * @param noteRow The row of the notes
     * @param normalizedValue The normalized absolute value to set (0..1)
     */
    protected abstract void handleKnobRow0 (final INoteClip clip, final int channel, final int column, final int noteRow, final double normalizedValue);


    /**
     * Implement to get the row of notes to edit.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @return The index of the row (0-127)
     */
    protected abstract int getNoteRow (int channel, int step);


    /**
     * Get the knob value for the 1st row knobs.
     * 
     * @param noteRow The note row
     * @param stepInfo The step info
     * @return The value
     */
    protected abstract int getKnobValueRow0 (final int noteRow, final IStepInfo stepInfo);


    /**
     * Get the first row color.
     * 
     * @param index The index
     * @return The color
     */
    protected abstract int getFirstRowColor (final int index);


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.isButtonCombination (ButtonID.DEVICE))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getCursorDevice ().selectPrevious ();
            this.mvHelper.notifySelectedDevice ();
            return;
        }

        if (this.isButtonCombination (ButtonID.SOLO))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getSceneBank ().selectPreviousPage ();
            this.mvHelper.notifyScenePage ();
            return;
        }

        final INoteClip clip = this.getClip ();
        clip.scrollStepsPageBackwards ();
        this.mvHelper.notifyEditPage (clip);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.surface.isPressed (ButtonID.DEVICE))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getCursorDevice ().selectNext ();
            this.mvHelper.notifySelectedDevice ();
            return;
        }

        if (this.surface.isPressed (ButtonID.SOLO))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getSceneBank ().selectNextPage ();
            this.mvHelper.notifyScenePage ();
            return;
        }

        final INoteClip clip = this.getClip ();
        clip.scrollStepsPageForward ();
        this.mvHelper.notifyEditPage (clip);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        if (this.surface.isPressed (ButtonID.DEVICE))
            return this.model.getCursorDevice ().canSelectPrevious ();

        if (this.surface.isPressed (ButtonID.SOLO))
            return this.model.getSceneBank ().canScrollPageBackwards ();

        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        if (this.surface.isPressed (ButtonID.DEVICE))
            return this.model.getCursorDevice ().canSelectNext ();

        if (this.surface.isPressed (ButtonID.SOLO))
            return this.model.getSceneBank ().canScrollPageForwards ();

        return true;
    }
}
