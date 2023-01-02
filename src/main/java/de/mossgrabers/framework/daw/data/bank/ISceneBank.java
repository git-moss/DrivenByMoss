// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IScene;


/**
 * Interface to a scene bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISceneBank extends IBank<IScene>
{
    /**
     * Stop all playing clips.
     */
    void stop ();
}