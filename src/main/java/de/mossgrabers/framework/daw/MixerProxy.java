// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Mixer;


/**
 * Encapsulates the Mixer instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixerProxy
{
    private Mixer mixer;


    /**
     * Constructor
     *
     * @param host The host
     */
    public MixerProxy (final ControllerHost host)
    {
        this.mixer = host.createMixer ();

        this.mixer.isClipLauncherSectionVisible ().markInterested ();
        this.mixer.isCrossFadeSectionVisible ().markInterested ();
        this.mixer.isDeviceSectionVisible ().markInterested ();
        this.mixer.isIoSectionVisible ().markInterested ();
        this.mixer.isMeterSectionVisible ().markInterested ();
        this.mixer.isSendSectionVisible ().markInterested ();
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.mixer.isClipLauncherSectionVisible ().setIsSubscribed (enable);
        this.mixer.isCrossFadeSectionVisible ().setIsSubscribed (enable);
        this.mixer.isDeviceSectionVisible ().setIsSubscribed (enable);
        this.mixer.isIoSectionVisible ().setIsSubscribed (enable);
        this.mixer.isMeterSectionVisible ().setIsSubscribed (enable);
        this.mixer.isSendSectionVisible ().setIsSubscribed (enable);
    }


    /**
     * Is the clip launcher section visible?
     *
     * @return True if the clip launcher section is visible
     */
    public boolean isClipLauncherSectionVisible ()
    {
        return this.mixer.isClipLauncherSectionVisible ().get ();
    }


    /**
     * Toggle the visibility of the clip launcher section.
     */
    public void toggleClipLauncherSectionVisibility ()
    {
        this.mixer.isClipLauncherSectionVisible ().toggle ();
    }


    /**
     * Is the crossfade section visible?
     *
     * @return True if the crossfade section is visible
     */
    public boolean isCrossFadeSectionVisible ()
    {
        return this.mixer.isCrossFadeSectionVisible ().get ();
    }


    /**
     * Toggles the visibility of the crossfade section in the mixer panel.
     */
    public void toggleCrossFadeSectionVisibility ()
    {
        this.mixer.isCrossFadeSectionVisible ().toggle ();
    }


    /**
     * Is the device section visible?
     *
     * @return True if the device section is visible
     */
    public boolean isDeviceSectionVisible ()
    {
        return this.mixer.isDeviceSectionVisible ().get ();
    }


    /**
     * Toggle the visibility of the device section.
     */
    public void toggleDeviceSectionVisibility ()
    {
        this.mixer.isDeviceSectionVisible ().toggle ();
    }


    /**
     * Is the IO section visible?
     *
     * @return True if the IO section is visible
     */
    public boolean isIoSectionVisible ()
    {
        return this.mixer.isIoSectionVisible ().get ();
    }


    /**
     * Toggle the visibility of the IO section.
     */
    public void toggleIoSectionVisibility ()
    {
        this.mixer.isIoSectionVisible ().toggle ();
    }


    /**
     * Is the meter section visible?
     *
     * @return True if the meter section is visible
     */
    public boolean isMeterSectionVisible ()
    {
        return this.mixer.isMeterSectionVisible ().get ();
    }


    /**
     * Toggle the visibility of the meter section.
     */
    public void toggleMeterSectionVisibility ()
    {
        this.mixer.isMeterSectionVisible ().toggle ();
    }


    /**
     * Is the send section visible?
     *
     * @return True if the send section is visible
     */
    public boolean isSendSectionVisible ()
    {
        return this.mixer.isSendSectionVisible ().get ();
    }


    /**
     * Toggle the visibility of the sends section.
     */
    public void toggleSendsSectionVisibility ()
    {
        this.mixer.isSendSectionVisible ().toggle ();
    }
}