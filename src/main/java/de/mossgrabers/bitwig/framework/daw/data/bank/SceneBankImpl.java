// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.SceneImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;

import com.bitwig.extension.controller.api.SceneBank;


/**
 * Encapsulates the data of a scene bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneBankImpl extends AbstractItemBankImpl<SceneBank, IScene> implements ISceneBank
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param sceneBank The scene bank
     * @param numScenes The number of scenes in the page of the bank
     */
    public SceneBankImpl (final IHost host, final IValueChanger valueChanger, final SceneBank sceneBank, final int numScenes)
    {
        super (host, valueChanger, sceneBank, numScenes);

        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new SceneImpl (this.bank.getItemAt (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        this.bank.stop ();
    }
}