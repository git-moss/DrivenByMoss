// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Default data for an empty channel.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyChannel extends EmptyItem implements IChannel
{
    private final int sendPageSize;


    /**
     * Constructor.
     *
     * @param sendPageSize The size of the sends pages
     */
    public EmptyChannel (final int sendPageSize)
    {
        this.sendPageSize = sendPageSize;
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        return ChannelType.UNKNOWN;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActivated ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getVolumeParameter ()
    {
        return EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getVolume ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr (final int limit)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getPanParameter ()
    {
        return EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr (final int limit)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedVolume ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getPan ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedPan ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        return ColorEx.BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMute ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSolo ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getVu ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuLeft ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuRight ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuPeakLeft ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuPeakRight ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (final int control)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (final boolean indicate)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (final int control)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (final boolean indicate)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMutedBySolo ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public ISendBank getSendBank ()
    {
        return EmptySendBank.getInstance (this.sendPageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void addColorObserver (final IValueObserver<ColorEx> observer)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void addDevice (final IDeviceMetadata metadata)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void addEqualizerDevice ()
    {
        // Intentionally empty
    }
}
