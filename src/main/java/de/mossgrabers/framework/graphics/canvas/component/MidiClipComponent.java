// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * A component which displays the notes of a MIDI clip.
 *
 * @author Jürgen Moßgraber
 */
public class MidiClipComponent implements IComponent
{
    private final INoteClip clip;
    private final int       quartersPerMeasure;


    /**
     * Constructor.
     *
     * @param clip The clip to display
     * @param quartersPerMeasure The quarters of a measure
     */
    public MidiClipComponent (final INoteClip clip, final int quartersPerMeasure)
    {
        this.clip = clip;
        this.quartersPerMeasure = quartersPerMeasure;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        final IGraphicsConfiguration configuration = info.getConfiguration ();

        final ColorEx gridBackground = configuration.getColorBackgroundLighter ();
        final ColorEx measureTextColor = ColorEx.WHITE;

        final ColorEx dividersColor = configuration.getColorBackgroundDarker ();

        final ColorEx noteColor = this.clip.getColor ();
        final ColorEx noteMutedColor = ColorEx.DARK_GRAY;
        final ColorEx noteGridLoopColor = configuration.getColorBackground ();
        final ColorEx noteBorderColor = ColorEx.BLACK;

        final IGraphicsContext gc = info.getContext ();
        final double left = info.getBounds ().left ();
        final double width = info.getBounds ().width ();
        final double height = info.getBounds ().height ();

        final int top = 14;
        final double noteAreaHeight = height - top;

        // Draw the background
        gc.fillRectangle (left, top, width, noteAreaHeight, gridBackground);

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
                gc.fillRectangle (x + 1, 0, w, len, noteColor);

                // Background in note area
                gc.fillRectangle (x + 1, top, w, noteAreaHeight, noteGridLoopColor);
            }
        }
        // Draw play start in header
        final double playStart = this.clip.getPlayStart ();
        if (playStart >= startPos && playStart <= endPos)
        {
            final double start = playStart - startPos;
            final double x = width * start / pageLength;
            gc.fillTriangle (x + 1, 0, x + 1 + len, len / 2.0, x + 1, len, noteColor);
            gc.strokeTriangle (x + 1, 0, x + 1 + len, len / 2.0, x + 1, len, ColorEx.evenDarker (noteColor));
        }
        // Draw play end in header
        final double playEnd = this.clip.getPlayEnd ();
        if (playEnd >= startPos && playEnd <= endPos)
        {
            final double end = playEnd - startPos;
            final double x = width * end / pageLength;
            gc.fillTriangle (x + 1, 0, x + 1, len, x + 1 - top, len / 2.0, noteColor);
            gc.strokeTriangle (x + 1, 0, x + 1, len, x + 1 - top, len / 2.0, ColorEx.evenDarker (noteColor));
        }

        // Draw dividers
        final double stepWidth = width / numSteps;
        for (int step = 0; step <= numSteps; step++)
        {
            final double x = left + step * stepWidth;
            gc.fillRectangle (x, top, 1, noteAreaHeight, dividersColor);

            // Draw measure texts
            if (step % 4 == 0)
            {
                final double time = startPos + step * stepLength;
                final String measureText = StringUtils.formatMeasures (this.quartersPerMeasure, time, 1, false);
                gc.drawTextInHeight (measureText, x, 0, top - 1.0, measureTextColor, top);
            }
        }

        // Draw the notes
        final int lowerRowWithData = this.clip.getLowestRowWithData ();
        if (lowerRowWithData == -1)
            return;
        final int upperRowWithData = this.clip.getHighestRowWithData ();
        // Display at least 4 rows
        final int range = Math.max (4, 1 + upperRowWithData - lowerRowWithData);
        final double stepHeight = noteAreaHeight / range;

        final double fontSize = gc.calculateFontSize ("G#5", stepHeight, stepWidth, 12.0);

        final NotePosition notePosition = new NotePosition ();

        for (int row = 0; row < range; row++)
        {
            notePosition.setNote (lowerRowWithData + row);
            gc.fillRectangle (left, top + (range - row - 1) * stepHeight, width, 1, dividersColor);

            for (int step = 0; step < numSteps; step++)
            {
                notePosition.setStep (step);

                // Get step, check for length
                for (int channel = 0; channel < 16; channel++)
                {
                    notePosition.setChannel (channel);

                    final IStepInfo stepInfo = this.clip.getStep (notePosition);
                    final StepState stepState = stepInfo.getState ();
                    if (stepState == StepState.OFF)
                        continue;

                    double x = left + step * stepWidth - 1;
                    double w = stepWidth + 2;
                    final boolean isStart = stepState == StepState.START;
                    if (isStart)
                    {
                        x += 2;
                        w -= 2;
                    }

                    gc.strokeRectangle (x, top + (range - row - 1) * stepHeight + 2, w, stepHeight - 2, noteBorderColor);
                    gc.fillRectangle (x + (isStart ? 0 : -2), top + (range - row - 1) * stepHeight + 2, w - 1 + (isStart ? 0 : 2), stepHeight - 3, stepInfo.isMuted () ? noteMutedColor : noteColor);

                    if (isStart && fontSize > 0)
                    {
                        final String text = channel + 1 + ": " + Scales.formatDrumNote (notePosition.getNote ());
                        final ColorEx textColor = ColorEx.calcContrastColor (noteColor);
                        gc.drawTextInBounds (text, x, top + (range - row - 1) * stepHeight + 2, w - 1, stepHeight - 3, Align.CENTER, textColor, fontSize);
                    }
                }
            }
        }

        // Draw the play cursor
        final int playStep = this.clip.getCurrentStep ();
        if (playStep >= 0)
            gc.fillRectangle (left + playStep * stepWidth - 1, 0, 3, height, measureTextColor);
    }
}
