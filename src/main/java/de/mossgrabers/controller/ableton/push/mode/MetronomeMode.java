// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of transport parameters.
 *
 * @author Jürgen Moßgraber
 */
public class MetronomeMode extends BaseMode<IItem>
{
    private static final int []    PREROLL_MEASURE =
    {
        0,
        1,
        2,
        4
    };

    private static final String [] PREROLL_NAMES   =
    {
        "None",
        "1 Bar",
        "2 Bars",
        "4 Bars"
    };

    private final ITransport       transport;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MetronomeMode (final PushControlSurface surface, final IModel model)
    {
        super ("Transport", surface, model);

        this.transport = this.model.getTransport ();

        this.setParameterProvider (new CombinedParameterProvider (new EmptyParameterProvider (7), new FixedParameterProvider (this.model.getTransport ().getMetronomeVolumeParameter ())));
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (index < PREROLL_MEASURE.length)
            this.transport.setPrerollMeasures (PREROLL_MEASURE[index]);
        else if (index == 5)
            this.transport.togglePrerollMetronome ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final int prerollMeasures = this.transport.getPrerollMeasures ();
            if (index < PREROLL_MEASURE.length)
                return PREROLL_MEASURE[index] == prerollMeasures ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
            if (index == 5)
                return this.transport.isPrerollMetronomeEnabled () ? AbstractMode.BUTTON_COLOR2_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
        }

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        display.setBlock (2, 0, "Pre-roll");

        final int prerollMeasures = this.transport.getPrerollMeasures ();
        for (int i = 0; i < PREROLL_MEASURE.length; i++)
            display.setCell (3, i, (PREROLL_MEASURE[i] == prerollMeasures ? Push1Display.SELECT_ARROW : "") + PREROLL_NAMES[i]);

        display.setCell (0, 7, "Volume");
        display.setCell (1, 7, this.transport.getMetronomeVolumeStr ());
        display.setCell (2, 7, this.transport.getMetronomeVolume (), Format.FORMAT_VALUE);

        display.setBlock (1, 2, "Play Metronome").setBlock (2, 2, "during Pre-roll?");
        display.setCell (3, 5, this.transport.isPrerollMetronomeEnabled () ? "  Yes" : "  No");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final int prerollMeasures = this.transport.getPrerollMeasures ();
        for (int i = 0; i < PREROLL_MEASURE.length; i++)
            display.addOptionElement ("", "", false, i == 0 ? "Pre-roll" : "", PREROLL_NAMES[i], PREROLL_MEASURE[i] == prerollMeasures, false);

        display.addEmptyElement ();
        display.addOptionElement ("Play Metronome", "", false, "during Pre-Roll?", this.transport.isPrerollMetronomeEnabled () ? "Yes" : "No", this.transport.isPrerollMetronomeEnabled (), false);
        display.addEmptyElement ();
        final int metronomeVolume = this.transport.getMetronomeVolume ();
        display.addParameterElement ("", false, "", ChannelType.AUDIO, null, false, "Volume", metronomeVolume, this.transport.getMetronomeVolumeStr (), false, metronomeVolume);
    }
}