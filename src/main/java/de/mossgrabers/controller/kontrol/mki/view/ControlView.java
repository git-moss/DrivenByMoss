// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.view;

import de.mossgrabers.controller.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.controller.grid.ILightGuide;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractPlayView;


/**
 * The view for controlling the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlView extends AbstractPlayView<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final Kontrol1ControlSurface surface, final IModel model)
    {
        super (surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        final boolean isActive = this.surface.getConfiguration ().isScaleIsActive ();
        final int [] matrix = isActive && this.model.canSelectedTrackHoldNotes () ? Scales.getIdentityMatrix () : EMPTY_TABLE;
        // Only set the key manager not the translation matrix, since the keyboard always plays the
        // same notes!
        this.surface.scheduleTask ( () -> this.keyManager.setNoteMatrix (matrix), 6);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ILightGuide lightGuide = this.surface.getLightGuide ();

        if (this.model.canSelectedTrackHoldNotes ())
        {
            final ICursorDevice primary = this.model.getInstrumentDevice ();
            if (primary.hasDrumPads ())
            {
                boolean isSoloed = false;
                final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
                for (int i = 0; i < drumPadBank.getPageSize (); i++)
                {
                    if (drumPadBank.getItem (i).isSolo ())
                    {
                        isSoloed = true;
                        break;
                    }
                }

                final boolean isRecording = this.model.hasRecordingState ();
                for (int i = this.scales.getStartNote (); i < this.scales.getEndNote (); i++)
                    lightGuide.light (i, this.getDrumPadColor (i, primary, isSoloed, isRecording));

                return;
            }
        }

        this.drawLightGuide (lightGuide);
    }


    protected String getDrumPadColor (final int index, final ICursorDevice primary, final boolean isSoloed, final boolean isRecording)
    {
        // Playing note?
        if (this.keyManager.isKeyPressed (index))
            return isRecording ? AbstractDrumView.COLOR_PAD_RECORD : AbstractDrumView.COLOR_PAD_PLAY;
        // Exists and active?
        final IChannel drumPad = primary.getDrumPadBank ().getItem (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return this.surface.getConfiguration ().isTurnOffEmptyDrumPads () ? AbstractDrumView.COLOR_PAD_OFF : AbstractDrumView.COLOR_PAD_NO_CONTENT;
        // Muted or soloed?
        if (drumPad.isMute () || isSoloed && !drumPad.isSolo ())
            return AbstractDrumView.COLOR_PAD_MUTED;
        return DAWColor.getColorIndex (drumPad.getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }
}