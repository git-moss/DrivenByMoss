// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.view.IMaschineView;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * View to select the velocity for new notes.
 *
 * @author Jürgen Moßgraber
 */
public class AccentView extends AbstractView<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineView
{
    private static final int BLOCK_SIZE = 4;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public AccentView (final MaschineJamControlSurface surface, final IModel model)
    {
        super ("Accent", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        for (int i = 36; i < 40; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 44; i < 48; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 52; i < 56; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 60; i < 64; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 68; i < 100; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);

        final MaschineJamConfiguration configuration = this.surface.getConfiguration ();
        int selectedPad = 15 - configuration.getFixedAccentValue () / 8;
        final int selY = selectedPad / 4;
        final int selX = selectedPad % 4;
        selectedPad = selY * 4 + 3 - selX;

        for (int pad = 0; pad < 16; pad++)
        {
            final int x = BLOCK_SIZE + pad % BLOCK_SIZE;
            final int y = BLOCK_SIZE + pad / BLOCK_SIZE;
            padGrid.lightEx (x, y, pad == selectedPad ? MaschineColorManager.COLOR_GREEN : MaschineColorManager.COLOR_LIME_LO);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;
        if (x < 4 || y >= 4)
            return;

        final int pad = (BLOCK_SIZE - 1 - y) * BLOCK_SIZE + x - BLOCK_SIZE;
        final int selPad = (3 - pad / 4) * 4 + pad % 4;
        this.surface.getConfiguration ().setFixedAccentValue ((selPad + 1) * 8 - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void changeOption (final EncoderMode temporaryEncoderMode, final int control)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final Views activeID = viewManager.getActiveIDIgnoreTemporary ();
        if (activeID == Views.CONTROL)
            return;
        final IView view = viewManager.get (activeID);
        if (view instanceof final IMaschineView maschineView)
            maschineView.changeOption (temporaryEncoderMode, control);
    }
}