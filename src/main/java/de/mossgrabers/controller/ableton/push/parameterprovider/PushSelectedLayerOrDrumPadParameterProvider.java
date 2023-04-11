// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.parameterprovider;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.observer.ISettingObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.SelectedLayerOrDrumPadParameterProvider;


/**
 * Extends channel parameter provider with the specific layout of Push 2.
 *
 * @author Jürgen Moßgraber
 */
public class PushSelectedLayerOrDrumPadParameterProvider extends SelectedLayerOrDrumPadParameterProvider implements ISettingObserver
{
    private final PushConfiguration configuration;


    /**
     * Constructor.
     *
     * @param configuration The configuration
     * @param device Uses the layer bank from the given device to get the parameters
     */
    public PushSelectedLayerOrDrumPadParameterProvider (final ISpecificDevice device, final PushConfiguration configuration)
    {
        super (device);

        this.configuration = configuration;
    }


    /** {@inheritDoc} */
    @Override
    protected IParameter getInternal (final int index, final IChannel selectedChannel)
    {
        if (index < 2 || !this.configuration.isPush2 ())
            return super.getInternal (index, selectedChannel);

        switch (index)
        {
            case 2, 3:
                return EmptyParameter.INSTANCE;
            default:
                return this.handleSends (index - 4, selectedChannel);
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