// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of accent parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RibbonMode extends BaseMode<IItem>
{
    private static final int []     MIDI_CCS          =
    {
        1,
        11,
        7,
        64
    };

    private static final String []  TOP_HEADERS       =
    {
        "CC",
        "",
        "Quick Select",
        "",
        "",
        "",
        "",
        ""
    };

    private static final String []  CC_SELECT         =
    {
        "",
        "",
        "Modulation",
        "Expression",
        "Volume",
        "Sustain",
        "",
        ""
    };

    private static final String []  BOTTOM_HEADERS    =
    {
        "Function",
        "",
        "",
        "",
        "",
        "Note Repeat",
        "",
        ""
    };

    private static final String []  FUNCTION_NAMES    =
    {
        "Pitchbend",
        "CC",
        "CC/Pitch",
        "Pitch/CC",
        "Fader",
        "Off",
        "Period",
        "Length"
    };

    private static final int []     FUNCTION_IDS      =
    {
        PushConfiguration.RIBBON_MODE_PITCH,
        PushConfiguration.RIBBON_MODE_CC,
        PushConfiguration.RIBBON_MODE_CC_PB,
        PushConfiguration.RIBBON_MODE_PB_CC,
        PushConfiguration.RIBBON_MODE_FADER
    };

    private static final String []  NOTE_REPEAT_NAMES =
    {
        "Off",
        "Period",
        "Length"
    };

    private final PushConfiguration configuration;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public RibbonMode (final PushControlSurface surface, final IModel model)
    {
        super ("Ribbon", surface, model);

        this.configuration = this.surface.getConfiguration ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (index == 0)
            this.configuration.setRibbonModeCC (this.model.getValueChanger ().changeValue (value, this.configuration.getRibbonModeCCVal (), -100, 128));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index < 5)
            this.configuration.setRibbonMode (index);
        else
            this.configuration.setRibbonNoteRepeat (index - 5);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index > 1 && index < 6)
            this.surface.getConfiguration ().setRibbonModeCC (RibbonMode.MIDI_CCS[index - 2]);
        else
            this.surface.getModeManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            if (index < 5)
                return this.configuration.getRibbonMode () == PushConfiguration.RIBBON_MODE_PITCH + index ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;

            return this.configuration.getRibbonNoteRepeat () == index - 5 ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index > 1 && index < 6)
                return this.isPush2 ? AbstractFeatureGroup.BUTTON_COLOR_ON : AbstractMode.BUTTON_COLOR2_ON;
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;
        }

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final String ribbonModeCC = Integer.toString (this.configuration.getRibbonModeCCVal ());
        final int ribbonMode = this.configuration.getRibbonMode ();
        final int noteRepeat = this.configuration.getRibbonNoteRepeat ();

        display.setCell (0, 0, ribbonModeCC).setCell (0, 2, "Modulatn").setCell (0, 3, "Expressn").setCell (0, 4, "Volume").setCell (0, 5, "Sustain");
        display.setCell (1, 0, "CC").setBlock (1, 1, "Quick Select");

        display.setCell (2, 0, "Function").setCell (2, 5, "Note Rep").setCell (2, 6, "eat");

        display.setCell (3, 0, (ribbonMode == PushConfiguration.RIBBON_MODE_PITCH ? Push1Display.SELECT_ARROW : "") + "Pitchbd");
        display.setCell (3, 1, (ribbonMode == PushConfiguration.RIBBON_MODE_CC ? Push1Display.SELECT_ARROW : "") + "CC");
        display.setCell (3, 2, (ribbonMode == PushConfiguration.RIBBON_MODE_CC_PB ? Push1Display.SELECT_ARROW : "") + "CC/Pitch");
        display.setCell (3, 3, (ribbonMode == PushConfiguration.RIBBON_MODE_PB_CC ? Push1Display.SELECT_ARROW : "") + "Pitch/CC");
        display.setCell (3, 4, (ribbonMode == PushConfiguration.RIBBON_MODE_FADER ? Push1Display.SELECT_ARROW : "") + "Fader");

        for (int i = 0; i < 3; i++)
            display.setCell (3, 5 + i, (noteRepeat == i ? Push1Display.SELECT_ARROW : "") + NOTE_REPEAT_NAMES[i]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final String ribbonModeCC = Integer.toString (this.configuration.getRibbonModeCCVal ());
        final int ribbonMode = this.configuration.getRibbonMode ();
        final int ribbonNoteRepeat = this.configuration.getRibbonNoteRepeat ();

        for (int i = 0; i < 5; i++)
            display.addOptionElement (TOP_HEADERS[i], i == 0 ? ribbonModeCC : CC_SELECT[i], i == 0, BOTTOM_HEADERS[i], FUNCTION_NAMES[i], i < RibbonMode.FUNCTION_IDS.length && ribbonMode == RibbonMode.FUNCTION_IDS[i], false);

        for (int i = 0; i < 3; i++)
            display.addOptionElement ("", CC_SELECT[5 + i], false, BOTTOM_HEADERS[5 + i], NOTE_REPEAT_NAMES[i], ribbonNoteRepeat == i, false);
    }
}
