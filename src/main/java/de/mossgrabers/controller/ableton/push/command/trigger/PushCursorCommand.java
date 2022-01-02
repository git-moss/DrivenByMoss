// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.trigger;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.view.Views;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushCursorCommand extends CursorCommand<PushControlSurface, PushConfiguration>
{
    private final ISceneBank sceneBank64;


    /**
     * Constructor.
     *
     * @param sceneBank The 64 entry scene bank
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public PushCursorCommand (final ISceneBank sceneBank, final Direction direction, final IModel model, final PushControlSurface surface)
    {
        super (direction, model, surface, false);

        this.sceneBank64 = sceneBank;
    }


    /**
     * Scroll scenes up.
     */
    @Override
    protected void scrollUp ()
    {
        final ISceneBank sceneBank = this.getSceneBank ();
        if (this.surface.isShiftPressed () || this.isScenePlay ())
            sceneBank.selectPreviousPage ();
        else
            sceneBank.scrollBackwards ();
    }


    /**
     * Scroll scenes down.
     */
    @Override
    protected void scrollDown ()
    {
        final ISceneBank sceneBank = this.getSceneBank ();
        if (this.surface.isShiftPressed () || this.isScenePlay ())
            sceneBank.selectNextPage ();
        else
            sceneBank.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    protected ISceneBank getSceneBank ()
    {
        if (this.isScenePlay ())
            return this.sceneBank64;
        return this.model.getCurrentTrackBank ().getSceneBank ();
    }


    private boolean isScenePlay ()
    {
        return this.surface.getViewManager ().isActive (Views.SCENE_PLAY);
    }
}
