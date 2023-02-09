package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.main.XLBaseNoteEditMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode to set the sequencer step resolution.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLSequencerResolutionMode extends XLTemporaryButtonMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public XLSequencerResolutionMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Step Resolution", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row != 0 || event != ButtonEvent.DOWN)
            return;

        this.getClip ().setStepLength (Resolution.getValueAt (index));
        this.setResolution (index);
        this.model.getHost ().showNotification ("Step Resolution: " + Resolution.getNameAt (index));
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        return index == this.getResolution () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN_LO;
    }


    private INoteClip getClip ()
    {
        return ((XLBaseNoteEditMode) this.surface.getModeManager ().get (Modes.NOTE_SEQUENCER)).getClip ();
    }


    private int getResolution ()
    {
        return ((XLDrumSequencerMode) this.surface.getTrackButtonModeManager ().get (Modes.DRUM_SEQUENCER)).getSelectedResolutionIndex ();
    }


    private void setResolution (final int resolutionIndex)
    {
        ((XLDrumSequencerMode) this.surface.getTrackButtonModeManager ().get (Modes.DRUM_SEQUENCER)).setSelectedResolutionIndex (resolutionIndex);
    }
}
