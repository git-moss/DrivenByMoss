// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.controller.ni.maschine.mk3.view.PlayView;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.ScaleLayout;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.Views;


/**
 * The Play Configuration mode.
 *
 * @author Jürgen Moßgraber
 */
public class PlayConfigurationMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public PlayConfigurationMode (final MaschineControlSurface surface, final IModel model)
    {
        super ("Play Configuration", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int idx = index < 0 ? this.selectedParam : index;

        final Scales scales = this.model.getScales ();
        final boolean inc = this.model.getValueChanger ().isIncrease (value);

        switch (idx)
        {
            case 0:
            case 1:
            case 2:
                if (inc)
                    scales.nextScale ();
                else
                    scales.prevScale ();
                if (!this.surface.getMaschine ().hasMCUDisplay ())
                    this.surface.getDisplay ().notify (scales.getScale ().getName ());
                break;

            case 3:
                if (inc)
                    scales.nextScaleOffset ();
                else
                    scales.prevScaleOffset ();
                break;

            case 4:
            case 5:
                if (inc)
                    scales.nextScaleLayout ();
                else
                    scales.prevScaleLayout ();
                break;

            case 6:
                scales.setChromatic (!inc);
                this.surface.getConfiguration ().setScaleInKey (!scales.isChromatic ());
                break;

            case 7:
                final PlayView playView = (PlayView) this.surface.getViewManager ().get (Views.PLAY);
                if (inc)
                    playView.onOctaveUp (ButtonEvent.DOWN);
                else
                    playView.onOctaveDown (ButtonEvent.DOWN);
                ((INoteMode) this.surface.getModeManager ().get (Modes.NOTE)).clearNotes ();
                break;

            default:
                // Not used
                break;
        }

        this.update ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);

            final Scales scales = this.model.getScales ();
            final int idx = index < 0 ? this.selectedParam : index;
            switch (idx)
            {
                case 0:
                case 1:
                case 2:
                    scales.setScale (Scale.MAJOR);
                    if (!this.surface.getMaschine ().hasMCUDisplay ())
                        this.surface.getDisplay ().notify (scales.getScale ().getName ());
                    break;

                case 3:
                    scales.setScaleOffsetByIndex (0);
                    break;

                case 4:
                case 5:
                    scales.setScaleLayout (ScaleLayout.SEQUENT_UP);
                    break;

                case 6:
                    scales.setChromatic (false);
                    this.surface.getConfiguration ().setScaleInKey (!scales.isChromatic ());
                    break;

                case 7:
                    ((PlayView) this.surface.getViewManager ().get (Views.PLAY)).resetOctave ();
                    break;

                default:
                    // Not used
                    break;
            }
            this.update ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final Scales scales = this.model.getScales ();

        d.setBlock (0, 0, this.mark ("Scale", 0)).setBlock (1, 0, scales.getScale ().getName ());
        d.setCell (0, 3, this.mark ("Base", 3)).setCell (1, 3, Scales.BASES.get (scales.getScaleOffsetIndex ()));
        d.setBlock (0, 2, this.mark ("Layout", 4)).setBlock (1, 2, StringUtils.optimizeName (scales.getScaleLayout ().getName (), 12));
        d.setCell (0, 6, this.mark ("In-Key", 6)).setCell (1, 6, scales.isChromatic () ? "Off" : "On");
        final int octave = scales.getOctave ();
        d.setCell (0, 7, this.mark ("Octave", 7)).setCell (1, 7, (octave > 0 ? "+" : "") + Integer.toString (octave));

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        super.selectPreviousItem ();

        if (this.selectedParam == 1 || this.selectedParam == 2)
            this.selectedParam = 0;
        else if (this.selectedParam == 5)
            this.selectedParam = 4;
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        super.selectNextItem ();

        if (this.selectedParam == 1 || this.selectedParam == 2)
            this.selectedParam = 3;
        else if (this.selectedParam == 5)
            this.selectedParam = 6;
    }


    private void update ()
    {
        this.surface.getViewManager ().get (Views.PLAY).updateNoteMapping ();
        final MaschineConfiguration config = this.surface.getConfiguration ();
        final Scales scales = this.model.getScales ();
        config.setScale (scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES.get (scales.getScaleOffsetIndex ()));
        config.setScaleLayout (scales.getScaleLayout ().getName ());
    }
}