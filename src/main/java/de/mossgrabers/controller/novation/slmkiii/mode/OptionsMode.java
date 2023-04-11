// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode;

import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for global parameters.
 *
 * @author Jürgen Moßgraber
 */
public class OptionsMode extends BaseMode<IItem>
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
                this.model.getTransport ().changeTempo (this.model.getValueChanger ().isIncrease (value), this.surface.isKnobSensitivitySlow ());
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
    public int getKnobValue (final int index)
    {
        final ITransport transport = this.model.getTransport ();
        final IMasterTrack master = this.model.getMasterTrack ();
        switch (index)
        {
            case 0:
                return master.getVolume ();

            case 1:
                return master.getPan ();

            case 4:
                return (int) transport.scaleTempo (transport.getTempo (), this.model.getValueChanger ().getUpperBound ());

            case 5:
                return transport.getMetronomeVolume ();

            default:
                return 0;
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
                final IClip clip = this.model.getCursorClip ();
                if (clip.doesExist ())
                    clip.quantize (this.surface.getConfiguration ().getQuantizeAmount () / 100.0);
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
        switch (buttonID)
        {
            case ROW1_6:
                return this.model.getTransport ().isMetronomeOn () ? SLMkIIIColorManager.SLMKIII_BROWN : SLMkIIIColorManager.SLMKIII_DARK_BROWN;

            case ROW1_7:
                return this.model.getApplication ().isEngineActive () ? SLMkIIIColorManager.SLMKIII_BROWN : SLMkIIIColorManager.SLMKIII_DARK_BROWN;

            default:
                return SLMkIIIColorManager.SLMKIII_DARK_BROWN;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITransport transport = this.model.getTransport ();

        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();

        d.setCell (3, 0, "Undo");
        d.setPropertyColor (0, 2, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyValue (0, 1, 0);

        d.setCell (3, 1, "Redo");
        d.setPropertyColor (1, 2, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyValue (1, 1, 0);

        d.setCell (3, 2, "<<Project");
        d.setPropertyColor (2, 2, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyValue (2, 1, 0);

        d.setCell (3, 3, "Project>>");
        d.setPropertyColor (3, 2, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyValue (3, 1, 0);

        d.setCell (3, 4, "Tap");
        d.setPropertyColor (4, 2, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyValue (4, 1, 0);

        d.setCell (3, 5, transport.isMetronomeOn () ? "On" : "Off");
        d.setPropertyColor (5, 2, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyValue (5, 1, transport.isMetronomeOn () ? 1 : 0);

        d.setCell (3, 6, "Engine");
        d.setPropertyColor (6, 2, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyValue (6, 1, this.model.getApplication ().isEngineActive () ? 1 : 0);

        d.setCell (3, 7, "Quantize");
        d.setPropertyColor (7, 2, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyValue (7, 1, 0);

        final IMasterTrack master = this.model.getMasterTrack ();
        d.setCell (0, 0, StringUtils.fixASCII ("Mstr Vol")).setCell (1, 0, master.getVolumeStr (9));
        d.setPropertyColor (0, 0, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyColor (0, 1, SLMkIIIColorManager.SLMKIII_BROWN);

        d.setCell (0, 1, StringUtils.fixASCII ("Mstr Pan")).setCell (1, 1, master.getPanStr (9));
        d.setPropertyColor (1, 0, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyColor (1, 1, SLMkIIIColorManager.SLMKIII_BROWN);

        d.setCell (0, 4, StringUtils.fixASCII ("Tempo")).setCell (1, 4, transport.formatTempo (transport.getTempo ()));
        d.setPropertyColor (4, 0, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyColor (4, 1, SLMkIIIColorManager.SLMKIII_BROWN);

        d.setCell (0, 5, StringUtils.fixASCII ("Metronome")).setCell (1, 5, transport.getMetronomeVolumeStr ());
        d.setPropertyColor (5, 0, SLMkIIIColorManager.SLMKIII_BROWN);
        d.setPropertyColor (5, 1, SLMkIIIColorManager.SLMKIII_BROWN);

        d.setCell (0, 8, "Master");

        this.setButtonInfo (d);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return SLMkIIIColorManager.SLMKIII_BROWN;
    }
}