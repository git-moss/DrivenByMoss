// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;


/**
 * Command for cursor arrow keys.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class CursorCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private static final int BUTTON_REPEAT_INTERVAL = 75;

    /** The direction of the cursor. */
    public enum Direction
    {
        /** Move left. */
        LEFT,
        /** Move right. */
        RIGHT,
        /** Move up. */
        UP,
        /** Move down. */
        DOWN
    }

    protected Direction direction;
    protected boolean   canScrollLeft;
    protected boolean   canScrollRight;
    protected boolean   canScrollUp;
    protected boolean   canScrollDown;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public CursorCommand (final Direction direction, final Model model, final S surface)
    {
        super (model, surface);
        this.direction = direction;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (this.direction)
        {
            case LEFT:
                this.scrollLeft ();
                break;
            case RIGHT:
                this.scrollRight ();
                break;
            case UP:
                this.scrollUp ();
                break;
            case DOWN:
                this.scrollDown ();
                break;
        }
    }


    /**
     * Update the arrow buttons.
     */
    public void updateArrows ()
    {
        this.updateArrowStates ();
        this.surface.scheduleTask (this::delayedUpdateArrows, 150);
    }


    protected void delayedUpdateArrows ()
    {
        this.surface.updateButton (this.surface.getLeftButtonId (), this.canScrollLeft ? this.getButtonOnColor () : this.getButtonOffColor ());
        this.surface.updateButton (this.surface.getRightButtonId (), this.canScrollRight ? this.getButtonOnColor () : this.getButtonOffColor ());
        this.surface.updateButton (this.surface.getUpButtonId (), this.canScrollUp ? this.getButtonOnColor () : this.getButtonOffColor ());
        this.surface.updateButton (this.surface.getDownButtonId (), this.canScrollDown ? this.getButtonOnColor () : this.getButtonOffColor ());
    }


    /**
     * Get the color of when the button should be off.
     *
     * @return The color ID
     */
    protected int getButtonOffColor ()
    {
        return this.model.getColorManager ().getColor (ColorManager.BUTTON_STATE_OFF);
    }


    /**
     * Get the color of when the button should be on.
     *
     * @return The color ID
     */
    protected int getButtonOnColor ()
    {
        return this.model.getColorManager ().getColor (ColorManager.BUTTON_STATE_ON);
    }


    /**
     * Update the states of the arrow buttons.
     */
    protected abstract void updateArrowStates ();


    /**
     * Scroll left. Tracks, devices or parameter banks.
     */
    protected abstract void scrollLeft ();


    /**
     * Scroll right. Tracks, devices or parameter banks.
     */
    protected abstract void scrollRight ();


    /**
     * Scroll scenes up.
     */
    protected void scrollUp ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        if (this.surface.isShiftPressed ())
            tb.scrollScenesPageUp ();
        else
            tb.scrollScenesUp ();
    }


    /**
     * Scroll scenes down.
     */
    protected void scrollDown ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        if (this.surface.isShiftPressed ())
            tb.scrollScenesPageDown ();
        else
            tb.scrollScenesDown ();
    }


    protected void scrollTracksLeft ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData sel = tb.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1 || this.surface.isShiftPressed ())
        {
            this.scrollTrackBankLeft (sel, index);
            return;
        }
        this.selectTrack (index);
    }


    protected void scrollTrackBankLeft (final TrackData sel, final int index)
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollTracksUp ())
            return;
        tb.scrollTracksPageUp ();
        final int newSel = index == -1 || sel == null ? 7 : sel.getIndex ();
        this.surface.scheduleTask ( () -> this.selectTrack (newSel), BUTTON_REPEAT_INTERVAL);
    }


    protected void scrollTracksRight ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData sel = tb.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == 8 || this.surface.isShiftPressed ())
        {
            this.scrollTrackBankRight (sel, index);
            return;
        }
        this.selectTrack (index);
    }


    protected void scrollTrackBankRight (final TrackData sel, final int index)
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollTracksDown ())
            return;
        tb.scrollTracksPageDown ();
        final int newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
        this.surface.scheduleTask ( () -> this.selectTrack (newSel), BUTTON_REPEAT_INTERVAL);
    }
}
