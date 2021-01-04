// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.mode;

import de.mossgrabers.controller.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * Base mode for all Kontrol 1 modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractKontrol1Mode extends AbstractMode<Kontrol1ControlSurface, Kontrol1Configuration> implements IKontrol1Mode
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The surface
     * @param model The model
     */
    public AbstractKontrol1Mode (final String name, final Kontrol1ControlSurface surface, final IModel model)
    {
        super (name, surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The surface
     * @param model The model
     * @param bank The bank
     */
    public AbstractKontrol1Mode (final String name, final Kontrol1ControlSurface surface, final IModel model, final IBank<? extends IItem> bank)
    {
        super (name, surface, model, false, bank, DEFAULT_KNOB_IDS);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final ITrack t = this.model.getCurrentTrackBank ().getSelectedItem ();

        switch (buttonID)
        {
            case MUTE:
                return t != null && t.isMute () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON;
            case SOLO:
                return t != null && t.isSolo () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON;
            case BROWSE:
                return ColorManager.BUTTON_STATE_ON;
            default:
                return ColorManager.BUTTON_STATE_OFF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onMainKnob (final int value)
    {
        if (this.model.getValueChanger ().isIncrease (value))
            this.selectNextItem ();
        else
            this.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void onMainKnobPressed ()
    {
        this.model.toggleCurrentTrackBank ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getSelectedItem ();
        if (track == null)
            tb.getItem (0).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void onBack ()
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack == null)
            return;
        if (this.surface.isShiftPressed ())
            selectedTrack.toggleMonitor ();
        else
            selectedTrack.toggleMute ();
    }


    /** {@inheritDoc} */
    @Override
    public void onEnter ()
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack == null)
            return;
        if (this.surface.isShiftPressed ())
            selectedTrack.toggleRecArm ();
        else
            selectedTrack.toggleSolo ();
    }


    protected static String getSecondLineText (final ITrack track)
    {
        if (track.isMute ())
            return "-MUTED-";
        return track.isSolo () ? "-SOLO-" : track.getVolumeStr (8);
    }
}
