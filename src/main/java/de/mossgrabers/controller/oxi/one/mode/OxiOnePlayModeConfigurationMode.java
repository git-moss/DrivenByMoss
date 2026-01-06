// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.ScaleLayout;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TransposeView;


/**
 * The configuration mode to change the scale, layout, octave offset and the root note.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOnePlayModeConfigurationMode extends AbstractMode<OxiOneControlSurface, OxiOneConfiguration> implements IOxiModeReset
{
    private static final String [] MENU          =
    {
        "Octv",
        "Scle",
        "Base",
        "Chro"
    };

    private static final String [] SHIFTED_MENU  =
    {
        "Layt",
        "",
        "",
        ""
    };

    private int                    selectedIndex = 0;
    private final Scales           scales;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOnePlayModeConfigurationMode (final OxiOneControlSurface surface, final IModel model)
    {
        super ("PlayConfiguration", surface, model, false);

        this.scales = this.model.getScales ();
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

        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        switch (this.selectedIndex)
        {
            case 0:
                final int octave = this.scales.getOctave ();
                label = "Octave: " + octave;
                value = (octave + Scales.OCTAVE_RANGE) / (Scales.OCTAVE_RANGE * 2);
                break;

            case 1:
                final Scale scale = this.scales.getScale ();
                label = "Scale: " + scale.getName ();
                value = (int) (scale.ordinal () / (double) (Scale.values ().length - 1) * upperBound);
                break;

            case 2:
                final int scaleOffsetIndex = this.scales.getScaleOffsetIndex ();
                label = "Base Note: " + Scales.BASES.get (scaleOffsetIndex);
                value = (int) (scaleOffsetIndex / (double) (Scales.BASES.size () - 1) * upperBound);
                break;

            case 3:
                final boolean chromatic = this.scales.isChromatic ();
                label = chromatic ? "Chromatic" : "In-Key";
                value = chromatic ? upperBound : 0;
                break;

            case 4:
                final ScaleLayout scaleLayout = this.scales.getScaleLayout ();
                label = "Layout: " + scaleLayout.getName ();
                value = (int) (scaleLayout.ordinal () / (double) (ScaleLayout.values ().length - 1) * upperBound);
                break;

            default:
                // Not used
                break;
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

        switch (this.selectedIndex)
        {
            case 0:
                final IView activeView = this.surface.getViewManager ().getActive ();
                if (activeView instanceof final TransposeView transposeView)
                {
                    if (isInc)
                        transposeView.onOctaveUp (ButtonEvent.DOWN);
                    else
                        transposeView.onOctaveDown (ButtonEvent.DOWN);
                }
                else
                {
                    if (isInc)
                        this.scales.incOctave ();
                    else
                        this.scales.decOctave ();
                }
                break;

            case 1:
                this.scales.changeScale (value);
                break;

            case 2:
                this.scales.setScaleOffsetByIndex (this.scales.getScaleOffsetIndex () + (isInc ? 1 : -1));
                break;

            case 3:
                this.scales.setChromatic (isInc);
                break;

            case 4:
                if (isInc)
                    this.scales.nextScaleLayout ();
                else
                    this.scales.prevScaleLayout ();
                break;

            default:
                // Not used
                return;
        }

        this.updateScaleProperties ();
    }


    protected void updateScaleProperties ()
    {
        this.scales.updateScaleProperties (this.surface.getConfiguration ());
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue (final int index)
    {
        switch (this.selectedIndex)
        {
            case 0:
                final IView activeView = this.surface.getViewManager ().getActive ();
                if (activeView instanceof final TransposeView transposeView)
                    transposeView.resetOctave ();
                else
                    this.scales.setOctave (2);
                break;

            case 1:
                this.scales.setScale (Scale.MAJOR);
                break;

            case 2:
                this.scales.setScaleOffsetByIndex (0);
                break;

            case 3:
                this.scales.setChromatic (false);
                break;

            case 4:
                this.scales.setScaleLayout (ScaleLayout.FOURTH_UP);
                break;

            default:
                // Not used
                break;
        }
    }
}
