// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.command.core.AftertouchCommand;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.PitchbendCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.utils.FrameworkException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Manages all views and assigned commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ViewManager
{
    private final Map<Integer, View>       views               = new HashMap<> ();
    private final List<ViewChangeListener> viewChangeListeners = new ArrayList<> ();
    private final Map<Integer, Integer>    preferredViews      = new HashMap<> ();

    private Integer                        activeViewId        = Integer.valueOf (-1);
    private Integer                        previousViewId      = Integer.valueOf (-1);


    /**
     * Register a view.
     *
     * @param viewId The ID of the view to register
     * @param view The view to register
     */
    public void registerView (final Integer viewId, final View view)
    {
        this.views.put (viewId, view);

        // Make sure it is off until used
        view.onDeactivate ();
    }


    /**
     * Get the view with the given ID.
     *
     * @param viewId An ID
     * @return The view or null if no view with that ID is registered
     */
    public View getView (final Integer viewId)
    {
        return this.views.get (viewId);
    }


    /**
     * Set the active view.
     *
     * @param viewId The ID of the view to activate
     */
    public void setActiveView (final Integer viewId)
    {
        // Deactivate current view
        View view = this.getActiveView ();
        if (view != null)
            view.onDeactivate ();

        // Set the new view
        this.previousViewId = this.activeViewId;
        this.activeViewId = viewId;

        view = this.getActiveView ();
        if (view == null)
            throw new FrameworkException ("Trying to activate view that does not exist: " + viewId);

        view.onActivate ();

        // Notify all view change listeners
        for (final ViewChangeListener listener: this.viewChangeListeners)
            listener.call (this.previousViewId, this.activeViewId);
    }


    /**
     * Get the active view ID.
     *
     * @return The ID of the active view
     */
    public Integer getActiveViewId ()
    {
        return this.activeViewId;
    }


    /**
     * Get the active view.
     *
     * @return The active view
     */
    public View getActiveView ()
    {
        return this.activeViewId.intValue () < 0 ? null : this.getView (this.activeViewId);
    }


    /**
     * Checks if the view with the given ID is the active view.
     *
     * @param viewId An ID
     * @return True if active
     */
    public boolean isActiveView (final Integer viewId)
    {
        return this.activeViewId.equals (viewId);
    }


    /**
     * Get the previous view ID.
     *
     * @return The ID of the previous view
     */
    public Integer getPreviousViewId ()
    {
        return this.previousViewId;
    }


    /**
     * Set the previous view.
     *
     * @param viewId The ID of the previous view
     */
    public void setPreviousView (final Integer viewId)
    {
        this.previousViewId = viewId;
    }


    /**
     * Set the previous view as the active one.
     */
    public void restoreView ()
    {
        this.setActiveView (this.previousViewId);
    }


    /**
     * Register a listener which gets notified if the active view has changed.
     *
     * @param listener The listener to register
     */
    public void addViewChangeListener (final ViewChangeListener listener)
    {
        this.viewChangeListeners.add (listener);
    }


    /**
     * Register a (global) trigger command for all views.
     *
     * @param commandID The ID of the command to register
     * @param command The command to register
     */
    public void registerTriggerCommand (final Integer commandID, final TriggerCommand command)
    {
        this.views.forEach ( (viewID, view) -> view.registerTriggerCommand (commandID, command));
    }


    /**
     * Register a (global) continuous command for all views.
     *
     * @param commandID The ID of the command to register
     * @param command The command to register
     */
    public void registerContinuousCommand (final Integer commandID, final ContinuousCommand command)
    {
        this.views.forEach ( (viewID, view) -> view.registerContinuousCommand (commandID, command));
    }


    /**
     * Register a (global) note (trigger) command for all views.
     *
     * @param commandID The ID of the command to register
     * @param command The command to register
     */
    public void registerNoteCommand (final Integer commandID, final TriggerCommand command)
    {
        this.views.forEach ( (viewID, view) -> view.registerNoteCommand (commandID, command));
    }


    /**
     * Register a pitchbend command for all views.
     *
     * @param command The command to register
     */
    public void registerPitchbendCommand (final PitchbendCommand command)
    {
        this.views.forEach ( (viewID, view) -> view.registerPitchbendCommand (command));
    }


    /**
     * Register an aftertouch command for all views.
     *
     * @param command The command to register
     */
    public void registerAftertouchCommand (final AftertouchCommand command)
    {
        this.views.forEach ( (viewID, view) -> view.registerAftertouchCommand (command));
    }


    /**
     * Stores the given view for the currently selected track.
     *
     * @param position The position of track (over all tracks)
     * @param viewID The ID of the view to set
     */
    public void setPreferredView (final int position, final Integer viewID)
    {
        if (position >= 0)
            this.preferredViews.put (Integer.valueOf (position), viewID);
    }


    /**
     * Get the stored view for a track.
     *
     * @param position The position of track (over all tracks)
     * @return The preferred view or null if none is stored
     */
    public Integer getPreferredView (final int position)
    {
        return position >= 0 ? this.preferredViews.get (Integer.valueOf (position)) : null;
    }
}
