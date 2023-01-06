// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Simulates the missing buttons (in contrast to Launchpad Pro) on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftView extends AbstractView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private static final String                                                    TAG_ACTIVE = "Active";

    final ConfiguredRecordCommand<LaunchpadControlSurface, LaunchpadConfiguration> configuredRecordCommand;
    final ConfiguredRecordCommand<LaunchpadControlSurface, LaunchpadConfiguration> configuredShiftedRecordCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);

        this.configuredRecordCommand = new ConfiguredRecordCommand<> (false, this.model, surface);
        this.configuredShiftedRecordCommand = new ConfiguredRecordCommand<> (true, this.model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final LaunchpadConfiguration configuration = this.surface.getConfiguration ();

        final ITransport transport = this.model.getTransport ();

        // Add tracks
        padGrid.light (97, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN);
        padGrid.light (98, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING);
        padGrid.light (99, LaunchpadColorManager.LAUNCHPAD_COLOR_TURQUOISE_CYAN);

        // Accent on/off
        padGrid.light (91, configuration.isAccentActive () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO);

        // New clip length
        final int clipLengthIndex = this.surface.getConfiguration ().getNewClipLength ();
        for (int i = 0; i < 8; i++)
            padGrid.light (36 + i, i == clipLengthIndex ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);

        // Note Repeat
        final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
        padGrid.light (87, noteRepeat.isActive () ? LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID_LO);

        // Note Repeat period
        final int periodIndex = Resolution.getMatch (noteRepeat.getPeriod ());
        padGrid.light (79, periodIndex == 0 ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);
        padGrid.light (71, periodIndex == 2 ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);
        padGrid.light (63, periodIndex == 4 ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);
        padGrid.light (55, periodIndex == 6 ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);

        padGrid.light (80, periodIndex == 1 ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);
        padGrid.light (72, periodIndex == 3 ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);
        padGrid.light (64, periodIndex == 5 ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);
        padGrid.light (56, periodIndex == 7 ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);

        // Note Repeat length
        final int lengthIndex = Resolution.getMatch (noteRepeat.getNoteLength ());
        padGrid.light (81, lengthIndex == 0 ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);
        padGrid.light (73, lengthIndex == 2 ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);
        padGrid.light (65, lengthIndex == 4 ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);
        padGrid.light (57, lengthIndex == 6 ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO);

        padGrid.light (82, lengthIndex == 1 ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);
        padGrid.light (74, lengthIndex == 3 ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);
        padGrid.light (66, lengthIndex == 5 ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);
        padGrid.light (58, lengthIndex == 7 ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO);

        // Stop all
        padGrid.light (51, LaunchpadColorManager.LAUNCHPAD_COLOR_RED);

        // Note Repeat Octave Range up/down
        padGrid.light (88, LaunchpadColorManager.LAUNCHPAD_COLOR_RED);
        padGrid.light (89, LaunchpadColorManager.LAUNCHPAD_COLOR_RED);

        for (int i = 90; i < 91; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        if (this.surface.isPro ())
        {
            for (int i = 44; i < 51; i++)
                padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            for (int i = 52; i < 55; i++)
                padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            for (int i = 59; i < 63; i++)
                padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            for (int i = 67; i < 71; i++)
                padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            for (int i = 75; i < 79; i++)
                padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            for (int i = 83; i < 87; i++)
                padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            for (int i = 92; i < 97; i++)
                padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            return;
        }

        // Record
        padGrid.light (44, this.configuredRecordCommand.isLit () ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO);
        padGrid.light (45, this.configuredShiftedRecordCommand.isLit () ? LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE : LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE);

        for (int i = 46; i < 51; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Play / New
        padGrid.light (52, transport.isPlaying () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO);
        padGrid.light (53, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING);

        padGrid.light (54, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        padGrid.light (59, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Duplicate
        if (configuration.isDuplicateModeActive ())
            padGrid.light (60, LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE, LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_BLUE, true);
        else
            padGrid.light (60, LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE);
        // Double
        padGrid.light (61, LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_ORCHID);

        padGrid.light (62, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        padGrid.light (67, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Quantize
        padGrid.light (68, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN);
        // Record Quantization
        padGrid.light (69, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING);

        padGrid.light (70, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        padGrid.light (75, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Delete
        if (configuration.isDeleteModeActive ())
            padGrid.light (76, LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA, LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_PINK, true);
        else
            padGrid.light (76, LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA);
        padGrid.light (77, transport.isLoop () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO);

        padGrid.light (78, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        padGrid.light (83, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Undo / Redo
        padGrid.light (84, LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER);
        padGrid.light (85, LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_YELLOW);

        padGrid.light (86, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Metronome
        padGrid.light (92, transport.isMetronomeOn () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO);
        // Tap Tempo
        padGrid.light (93, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING);

        for (int i = 94; i < 97; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final LaunchpadConfiguration configuration = this.surface.getConfiguration ();

        switch (note)
        {
            case 36, 37, 38, 39, 40, 41, 42, 43:
                final int newClipLength = note - 36;
                configuration.setNewClipLength (newClipLength);
                this.surface.getDisplay ().notify ("New clip length: " + AbstractConfiguration.getNewClipLengthValue (newClipLength));
                return;

            case 51:
                this.model.getCurrentTrackBank ().stop ();
                this.surface.getDisplay ().notify ("Stop");
                break;

            case 87:
                configuration.toggleNoteRepeatActive ();
                this.mvHelper.delayDisplay ( () -> "Note Repeat: " + (configuration.isNoteRepeatActive () ? TAG_ACTIVE : "Off"));
                return;

            case 79:
                this.setPeriod (0);
                return;
            case 80:
                this.setPeriod (1);
                return;
            case 71:
                this.setPeriod (2);
                return;
            case 72:
                this.setPeriod (3);
                return;
            case 63:
                this.setPeriod (4);
                return;
            case 64:
                this.setPeriod (5);
                return;
            case 55:
                this.setPeriod (6);
                return;
            case 56:
                this.setPeriod (7);
                return;

            case 81:
                this.setNoteLength (0);
                return;
            case 82:
                this.setNoteLength (1);
                return;
            case 73:
                this.setNoteLength (2);
                return;
            case 74:
                this.setNoteLength (3);
                return;
            case 65:
                this.setNoteLength (4);
                return;
            case 66:
                this.setNoteLength (5);
                return;
            case 57:
                this.setNoteLength (6);
                return;
            case 58:
                this.setNoteLength (7);
                return;

            case 88:
                final int octave = Math.max (0, configuration.getNoteRepeatOctave () - 1);
                configuration.setNoteRepeatOctave (octave);
                this.surface.getDisplay ().notify ("Note Repeat Octave Range: " + octave);
                break;
            case 89:
                final int octave2 = Math.min (8, configuration.getNoteRepeatOctave () + 1);
                configuration.setNoteRepeatOctave (octave2);
                this.surface.getDisplay ().notify ("Note Repeat Octave Range: " + octave2);
                break;

            case 91:
                final boolean enabled = !configuration.isAccentActive ();
                configuration.setAccentEnabled (enabled);
                this.surface.getDisplay ().notify ("Fixed Accent: " + (enabled ? "On" : "Off"));
                return;

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
                // Fall through to be handled below
                break;
        }

        if (this.surface.isPro ())
            return;

        switch (note)
        {
            case 92:
                this.simulateNormalButtonPress (ButtonID.METRONOME);
                this.mvHelper.delayDisplay ( () -> "Metronome: " + (this.model.getTransport ().isMetronomeOn () ? "On" : "Off"));
                break;
            case 93:
                this.simulateShiftedButtonPress (ButtonID.METRONOME);
                this.surface.getDisplay ().notify ("Tap Tempo");
                break;
            case 84:
                this.simulateNormalButtonPress (ButtonID.UNDO);
                this.surface.getDisplay ().notify ("Undo");
                break;
            case 85:
                this.simulateShiftedButtonPress (ButtonID.UNDO);
                this.surface.getDisplay ().notify ("Redo");
                break;
            case 76:
                configuration.toggleDeleteModeActive ();
                this.surface.getDisplay ().notify ("Delete " + (configuration.isDeleteModeActive () ? TAG_ACTIVE : "Off"));
                break;
            case 77:
                this.simulateShiftedButtonPress (ButtonID.DELETE);
                this.mvHelper.delayDisplay ( () -> "Arrangement Loop: " + (this.model.getTransport ().isLoop () ? "On" : "Off"));
                break;
            case 69:
                this.simulateShiftedButtonPress (ButtonID.QUANTIZE);
                break;
            case 68:
                this.simulateNormalButtonPress (ButtonID.QUANTIZE);
                this.surface.getDisplay ().notify ("Quantize");
                break;
            case 60:
                configuration.toggleDuplicateModeActive ();
                this.surface.getDisplay ().notify ("Duplicate " + (configuration.isDuplicateModeActive () ? TAG_ACTIVE : "Off"));
                break;
            case 61:
                this.simulateShiftedButtonPress (ButtonID.DUPLICATE);
                this.surface.getDisplay ().notify ("Double");
                break;
            case 52:
                this.simulateNormalButtonPress (ButtonID.PLAY);
                this.surface.getDisplay ().notify ("Play");
                break;
            case 53:
                this.simulateShiftedButtonPress (ButtonID.PLAY);
                this.surface.getDisplay ().notify ("New");
                break;
            case 44:
                this.configuredRecordCommand.execute (ButtonEvent.DOWN, 127);
                this.configuredRecordCommand.execute (ButtonEvent.UP, 0);
                break;
            case 45:
                this.configuredShiftedRecordCommand.execute (ButtonEvent.DOWN, 127);
                this.configuredShiftedRecordCommand.execute (ButtonEvent.UP, 0);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final boolean isPro = this.surface.isPro ();
        switch (buttonID)
        {
            case SCENE1:
                return isPro ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN;
            case SCENE2:
                return isPro ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY;
            case SCENE3:
                return isPro ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID;
            case SCENE4:
                return isPro ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
            case SCENE5:
                return isPro ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE;
            case SCENE6:
                return isPro ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW;
            case SCENE7:
                return isPro ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE;
            case SCENE8:
                return isPro ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : LaunchpadColorManager.LAUNCHPAD_COLOR_RED;
            default:
                return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        }
    }


    private void handleControlModes (final ButtonID commandID)
    {
        this.surface.getButton (commandID).getCommand ().execute (ButtonEvent.DOWN, 127);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPro () || event != ButtonEvent.DOWN)
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.handleControlModes (ButtonID.VOLUME);
                break;
            case SCENE2:
                this.handleControlModes (ButtonID.PAN_SEND);
                break;
            case SCENE3:
                this.handleControlModes (ButtonID.SENDS);
                break;
            case SCENE4:
                this.handleControlModes (ButtonID.TRACK);
                break;
            case SCENE5:
                this.handleControlModes (ButtonID.STOP_CLIP);
                break;
            case SCENE6:
                this.handleControlModes (ButtonID.MUTE);
                break;
            case SCENE7:
                this.handleControlModes (ButtonID.SOLO);
                break;
            case SCENE8:
                this.handleControlModes (ButtonID.REC_ARM);
                break;
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
        this.surface.getConfiguration ().setNoteRepeatLength (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Length: " + Resolution.getNameAt (index)), 100);
    }
}