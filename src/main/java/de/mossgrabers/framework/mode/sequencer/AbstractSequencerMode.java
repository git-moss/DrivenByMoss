package de.mossgrabers.framework.mode.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Abstract implementation for a mode which provides a sequencer.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractSequencerMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractFeatureGroup<S, C> implements IMode
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
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     * @param useDawColors True to use the DAW color of items for coloring (for full RGB devices)
     */
    protected AbstractSequencerMode (final String name, final S surface, final IModel model, final int clipRows, final int clipCols, final boolean useDawColors)
    {
        super (name, surface, model);

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
        this.getClip ().setStepLength (Resolution.getValueAt (this.selectedResolutionIndex));
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        // Intentionally empty
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        // Intentionally empty
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAnyKnobTouched ()
    {
        // Intentionally empty
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setTouchedKnob (final int knobIndex, final boolean isTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getTouchedKnob ()
    {
        // Intentionally empty
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getLastTouchedKnob ()
    {
        // Intentionally empty
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isKnobTouched (final int index)
    {
        // Intentionally empty
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectItem (final int index)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItemName ()
    {
        // Intentionally empty
        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemPage (final int page)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        // Intentionally empty
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        // Intentionally empty
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        // Intentionally empty
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        // Intentionally empty
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String formatPageRange (final String format)
    {
        // Intentionally empty
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public IParameterProvider getParameterProvider ()
    {
        // Intentionally empty
        return null;
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
}
