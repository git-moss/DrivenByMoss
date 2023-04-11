// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * Navigate the browser.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class BrowserView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C>
{
    /** The color for the unused pads. */
    public static final String     OFF          = "BROWSER_OFF";
    /** The color for the discard pad. */
    public static final String     DISCARD      = "BROWSER_DISCARD";
    /** The color for the confirmation pad. */
    public static final String     CONFIRM      = "BROWSER_CONFIRM";
    /** The color for the play (test) pads. */
    public static final String     PLAY         = "BROWSER_PLAY";
    /** The color for the first column. */
    public static final String     COLUMN1      = "BROWSER_COLUMN1";
    /** The color for the second column. */
    public static final String     COLUMN2      = "BROWSER_COLUMN2";
    /** The color for the third column. */
    public static final String     COLUMN3      = "BROWSER_COLUMN3";
    /** The color for the fourth column. */
    public static final String     COLUMN4      = "BROWSER_COLUMN4";
    /** The color for the fifth column. */
    public static final String     COLUMN5      = "BROWSER_COLUMN5";
    /** The color for the sixth column. */
    public static final String     COLUMN6      = "BROWSER_COLUMN6";
    /** The color for the seventh column. */
    public static final String     COLUMN7      = "BROWSER_COLUMN7";
    /** The color for the eighth column. */
    public static final String     COLUMN8      = "BROWSER_COLUMN8";

    private static final String [] COLUMNS      =
    {
        COLUMN1,
        COLUMN2,
        COLUMN3,
        COLUMN4,
        COLUMN5,
        COLUMN6,
        COLUMN7,
        COLUMN8
    };

    private static final int []    COLUMN_ORDER =
    {
        0,
        1,
        2,
        3,
        4,
        5
    };


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public BrowserView (final S surface, final IModel model)
    {
        super (Views.NAME_BROWSER, surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        padGrid.light (36, DISCARD);

        padGrid.light (37, OFF);

        for (int i = 38; i < 42; i++)
            padGrid.light (i, PLAY);

        padGrid.light (42, OFF);

        padGrid.light (43, CONFIRM);

        for (int i = 44; i < 52; i++)
            padGrid.light (i, OFF);

        for (int i = 52; i < 60; i++)
            padGrid.light (i, COLUMNS[i - 52]);
        for (int i = 60; i < 68; i++)
            padGrid.light (i, COLUMNS[i - 60]);
        for (int i = 68; i < 76; i++)
            padGrid.light (i, COLUMNS[i - 68]);
        for (int i = 76; i < 84; i++)
            padGrid.light (i, COLUMNS[i - 76]);

        for (int i = 84; i < 100; i++)
            padGrid.light (i, OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        switch (note)
        {
            // Cancel
            case 36:
                if (velocity == 0)
                    return;
                browser.stopBrowsing (false);
                this.surface.getViewManager ().restore ();
                break;

            // OK
            case 43:
                if (velocity == 0)
                    return;
                browser.stopBrowsing (true);
                this.surface.getViewManager ().restore ();
                break;

            case 38:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 48, velocity);
                break;
            case 39:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 60, velocity);
                break;
            case 40:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 72, velocity);
                break;
            case 41:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 84, velocity);
                break;

            default:
                // Not used
                break;
        }

        if (velocity == 0)
            return;

        if (note >= 52 && note < 84)
        {
            final int n = note - 52;
            final int row = n / 8;
            final int col = n % 8;

            switch (col)
            {
                case 6:
                    return;

                case 7:
                    if (row == 0)
                        browser.selectNextResult ();
                    else if (row == 1)
                    {
                        for (int i = 0; i < 8; i++)
                            browser.selectNextResult ();
                    }
                    else if (row == 2)
                    {
                        for (int i = 0; i < 8; i++)
                            browser.selectPreviousResult ();
                    }
                    else if (row == 3)
                        browser.selectPreviousResult ();
                    break;

                default:
                    if (row == 0)
                        browser.selectNextFilterItem (BrowserView.COLUMN_ORDER[col]);
                    else if (row == 1)
                    {
                        for (int i = 0; i < 8; i++)
                            browser.selectNextFilterItem (BrowserView.COLUMN_ORDER[col]);
                    }
                    else if (row == 2)
                    {
                        for (int i = 0; i < 8; i++)
                            browser.selectPreviousFilterItem (BrowserView.COLUMN_ORDER[col]);
                    }
                    else if (row == 3)
                        browser.selectPreviousFilterItem (BrowserView.COLUMN_ORDER[col]);
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        return ColorManager.BUTTON_STATE_OFF;
    }
}