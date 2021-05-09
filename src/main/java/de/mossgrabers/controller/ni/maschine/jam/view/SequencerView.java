// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;
import de.mossgrabers.framework.view.Views;


/**
 * The Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends AbstractNoteSequencerView<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SequencerView (final MaschineJamControlSurface surface, final IModel model)
    {
        super (Views.NAME_SEQUENCER, surface, model, true);
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
                    this.onOctaveUp (ButtonEvent.DOWN);
                else
                    this.onOctaveDown (ButtonEvent.DOWN);
                break;

            default:
                // Not used
                break;
        }

        this.updateScaleConfig ();
    }


    private void updateScaleConfig ()
    {
        final MaschineJamConfiguration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES.get (this.scales.getScaleOffset ()));
        config.setScaleInKey (!this.scales.isChromatic ());
        config.setScaleLayout (this.scales.getScaleLayout ().getName ());

        this.updateNoteMapping ();
    }
}