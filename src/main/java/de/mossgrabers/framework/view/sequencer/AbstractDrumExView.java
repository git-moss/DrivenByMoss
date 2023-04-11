package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.midi.INoteRepeat;


/**
 * Abstract implementation for a drum sequencer with additional features in the loop area (Mute,
 * Solo, etc.).
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractDrumExView<S extends IControlSurface<C>, C extends Configuration> extends AbstractDrumView<S, C>
{
    /** The color ID for the selection button - on state. */
    public static final String COLOR_EX_SELECT_ON              = "COLOR_EX_SELECT_ON";
    /** The color ID for the selection button - off state. */
    public static final String COLOR_EX_SELECT_OFF             = "COLOR_EX_SELECT_OFF";
    /** The color ID for the mute button - on state. */
    public static final String COLOR_EX_MUTE_ON                = "COLOR_EX_MUTE_ON";
    /** The color ID for the mute button - off state. */
    public static final String COLOR_EX_MUTE_OFF               = "COLOR_EX_MUTE_OFF";
    /** The color ID for the solo button - on state. */
    public static final String COLOR_EX_SOLO_ON                = "COLOR_EX_SOLO_ON";
    /** The color ID for the solo button - off state. */
    public static final String COLOR_EX_SOLO_OFF               = "COLOR_EX_SOLO_OFF";
    /** The color ID for the browse button - on state. */
    public static final String COLOR_EX_BROWSE_ON              = "COLOR_EX_BROWSE_ON";
    /** The color ID for the browse button - off state. */
    public static final String COLOR_EX_BROWSE_OFF             = "COLOR_EX_BROWSE_OFF";
    /** The color ID for the note repeat button - on state. */
    public static final String COLOR_EX_NOTE_REPEAT_ON         = "COLOR_EX_NOTE_REPEAT_ON";
    /** The color ID for the note repeat button - off state. */
    public static final String COLOR_EX_NOTE_REPEAT_OFF        = "COLOR_EX_NOTE_REPEAT_OFF";
    /** The color ID for the note repeat period button - on state. */
    public static final String COLOR_EX_NOTE_REPEAT_PERIOD_ON  = "COLOR_EX_NOTE_REPEAT_PERIOD_ON";
    /** The color ID for the note repeat period button - off state. */
    public static final String COLOR_EX_NOTE_REPEAT_PERIOD_OFF = "COLOR_EX_NOTE_REPEAT_PERIOD_OFF";
    /** The color ID for the note repeat length button - on state. */
    public static final String COLOR_EX_NOTE_REPEAT_LENGTH_ON  = "COLOR_EX_NOTE_REPEAT_LENGTH_ON";
    /** The color ID for the note repeat length button - off state. */
    public static final String COLOR_EX_NOTE_REPEAT_LENGTH_OFF = "COLOR_EX_NOTE_REPEAT_LENGTH_OFF";
    /** The color ID for the extra buttons toggle button - on state. */
    public static final String COLOR_EX_TOGGLE_ON              = "COLOR_EX_TOGGLE_ON";
    /** The color ID for the extra buttons toggle button - off state. */
    public static final String COLOR_EX_TOGGLE_OFF             = "COLOR_EX_TOGGLE_OFF";

    protected boolean          extraButtonsOn                  = false;
    protected boolean          noteRepeatPeriodOn              = false;
    protected boolean          noteRepeatLengthOn              = false;
    protected boolean          useExtraToggleButton            = true;
    protected int              firstExtraPad;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numSequencerLines The number of rows to use for the sequencer
     * @param numPlayLines The number of rows to use for playing
     * @param useDawColors True to use the drum machine pad colors for coloring the octaves
     */
    protected AbstractDrumExView (final String name, final S surface, final IModel model, final int numSequencerLines, final int numPlayLines, final boolean useDawColors)
    {
        super (name, surface, model, numSequencerLines, numPlayLines, useDawColors);

        this.firstExtraPad = (this.playRows - 2) * this.playColumns;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleLoopArea (final int pad, final int velocity)
    {
        if (!this.extraButtonsOn || pad < this.firstExtraPad)
        {
            super.handleLoopArea (pad, velocity);
            return;
        }

        if (velocity == 0)
            return;

        if (pad == this.firstExtraPad + 4)
        {
            this.surface.getConfiguration ().toggleNoteRepeatActive ();
            return;
        }

        if (pad == this.firstExtraPad + 5)
        {
            this.noteRepeatPeriodOn = !this.noteRepeatPeriodOn;
            if (this.noteRepeatPeriodOn)
                this.noteRepeatLengthOn = false;
            return;
        }

        if (pad == this.firstExtraPad + 6)
        {
            this.noteRepeatLengthOn = !this.noteRepeatLengthOn;
            if (this.noteRepeatLengthOn)
                this.noteRepeatPeriodOn = false;
        }
    }


    /**
     * Toggle the extra buttons on/off.
     */
    protected void toggleExtraButtons ()
    {
        this.extraButtonsOn = !this.extraButtonsOn;
    }


    /** {@inheritDoc} */
    @Override
    protected int getNumberOfAvailablePages ()
    {
        // Remove the last 8 buttons so we can use it for something else if extra buttons are active
        return super.getNumberOfAvailablePages () - (this.extraButtonsOn ? 8 : 0);
    }


    /** {@inheritDoc} */
    @Override
    protected void drawPages (final INoteClip clip, final boolean isActive)
    {
        super.drawPages (clip, isActive);

        // Draw the extra buttons
        final IPadGrid padGrid = this.surface.getPadGrid ();
        if (this.extraButtonsOn)
        {
            int row = this.allRows - 2;

            padGrid.lightEx (4, row, this.colorManager.getColorIndex (this.isSelectTrigger () ? COLOR_EX_SELECT_ON : COLOR_EX_SELECT_OFF));
            padGrid.lightEx (5, row, this.colorManager.getColorIndex (this.isMuteTrigger () ? COLOR_EX_MUTE_ON : COLOR_EX_MUTE_OFF));
            padGrid.lightEx (6, row, this.colorManager.getColorIndex (this.isSoloTrigger () ? COLOR_EX_SOLO_ON : COLOR_EX_SOLO_OFF));
            padGrid.lightEx (7, row, this.colorManager.getColorIndex (this.isBrowseTrigger () ? COLOR_EX_BROWSE_ON : COLOR_EX_BROWSE_OFF));

            final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();

            row++;
            padGrid.lightEx (4, row, this.colorManager.getColorIndex (noteRepeat.isActive () ? COLOR_EX_NOTE_REPEAT_ON : COLOR_EX_NOTE_REPEAT_OFF));
            padGrid.lightEx (5, row, this.colorManager.getColorIndex (this.noteRepeatPeriodOn ? COLOR_EX_NOTE_REPEAT_PERIOD_ON : COLOR_EX_NOTE_REPEAT_PERIOD_OFF));
            padGrid.lightEx (6, row, this.colorManager.getColorIndex (this.noteRepeatLengthOn ? COLOR_EX_NOTE_REPEAT_LENGTH_ON : COLOR_EX_NOTE_REPEAT_LENGTH_OFF));
        }

        if (this.useExtraToggleButton || this.extraButtonsOn)
            padGrid.lightEx (7, this.allRows - 1, this.colorManager.getColorIndex (this.extraButtonsOn ? COLOR_EX_TOGGLE_ON : COLOR_EX_TOGGLE_OFF));
    }
}
