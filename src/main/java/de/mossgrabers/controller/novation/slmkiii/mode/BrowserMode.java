// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode;

import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for browsing.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserMode extends BaseMode<IItem>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BrowserMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final boolean isInc = this.model.getValueChanger ().isIncrease (value);

        final IBrowser browser = this.model.getBrowser ();
        if (browser == null)
            return;

        switch (index)
        {
            case 0:
            case 1:
                if (isInc)
                    browser.nextContentType ();
                else
                    browser.previousContentType ();
                break;
            case 2:
            case 3:
                if (isInc)
                    browser.selectNextFilterColumn ();
                else
                    browser.selectPreviousFilterColumn ();
                break;
            case 4:
            case 5:
                if (isInc)
                    browser.getSelectedFilterColumn ().selectNextItem ();
                else
                    browser.getSelectedFilterColumn ().selectPreviousItem ();
                break;
            case 6:
            case 7:
                if (isInc)
                    browser.selectNextResult ();
                else
                    browser.selectPreviousResult ();
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (browser == null)
            return;

        switch (index)
        {
            case 0:
                browser.previousContentType ();
                break;
            case 1:
                browser.nextContentType ();
                break;
            case 2:
                browser.selectPreviousFilterColumn ();
                break;
            case 3:
                browser.selectNextFilterColumn ();
                break;
            case 4:
                browser.getSelectedFilterColumn ().selectPreviousItem ();
                break;
            case 5:
                browser.getSelectedFilterColumn ().selectNextItem ();
                break;
            case 6:
                browser.selectPreviousResult ();
                break;
            case 7:
                browser.selectNextResult ();
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case ROW1_1:
            case ROW1_2:
                return SLMkIIIColorManager.SLMKIII_GREEN;

            case ROW1_3:
            case ROW1_4:
                return SLMkIIIColorManager.SLMKIII_DARK_GREEN_HALF;

            case ROW1_5:
            case ROW1_6:
                return SLMkIIIColorManager.SLMKIII_GREEN_LIGHT;

            case ROW1_7:
            case ROW1_8:
                return SLMkIIIColorManager.SLMKIII_GREEN_GRASS;

            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();

        d.setCell (0, 0, StringUtils.fixASCII (browser.getSelectedContentType ()));
        d.setPropertyColor (0, 0, SLMkIIIColorManager.SLMKIII_GREEN);
        d.setCell (3, 0, "<< Tab");
        d.setPropertyColor (0, 2, SLMkIIIColorManager.SLMKIII_GREEN);
        d.setPropertyValue (0, 1, 0);

        d.setPropertyColor (1, 0, SLMkIIIColorManager.SLMKIII_GREEN);
        d.setCell (3, 1, "Tab >>");
        d.setPropertyColor (1, 2, SLMkIIIColorManager.SLMKIII_GREEN);
        d.setPropertyValue (1, 1, 0);

        final IBrowserColumn selectedFilterColumn = browser.getSelectedFilterColumn ();
        d.setCell (0, 2, selectedFilterColumn.doesExist () ? StringUtils.fixASCII (selectedFilterColumn.getName ()) : "-");
        d.setPropertyColor (2, 0, SLMkIIIColorManager.SLMKIII_DARK_GREEN_HALF);
        d.setCell (3, 2, "<< Filter");
        d.setPropertyColor (2, 2, SLMkIIIColorManager.SLMKIII_DARK_GREEN_HALF);
        d.setPropertyValue (2, 1, 0);

        d.setPropertyColor (3, 0, SLMkIIIColorManager.SLMKIII_DARK_GREEN_HALF);
        d.setCell (3, 3, "Filter >>");
        d.setPropertyColor (3, 2, SLMkIIIColorManager.SLMKIII_DARK_GREEN_HALF);
        d.setPropertyValue (3, 1, 0);

        d.setCell (0, 4, selectedFilterColumn.doesExist () ? StringUtils.fixASCII (selectedFilterColumn.getCursorName ()) : "-");
        d.setPropertyColor (4, 0, SLMkIIIColorManager.SLMKIII_GREEN_LIGHT);
        d.setCell (3, 4, "<< F-Sel");
        d.setPropertyColor (4, 2, SLMkIIIColorManager.SLMKIII_GREEN_LIGHT);
        d.setPropertyValue (4, 1, 0);

        d.setPropertyColor (5, 0, SLMkIIIColorManager.SLMKIII_GREEN_LIGHT);
        d.setCell (3, 5, "F-Sel >>");
        d.setPropertyColor (5, 2, SLMkIIIColorManager.SLMKIII_GREEN_LIGHT);
        d.setPropertyValue (5, 1, 0);

        final String resultName = StringUtils.pad (StringUtils.fixASCII (browser.getSelectedResult ()), 18, ' ');
        final String name1 = resultName.substring (0, 9);
        final String name2 = resultName.substring (9, 18);

        d.setCell (0, 6, name1);
        d.setPropertyColor (6, 0, SLMkIIIColorManager.SLMKIII_GREEN_GRASS);
        d.setCell (3, 6, "<< Result");
        d.setPropertyColor (6, 2, SLMkIIIColorManager.SLMKIII_GREEN_GRASS);
        d.setPropertyValue (6, 1, 0);

        d.setCell (0, 7, name2);
        d.setPropertyColor (7, 0, SLMkIIIColorManager.SLMKIII_GREEN_GRASS);
        d.setCell (3, 7, "Result >>");
        d.setPropertyColor (7, 2, SLMkIIIColorManager.SLMKIII_GREEN_GRASS);
        d.setPropertyValue (7, 1, 0);

        d.setCell (0, 8, "Browser");

        this.setButtonInfo (d);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return SLMkIIIColorManager.SLMKIII_GREEN_GRASS;
    }
}