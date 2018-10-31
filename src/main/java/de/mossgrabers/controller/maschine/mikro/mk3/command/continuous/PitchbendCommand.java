// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.command.continuous;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to handle pitchbend.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PitchbendCommand extends AbstractContinuousCommand<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    private int pitchValue = 0;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PitchbendCommand (final IModel model, final MaschineMikroMk3ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final MaschineMikroMk3Configuration config = this.surface.getConfiguration ();
        switch (config.getRibbonMode ())
        {
            case MaschineMikroMk3Configuration.RIBBON_MODE_PITCH_DOWN:
                // TODO
                // this.surface.sendMidiEvent (0xE0, data1, data2);
                break;

            case MaschineMikroMk3Configuration.RIBBON_MODE_PITCH_UP:
                // TODO
                // this.surface.sendMidiEvent (0xE0, data1, data2);
                break;

            case MaschineMikroMk3Configuration.RIBBON_MODE_PITCH_DOWN_UP:
                // this.surface.sendMidiEvent (0xE0, data1, data2);
                break;

            case MaschineMikroMk3Configuration.RIBBON_MODE_CC_1:
                this.surface.sendMidiEvent (0xB0, 1, value);
                this.pitchValue = value;
                break;

            case MaschineMikroMk3Configuration.RIBBON_MODE_CC_11:
                this.surface.sendMidiEvent (0xB0, 11, value);
                this.pitchValue = value;
                break;

            case MaschineMikroMk3Configuration.RIBBON_MODE_MASTER_VOLUME:
                this.model.getMasterTrack ().setVolume (this.model.getValueChanger ().toDAWValue (value));
                return;
        }

        // TODO this.surface.getOutput ().sendPitchbend (data1, data2);
    }

    // /** {@inheritDoc} */
    // @Override
    // public void updateValue ()
    // {
    // final MaschineMikroMk3Configuration config = this.surface.getConfiguration ();
    // switch (config.getRibbonMode ())
    // {
    // case MaschineMikroMk3Configuration.RIBBON_MODE_CC:
    // this.surface.setRibbonValue (this.pitchValue);
    // break;
    //
    // case MaschineMikroMk3Configuration.RIBBON_MODE_FADER:
    // final ITrack t = this.model.getSelectedTrack ();
    // this.surface.setRibbonValue (t == null ? 0 : this.model.getValueChanger ().toMidiValue
    // (t.getVolume ()));
    // break;
    //
    // default:
    // this.surface.setRibbonValue (64);
    // break;
    // }
    // }
}
