// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;


/**
 * The configuration mode to change the note repeat (arpeggiator) settings.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneRepeatModeConfigurationMode extends AbstractMode<OxiOneControlSurface, OxiOneConfiguration> implements IOxiModeReset
{
    private static final String [] MENU          =
    {
        "Rept",
        "Peri",
        "Lnth",
        "Mode"
    };

    private static final String [] SHIFTED_MENU  =
    {
        "Rnge",
        "Hold",
        "",
        ""
    };

    private int                    selectedIndex = 0;
    private final IHost            host;
    private final Configuration    configuration;
    private final INoteRepeat      noteRepeat;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneRepeatModeConfigurationMode (final OxiOneControlSurface surface, final IModel model)
    {
        super ("RepeatConfiguration", surface, model, false);

        this.host = this.surface.getHost ();
        this.configuration = this.surface.getConfiguration ();
        this.noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateSelectedIndex ();

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final String desc = "";
        String label = "";
        int value = -1;

        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        switch (this.selectedIndex)
        {
            case 0:
                final boolean isRepeatActive = this.configuration.isNoteRepeatActive ();
                label = "Note Repeat: " + (isRepeatActive ? "On" : "Off");
                value = isRepeatActive ? upperBound : 0;
                break;

            case 1:
                final Resolution noteRepeatPeriod = this.configuration.getNoteRepeatPeriod ();
                label = "Period: " + noteRepeatPeriod.getName ();
                value = (int) (noteRepeatPeriod.ordinal () / (double) (Resolution.values ().length - 1) * upperBound);
                break;

            case 2:
                if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
                {
                    final Resolution noteRepeatLength = this.configuration.getNoteRepeatLength ();
                    label = "Length: " + noteRepeatLength.getName ();
                    value = (int) (noteRepeatLength.ordinal () / (double) (Resolution.values ().length - 1) * upperBound);
                }
                break;

            case 3:
                if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                {
                    final ArpeggiatorMode noteRepeatMode = this.configuration.getNoteRepeatMode ();
                    label = "Mode: " + noteRepeatMode.getName ();
                    value = (int) (noteRepeatMode.ordinal () / (double) (ArpeggiatorMode.values ().length - 1) * upperBound);
                }
                break;

            case 4:
                if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                {
                    final int noteRepeatOctave = this.configuration.getNoteRepeatOctave ();
                    label = "Octave Range: " + noteRepeatOctave;
                    value = (int) (noteRepeatOctave / (double) 8 * upperBound);
                }
                break;

            case 5:
                if (this.host.supports (Capability.NOTE_REPEAT_LATCH))
                {
                    final boolean isLatchActive = this.noteRepeat.isLatchActive ();
                    label = "Hold: " + (isLatchActive ? "On" : "Off");
                    value = isLatchActive ? upperBound : 0;
                }
                break;

            default:
                // Not used
                break;
        }

        display.addElement (new TitleValueMenuComponent (desc, label, this.surface.isShiftPressed () ? SHIFTED_MENU : MENU, value, 0, 0, false));
        display.send ();
    }


    private void updateSelectedIndex ()
    {
        if (this.surface.isPressed (ButtonID.SHIFT))
        {
            if (this.selectedIndex < 4)
                this.selectedIndex += 4;
        }
        else
        {
            if (this.selectedIndex >= 4)
                this.selectedIndex -= 4;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.selectedIndex = this.surface.isPressed (ButtonID.SHIFT) ? index + 4 : index;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final boolean isInc = valueChanger.isIncrease (value);

        switch (this.selectedIndex)
        {
            case 0:
                this.configuration.setNoteRepeatActive (isInc);
                break;

            case 1:
                final int sel = Resolution.change (Resolution.getMatch (this.configuration.getNoteRepeatPeriod ().getValue ()), isInc);
                this.configuration.setNoteRepeatPeriod (Resolution.values ()[sel]);
                break;

            case 2:
                if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
                {
                    final int sel2 = Resolution.change (Resolution.getMatch (this.configuration.getNoteRepeatLength ().getValue ()), valueChanger.calcKnobChange (value) > 0);
                    this.configuration.setNoteRepeatLength (Resolution.values ()[sel2]);
                }
                break;

            case 3:
                if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                    this.configuration.setPrevNextNoteRepeatMode (valueChanger.isIncrease (value));
                break;

            case 4:
                if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                    this.configuration.setNoteRepeatOctave (this.configuration.getNoteRepeatOctave () + (valueChanger.calcKnobChange (value) > 0 ? 1 : -1));
                break;

            case 5:
                if (this.host.supports (Capability.NOTE_REPEAT_LATCH))
                    this.noteRepeat.setLatchActive (valueChanger.isIncrease (value));
                break;

            default:
                // Not used
                return;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue (final int index)
    {
        switch (this.selectedIndex)
        {
            case 0:
                this.configuration.setNoteRepeatActive (false);
                break;

            case 1:
                this.configuration.setNoteRepeatPeriod (Resolution.RES_1_16);
                break;

            case 2:
                if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
                    this.configuration.setNoteRepeatLength (Resolution.RES_1_16);
                break;

            case 3:
                if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                    this.configuration.setNoteRepeatMode (ArpeggiatorMode.UP);
                break;

            case 4:
                if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                    this.configuration.setNoteRepeatOctave (0);
                break;

            case 5:
                if (this.host.supports (Capability.NOTE_REPEAT_LATCH))
                    this.noteRepeat.setLatchActive (false);
                break;

            default:
                // Not used
                break;
        }
    }
}
