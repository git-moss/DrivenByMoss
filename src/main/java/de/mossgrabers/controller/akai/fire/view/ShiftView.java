// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireColorManager;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractShiftView;


/**
 * Provides several additional functions and settings.
 *
 * @author Jürgen Moßgraber
 */
public class ShiftView extends AbstractShiftView<FireControlSurface, FireConfiguration> implements IFireView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final FireControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final IHost host = this.model.getHost ();

        // Note Repeat
        final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();

        // - on/off
        padGrid.light (84, DAWColor.getColorID ((noteRepeat.isActive () ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));

        // Octave
        if (host.supports (Capability.NOTE_REPEAT_OCTAVES))
        {
            final int octaves = noteRepeat.getOctaves ();
            for (int i = 0; i < 4; i++)
            {
                padGrid.light (36 + i, DAWColor.getColorID ((octaves == i ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));
                padGrid.light (52 + i, DAWColor.getColorID ((octaves == 4 + i ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));
            }
            padGrid.light (68, DAWColor.getColorID ((octaves == 8 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));
        }
        else
        {
            for (int i = 0; i < 4; i++)
            {
                padGrid.light (36 + i, 0);
                padGrid.light (52 + i, 0);
            }
            padGrid.light (68, 0);
        }

        padGrid.light (69, 0);
        padGrid.light (70, 0);
        padGrid.light (71, 0);
        padGrid.light (85, 0);

        // Dec/Inc Arp Mode
        if (host.supports (Capability.NOTE_REPEAT_MODE))
        {
            padGrid.light (86, DAWColor.getColorID (ColorEx.WHITE));
            padGrid.light (87, DAWColor.getColorID (ColorEx.WHITE));
        }
        else
        {
            padGrid.light (86, DAWColor.getColorID (ColorEx.BLACK));
            padGrid.light (87, DAWColor.getColorID (ColorEx.BLACK));
        }

        // Note Repeat period
        final int periodIndex = Resolution.getMatch (noteRepeat.getPeriod ());
        padGrid.light (88, DAWColor.getColorID ((periodIndex == 0 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (72, DAWColor.getColorID ((periodIndex == 2 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (56, DAWColor.getColorID ((periodIndex == 4 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (40, DAWColor.getColorID ((periodIndex == 6 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));

        padGrid.light (89, DAWColor.getColorID ((periodIndex == 1 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (73, DAWColor.getColorID ((periodIndex == 3 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (57, DAWColor.getColorID ((periodIndex == 5 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (41, DAWColor.getColorID ((periodIndex == 7 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));

        // Note Repeat length
        if (this.model.getHost ().supports (Capability.NOTE_REPEAT_LENGTH))
        {
            final int lengthIndex = Resolution.getMatch (noteRepeat.getNoteLength ());
            padGrid.light (90, DAWColor.getColorID ((lengthIndex == 0 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
            padGrid.light (74, DAWColor.getColorID ((lengthIndex == 2 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
            padGrid.light (58, DAWColor.getColorID ((lengthIndex == 4 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
            padGrid.light (42, DAWColor.getColorID ((lengthIndex == 6 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));

            padGrid.light (91, DAWColor.getColorID ((lengthIndex == 1 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
            padGrid.light (75, DAWColor.getColorID ((lengthIndex == 3 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
            padGrid.light (59, DAWColor.getColorID ((lengthIndex == 5 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
            padGrid.light (43, DAWColor.getColorID ((lengthIndex == 7 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        }
        else
        {
            padGrid.light (90, 0);
            padGrid.light (74, 0);
            padGrid.light (58, 0);
            padGrid.light (42, 0);

            padGrid.light (91, 0);
            padGrid.light (75, 0);
            padGrid.light (59, 0);
            padGrid.light (43, 0);
        }

        // New clip length
        final FireConfiguration configuration = this.surface.getConfiguration ();
        final int clipLengthIndex = configuration.getNewClipLength ();
        for (int i = 0; i < 8; i++)
            padGrid.light (44 + i, DAWColor.getColorID ((i == clipLengthIndex ? DAWColor.DAW_COLOR_RED : DAWColor.DAW_COLOR_LIGHT_ORANGE).getColor ()));

        // Not used
        for (int i = 0; i < 8; i++)
        {
            padGrid.light (60 + i, 0);
            padGrid.light (76 + i, 0);
        }

        padGrid.light (92, 0);
        padGrid.light (96, 0);

        // Duplicate
        if (configuration.isDuplicateModeActive ())
            padGrid.light (93, FireColorManager.FIRE_COLOR_DARK_OCEAN, FireColorManager.FIRE_COLOR_BLUE, true);
        else
            padGrid.light (93, FireColorManager.FIRE_COLOR_DARK_OCEAN);

        padGrid.light (94, FireColorManager.FIRE_COLOR_GREEN);

        // Delete
        if (configuration.isDeleteModeActive ())
            padGrid.light (95, FireColorManager.FIRE_COLOR_DARK_RED, FireColorManager.FIRE_COLOR_RED, true);
        else
            padGrid.light (95, FireColorManager.FIRE_COLOR_DARK_RED);

        // Add tracks
        padGrid.light (97, DAWColor.getColorID (ColorEx.ORANGE));
        padGrid.light (98, DAWColor.getColorID (ColorEx.BLUE));
        padGrid.light (99, DAWColor.getColorID (ColorEx.PINK));
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        this.setWasUsed ();

        final FireConfiguration configuration = this.surface.getConfiguration ();
        final IHost host = this.model.getHost ();

        switch (note)
        {
            case 36, 37, 38, 39:
                this.setNoteRepeatOctave (note - 36);
                break;
            case 52, 53, 54, 55:
                this.setNoteRepeatOctave (note - 48);
                break;
            case 68:
                this.setNoteRepeatOctave (note - 60);
                break;

            case 86:
                if (host.supports (Capability.NOTE_REPEAT_MODE))
                {
                    final ArpeggiatorMode prevArpeggiatorMode = configuration.prevArpeggiatorMode ();
                    configuration.setNoteRepeatMode (prevArpeggiatorMode);
                    this.mvHelper.delayDisplay ( () -> "Arp: " + prevArpeggiatorMode.getName ());
                }
                break;
            case 87:
                if (host.supports (Capability.NOTE_REPEAT_MODE))
                {
                    final ArpeggiatorMode nextArpeggiatorMode = configuration.nextArpeggiatorMode ();
                    configuration.setNoteRepeatMode (nextArpeggiatorMode);
                    this.mvHelper.delayDisplay ( () -> "Arp: " + nextArpeggiatorMode.getName ());
                }
                break;

            case 84:
                configuration.toggleNoteRepeatActive ();
                this.mvHelper.delayDisplay ( () -> "Note Repeat: " + (configuration.isNoteRepeatActive () ? "On" : "Off"));
                break;

            case 88:
                this.setPeriod (0);
                break;
            case 89:
                this.setPeriod (1);
                break;
            case 72:
                this.setPeriod (2);
                break;
            case 73:
                this.setPeriod (3);
                break;
            case 56:
                this.setPeriod (4);
                break;
            case 57:
                this.setPeriod (5);
                break;
            case 40:
                this.setPeriod (6);
                break;
            case 41:
                this.setPeriod (7);
                break;

            case 90:
                this.setNoteLength (0);
                break;
            case 91:
                this.setNoteLength (1);
                break;
            case 74:
                this.setNoteLength (2);
                break;
            case 75:
                this.setNoteLength (3);
                break;
            case 58:
                this.setNoteLength (4);
                break;
            case 59:
                this.setNoteLength (5);
                break;
            case 42:
                this.setNoteLength (6);
                break;
            case 43:
                this.setNoteLength (7);
                break;

            case 44, 45, 46, 47, 48, 49, 50, 51:
                final int newClipLength = note - 44;
                configuration.setNewClipLength (newClipLength);
                this.surface.getDisplay ().notify ("Clip len: " + AbstractConfiguration.getNewClipLengthValue (newClipLength));
                break;

            case 93:
                configuration.toggleDuplicateModeActive ();
                this.surface.getDisplay ().notify ("Duplicate " + (configuration.isDuplicateModeActive () ? "Active" : "Off"));
                break;

            case 94:
                final IClip clip = this.model.getCursorClip ();
                if (clip.doesExist ())
                {
                    clip.duplicateContent ();
                    this.surface.getDisplay ().notify ("Double clip");
                }
                else
                    this.surface.getDisplay ().notify ("No clip.");

                break;

            case 95:
                configuration.toggleDeleteModeActive ();
                this.surface.getDisplay ().notify ("Delete " + (configuration.isDeleteModeActive () ? "Active" : "Off"));
                break;

            case 97:
                this.model.getTrackBank ().addChannel (ChannelType.INSTRUMENT);
                return;
            case 98:
                this.model.getTrackBank ().addChannel (ChannelType.AUDIO);
                return;
            case 99:
                this.model.getApplication ().addEffectTrack ();
                return;

            default:
                // Not used
                break;
        }
    }


    private void setPeriod (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatPeriod (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Period: " + Resolution.getNameAt (index)), 100);
    }


    private void setNoteLength (final int index)
    {
        if (this.model.getHost ().supports (Capability.NOTE_REPEAT_LENGTH))
        {
            this.surface.getConfiguration ().setNoteRepeatLength (Resolution.values ()[index]);
            this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Len: " + Resolution.getNameAt (index)), 100);
        }
    }


    private void setNoteRepeatOctave (final int octave)
    {
        if (this.model.getHost ().supports (Capability.NOTE_REPEAT_OCTAVES))
        {
            final FireConfiguration configuration = this.surface.getConfiguration ();
            configuration.setNoteRepeatOctave (octave);
            this.mvHelper.delayDisplay ( () -> "Octave: " + configuration.getNoteRepeatOctave ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        return viewManager.get (viewManager.getActiveIDIgnoreTemporary ()).getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        // Relay to the actually active view
        final ViewManager viewManager = this.surface.getViewManager ();
        viewManager.get (viewManager.getActiveIDIgnoreTemporary ()).onButton (buttonID, event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        // Relay to the actually active view
        final ViewManager viewManager = this.surface.getViewManager ();
        final IView previousView = viewManager.get (viewManager.getActiveIDIgnoreTemporary ());
        if (previousView instanceof final IFireView fireView)
            fireView.onSelectKnobValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        // Relay to the actually active view
        final ViewManager viewManager = this.surface.getViewManager ();
        final IView previousView = viewManager.get (viewManager.getActiveIDIgnoreTemporary ());
        return previousView instanceof final IFireView fireView ? fireView.getSoloButtonColor (index) : 0;
    }
}