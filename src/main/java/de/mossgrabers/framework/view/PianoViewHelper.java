// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.KeyManager;


/**
 * Piano view helper methods. Since the piano view is sub-classed from the PlayView it cannot be
 * sub-classed from an abstract piano view. To not repeat the code here are the helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PianoViewHelper
{
    /**
     * Private due to helper class.
     */
    private PianoViewHelper ()
    {
        // Intentionally empty
    }


    /**
     * Draw the piano view grid. Uses 8 columns.
     *
     * @param gridPad The grid to draw
     * @param model The model
     * @param keyManager The key manager
     */
    public static void drawGrid (final IPadGrid gridPad, final IModel model, final KeyManager keyManager)
    {
        drawGrid (gridPad, model, keyManager, 8, 8);
    }


    /**
     * Draw the piano view grid.
     *
     * @param gridPad The grid to draw
     * @param model The model
     * @param keyManager The key manager
     * @param rows The number of rows of the grid
     * @param columns The number of columns of the grid
     */
    public static void drawGrid (final IPadGrid gridPad, final IModel model, final KeyManager keyManager, final int rows, final int columns)
    {
        if (!model.canSelectedTrackHoldNotes ())
        {
            gridPad.turnOff ();
            return;
        }

        final int startKey = 36;

        final ColorManager colorManager = model.getColorManager ();
        final boolean isRecording = model.hasRecordingState ();
        final ITrack track = model.getCursorTrack ();
        final int playKeyColor = colorManager.getColorIndex (isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY);
        final int whiteKeyColor = colorManager.getColorIndex (Scales.SCALE_COLOR_NOTE);
        final int blackKeyColor = colorManager.getColorIndex (AbstractView.replaceOctaveColorWithTrackColor (track, Scales.SCALE_COLOR_OCTAVE));
        final int offKeyColor = colorManager.getColorIndex (Scales.SCALE_COLOR_OFF);

        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < columns; column++)
            {
                final int n = startKey + columns * row + column;
                final int color;
                if (row % 2 == 0)
                {
                    // White keys
                    color = keyManager.isKeyPressed (n) ? playKeyColor : whiteKeyColor;
                }
                else
                {
                    // Black keys
                    final int octaveColumn = column % 7;
                    if (octaveColumn == 0 || octaveColumn == 3 || octaveColumn == 7)
                        color = offKeyColor;
                    else
                        color = keyManager.isKeyPressed (n) ? playKeyColor : blackKeyColor;
                }
                gridPad.light (n, color, -1, false);
            }
        }
    }
}
