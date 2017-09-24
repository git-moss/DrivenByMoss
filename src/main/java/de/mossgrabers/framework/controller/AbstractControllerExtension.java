// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.View;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

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
public abstract class AbstractControllerExtension<S extends ControlSurface<C>, C extends Configuration> extends ControllerExtension
{
    protected List<S>      surfaces = new ArrayList<> ();
    protected Scales       scales;
    protected Model        model;
    protected C            configuration;
    protected ColorManager colorManager;
    protected ValueChanger valueChanger;


    /**
     * Csontructor.
     *
     * @param definition The definition
     * @param host The host
     */
    protected AbstractControllerExtension (final ControllerExtensionDefinition definition, final ControllerHost host)
    {
        super (definition, host);
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
    public void init ()
    {
        this.configuration.init (this.getHost ().getPreferences ());

        this.createScales ();
        this.createModel ();
        this.createSurface ();
        this.createModes ();
        this.createObservers ();
        this.createViews ();
        this.registerTriggerCommands ();
        this.registerContinuousCommands ();
        this.startup ();

        this.getHost ().println ("Initialized.");
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.configuration.clearSettingObservers ();
        for (final S surface: this.surfaces)
            surface.shutdown ();
        this.getHost ().println ("Exited.");
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
    protected abstract void createViews ();


    /**
     * Create the listeners.
     */
    protected abstract void createObservers ();


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
     * Startup the controller.
     */
    protected abstract void startup ();


    /**
     * Register a (global) trigger command for all views and assign it to a MIDI CC for the first
     * device.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     */
    protected void addTriggerCommand (final Integer commandID, final int midiCC, final TriggerCommand command)
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
    protected void addTriggerCommand (final Integer commandID, final int midiCC, final TriggerCommand command, final int deviceIndex)
    {
        final S surface = this.surfaces.get (deviceIndex);
        surface.getViewManager ().registerTriggerCommand (commandID, command);
        surface.assignTriggerCommand (midiCC, commandID);
    }


    /**
     * Register a (global) continuous command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     */
    protected void addContinuousCommand (final Integer commandID, final int midiCC, final ContinuousCommand command)
    {
        final S surface = this.surfaces.get (0);
        surface.getViewManager ().registerContinuousCommand (commandID, command);
        surface.assignContinuousCommand (midiCC, commandID);
    }


    /**
     * Register a (global) note command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     */
    protected void addNoteCommand (final Integer commandID, final int midiCC, final TriggerCommand command)
    {
        final S surface = this.surfaces.get (0);
        surface.getViewManager ().registerNoteCommand (commandID, command);
        surface.assignNoteCommand (midiCC, commandID);
    }


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
    private void updateViewNoteMapping ()
    {
        for (final S surface: this.surfaces)
        {
            final View view = surface.getViewManager ().getActiveView ();
            if (view != null)
                view.updateNoteMapping ();
        }
    }
}
