// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract base class for controller extensions.
 *
 * @param <C> The type of the configuration
 * @param <S> The type of the control surface
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerSetup<S extends IControlSurface<C>, C extends Configuration> implements IControllerSetup
{
    protected final List<S>       surfaces    = new ArrayList<> ();
    protected final IHost         host;
    protected final ISettingsUI   settings;
    protected final ISetupFactory factory;

    protected Scales              scales;
    protected IModel              model;
    protected C                   configuration;
    protected ColorManager        colorManager;
    protected IValueChanger       valueChanger;
    protected Modes               currentMode = Modes.MODE_VOLUME;


    /**
     * Constructor.
     *
     * @param factory The factory
     * @param host The host
     * @param settings The settings
     */
    protected AbstractControllerSetup (final ISetupFactory factory, final IHost host, final ISettingsUI settings)
    {
        this.factory = factory;
        this.host = host;
        this.settings = settings;
    }


    /**
     * Get the 1st surface. Convenience method for backwards compatibility.
     *
     * @return The 1st surface
     */
    public S getSurface ()
    {
        return this.surfaces.get (0);
    }


    /**
     * Get a surface.
     *
     * @param index The index of the surface
     * @return The surface
     */
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
        if (this.model != null)
            this.model.ensureClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.configuration.clearSettingObservers ();
        for (final S surface: this.surfaces)
            surface.shutdown ();
        this.host.println ("Exited.");
    }


    /**
     * Flush all surfaces.
     */
    public void flushSurfaces ()
    {
        for (final S surface: this.surfaces)
            surface.flush ();
    }


    /**
     * Initialize the configuration settings.
     */
    protected void initConfiguration ()
    {
        this.configuration.init (this.settings);
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
        // Intentionally empty
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
     * Register a (global) trigger command for all views and assign it to a MIDI CC for the first
     * device.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     */
    protected void addTriggerCommand (final TriggerCommandID commandID, final int midiCC, final TriggerCommand command)
    {
        this.addTriggerCommand (commandID, midiCC, command, 0);
    }


    /**
     * Register a (global) trigger command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     * @param deviceIndex The index of the device
     */
    protected void addTriggerCommand (final TriggerCommandID commandID, final int midiCC, final TriggerCommand command, final int deviceIndex)
    {
        final S surface = this.surfaces.get (deviceIndex);
        surface.getViewManager ().registerTriggerCommand (commandID, command);
        surface.assignTriggerCommand (midiCC, commandID);
    }


    /**
     * Register a (global) trigger command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @param command The command to register
     */
    protected void addTriggerCommand (final TriggerCommandID commandID, final int midiCC, final int midiChannel, final TriggerCommand command)
    {
        this.addTriggerCommand (commandID, midiCC, midiChannel, command, 0);
    }


    /**
     * Register a (global) trigger command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @param command The command to register
     * @param deviceIndex The index of the device
     */
    protected void addTriggerCommand (final TriggerCommandID commandID, final int midiCC, final int midiChannel, final TriggerCommand command, final int deviceIndex)
    {
        final S surface = this.surfaces.get (deviceIndex);
        surface.getViewManager ().registerTriggerCommand (commandID, command);
        surface.assignTriggerCommand (midiCC, midiChannel, commandID);
    }


    /**
     * Register a (global) continuous command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @param command The command to register
     */
    protected void addContinuousCommand (final ContinuousCommandID commandID, final int midiCC, final int midiChannel, final ContinuousCommand command)
    {
        final S surface = this.surfaces.get (0);
        surface.getViewManager ().registerContinuousCommand (commandID, command);
        surface.assignContinuousCommand (midiCC, midiChannel, commandID);
    }


    /**
     * Register a (global) continuous command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     */
    protected void addContinuousCommand (final ContinuousCommandID commandID, final int midiCC, final ContinuousCommand command)
    {
        final S surface = this.surfaces.get (0);
        surface.getViewManager ().registerContinuousCommand (commandID, command);
        surface.assignContinuousCommand (midiCC, commandID);
    }


    /**
     * Register a (global) note command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param note The midi note
     * @param command The command to register
     */
    protected void addNoteCommand (final TriggerCommandID commandID, final int note, final TriggerCommand command)
    {
        final S surface = this.surfaces.get (0);
        surface.getViewManager ().registerNoteCommand (commandID, command);
        surface.assignNoteCommand (note, commandID);
    }


    /**
     * Update the DAW indications for the given mode.
     *
     * @param mode The new mode
     */
    protected abstract void updateIndication (final Modes mode);


    /**
     * Register observers for all scale settings. Stores the changed value in the scales object and
     * updates the actives views note mapping.
     *
     * @param conf The configuration
     */
    protected void createScaleObservers (final C conf)
    {
        conf.addSettingObserver (AbstractConfiguration.SCALES_SCALE, () -> {
            this.scales.setScaleByName (conf.getScale ());
            this.updateViewNoteMapping ();
        });
        conf.addSettingObserver (AbstractConfiguration.SCALES_BASE, () -> {
            this.scales.setScaleOffsetByName (conf.getScaleBase ());
            this.updateViewNoteMapping ();
        });
        conf.addSettingObserver (AbstractConfiguration.SCALES_IN_KEY, () -> {
            this.scales.setChromatic (!conf.isScaleInKey ());
            this.updateViewNoteMapping ();
        });
        conf.addSettingObserver (AbstractConfiguration.SCALES_LAYOUT, () -> {
            this.scales.setScaleLayoutByName (conf.getScaleLayout ());
            this.updateViewNoteMapping ();
        });
    }


    /**
     * Update the active views note mapping.
     */
    protected void updateViewNoteMapping ()
    {
        for (final S surface: this.surfaces)
        {
            final View view = surface.getViewManager ().getActiveView ();
            if (view != null)
                view.updateNoteMapping ();
        }
    }
}
