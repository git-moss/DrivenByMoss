// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.mode;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.graphics.canvas.component.TitleValueComponent;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.TrackMode;
import de.mossgrabers.framework.parameterprovider.ChannelParameterProvider;


/**
 * The track mode. The knobs control the volume, the panorama and the sends of the selected track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireTrackMode extends TrackMode<FireControlSurface, FireConfiguration>
{
    private static final Modes [] MODES             =
    {
        Modes.VOLUME,
        Modes.PAN,
        Modes.SEND1,
        Modes.SEND2
    };

    private static final Modes [] ALT_MODES         =
    {
        Modes.SEND3,
        Modes.SEND4,
        Modes.SEND5,
        Modes.SEND6
    };

    private Modes                 selectedParameter = Modes.VOLUME;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FireTrackMode (final FireControlSurface surface, final IModel model)
    {
        super ("Mixer", surface, model, false, null);

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));

        this.setParameters (new Fire4KnobProvider (surface, new ChannelParameterProvider (model)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateMode ();

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        String desc = "Select";
        String label = "a track";
        int value = -1;
        boolean isPan = false;

        final ITrack track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track != null)
        {
            desc = track.getPosition () + 1 + ": " + track.getName (9);

            final ISendBank sendBank = track.getSendBank ();

            switch (this.selectedParameter)
            {
                case VOLUME:
                    label = "Vol: " + track.getVolumeStr ();
                    value = track.getVolume ();
                    break;

                case PAN:
                    label = "Pan: " + track.getPanStr ();
                    value = track.getPan ();
                    isPan = true;
                    break;

                case SEND1:
                case SEND2:
                case SEND3:
                case SEND4:
                case SEND5:
                case SEND6:
                    final int sendIndex = this.selectedParameter.ordinal () - Modes.SEND1.ordinal ();
                    label = getSendLabel (sendBank, sendIndex);
                    value = getSendValue (sendBank, sendIndex);
                    break;

                default:
                    // Not used
                    break;
            }
        }

        display.addElement (new TitleValueComponent (desc, label, value, isPan));
        display.send ();
    }


    /**
     * Ensure that the correct mode is still active in case the ALT key was toggled.
     */
    private void updateMode ()
    {
        int index = -1;
        final Modes [] ms = this.surface.isPressed (ButtonID.ALT) ? MODES : ALT_MODES;
        for (int i = 0; i < ms.length; i++)
        {
            if (ms[i] == this.selectedParameter)
            {
                index = i;
                break;
            }
        }
        if (index >= 0)
            this.selectedParameter = this.surface.isPressed (ButtonID.ALT) ? ALT_MODES[index] : MODES[index];
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.selectedParameter = this.surface.isPressed (ButtonID.ALT) ? ALT_MODES[index] : MODES[index];

        super.onKnobTouch (index, isTouched);
    }


    private static String getSendLabel (final ISendBank sendBank, final int index)
    {
        final int pos = index + 1;

        if (sendBank == null)
            return "No FX " + pos;

        final ISend send = sendBank.getItem (index);
        if (!send.doesExist ())
            return "No FX " + pos;

        return send.getName (4) + ": " + send.getDisplayedValue ();
    }


    private static int getSendValue (final ISendBank sendBank, final int index)
    {
        if (sendBank == null)
            return 0;
        final ISend send = sendBank.getItem (index);
        return send.doesExist () ? send.getValue () : 0;
    }
}
