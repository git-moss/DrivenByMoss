// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

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
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IImage;
import de.mossgrabers.framework.osc.IOpenSoundControlCallback;
import de.mossgrabers.framework.osc.IOpenSoundControlClient;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.UsbException;

import com.bitwig.extension.api.graphics.BitmapFormat;
import com.bitwig.extension.api.opensoundcontrol.OscAddressSpace;
import com.bitwig.extension.api.opensoundcontrol.OscModule;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareDevice;
import com.bitwig.extension.controller.api.UsbDevice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Encapsulates the ControllerHost instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HostImpl implements IHost
{
    private ControllerHost   host;
    private List<IUsbDevice> usbDevices = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     */
    public HostImpl (final ControllerHost host)
    {
        this.host = host;
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
        switch (capability)
        {
            case MARKERS:
                return false;

            case NOTE_REPEAT_LENGTH:
            case NOTE_REPEAT_SWING:
            case NOTE_REPEAT_MODE:
            case NOTE_REPEAT_OCTAVES:
            case NOTE_REPEAT_IS_FREE_RUNNING:
            case NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY:
                return true;

            case NOTE_EDIT_RELEASE_VELOCITY:
            case NOTE_EDIT_PRESSURE:
            case NOTE_EDIT_TIMBRE:
            case NOTE_EDIT_PANORAMA:
            case NOTE_EDIT_TRANSPOSE:
            case NOTE_EDIT_GAIN:
                return true;

            case QUANTIZE_INPUT_NOTE_LENGTH:
            case QUANTIZE_AMOUNT:
                return true;

            case CUE_VOLUME:
                return true;

            case HAS_SLOT_CHAINS:
            case HAS_DRUM_DEVICE:
            case HAS_CROSSFADER:
            case HAS_PINNING:
            case HAS_EFFECT_BANK:
                return true;
        }
        return false;
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
    public IOpenSoundControlMessage createOSCMessage (final String address, final List<Object> values)
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
}
