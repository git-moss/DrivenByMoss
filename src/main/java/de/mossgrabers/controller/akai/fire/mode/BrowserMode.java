// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Locale;


/**
 * Mode for navigating the browser.
 *
 * @author Jürgen Moßgraber
 */
public class BrowserMode extends AbstractParameterMode<FireControlSurface, FireConfiguration, IItem>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BrowserMode (final FireControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model, false);

        this.initTouchedStates (9);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        for (int i = 0; i < 4; i++)
            ((IHwRelativeKnob) this.surface.getContinuous (ContinuousID.get (ContinuousID.KNOB1, i))).setSensitivity (1);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.model.getBrowser ().stopBrowsing (true);

        final int knobSensitivityDefault = this.surface.getConfiguration ().getKnobSensitivityDefault ();
        for (int i = 0; i < 4; i++)
            ((IHwRelativeKnob) this.surface.getContinuous (ContinuousID.get (ContinuousID.KNOB1, i))).setSensitivity (knobSensitivityDefault);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final int idx = this.getKnobIndex (index);
        int speed = this.model.getValueChanger ().calcSteppedKnobChange (value);

        if (index == 8 && this.surface.isPressed (ButtonID.SELECT))
        {
            this.surface.getButton (ButtonID.SELECT).setConsumed ();
            speed = speed * 4;
        }

        if (speed > 0)
            this.selectNext (idx, speed);
        else
            this.selectPrevious (idx, -speed);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final int idx = this.getKnobIndex (index);

        // Make sure that only 1 knob gets changed in browse mode to prevent weird behavior. Also
        // ignore the 8th element which does not have touch
        for (int i = 0; i < 8; i++)
            if (this.isKnobTouched (i) && i != idx)
                return;

        this.setTouchedKnob (idx, isTouched);
    }


    private int getKnobIndex (final int index)
    {
        int idx = 8;
        if (index < 8)
            idx = this.surface.isPressed (ButtonID.ALT) ? 4 + index : index;
        return idx;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        final String [] rows = new String [3];
        final boolean [] sels = new boolean []
        {
            false,
            false,
            false
        };

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();
        final int idx = this.getTouchedKnob ();

        if (idx < 0)
        {
            final String selectedResult = browser.getSelectedResult ();
            rows[0] = browser.getSelectedContentType ().toUpperCase (Locale.US);
            rows[1] = "Selection: ";
            rows[2] = selectedResult == null || selectedResult.isBlank () ? "None" : selectedResult;
        }
        else if (idx == 8)
        {
            final IBrowserColumnItem [] results = browser.getResultColumnItems ();
            if (results[0].doesExist ())
            {
                for (int item = 0; item < 3; item++)
                {
                    rows[item] = item < results.length ? results[item].getName () : "";
                    sels[item] = item < results.length && results[item].isSelected ();
                }
            }
            else
            {
                rows[0] = "No";
                rows[1] = "results ";
                rows[2] = "available...";
            }
        }
        else
        {
            final IBrowserColumn filterColumn = browser.getFilterColumn (idx);
            if (filterColumn.doesExist () && filterColumn.doesCursorExist ())
            {
                final IBrowserColumnItem [] item = filterColumn.getItems ();
                for (int itemIndex = 0; itemIndex < 3; itemIndex++)
                {
                    final int pos = itemIndex;
                    String text = StringUtils.optimizeName (item[pos].getName (), 10);
                    if (!text.isEmpty ())
                        text = text + " (" + item[pos].getHitCount () + ")";
                    rows[itemIndex] = text;
                    sels[itemIndex] = item[pos].isSelected ();
                }
            }
            else
            {
                rows[0] = "";
                rows[1] = "";
                rows[2] = "";
            }
        }

        display.addListElement (rows, sels);
        display.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.model.getBrowser ().previousContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.model.getBrowser ().nextContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.model.getBrowser ().hasPreviousContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.model.getBrowser ().hasNextContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return this.hasPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return this.hasNextItem ();
    }


    private void selectNext (final int index, final int count)
    {
        final IBrowser browser = this.model.getBrowser ();
        final int filterColumnCount = Math.min (browser.getFilterColumnCount (), 7);
        if (index < filterColumnCount)
        {
            final IBrowserColumn fc = browser.getFilterColumn (index);
            if (fc != null && fc.doesExist ())
            {
                final int fi = fc.getIndex ();
                if (fi < 0)
                    return;
                for (int i = 0; i < count; i++)
                    browser.selectNextFilterItem (fi);
            }
        }
        else
        {
            for (int i = 0; i < count; i++)
                browser.selectNextResult ();
        }
    }


    private void selectPrevious (final int index, final int count)
    {
        final IBrowser browser = this.model.getBrowser ();
        final int filterColumnCount = Math.min (browser.getFilterColumnCount (), 7);
        if (index < filterColumnCount)
        {
            final IBrowserColumn fc = browser.getFilterColumn (index);
            if (fc != null && fc.doesExist ())
            {
                final int fi = fc.getIndex ();
                if (fi < 0)
                    return;
                for (int j = 0; j < count; j++)
                    browser.selectPreviousFilterItem (fi);
            }
        }
        else
        {
            for (int j = 0; j < count; j++)
                browser.selectPreviousResult ();
        }
    }
}