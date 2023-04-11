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
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode for editing the recording options.
 *
 * @author Jürgen Moßgraber
 */
public class QuantizeMode extends BaseMode<IItem>
{
    private static final String [] MENU =
    {
        "Quantize",
        "Groove",
        " ",
        " ",
        " ",
        " ",
        " ",
        " "
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public QuantizeMode (final PushControlSurface surface, final IModel model)
    {
        super ("Record", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (index == 7)
            this.surface.getConfiguration ().changeQuantizeAmount (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            if (index == 7)
                this.surface.getConfiguration ().resetQuantizeAmount ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        display.setCell (0, 0, Push1Display.SELECT_ARROW + "Quantize");
        display.setCell (0, 1, "Groove");

        final ITrack cursorTrack = this.model.getCursorTrack ();
        final RecordQuantization recQuant = cursorTrack.doesExist () ? cursorTrack.getRecordQuantizationGrid () : RecordQuantization.RES_OFF;
        final RecordQuantization [] values = RecordQuantization.values ();
        display.setBlock (2, 0, "Record Quantize:");
        for (int i = 0; i < values.length; i++)
            display.setCell (3, i, (values[i] == recQuant ? Push1Display.SELECT_ARROW : "") + values[i].getName ());

        if (this.model.getHost ().supports (Capability.QUANTIZE_INPUT_NOTE_LENGTH))
        {
            display.setBlock (2, 2, "       Quant Note");
            display.setCell (2, 6, "Length:");
            display.setCell (3, 6, cursorTrack.doesExist () && cursorTrack.isRecordQuantizationNoteLength () ? "On" : "Off");
        }

        final int quantizeAmount = this.surface.getConfiguration ().getQuantizeAmount ();
        display.setCell (0, 7, "Quant Amnt").setCell (1, 7, quantizeAmount + "%").setCell (2, 7, quantizeAmount * 1023 / 100, Format.FORMAT_VALUE);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        final RecordQuantization recQuant = cursorTrack.doesExist () ? cursorTrack.getRecordQuantizationGrid () : RecordQuantization.RES_OFF;
        final RecordQuantization [] values = RecordQuantization.values ();
        for (int i = 0; i < values.length; i++)
            display.addOptionElement ("", MENU[i], i == 0, i == 0 ? "Record Quantization" : "", values[i].getName (), values[i] == recQuant, true);

        if (this.model.getHost ().supports (Capability.QUANTIZE_INPUT_NOTE_LENGTH))
        {
            display.addOptionElement ("", " ", false, null, "Quantize Note Length", "", false, null, true);
            final boolean isQuantLength = cursorTrack.doesExist () && cursorTrack.isRecordQuantizationNoteLength ();
            display.addOptionElement ("", " ", false, "", isQuantLength ? "On" : "Off", isQuantLength, true);
        }
        else
        {
            display.addEmptyElement (true);
            display.addEmptyElement (true);
        }

        if (this.model.getHost ().supports (Capability.QUANTIZE_AMOUNT))
        {
            final int quantizeAmount = this.surface.getConfiguration ().getQuantizeAmount ();
            display.addParameterElement (" ", false, "", (ChannelType) null, null, false, "Qunt Amnt", quantizeAmount * 1023 / 100, quantizeAmount + "%", this.isKnobTouched (0), -1);
        }
        else
            display.addEmptyElement (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
            return;

        switch (index)
        {
            case 0, 1, 2, 3, 4:
                cursorTrack.setRecordQuantizationGrid (RecordQuantization.values ()[index]);
                break;

            case 6:
                cursorTrack.toggleRecordQuantizationNoteLength ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (index == 1)
            this.surface.getModeManager ().setTemporary (Modes.GROOVE);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final RecordQuantization [] values = RecordQuantization.values ();
            final ITrack cursorTrack = this.model.getCursorTrack ();
            final RecordQuantization recQuant = cursorTrack.doesExist () ? cursorTrack.getRecordQuantizationGrid () : RecordQuantization.RES_OFF;
            if (index < values.length)
                return values[index] == recQuant ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
            if (index == 6)
                return cursorTrack.isRecordQuantizationNoteLength () ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0)
                return AbstractMode.BUTTON_COLOR_HI;
            if (index == 1)
                return AbstractFeatureGroup.BUTTON_COLOR_ON;
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;
        }

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }
}
