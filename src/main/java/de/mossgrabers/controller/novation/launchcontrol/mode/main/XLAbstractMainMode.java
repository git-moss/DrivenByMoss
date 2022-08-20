package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.IXLMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTemporaryButtonMode;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;


/**
 * Base mode for the main modes of the LaunchControl XL.
 *
 * @param <B> The type of the item bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class XLAbstractMainMode<B extends IItem> extends AbstractMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, B> implements IXLMode
{
    protected Modes   defaultMode = Modes.MUTE;

    private Modes     trackMode;
    protected boolean wasLong     = false;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param bank The parameter bank to control with this mode, might be null
     * @param controls The IDs of the knobs or faders to control this mode
     */
    protected XLAbstractMainMode (final String name, final LaunchControlXLControlSurface surface, final IModel model, final IBank<B> bank, final List<ContinuousID> controls)
    {
        super (name, surface, model, true, bank, controls);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 0)
            this.handleRow0 (index, event);
        else if (row == 2)
            this.handleRow2 (index, event);
    }


    /**
     * Handle the main row.
     *
     * @param index The index of the button
     * @param event The button event
     */
    protected abstract void handleRow0 (final int index, final ButtonEvent event);


    /**
     * Handle the 'track mode' row.
     *
     * @param index The index of the button
     * @param event The button event
     */
    protected abstract void handleRow2 (final int index, final ButtonEvent event);


    protected void alternativeModeSelect (final ButtonEvent event, final Modes modeID, final Modes altModeID)
    {
        final ModeManager trackModeManager = this.surface.getTrackButtonModeManager ();

        if (event == ButtonEvent.DOWN)
        {
            trackModeManager.setTemporary (altModeID);
            return;
        }

        if (event == ButtonEvent.UP)
        {
            trackModeManager.restore ();

            if (((XLTemporaryButtonMode) trackModeManager.get (altModeID)).hasBeenUsed ())
                return;

            if (modeID != null)
                trackModeManager.setActive (modeID);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.surface.getTrackButtonModeManager ().setActive (this.trackMode == null ? this.defaultMode : this.trackMode);

        super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.trackMode = this.surface.getTrackButtonModeManager ().getActiveID ();

        super.onDeactivate ();
    }
}
