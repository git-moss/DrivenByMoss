// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Default data for an empty layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyLayerData extends EmptyChannelData implements ILayer
{
    /** The singleton. */
    public static final ILayer INSTANCE = new EmptyLayerData ();
}
