// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
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
     * Draw the piano view grid.
     * 
     * @param gridPad The grid to draw
     * @param model The model
     * @param keyManager The key manager
     */
    public static void drawGrid (final IPadGrid gridPad, final IModel model, final KeyManager keyManager)
    {
        if (!model.canSelectedTrackHoldNotes ())
        {
            gridPad.turnOff ();
            return;
        }

        final ColorManager colorManager = model.getColorManager ();
        final boolean isRecording = model.hasRecordingState ();
        final ITrack track = model.getSelectedTrack ();
        final int playKeyColor = colorManager.getColorIndex (isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY);
        final int whiteKeyColor = colorManager.getColorIndex (Scales.SCALE_COLOR_NOTE);
        final int blackKeyColor = colorManager.getColorIndex (AbstractView.replaceOctaveColorWithTrackColor (track, Scales.SCALE_COLOR_OCTAVE));
        final int offKeyColor = colorManager.getColorIndex (Scales.SCALE_COLOR_OFF);

        for (int i = 0; i < 8; i++)
        {
            if (i % 2 == 0)
            {
                for (int j = 0; j < 8; j++)
                {
                    final int n = 36 + 8 * i + j;
                    gridPad.light (n, keyManager.isKeyPressed (n) ? playKeyColor : whiteKeyColor, -1, false);
                }
            }
            else
            {
                for (int j = 0; j < 8; j++)
                {
                    final int n = 36 + 8 * i + j;
                    if (j == 0 || j == 3 || j == 7)
                        gridPad.light (n, offKeyColor, -1, false);
                    else
                        gridPad.light (n, keyManager.isKeyPressed (n) ? playKeyColor : blackKeyColor, -1, false);
                }
            }
        }
    }
}
