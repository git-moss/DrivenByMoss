// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.List;


/**
 * Editing the length of note repeat notes.
 *
 * @author Jürgen Moßgraber
 */
public class NoteRepeatMode extends BaseMode<IItem>
{
    private final IHost       host;
    private final INoteRepeat noteRepeat;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteRepeatMode (final PushControlSurface surface, final IModel model)
    {
        super ("Note Repeat", surface, model);

        this.host = this.model.getHost ();

        final INoteInput defaultNoteInput = surface.getMidiInput ().getDefaultNoteInput ();
        this.noteRepeat = defaultNoteInput == null ? null : defaultNoteInput.getNoteRepeat ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.model.getGroove ().enableObservers (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.model.getGroove ().enableObservers (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (index != 7 && !this.increaseKnobMovement ())
            return;

        final PushConfiguration configuration = this.surface.getConfiguration ();
        final IValueChanger valueChanger = this.model.getValueChanger ();
        switch (index)
        {
            case 0, 1:
                final int sel = Resolution.change (Resolution.getMatch (configuration.getNoteRepeatPeriod ().getValue ()), valueChanger.isIncrease (value));
                configuration.setNoteRepeatPeriod (Resolution.values ()[sel]);
                break;

            case 2, 3:
                if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
                {
                    final int sel2 = Resolution.change (Resolution.getMatch (configuration.getNoteRepeatLength ().getValue ()), valueChanger.calcKnobChange (value) > 0);
                    configuration.setNoteRepeatLength (Resolution.values ()[sel2]);
                }
                break;

            case 5:
                if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                    configuration.setPrevNextNoteRepeatMode (valueChanger.isIncrease (value));
                break;

            case 6:
                if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                    configuration.setNoteRepeatOctave (configuration.getNoteRepeatOctave () + (valueChanger.calcKnobChange (value) > 0 ? 1 : -1));
                break;

            case 7:
                if (this.host.supports (Capability.NOTE_REPEAT_SWING))
                    this.model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_AMOUNT).changeValue (value);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);

            switch (index)
            {
                case 5:
                    if (this.host.supports (Capability.NOTE_REPEAT_MODE))
                        this.noteRepeat.setMode (ArpeggiatorMode.UP);
                    break;

                case 6:
                    if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
                        this.noteRepeat.setOctaves (1);
                    break;

                case 7:
                    if (this.host.supports (Capability.NOTE_REPEAT_SWING))
                        this.model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_AMOUNT).resetValue ();
                    break;

                default:
                    // Unused
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || this.noteRepeat == null)
            return;

        final PushConfiguration configuration = this.surface.getConfiguration ();

        switch (index)
        {
            case 0, 1:
                final int sel = Resolution.change (Resolution.getMatch (this.noteRepeat.getPeriod ()), index == 1);
                configuration.setNoteRepeatPeriod (Resolution.values ()[sel]);
                break;

            case 2, 3:
                if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
                {
                    final int sel2 = Resolution.change (Resolution.getMatch (this.noteRepeat.getNoteLength ()), index == 3);
                    configuration.setNoteRepeatLength (Resolution.values ()[sel2]);
                }
                break;

            case 4:
                if (this.host.supports (Capability.NOTE_REPEAT_LATCH))
                    this.noteRepeat.toggleLatchActive ();
                break;

            case 5:
                if (this.host.supports (Capability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY))
                    this.noteRepeat.toggleUsePressure ();
                break;

            case 6:
                if (this.host.supports (Capability.NOTE_REPEAT_IS_FREE_RUNNING))
                    this.noteRepeat.toggleIsFreeRunning ();
                break;

            case 7:
                if (this.host.supports (Capability.NOTE_REPEAT_SWING))
                    this.noteRepeat.toggleShuffle ();
                break;

            default:
                // Unused
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || this.noteRepeat == null)
            return;

        if (index == 7 && this.host.supports (Capability.NOTE_REPEAT_SWING))
        {
            final IParameter grooveEnabled = this.model.getGroove ().getParameter (GrooveParameterID.ENABLED);
            grooveEnabled.setValue (grooveEnabled.getValue () == 0 ? this.model.getValueChanger ().getUpperBound () : 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final ColorManager colorManager = this.model.getColorManager ();
            final int offColor = colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);
            final int onColor = colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON);
            final int hiColor = colorManager.getColorIndex (AbstractMode.BUTTON_COLOR_HI);

            switch (index)
            {
                default:
                case 0, 1:
                    return onColor;
                case 2:
                    return this.host.supports (Capability.NOTE_REPEAT_LENGTH) ? onColor : offColor;
                case 3:
                    return this.host.supports (Capability.NOTE_REPEAT_LENGTH) ? onColor : offColor;

                case 4:
                    if (this.host.supports (Capability.NOTE_REPEAT_LATCH))
                        return this.noteRepeat.isLatchActive () ? hiColor : onColor;
                    return offColor;

                case 5:
                    if (this.host.supports (Capability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY))
                        return this.noteRepeat.usePressure () ? hiColor : onColor;
                    return offColor;

                case 6:
                    if (this.host.supports (Capability.NOTE_REPEAT_IS_FREE_RUNNING))
                        return !this.noteRepeat.isFreeRunning () ? hiColor : onColor;
                    return offColor;

                case 7:
                    if (this.host.supports (Capability.NOTE_REPEAT_SWING))
                        return this.noteRepeat.isShuffle () ? hiColor : onColor;
                    return offColor;
            }
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final ColorManager colorManager = this.model.getColorManager ();
            if (index < 7)
                return colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);
            return this.model.getGroove ().getParameter (GrooveParameterID.ENABLED).getValue () > 0 ? colorManager.getColorIndex (AbstractMode.BUTTON_COLOR_HI) : colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON);
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        display.setCell (0, 0, "Period:");
        final int selPeriodIndex = this.getSelectedPeriodIndex ();
        int pos = 0;
        for (final Pair<String, Boolean> p: Push1Display.createMenuList (4, Resolution.getNames (), selPeriodIndex))
        {
            display.setCell (pos, 1, (p.getValue ().booleanValue () ? Push1Display.SELECT_ARROW : " ") + p.getKey ());
            pos++;
        }

        if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
        {
            display.setCell (0, 2, "Length:");
            final int selLengthIndex = this.getSelectedNoteLengthIndex ();
            pos = 0;
            for (final Pair<String, Boolean> p: Push1Display.createMenuList (4, Resolution.getNames (), selLengthIndex))
            {
                display.setCell (pos, 3, (p.getValue ().booleanValue () ? Push1Display.SELECT_ARROW : " ") + p.getKey ());
                pos++;
            }
        }

        if (this.host.supports (Capability.NOTE_REPEAT_LATCH))
            display.setCell (3, 4, " Latch");

        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        if (this.host.supports (Capability.NOTE_REPEAT_MODE))
        {
            final String bottomMenu = this.host.supports (Capability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY) ? "Use Pressure" : "";
            final ArpeggiatorMode mode = this.noteRepeat.getMode ();
            final Configuration configuration = this.surface.getConfiguration ();
            final List<ArpeggiatorMode> arpeggiatorModes = configuration.getArpeggiatorModes ();
            final int modeIndex = configuration.lookupArpeggiatorModeIndex (mode);
            final int value = modeIndex * upperBound / (arpeggiatorModes.size () - 1);
            display.setCell (0, 5, "Mode");
            display.setCell (1, 5, StringUtils.optimizeName (mode.getName (), 8));
            display.setCell (2, 5, value, Format.FORMAT_VALUE);
            display.setCell (3, 5, bottomMenu);
        }

        if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
        {
            final String bottomMenu = this.host.supports (Capability.NOTE_REPEAT_IS_FREE_RUNNING) ? "  Sync" : "";
            final int octaves = this.noteRepeat.getOctaves ();
            final int value = octaves * upperBound / 8;
            display.setCell (0, 6, "Octaves");
            display.setCell (1, 6, Integer.toString (octaves));
            display.setCell (2, 6, value, Format.FORMAT_VALUE);
            display.setCell (3, 6, bottomMenu);
        }

        if (this.host.supports (Capability.NOTE_REPEAT_SWING))
        {
            final IParameter shuffleParam = this.model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_AMOUNT);
            display.setCell (0, 7, shuffleParam.getName (10));
            display.setCell (1, 7, shuffleParam.getDisplayedValue (8));
            display.setCell (2, 7, shuffleParam.getValue (), Format.FORMAT_VALUE);
            display.setCell (3, 7, "Shuffle");
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        if (this.noteRepeat == null)
            return;

        display.addOptionElement ("Period", "", false, "", "", false, false);
        final int selPeriodIndex = this.getSelectedPeriodIndex ();
        display.addListElement (6, Resolution.getNames (), selPeriodIndex);

        if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
        {
            display.addOptionElement ("  Length", "", false, "", "", false, false);
            final int selLengthIndex = this.getSelectedNoteLengthIndex ();
            display.addListElement (6, Resolution.getNames (), selLengthIndex);
        }
        else
        {
            display.addEmptyElement ();
            display.addEmptyElement ();
        }

        if (this.host.supports (Capability.NOTE_REPEAT_LATCH))
            display.addOptionElement ("", "", false, "", "Latch", this.noteRepeat.isLatchActive (), false);
        else
            display.addEmptyElement ();

        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        if (this.host.supports (Capability.NOTE_REPEAT_MODE))
        {
            final String bottomMenu = this.host.supports (Capability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY) ? "Use Pressure" : "";
            final boolean isBottomMenuEnabled = this.noteRepeat.usePressure ();
            final ArpeggiatorMode mode = this.noteRepeat.getMode ();
            final Configuration configuration = this.surface.getConfiguration ();
            final List<ArpeggiatorMode> arpeggiatorModes = configuration.getArpeggiatorModes ();
            final int modeIndex = configuration.lookupArpeggiatorModeIndex (mode);
            final int value = modeIndex * upperBound / (arpeggiatorModes.size () - 1);
            display.addParameterElementWithPlainMenu ("", false, bottomMenu, null, isBottomMenuEnabled, "Mode", value, StringUtils.optimizeName (mode.getName (), 8), this.isKnobTouched (5), -1);
        }
        else
            display.addEmptyElement ();

        if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
        {
            final String bottomMenu = this.host.supports (Capability.NOTE_REPEAT_IS_FREE_RUNNING) ? "Sync" : "";
            final boolean isBottomMenuEnabled = !this.noteRepeat.isFreeRunning ();
            final int octaves = this.noteRepeat.getOctaves ();
            final int value = octaves * upperBound / 8;
            display.addParameterElementWithPlainMenu ("", false, bottomMenu, null, isBottomMenuEnabled, "Octaves", value, Integer.toString (octaves), this.isKnobTouched (6), -1);
        }
        else
            display.addEmptyElement ();

        if (this.host.supports (Capability.NOTE_REPEAT_SWING))
        {
            final IGroove groove = this.model.getGroove ();
            final IParameter shuffleParam = groove.getParameter (GrooveParameterID.SHUFFLE_AMOUNT);
            final IParameter enabledParam = groove.getParameter (GrooveParameterID.ENABLED);
            final int value = enabledParam.getValue ();
            display.addParameterElementWithPlainMenu ("Groove " + enabledParam.getDisplayedValue (8), value != 0, "Shuffle", null, this.noteRepeat.isShuffle (), shuffleParam.getName (10), shuffleParam.getValue (), shuffleParam.getDisplayedValue (8), this.isKnobTouched (7), -1);
        }
        else
            display.addEmptyElement ();
    }


    /**
     * Get the index of the selected period.
     *
     * @return The selected period index
     */
    private int getSelectedPeriodIndex ()
    {
        return this.noteRepeat == null ? -1 : Resolution.getMatch (this.noteRepeat.getPeriod ());
    }


    /**
     * Get the index of the selected length.
     *
     * @return The selected length index
     */
    private int getSelectedNoteLengthIndex ()
    {
        return this.noteRepeat == null ? -1 : Resolution.getMatch (this.noteRepeat.getNoteLength ());
    }
}
