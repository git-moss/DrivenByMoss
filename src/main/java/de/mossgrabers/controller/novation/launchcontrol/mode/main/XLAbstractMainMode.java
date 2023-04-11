package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.IXLMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTemporaryButtonMode;
import de.mossgrabers.framework.command.TempoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;

import java.util.List;


/**
 * Base mode for the main modes of the LaunchControl XL.
 *
 * @param <B> The type of the item bank
 *
 * @author Jürgen Moßgraber
 */
public abstract class XLAbstractMainMode<B extends IItem> extends AbstractParameterMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, B> implements IXLMode
{
    protected final LaunchControlXLConfiguration                                            configuration;
    protected Modes                                                                         defaultMode   = Modes.MUTE;
    protected Modes                                                                         trackMode;

    private IParameterProvider                                                              parameterProvider;
    private IParameterProvider                                                              parameterProviderWithDeviceParams;

    private boolean                                                                         wasLong       = false;
    private boolean                                                                         transportUsed = false;

    private final NewCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration>   newCommand;
    private final TempoCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> tempoDown;
    private final TempoCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> tempoUp;


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

        this.configuration = this.surface.getConfiguration ();

        this.newCommand = new NewCommand<> (model, surface);
        this.tempoDown = new TempoCommand<> (false, model, surface);
        this.tempoUp = new TempoCommand<> (true, model, surface);
    }


    /**
     * Initialize the parameter providers.
     *
     * @param parameterProvider The default parameter provider for the knobs
     * @param parameterProviderWithDeviceParams As above but with the 3rd row as parameter control
     */
    protected void setParameterProviders (final IParameterProvider parameterProvider, final IParameterProvider parameterProviderWithDeviceParams)
    {
        this.parameterProvider = parameterProvider;
        this.parameterProviderWithDeviceParams = parameterProviderWithDeviceParams;
        this.setParameterProvider (parameterProvider);
    }


    /**
     * Toggle between panorama and device parameters control on 3rd row.
     */
    public void toggleDeviceActive ()
    {
        this.configuration.toggleDeviceActive ();

        this.setParameterProvider (this.configuration.isDeviceActive () ? this.parameterProviderWithDeviceParams : this.parameterProvider);
        this.bindControls ();
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
    protected void handleRow0 (final int index, final ButtonEvent event)
    {
        final ModeManager modeManager = this.surface.getFaderModeManager ();
        switch (event)
        {
            case DOWN:
                if (this.surface.isPressed (ButtonID.REC_ARM))
                {
                    this.transportUsed = true;
                    this.handleTransport (index, event);
                    return;
                }

                this.transportUsed = false;
                this.wasLong = false;
                modeManager.setTemporary (Modes.MASTER);
                break;

            case LONG:
                this.wasLong = true;
                break;

            case UP:
                if (this.transportUsed)
                {
                    this.handleTransport (index, event);
                    return;
                }

                modeManager.restore ();
                if (!this.wasLong)
                    this.executeRow0 (index);
                break;
        }
    }


    /**
     * Implement for the specific function of the 1st row to execute.
     *
     * @param index The button index of the row
     */
    protected abstract void executeRow0 (final int index);


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
        if (this.parameterProviderWithDeviceParams == null || this.parameterProvider == null)
            throw new FrameworkException ("Parameter providers must be initialized for XLAbstractMainMode! Call setParameterProviders.");

        // Make sure there is the correct provider if the settings has changed in another main mode
        this.setParameterProvider (this.configuration.isDeviceActive () ? this.parameterProviderWithDeviceParams : this.parameterProvider);

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


    private void handleTransport (final int index, final ButtonEvent event)
    {
        final ITransport transport = this.model.getTransport ();
        switch (index)
        {
            // New clip
            case 0:
                if (event == ButtonEvent.DOWN)
                    this.newCommand.handleExecute (false);
                break;

            // Toggle Launcher Overdub
            case 1:
                if (event == ButtonEvent.DOWN)
                    transport.toggleLauncherOverdub ();
                break;

            // Decrease tempo
            case 2:
                this.tempoDown.execute (event, event == ButtonEvent.DOWN ? 127 : 0);
                break;

            // Increase tempo
            case 3:
                this.tempoUp.execute (event, event == ButtonEvent.DOWN ? 127 : 0);
                break;

            // Toggle Clip Automation Write
            case 6:
                if (event == ButtonEvent.DOWN)
                    transport.toggleWriteClipLauncherAutomation ();
                break;

            // Toggle plugin window
            case 7:
                if (event == ButtonEvent.DOWN)
                    this.model.getCursorDevice ().toggleWindowOpen ();
                break;

            default:
                break;
        }
    }


    protected int getTransportButtonColor (final int index)
    {
        final ITransport transport = this.model.getTransport ();

        switch (index)
        {
            // New clip
            case 0:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;

            // Toggle Launcher Overdub
            case 1:
                return transport.isLauncherOverdub () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER_LO;

            // Tempo
            case 2, 3:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN_LO;

            // Toggle Clip Automation Write
            case 6:
                return transport.isWritingClipLauncherAutomation () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED_LO;

            case 7:
                return this.model.getCursorDevice ().isWindowOpen () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN_LO;

            default:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        }
    }
}
