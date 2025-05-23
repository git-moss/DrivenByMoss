// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The interface to a channel.
 *
 * @author Jürgen Moßgraber
 */
public interface IChannel extends IItem
{
    /**
     * Returns true if the channel is activated.
     *
     * @return True if the channel is activated
     */
    boolean isActivated ();


    /**
     * Get the type of the channel.
     *
     * @return The type
     */
    ChannelType getType ();


    /**
     * Get the volume parameter.
     *
     * @return The volume parameter
     */
    IParameter getVolumeParameter ();


    /**
     * Get the volume as a formatted text.
     *
     * @return The volume text
     */
    String getVolumeStr ();


    /**
     * Get the volume as a formatted text.
     *
     * @param limit Limit the text to this length
     * @return The volume text
     */
    String getVolumeStr (int limit);


    /**
     * Get the volume.
     *
     * @return The volume
     */
    int getVolume ();


    /**
     * Change the volume.
     *
     * @param control The control value
     */
    void changeVolume (int control);


    /**
     * Set the volume.
     *
     * @param value The new value
     */
    void setVolume (int value);


    /**
     * Reset the volume to its default value.
     */
    void resetVolume ();


    /**
     * Signal that the volume fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchVolume (boolean isBeingTouched);


    /**
     * Signal that the volume is edited.
     *
     * @param indicate True if edited
     */
    void setVolumeIndication (boolean indicate);


    /**
     * Get the modulated volume.
     *
     * @return The modulated volume
     */
    int getModulatedVolume ();


    /**
     * Get the panning parameter.
     *
     * @return The panning parameter
     */
    IParameter getPanParameter ();


    /**
     * Get the panning as a formatted text
     *
     * @return The ppanning text
     */
    String getPanStr ();


    /**
     * Get the panning as a formatted text
     *
     * @param limit Limit the text to this length
     * @return The panning text
     */
    String getPanStr (int limit);


    /**
     * Get the panning.
     *
     * @return The panning
     */
    int getPan ();


    /**
     * Change the panning.
     *
     * @param control The control value
     */
    void changePan (int control);


    /**
     * Set the panning.
     *
     * @param value The new value
     */
    void setPan (int value);


    /**
     * Reset the panning to its default value.
     */
    void resetPan ();


    /**
     * Signal that the panning fader/knob is touched for automation recording.
     *
     * @param isBeingTouched True if touched
     */
    void touchPan (boolean isBeingTouched);


    /**
     * Signal that the panning is edited.
     *
     * @param indicate True if edited
     */
    void setPanIndication (boolean indicate);


    /**
     * Get the modulated panning.
     *
     * @return The modulated panning
     */
    int getModulatedPan ();


    /**
     * Sets the activated state of the channel.
     *
     * @param value True to activate
     */
    void setIsActivated (boolean value);


    /**
     * Toggle the activated state of the channel.
     */
    void toggleIsActivated ();


    /**
     * Get the color of the channel.
     *
     * @return The color in RGB
     */
    ColorEx getColor ();


    /**
     * Set the color of the channel as a RGB value.
     *
     * @param color The color
     */
    void setColor (ColorEx color);


    /**
     * Get the mute parameter.
     *
     * @return The mute parameter
     */
    IParameter getMuteParameter ();


    /**
     * True if muted.
     *
     * @return True if muted.
     */
    boolean isMute ();


    /**
     * Turn on/off mute.
     *
     * @param value True to turn on mute, otherwise off
     */
    void setMute (boolean value);


    /**
     * Toggle mute.
     */
    void toggleMute ();


    /**
     * True if soloed.
     *
     * @return True if soloed.
     */
    boolean isSolo ();


    /**
     * Get the solo parameter.
     *
     * @return The solo parameter
     */
    IParameter getSoloParameter ();


    /**
     * Turn on/off solo.
     *
     * @param value True to turn on solo, otherwise off
     */
    void setSolo (boolean value);


    /**
     * Toggle solo.
     */
    void toggleSolo ();


    /**
     * True if muted by another tracks' solo.
     *
     * @return True if muted.
     */
    boolean isMutedBySolo ();


    /**
     * Get the mono VU value.
     *
     * @return The VU value
     */
    int getVu ();


    /**
     * Get the clip state of the mono VU meter.
     *
     * @return True if clipped
     */
    boolean getVuClipState ();


    /**
     * Get the left VU value.
     *
     * @return The left VU value
     */
    int getVuLeft ();


    /**
     * Get the clip state of the left VU meter.
     *
     * @return True if clipped
     */
    boolean getVuLeftClipState ();


    /**
     * Get the right VU value.
     *
     * @return The right VU value
     */
    int getVuRight ();


    /**
     * Get the clip state of the right VU meter.
     *
     * @return True if clipped
     */
    boolean getVuRightClipState ();


    /**
     * Get the maximum peak of the left VU value. This takes the maximum of the left VU value since
     * the first call. If a volume change happened since the last call the value is reset.
     *
     * @return The left VU value peak
     */
    int getVuPeakLeft ();


    /**
     * Get the maximum peak of the right VU value. This takes the maximum of the right VU value
     * since the first call. If a volume change happened since the last call the value is reset.
     *
     * @return The right VU value peak
     */
    int getVuPeakRight ();


    /**
     * Delete the channel.
     */
    void remove ();


    /**
     * Duplicate the channel.
     */
    void duplicate ();


    /**
     * Get the send bank.
     *
     * @return The send bank
     */
    ISendBank getSendBank ();


    /**
     * Enter sub-channels of the channel, if any.
     */
    void enter ();


    /**
     * Add an observer for the color.
     *
     * @param observer The observer to notify on a color change
     */
    void addColorObserver (IValueObserver<ColorEx> observer);


    /**
     * Add a device at the end of the channels' device chain.
     *
     * @param metadata The metadata of the device to add
     */
    void addDevice (IDeviceMetadata metadata);


    /**
     * Add an equalizer device to this channels' device chain.
     */
    void addEqualizerDevice ();
}