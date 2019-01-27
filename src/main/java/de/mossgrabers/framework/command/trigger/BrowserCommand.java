// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract command to open the browser.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private static final int NUMBER_OF_RETRIES = 20;

    protected Integer        browserMode;
    protected int            startRetries;


    /**
     * Constructor.
     *
     * @param browserMode The ID of the mode to activate for browsing
     * @param model The model
     * @param surface The surface
     */
    public BrowserCommand (final Integer browserMode, final IModel model, final S surface)
    {
        super (model, surface);

        this.browserMode = browserMode;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.startBrowser (this.surface.isSelectPressed (), false);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.startBrowser (true, true);
    }


    /**
     * Start a browser.
     *
     * @param insertDevice Insert a device if true otherwise select preset
     * @param beforeCurrent Insert the device before the current device if any
     */
    public void startBrowser (final boolean insertDevice, final boolean beforeCurrent)
    {
        final IBrowser browser = this.model.getBrowser ();

        // Patch Browser already active?
        if (browser.isActive ())
        {
            this.discardBrowser (this.getCommit ());
            return;
        }

        if (!insertDevice && this.model.getCursorDevice ().doesExist ())
            browser.browseForPresets ();
        else
        {
            if (beforeCurrent)
                browser.browseToInsertBeforeDevice ();
            else
                browser.browseToInsertAfterDevice ();
        }

        this.startRetries = 0;
        this.activateMode ();
    }


    /**
     * Tries to activate the mode 20 times.
     */
    protected void activateMode ()
    {
        if (this.model.getBrowser ().isActive ())
            this.surface.getModeManager ().setActiveMode (this.browserMode);
        else if (this.startRetries < NUMBER_OF_RETRIES)
        {
            this.startRetries++;
            this.surface.scheduleTask (this::activateMode, 200);
        }
    }


    /**
     * Stop browsing and restore the previous mode.
     *
     * @param commit True to commit otherwise cancel
     */
    protected void discardBrowser (final boolean commit)
    {
        this.model.getBrowser ().stopBrowsing (commit);
        this.surface.getModeManager ().restoreMode ();
    }


    /**
     * Commit or cancel browsing? Default implementation cancels if combined with Shift.
     *
     * @return True to commit otherwise cancel
     */
    protected boolean getCommit ()
    {
        return !this.surface.isShiftPressed ();
    }
}
