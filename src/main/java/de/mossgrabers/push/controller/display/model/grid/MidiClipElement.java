// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model.grid;

import de.mossgrabers.framework.daw.CursorClipProxy;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.push.PushConfiguration;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.api.GraphicsOutput;
import com.bitwig.extension.api.GraphicsOutput.AntialiasMode;


/**
 * An element which displays the notes of a midi clip.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiClipElement extends AbstractGridElement
{
    private final CursorClipProxy clip;
    private int                   quartersPerMeasure;


    /**
     * Constructor.
     *
     * @param clip The clip to display
     * @param quartersPerMeasure The quarters of a measure
     */
    public MidiClipElement (final CursorClipProxy clip, final int quartersPerMeasure)
    {
        super (null, false, null, null, null, false);
        this.clip = clip;
        this.quartersPerMeasure = quartersPerMeasure;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final GraphicsOutput gc, final double left, final double width, final double height, final PushConfiguration configuration)
    {
        final int top = 14;
        final double noteAreaHeight = height - top;

        gc.setAntialias (AntialiasMode.OFF);

        // Draw the background
        gc.setColor (Color.fromRGB255 (168, 168, 168));
        gc.rectangle (left, top, width, noteAreaHeight);
        gc.fill ();

        // Draw the loop, if any and ...
        final int numSteps = this.clip.getNumSteps ();
        final double stepLength = this.clip.getStepLength ();
        final double pageLength = numSteps * stepLength;
        final int editPage = this.clip.getEditPage ();
        final double startPos = editPage * pageLength;
        final double endPos = (editPage + 1) * pageLength;
        final int len = top - 1;
        if (this.clip.isLoopEnabled ())
        {
            final double loopStart = this.clip.getLoopStart ();
            final double loopLength = this.clip.getLoopLength ();
            // ... the loop is visible in the current page
            if (loopStart < endPos && loopStart + loopLength > startPos)
            {
                final double start = Math.max (0, loopStart - startPos);
                final double end = Math.min (endPos, loopStart + loopLength) - startPos;
                final double x = width * start / pageLength;
                final double w = width * end / pageLength - x;
                // The header loop
                gc.setColor (Color.fromRGB255 (84, 84, 84));
                gc.rectangle (x + 1, 0, w, len);
                gc.fill ();

                // Background in note area
                gc.setColor (Color.fromRGB255 (181, 181, 181));
                gc.rectangle (x + 1, top, w, noteAreaHeight);
                gc.fill ();
            }
        }
        // Draw play start in header
        gc.setAntialias (AntialiasMode.BEST);
        final Color clipColor = this.clip.getColor ();
        final double playStart = this.clip.getPlayStart ();
        if (playStart >= startPos && playStart <= endPos)
        {
            final double start = playStart - startPos;
            final double x = width * start / pageLength;
            gc.setColor (clipColor);
            gc.moveTo (x + 1, 0);
            gc.lineTo (x + 1 + len, len / 2);
            gc.lineTo (x + 1, len);
            gc.lineTo (x + 1, 0);
            gc.fill ();
        }
        // Draw play end in header
        final double playEnd = this.clip.getPlayEnd ();
        if (playEnd >= startPos && playEnd <= endPos)
        {
            final double end = playEnd - startPos;
            final double x = width * end / pageLength;
            gc.setColor (clipColor);
            gc.moveTo (x + 1, 0);
            gc.lineTo (x + 1, len);
            gc.lineTo (x + 1 - top, len / 2);
            gc.moveTo (x + 1, 0);
            gc.fill ();
        }
        gc.setAntialias (AntialiasMode.OFF);

        // Draw dividers
        final double stepWidth = width / numSteps;
        gc.setColor (Color.fromRGB255 (130, 130, 130));
        for (int step = 0; step <= numSteps; step++)
        {
            final double x = left + step * stepWidth;
            gc.rectangle (x, top, 1, noteAreaHeight);
            gc.fill ();

            // Draw measure texts
            if (step % 4 == 0)
            {
                gc.setAntialias (AntialiasMode.BEST);
                gc.setFontSize (top);
                final double time = startPos + step * stepLength;
                final String measureText = CursorClipProxy.formatMeasures (this.quartersPerMeasure, time);
                drawTextInHeight (gc, measureText, x, 0, top - 1, Color.whiteColor ());
                gc.setAntialias (AntialiasMode.OFF);
            }
        }

        // Draw the notes
        final int lowerRowWithData = this.clip.getLowerRowWithData ();
        if (lowerRowWithData == -1)
            return;
        final int upperRowWithData = this.clip.getUpperRowWithData ();
        final int range = 1 + upperRowWithData - lowerRowWithData;
        final double stepHeight = noteAreaHeight / range;

        final double fontSize = calculateFontSize (gc, stepHeight, stepWidth);
        if (fontSize > 0)
            gc.setFontSize (fontSize);

        for (int row = 0; row < range; row++)
        {
            gc.setColor (Color.fromRGB255 (130, 130, 130));
            gc.rectangle (left, top + (range - row - 1) * stepHeight, width, 1);
            gc.fill ();

            for (int step = 0; step < numSteps; step++)
            {
                final int note = lowerRowWithData + row;

                // Get step, check for length
                final int stepState = this.clip.getStep (step, note);
                if (stepState == 0)
                    continue;

                double x = left + step * stepWidth - 1;
                double w = stepWidth + 2;
                final boolean isStart = stepState == 2;
                if (isStart)
                {
                    x += 2;
                    w -= 2;
                }

                gc.setColor (Color.fromRGB255 (0, 0, 0));
                gc.setLineWidth (1.0);
                gc.rectangle (x, top + (range - row - 1) * stepHeight + 2, w, stepHeight - 2);
                gc.stroke ();

                gc.setColor (clipColor);
                gc.rectangle (x + (isStart ? 0 : -2), top + (range - row - 1) * stepHeight + 2, w - 1 + (isStart ? 0 : 2), stepHeight - 3);
                gc.fill ();

                if (isStart && fontSize > 0)
                {
                    gc.setAntialias (AntialiasMode.BEST);
                    final String text = Scales.formatDrumNote (note);
                    drawTextInBounds (gc, text, x, top + (range - row - 1) * stepHeight + 2, w - 1, stepHeight - 3, Align.CENTER, Color.blackColor ());
                    gc.setAntialias (AntialiasMode.OFF);
                }
            }
        }

        gc.setAntialias (AntialiasMode.BEST);

        // Draw the play cursor
        final int playStep = this.clip.getCurrentStep ();
        if (playStep >= 0)
        {
            gc.setColor (Color.blackColor ());
            gc.rectangle (left + playStep * stepWidth, 0, 1, height);
            gc.fill ();
        }
    }


    /**
     * Calculates the maximum height of a text which needs to fit into a width.
     *
     * @param gc The graphics context
     * @param maxHeight The maximum height of the text
     * @param maxWidth The maximum width
     * @return The text height or -1 if the minimum height of 10 does not fit into the width
     */
    private static double calculateFontSize (final GraphicsOutput gc, final double maxHeight, final double maxWidth)
    {
        final String maxString = "G#5";
        final double minSize = 12.0;

        double size = minSize;
        double fittingSize = -1;
        while (size < maxHeight)
        {
            gc.setFontSize (size);
            final double width = gc.getTextExtents (maxString).getWidth ();
            if (width > maxWidth)
                break;
            fittingSize = size;
            size += 1.0;
        }
        return fittingSize;
    }
}
