// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.mode;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Session mode.
 *
 * @author Jürgen Moßgraber
 */
public class SessionMode extends AbstractParameterMode<SLControlSurface, SLConfiguration, IItem>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionMode (final SLControlSurface surface, final IModel model)
    {
        super ("Session", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (1);
        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < 8; i++)
        {
            final IScene scene = sceneBank.getItem (i);
            String sceneTitle = "";
            if (scene.doesExist ())
            {
                sceneTitle = scene.getName ();
                if (sceneTitle.isBlank ())
                    sceneTitle = "Scene " + (i + 1);
            }
            d.setCell (1, i, StringUtils.shortenAndFixASCII (sceneTitle, 7));
        }
        d.done (0).done (1);
    }
}
