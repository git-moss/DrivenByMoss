// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;

import java.util.List;


/**
 * Abstract class for all modes which edit note parameters.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 * @param <B> The type of the item bank
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractNoteParameterMode<S extends IControlSurface<C>, C extends Configuration, B extends IItem> extends AbstractParameterMode<S, C, B> implements INoteEditorMode
{
    protected final IHost      host;
    protected final NoteEditor noteEditor;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param bank The parameter bank to control with this mode, might be null
     * @param controls The IDs of the knobs or faders to control this mode
     */
    protected AbstractNoteParameterMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final IBank<B> bank, final List<ContinuousID> controls)
    {
        super (name, surface, model, isAbsolute, bank, controls, surface::isShiftPressed);

        this.host = this.model.getHost ();
        this.noteEditor = new NoteEditor ();
    }


    /** {@inheritDoc} */
    @Override
    public INoteEditor getNoteEditor ()
    {
        return this.noteEditor;
    }
}