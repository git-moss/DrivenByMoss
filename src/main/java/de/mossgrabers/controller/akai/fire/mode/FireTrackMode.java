// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.graphics.canvas.component.TitleValueComponent;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.TrackMode;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;

import java.util.Optional;


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

    protected Modes               selectedParameter = Modes.VOLUME;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FireTrackMode (final FireControlSurface surface, final IModel model)
    {
        this ("Track", surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected FireTrackMode (final String name, final FireControlSurface surface, final IModel model)
    {
        super (name, surface, model, false, null);

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));
        this.setParameterProvider (new Fire4KnobProvider (surface, new SelectedTrackParameterProvider (model)));
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
        int vuLeft = -1;
        int vuRight = -1;
        boolean isPan = false;

        final Optional<ITrack> trackOptional = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (trackOptional.isPresent ())
        {
            final ITrack track = trackOptional.get ();
            vuLeft = track.getVuLeft ();
            vuRight = track.getVuRight ();

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

                case SEND1, SEND2, SEND3, SEND4, SEND5, SEND6:
                    final int sendIndex = this.selectedParameter.ordinal () - Modes.SEND1.ordinal ();
                    label = getSendLabel (sendBank, sendIndex);
                    value = getSendValue (sendBank, sendIndex);
                    break;

                default:
                    // Not used
                    break;
            }
        }

        display.addElement (new TitleValueComponent (desc, label, value, vuLeft, vuRight, isPan));
        display.send ();
    }


    /**
     * Ensure that the correct mode is still active in case the ALT key was toggled.
     */
    protected void updateMode ()
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

        this.setTouchedKnob (index, isTouched);

        final Optional<ITrack> trackOptional = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (!trackOptional.isPresent ())
            return;

        final ITrack track = trackOptional.get ();
        switch (this.selectedParameter)
        {
            case VOLUME:
                if (isTouched && this.surface.isDeletePressed ())
                    track.resetVolume ();
                track.touchVolume (isTouched);
                break;

            case PAN:
                if (isTouched && this.surface.isDeletePressed ())
                    track.resetPan ();
                track.touchPan (isTouched);
                break;

            case SEND1, SEND2, SEND3, SEND4, SEND5, SEND6:
                final int sendIndex = this.selectedParameter.ordinal () - Modes.SEND1.ordinal ();
                final ISend item = track.getSendBank ().getItem (sendIndex);
                if (isTouched && this.surface.isDeletePressed ())
                    item.resetValue ();
                item.touchValue (isTouched);
                break;

            default:
                // Not used
                break;
        }
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
