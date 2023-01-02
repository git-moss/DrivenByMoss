// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.view.Views;

import java.util.HashMap;
import java.util.Map;


/**
 * Manages all views and assigned commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ViewManager extends FeatureGroupManager<Views, IView>
{
    private final Map<Integer, Views> preferredViews = new HashMap<> ();


    /**
     * Constructor.
     */
    public ViewManager ()
    {
        super (Views.class);
    }


    /** {@inheritDoc} */
    @Override
    public void register (final Views viewId, final IView view)
    {
        super.register (viewId, view);

        // Make sure it is off until used
        view.onDeactivate ();
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
