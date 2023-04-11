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
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.KeyManager;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Base sequencer mode for the LauchControl XL.
 *
 * @author Jürgen Moßgraber
 */
public abstract class XLBaseNoteEditMode extends XLAbstractMainMode<IItem> implements INoteMode
{
    protected final int                    clipRows;
    protected final int                    clipCols;
    protected final IHost                  host;
    protected final ISpecificDevice        firstInstrument;
    protected final Scales                 scales;
    protected final KeyManager             keyManager;
    protected final FixedParameterProvider chanceParameterProvider;
    protected final FixedParameterProvider repeatParameterProvider;
    protected final FixedParameterProvider velocitySpreadParameterProvider;
    protected final FixedParameterProvider panParameterProvider;
    protected final BankParameterProvider  deviceParameterProvider;


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
        this.deviceParameterProvider = new BankParameterProvider (this.firstInstrument.getParameterBank ());

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final IDisplay display = surface.getDisplay ();

        final List<IParameter> chanceParameters = new ArrayList<> (8);
        final List<IParameter> repeatParameters = new ArrayList<> (8);
        final List<IParameter> velocitySpreadParameters = new ArrayList<> (8);
        final List<IParameter> panParameters = new ArrayList<> (8);
        for (int i = 0; i < 8; i++)
        {
            chanceParameters.add (new NoteParameter (i, NoteAttribute.CHANCE, display, model, this, valueChanger));
            repeatParameters.add (new NoteParameter (i, NoteAttribute.REPEAT, display, model, this, valueChanger));
            velocitySpreadParameters.add (new NoteParameter (i, NoteAttribute.VELOCITY_SPREAD, display, model, this, valueChanger));
            panParameters.add (new NoteParameter (i, NoteAttribute.PANORAMA, display, model, this, valueChanger));
        }
        this.chanceParameterProvider = new FixedParameterProvider (chanceParameters);
        this.repeatParameterProvider = new FixedParameterProvider (repeatParameters);
        this.velocitySpreadParameterProvider = new FixedParameterProvider (velocitySpreadParameters);
        this.panParameterProvider = new FixedParameterProvider (panParameters);

        // Force clip creation
        this.getClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Row 2 starts with index 8, row 4 with 16.
        final int row = index / 8;

        // This is only triggered from the faders mode
        if (row != 3)
            return;

        final INoteClip clip = this.getClip ();
        final NotePosition notePosition = this.getNotePosition (index).get (0);
        if (notePosition.getNote () < 0)
            return;
        final IStepInfo stepInfo = clip.getStep (notePosition);
        if (stepInfo.getState () == StepState.OFF)
            return;

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final double normalizedValue = valueChanger.toNormalizedValue (value);
        clip.updateStepVelocity (notePosition, normalizedValue);
        final IDisplay display = this.surface.getDisplay ();
        display.notify ("Velocity: " + StringUtils.formatPercentage (normalizedValue));
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
                final Modes modeID = activeIDIgnoreTemporary == Modes.DEVICE_LAYER_MUTE ? this.getSequencerMode () : Modes.DEVICE_LAYER_MUTE;
                this.alternativeModeSelect (event, modeID, Modes.CONFIGURATION);
                break;

            // Solo button
            case 2:
                final Modes activeIDIgnoreTemporary2 = trackButtonModeManager.getActiveIDIgnoreTemporary ();
                final Modes modeID2 = activeIDIgnoreTemporary2 == Modes.DEVICE_LAYER_SOLO ? this.getSequencerMode () : Modes.DEVICE_LAYER_SOLO;
                this.alternativeModeSelect (event, modeID2, Modes.CLIP);
                break;

            // Record Arm button
            case 3:
                final Modes activeIDIgnoreTemporary3 = trackButtonModeManager.getActiveIDIgnoreTemporary ();
                final Modes modeID3 = activeIDIgnoreTemporary3 == Modes.LOOP_LENGTH ? this.getSequencerMode () : Modes.LOOP_LENGTH;
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


    /** {@inheritDoc} */
    @Override
    public final INoteClip getClip ()
    {
        return this.model.getNoteClip (this.clipCols, this.clipRows);
    }


    /**
     * Implement to get the row of notes to edit.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @return The index of the row (0-127)
     */
    protected abstract int getNoteRow (int channel, int step);


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


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotePosition (final int parameterIndex)
    {
        final int column = parameterIndex % 8;
        final int channel = this.configuration.getMidiEditChannel ();
        final int noteRow = this.getNoteRow (channel, column);
        return Collections.singletonList (new NotePosition (channel, column, noteRow));
    }


    /** {@inheritDoc} */
    @Override
    public void setNote (final INoteClip clip, final NotePosition notePosition)
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void addNote (final INoteClip clip, final NotePosition notePosition)
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearNotes ()
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotes ()
    {
        throw new UnsupportedOperationException ();
    }
}
