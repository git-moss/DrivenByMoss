package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLDrumSequencerMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTemporaryButtonMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.StepState;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.KeyManager;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;


/**
 * Drum sequencer mode for the LauchControl XL.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLNoteEditMode extends XLAbstractMainMode<IItem>
{
    private final int                          clipRows;
    private final int                          clipCols;
    private final IHost                        host;
    private final LaunchControlXLConfiguration configuration;
    private final ISpecificDevice              firstInstrument;
    private final Scales                       scales;
    private final KeyManager                   keyManager;

    private int                                channel = 0;
    private int                                note;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     */
    public XLNoteEditMode (final LaunchControlXLControlSurface surface, final IModel model, final int clipRows, final int clipCols)
    {
        super ("Drum Sequencer", surface, model, null, null);

        this.host = model.getHost ();

        this.scales = model.getScales ();
        this.keyManager = new KeyManager (model, this.scales, surface.getPadGrid ());

        this.configuration = this.surface.getConfiguration ();
        this.firstInstrument = model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);

        this.note = this.model.getScales ().getDrumOffset ();

        this.clipRows = clipRows;
        this.clipCols = clipCols;

        this.defaultMode = Modes.DRUM_SEQUENCER;

        // Force clip creation
        this.getClip ();
    }


    /**
     * Set the values.
     *
     * @param channel The MIDI channel
     * @param note The note to edit
     */
    public void setValues (final int channel, final int note)
    {
        this.channel = channel;
        this.note = note;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Row 2 starts with index 8, row 4 with 16.
        final int row = index / 8;
        final int column = index % 8;

        final INoteClip clip = this.getClip ();
        final IStepInfo stepInfo = clip.getStep (this.channel, column, this.note);
        if (stepInfo.getState () == StepState.OFF)
            return;

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final double normalizedValue = valueChanger.toNormalizedValue (value);

        final IDisplay display = this.surface.getDisplay ();

        switch (row)
        {
            case 0:
                if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                {
                    clip.updateChance (this.channel, column, this.note, normalizedValue);
                    display.notify (String.format ("Chance: %d%%", Integer.valueOf ((int) Math.round (normalizedValue * 100))));
                }
                break;

            case 1:
                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                {
                    final int v = (int) Math.round ((value - 64) / 4.26);
                    clip.updateRepeatCount (this.channel, column, this.note, v);
                    display.notify ("Repeat Count: " + stepInfo.getFormattedRepeatCount ());
                }
                break;

            case 2:
                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                {
                    final double pan = normalizedValue * 2.0 - 1.0;
                    clip.updateStepPan (this.channel, column, this.note, pan);
                    display.notify ("Panorama: " + StringUtils.formatPercentage (pan));
                }
                break;

            case 3:
                clip.updateStepVelocity (this.channel, column, this.note, normalizedValue);
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

        final INoteClip clip = this.getClip ();
        final IStepInfo stepInfo = clip.getStep (this.channel, column, this.note);
        final IValueChanger valueChanger = this.model.getValueChanger ();

        if (stepInfo.getState () == StepState.OFF)
            return 0;

        switch (row)
        {
            case 0:
                if (this.host.supports (Capability.NOTE_EDIT_CHANCE))
                    return (int) (stepInfo.getChance () * 127);
                return 0;

            case 1:
                if (this.host.supports (Capability.NOTE_EDIT_REPEAT))
                {
                    final int repeatCount = stepInfo.getRepeatCount ();
                    return Math.min (127, (repeatCount + 128) / 2);
                }
                return 0;

            case 2:
                if (this.configuration.isDeviceActive ())
                    return this.firstInstrument.getParameterBank ().getItem (column).getValue ();

                if (this.host.supports (Capability.NOTE_EDIT_EXPRESSIONS))
                    return valueChanger.fromNormalizedValue ((stepInfo.getPan () + 1.0) / 2.0);
                return 0;

            case 3:
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
    protected void handleRow0 (final int index, final ButtonEvent event)
    {
        final ModeManager modeManager = this.surface.getFaderModeManager ();

        if (event == ButtonEvent.DOWN)
        {
            modeManager.setTemporary (Modes.MASTER);
            this.wasLong = false;
            return;
        }

        if (event == ButtonEvent.LONG)
        {
            this.wasLong = true;
            return;
        }

        if (event == ButtonEvent.UP)
        {
            modeManager.restore ();

            if (this.wasLong)
                return;

            ((XLDrumSequencerMode) this.surface.getTrackButtonModeManager ().get (Modes.DRUM_SEQUENCER)).setSelectedPad (index);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void handleRow2 (final int index, final ButtonEvent event)
    {
        // TODO
    }


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
                return trackModeManager.isActive (Modes.MUTE) ? 127 : 0;

            case SOLO:
                return trackModeManager.isActive (Modes.SOLO) ? 127 : 0;

            case REC_ARM:
                return trackModeManager.isActive (Modes.REC_ARM) ? 127 : 0;

            case ROW1_1, ROW1_2, ROW1_3, ROW1_4, ROW1_5, ROW1_6, ROW1_7, ROW1_8:
                final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
                final boolean isRecording = this.model.hasRecordingState ();
                final IDrumDevice drumDevice = this.model.getDrumDevice ();
                final String drumPadColor = this.getDrumPadColor (index, drumDevice.getDrumPadBank (), isRecording);
                return this.colorManager.getColorIndex (drumPadColor);

            default:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        }
    }


    /**
     * Get the clip.
     *
     * @return The clip
     */
    private final INoteClip getClip ()
    {
        return this.model.getNoteClip (this.clipCols, this.clipRows);
    }


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


    protected String getDrumPadColor (final int index, final IDrumPadBank drumPadBank, final boolean isRecording)
    {
        final int offsetY = this.scales.getDrumOffset ();

        // Playing note?
        if (this.keyManager.isKeyPressed (offsetY + index))
            return isRecording ? AbstractDrumView.COLOR_PAD_RECORD : AbstractDrumView.COLOR_PAD_PLAY;

        // Selected?
        final int selectedPad = ((XLDrumSequencerMode) this.surface.getTrackButtonModeManager ().get (Modes.DRUM_SEQUENCER)).getSelectedPad ();
        if (selectedPad == index)
            return AbstractDrumView.COLOR_PAD_SELECTED;

        // Exists and active?
        final IChannel drumPad = drumPadBank.getItem (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return this.surface.getConfiguration ().isTurnOffEmptyDrumPads () ? AbstractDrumView.COLOR_PAD_OFF : AbstractDrumView.COLOR_PAD_NO_CONTENT;

        // Muted or soloed?
        if (drumPad.isMute () || drumPadBank.hasSoloedPads () && !drumPad.isSolo ())
            return AbstractDrumView.COLOR_PAD_MUTED;
        return AbstractDrumView.COLOR_PAD_HAS_CONTENT;
    }
}
