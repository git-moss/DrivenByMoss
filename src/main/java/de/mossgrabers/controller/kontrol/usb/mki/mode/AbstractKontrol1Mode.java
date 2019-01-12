// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.mode;

import de.mossgrabers.controller.kontrol.usb.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;


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
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack t = tb.getSelectedItem ();
        final int selIndex = t != null ? t.getIndex () : -1;
        final boolean canScrollLeft = selIndex > 0 || tb.canScrollBackwards ();
        final boolean canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollForwards ();
        final boolean canScrollUp = tb.canScrollForwards ();
        final boolean canScrollDown = tb.canScrollBackwards ();

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT, canScrollLeft ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT, canScrollRight ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_UP, canScrollUp ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN, canScrollDown ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_BACK, t != null && t.isMute () ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_ENTER, t != null && t.isSolo () ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_BROWSE, Kontrol1ControlSurface.BUTTON_STATE_ON);
    }


    /** {@inheritDoc} */
    @Override
    public void onMainKnob (final int value)
    {
        if (this.model.getValueChanger ().calcKnobSpeed (value) > 0)
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
        if (selectedTrack != null)
            selectedTrack.toggleMute ();
    }


    /** {@inheritDoc} */
    @Override
    public void onEnter ()
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack != null)
            selectedTrack.toggleSolo ();
    }
}
