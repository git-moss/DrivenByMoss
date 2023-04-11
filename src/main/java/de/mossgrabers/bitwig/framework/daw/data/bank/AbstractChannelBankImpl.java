// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;

import com.bitwig.extension.controller.api.Bank;

import java.util.Optional;


/**
 * An abstract channel bank.
 *
 * @param <B> The specific Bitwig bank type
 * @param <T> The specific item type of the bank
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractChannelBankImpl<B extends Bank<?>, T extends IChannel> extends AbstractItemBankImpl<B, T> implements IChannelBank<T>
{
    protected final int  numScenes;
    protected final int  numSends;

    protected ISceneBank sceneBank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param bank The Bitwig bank to encapsulate
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    protected AbstractChannelBankImpl (final IHost host, final IValueChanger valueChanger, final B bank, final int numTracks, final int numScenes, final int numSends)
    {
        super (host, valueChanger, bank, numTracks);

        this.numScenes = numScenes;
        this.numSends = numSends;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        final Optional<T> sel = this.getSelectedItem ();
        if (sel.isEmpty ())
            return DAWColor.COLOR_OFF.name ();
        return DAWColor.getColorID (sel.get ().getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        if (this.sceneBank != null)
            this.sceneBank.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.sceneBank;
    }
}
