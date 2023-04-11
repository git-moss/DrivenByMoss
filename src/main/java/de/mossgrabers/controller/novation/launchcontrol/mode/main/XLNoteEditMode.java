package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;

import java.util.ArrayList;
import java.util.List;


/**
 * Note sequencer mode for the LauchControl XL.
 *
 * @author Jürgen Moßgraber
 */
public class XLNoteEditMode extends XLBaseNoteEditMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLNoteEditMode (final LaunchControlXLControlSurface surface, final IModel model, final int clipRows, final int clipCols, final List<ContinuousID> controls)
    {
        super ("Note Sequencer", surface, model, clipRows, clipCols, controls);

        this.defaultMode = Modes.NOTE_SEQUENCER;

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final IDisplay display = surface.getDisplay ();
        final List<IParameter> pitchParameters = new ArrayList<> (8);
        for (int i = 0; i < 8; i++)
            pitchParameters.add (new NoteParameter (i, NoteAttribute.PITCH, display, model, this, valueChanger));
        final IParameterProvider pitchParameterProvider = new FixedParameterProvider (pitchParameters);

        final IParameterProvider noteEditProvider = new CombinedParameterProvider (pitchParameterProvider, this.repeatParameterProvider, this.panParameterProvider);
        final IParameterProvider noteEditWithDeviceParamsProvider = new CombinedParameterProvider (pitchParameterProvider, this.repeatParameterProvider, this.deviceParameterProvider);
        final IParameterProvider shiftedParameterProvider = new CombinedParameterProvider (this.chanceParameterProvider, this.velocitySpreadParameterProvider, this.panParameterProvider);

        this.setParameterProviders (noteEditProvider, noteEditWithDeviceParamsProvider);
        this.setParameterProvider (ButtonID.REC_ARM, shiftedParameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    protected int getNoteRow (final int channel, final int step)
    {
        return this.getClip ().getHighestRow (channel, step);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeRow0 (final int index)
    {
        final int channel = this.configuration.getMidiEditChannel ();
        final int noteRow = this.getNoteRow (channel, index);
        final INoteClip clip = this.getClip ();
        if (noteRow == -1)
        {
            // Use the note of the currently selected scale base
            clip.toggleStep (new NotePosition (channel, index, 60 + this.scales.getScaleOffset ()), 127);
        }
        else
            clip.clearStep (new NotePosition (channel, index, noteRow));
    }


    /** {@inheritDoc} */
    @Override
    protected int getFirstRowColor (final int index)
    {
        final int channel = this.configuration.getMidiEditChannel ();
        final int noteRow = this.getNoteRow (channel, index);
        return noteRow < 0 ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.scales.prevScale ();
        this.configuration.setScale (this.scales.getScale ().getName ());
        this.mvHelper.notifyScale (this.scales);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.scales.nextScale ();
        this.configuration.setScale (this.scales.getScale ().getName ());
        this.mvHelper.notifyScale (this.scales);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.scales.hasPrevScale ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.scales.hasNextScale ();
    }


    /** {@inheritDoc} */
    @Override
    protected Modes getSequencerMode ()
    {
        return Modes.NOTE_SEQUENCER;
    }
}
