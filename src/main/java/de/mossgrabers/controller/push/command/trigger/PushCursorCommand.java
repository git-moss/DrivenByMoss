// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushCursorCommand extends de.mossgrabers.framework.command.trigger.CursorCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public PushCursorCommand (final Direction direction, final IModel model, final PushControlSurface surface)
    {
        super (direction, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = tb.getSceneBank ();
        this.canScrollUp = sceneBank.canScrollPageBackwards ();
        this.canScrollDown = sceneBank.canScrollPageForwards ();

        final ModeManager modeManager = this.surface.getModeManager ();
        final Mode mode = modeManager.getActiveOrTempMode ();

        if (modeManager.isActiveOrTempMode (Modes.MODE_BROWSER))
        {
            final IBrowser browser = this.model.getBrowser ();
            this.canScrollLeft = browser.hasPreviousContentType ();
            this.canScrollRight = browser.hasNextContentType ();
            return;
        }

        final boolean shiftPressed = this.surface.isShiftPressed ();
        this.canScrollLeft = shiftPressed ? mode.hasPreviousItemPage () : mode.hasPreviousItem ();
        this.canScrollRight = shiftPressed ? mode.hasNextItemPage () : mode.hasNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectNextItem ();
    }
}
