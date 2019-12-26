// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.utils.FrameworkException;

import java.util.ArrayList;
import java.util.EnumMap;
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
    private final Map<Views, View>         views               = new EnumMap<> (Views.class);
    private final List<ViewChangeListener> viewChangeListeners = new ArrayList<> ();
    private final Map<Integer, Views>      preferredViews      = new HashMap<> ();

    private Views                          activeViewId        = null;
    private Views                          previousViewId      = null;


    /**
     * Register a view.
     *
     * @param viewId The ID of the view to register
     * @param view The view to register
     */
    public void registerView (final Views viewId, final View view)
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
    public View getView (final Views viewId)
    {
        return this.views.get (viewId);
    }


    /**
     * Set the active view.
     *
     * @param viewId The ID of the view to activate
     */
    public void setActiveView (final Views viewId)
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
    public Views getActiveViewId ()
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
        return this.activeViewId == null ? null : this.getView (this.activeViewId);
    }


    /**
     * Checks if the view with the given ID is the active view.
     *
     * @param viewId An ID
     * @return True if active
     */
    public boolean isActiveView (final Views viewId)
    {
        return this.activeViewId == viewId;
    }


    /**
     * Get the previous view ID.
     *
     * @return The ID of the previous view
     */
    public Views getPreviousViewId ()
    {
        return this.previousViewId;
    }


    /**
     * Set the previous view.
     *
     * @param viewId The ID of the previous view
     */
    public void setPreviousView (final Views viewId)
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
     * Stores the given view for the currently selected track.
     *
     * @param position The position of track (over all tracks)
     * @param viewID The ID of the view to set
     */
    public void setPreferredView (final int position, final Views viewID)
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
    public Views getPreferredView (final int position)
    {
        return position >= 0 ? this.preferredViews.get (Integer.valueOf (position)) : null;
    }
}
