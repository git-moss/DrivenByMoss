// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.parameterprovider;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.observer.ISettingObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;


/**
 * Extends channel parameter provider with the specific layout of Push 2.
 *
 * @author Jürgen Moßgraber
 */
public class PushTrackParameterProvider extends SelectedTrackParameterProvider implements ISettingObserver
{
    private final PushConfiguration configuration;


    /**
     * Constructor.
     *
     * @param model Uses the current channel bank from this model to get the parameters
     * @param configuration The configuration
     */
    public PushTrackParameterProvider (final IModel model, final PushConfiguration configuration)
    {
        super (model);

        this.configuration = configuration;
    }


    /** {@inheritDoc} */
    @Override
    protected IParameter getInternal (final int index, final IChannel selectedChannel)
    {
        if (index < 2 || !this.configuration.isPushModern ())
            return super.getInternal (index, selectedChannel);

        switch (index)
        {
            case 2:
                return ((ITrack) selectedChannel).getCrossfadeParameter ();
            case 3:
                return EmptyParameter.INSTANCE;
            default:
                return this.getSend (index - 4, selectedChannel);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void hasChanged ()
    {
        // Sends are toggled...
        this.notifyParametersObservers ();
    }
}