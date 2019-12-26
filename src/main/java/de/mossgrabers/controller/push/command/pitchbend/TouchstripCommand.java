// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.pitchbend;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractPitchbendCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.view.Views;


/**
 * Command to handle pitchbend.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TouchstripCommand extends AbstractPitchbendCommand<PushControlSurface, PushConfiguration>
{
    private int pitchValue = 0;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TouchstripCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void onPitchbend (final int data1, final int data2)
    {
        if (this.surface.getViewManager ().isActiveView (Views.SESSION))
        {
            final int value = this.surface.isShiftPressed () ? 63 : data2;
            this.model.getTransport ().setCrossfade (this.model.getValueChanger ().toDAWValue (value));
            this.surface.setRibbonValue (value);
            return;
        }

        // Don't get in the way of configuration
        if (this.surface.isShiftPressed ())
            return;

        final PushConfiguration config = this.surface.getConfiguration ();
        switch (config.getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_PITCH:
                this.surface.sendMidiEvent (0xE0, data1, data2);
                break;

            case PushConfiguration.RIBBON_MODE_CC:
                this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), data2);
                this.pitchValue = data2;
                break;

            case PushConfiguration.RIBBON_MODE_CC_PB:
                if (data2 > 64)
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                else if (data2 < 64)
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 127 - data2 * 2);
                else
                {
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 0);
                }
                break;

            case PushConfiguration.RIBBON_MODE_PB_CC:
                if (data2 > 64)
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), (data2 - 64) * 2);
                else if (data2 < 64)
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                else
                {
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 0);
                }
                break;

            case PushConfiguration.RIBBON_MODE_FADER:
                final ITrack selTrack = this.model.getSelectedTrack ();
                if (selTrack != null)
                    selTrack.setVolume (this.model.getValueChanger ().toDAWValue (data2));
                return;

            default:
                // Not used
                break;
        }

        this.surface.getMidiOutput ().sendPitchbend (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void updateValue ()
    {
        if (this.surface.getViewManager ().isActiveView (Views.SESSION))
        {
            this.surface.setRibbonValue (this.model.getValueChanger ().toMidiValue (this.model.getTransport ().getCrossfade ()));
            return;
        }

        final PushConfiguration config = this.surface.getConfiguration ();
        switch (config.getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_CC:
                this.surface.setRibbonValue (this.pitchValue);
                break;

            case PushConfiguration.RIBBON_MODE_FADER:
                final ITrack t = this.model.getSelectedTrack ();
                this.surface.setRibbonValue (t == null ? 0 : this.model.getValueChanger ().toMidiValue (t.getVolume ()));
                break;

            default:
                this.surface.setRibbonValue (64);
                break;
        }
    }
}
