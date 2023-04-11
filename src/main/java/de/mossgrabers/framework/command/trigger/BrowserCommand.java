// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;

import java.util.Optional;


/**
 * Abstract command to open the browser.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class BrowserCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final ButtonID firstTrigger;
    private final ButtonID secondTrigger;


    /**
     * Constructor. Uses SHIFT as the first trigger and SELECT as the second.
     *
     * @param model The model
     * @param surface The surface
     */
    public BrowserCommand (final IModel model, final S surface)
    {
        this (model, surface, ButtonID.SHIFT, ButtonID.SELECT);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param firstTrigger If this button is pressed when the command is executed a new device is
     *            inserted before the current one
     * @param secondTrigger If this button is pressed when the command is executed a new device is
     *            inserted after the current one
     */
    public BrowserCommand (final IModel model, final S surface, final ButtonID firstTrigger, final ButtonID secondTrigger)
    {
        super (model, surface);

        this.firstTrigger = firstTrigger;
        this.secondTrigger = secondTrigger;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.surface.isPressed (this.firstTrigger))
            this.startBrowser (true, true);
        else
            this.startBrowser (this.secondTrigger == null || this.surface.isPressed (this.secondTrigger), false);
    }


    /**
     * Start a browser.
     *
     * @param insertDevice Insert a device if true otherwise select preset
     * @param beforeCurrent Insert the device before the current device if any
     */
    public void startBrowser (final boolean insertDevice, final boolean beforeCurrent)
    {
        // Patch Browser already active?
        if (this.model.getBrowser ().isActive ())
        {
            this.discardBrowser (this.getCommit ());
            return;
        }

        this.activateBrowser (insertDevice, beforeCurrent);
    }


    /**
     * Activate the browser depending on the parameters and the currently active mode.
     *
     * @param insertDevice Insert a device if true otherwise select preset
     * @param beforeCurrent Insert the device before the current device if any
     * @return True if activated
     */
    private boolean activateBrowser (final boolean insertDevice, final boolean beforeCurrent)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean hasCursorDevice = cursorDevice.doesExist ();

        final IBrowser browser = this.model.getBrowser ();

        if (hasCursorDevice)
        {
            // Replace the cursor device
            if (!insertDevice)
            {
                browser.replace (cursorDevice);
                return true;
            }

            // Add device to layer or track

            if (Modes.isLayerMode (this.surface.getModeManager ().getActiveID ()))
            {
                final Optional<ILayer> layer = cursorDevice.getLayerBank ().getSelectedItem ();
                if (layer.isEmpty ())
                    return false;
                browser.addDevice (layer.get ());
                return true;
            }

            if (beforeCurrent)
                browser.insertBeforeCursorDevice ();
            else
                browser.insertAfterCursorDevice ();
            return true;
        }

        // No cursor device, add to the selected channel, if any
        final Optional<ITrack> channel = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (channel.isPresent ())
        {
            browser.addDevice (channel.get ());
            return true;
        }

        final IMasterTrack master = this.model.getMasterTrack ();
        if (!master.isSelected ())
            return false;
        browser.addDevice (master);
        return true;
    }


    /**
     * Stop browsing and restore the previous mode.
     *
     * @param commit True to commit otherwise cancel
     */
    public void discardBrowser (final boolean commit)
    {
        this.model.getBrowser ().stopBrowsing (commit);

        if (!commit)
            return;

        // Workaround for drum page scroll bug
        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActive (Views.DRUM))
            AbstractDrumView.class.cast (viewManager.get (Views.DRUM)).repositionBankPage ();
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
