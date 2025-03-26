// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.INoteEditor;
import de.mossgrabers.framework.mode.INoteEditorMode;
import de.mossgrabers.framework.mode.NoteEditor;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Edit a note from a sequencer step. Columns edit:
 * <ol>
 * <li>Velocity
 * <li>Velocity Spread
 * <li>Chance
 * <li>Gain
 * <li>Pan
 * <li>Pitch
 * <li>Timbre
 * <li>Pressure
 * </ol>
 *
 * @author Jürgen Moßgraber
 */
public class NoteEditView extends AbstractFaderView implements INoteEditorMode
{
    private static final int []    COLUMN_COLORS =
    {
        LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE,
        LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN,
        LaunchpadColorManager.LAUNCHPAD_COLOR_PINK,
        LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE,
        LaunchpadColorManager.LAUNCHPAD_COLOR_ORANGE,
        LaunchpadColorManager.LAUNCHPAD_COLOR_TURQUOISE,
        LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW,
        LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE
    };

    private final boolean []       columnPan;

    private final NoteParameter [] parameters;
    private final NoteParameter    muteParameter;
    private final NoteEditor       noteEditor    = new NoteEditor ();


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public NoteEditView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Note Edit", surface, model);

        final IValueChanger valueChanger = model.getValueChanger ();

        this.muteParameter = new NoteParameter (NoteAttribute.MUTE, null, model, this.noteEditor, valueChanger);
        this.parameters = new NoteParameter []
        {
            new NoteParameter (NoteAttribute.VELOCITY, null, model, this.noteEditor, valueChanger),
            new NoteParameter (NoteAttribute.VELOCITY_SPREAD, null, model, this.noteEditor, valueChanger),
            new NoteParameter (NoteAttribute.CHANCE, null, model, this.noteEditor, valueChanger),
            new NoteParameter (NoteAttribute.GAIN, null, model, this.noteEditor, valueChanger),
            new NoteParameter (NoteAttribute.PANNING, null, model, this.noteEditor, valueChanger),
            new NoteParameter (NoteAttribute.TRANSPOSE, null, model, this.noteEditor, valueChanger),
            new NoteParameter (NoteAttribute.TIMBRE, null, model, this.noteEditor, valueChanger),
            new NoteParameter (NoteAttribute.PRESSURE, null, model, this.noteEditor, valueChanger)
        };

        final IHost host = model.getHost ();

        this.columnPan = new boolean []
        {
            false,
            false,
            false,
            host.supports (NoteAttribute.GAIN),
            host.supports (NoteAttribute.PANNING),
            host.supports (NoteAttribute.TRANSPOSE),
            host.supports (NoteAttribute.TIMBRE),
            false
        };
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        // Clear the edited note
        this.noteEditor.clearNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        this.surface.setupFader (index, COLUMN_COLORS[index], this.columnPan[index]);

        // Prevent issue with catch mode by initializing fader value at setup
        this.onValueKnob (index, this.getFaderValue (index));
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Set immediately to prevent issue with relative scaling mode
        this.parameters[index].setValueImmediatly (value);
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        return this.parameters[index].getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        for (int i = 0; i < 8; i++)
            this.surface.setFaderValue (i, this.parameters[i].getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event == ButtonEvent.DOWN && buttonID == ButtonID.SCENE1)
            this.muteParameter.setNormalizedValue (this.muteParameter.getValue () > 0 ? 0 : 1);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE1)
            return this.muteParameter.getValue () > 0 ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;

        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public INoteEditor getNoteEditor ()
    {
        return this.noteEditor;
    }
}
