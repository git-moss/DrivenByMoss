// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.view;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;

import java.util.EnumMap;
import java.util.Map;


/**
 * The Browser view.
 *
 * @author Jürgen Moßgraber
 */
public class BrowserView extends AbstractView<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private static final Map<Views, Integer> VIEW_COMMANDS = new EnumMap<> (Views.class);
    static
    {
        VIEW_COMMANDS.put (Views.SESSION, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_SESSION));
        VIEW_COMMANDS.put (Views.DRUM, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_DRUM));
        VIEW_COMMANDS.put (Views.DUMMY1, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_SCALE_CHORDS));
        VIEW_COMMANDS.put (Views.DUMMY2, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_USER_CHORDS));
        VIEW_COMMANDS.put (Views.DUMMY3, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_CUSTOM_MODE0));
        VIEW_COMMANDS.put (Views.DUMMY4, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_CUSTOM_MODE1));
        VIEW_COMMANDS.put (Views.DUMMY5, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_CUSTOM_MODE2));
        VIEW_COMMANDS.put (Views.DUMMY6, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_CUSTOM_MODE3));
        VIEW_COMMANDS.put (Views.DEVICE, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_DEVICE_SELECT));
        VIEW_COMMANDS.put (Views.BROWSER, Integer.valueOf (LaunchkeyMk3ControlSurface.PAD_MODE_NAVIGATION));
    }


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public BrowserView (final LaunchkeyMk3ControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Provided by the hardware
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Provided by the hardware
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final IBrowser browser = this.model.getBrowser ();
        switch (buttonID)
        {
            case SCENE1:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI;

            case SCENE2:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_LO;

            case ARROW_UP:
                return browser.isActive () && browser.hasNextContentType () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_LO : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            case ARROW_DOWN:
                return browser.isActive () && browser.hasPreviousContentType () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_LO : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            default:
                // Not used
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.confirmBrowser (browser, true);
                break;

            case SCENE2:
                this.confirmBrowser (browser, false);
                break;

            case ARROW_UP:
                browser.nextContentType ();
                break;

            case ARROW_DOWN:
                browser.previousContentType ();
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Close the browser and the Launchkey navigation mode.
     *
     * @param browser The browser
     * @param commitSelection Commit or cancel?
     */
    protected void confirmBrowser (final IBrowser browser, final boolean commitSelection)
    {
        browser.stopBrowsing (commitSelection);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
        {
            // Browser is already opened, normally this means the user opened it in Bitwig.
            // Set the Navigation mode on the Launchkey
            this.surface.getMidiOutput ().sendCCEx (15, LaunchkeyMk3ControlSurface.LAUNCHKEY_VIEW_SELECT, LaunchkeyMk3ControlSurface.PAD_MODE_NAVIGATION);
        }
        else
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (this.surface.isShiftPressed () || !cursorDevice.doesExist ())
                browser.insertAfterCursorDevice ();
            else
                browser.replace (cursorDevice);
        }

        super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        // Close the browser if still open
        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
            browser.stopBrowsing (false);

        final ViewManager viewManager = this.surface.getViewManager ();
        final Views activeID = viewManager.getActiveID ();
        if (activeID == Views.BROWSER)
        {
            // Browse (...) button does not send an 'up' event
            this.surface.getButton (ButtonID.BROWSE).clearState ();

            final Views previousID = viewManager.getActiveIDIgnoreTemporary ();
            final Integer id = VIEW_COMMANDS.get (previousID);
            final int viewCommand = id == null || previousID == Views.BROWSER ? LaunchkeyMk3ControlSurface.PAD_MODE_SESSION : id.intValue ();
            this.surface.getMidiOutput ().sendCCEx (15, LaunchkeyMk3ControlSurface.LAUNCHKEY_VIEW_SELECT, viewCommand);
        }
    }
}