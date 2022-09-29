package de.mossgrabers.controller.yaeltex.turn.mode;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnColorManager;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.controller.yaeltex.turn.view.MonophonicSequencerView;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.NullParameterProvider;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * Track mix mode with note sequencer editing for the Yaeltex Turn.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class YaeltexTurnNoteSeqMode extends YaeltexTurnTrackMixMode implements INoteMode
{
    private static final NoteAttribute []                NOTE_ATTRIBUTES         =
    {
        NoteAttribute.PITCH,
        NoteAttribute.GAIN,
        NoteAttribute.PANORAMA,
        NoteAttribute.DURATION,
        NoteAttribute.VELOCITY,
        NoteAttribute.RELEASE_VELOCITY,
        NoteAttribute.VELOCITY_SPREAD,
        NoteAttribute.MUTE,
        NoteAttribute.PRESSURE,
        NoteAttribute.TIMBRE,
        NoteAttribute.CHANCE,
        NoteAttribute.OCCURRENCE,
        NoteAttribute.REPEAT,
        NoteAttribute.REPEAT_CURVE,
        NoteAttribute.REPEAT_VELOCITY_CURVE,
        NoteAttribute.REPEAT_VELOCITY_END
    };

    private final Map<NoteAttribute, IParameterProvider> noteEditProviders       = new EnumMap<> (NoteAttribute.class);
    private final YaeltexTurnConfiguration               configuration;
    private NoteAttribute                                noteEditParameter       = NoteAttribute.PITCH;
    private int                                          selectedResolutionIndex = 4;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public YaeltexTurnNoteSeqMode (final YaeltexTurnControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Monophonic Sequencer", surface, model, controls);

        this.configuration = this.surface.getConfiguration ();

        final IValueChanger valueChanger = model.getValueChanger ();
        final IDisplay display = surface.getDisplay ();

        for (final NoteAttribute attribute: NoteAttribute.values ())
        {
            final List<IParameter> parameters = new ArrayList<> (32);
            for (int i = 0; i < 32; i++)
                parameters.add (new NoteParameter (i, attribute, display, model, this, valueChanger));
            this.noteEditProviders.put (attribute, new FixedParameterProvider (parameters));
        }

        this.providers.add (0, new NullParameterProvider (32));

        this.rebind ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.selectedResolutionIndex = Resolution.getMatch (this.getClip ().getStepLength ());

        super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        return this.noteEditProviders.get (this.noteEditParameter).get (index).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getKnobColor (final int index)
    {
        return YaeltexTurnColorManager.COLOR_TABLE[YaeltexTurnColorManager.NOTE_PARAM_COLORS.get (this.noteEditParameter).intValue ()];
    }


    /** {@inheritDoc} */
    @Override
    protected void onShiftButton (final int row, final int index)
    {
        final IHost host = this.model.getHost ();

        if (row == 0 || row == 1)
        {
            final int noteParamIndex = 2 * index + row;
            this.noteEditParameter = NOTE_ATTRIBUTES[noteParamIndex];
            this.rebind ();
            host.showNotification (this.noteEditProviders.get (this.noteEditParameter).get (0).getName ());
            return;
        }

        if (row == 2 || row == 3)
        {
            // Intentionally empty
            return;
        }

        final INoteClip clip = this.getClip ();

        if (row == 4)
        {
            this.selectedResolutionIndex = index;
            final Resolution resolution = Resolution.values ()[index];
            clip.setStepLength (resolution.getValue ());
            host.showNotification ("Grid res.: " + resolution.getName ());
        }

        if (row == 5 && clip.getEditPage () != index)
        {
            clip.scrollToPage (index);
            host.showNotification ("Edit Page: " + (index + 1));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected int getButtonShiftColor (final ButtonID buttonID)
    {
        int noteParamIndex = -1;
        int onOffIndex = -1;
        switch (buttonID)
        {
            case ROW1_1, ROW1_2, ROW1_3, ROW1_4, ROW1_5, ROW1_6, ROW1_7, ROW1_8:
                noteParamIndex = 2 * (buttonID.ordinal () - ButtonID.ROW1_1.ordinal ());
                break;
            case ROW2_1, ROW2_2, ROW2_3, ROW2_4, ROW2_5, ROW2_6, ROW2_7, ROW2_8:
                noteParamIndex = 2 * (buttonID.ordinal () - ButtonID.ROW2_1.ordinal ()) + 1;
                break;
            case ROW3_1, ROW3_2, ROW3_3, ROW3_4, ROW3_5, ROW3_6, ROW3_7, ROW3_8:
                onOffIndex = 2 * (buttonID.ordinal () - ButtonID.ROW3_1.ordinal ());
                break;
            case ROW4_1, ROW4_2, ROW4_3, ROW4_4, ROW4_5, ROW4_6, ROW4_7, ROW4_8:
                onOffIndex = 2 * (buttonID.ordinal () - ButtonID.ROW4_1.ordinal ()) + 1;
                break;
            case ROW5_1, ROW5_2, ROW5_3, ROW5_4, ROW5_5, ROW5_6, ROW5_7, ROW5_8:
                final int index = buttonID.ordinal () - ButtonID.ROW5_1.ordinal ();
                return this.selectedResolutionIndex == index ? YaeltexTurnColorManager.GREEN : YaeltexTurnColorManager.WHITE;
            case ROW6_1, ROW6_2, ROW6_3, ROW6_4, ROW6_5, ROW6_6, ROW6_7, ROW6_8:
                final int page = buttonID.ordinal () - ButtonID.ROW6_1.ordinal ();
                return this.getClip ().getEditPage () == page ? YaeltexTurnColorManager.BLUE : YaeltexTurnColorManager.WHITE;

            default:
                break;
        }
        if (noteParamIndex != -1)
            return YaeltexTurnColorManager.NOTE_PARAM_COLORS.get (NOTE_ATTRIBUTES[noteParamIndex]).intValue ();

        if (onOffIndex != -1)
        {
            if (NOTE_ATTRIBUTES[onOffIndex] == NoteAttribute.CHANCE || NOTE_ATTRIBUTES[onOffIndex] == NoteAttribute.OCCURRENCE || NOTE_ATTRIBUTES[onOffIndex] == NoteAttribute.REPEAT)
                return YaeltexTurnColorManager.NOTE_PARAM_COLORS.get (NOTE_ATTRIBUTES[onOffIndex]).intValue ();
            return 0;
        }

        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getClip ()
    {
        return ((MonophonicSequencerView) this.surface.getViewManager ().get (Views.SEQUENCER)).getClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearNotes ()
    {
        throw new UnsupportedOperationException ();
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
    public List<NotePosition> getNotes ()
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotePosition (final int parameterIndex)
    {
        final int channel = this.configuration.getMidiEditChannel ();
        final int noteRow = this.getClip ().getHighestRow (channel, parameterIndex);
        if (noteRow == -1)
            return Collections.emptyList ();
        return Collections.singletonList (new NotePosition (channel, parameterIndex, noteRow));
    }


    private void rebind ()
    {
        this.providers.set (0, this.noteEditProviders.get (this.noteEditParameter));
        this.setParameterProvider (new CombinedParameterProvider (this.providers));
        this.bindControls ();
    }
}
