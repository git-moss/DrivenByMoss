// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of accent parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RibbonMode extends BaseMode
{
    private static final int []    MIDI_CCS        =
    {
        1,
        11,
        7,
        64
    };
    private static final String [] CC_QUICK_SELECT =
    {
        "Modulation",
        "Expression",
        "Volume",
        "Sustain",
        "",
        "",
        ""
    };
    private static final String [] FUNCTION        =
    {
        "Pitchbend",
        "CC",
        "CC/Pitch",
        "Pitch/CC",
        "Fader",
        "",
        ""
    };
    private static final int []    FUNCTION_IDS    =
    {
        PushConfiguration.RIBBON_MODE_PITCH,
        PushConfiguration.RIBBON_MODE_CC,
        PushConfiguration.RIBBON_MODE_CC_PB,
        PushConfiguration.RIBBON_MODE_PB_CC,
        PushConfiguration.RIBBON_MODE_FADER
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public RibbonMode (final PushControlSurface surface, final IModel model)
    {
        super ("Ribbon", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (index == 7)
        {
            final PushConfiguration config = this.surface.getConfiguration ();
            config.setRibbonModeCC (this.model.getValueChanger ().changeValue (value, config.getRibbonModeCCVal (), 1, 128));
        }
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
            this.surface.getConfiguration ().setRibbonMode (index);
        else
            this.surface.getModeManager ().restoreMode ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final int ribbonMode = this.surface.getConfiguration ().getRibbonMode ();
            if (index < 5)
                return ribbonMode == PushConfiguration.RIBBON_MODE_PITCH + index ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON;
            return AbstractMode.BUTTON_COLOR_OFF;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index < 4)
                return this.isPush2 ? AbstractMode.BUTTON_COLOR_ON : AbstractMode.BUTTON_COLOR2_ON;
            return AbstractMode.BUTTON_COLOR_OFF;
        }

        return AbstractMode.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index < 4)
            this.surface.getConfiguration ().setRibbonModeCC (RibbonMode.MIDI_CCS[index]);
        else
            this.surface.getModeManager ().restoreMode ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        final String ribbonModeCC = Integer.toString (config.getRibbonModeCCVal ());
        final int ribbonMode = config.getRibbonMode ();
        display.setCell (0, 0, "Modulatn").setCell (0, 1, "Expressn").setCell (0, 2, "Volume").setCell (0, 3, "Sustain").setCell (0, 7, "Midi CC");
        display.setCell (1, 7, ribbonModeCC).setCell (3, 0, (ribbonMode == PushConfiguration.RIBBON_MODE_PITCH ? Push1Display.SELECT_ARROW : "") + "Pitchbd").setCell (3, 1, (ribbonMode == PushConfiguration.RIBBON_MODE_CC ? Push1Display.SELECT_ARROW : "") + "CC").setCell (3, 2, (ribbonMode == PushConfiguration.RIBBON_MODE_CC_PB ? Push1Display.SELECT_ARROW : "") + "CC/Pitch").setCell (3, 3, (ribbonMode == PushConfiguration.RIBBON_MODE_PB_CC ? Push1Display.SELECT_ARROW : "") + "Pitch/CC").setCell (3, 4, (ribbonMode == PushConfiguration.RIBBON_MODE_FADER ? Push1Display.SELECT_ARROW : "") + "Fader");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        final String ribbonModeCC = Integer.toString (config.getRibbonModeCCVal ());
        final int ribbonMode = config.getRibbonMode ();

        for (int i = 0; i < 7; i++)
            display.addOptionElement (i == 0 ? "CC Quick Select" : "", RibbonMode.CC_QUICK_SELECT[i], false, i == 0 ? "Function" : "", RibbonMode.FUNCTION[i], i < RibbonMode.FUNCTION_IDS.length && ribbonMode == RibbonMode.FUNCTION_IDS[i], false);
        display.addParameterElement ("Midi CC", -1, ribbonModeCC, this.isKnobTouched[5], -1);
    }
}
