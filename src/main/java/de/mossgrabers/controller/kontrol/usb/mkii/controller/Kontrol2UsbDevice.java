// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.usb.IHidDevice;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.IUsbEndpoint;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * USB connection for display and UI controls of Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2UsbDevice
{

    private static final int []                BYTE_1             =
    {
        Kontrol2ControlSurface.BUTTON_AUTO,
        Kontrol2ControlSurface.BUTTON_QUANTIZE,
        Kontrol2ControlSurface.BUTTON_ARP,
        Kontrol2ControlSurface.BUTTON_SCALE,
        Kontrol2ControlSurface.BUTTON_PLAY,
        Kontrol2ControlSurface.BUTTON_LOOP,
        Kontrol2ControlSurface.BUTTON_UNDO,
        Kontrol2ControlSurface.BUTTON_SHIFT,
    };

    private static final int []                BYTE_2             =
    {
        Kontrol2ControlSurface.BUTTON_STOP,
        Kontrol2ControlSurface.BUTTON_REC,
        Kontrol2ControlSurface.BUTTON_TEMPO,
        Kontrol2ControlSurface.BUTTON_METRO,
        Kontrol2ControlSurface.BUTTON_PRESET_UP,
        Kontrol2ControlSurface.BUTTON_PAGE_RIGHT,
        Kontrol2ControlSurface.BUTTON_PRESET_DOWN,
        Kontrol2ControlSurface.BUTTON_PAGE_LEFT
    };

    private static final int []                BYTE_3             =
    {
        Kontrol2ControlSurface.BUTTON_MUTE,
        Kontrol2ControlSurface.BUTTON_SOLO,
        Kontrol2ControlSurface.BUTTON_SCENE,
        Kontrol2ControlSurface.BUTTON_PATTERN,
        Kontrol2ControlSurface.BUTTON_TRACK,
        Kontrol2ControlSurface.BUTTON_CLEAR,
        Kontrol2ControlSurface.BUTTON_KEY_MODE,
    };

    private static final int []                BYTE_4             =
    {
        Kontrol2ControlSurface.BUTTON_MIXER,
        Kontrol2ControlSurface.BUTTON_PLUGIN,
        Kontrol2ControlSurface.BUTTON_BROWSER,
        Kontrol2ControlSurface.BUTTON_SETUP,
        Kontrol2ControlSurface.BUTTON_INSTANCE,
        Kontrol2ControlSurface.BUTTON_MIDI
    };

    private static final int []                BYTE_5             =
    {
        Kontrol2ControlSurface.TOUCH_ENCODER_MAIN
    };

    private static final int []                BYTE_6             =
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

    private static final int                   DATA_SZ            = 249;
    private static final int                   TIMEOUT            = 0;
    private static final int                   MESSAGE_SIZE       = 49;

    private static final Map<Integer, Integer> LED_MAPPING        = new HashMap<> (21);

    private IHost                              host;
    private IUsbDevice                         usbDevice;
    private IUsbEndpoint                       usbEndpointDisplay;
    private IHidDevice                         usbEndpointUI;

    private IMemoryBlock                       initBlock;
    private IMemoryBlock                       displayBlock;
    private IMemoryBlock                       ledBlock;
    private IMemoryBlock                       keyLedBlock;

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
        // TODO
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_SHIFT), Integer.valueOf (0));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_SCALE), Integer.valueOf (1));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_ARP), Integer.valueOf (2));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_LOOP), Integer.valueOf (3));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PLAY), Integer.valueOf (6));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_REC), Integer.valueOf (7));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_STOP), Integer.valueOf (8));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PAGE_LEFT), Integer.valueOf (9));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PAGE_RIGHT), Integer.valueOf (10));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PRESET_UP), Integer.valueOf (12));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_INSTANCE), Integer.valueOf (13));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_PRESET_DOWN), Integer.valueOf (14));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_NAVIGATE_UP), Integer.valueOf (16));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_NAVIGATE_LEFT), Integer.valueOf (18));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_NAVIGATE_DOWN), Integer.valueOf (19));
        LED_MAPPING.put (Integer.valueOf (Kontrol2ControlSurface.BUTTON_NAVIGATE_RIGHT), Integer.valueOf (20));
    }


    /**
     * Constructor.
     *
     * @param host The controller host
     */
    public Kontrol2UsbDevice (final IHost host)
    {
        this.host = host;

        try
        {
            this.usbDevice = host.getUsbDevice (0);
            this.usbEndpointUI = this.usbDevice.getHidDevice ();
            this.usbEndpointUI.setCallback (this::processMessage);
            // TODO Deactivate
            // this.usbEndpointDisplay = this.usbDevice.getEndpoint (1, 0);
        }
        catch (final RuntimeException ex)
        {
            this.usbDevice = null;
            this.usbEndpointDisplay = null;
            this.usbEndpointUI = null;
            host.error ("Could not open USB connection: " + ex.getMessage ());
        }

        this.displayBlock = host.createMemoryBlock (DATA_SZ);
        this.ledBlock = host.createMemoryBlock (26);
        this.keyLedBlock = host.createMemoryBlock (267);
        this.initBlock = host.createMemoryBlock (3);
        final ByteBuffer initBuffer = this.initBlock.createByteBuffer ();
        initBuffer.put ((byte) 0xA0);
        initBuffer.put ((byte) 0x00);
        initBuffer.put ((byte) 0x00);

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
            this.usbEndpointDisplay.send (this.initBlock, TIMEOUT);
    }


    /**
     * Send all display data to the device.
     */
    public void sendDisplayData ()
    {
        if (this.busySendingDisplay || this.usbEndpointDisplay == null)
            return;

        // TODO Implement sending to display
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

        final ByteBuffer ledBuffer = this.ledBlock.createByteBuffer ();
        ledBuffer.clear ();
        ledBuffer.put ((byte) 0x80);
        ledBuffer.put (this.buttonStates);
        ledBuffer.put ((byte) 0);
        ledBuffer.put ((byte) 0);
        ledBuffer.put ((byte) 0);
        ledBuffer.put ((byte) 0);

        this.busySendingLEDs = true;
        this.usbEndpointDisplay.send (this.ledBlock, TIMEOUT);
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

        final ByteBuffer keyLedBuffer = this.keyLedBlock.createByteBuffer ();
        keyLedBuffer.clear ();
        keyLedBuffer.put ((byte) 0x82);
        keyLedBuffer.put (this.keyColors);
        keyLedBuffer.put ((byte) 0x0);
        keyLedBuffer.put ((byte) 0x0);

        this.busySendingKeyLEDs = true;
        this.usbEndpointDisplay.send (this.keyLedBlock, TIMEOUT);
        this.busySendingKeyLEDs = false;
    }


    private void processMessage (final byte [] data, final int received)
    {
        // TODO Remove
        // for (int i = 0; i < 34; i++)
        // System.out.printf ("%d ", data[i]);
        // System.out.println ("*");

        for (int i = 0; i < received / MESSAGE_SIZE; i++)
        {
            boolean encoderChange = false;

            // Handle button presses
            if (data[30] == 36)
            {
                // Decode main knob
                final int currentEncoderValue = Byte.toUnsignedInt (data[29]);
                if (currentEncoderValue != this.mainEncoderValue)
                {
                    final boolean valueIncreased = (this.mainEncoderValue < currentEncoderValue || this.mainEncoderValue == 0x0F && currentEncoderValue == 0) && !(this.mainEncoderValue == 0 && currentEncoderValue == 0x0F);
                    this.mainEncoderValue = currentEncoderValue;
                    if (!this.isFirstStateMsg)
                        this.callback.mainEncoderChanged (valueIncreased);
                    encoderChange = true;
                }

                // Test the pressed buttons
                this.testByteForButtons (data[1], BYTE_1);
                this.testByteForButtons (data[2], BYTE_2);
                this.testByteForButtons (data[3], BYTE_3);
                this.testByteForButtons (data[4], BYTE_4);
                // Don't test touch events on encoder change to prevent flickering
                if (!encoderChange)
                {
                    this.testByteForButtons (data[3], BYTE_5);
                    this.testByteForButtons (data[4], BYTE_6);
                }
            }
            else
            {
                // Decode 8 value knobs
                final int start = 16;
                for (int encIndex = 0; encIndex < 8; encIndex++)
                {
                    final int pos = start + 2 * encIndex;

                    final int value = Byte.toUnsignedInt (data[pos]) | Byte.toUnsignedInt (data[pos + 1]) << 8;
                    final int hValue = Byte.toUnsignedInt (data[pos + 1]);
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
            }

            this.isFirstStateMsg = false;
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
