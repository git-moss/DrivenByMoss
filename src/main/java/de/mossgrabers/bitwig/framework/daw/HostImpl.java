// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.DeviceMetadataImpl.PluginType;
import de.mossgrabers.bitwig.framework.graphics.BitmapImpl;
import de.mossgrabers.bitwig.framework.graphics.ImageImpl;
import de.mossgrabers.bitwig.framework.hardware.HwSurfaceFactoryImpl;
import de.mossgrabers.bitwig.framework.osc.OpenSoundControlClientImpl;
import de.mossgrabers.bitwig.framework.osc.OpenSoundControlMessageImpl;
import de.mossgrabers.bitwig.framework.osc.OpenSoundControlServerImpl;
import de.mossgrabers.bitwig.framework.usb.UsbDeviceImpl;
import de.mossgrabers.framework.controller.hardware.IHwSurfaceFactory;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IImage;
import de.mossgrabers.framework.osc.IOpenSoundControlCallback;
import de.mossgrabers.framework.osc.IOpenSoundControlClient;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.UsbException;
import de.mossgrabers.framework.utils.ConsoleLogger;

import com.bitwig.extension.api.graphics.BitmapFormat;
import com.bitwig.extension.api.opensoundcontrol.OscAddressSpace;
import com.bitwig.extension.api.opensoundcontrol.OscModule;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareDevice;
import com.bitwig.extension.controller.api.UsbDevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Encapsulates the ControllerHost instance.
 *
 * @author Jürgen Moßgraber
 */
public class HostImpl implements IHost
{
    private static final List<IDeviceMetadata> INSTRUMENT_METADATA    = new ArrayList<> ();
    private static final List<IDeviceMetadata> AUDIO_EFFECTS_METADATA = new ArrayList<> ();
    private static final Set<Capability>       CAPABILITIES           = new HashSet<> ();

    static
    {
        CAPABILITIES.add (Capability.NOTE_REPEAT_LENGTH);
        CAPABILITIES.add (Capability.NOTE_REPEAT_SWING);
        CAPABILITIES.add (Capability.NOTE_REPEAT_MODE);
        CAPABILITIES.add (Capability.NOTE_REPEAT_OCTAVES);
        CAPABILITIES.add (Capability.NOTE_REPEAT_IS_FREE_RUNNING);
        CAPABILITIES.add (Capability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY);
        CAPABILITIES.add (Capability.NOTE_REPEAT_LATCH);

        CAPABILITIES.add (Capability.QUANTIZE_INPUT_NOTE_LENGTH);
        CAPABILITIES.add (Capability.QUANTIZE_AMOUNT);

        CAPABILITIES.add (Capability.CUE_VOLUME);

        CAPABILITIES.add (Capability.HAS_SLOT_CHAINS);
        CAPABILITIES.add (Capability.HAS_DRUM_DEVICE);
        CAPABILITIES.add (Capability.HAS_CROSSFADER);
        CAPABILITIES.add (Capability.HAS_PINNING);
        CAPABILITIES.add (Capability.HAS_PARAMETER_PAGE_SECTION);
        CAPABILITIES.add (Capability.HAS_EFFECT_BANK);
        CAPABILITIES.add (Capability.HAS_BROWSER_PREVIEW);
    }

    private final ControllerHost   host;
    private final List<IUsbDevice> usbDevices = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     */
    public HostImpl (final ControllerHost host)
    {
        this.host = host;

        readDeviceFiles ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Bitwig";
    }


    /** {@inheritDoc} */
    @Override
    public boolean supports (final Capability capability)
    {
        return CAPABILITIES.contains (capability);
    }


