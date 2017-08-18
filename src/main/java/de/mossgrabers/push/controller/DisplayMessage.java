package de.mossgrabers.push.controller;

import de.mossgrabers.push.controller.display.model.DisplayModel;
import de.mossgrabers.push.controller.display.model.ProtocolParser;
import de.mossgrabers.push.controller.display.model.grid.GridElement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Communication message to talk to the display process.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DisplayMessage
{
    /** Display only a channel name for selection. */
    public static final int      GRID_ELEMENT_CHANNEL_SELECTION  = 0;
    /** Display a channel, edit volume. */
    public static final int      GRID_ELEMENT_CHANNEL_VOLUME     = 1;
    /** Display a channel, edit panorama. */
    public static final int      GRID_ELEMENT_CHANNEL_PAN        = 2;
    /** Display a channel, edit crossfader. */
    public static final int      GRID_ELEMENT_CHANNEL_CROSSFADER = 3;
    /** Display a channel sends. */
    public static final int      GRID_ELEMENT_CHANNEL_SENDS      = 4;
    /** Display a channel, edit all parameters. */
    public static final int      GRID_ELEMENT_CHANNEL_ALL        = 5;
    /** Display a parameter with name and value. */
    public static final int      GRID_ELEMENT_PARAMETERS         = 6;
    /** Display options on top and bottom. */
    public static final int      GRID_ELEMENT_OPTIONS            = 7;
    /** Display a list. */
    public static final int      GRID_ELEMENT_LIST               = 8;

    /** The grid command. */
    public static final int      DISPLAY_COMMAND_GRID            = 10;

    private int                  command;
    private List<Integer>        array;
    private int                  port;

    private final ProtocolParser parser                          = new ProtocolParser ();
    private DisplayModel         model;


    /**
     * Constructor. Uses the grid command.
     *
     * @param model The display model
     * @param port The communication port
     */
    public DisplayMessage (final DisplayModel model, final int port)
    {
        this (model, port, DISPLAY_COMMAND_GRID);
    }


    /**
     * Constructor.
     *
     * @param model The display model
     * @param port The communication port
     * @param command The command to send
     */
    public DisplayMessage (final DisplayModel model, final int port, final int command)
    {
        this.model = model;
        this.port = port;
        this.command = command;
        this.array = new ArrayList<> ();
    }


    /**
     * Send the message to the display process.
     */
    public void send ()
    {
        if (this.port < 1)
            return;

        final int size = this.array.size ();
        final byte [] data = new byte [3 + size];
        data[0] = -16; // -16 = 0xF0
        data[1] = (byte) this.command;
        for (int i = 0; i < size; i++)
            data[2 + i] = this.array.get (i).byteValue ();
        data[size + 2] = -9; // -9 = 0xF7
        this.sendToDisplay (data);
    }


    /**
     * Set a message on the display.
     *
     * @param column The column in which to display the message
     * @param text The text to display
     * @return The message
     */
    public DisplayMessage setMessage (final int column, final String text)
    {
        for (int i = 0; i < 8; i++)
            this.addOptionElement (column == i ? text : "", "", false, "", "", false, false);
        return this;
    }


    /**
     * Adds an empty element.
     */
    public void addEmptyElement ()
    {
        this.addOptionElement ("", "", false, "", "", false, false);
    }


    /**
     * Add an options element to the message.
     *
     * @param headerTopName A text on the top
     * @param menuTopName The text for the top menu
     * @param isMenuTopSelected True if the top menu is selected (on)
     * @param headerBottomName A text on the bottom
     * @param menuBottomName The text for the bottom menu
     * @param isMenuBottomSelected True if the bottom menu is selected (on)
     * @param useSmallTopMenu If true use small menus
     */
    public void addOptionElement (final String headerTopName, final String menuTopName, final boolean isMenuTopSelected, final String headerBottomName, final String menuBottomName, final boolean isMenuBottomSelected, final boolean useSmallTopMenu)
    {
        this.addByte (DisplayMessage.GRID_ELEMENT_OPTIONS);
        this.addString (headerTopName);
        this.addString (menuTopName);
        this.addBoolean (isMenuTopSelected);
        this.addString (headerBottomName);
        this.addString (menuBottomName);
        this.addBoolean (isMenuBottomSelected);
        this.addBoolean (useSmallTopMenu);
    }


    /**
     * Adds a string to the message.
     *
     * @param text The text to add
     */
    public void addString (final String text)
    {
        if (text != null)
        {
            for (int i = 0; i < text.length (); i++)
            {
                final char character = text.charAt (i);
                if (character < 128)
                    this.array.add (Integer.valueOf (character));
                else
                {
                    // Split up non-ASII characters into 3 bytes
                    this.array.add (Integer.valueOf (-1));
                    this.addInteger (character);
                }
            }
        }
        this.array.add (Integer.valueOf (0));
    }


    /**
     * Adds an integer to the message.
     *
     * @param value The text to add
     */
    public void addInteger (final int value)
    {
        this.array.add (Integer.valueOf (value & 0x7F));
        this.array.add (Integer.valueOf (value >> 7 & 0x7F));
    }


    /**
     * Adds an boolean to the message.
     *
     * @param value The boolean to add
     */
    public void addBoolean (final boolean value)
    {
        this.array.add (Integer.valueOf (value ? 1 : 0));
    }


    /**
     * Adds an color to the message.
     *
     * @param color The color in RGB to add
     */
    public void addColor (final double [] color)
    {
        if (color != null)
        {
            this.addInteger ((int) Math.round (color[0] * 255));
            this.addInteger ((int) Math.round (color[1] * 255));
            this.addInteger ((int) Math.round (color[2] * 255));
        }
        else
        {
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
            this.array.add (Integer.valueOf (0));
        }
    }


    /**
     * Adds a byte to the message.
     *
     * @param value The byte to add
     */
    public void addByte (final int value)
    {
        this.array.add (Integer.valueOf (value));
    }


    private void sendToDisplay (final byte [] data)
    {
        // this.host.sendDatagramPacket ("127.0.0.1", this.port, data);
        this.handleData (data, data.length);
    }


    /**
     * Handle the received data.
     *
     * @param data The data buffer with the received data
     * @param length The length of usable data in the buffer
     */
    public void handleData (final byte [] data, final int length)
    {
        // -16 == 0xF0, -9 == 0xF7
        if (data[0] != -16 || data[length - 1] != -9)
        {
            // this.model.addLogMessage ("Unformatted messaged received.");
            return;
        }

        switch (data[1])
        {
            case DISPLAY_COMMAND_GRID:
                try (final ByteArrayInputStream in = new ByteArrayInputStream (data, 2, length - 3))
                {
                    final List<GridElement> elements = this.parser.parse (in);
                    if (elements != null)
                        this.model.setGridElements (elements);
                }
                catch (final IOException ex)
                {
                    // this.model.addLogMessage ("Unparsable grid element message: " +
                    // ex.getLocalizedMessage ());
                }
                break;
        }
    }
}