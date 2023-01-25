// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.PitchbendCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ConsoleLogger;
import de.mossgrabers.framework.utils.IntConsumerSupplier;
import de.mossgrabers.framework.utils.TestCallback;
import de.mossgrabers.framework.utils.TestFramework;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;


/**
 * Abstract base class for controller extensions.
 *
 * @param <C> The type of the configuration
 * @param <S> The type of the control surface
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerSetup<S extends IControlSurface<C>, C extends Configuration> implements IControllerSetup<S, C>
{
    protected final List<S>       surfaces    = new ArrayList<> ();
    protected final IHost         host;
    protected final ISettingsUI   globalSettings;
    protected final ISettingsUI   documentSettings;
    protected final ISetupFactory factory;

    protected Scales              scales;
    protected IModel              model;
    protected C                   configuration;
    protected ColorManager        colorManager;
    protected IValueChanger       valueChanger;
    protected Modes               currentMode = null;


    /**
     * Constructor.
     *
     * @param factory The factory
     * @param host The host
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    protected AbstractControllerSetup (final ISetupFactory factory, final IHost host, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        this.factory = factory;
        this.host = host;
        this.globalSettings = globalSettings;
        this.documentSettings = documentSettings;

        ConsoleLogger.init (host);
    }


    /** {@inheritDoc} */
    @Override
    public S getSurface ()
    {
        return this.surfaces.get (0);
    }


    /** {@inheritDoc} */
    @Override
    public List<S> getSurfaces ()
    {
        return new ArrayList<> (this.surfaces);
    }


    /** {@inheritDoc} */
    @Override
    public S getSurface (final int index)
    {
        return this.surfaces.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public IModel getModel ()
    {
        return this.model;
    }


    /** {@inheritDoc} */
    @Override
    public C getConfiguration ()
    {
        return this.configuration;
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        this.initConfiguration ();
        this.createScales ();
        this.createModel ();
        this.createSurface ();
        this.createModes ();
        this.createObservers ();
        this.createViews ();
        this.registerTriggerCommands ();
        this.registerContinuousCommands ();
        this.layoutControls ();
        if (this.model != null)
            this.model.ensureClip ();

        this.configuration.notifyAllObservers ();
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.configuration.clearSettingObservers ();
        for (final S surface: this.surfaces)
            surface.shutdown ();
        this.host.releaseUsbDevices ();
        this.host.println ("Exited.");
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        for (final S surface: this.surfaces)
            surface.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void test (final TestCallback callback)
    {
        final TestFramework framework = new TestFramework (this.host);

        this.getSurfaces ().forEach (surface -> {

            framework.scheduleFunction ( () -> this.host.println ("Testing controller: " + this.getClass ().getName ()));

            final ViewManager viewManager = surface.getViewManager ();
            final ModeManager modeManager = surface.getModeManager ();
            final int max = this.model.getValueChanger ().getUpperBound () - 1;

            for (final Views viewID: Views.values ())
            {
                if (viewManager.get (viewID) == null)
                    continue;

                for (final Modes modeID: Modes.values ())
                {
                    if (modeManager.get (modeID) == null)
                        continue;

                    framework.scheduleFunction ( () -> {

                        this.host.println ("- View " + viewID + " Mode " + modeID);

                        viewManager.setActive (viewID);
                        modeManager.setActive (modeID);

                        for (final ButtonID buttonID: ButtonID.values ())
                        {
                            final IHwButton button = surface.getButton (buttonID);
                            if (button == null)
                                continue;

                            button.trigger (ButtonEvent.DOWN);
                            button.trigger (ButtonEvent.LONG);
                            button.trigger (ButtonEvent.UP);
                        }

                        for (final ContinuousID continuousID: ContinuousID.values ())
                        {
                            final IHwContinuousControl continuous = surface.getContinuous (continuousID);
                            if (continuous == null)
                                continue;

                            final TriggerCommand touchCommand = continuous.getTouchCommand ();
                            if (touchCommand != null)
                            {
                                touchCommand.execute (ButtonEvent.DOWN, 127);
                                touchCommand.execute (ButtonEvent.LONG, 127);
                                touchCommand.execute (ButtonEvent.UP, 0);
                            }
                            final ContinuousCommand command = continuous.getCommand ();
                            if (command != null)
                            {
                                command.execute (0);
                                command.execute (max);
                                command.execute (max / 2);
                            }
                            final PitchbendCommand pitchbendCommand = continuous.getPitchbendCommand ();
                            if (pitchbendCommand != null)
                            {
                                pitchbendCommand.onPitchbend (0, 0);
                                pitchbendCommand.onPitchbend (0, 127);
                                pitchbendCommand.onPitchbend (0, 64);
                            }
                        }

                    });
                }
            }

        });

        callback.startTesting ();
        framework.executeScheduler (callback);
    }


    /**
     * De-/Activate the browser mode depending on the browsers' active state.
     *
     * @param browserMode The mode to hide/show when the browser becomes de-/active
     */
    protected void activateBrowserObserver (final Modes browserMode)
    {
        this.model.getBrowser ().addActiveObserver (isActive -> {

            final ModeManager modeManager = this.getSurface ().getModeManager ();
            if (isActive.booleanValue ())
                modeManager.setTemporary (browserMode);
            else if (modeManager.isActive (browserMode))
                modeManager.restore ();

        });
    }


    /**
     * De-/Activate the browser view depending on the browsers' active state.
     *
     * @param browserView The view to hide/show when the browser becomes de-/active
     */
    protected void activateBrowserObserver (final Views browserView)
    {
        this.model.getBrowser ().addActiveObserver (isActive -> {

            final ViewManager viewManager = this.getSurface ().getViewManager ();
            if (isActive.booleanValue ())
            {
                final Views previousViewId = viewManager.getPreviousID ();
                viewManager.setTemporary (browserView);
                if (viewManager.getPreviousID () == Views.SHIFT)
                    viewManager.setPreviousID (previousViewId);
            }
            else if (viewManager.isActive (browserView))
                viewManager.restore ();

        });
    }


    /**
     * Initialize the configuration settings.
     */
    protected void initConfiguration ()
    {
        this.configuration.init (this.globalSettings, this.documentSettings);
    }


    /**
     * Create the scales object.
     */
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 100, 8, 8);
    }


    /**
     * Create the model.
     */
    protected abstract void createModel ();


    /**
     * Create the surface.
     */
    protected abstract void createSurface ();


    /**
     * Create the modes.
     */
    protected void createModes ()
    {
        // Intentionally empty
    }


    /**
     * Create the views.
     */
    protected void createViews ()
    {
        // Intentionally empty
    }


    /**
     * Create the listeners.
     */
    protected void createObservers ()
    {
        if (this.configuration.canSettingBeObserved (AbstractConfiguration.KNOB_SENSITIVITY_DEFAULT))
        {
            this.configuration.addSettingObserver (AbstractConfiguration.KNOB_SENSITIVITY_DEFAULT, this::updateRelativeKnobSensitivity);
            this.configuration.addSettingObserver (AbstractConfiguration.KNOB_SENSITIVITY_SLOW, this::updateRelativeKnobSensitivity);

            this.surfaces.forEach (surface -> surface.addKnobSensitivityObserver (this::updateRelativeKnobSensitivity));
        }
    }


    /**
     * Create and register the trigger commands.
     */
    protected void registerTriggerCommands ()
    {
        // Intentionally empty
    }


    /**
     * Create and register the continuous commands.
     */
    protected void registerContinuousCommands ()
    {
        // Intentionally empty
    }


    /**
     * Layout the controls on the virtual GUI.
     */
    protected void layoutControls ()
    {
        // Intentionally empty
    }


    /**
     * Create a hardware button on/off proxy on controller device 1, bind a trigger command to it
     * and bind it to a MIDI CC on MIDI channel 1. State colors are ON and HI.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param command The command to bind
     * @param midiControl The MIDI CC or note
     * @param supplier Callback for retrieving the on/off state of the light
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final BooleanSupplier supplier)
    {
        this.addButton (buttonID, label, command, 0, midiControl, supplier);
    }


    /**
     * Create a hardware button on/off proxy on controller device 1, bind a trigger command to it
     * and bind it to a MIDI CC on MIDI channel 1. State colors are ON and HI.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param command The command to bind
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param supplier Callback for retrieving the on/off state of the light
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final BooleanSupplier supplier)
    {
        this.addButton (buttonID, label, command, midiChannel, midiControl, () -> supplier.getAsBoolean () ? 1 : 0, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1. The button has an on/off state.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param command The command to bind
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param supplier Callback for retrieving the on/off state of the light
     */
    protected void addButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final BooleanSupplier supplier)
    {
        this.addButton (surface, buttonID, label, command, midiChannel, midiControl, () -> supplier.getAsBoolean () ? 1 : 0, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
    }


    /**
     * Create a hardware button on/off proxy on controller device 1, bind a trigger command to it
     * and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIdOn The color ID for on state
     * @param colorIdHi The color ID for off state
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final BooleanSupplier supplier, final String colorIdOn, final String colorIdHi)
    {
        this.addButton (buttonID, label, command, midiControl, () -> supplier.getAsBoolean () ? 1 : 0, colorIdOn, colorIdHi);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1. The button has an on/off state.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl)
    {
        this.addButton (buttonID, label, command, 0, midiControl);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1. The button has an on/off state.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     */
    protected void addButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl)
    {
        this.addButton (surface, buttonID, label, command, 0, midiControl, (IntSupplier) null, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1. The button has an on/off state.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     */
    protected void addButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl)
    {
        this.addButton (surface, buttonID, label, command, midiChannel, midiControl, (IntSupplier) null, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC. The button has an on/off state.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl)
    {
        this.addButton (buttonID, label, command, midiChannel, midiControl, (IntSupplier) null, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        this.addButton (0, buttonID, label, command, midiControl, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        this.addButton (0, buttonID, label, command, midiChannel, midiControl, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy, bind a trigger command to it and bind it to a MIDI CC.
     *
     * @param deviceIndex The index of the device
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final int deviceIndex, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        this.addButton (this.surfaces.get (deviceIndex), buttonID, label, command, midiChannel, midiControl, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param deviceIndex The index of the device
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final int deviceIndex, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        this.addButton (this.surfaces.get (deviceIndex), buttonID, label, command, midiControl, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final IntSupplier supplier)
    {
        this.addButton (0, buttonID, label, command, midiControl, supplier);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     */
    protected void addButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntSupplier supplier)
    {
        this.addButton (0, buttonID, label, command, midiChannel, midiControl, supplier);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param deviceIndex The index of the device
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     */
    protected void addButton (final int deviceIndex, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final IntSupplier supplier)
    {
        this.addButton (deviceIndex, buttonID, label, command, 0, midiControl, supplier);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param deviceIndex The index of the device
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     */
    protected void addButton (final int deviceIndex, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntSupplier supplier)
    {
        this.addButton (this.surfaces.get (deviceIndex), buttonID, label, command, midiChannel, midiControl, supplier);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        this.addButton (surface, buttonID, label, command, 0, midiControl, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy, bind it to the trigger bind type retrieved from
     * {@link #getTriggerBindType(ButtonID)}. Use to ignore a message.
     *
     * @param buttonID The ID of the button (for later access)
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     */
    protected void addDummyButton (final ButtonID buttonID, final int midiChannel, final int midiControl)
    {
        this.addButton (this.getSurface (), buttonID, "", null, midiChannel, midiControl, -1, false, null);
    }


    /**
     * Create a hardware button proxy, bind a trigger command to it and bind it to the trigger bind
     * type retrieved from {@link #getTriggerBindType(ButtonID)}.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param value The specific value of the control to bind to
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final int value, final IntSupplier supplier, final String... colorIds)
    {
        this.addButton (surface, buttonID, label, command, midiChannel, midiControl, value, true, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy, bind a trigger command to it and bind it to the trigger bind
     * type retrieved from {@link #getTriggerBindType(ButtonID)}.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        this.addButton (surface, buttonID, label, command, midiChannel, midiControl, -1, true, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy, bind a trigger command to it and bind it to the trigger bind
     * type retrieved from {@link #getTriggerBindType(ButtonID)}.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param value The specific value of the control to bind to
     * @param command The command to bind
     * @param hasLight True create and add a light
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final int value, final boolean hasLight, final IntSupplier supplier, final String... colorIds)
    {
        this.addButton (surface, buttonID, label, command, midiChannel, midiChannel, midiControl, value, hasLight, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy, bind a trigger command to it and bind it to the trigger bind
     * type retrieved from {@link #getTriggerBindType(ButtonID)}.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiInputChannel The MIDI input channel
     * @param midiOutputChannel The MIDI output channel
     * @param midiControl The MIDI CC or note
     * @param value The specific value of the control to bind to
     * @param command The command to bind
     * @param hasLight True create and add a light
     * @param colorIds The color IDs to map to the states
     */
    protected void addButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiInputChannel, final int midiOutputChannel, final int midiControl, final int value, final boolean hasLight, final IntSupplier supplier, final String... colorIds)
    {
        final IHwButton button = surface.createButton (buttonID, label);
        button.bind (command);
        if (midiControl < 0)
            return;
        final IMidiInput midiInput = surface.getMidiInput ();
        final BindType bindType = this.getTriggerBindType (buttonID);
        if (value == -1)
            button.bind (midiInput, bindType, midiInputChannel, midiControl);
        else
            button.bind (midiInput, bindType, midiInputChannel, midiControl, value);
        if (hasLight)
        {
            final IntSupplier supp = supplier == null ? new ButtonPressedSupplier (button) : supplier;
            this.addLight (surface, null, buttonID, button, bindType, midiOutputChannel, midiControl, supp, colorIds);
        }
    }


    /**
     * Create multiple hardware button proxies. Each button is matched by a specific value. The
     * first value is startValue, which gets increased by one for the other buttons.
     *
     * @param surface The control surface
     * @param startValue The first matched value
     * @param numberOfValues The number of buttons
     * @param firstButtonID The first ID of the buttons
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButtons (final S surface, final int startValue, final int numberOfValues, final ButtonID firstButtonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntConsumerSupplier supplier, final String... colorIds)
    {
        for (int i = 0; i < numberOfValues; i++)
        {
            final int index = i;

            final ButtonID buttonID = ButtonID.get (firstButtonID, i);
            final IHwButton button = surface.createButton (buttonID, label + " " + (i + 1));
            button.bind ( (event, velocity) -> command.execute (event, index));
            if (midiControl < 0)
                continue;
            final BindType bindType = this.getTriggerBindType (buttonID);
            button.bind (surface.getMidiInput (), bindType, midiChannel, midiControl, startValue + i);
            final IntSupplier supp = supplier == null ? new ButtonPressedSupplier (button) : () -> supplier.process (index);
            this.addLight (surface, null, buttonID, button, bindType, midiChannel, midiControl, supp, colorIds);
        }
    }


    /**
     * Creates a light.
     *
     * @param surface The control surface
     * @param outputID The ID of the light (for later access)
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param supplier The supplier for the color state of the light
     * @param colorIds The color IDs to map to the states
     */
    protected void addLight (final S surface, final OutputID outputID, final int midiChannel, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        this.addLight (surface, outputID, null, null, null, midiChannel, midiControl, supplier, colorIds);
    }


    /**
     * Creates a light and adds it to the given button.
     *
     * @param surface The control surface
     * @param outputID The ID of the light (for later access)
     * @param buttonID The ID of the button (for later access)
     * @param button The button to assign it to, may be null
     * @param bindType The bind type used if the light is bound to a control widget
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param supplier The supplier for the color state of the light
     * @param colorIds The color IDs to map to the states
     */
    protected void addLight (final S surface, final OutputID outputID, final ButtonID buttonID, final IHwButton button, final BindType bindType, final int midiChannel, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        surface.createLight (outputID, () -> {
            final int state = supplier.getAsInt ();
            // Color is the state if there are no colors provided!
            if (colorIds == null || colorIds.length == 0)
                return state;
            return this.colorManager.getColorIndex (state < 0 ? ColorManager.BUTTON_STATE_OFF : colorIds[state]);
        }, color -> surface.setTrigger (bindType, midiChannel, midiControl, color), state -> this.colorManager.getColor (state, buttonID), button);
    }


    /**
     * Get the default bind type for triggering buttons.
     *
     * @param buttonID The button ID
     * @return The default, returns CC as default
     */
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return BindType.CC;
    }


    /**
     * Create a hardware fader proxy on controller device 1, bind a continuous command to it and
     * bind it to a MIDI pitchbend.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @return The created fader
     */
    protected IHwFader addFader (final ContinuousID continuousID, final String label, final PitchbendCommand command)
    {
        return this.addFader (this.getSurface (), continuousID, label, command, 0);
    }


    /**
     * Create a hardware fader proxy on controller device 1, bind a continuous command to it and
     * bind it to a MIDI pitchbend.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param midiChannel The MIDI channel
     * @return The created fader
     */
    protected IHwFader addFader (final ContinuousID continuousID, final String label, final PitchbendCommand command, final int midiChannel)
    {
        return this.addFader (this.getSurface (), continuousID, label, command, midiChannel);
    }


    /**
     * Create a hardware fader proxy on controller device 1, bind a continuous command to it and
     * bind it to a MIDI pitchbend.
     *
     * @param surface The control surface
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param midiChannel The MIDI channel
     * @return The created fader
     */
    protected IHwFader addFader (final S surface, final ContinuousID continuousID, final String label, final PitchbendCommand command, final int midiChannel)
    {
        final IHwFader fader = surface.createFader (continuousID, label, true);
        if (command != null)
            fader.bind (command);
        fader.bind (surface.getMidiInput (), BindType.PITCHBEND, midiChannel, 0);
        return fader;
    }


    /**
     * Create a hardware fader proxy on controller device 1, bind a continuous command to it and
     * bind it to a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiControl The MIDI CC or note
     * @return The created fader
     */
    protected IHwFader addFader (final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiControl)
    {
        return this.addFader (continuousID, label, command, bindType, 0, midiControl, true);
    }


    /**
     * Create a hardware fader proxy on controller device 1, bind a continuous command to it and
     * bind it to a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiControl The MIDI CC or note
     * @param isVertical True if the fader is vertical, otherwise horizontal
     * @return The created fader
     */
    protected IHwFader addFader (final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiControl, final boolean isVertical)
    {
        return this.addFader (continuousID, label, command, bindType, 0, midiControl, isVertical);
    }


    /**
     * Create a hardware fader proxy on a controller, bind a continuous command to it and bind it to
     * a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @return The created fader
     */
    protected IHwFader addFader (final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl)
    {
        return this.addFader (this.getSurface (), continuousID, label, command, bindType, midiChannel, midiControl, true);
    }


    /**
     * Create a hardware fader proxy on a controller, bind a continuous command to it and bind it to
     * a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param isVertical True if the fader is vertical, otherwise horizontal
     * @return The created fader
     */
    protected IHwFader addFader (final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl, final boolean isVertical)
    {
        return this.addFader (this.getSurface (), continuousID, label, command, bindType, midiChannel, midiControl, isVertical);
    }


    /**
     * Create a hardware fader proxy on a controller, bind a continuous command to it and bind it to
     * a MIDI CC.
     *
     * @param surface The control surface
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @return The created fader
     */
    protected IHwFader addFader (final S surface, final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl)
    {
        return this.addFader (surface, continuousID, label, command, bindType, midiChannel, midiControl, true);
    }


    /**
     * Create a hardware fader proxy on a controller, bind a continuous command to it and bind it to
     * a MIDI CC.
     *
     * @param surface The control surface
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param isVertical True if the fader is vertical, otherwise horizontal
     * @return The created fader
     */
    protected IHwFader addFader (final S surface, final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl, final boolean isVertical)
    {
        final IHwFader fader = surface.createFader (continuousID, label, isVertical);
        fader.bind (command);
        fader.bind (surface.getMidiInput (), bindType, midiChannel, midiControl);
        return fader;
    }


    /**
     * Create a hardware knob proxy on a controller, which sends absolute values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @return The created knob
     */
    protected IHwAbsoluteKnob addAbsoluteKnob (final ContinuousID continuousID, final String label, final ContinuousCommand command, final int midiControl)
    {
        return this.addAbsoluteKnob (continuousID, label, command, BindType.CC, 0, midiControl);
    }


    /**
     * Create a hardware knob proxy on a controller, which sends absolute values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @return The created knob
     */
    protected IHwAbsoluteKnob addAbsoluteKnob (final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl)
    {
        return this.addAbsoluteKnob (this.getSurface (), continuousID, label, command, bindType, midiChannel, midiControl);
    }


    /**
     * Create a hardware knob proxy on a controller, which sends absolute values, bind a continuous
     * command to it and bind it to a MIDI CC.
     *
     * @param surface The control surface
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @return The created knob
     */
    protected IHwAbsoluteKnob addAbsoluteKnob (final S surface, final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl)
    {
        final IHwAbsoluteKnob knob = surface.createAbsoluteKnob (continuousID, label);
        if (command != null)
            knob.bind (command);
        knob.bind (surface.getMidiInput (), bindType, midiChannel, midiControl);
        return knob;
    }


    /**
     * Create a hardware knob proxy on a controller, which sends relative values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @return The created knob
     */
    protected IHwRelativeKnob addRelativeKnob (final ContinuousID continuousID, final String label, final ContinuousCommand command, final int midiControl)
    {
        return this.addRelativeKnob (continuousID, label, command, BindType.CC, 0, midiControl, RelativeEncoding.TWOS_COMPLEMENT);
    }


    /**
     * Create a hardware knob proxy on a controller, which sends relative values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param encoding The relative value encoding
     * @return The created knob
     */
    protected IHwRelativeKnob addRelativeKnob (final ContinuousID continuousID, final String label, final ContinuousCommand command, final int midiControl, final RelativeEncoding encoding)
    {
        return this.addRelativeKnob (continuousID, label, command, BindType.CC, 0, midiControl, encoding);
    }


    /**
     * Create a hardware knob proxy on a controller, which sends relative values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param surface The control surface
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @return The created knob
     */
    protected IHwRelativeKnob addRelativeKnob (final S surface, final ContinuousID continuousID, final String label, final ContinuousCommand command, final int midiControl)
    {
        return this.addRelativeKnob (surface, continuousID, label, command, BindType.CC, 0, midiControl, RelativeEncoding.TWOS_COMPLEMENT);
    }


    /**
     * Create a hardware knob proxy on a controller, which sends relative values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param surface The control surface
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param encoding The relative value encoding
     * @return The created knob
     */
    protected IHwRelativeKnob addRelativeKnob (final S surface, final ContinuousID continuousID, final String label, final ContinuousCommand command, final int midiControl, final RelativeEncoding encoding)
    {
        return this.addRelativeKnob (surface, continuousID, label, command, BindType.CC, 0, midiControl, encoding);
    }


    /**
     * Create a hardware knob proxy on a controller, which sends relative values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @return The created knob
     */
    protected IHwRelativeKnob addRelativeKnob (final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl)
    {
        return this.addRelativeKnob (this.getSurface (), continuousID, label, command, bindType, midiChannel, midiControl, RelativeEncoding.TWOS_COMPLEMENT);
    }


    /**
     * Create a hardware knob proxy on a controller, which sends relative values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param encoding The relative value encoding
     * @return The created knob
     */
    protected IHwRelativeKnob addRelativeKnob (final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl, final RelativeEncoding encoding)
    {
        return this.addRelativeKnob (this.getSurface (), continuousID, label, command, bindType, midiChannel, midiControl, encoding);
    }


    /**
     * Create a hardware knob proxy on a controller, which sends relative values, bind a continuous
     * command to it and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param surface The control surface
     * @param continuousID The ID of the control (for later access)
     * @param label The label of the fader
     * @param command The command to bind
     * @param bindType The MIDI bind type
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param encoding The relative value encoding
     * @return The created knob
     */
    protected IHwRelativeKnob addRelativeKnob (final S surface, final ContinuousID continuousID, final String label, final ContinuousCommand command, final BindType bindType, final int midiChannel, final int midiControl, final RelativeEncoding encoding)
    {
        final IHwRelativeKnob knob = surface.createRelativeKnob (continuousID, label, encoding);
        knob.bind (command);
        knob.bind (surface.getMidiInput (), bindType, midiChannel, midiControl);
        return knob;
    }


    /**
     * Register observers for all scale settings. Stores the changed value in the scales object and
     * updates the active views note mapping.
     *
     * @param conf The configuration
     */
    protected void createScaleObservers (final C conf)
    {
        if (conf.canSettingBeObserved (AbstractConfiguration.SCALES_SCALE))
        {
            conf.addSettingObserver (AbstractConfiguration.SCALES_SCALE, () -> {
                this.scales.setScaleByName (conf.getScale ());
                this.updateViewNoteMapping ();
            });
        }

        if (conf.canSettingBeObserved (AbstractConfiguration.SCALES_BASE))
        {
            conf.addSettingObserver (AbstractConfiguration.SCALES_BASE, () -> {
                this.scales.setScaleOffsetByName (conf.getScaleBase ());
                this.updateViewNoteMapping ();
            });
        }

        if (conf.canSettingBeObserved (AbstractConfiguration.SCALES_IN_KEY))
        {
            conf.addSettingObserver (AbstractConfiguration.SCALES_IN_KEY, () -> {
                this.scales.setChromatic (!conf.isScaleInKey ());
                this.updateViewNoteMapping ();
            });
        }

        if (conf.canSettingBeObserved (AbstractConfiguration.SCALES_LAYOUT))
        {
            conf.addSettingObserver (AbstractConfiguration.SCALES_LAYOUT, () -> {
                this.scales.setScaleLayoutByName (conf.getScaleLayout ());
                this.updateViewNoteMapping ();
            });
        }
    }


    protected void createNoteRepeatObservers (final C conf, final S surface)
    {
        final INoteInput defaultNoteInput = surface.getMidiInput ().getDefaultNoteInput ();
        if (defaultNoteInput == null)
            return;

        final INoteRepeat noteRepeat = defaultNoteInput.getNoteRepeat ();
        conf.addSettingObserver (AbstractConfiguration.NOTEREPEAT_ACTIVE, () -> noteRepeat.setActive (conf.isNoteRepeatActive ()));
        conf.addSettingObserver (AbstractConfiguration.NOTEREPEAT_PERIOD, () -> noteRepeat.setPeriod (conf.getNoteRepeatPeriod ().getValue ()));
        if (this.host.supports (Capability.NOTE_REPEAT_LENGTH))
            conf.addSettingObserver (AbstractConfiguration.NOTEREPEAT_LENGTH, () -> noteRepeat.setNoteLength (conf.getNoteRepeatLength ().getValue ()));
        if (this.host.supports (Capability.NOTE_REPEAT_MODE))
            conf.addSettingObserver (AbstractConfiguration.NOTEREPEAT_MODE, () -> noteRepeat.setMode (conf.getNoteRepeatMode ()));
        if (this.host.supports (Capability.NOTE_REPEAT_OCTAVES))
            conf.addSettingObserver (AbstractConfiguration.NOTEREPEAT_OCTAVE, () -> noteRepeat.setOctaves (conf.getNoteRepeatOctave ()));
    }


    /**
     * Update the active views note mapping.
     */
    protected void updateViewNoteMapping ()
    {
        for (final S surface: this.surfaces)
        {
            final IView view = surface.getViewManager ().getActive ();
            if (view != null)
                view.updateNoteMapping ();
        }
    }


    /**
     * Test if record mode (Arrange / Session) is flipped due to Shift press or configuration
     * setting.
     *
     * @param surface The surface
     * @return True if shifted
     */
    protected boolean isRecordShifted (final S surface)
    {
        final boolean isShift = surface.isShiftPressed ();
        final boolean isFlipRecord = this.configuration.isFlipRecord ();
        return isShift && !isFlipRecord || !isShift && isFlipRecord;
    }


    /**
     * Get the color for a button, which is controlled by the active mode.
     *
     * @param buttonID The ID of the button
     * @return A color index
     */
    protected int getModeColor (final ButtonID buttonID)
    {
        final IMode mode = this.getSurface ().getModeManager ().getActive ();
        return mode == null ? 0 : mode.getButtonColor (buttonID);
    }


    /**
     * Get the color for a button, which is controlled by the active view.
     *
     * @param buttonID The ID of the button
     * @return A color index
     */
    protected int getButtonColorFromActiveView (final ButtonID buttonID)
    {
        final IView view = this.getSurface ().getViewManager ().getActive ();
        return view == null ? 0 : view.getButtonColor (buttonID);
    }


    /**
     * Updates the knob sensitivities from the configuration settings.
     */
    protected void updateRelativeKnobSensitivity ()
    {
        this.surfaces.forEach (surface -> {

            final int knobSensitivity = surface.isKnobSensitivitySlow () ? this.configuration.getKnobSensitivitySlow () : this.configuration.getKnobSensitivityDefault ();
            this.valueChanger.setSensitivity (knobSensitivity);
            surface.getRelativeKnobs ().forEach (knob -> {
                if (knob.shouldAdaptSensitivity ())
                    knob.setSensitivity (knobSensitivity);
            });

        });
    }


    /**
     * Get the button color index from the active mode. Returns 0 if there is no active mode.
     *
     * @param surface The surface
     * @param buttonID The ID of the button for which to get the color
     * @return The index of the color
     */
    protected int getButtonColor (final S surface, final ButtonID buttonID)
    {
        final IMode mode = surface.getModeManager ().getActive ();
        return mode == null ? 0 : mode.getButtonColor (buttonID);
    }


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    protected void handleTrackChange (final boolean isSelected)
    {
        if (!isSelected)
            return;
        this.updateView ();
        this.updateMode ();
    }


    /**
     * Update the used view.
     */
    protected void updateView ()
    {
        this.recallLastView ();
        this.resetDrumOctave ();
    }


    /**
     * Reset drum octave because the drum pad bank is also reset.
     */
    protected void resetDrumOctave ()
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        this.scales.resetDrumOctave ();
        if (viewManager.isActive (Views.DRUM))
            viewManager.get (Views.DRUM).updateNoteMapping ();
        else if (viewManager.isActive (Views.PLAY))
            viewManager.getActive ().updateNoteMapping ();
    }


    /**
     * Recall last used view (if we are not in session mode).
     */
    protected void recallLastView ()
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (viewManager.isActive (Views.SESSION, Views.MIX))
            return;

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ())
        {
            Views preferredView = viewManager.getPreferredView (cursorTrack.getPosition ());
            if (preferredView == null)
                preferredView = cursorTrack.canHoldNotes () ? this.configuration.getPreferredNoteView () : this.configuration.getPreferredAudioView ();
            if (viewManager.get (preferredView) != null)
                viewManager.setActive (preferredView);
        }
    }


    /**
     * Update the used mode.
     */
    protected void updateMode ()
    {
        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (modeManager.isActive (Modes.MASTER) && !this.model.getMasterTrack ().isSelected ())
        {
            if (Modes.isTrackMode (modeManager.getPreviousID ()))
                modeManager.restore ();
            else
                modeManager.setActive (Modes.TRACK);
        }
    }


    /** Supplier for a buttons' pressed state. */
    private class ButtonPressedSupplier implements IntSupplier
    {
        private final IHwButton button;


        /**
         * Constructor.
         *
         * @param button The hardware button
         */
        ButtonPressedSupplier (final IHwButton button)
        {
            this.button = button;
        }


        /** {@inheritDoc} */
        @Override
        public int getAsInt ()
        {
            return this.button.isPressed () ? 1 : 0;
        }
    }
}
