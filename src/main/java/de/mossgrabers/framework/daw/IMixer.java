// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.observer.IObserverManagement;


/**
 * Encapsulates the Mixer instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMixer extends IObserverManagement
{
    /**
     * Is the clip launcher section visible?
     *
     * @return True if the clip launcher section is visible
     */
    boolean isClipLauncherSectionVisible ();


    /**
     * Toggle the visibility of the clip launcher section.
     */
    void toggleClipLauncherSectionVisibility ();


    /**
     * Is the crossfade section visible?
     *
     * @return True if the crossfade section is visible
     */
    boolean isCrossFadeSectionVisible ();


    /**
     * Toggles the visibility of the crossfade section in the mixer panel.
     */
    void toggleCrossFadeSectionVisibility ();


    /**
     * Is the device section visible?
     *
     * @return True if the device section is visible
     */
    boolean isDeviceSectionVisible ();


    /**
     * Toggle the visibility of the device section.
     */
    void toggleDeviceSectionVisibility ();


    /**
     * Is the IO section visible?
     *
     * @return True if the IO section is visible
     */
    boolean isIoSectionVisible ();


    /**
     * Toggle the visibility of the IO section.
     */
    void toggleIoSectionVisibility ();


    /**
     * Is the meter section visible?
     *
     * @return True if the meter section is visible
     */
    boolean isMeterSectionVisible ();


    /**
     * Toggle the visibility of the meter section.
     */
    void toggleMeterSectionVisibility ();


    /**
     * Is the send section visible?
     *
     * @return True if the send section is visible
     */
    boolean isSendSectionVisible ();


    /**
     * Toggle the visibility of the sends section.
     */
    void toggleSendsSectionVisibility ();
}