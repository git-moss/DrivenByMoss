// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.daw.IMixer;

import com.bitwig.extension.controller.api.Mixer;


/**
 * Interface to the Mixer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixerImpl implements IMixer
{
    private final Mixer mixer;


    /**
     * Constructor
     *
     * @param mixer The mixer
     */
    public MixerImpl (final Mixer mixer)
    {
        this.mixer = mixer;

        this.mixer.isClipLauncherSectionVisible ().markInterested ();
        this.mixer.isCrossFadeSectionVisible ().markInterested ();
        this.mixer.isDeviceSectionVisible ().markInterested ();
        this.mixer.isIoSectionVisible ().markInterested ();
        this.mixer.isMeterSectionVisible ().markInterested ();
        this.mixer.isSendSectionVisible ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.mixer.isClipLauncherSectionVisible (), enable);
        Util.setIsSubscribed (this.mixer.isCrossFadeSectionVisible (), enable);
        Util.setIsSubscribed (this.mixer.isDeviceSectionVisible (), enable);
        Util.setIsSubscribed (this.mixer.isIoSectionVisible (), enable);
        Util.setIsSubscribed (this.mixer.isMeterSectionVisible (), enable);
        Util.setIsSubscribed (this.mixer.isSendSectionVisible (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipLauncherSectionVisible ()
    {
        return this.mixer.isClipLauncherSectionVisible ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleClipLauncherSectionVisibility ()
    {
        this.mixer.isClipLauncherSectionVisible ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCrossFadeSectionVisible ()
    {
        return this.mixer.isCrossFadeSectionVisible ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCrossFadeSectionVisibility ()
    {
        this.mixer.isCrossFadeSectionVisible ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDeviceSectionVisible ()
    {
        return this.mixer.isDeviceSectionVisible ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDeviceSectionVisibility ()
    {
        this.mixer.isDeviceSectionVisible ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isIoSectionVisible ()
    {
        return this.mixer.isIoSectionVisible ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIoSectionVisibility ()
    {
        this.mixer.isIoSectionVisible ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMeterSectionVisible ()
    {
        return this.mixer.isMeterSectionVisible ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMeterSectionVisibility ()
    {
        this.mixer.isMeterSectionVisible ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSendSectionVisible ()
    {
        return this.mixer.isSendSectionVisible ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSendsSectionVisibility ()
    {
        this.mixer.isSendSectionVisible ().toggle ();
    }
}