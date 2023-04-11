package de.mossgrabers.framework.mode.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * Abstract implementation for a mode which provides a sequencer.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractSequencerMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    protected final int     clipRows;
    protected final int     clipCols;
    protected final boolean useDawColors;

    protected int           selectedResolutionIndex;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     * @param useDawColors True to use the DAW color of items for coloring (for full RGB devices)
     */
    protected AbstractSequencerMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final int clipRows, final int clipCols, final boolean useDawColors)
    {
        super (name, surface, model, isAbsolute);

        this.clipRows = clipRows;
        this.clipCols = clipCols;
        this.useDawColors = useDawColors;

        this.selectedResolutionIndex = 4;

        this.getClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.getClip ().setStepLength (Resolution.getValueAt (this.selectedResolutionIndex));
    }


    /**
     * Get the clip.
     *
     * @return The clip
     */
    public final INoteClip getClip ()
    {
        return this.model.getNoteClip (this.clipCols, this.clipRows);
    }


    /**
     * @return the selectedResolutionIndex
     */
    public int getSelectedResolutionIndex ()
    {
        return this.selectedResolutionIndex;
    }


    /**
     * @param selectedResolutionIndex the selectedResolutionIndex to set
     */
    public void setSelectedResolutionIndex (final int selectedResolutionIndex)
    {
        this.selectedResolutionIndex = selectedResolutionIndex;
    }
}
