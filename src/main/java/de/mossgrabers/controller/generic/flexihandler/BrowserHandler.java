// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;


/**
 * The handler for browser commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserHandler extends AbstractHandler
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param relative2ValueChanger The relative value changer variant 2
     * @param relative3ValueChanger The relative value changer variant 3
     */
    public BrowserHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger relative2ValueChanger, final IValueChanger relative3ValueChanger)
    {
        super (model, surface, configuration, relative2ValueChanger, relative3ValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.BROWSER_BROWSE_PRESETS,
            FlexiCommand.BROWSER_INSERT_DEVICE_BEFORE_CURRENT,
            FlexiCommand.BROWSER_INSERT_DEVICE_AFTER_CURRENT,
            FlexiCommand.BROWSER_COMMIT_SELECTION,
            FlexiCommand.BROWSER_CANCEL_SELECTION,
            FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_1,
            FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_2,
            FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_3,
            FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_4,
            FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_5,
            FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_6,
            FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_7,
            FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_8,
            FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1,
            FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_2,
            FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_3,
            FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_4,
            FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_5,
            FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_6,
            FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_7,
            FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_8,
            FlexiCommand.BROWSER_SCROLL_FILTER_IN_COLUMN_1,
            FlexiCommand.BROWSER_SCROLL_FILTER_IN_COLUMN_2,
            FlexiCommand.BROWSER_SCROLL_FILTER_IN_COLUMN_3,
            FlexiCommand.BROWSER_SCROLL_FILTER_IN_COLUMN_4,
            FlexiCommand.BROWSER_SCROLL_FILTER_IN_COLUMN_5,
            FlexiCommand.BROWSER_SCROLL_FILTER_IN_COLUMN_6,
            FlexiCommand.BROWSER_SCROLL_FILTER_IN_COLUMN_7,
            FlexiCommand.BROWSER_SCROLL_FILTER_IN_COLUMN_8,
            FlexiCommand.BROWSER_RESET_FILTER_COLUMN_1,
            FlexiCommand.BROWSER_RESET_FILTER_COLUMN_2,
            FlexiCommand.BROWSER_RESET_FILTER_COLUMN_3,
            FlexiCommand.BROWSER_RESET_FILTER_COLUMN_4,
            FlexiCommand.BROWSER_RESET_FILTER_COLUMN_5,
            FlexiCommand.BROWSER_RESET_FILTER_COLUMN_6,
            FlexiCommand.BROWSER_RESET_FILTER_COLUMN_7,
            FlexiCommand.BROWSER_RESET_FILTER_COLUMN_8,
            FlexiCommand.BROWSER_SELECT_THE_PREVIOUS_PRESET,
            FlexiCommand.BROWSER_SELECT_THE_NEXT_PRESET,
            FlexiCommand.BROWSER_SCROLL_PRESETS,
            FlexiCommand.BROWSER_SELECT_THE_PREVIOUS_TAB,
            FlexiCommand.BROWSER_SELECT_THE_NEXT_TAB,
            FlexiCommand.BROWSER_SCROLL_TABS
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final int knobMode, final int value)
    {
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            // Browser: Browse Presets
            case BROWSER_BROWSE_PRESETS:
                if (isButtonPressed)
                    this.model.getBrowser ().replace (this.model.getCursorDevice ());
                break;
            // Browser: Insert Device before current
            case BROWSER_INSERT_DEVICE_BEFORE_CURRENT:
                if (isButtonPressed)
                    this.model.getBrowser ().insertBeforeCursorDevice ();
                break;
            // Browser: Insert Device after current
            case BROWSER_INSERT_DEVICE_AFTER_CURRENT:
                if (isButtonPressed)
                    this.model.getBrowser ().insertAfterCursorDevice ();
                break;
            // Browser: Commit Selection
            case BROWSER_COMMIT_SELECTION:
                if (isButtonPressed)
                    this.model.getBrowser ().stopBrowsing (true);
                break;
            // Browser: Cancel Selection
            case BROWSER_CANCEL_SELECTION:
                if (isButtonPressed)
                    this.model.getBrowser ().stopBrowsing (false);
                break;

            // Browser: Select Previous Filter in Column 1-6
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_1:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_2:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_3:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_4:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_5:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_6:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_7:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_8:
                if (isButtonPressed)
                    this.model.getBrowser ().selectPreviousFilterItem (command.ordinal () - FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_1.ordinal ());
                break;

            // Browser: Select Next Filter in Column 1-6
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_2:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_3:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_4:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_5:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_6:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_7:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_8:
                if (isButtonPressed)
                    this.model.getBrowser ().selectNextFilterItem (command.ordinal () - FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1.ordinal ());
                break;

            case BROWSER_SCROLL_FILTER_IN_COLUMN_1:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_2:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_3:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_4:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_5:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_6:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_7:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_8:
                this.scrollFilterColumn (knobMode, command.ordinal () - FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1.ordinal (), value);
                break;

            // Browser: Reset Filter Column 1-6
            case BROWSER_RESET_FILTER_COLUMN_1:
            case BROWSER_RESET_FILTER_COLUMN_2:
            case BROWSER_RESET_FILTER_COLUMN_3:
            case BROWSER_RESET_FILTER_COLUMN_4:
            case BROWSER_RESET_FILTER_COLUMN_5:
            case BROWSER_RESET_FILTER_COLUMN_6:
            case BROWSER_RESET_FILTER_COLUMN_7:
            case BROWSER_RESET_FILTER_COLUMN_8:
                if (isButtonPressed)
                    this.model.getBrowser ().resetFilterColumn (command.ordinal () - FlexiCommand.BROWSER_RESET_FILTER_COLUMN_1.ordinal ());
                break;

            // Browser: Select the previous preset
            case BROWSER_SELECT_THE_PREVIOUS_PRESET:
                if (isButtonPressed)
                    this.model.getBrowser ().selectPreviousResult ();
                break;
            // Browser: Select the next preset
            case BROWSER_SELECT_THE_NEXT_PRESET:
                if (isButtonPressed)
                    this.model.getBrowser ().selectNextResult ();
                break;
            case BROWSER_SCROLL_PRESETS:
                this.scrollPresetColumn (knobMode, value);
                break;
            // Browser: Select the previous tab
            case BROWSER_SELECT_THE_PREVIOUS_TAB:
                if (isButtonPressed)
                    this.model.getBrowser ().previousContentType ();
                break;
            // Browser: Select the next tab"
            case BROWSER_SELECT_THE_NEXT_TAB:
                if (isButtonPressed)
                    this.model.getBrowser ().nextContentType ();
                break;
            case BROWSER_SCROLL_TABS:
                this.scrollBrowserTabs (knobMode, value);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void scrollFilterColumn (final int knobMode, final int filterColumn, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            browser.selectNextFilterItem (filterColumn);
        else
            browser.selectPreviousFilterItem (filterColumn);
    }


    private void scrollPresetColumn (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            browser.selectNextResult ();
        else
            browser.selectPreviousResult ();
    }


    private void scrollBrowserTabs (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            browser.nextContentType ();
        else
            browser.previousContentType ();
    }
}
