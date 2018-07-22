// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IChannel;

import com.bitwig.extension.controller.api.Bank;


/**
 * An abstract channel bank.
 *
 * @param <B> The specific Bitwig bank type
 * @param <T> The specific item type of the bank
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractChannelBank<B extends Bank<?>, T extends IChannel> extends AbstractBankImpl<B, T> implements IChannelBank<T>
{
    protected int                 numScenes;
    protected int                 numSends;
    protected ISceneBank          sceneBank;
    protected final IValueChanger valueChanger;


    /**
     * Constructor.
     *
     * @param bank The Bitwig bank to encapsulate
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractChannelBank (final B bank, final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
    {
        super (bank, numTracks);

        this.valueChanger = valueChanger;
        this.numScenes = numScenes;
        this.numSends = numSends;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        final T sel = this.getSelectedItem ();
        if (sel == null)
            return DAWColors.COLOR_OFF;
        final double [] color = sel.getColor ();
        return DAWColors.getColorIndex (color[0], color[1], color[2]);
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
