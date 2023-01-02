// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
    private static final int []     MIDI_CCS                   =
    {
        1,
        11,
        7,
        64
    };

    private static final String []  TOP_HEADERS                =
    {
        "CC",
        "Quick Select",
        "",
        "",
        "",
        "Note Repeat",
        "",
        ""
    };

    private static final String []  BOTTOM_HEADERS             =
    {
        "Function",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    };

    private static final String []  TOP_OPTIONS                =
    {
        "",
        "Modulation",
        "Expression",
        "Volume",
        "Sustain",
        "Off",
        "Period",
        "Length"
    };

    private static final String []  BOTTOM_OPTIONS             =
    {
        "Pitchbend",
        "CC",
        "CC/Pitch",
        "Pitch/CC",
        "Fader",
        "Last Touched",
        "",
        ""
    };

    private static final int []     FUNCTION_IDS               =
    {
        PushConfiguration.RIBBON_MODE_PITCH,
        PushConfiguration.RIBBON_MODE_CC,
        PushConfiguration.RIBBON_MODE_CC_PB,
        PushConfiguration.RIBBON_MODE_PB_CC,
        PushConfiguration.RIBBON_MODE_FADER,
        PushConfiguration.RIBBON_MODE_LAST_TOUCHED
    };

    private static final String []  NOTE_REPEAT_NAMES          =
    {
        "    Off",
        " Period",
        " Length"
    };

    private static final String []  NOTE_REPEAT_NAMES_SELECTED =
    {
        "    " + Push1Display.SELECT_ARROW + "Off",
        Push1Display.SELECT_ARROW + "Period",
        Push1Display.SELECT_ARROW + "Length"
    };

    private static final String []  FUNCTION_NAMES             =
    {
        "Pitchbnd",
        "   CC   ",
        "CC/Pitch",
        "Pitch/CC",
        " Fader  ",
        " Touched"
    };

    private static final String []  FUNCTION_NAMES_SELECTED    =
    {
        Push1Display.SELECT_ARROW + "Pitchbd",
        "  " + Push1Display.SELECT_ARROW + "CC   ",
        Push1Display.SELECT_ARROW + "CC/Ptch",
        Push1Display.SELECT_ARROW + "Ptch/CC",
        Push1Display.SELECT_ARROW + "Fader  ",
        Push1Display.SELECT_ARROW + "Touched"
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
        this.setTouchedKnob (index, isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index < RibbonMode.FUNCTION_IDS.length)
            this.configuration.setRibbonMode (index);
        else
            this.surface.getModeManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || index == 0)
            return;
        if (index < 5)
            this.surface.getConfiguration ().setRibbonModeCC (RibbonMode.MIDI_CCS[index - 1]);
        else
            this.configuration.setRibbonNoteRepeat (index - 5);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            if (index < RibbonMode.FUNCTION_IDS.length)
                return this.configuration.getRibbonMode () == PushConfiguration.RIBBON_MODE_PITCH + index ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0)
                return AbstractFeatureGroup.BUTTON_COLOR_OFF;
            if (index < 5)
                return this.isPush2 ? AbstractFeatureGroup.BUTTON_COLOR_ON : AbstractMode.BUTTON_COLOR2_ON;
            return this.configuration.getRibbonNoteRepeat () == index - 5 ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
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

        display.setCell (0, 0, "CC: " + ribbonModeCC).setCell (0, 1, "Modulatn").setCell (0, 2, "Expressn").setCell (0, 3, "Volume").setCell (0, 4, "Sustain");
        display.setCell (1, 1, "CC Quick").setCell (1, 2, "Select").setCell (1, 5, "    Note").setCell (1, 6, "Repeat");

        display.setCell (2, 0, "Function");
        display.setCell (3, 0, ribbonMode == PushConfiguration.RIBBON_MODE_PITCH ? FUNCTION_NAMES_SELECTED[0] : FUNCTION_NAMES[0]);
        display.setCell (3, 1, ribbonMode == PushConfiguration.RIBBON_MODE_CC ? FUNCTION_NAMES_SELECTED[1] : FUNCTION_NAMES[1]);
        display.setCell (3, 2, ribbonMode == PushConfiguration.RIBBON_MODE_CC_PB ? FUNCTION_NAMES_SELECTED[2] : FUNCTION_NAMES[2]);
        display.setCell (3, 3, ribbonMode == PushConfiguration.RIBBON_MODE_PB_CC ? FUNCTION_NAMES_SELECTED[3] : FUNCTION_NAMES[3]);
        display.setCell (3, 4, ribbonMode == PushConfiguration.RIBBON_MODE_FADER ? FUNCTION_NAMES_SELECTED[4] : FUNCTION_NAMES[4]);
        display.setCell (3, 5, ribbonMode == PushConfiguration.RIBBON_MODE_LAST_TOUCHED ? FUNCTION_NAMES_SELECTED[5] : FUNCTION_NAMES[5]);

        for (int i = 0; i < 3; i++)
            display.setCell (0, 5 + i, noteRepeat == i ? NOTE_REPEAT_NAMES_SELECTED[i] : NOTE_REPEAT_NAMES[i]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final int ribbonMode = this.configuration.getRibbonMode ();
        final int ribbonNoteRepeat = this.configuration.getRibbonNoteRepeat ();

        for (int i = 0; i < 8; i++)
        {
            boolean isMenuTopSelected = true;
            String menuTopName = TOP_OPTIONS[i];
            if (i == 0)
                menuTopName = Integer.toString (this.configuration.getRibbonModeCCVal ());
            else
                isMenuTopSelected = i > 4 && ribbonNoteRepeat == i - 5;
            display.addOptionElement (TOP_HEADERS[i], menuTopName, isMenuTopSelected, BOTTOM_HEADERS[i], BOTTOM_OPTIONS[i], i < RibbonMode.FUNCTION_IDS.length && ribbonMode == RibbonMode.FUNCTION_IDS[i], false);
        }
    }
}
