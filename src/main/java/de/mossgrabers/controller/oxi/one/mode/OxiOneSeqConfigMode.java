// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import java.util.Optional;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * The mode to configure the sequencer.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneSeqConfigMode extends AbstractParameterMode<OxiOneControlSurface, OxiOneConfiguration, IItem> implements IOxiModeReset
{
    private static final String [] MENU          =
    {
        "Rnge",
        "Page",
        "Reso",
        ""
    };

    private static final String [] SHIFTED_MENU  =
    {
        "",
        "",
        "",
        ""
    };

    private int                    selectedIndex = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneSeqConfigMode (final OxiOneControlSurface surface, final IModel model)
    {
        super ("Seq. Config", surface, model, false);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateSelectedIndex ();

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final String desc = "";
        String label = "";
        int value = -1;

        final Optional<INoteClip> clipOpt = this.getClip ();
        if (clipOpt.isPresent ())
        {
            final INoteClip clip = clipOpt.get ();

            final int upperBound = this.model.getValueChanger ().getUpperBound ();
            switch (this.selectedIndex)
            {
                case 0:
                    final Scales scales = this.model.getScales ();
                    label = scales.getDrumRangeText ();
                    value = (int) (scales.getDrumOffset () / (double) (Scales.DRUM_NOTE_UPPER - Scales.DRUM_NOTE_LOWER) * upperBound);
                    break;

                case 1:
                    label = "Page: " + (clip.getEditPage () + 1);
                    value = 0;
                    break;

                case 2:
                    final int resolutionIndex = this.getResolutionIndex (clip);
                    final Resolution resolution = Resolution.values ()[resolutionIndex];
                    label = "Resolution: " + resolution.getName ();
                    value = (int) (resolutionIndex / (double) (Resolution.values ().length - 1) * upperBound);
                    break;

                default:
                    // Not used
                    break;
            }

        }

        display.addElement (new TitleValueMenuComponent (desc, label, this.surface.isShiftPressed () ? SHIFTED_MENU : MENU, value, 0, 0, false));
        display.send ();
    }


    private void updateSelectedIndex ()
    {
        if (this.surface.isPressed (ButtonID.SHIFT))
        {
            if (this.selectedIndex < 4)
                this.selectedIndex += 4;
        }
        else
        {
            if (this.selectedIndex >= 4)
                this.selectedIndex -= 4;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.selectedIndex = this.surface.isPressed (ButtonID.SHIFT) ? index + 4 : index;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final boolean isInc = this.model.getValueChanger ().isIncrease (value);

        final Optional<INoteClip> clipOpt = this.getClip ();
        if (clipOpt.isEmpty ())
            return;

        final INoteClip clip = clipOpt.get ();
        switch (this.selectedIndex)
        {
            case 0:
                final IView activeView = this.surface.getViewManager ().getActive ();
                if (activeView instanceof final AbstractDrumView drumView)
                    drumView.changeOctave (ButtonEvent.DOWN, isInc, 8, true, false);
                else if (activeView instanceof final TransposeView transView)
                {
                    if (isInc)
                        transView.onOctaveUp (ButtonEvent.DOWN);
                    else
                        transView.onOctaveDown (ButtonEvent.DOWN);
                }
                break;

            case 1:
                if (isInc)
                    clip.scrollStepsPageForward ();
                else
                    clip.scrollStepsPageBackwards ();
                break;

            case 2:
                this.setResolutionIndex (clip, this.getResolutionIndex (clip) + (isInc ? 1 : -1));
                break;

            default:
                // Not used
                return;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue (final int index)
    {
        final Optional<INoteClip> clipOpt = this.getClip ();
        if (clipOpt.isEmpty ())
            return;

        final INoteClip clip = clipOpt.get ();

        switch (this.selectedIndex)
        {
            case 0:
                final IView activeView = this.surface.getViewManager ().getActive ();
                if (activeView instanceof final AbstractDrumView drumView)
                    drumView.resetOctave ();
                break;

            case 1:
                clip.scrollToPage (0);
                break;

            case 2:
                clip.setStepLength (Resolution.RES_1_16.getValue ());
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Set the resolution index.
     *
     * @param clip The clip for which to set the resolution
     *
     * @param selectedResolutionIndex The index 0-7
     */
    public void setResolutionIndex (final INoteClip clip, final int selectedResolutionIndex)
    {
        final int resolutionIndex = Math.min (Math.max (0, selectedResolutionIndex), 7);
        final Resolution resolution = Resolution.values ()[resolutionIndex];
        clip.setStepLength (resolution.getValue ());
    }


    /**
     * Get the resolution index.
     *
     * @param clip The clip for which to get the resolution
     *
     * @return The index 0-7
     */
    public int getResolutionIndex (final INoteClip clip)
    {
        return Resolution.getMatch (clip.getStepLength ());
    }


    private Optional<INoteClip> getClip ()
    {
        if (this.surface.getViewManager ().getActive () instanceof final AbstractSequencerView sequencer)
            return Optional.of (sequencer.getClip ());
        return Optional.empty ();
    }
}
