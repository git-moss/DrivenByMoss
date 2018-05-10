// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.usb.IUSBDevice;
import de.mossgrabers.framework.usb.IUSBEndpoint;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * USB connection for display and UI controls of Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2USBDevice
{

    private static final int []                BYTE_0             =
    {
        Kontrol2ControlSurface.BUTTON_MAIN_ENCODER,
        Kontrol2ControlSurface.BUTTON_PRESET_UP,
        Kontrol2ControlSurface.BUTTON_ENTER,
        Kontrol2ControlSurface.BUTTON_PRESET_DOWN,
        Kontrol2ControlSurface.BUTTON_BROWSE,
        Kontrol2ControlSurface.BUTTON_INSTANCE,
        Kontrol2ControlSurface.BUTTON_OCTAVE_DOWN,
        Kontrol2ControlSurface.BUTTON_OCTAVE_UP
    };

    private static final int []                BYTE_1             =
    {
        Kontrol2ControlSurface.BUTTON_STOP,
        Kontrol2ControlSurface.BUTTON_REC,
        Kontrol2ControlSurface.BUTTON_PLAY,
        Kontrol2ControlSurface.BUTTON_NAVIGATE_RIGHT,
        Kontrol2ControlSurface.BUTTON_NAVIGATE_DOWN,
        Kontrol2ControlSurface.BUTTON_NAVIGATE_LEFT,
        Kontrol2ControlSurface.BUTTON_BACK,
        Kontrol2ControlSurface.BUTTON_NAVIGATE_UP
    };

    private static final int []                BYTE_2             =
    {
        Kontrol2ControlSurface.BUTTON_SHIFT,
        Kontrol2ControlSurface.BUTTON_SCALE,
        Kontrol2ControlSurface.BUTTON_ARP,
        Kontrol2ControlSurface.BUTTON_LOOP,
        Kontrol2ControlSurface.BUTTON_PAGE_RIGHT,
        Kontrol2ControlSurface.BUTTON_PAGE_LEFT,
        Kontrol2ControlSurface.BUTTON_RWD,
        Kontrol2ControlSurface.BUTTON_FWD
    };

    private static final int []                BYTE_3             =
    {
        Kontrol2ControlSurface.TOUCH_ENCODER_1,
        Kontrol2ControlSurface.TOUCH_ENCODER_2,
        Kontrol2ControlSurface.TOUCH_ENCODER_3,
        Kontrol2ControlSurface.TOUCH_ENCODER_4,
        Kontrol2ControlSurface.TOUCH_ENCODER_5,
        Kontrol2ControlSurface.TOUCH_ENCODER_6,
        Kontrol2ControlSurface.TOUCH_ENCODER_7,
        Kontrol2ControlSurface.TOUCH_ENCODER_8
    };

    private static final int []                BYTE_4             =
    {
        Kontrol2ControlSurface.TOUCH_ENCODER_MAIN
    };

    private static final int []                TEST_BITS          =
    {
        0x01,
        0x02,
        0x04,
        0x08,
        0x10,
        0x20,
        0x40,
        0x80
    };

    private final static int                   DATA_SZ            = 249;
    private static final int                   TIMEOUT            = 1000;
    private static final int                   MESSAGE_SIZE       = 49;

    private final static Map<Integer, Integer> LED_MAPPING        = new HashMap<> (21);

    private IHost                              host;
    private IUSBDevice                         usbDevice;
    private IUSBEndpoint                       usbEndpointDisplay;
    private IUSBEndpoint                       usbEndpointUI;

    private ByteBuffer                         initBuffer;
    private ByteBuffer                         displayBuffer;
    private ByteBuffer                         uiBuffer;
    private ByteBuffer                         ledBuffer;
    private ByteBuffer                         keyLedBuffer;

    private boolean                            busySendingDisplay = false;
    private boolean                            busySendingLEDs    = false;
    private boolean                            busySendingKeyLEDs = false;

    private int                                mainEncoderValue;
    private int []                             encoderValues      = new int [8];

    private byte []                            buttonStates       = new byte [21];
    private byte []                            oldButtonStates    = new byte [21];

    private byte []                            keyColors          = new byte [88 * 3];
    private byte []                            oldKeyColors       = new byte [88 * 3];

    private boolean                            isFirstStateMsg    = true;

    private UIChangeCallback                   callback;

    static
    {
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_SHIFT), Integer.valueOf (0));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_SCALE), Integer.valueOf (1));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_ARP), Integer.valueOf (2));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_LOOP), Integer.valueOf (3));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_RWD), Integer.valueOf (4));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_FWD), Integer.valueOf (5));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PLAY), Integer.valueOf (6));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_REC), Integer.valueOf (7));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_STOP), Integer.valueOf (8));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PAGE_LEFT), Integer.valueOf (9));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PAGE_RIGHT), Integer.valueOf (10));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_BROWSE), Integer.valueOf (11));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PRESET_UP), Integer.valueOf (12));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_INSTANCE), Integer.valueOf (13));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PRESET_DOWN), Integer.valueOf (14));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_BACK), Integer.valueOf (15));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_NAVIGATE_UP), Integer.valueOf (16));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_ENTER), Integer.valueOf (17));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_NAVIGATE_LEFT), Integer.valueOf (18));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_NAVIGATE_DOWN), Integer.valueOf (19));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_NAVIGATE_RIGHT), Integer.valueOf (20));
    }


    /**
     * Constructor.
     *
     * @param host The controller host
     */
    public Kontrol2USBDevice (final IHost host)
    {
        this.host = host;

        try
        {
            this.usbDevice = host.getUsbDevice (0);
            this.usbEndpointUI = this.usbDevice.getEndpoint (0, 0);
            this.usbEndpointDisplay = this.usbDevice.getEndpoint (1, 0);
        }
        catch (final RuntimeException ex)
        {
            this.usbDevice = null;
            this.usbEndpointDisplay = null;
            this.usbEndpointUI = null;
            host.error ("Could not open USB connection: " + ex.getMessage ());
        }

        this.displayBuffer = host.createByteBuffer (DATA_SZ);
        this.uiBuffer = host.createByteBuffer (1024);
        this.ledBuffer = host.createByteBuffer (26);
        this.keyLedBuffer = host.createByteBuffer (267);
        this.initBuffer = host.createByteBuffer (3);
        this.initBuffer.put ((byte) 0xA0);
        this.initBuffer.put ((byte) 0x00);
        this.initBuffer.put ((byte) 0x00);

        // To send black LEDs on startup
        this.oldKeyColors[0] = -1;
    }


    /**
     * Callback function for device control changes.
     *
     * @param callback The callback
     */
    public void setCallback (final UIChangeCallback callback)
    {
        this.callback = callback;
    }


    /**
     * Send the initialization message to the device.
     */
    public void init ()
    {
        if (this.usbEndpointDisplay != null)
            this.usbEndpointDisplay.send (this.initBuffer, TIMEOUT);
    }


    /**
     * Poll the user interface controls.
     */
    public void pollUI ()
    {
        if (this.usbEndpointUI == null)
            return;

        this.usbEndpointUI.sendAsync (this.uiBuffer, resultLength -> {
            if (resultLength > 0)
                this.processMessage (resultLength);
            this.uiBuffer.clear ();
            this.host.scheduleTask (this::pollUI, 10);
        }, TIMEOUT);
    }


    /**
     * Send all display data to the device.
     */
    public void sendDisplayData ()
    {
        if (this.busySendingDisplay || this.usbEndpointDisplay == null)
            return;

        // TODO
        // Use this.displayBuffer
    }


    /**
     * Stop sending USB data.
     */
    public void shutdown ()
    {
        this.usbEndpointDisplay = null;
    }


    /**
     * Set a button LED.
     *
     * @param buttonID The ID of the button
     * @param intensity 0-255 The light intensity (0 is off)
     */
    public void setButtonLED (final int buttonID, final int intensity)
    {
        final Integer pos = LED_MAPPING.get (Integer.valueOf (buttonID));
        if (pos == null)
        {
            this.host.error ("Illegal button LED: " + buttonID);
            return;
        }

        this.buttonStates[pos.intValue ()] = (byte) intensity;
    }


    /**
     * Send the LED stati updates to the device.
     */
    public void updateButtonLEDs ()
    {
        if (this.usbEndpointDisplay == null)
            return;

        if (this.busySendingLEDs || Arrays.equals (this.oldButtonStates, this.buttonStates))
            return;
        System.arraycopy (this.buttonStates, 0, this.oldButtonStates, 0, this.oldButtonStates.length);

        this.ledBuffer.clear ();
        this.ledBuffer.put ((byte) 0x80);
        this.ledBuffer.put (this.buttonStates);
        this.ledBuffer.put ((byte) 0);
        this.ledBuffer.put ((byte) 0);
        this.ledBuffer.put ((byte) 0);
        this.ledBuffer.put ((byte) 0);

        this.busySendingLEDs = true;
        this.usbEndpointDisplay.send (this.ledBuffer, TIMEOUT);
        this.busySendingLEDs = false;
    }


    /**
     * Set a key LED. Note that key 0 is always the first key of the Sxx, which means they are
     * different for the different models. Furthermore, this is independent from the octave
     * transposition.
     *
     * @param key The index of the key 0-87
     * @param red The red value 0-255
     * @param green The green value 0-255
     * @param blue The blue value 0-255
     */
    public void setKeyLED (final int key, final int red, final int green, final int blue)
    {
        if (key < 0 || key >= 88)
            return;
        final int pos = 3 * key;
        this.keyColors[pos] = (byte) red;
        this.keyColors[pos + 1] = (byte) green;
        this.keyColors[pos + 2] = (byte) blue;
    }


    /**
     * Send the key LED stati updates to the device.
     */
    public void updateKeyLEDs ()
    {
        if (this.usbEndpointDisplay == null)
            return;

        if (this.busySendingKeyLEDs || Arrays.equals (this.oldKeyColors, this.keyColors))
            return;
        System.arraycopy (this.keyColors, 0, this.oldKeyColors, 0, this.oldKeyColors.length);

        this.keyLedBuffer.clear ();
        this.keyLedBuffer.put ((byte) 0x82);
        this.keyLedBuffer.put (this.keyColors);
        this.keyLedBuffer.put ((byte) 0x0);
        this.keyLedBuffer.put ((byte) 0x0);

        this.busySendingKeyLEDs = true;
        this.usbEndpointDisplay.send (this.keyLedBuffer, TIMEOUT);
        this.busySendingKeyLEDs = false;
    }


    private void processMessage (final int received)
    {
        this.uiBuffer.rewind ();

        final byte [] dst = new byte [MESSAGE_SIZE];

        for (int i = 0; i < received / MESSAGE_SIZE; i++)
        {
            this.uiBuffer.get (dst);

            boolean encoderChange = false;

            // Decode main knob
            final int currentEncoderValue = Byte.toUnsignedInt (dst[6]);
            if (currentEncoderValue != this.mainEncoderValue)
            {
                final boolean valueIncreased = (this.mainEncoderValue < currentEncoderValue || this.mainEncoderValue == 0x0F && currentEncoderValue == 0) && !(this.mainEncoderValue == 0 && currentEncoderValue == 0x0F);
                this.mainEncoderValue = currentEncoderValue;
                if (!this.isFirstStateMsg)
                    this.callback.mainEncoderChanged (valueIncreased);
                encoderChange = true;
            }

            // Decode 8 value knobs
            final int start = 7;
            for (int encIndex = 0; encIndex < 8; encIndex++)
            {
                final int pos = start + 2 * encIndex;

                final int value = Byte.toUnsignedInt (dst[pos]) | Byte.toUnsignedInt (dst[pos + 1]) << 8;
                final int hValue = Byte.toUnsignedInt (dst[pos + 1]);
                if (this.encoderValues[encIndex] != value)
                {
                    final int prevHValue = (this.encoderValues[encIndex] & 0xF00) >> 8;
                    final boolean valueIncreased = (this.encoderValues[encIndex] < value || prevHValue == 3 && hValue == 0) && !(prevHValue == 0 && hValue == 3);
                    this.encoderValues[encIndex] = value;
                    if (!this.isFirstStateMsg)
                        this.callback.encoderChanged (encIndex, valueIncreased);
                    encoderChange = true;
                }
            }

            this.isFirstStateMsg = false;

            // Test the pressed buttons
            this.testByteForButtons (dst[1], BYTE_0);
            this.testByteForButtons (dst[2], BYTE_1);
            this.testByteForButtons (dst[3], BYTE_2);
            // Don't test touch events on encoder change to prevent flickering
            if (!encoderChange)
            {
                this.testByteForButtons (dst[4], BYTE_3);
                this.testByteForButtons (dst[5], BYTE_4);
            }
        }
    }


    private void testByteForButtons (final byte b, final int [] buttons)
    {
        if (this.callback == null)
            return;

        final int t = Byte.toUnsignedInt (b);
        for (int i = 0; i < buttons.length; i++)
            this.callback.buttonChange (buttons[i], (t & TEST_BITS[i]) > 0);
    }


    /**
     * Turn off all button LEDs.
     */
    public void turnOffButtonLEDs ()
    {
        for (final Integer buttonLED: LED_MAPPING.values ())
            this.buttonStates[buttonLED.intValue ()] = 0;
        this.updateButtonLEDs ();
    }
}
