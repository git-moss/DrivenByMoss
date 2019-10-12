// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.view;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The Scene view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneView extends BaseView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public SceneView (final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super ("Scene", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeFunction (final int padIndex)
    {
        final IScene scene = this.model.getSceneBank ().getItem (padIndex);

        final MaschineMikroMk3Configuration configuration = this.surface.getConfiguration ();
        if (configuration.isDuplicateEnabled ())
        {
            scene.duplicate ();
            this.disableDuplicate ();
            return;
        }

        if (this.surface.getConfiguration ().isSelectClipOnLaunch ())
            scene.select ();
        scene.launch ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (index)
        {
            case 0:
                this.model.getCurrentTrackBank ().selectPreviousItem ();
                break;
            case 1:
                this.model.getCurrentTrackBank ().selectNextItem ();
                break;
            case 2:
                this.model.getSceneBank ().selectPreviousPage ();
                break;
            case 3:
                this.model.getSceneBank ().selectNextPage ();
                break;
            default:
                // Not used
                break;
        }
    }
}