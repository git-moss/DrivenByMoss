// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode;

import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for global parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OptionsMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OptionsMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Options", surface, model);

        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        switch (index)
        {
            case 0:
                this.model.getMasterTrack ().changeVolume (value);
                break;
            case 1:
                this.model.getMasterTrack ().changePan (value);
                break;
            case 4:
                this.model.getTransport ().changeTempo (this.model.getValueChanger ().calcKnobSpeed (value) > 0);
                break;
            case 5:
                this.model.getTransport ().changeMetronomeVolume (value);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        switch (index)
        {
            case 0:
                this.model.getApplication ().undo ();
                break;
            case 1:
                this.model.getApplication ().redo ();
                break;
            case 2:
                this.model.getProject ().previous ();
                break;
            case 3:
                this.model.getProject ().next ();
                break;
            case 4:
                this.model.getTransport ().tapTempo ();
                break;
            case 5:
                this.model.getTransport ().toggleMetronome ();
                break;
            case 6:
                this.model.getApplication ().toggleEngineActive ();
                break;
            case 7:
                this.model.getClip ().quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1, SLMkIIIColors.SLMKIII_BROWN_DARK);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_2, SLMkIIIColors.SLMKIII_BROWN_DARK);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_3, SLMkIIIColors.SLMKIII_BROWN_DARK);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_4, SLMkIIIColors.SLMKIII_BROWN_DARK);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_5, SLMkIIIColors.SLMKIII_BROWN_DARK);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_6, this.model.getTransport ().isMetronomeOn () ? SLMkIIIColors.SLMKIII_BROWN : SLMkIIIColors.SLMKIII_BROWN_DARK);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_7, this.model.getApplication ().isEngineActive () ? SLMkIIIColors.SLMKIII_BROWN : SLMkIIIColors.SLMKIII_BROWN_DARK);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_8, SLMkIIIColors.SLMKIII_BROWN_DARK);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITransport transport = this.model.getTransport ();
        final IValueChanger valueChanger = this.model.getValueChanger ();

        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();

        d.setCell (3, 0, "Undo");
        d.setPropertyColor (0, 2, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyValue (0, 1, 0);

        d.setCell (3, 1, "Redo");
        d.setPropertyColor (1, 2, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyValue (1, 1, 0);

        d.setCell (3, 2, "<< Project");
        d.setPropertyColor (2, 2, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyValue (2, 1, 0);

        d.setCell (3, 3, "Project >>");
        d.setPropertyColor (3, 2, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyValue (3, 1, 0);

        d.setCell (3, 4, "Tap");
        d.setPropertyColor (4, 2, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyValue (4, 1, 0);

        d.setCell (3, 5, transport.isMetronomeOn () ? "On" : "Off");
        d.setPropertyColor (5, 2, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyValue (5, 1, transport.isMetronomeOn () ? 1 : 0);

        d.setCell (3, 6, "Engine");
        d.setPropertyColor (6, 2, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyValue (6, 1, this.model.getApplication ().isEngineActive () ? 1 : 0);

        d.setCell (3, 7, "Quantize");
        d.setPropertyColor (7, 2, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyValue (7, 1, 0);

        final IMasterTrack master = this.model.getMasterTrack ();
        d.setCell (0, 0, StringUtils.fixASCII ("Mstr Vol")).setCell (1, 0, master.getVolumeStr (9));
        this.surface.updateContinuous (SLMkIIIControlSurface.MKIII_KNOB_1, valueChanger.toMidiValue (master.getVolume ()));
        d.setPropertyColor (0, 0, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyColor (0, 1, SLMkIIIColors.SLMKIII_BROWN);

        d.setCell (0, 1, StringUtils.fixASCII ("Mstr Pan")).setCell (1, 1, master.getPanStr (9));
        this.surface.updateContinuous (SLMkIIIControlSurface.MKIII_KNOB_2, valueChanger.toMidiValue (master.getPan ()));
        d.setPropertyColor (1, 0, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyColor (1, 1, SLMkIIIColors.SLMKIII_BROWN);

        d.setCell (0, 4, StringUtils.fixASCII ("Tempo")).setCell (1, 4, transport.formatTempo (transport.getTempo ()));
        this.surface.updateContinuous (SLMkIIIControlSurface.MKIII_KNOB_5, valueChanger.toMidiValue ((int) transport.rescaleTempo (transport.getTempo (), valueChanger.getUpperBound ())));
        d.setPropertyColor (4, 0, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyColor (4, 1, SLMkIIIColors.SLMKIII_BROWN);

        d.setCell (0, 5, StringUtils.fixASCII ("Metronome")).setCell (1, 5, transport.getMetronomeVolumeStr ());
        this.surface.updateContinuous (SLMkIIIControlSurface.MKIII_KNOB_6, valueChanger.toMidiValue (transport.getMetronomeVolume ()));
        d.setPropertyColor (5, 0, SLMkIIIColors.SLMKIII_BROWN);
        d.setPropertyColor (5, 1, SLMkIIIColors.SLMKIII_BROWN);

        d.setCell (0, 8, "Master");
        d.setPropertyColor (8, 0, SLMkIIIColors.SLMKIII_BROWN);

        this.setButtonInfo (d);

        d.allDone ();
    }
}