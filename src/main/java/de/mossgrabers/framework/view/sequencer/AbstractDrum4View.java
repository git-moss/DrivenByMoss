// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.Views;


/**
 * Abstract implementation for a drum sequencer with 4 lanes (sounds). The grid is split into 2
 * areas: The upper part are the steps 1-8 of the active page and resolution and the lower part the
 * steps 9-16.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractDrum4View<S extends IControlSurface<C>, C extends Configuration> extends AbstractDrumLaneView<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useDawColors True to use the drum machine pad colors for coloring the octaves
     */
    protected AbstractDrum4View (final S surface, final IModel model, final boolean useDawColors)
    {
        super (Views.NAME_DRUM4, surface, model, 4, 16, useDawColors);
    }


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param lanes The number of lanes to display
     * @param numRows The number of available rows on the grid
     * @param numColumns The number of available columns
     * @param clipCols The columns of the clip
     * @param followSelection Follow the drum pad selection if true
     * @param useDawColors True to use the drum machine pad colors for coloring the octaves
     */
    protected AbstractDrum4View (final S surface, final IModel model, final int lanes, final int numRows, final int numColumns, final int clipCols, final boolean followSelection, final boolean useDawColors)
    {
        super (Views.NAME_DRUM4, surface, model, lanes, numRows, numColumns, clipCols, followSelection, useDawColors);
    }
}
