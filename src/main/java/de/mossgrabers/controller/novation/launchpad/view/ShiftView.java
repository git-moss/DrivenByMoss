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
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractShiftView;

import java.util.EnumMap;
import java.util.Map;


/**
 * Simulates the missing buttons (in contrast to Launchpad Pro) on the grid.
 *
 * @author Jürgen Moßgraber
 */
public class ShiftView extends AbstractShiftView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private static final String                  TAG_ACTIVE    = "Active";
    private static final Map<ButtonID, ButtonID> CONTROL_MODES = new EnumMap<> (ButtonID.class);
    private static final Map<ButtonID, Integer>  MODE_COLORS   = new EnumMap<> (ButtonID.class);

    static
    {
        CONTROL_MODES.put (ButtonID.SCENE1, ButtonID.VOLUME);
        CONTROL_MODES.put (ButtonID.SCENE2, ButtonID.PAN_SEND);
        CONTROL_MODES.put (ButtonID.SCENE3, ButtonID.SENDS);
        CONTROL_MODES.put (ButtonID.SCENE4, ButtonID.TRACK);
        CONTROL_MODES.put (ButtonID.SCENE5, ButtonID.STOP_CLIP);
        CONTROL_MODES.put (ButtonID.SCENE6, ButtonID.MUTE);
        CONTROL_MODES.put (ButtonID.SCENE7, ButtonID.SOLO);
        CONTROL_MODES.put (ButtonID.SCENE8, ButtonID.REC_ARM);

        MODE_COLORS.put (ButtonID.SCENE1, Integer.valueOf (LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN));
        MODE_COLORS.put (ButtonID.SCENE2, Integer.valueOf (LaunchpadColorManager.LAUNCHPAD_COLOR_SKY));
        MODE_COLORS.put (ButtonID.SCENE3, Integer.valueOf (LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID));
        MODE_COLORS.put (ButtonID.SCENE4, Integer.valueOf (LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN));
        MODE_COLORS.put (ButtonID.SCENE5, Integer.valueOf (LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE));
        MODE_COLORS.put (ButtonID.SCENE6, Integer.valueOf (LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW));
        MODE_COLORS.put (ButtonID.SCENE7, Integer.valueOf (LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE));
        MODE_COLORS.put (ButtonID.SCENE8, Integer.valueOf (LaunchpadColorManager.LAUNCHPAD_COLOR_RED));
    }

    private final LaunchpadConfiguration                                                   configuration;
    private final ConfiguredRecordCommand<LaunchpadControlSurface, LaunchpadConfiguration> configuredRecordCommand;
    private final ConfiguredRecordCommand<LaunchpadControlSurface, LaunchpadConfiguration> configuredShiftedRecordCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);

        this.configuration = this.surface.getConfiguration ();

        this.configuredRecordCommand = new ConfiguredRecordCommand<> (false, this.model, surface);
        this.configuredShiftedRecordCommand = new ConfiguredRecordCommand<> (true, this.model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final ITransport transport = this.model.getTransport ();

        // Add tracks
        padGrid.light (97, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN);
        padGrid.light (98, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING);
        padGrid.light (99, LaunchpadColorManager.LAUNCHPAD_COLOR_TURQUOISE_CYAN);

        // Accent on/off
        padGrid.light (91, this.configuration.isAccentActive () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO);

        // New clip length
        final int clipLengthIndex = this.configuration.getNewClipLength ();
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
        padGrid.light (50, LaunchpadColorManager.LAUNCHPAD_COLOR_RED_AMBER);
        padGrid.light (51, LaunchpadColorManager.LAUNCHPAD_COLOR_RED);

        // Note Repeat Octave Range up/down
        padGrid.light (88, LaunchpadColorManager.LAUNCHPAD_COLOR_RED);
        padGrid.light (89, LaunchpadColorManager.LAUNCHPAD_COLOR_RED);

        for (int i = 90; i < 91; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        if (this.surface.isPro ())
        {
            for (int i = 44; i < 50; i++)
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

        for (int i = 46; i < 50; i++)
            padGrid.light (i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Play / New
        padGrid.light (52, transport.isPlaying () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO);
        padGrid.light (53, LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING);

        padGrid.light (54, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        padGrid.light (59, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        // Duplicate
        if (this.configuration.isDuplicateModeActive ())
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
        if (this.configuration.isDeleteModeActive ())
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

        this.setWasUsed ();

        switch (note)
        {
            case 36, 37, 38, 39, 40, 41, 42, 43:
                final int newClipLength = note - 36;
                this.configuration.setNewClipLength (newClipLength);
                this.surface.getDisplay ().notify ("New clip length: " + AbstractConfiguration.getNewClipLengthValue (newClipLength));
                break;

            case 50, 51:
                this.model.getCurrentTrackBank ().stop (note == 50);
                this.surface.getDisplay ().notify (note == 50 ? "Stop All Clips (Alt)" : "Stop All Clips");
                break;

            case 87:
                this.configuration.toggleNoteRepeatActive ();
                this.mvHelper.delayDisplay ( () -> "Note Repeat: " + (this.configuration.isNoteRepeatActive () ? TAG_ACTIVE : "Off"));
                break;

            case 79:
                this.setPeriod (0);
                break;
            case 80:
                this.setPeriod (1);
                break;
            case 71:
                this.setPeriod (2);
                break;
            case 72:
                this.setPeriod (3);
                break;
            case 63:
                this.setPeriod (4);
                break;
            case 64:
                this.setPeriod (5);
                break;
            case 55:
                this.setPeriod (6);
                break;
            case 56:
                this.setPeriod (7);
                break;

            case 81:
                this.setNoteLength (0);
                break;
            case 82:
                this.setNoteLength (1);
                break;
            case 73:
                this.setNoteLength (2);
                break;
            case 74:
                this.setNoteLength (3);
                break;
            case 65:
                this.setNoteLength (4);
                break;
            case 66:
                this.setNoteLength (5);
                break;
            case 57:
                this.setNoteLength (6);
                break;
            case 58:
                this.setNoteLength (7);
                break;

            case 88:
                final int octave = Math.max (0, this.configuration.getNoteRepeatOctave () - 1);
                this.configuration.setNoteRepeatOctave (octave);
                this.surface.getDisplay ().notify ("Note Repeat Octave Range: " + octave);
                break;
            case 89:
                final int octave2 = Math.min (8, this.configuration.getNoteRepeatOctave () + 1);
                this.configuration.setNoteRepeatOctave (octave2);
                this.surface.getDisplay ().notify ("Note Repeat Octave Range: " + octave2);
                break;

            case 91:
                final boolean enabled = !this.configuration.isAccentActive ();
                this.configuration.setAccentEnabled (enabled);
                this.surface.getDisplay ().notify ("Fixed Accent: " + (enabled ? "On" : "Off"));
                break;

            case 97:
                this.model.getTrackBank ().addChannel (ChannelType.INSTRUMENT);
                break;
            case 98:
                this.model.getTrackBank ().addChannel (ChannelType.AUDIO);
                break;
            case 99:
                this.model.getApplication ().addEffectTrack ();
                break;
            default:
                if (!this.surface.isPro ())
                    this.handleTransport (note);
                break;
        }
    }


    /**
     * Handle the transport area of the non-pro models.
     *
     * @param note The note of the pressed pad
     */
    private void handleTransport (final int note)
    {
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
                this.configuration.toggleDeleteModeActive ();
                this.surface.getDisplay ().notify ("Delete " + (this.configuration.isDeleteModeActive () ? TAG_ACTIVE : "Off"));
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
                this.configuration.toggleDuplicateModeActive ();
                this.surface.getDisplay ().notify ("Duplicate " + (this.configuration.isDuplicateModeActive () ? TAG_ACTIVE : "Off"));
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
        return this.surface.isPro () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK : MODE_COLORS.get (buttonID).intValue ();
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

        final ButtonID modeButton = CONTROL_MODES.get (buttonID);
        if (modeButton != null)
            this.handleControlModes (modeButton);
    }


    private void setPeriod (final int index)
    {
        this.configuration.setNoteRepeatPeriod (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Period: " + Resolution.getNameAt (index)), 100);
    }


    private void setNoteLength (final int index)
    {
        this.configuration.setNoteRepeatLength (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Length: " + Resolution.getNameAt (index)), 100);
    }
}