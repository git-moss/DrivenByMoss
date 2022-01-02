// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.parameterprovider;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.observer.ISettingObserver;
import de.mossgrabers.framework.parameterprovider.device.SelectedLayerOrDrumPadParameterProvider;


/**
 * Extends channel parameter provider with the specific layout of Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        super.addParametersObserver (observer);

        this.configuration.addSettingObserver (PushConfiguration.TOGGLING_SENDS, this);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        super.removeParametersObserver (observer);

        if (this.hasObservers ())
            this.configuration.removeSettingObserver (PushConfiguration.TOGGLING_SENDS, this);
    }


    /** {@inheritDoc} */
    @Override
    protected IParameter getInternal (final int index, final IChannel selectedChannel)
    {
        if (index < 2 || !this.configuration.isPush2 ())
            return super.getInternal (index, selectedChannel);

        switch (index)
        {
            case 2:
            case 3:
                return EmptyParameter.INSTANCE;
            default:
                final int sendOffset = this.configuration.isSendsAreToggled () ? 0 : 4;
                return this.handleSends (index - sendOffset, selectedChannel);
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