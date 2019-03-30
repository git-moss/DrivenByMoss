// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.mode;

import de.mossgrabers.controller.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1Colors;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1ControlSurface;
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
        final boolean canScrollLeft = selIndex > 0 || tb.canScrollPageBackwards ();
        final boolean canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollPageForwards ();
        final boolean canScrollUp = tb.canScrollPageForwards ();
        final boolean canScrollDown = tb.canScrollPageBackwards ();

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT, canScrollLeft ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT, canScrollRight ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_UP, canScrollUp ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN, canScrollDown ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_BACK, t != null && t.isMute () ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_ENTER, t != null && t.isSolo () ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_BROWSE, Kontrol1Colors.BUTTON_STATE_ON);
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