    /** {@inheritDoc} */
    @Override
    public boolean supports (final NoteAttribute noteAttribute)
    {
        // All note attributes are supported
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void restart ()
    {
        this.host.restart ();
    }


    /** {@inheritDoc} */
    @Override
    public void scheduleTask (final Runnable task, final long delay)
    {
        this.host.scheduleTask (task, delay);
    }


    /** {@inheritDoc} */
    @Override
    public void error (final String text)
    {
        this.host.errorln (text);
    }


    /** {@inheritDoc} */
    @Override
    public void error (final String text, final Throwable ex)
    {
        this.host.errorln (text);

        final StringWriter sw = new StringWriter ();
        final PrintWriter writer = new PrintWriter (sw);
        ex.printStackTrace (writer);
        this.host.errorln (sw.toString ());
    }


    /** {@inheritDoc} */
    @Override
    public void println (final String text)
    {
        this.host.println (text);
    }


    /** {@inheritDoc} */
    @Override
    public void showNotification (final String message)
    {
        this.host.showPopupNotification (message);
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlClient connectToOSCServer (final String serverAddress, final int serverPort)
    {
        final OscModule oscModule = this.host.getOscModule ();
        return new OpenSoundControlClientImpl (oscModule.connectToUdpServer (serverAddress, serverPort, oscModule.createAddressSpace ()));
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlServer createOSCServer (final IOpenSoundControlCallback callback)
    {
        final OscModule oscModule = this.host.getOscModule ();
        final OscAddressSpace addressSpace = oscModule.createAddressSpace ();
        addressSpace.registerDefaultMethod ( (source, message) -> callback.handle (new OpenSoundControlMessageImpl (message)));
        return new OpenSoundControlServerImpl (oscModule.createUdpServer (addressSpace));
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlMessage createOSCMessage (final String address, final List<?> values)
    {
        return new OpenSoundControlMessageImpl (address, values);
    }


    /** {@inheritDoc} */
    @Override
    public void releaseOSC ()
    {
        // This is automatically handled by the Bitwig framework
    }


    /** {@inheritDoc} */
    @Override
    public IImage loadSVG (final String path, final int scale)
    {
        return new ImageImpl (this.host.loadSVG (path, scale));
    }


    /** {@inheritDoc} */
    @Override
    public IBitmap createBitmap (final int width, final int height)
    {
        return new BitmapImpl (this.host.createBitmap (width, height, BitmapFormat.ARGB32));
    }


    /** {@inheritDoc} */
    @Override
    public IMemoryBlock createMemoryBlock (final int size)
    {
        return new MemoryBlockImpl (this.host.allocateMemoryBlock (size));
    }


    /** {@inheritDoc} */
    @Override
    public IUsbDevice getUsbDevice (final int index) throws UsbException
    {
        try
        {
            final HardwareDevice hardwareDevice = this.host.hardwareDevice (index);
            final UsbDeviceImpl usbDevice = new UsbDeviceImpl (this, (UsbDevice) hardwareDevice);
            this.usbDevices.add (usbDevice);
            return usbDevice;
        }
        catch (final RuntimeException ex)
        {
            throw new UsbException ("Could not lookup or open the device.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void releaseUsbDevices ()
    {
        for (final IUsbDevice usbDevice: this.usbDevices)
            usbDevice.release ();
        this.usbDevices.clear ();
    }


    /** {@inheritDoc} */
    @Override
    public IHwSurfaceFactory createSurfaceFactory (final double width, final double height)
    {
        return new HwSurfaceFactoryImpl (this, width, height);
    }


    /**
     * Get the Bitwig controller host.
     *
     * @return The host
     */
    public ControllerHost getControllerHost ()
    {
        return this.host;
    }


    /** {@inheritDoc} */
    @Override
    public List<IDeviceMetadata> getInstrumentMetadata ()
    {
        return new ArrayList<> (INSTRUMENT_METADATA);
    }


    /** {@inheritDoc} */
    @Override
    public List<IDeviceMetadata> getAudioEffectMetadata ()
    {
        return new ArrayList<> (AUDIO_EFFECTS_METADATA);
    }


    private static void readDeviceFiles ()
    {
        synchronized (INSTRUMENT_METADATA)
        {
            if (!INSTRUMENT_METADATA.isEmpty ())
                return;

            readDeviceFile ("Instruments.txt").forEach (line -> {
                final Optional<IDeviceMetadata> dm = parseDeviceLine (line);
                if (dm.isPresent ())
                    INSTRUMENT_METADATA.add (dm.get ());
            });
            readDeviceFile ("AudioEffects.txt").forEach (line -> {
                final Optional<IDeviceMetadata> dm = parseDeviceLine (line);
                if (dm.isPresent ())
                    AUDIO_EFFECTS_METADATA.add (dm.get ());
            });
        }
    }


    private static Optional<IDeviceMetadata> parseDeviceLine (final String line)
    {
        // Ignore comments
        if (line.startsWith ("#"))
            return Optional.empty ();

        final String [] parts = line.split ("\\$");
        if (parts.length != 3)
        {
            ConsoleLogger.log ("Could not parse device line. Wrong number of parts: " + line);
            return Optional.empty ();
        }

        try
        {
            final PluginType type = PluginType.valueOf (parts[0]);
            return Optional.of (new DeviceMetadataImpl (parts[1], parts[2], type));
        }
        catch (final IllegalArgumentException ex)
        {
            ConsoleLogger.log ("Could not parse device line. Wrong type argument: " + line);
            return Optional.empty ();
        }
    }


    private static List<String> readDeviceFile (final String fileName)
    {
        try (final BufferedReader reader = new BufferedReader (new InputStreamReader (HostImpl.class.getClassLoader ().getResourceAsStream ("devices/" + fileName))))
        {
            return reader.lines ().toList ();
        }
        catch (final IOException ex)
        {
            ConsoleLogger.log ("Could not load device file: " + fileName);
            return Collections.emptyList ();
        }
    }
}
