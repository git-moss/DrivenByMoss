package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.Views;


/**
 * Helper class for activating the browser mode. Retries several times until the browser is active.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserActivator<S extends IControlSurface<C>, C extends Configuration>
{
    private static final int NUMBER_OF_RETRIES = 20;

    private final Modes      browserMode;
    private final Views      browserView;
    private final IModel     model;
    private final S          surface;

    private int              startRetries;


    /**
     * Constructor.
     *
     * @param browserMode The ID of the mode to activate for browsing
     * @param model The model
     * @param surface The surface
     */
    public BrowserActivator (final Modes browserMode, final IModel model, final S surface)
    {
        this.model = model;
        this.surface = surface;
        this.browserMode = browserMode;
        this.browserView = null;
    }


    /**
     * Constructor.
     *
     * @param browserView The ID of the view to activate for browsing
     * @param model The model
     * @param surface The surface
     */
    public BrowserActivator (final Views browserView, final IModel model, final S surface)
    {
        this.model = model;
        this.surface = surface;
        this.browserMode = null;
        this.browserView = browserView;
    }


    /**
     * Activate the browser mode or view. Waits till the browser is active. Retries several times.
     */
    public void activate ()
    {
        this.startRetries = 0;
        this.activateInternal ();
    }


    /**
     * Tries to activate the mode or view several times.
     */
    protected void activateInternal ()
    {
        if (this.model.getBrowser ().isActive ())
        {
            if (this.browserMode != null)
                this.surface.getModeManager ().setActiveMode (this.browserMode);
            else
                this.surface.getViewManager ().setActiveView (this.browserView);
        }
        else if (this.startRetries < NUMBER_OF_RETRIES)
        {
            this.startRetries++;
            this.surface.scheduleTask (this::activateInternal, 200);
        }
    }
}
