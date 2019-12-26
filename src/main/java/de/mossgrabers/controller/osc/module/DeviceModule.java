// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ILayerBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.empty.EmptyLayer;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

import java.util.LinkedList;


/**
 * All device related commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceModule extends AbstractModule
{
    private final OSCConfiguration configuration;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     * @param configuration The configuration
     */
    public DeviceModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer, final OSCConfiguration configuration)
    {
        super (host, model, writer);

        this.configuration = configuration;
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "device",
            "primary"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "device":
                this.parseDeviceValue (this.model.getCursorDevice (), path, value);
                break;

            case "primary":
                this.parseDeviceValue (this.model.getInstrumentDevice (), path, value);
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        this.flushDevice (this.writer, "/device/", cd, dump);
        if (cd.hasDrumPads ())
        {
            final IDrumPadBank drumPadBank = cd.getDrumPadBank ();
            for (int i = 0; i < drumPadBank.getPageSize (); i++)
                this.flushDeviceLayer (this.writer, "/device/drumpad/" + (i + 1) + "/", drumPadBank.getItem (i), dump);
        }
        final ILayerBank layerBank = cd.getLayerBank ();
        for (int i = 0; i < layerBank.getPageSize (); i++)
            this.flushDeviceLayer (this.writer, "/device/layer/" + (i + 1) + "/", layerBank.getItem (i), dump);
        final ILayer selectedLayer = layerBank.getSelectedItem ();
        this.flushDeviceLayer (this.writer, "/device/layer/selected/", selectedLayer == null ? EmptyLayer.INSTANCE : selectedLayer, dump);

        this.flushDevice (this.writer, "/primary/", this.model.getInstrumentDevice (), dump);
    }


    /**
     * Flush all data of a device.
     *
     * @param writer Where to send the messages to
     * @param deviceAddress The start address for the device
     * @param device The device
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushDevice (final IOpenSoundControlWriter writer, final String deviceAddress, final ICursorDevice device, final boolean dump)
    {
        writer.sendOSC (deviceAddress + "exists", device.doesExist (), dump);
        writer.sendOSC (deviceAddress + "name", device.getName (), dump);
        writer.sendOSC (deviceAddress + "bypass", !device.isEnabled (), dump);
        writer.sendOSC (deviceAddress + "expand", device.isExpanded (), dump);
        writer.sendOSC (deviceAddress + "window", device.isWindowOpen (), dump);
        final int positionInBank = device.getIndex ();
        final IDeviceBank deviceBank = device.getDeviceBank ();
        for (int i = 0; i < deviceBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            writer.sendOSC (deviceAddress + "sibling/" + oneplus + "/name", deviceBank.getItem (i).getName (), dump);
            writer.sendOSC (deviceAddress + "sibling/" + oneplus + "/selected", i == positionInBank, dump);

        }
        final IParameterBank parameterBank = device.getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            this.flushParameterData (writer, deviceAddress + "param/" + oneplus + "/", parameterBank.getItem (i), dump);
        }

        final IParameterPageBank parameterPageBank = device.getParameterPageBank ();
        final int selectedParameterPage = parameterPageBank.getSelectedItemIndex ();
        for (int i = 0; i < parameterPageBank.getPageSize (); i++)
        {
            final int oneplus = i + 1;
            writer.sendOSC (deviceAddress + "page/" + oneplus + "/", parameterPageBank.getItem (i), dump);
            writer.sendOSC (deviceAddress + "page/" + oneplus + "/selected", selectedParameterPage == i, dump);
        }
        writer.sendOSC (deviceAddress + "page/selected/name", parameterPageBank.getSelectedItem (), dump);
    }


    /**
     * Flush all data of a device layer.
     *
     * @param writer Where to send the messages to
     * @param deviceAddress The start address for the device
     * @param channel The channel of the layer
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    private void flushDeviceLayer (final IOpenSoundControlWriter writer, final String deviceAddress, final IChannel channel, final boolean dump)
    {
        if (channel == null)
            return;

        writer.sendOSC (deviceAddress + "exists", channel.doesExist (), dump);
        writer.sendOSC (deviceAddress + "activated", channel.isActivated (), dump);
        writer.sendOSC (deviceAddress + "selected", channel.isSelected (), dump);
        writer.sendOSC (deviceAddress + "name", channel.getName (), dump);
        writer.sendOSC (deviceAddress + "volumeStr", channel.getVolumeStr (), dump);
        writer.sendOSC (deviceAddress + "volume", channel.getVolume (), dump);
        writer.sendOSC (deviceAddress + "panStr", channel.getPanStr (), dump);
        writer.sendOSC (deviceAddress + "pan", channel.getPan (), dump);
        writer.sendOSC (deviceAddress + "mute", channel.isMute (), dump);
        writer.sendOSC (deviceAddress + "solo", channel.isSolo (), dump);

        final ISendBank sendBank = channel.getSendBank ();
        for (int i = 0; i < sendBank.getPageSize (); i++)
            this.flushParameterData (writer, deviceAddress + "send/" + (i + 1) + "/", sendBank.getItem (i), dump);

        if (this.configuration.isEnableVUMeters ())
            writer.sendOSC (deviceAddress + "vu", channel.getVu (), dump);

        final ColorEx color = channel.getColor ();
        writer.sendOSCColor (deviceAddress + "color", color.getRed (), color.getGreen (), color.getBlue (), dump);
    }


    private void parseDeviceValue (final ICursorDevice cursorDevice, final LinkedList<String> path, final Object value) throws UnknownCommandException, MissingCommandException, IllegalParameterException
    {
        final String command = getSubCommand (path);
        final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
        switch (command)
        {
            case "page":
                final String subCommand = getSubCommand (path);
                switch (subCommand)
                {
                    case "select":
                    case "selected":
                        cursorDevice.getParameterPageBank ().selectPage (toInteger (value) - 1);
                        break;

                    default:
                        try
                        {
                            final int index = Integer.parseInt (subCommand) - 1;
                            cursorDevice.getParameterPageBank ().selectPage (index);
                        }
                        catch (final NumberFormatException ex)
                        {
                            throw new UnknownCommandException (subCommand);
                        }
                        break;
                }
                break;

            case "sibling":
                final String siblingIndex = getSubCommand (path);
                final int siblingNo = Integer.parseInt (siblingIndex);
                final String subCommand2 = getSubCommand (path);
                switch (subCommand2)
                {
                    case "select":
                    case "selected":
                        if (isTrigger (value))
                            deviceBank.getItem (siblingNo - 1).select ();
                        break;

                    default:
                        throw new UnknownCommandException (subCommand2);
                }
                break;

            case "bank":
                final String subCommand3 = getSubCommand (path);
                switch (subCommand3)
                {
                    case "page":
                        final String directionCommand = getSubCommand (path);
                        if ("+".equals (directionCommand))
                            deviceBank.selectNextPage ();
                        else // "-"
                            deviceBank.selectPreviousPage ();
                        break;
                    default:
                        throw new UnknownCommandException (subCommand3);
                }
                break;

            case "expand":
                cursorDevice.toggleExpanded ();
                break;

            case "bypass":
                cursorDevice.toggleEnabledState ();
                break;

            case "window":
                cursorDevice.toggleWindowOpen ();
                break;

            case "indicate":
                final String subCommand4 = getSubCommand (path);
                switch (subCommand4)
                {
                    case "param":
                        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
                        for (int i = 0; i < parameterBank.getPageSize (); i++)
                            parameterBank.getItem (i).setIndication (isTrigger (value));
                        break;
                    default:
                        throw new UnknownCommandException (subCommand4);
                }
                break;

            case "param":
                final String subCommand5 = getSubCommand (path);
                try
                {
                    final int paramNo = Integer.parseInt (subCommand5) - 1;
                    parseFXParamValue (cursorDevice, paramNo, path, value);
                }
                catch (final NumberFormatException ex)
                {
                    if (isTrigger (value))
                    {
                        switch (subCommand5)
                        {
                            case "+":
                                cursorDevice.getParameterBank ().selectNextPage ();
                                break;
                            case "-":
                                cursorDevice.getParameterBank ().selectPreviousPage ();
                                break;

                            case "bank":
                                final String subCommand6 = getSubCommand (path);
                                switch (subCommand6)
                                {
                                    case "page":
                                        final String subCommand7 = getSubCommand (path);
                                        if ("+".equals (subCommand7))
                                            cursorDevice.getParameterPageBank ().scrollForwards ();
                                        else // "-"
                                            cursorDevice.getParameterPageBank ().scrollBackwards ();
                                        break;
                                    default:
                                        throw new UnknownCommandException (subCommand6);
                                }
                                break;

                            default:
                                throw new UnknownCommandException (subCommand5);
                        }
                    }
                }
                break;

            case "+":
                if (isTrigger (value))
                    cursorDevice.selectNext ();
                break;

            case "-":
                if (isTrigger (value))
                    cursorDevice.selectPrevious ();
                break;

            case "drumpad":
                if (cursorDevice.hasDrumPads ())
                    this.parseLayerOrDrumpad (cursorDevice, path, value);
                break;

            case "layer":
                this.parseLayerOrDrumpad (cursorDevice, path, value);
                break;

            default:
                this.host.println ("Unknown Device command: " + command);
                break;
        }
    }


    private void parseLayerOrDrumpad (final ICursorDevice cursorDevice, final LinkedList<String> path, final Object value) throws MissingCommandException, UnknownCommandException, IllegalParameterException
    {
        final String command = getSubCommand (path);
        try
        {
            final int layerNo;
            if ("selected".equals (command) || "select".equals (command))
            {
                final IChannel selectedLayerOrDrumPad = cursorDevice.getLayerOrDrumPadBank ().getSelectedItem ();
                layerNo = selectedLayerOrDrumPad == null ? -1 : selectedLayerOrDrumPad.getIndex ();
            }
            else
            {
                layerNo = Integer.parseInt (command) - 1;
            }
            this.parseDeviceLayerValue (cursorDevice, layerNo, path, value);
        }
        catch (final NumberFormatException ex)
        {
            switch (command)
            {
                case "parent":
                    if (cursorDevice.doesExist ())
                    {
                        cursorDevice.selectParent ();
                        cursorDevice.selectChannel ();
                    }
                    break;

                case "+":
                    cursorDevice.getLayerOrDrumPadBank ().selectNextItem ();
                    break;

                case "-":
                    cursorDevice.getLayerOrDrumPadBank ().selectPreviousItem ();
                    break;

                case "page":
                    if (path.isEmpty ())
                    {
                        this.host.println ("Missing Layer/Drumpad Page subcommand: " + command);
                        return;
                    }
                    if ("+".equals (path.get (0)))
                        cursorDevice.getLayerOrDrumPadBank ().selectNextPage ();
                    else
                        cursorDevice.getLayerOrDrumPadBank ().selectPreviousPage ();
                    break;

                default:
                    throw new UnknownCommandException (command);
            }
        }
    }


    private void parseDeviceLayerValue (final ICursorDevice cursorDevice, final int layerIndex, final LinkedList<String> path, final Object value) throws UnknownCommandException, IllegalParameterException, MissingCommandException
    {
        final String command = getSubCommand (path);
        final IChannelBank<?> layerOrDrumPadBank = cursorDevice.getLayerOrDrumPadBank ();
        if (layerIndex >= layerOrDrumPadBank.getPageSize ())
        {
            this.host.println ("Layer or drumpad index larger than page size: " + layerIndex);
            return;
        }

        final IChannel layer = layerOrDrumPadBank.getItem (layerIndex);
        switch (command)
        {
            case "select":
            case "selected":
                layerOrDrumPadBank.getItem (layerIndex).select ();
                break;

            case "volume":
                if (path.isEmpty ())
                    layer.setVolume (toInteger (value));
                else if ("indicate".equals (path.get (0)))
                    layer.setVolumeIndication (isTrigger (value));
                else if ("touched".equals (path.get (0)))
                    layer.touchVolume (isTrigger (value));
                break;

            case "pan":
                if (path.isEmpty ())
                    layer.setPan (toInteger (value));
                else if ("indicate".equals (path.get (0)))
                    layer.setPanIndication (isTrigger (value));
                else if ("touched".equals (path.get (0)))
                    layer.touchPan (isTrigger (value));
                break;

            case "mute":
                if (value == null)
                    layer.toggleMute ();
                else
                    layer.setMute (isTrigger (value));
                break;

            case "solo":
                if (value == null)
                    layer.toggleSolo ();
                else
                    layer.setSolo (isTrigger (value));
                break;

            case "send":
                final int sendNo = Integer.parseInt (path.removeFirst ()) - 1;
                if (path.isEmpty ())
                    return;
                if (!"volume".equals (path.removeFirst ()))
                    return;
                final ISend send = layer.getSendBank ().getItem (sendNo);
                if (path.isEmpty ())
                    send.setValue (toInteger (value));
                else if ("indicate".equals (path.get (0)))
                    send.setIndication (isTrigger (value));
                else if ("touched".equals (path.get (0)))
                    send.touchValue (isTrigger (value));
                break;

            case "enter":
                layer.enter ();
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    private static void parseFXParamValue (final ICursorDevice cursorDevice, final int fxparamIndex, final LinkedList<String> path, final Object value) throws MissingCommandException, IllegalParameterException, UnknownCommandException
    {
        final String command = getSubCommand (path);
        switch (command)
        {
            case "value":
                cursorDevice.getParameterBank ().getItem (fxparamIndex).setValue (toInteger (value));
                break;

            case "indicate":
                cursorDevice.getParameterBank ().getItem (fxparamIndex).setIndication (isTrigger (value));
                break;

            case "reset":
                cursorDevice.getParameterBank ().getItem (fxparamIndex).resetValue ();
                break;

            case "touched":
                cursorDevice.getParameterBank ().getItem (fxparamIndex).touchValue (isTrigger (value));
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }
}
