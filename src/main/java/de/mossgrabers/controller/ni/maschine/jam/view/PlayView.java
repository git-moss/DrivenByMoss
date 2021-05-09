// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.Views;


/**
 * The play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final MaschineJamControlSurface surface, final IModel model)
    {
        this (Views.NAME_PLAY, surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final String name, final MaschineJamControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (this.isButtonCombination (ButtonID.DELETE))
        {
            final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
            this.model.getNoteClip (8, 128).clearRow (editMidiChannel, this.keyManager.map (note));
            return;
        }
        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void changeOption (final EncoderMode temporaryEncoderMode, final int control)
    {
        this.keyManager.clearPressedKeys ();

        final boolean increase = this.model.getValueChanger ().isIncrease (control);

        switch (temporaryEncoderMode)
        {
            case TEMPORARY_PERFORM:
                if (increase)
                    this.scales.nextScale ();
                else
                    this.scales.prevScale ();
                this.mvHelper.delayDisplay ( () -> "Scale: " + this.scales.getScale ().getName ());
                break;

            case TEMPORARY_NOTES:
                if (increase)
                    this.scales.nextScaleOffset ();
                else
                    this.scales.prevScaleOffset ();
                this.mvHelper.delayDisplay ( () -> "Scale Offset: " + Scales.BASES.get (this.scales.getScaleOffset ()));
                break;

            case TEMPORARY_LOCK:
                this.scales.toggleChromatic ();
                this.mvHelper.delayDisplay ( () -> "Chromatic: " + (this.scales.isChromatic () ? "On" : "Off"));
                break;

            case TEMPORARY_TUNE:
                if (increase)
                    this.scales.incOctave ();
                else
                    this.scales.decOctave ();
                this.mvHelper.delayDisplay ( () -> "Octave: " + this.scales.getOctave ());
                break;

            default:
                // Not used
                break;
        }

        this.updateScale ();
    }
}