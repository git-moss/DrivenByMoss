// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.CursorTrackImpl;
import de.mossgrabers.bitwig.framework.daw.data.SceneImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;

import com.bitwig.extension.controller.api.SceneBank;


/**
 * Encapsulates the data of a scene bank. Note: Navigating Scenes does not update the cursor track's
 * clips page, this needs to be kept in sync manually.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneBankImpl extends AbstractItemBankImpl<SceneBank, IScene> implements ISceneBank
{
    private ISlotBank slotBank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param sceneBank The scene bank
     * @param numScenes The number of scenes in the page of the bank
     * @param cursorTrack The cursor track, required for scene navigation
     */
    public SceneBankImpl (final IHost host, final IValueChanger valueChanger, final SceneBank sceneBank, final int numScenes, final CursorTrackImpl cursorTrack)
    {
        super (host, valueChanger, sceneBank, numScenes);

        this.slotBank = cursorTrack.getSlotBank ();

        if (this.bank.isEmpty ())
            return;

        final SceneBank sb = this.bank.get ();
        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new SceneImpl (sb.getItemAt (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        if (this.bank.isPresent ())
            this.bank.get ().stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.slotBank.scrollBackwards ();
        super.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.slotBank.scrollForwards ();
        super.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        this.slotBank.scrollTo (position, adjustPage);
        super.scrollTo (position, adjustPage);
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
    {
        this.slotBank.selectItemAtPosition (position);
        super.selectItemAtPosition (position);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.slotBank.selectNextItem ();
        super.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.slotBank.selectPreviousItem ();
        super.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.slotBank.selectNextPage ();
        super.selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.slotBank.selectPreviousPage ();
        super.selectPreviousPage ();
    }
}