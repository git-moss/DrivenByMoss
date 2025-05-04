// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.mode;

import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlColorManager;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlMessage;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;


/**
 * Fills the ROTO CONTROL displays with track related data.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlDisplay
{
    private static final MessageDigest SHA1_DIGEST;
    static
    {
        try
        {
            SHA1_DIGEST = MessageDigest.getInstance ("SHA-1");
        }
        catch (final NoSuchAlgorithmException ex)
        {
            throw new RuntimeException (ex);
        }
    }

    /** The maximum length of a (name) string including the final zero byte. */
    public static final int                 MAX_NAME_LENGTH          = 13;

    private int                             trackCountCache          = -1;
    private int                             sendsCountCache          = -1;
    private int                             firstTrackPositionCache  = -1;
    private final String []                 trackNameCache           = new String [8];
    private final int []                    trackColorCache          = new int [8];

    private int                             deviceCountCache         = -1;
    private int                             firstDevicePositionCache = -1;
    private int                             selectedDeviceIndexCache = -1;
    private final String []                 deviceNameCache          = new String [8];

    private final RotoControlControlSurface surface;
    private final IModel                    model;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public RotoControlDisplay (final RotoControlControlSurface surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
    }


    /**
     * Flush the track display cache.
     */
    public void flushTrackDisplay ()
    {
        this.trackCountCache = -1;
        this.sendsCountCache = -1;
        this.firstTrackPositionCache = -1;
        this.selectedDeviceIndexCache = -1;
        Arrays.fill (this.trackNameCache, null);
        Arrays.fill (this.trackColorCache, -1);
    }


    /**
     * Send all necessary track related data to the ROTO CONTROL.
     */
    public void updateTrackDisplay ()
    {
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        final int trackCount = Math.min (64, trackBank.getItemCount ());
        boolean updateAll = false;
        if (trackCount != this.trackCountCache)
        {
            updateAll = true;

            this.trackCountCache = trackCount;
            this.surface.sendSysex (RotoControlMessage.GENERAL, RotoControlMessage.TR_NUM_TRACKS, trackCount);
        }

        int sendsCount = 0;
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent ())
        {
            final ISendBank sendBank = selectedTrack.get ().getSendBank ();
            sendsCount = Math.min (127, sendBank.getItemCount ());
        }
        if (updateAll || sendsCount != this.sendsCountCache)
        {
            this.sendsCountCache = sendsCount;
            this.surface.sendSysex (RotoControlMessage.MIX, RotoControlMessage.TR_NUM_SENDS, sendsCount);
        }

        final int firstTrackPosition = trackBank.getScrollPosition ();
        if (updateAll || firstTrackPosition != this.firstTrackPositionCache)
        {
            this.firstTrackPositionCache = firstTrackPosition;
            this.surface.sendSysex (RotoControlMessage.GENERAL, RotoControlMessage.TR_FIRST_TRACK, firstTrackPosition);
        }

        int hasBeenUpdated = 0;
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = trackBank.getItem (i);
            if (!track.doesExist ())
                continue;
            final String name = create13ByteTextArray (track.getName ());
            final int colorIndex = ColorEx.getClosestColorIndex (track.getColor (), RotoControlColorManager.DEFAULT_PALETTE);

            if (updateAll || !name.equals (this.trackNameCache[i]) || this.trackColorCache[i] != colorIndex)
            {
                this.trackNameCache[i] = name;
                this.trackColorCache[i] = colorIndex;
                this.sendTrackDetails (track.getPosition (), name, colorIndex);
                hasBeenUpdated++;
            }
        }
        if (updateAll && trackCount < 8 && trackCount != hasBeenUpdated)
            this.trackCountCache = -1;
        if (updateAll)
        {
            // If tracks have been added or removed, ensure that the button mode is updated as well
            for (final IHwButton button: this.surface.getButtons ().values ())
                button.getLight ().forceFlush ();
        }

        if (hasBeenUpdated > 0)
            this.surface.sendSysex (RotoControlMessage.GENERAL, RotoControlMessage.TR_TRACK_DETAILS_END);
    }


    /**
     * Flush the device display cache.
     */
    public void flushDeviceDisplay ()
    {
        this.deviceCountCache = -1;
        this.firstDevicePositionCache = -1;
        Arrays.fill (this.deviceNameCache, null);
    }


    /**
     * Send all necessary device related data to the ROTO CONTROL.
     */
    public void updateDeviceDisplay ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
        final int deviceCount = Math.min (127, deviceBank.getItemCount ());
        if (deviceCount != this.deviceCountCache)
        {
            this.deviceCountCache = deviceCount;
            this.surface.sendSysex (RotoControlMessage.PLUGIN, RotoControlMessage.TR_NUM_PLUGINS, deviceCount);
        }

        final int firstDevicePosition = deviceBank.getScrollPosition ();
        if (firstDevicePosition != this.firstDevicePositionCache)
        {
            this.firstDevicePositionCache = firstDevicePosition;
            this.surface.sendSysex (RotoControlMessage.PLUGIN, RotoControlMessage.TR_FIRST_PLUGIN, firstDevicePosition);
        }

        try
        {
            boolean hasBeenUpdated = false;
            for (int i = 0; i < 8; i++)
            {
                final IDevice device = deviceBank.getItem (i);
                final String name = create13ByteTextArray (device.getName ());
                if (!name.equals (this.deviceNameCache[i]))
                {
                    this.deviceNameCache[i] = name;

                    final ByteArrayOutputStream out = new ByteArrayOutputStream ();
                    out.write (device.getPosition ());
                    final byte [] pluginHash = createHash (name, 8);
                    out.write (pluginHash);
                    out.write (device.isEnabled () ? 1 : 0);
                    out.write (name.getBytes ());
                    this.surface.sendSysex (RotoControlMessage.PLUGIN, RotoControlMessage.TR_PLUGIN_DETAILS, out.toByteArray ());

                    hasBeenUpdated = true;
                }
            }

            if (hasBeenUpdated)
                this.surface.sendSysex (RotoControlMessage.PLUGIN, RotoControlMessage.TR_PLUGIN_DETAILS_END);
        }
        catch (final IOException ex)
        {
            // Can never happen
        }

        final int selectedDeviceIndex = cursorDevice.getIndex ();
        if (selectedDeviceIndex != this.selectedDeviceIndexCache && selectedDeviceIndex >= 0)
        {
            this.selectedDeviceIndexCache = selectedDeviceIndex;
            this.surface.sendSysex (RotoControlMessage.PLUGIN, RotoControlMessage.TR_DAW_SELECT_PLUGIN, selectedDeviceIndex);
        }
    }


    /**
     * Creates a hash from a string. Each byte contains only 7-bit.
     *
     * @param input The string to hash
     * @param hashSize The number of bytes of the hash
     * @return The hash value
     */
    public static byte [] createHash (final String input, final int hashSize)
    {
        final byte [] fullHash = SHA1_DIGEST.digest (input.getBytes (StandardCharsets.UTF_8));
        final byte [] pluginDigest = new byte [hashSize];
        for (int i = 0; i < hashSize; i++)
            pluginDigest[i] = (byte) (fullHash[i] & 0x7f);
        return pluginDigest;
    }


    /**
     * Converts the given text to ASCII and pads it with zero bytes.
     *
     * @param text The text
     * @return The text bytes with at least 1 closing zero byte
     */
    public static String create13ByteTextArray (final String text)
    {
        return StringUtils.pad (StringUtils.shortenAndFixASCII (text, RotoControlDisplay.MAX_NAME_LENGTH - 1), RotoControlDisplay.MAX_NAME_LENGTH, (char) 0);
    }


    private void sendTrackDetails (final int position, final String name, final int colorIndex)
    {
        try
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream ();
            out.write (position);
            out.write (name.getBytes ());
            out.write (colorIndex);
            this.surface.sendSysex (RotoControlMessage.GENERAL, RotoControlMessage.TR_TRACK_DETAILS, out.toByteArray ());
        }
        catch (final IOException ex)
        {
            // Can never happen
        }
    }
}
