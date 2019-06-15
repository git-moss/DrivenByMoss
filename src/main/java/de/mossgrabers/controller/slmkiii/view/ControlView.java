// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.view;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ControlOnlyView;
import de.mossgrabers.framework.view.SceneView;


/**
 * The view for controlling the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlView extends ControlOnlyView<SLMkIIIControlSurface, SLMkIIIConfiguration> implements SceneView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final SLMkIIIControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        this.model.getSceneBank ().getItem (7 - scene).launch ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        // TODO Auto-generated method stub

    }
}