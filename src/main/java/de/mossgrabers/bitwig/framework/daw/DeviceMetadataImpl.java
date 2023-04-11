// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.daw.data.IDeviceMetadata;


/**
 * Default implementation for device metadata.
 *
 * @author Jürgen Moßgraber
 *
 * @param name The name of the plugin
 * @param id The ID of the plugin
 * @param pluginType The type
 */
public record DeviceMetadataImpl (String name, String id, PluginType pluginType) implements IDeviceMetadata
{
    /** The type of the plug-in. */
    public enum PluginType
    {
        /** A native Bitwig device. */
        BITWIG,
        /** A CLAP device. */
        CLAP,
        /** A VST 2 device. */
        VST2,
        /** A VST 3 device. */
        VST3
    }


    /** {@inheritDoc} */
    @Override
    public String fullName ()
    {
        if (this.pluginType == PluginType.BITWIG)
            return this.name;
        return String.format ("%s (%s)", this.name, this.pluginType);
    }
}
