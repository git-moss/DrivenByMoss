// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import java.util.HashMap;
import java.util.Map;

import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;


/**
 * Default data for an empty layer bank.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyLayerBank extends EmptyBank<ILayer> implements ILayerBank
{
    private static final Map<Integer, EmptyLayerBank> INSTANCES = new HashMap<> ();


    /**
     * Get an instance of an EmptyLayerBank for the given page size. Instances are cached.
     *
     * @param pageSize The page size for which to get an empty bank
     * @return The bank
     */
    public static EmptyLayerBank getInstance (final int pageSize)
    {
        return INSTANCES.computeIfAbsent (Integer.valueOf (pageSize), EmptyLayerBank::new);
    }


    /**
     * Constructor.
     *
     * @param pageSize The number of elements in a page of the bank
     */
    private EmptyLayerBank (final int pageSize)
    {
        super (pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public ILayer getItem (final int index)
    {
        return EmptyLayer.getInstance (this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return null;
    }
}