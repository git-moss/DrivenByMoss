// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.FeatureGroupManager;
import de.mossgrabers.framework.featuregroup.View;
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
public class ViewManager extends FeatureGroupManager<Views, View>
{
    private final List<ViewChangeListener> viewChangeListeners = new ArrayList<> ();
    private final Map<Integer, Views>      preferredViews      = new HashMap<> ();


    /**
     * Constructor.
     */
    public ViewManager ()
    {
        super (Views.class);
    }


    /** {@inheritDoc} */
    @Override
    public void register (final Views viewId, final View view)
    {
        super.register (viewId, view);

        // Make sure it is off until used
        view.onDeactivate ();
    }


    /**
     * Set the active view.
     *
     * @param viewId The ID of the view to activate
     */
    public void setActive (final Views viewId)
    {
        // Deactivate current view
        View view = this.getActive ();
        if (view != null)
            view.onDeactivate ();

        // Set the new view
        this.previousID = this.activeID;
        this.activeID = viewId;

        view = this.getActive ();
        if (view == null)
            throw new FrameworkException ("Trying to activate view that does not exist: " + viewId);

        view.onActivate ();

        // Notify all view change listeners
        for (final ViewChangeListener listener: this.viewChangeListeners)
            listener.call (this.previousID, this.activeID);
    }


    /**
     * Get the active view.
     *
     * @return The active view, might be null if not set
     */
    public View getActive ()
    {
        return this.activeID == null ? null : this.get (this.activeID);
    }


    /**
     * Checks if the view with the given ID is the active view.
     *
     * @param viewId An ID
     * @return True if active
     */
    public boolean isActive (final Views viewId)
    {
        return this.activeID == viewId;
    }


    /**
     * Checks if one of the view IDs is the active view.
     *
     * @param viewIds Several IDs
     * @return True if active
     */
    public boolean isActive (final Views... viewIds)
    {
        for (final Views viewID: viewIds)
        {
            if (this.isActive (viewID))
                return true;
        }
        return false;
    }


    /**
     * Get the previous view ID.
     *
     * @return The ID of the previous view
     */
    public Views getPreviousId ()
    {
        return this.previousID;
    }


    /**
     * Get the previous view.
     *
     * @return The previous view, might be null if not set
     */
    public View getPrevious ()
    {
        return this.previousID == null ? null : this.get (this.previousID);
    }


    /**
     * Set the previous view.
     *
     * @param viewId The ID of the previous view
     */
    public void setPrevious (final Views viewId)
    {
        this.previousID = viewId;
    }


    /**
     * Set the previous view as the active one.
     */
    public void restore ()
    {
        this.setActive (this.previousID);
    }


    /**
     * Register a listener which gets notified if the active view has changed.
     *
     * @param listener The listener to register
     */
    public void addChangeListener (final ViewChangeListener listener)
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
