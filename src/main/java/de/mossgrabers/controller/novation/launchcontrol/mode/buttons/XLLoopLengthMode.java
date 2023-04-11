package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.main.XLBaseNoteEditMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode to set the clips' loop length.
 *
 * @author Jürgen Moßgraber
 */
public class XLLoopLengthMode extends AbstractMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    private static final int SEQUENCER_STEPS = 8;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public XLLoopLengthMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Set loop length", surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row != 0 || event != ButtonEvent.DOWN)
            return;

        final INoteClip clip = this.getClip ();
        final int steps = clip.getEditPage () * SEQUENCER_STEPS + index + 1;
        clip.setLoopLength (steps * Resolution.getValueAt (this.getResolution ()));
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        final INoteClip clip = this.getClip ();
        int numberOfActiveSteps = (int) Math.floor (clip.getLoopLength () / Resolution.getValueAt (this.getResolution ()));
        numberOfActiveSteps -= clip.getEditPage () * SEQUENCER_STEPS;
        if (index >= numberOfActiveSteps)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        return index == numberOfActiveSteps - 1 ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER_LO;
    }


    private INoteClip getClip ()
    {
        return ((XLBaseNoteEditMode) this.surface.getModeManager ().get (Modes.DRUM_SEQUENCER)).getClip ();
    }


    private int getResolution ()
    {
        return ((XLDrumSequencerMode) this.surface.getTrackButtonModeManager ().get (Modes.DRUM_SEQUENCER)).getSelectedResolutionIndex ();
    }
}
