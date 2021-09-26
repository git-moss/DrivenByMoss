// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.daw.data.IDeviceMetadata;


/**
 * Default implementation for device metadata.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceMetadataImpl implements IDeviceMetadata
{
    /** The type of the plug-in. */
    public enum PluginType
    {
        /** A native Bitwig device. */
        BITWIG,
        /** A VST 2 device. */
        VST2,
        /** A VST 3 device. */
        VST3
    }


    private final String     name;
    private final String     id;
    private final PluginType pluginType;


    /**
     * Constructor.
     *
     * @param name The name of the plugin
     * @param id The ID of the plugin
     * @param pluginType The type
     */
    public DeviceMetadataImpl (final String name, final String id, final PluginType pluginType)
    {
        this.name = name;
        this.id = id;
        this.pluginType = pluginType;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name;
    }


    /** {@inheritDoc} */
    @Override
    public String getFullName ()
    {
        if (this.pluginType == PluginType.BITWIG)
            return this.name;
        return String.format ("%s (%s)", this.name, this.pluginType);
    }


    /**
     * Get the ID.
     *
     * @return The ID
     */
    public String getId ()
    {
        return this.id;
    }


    /**
     * Get the type of the plugin.
     *
     * @return The type
     */
    public PluginType getPluginType ()
    {
        return this.pluginType;
    }
}
