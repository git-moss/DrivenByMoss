// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.mode;

import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;

import java.util.Optional;


/**
 * Base mode for all Kontrol 1 modes.
 *
 * @param <B> The type of the item bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractKontrol1Mode<B extends IItem> extends AbstractParameterMode<Kontrol1ControlSurface, Kontrol1Configuration, B> implements IKontrol1Mode
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The surface
     * @param model The model
     */
    protected AbstractKontrol1Mode (final String name, final Kontrol1ControlSurface surface, final IModel model)
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
    protected AbstractKontrol1Mode (final String name, final Kontrol1ControlSurface surface, final IModel model, final IBank<B> bank)
    {
        super (name, surface, model, false, bank, DEFAULT_KNOB_IDS);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Note: this implementation uses USB and there is no Bitwig HW support for audio process
        // mapping!
        final IParameterProvider parameterProvider = this.getParameterProvider ();
        if (parameterProvider != null)
            parameterProvider.get (index).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final Optional<ITrack> t = this.model.getCurrentTrackBank ().getSelectedItem ();

        switch (buttonID)
        {
            case MUTE:
                return t.isPresent () && t.get ().isMute () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON;
            case SOLO:
                return t.isPresent () && t.get ().isSolo () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON;
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
        final Optional<ITrack> track = tb.getSelectedItem ();
        if (track.isEmpty ())
            tb.getItem (0).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void onBack ()
    {
        final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack.isEmpty ())
            return;
        if (this.surface.isShiftPressed ())
            selectedTrack.get ().toggleMonitor ();
        else
            selectedTrack.get ().toggleMute ();
    }


    /** {@inheritDoc} */
    @Override
    public void onEnter ()
    {
        final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack.isEmpty ())
            return;
        if (this.surface.isShiftPressed ())
            selectedTrack.get ().toggleRecArm ();
        else
            selectedTrack.get ().toggleSolo ();
    }


    protected static String getSecondLineText (final ITrack track)
    {
        if (track.isMute ())
            return "-MUTED-";
        return track.isSolo () ? "-SOLO-" : track.getVolumeStr (8);
    }
}
