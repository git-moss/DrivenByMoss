// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.core.AftertouchCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.KeyManager;


/**
 * Abstract implementation of a view.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractView<S extends IControlSurface<C>, C extends Configuration> extends AbstractFeatureGroup<S, C> implements IView
{
    protected static final int [] EMPTY_TABLE = Scales.getEmptyMatrix ();

    protected final Scales        scales;
    protected final KeyManager    keyManager;

    private AftertouchCommand     aftertouchCommand;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    protected AbstractView (final String name, final S surface, final IModel model)
    {
        super (name, surface, model);

        this.scales = model.getScales ();
        this.keyManager = new KeyManager (model, this.scales, surface.getPadGrid ());
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectTrack (final int index)
    {
        this.model.getCurrentTrackBank ().getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateControlSurface ()
    {
        final IMode m = this.surface.getModeManager ().getActive ();
        if (m != null)
            m.updateDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    public void registerAftertouchCommand (final AftertouchCommand command)
    {
        this.aftertouchCommand = command;
    }


    /** {@inheritDoc} */
    @Override
    public void executeAftertouchCommand (final int note, final int value)
    {
        if (this.aftertouchCommand == null)
            return;
        if (note == -1)
            this.aftertouchCommand.onChannelAftertouch (value);
        else
            this.aftertouchCommand.onPolyAftertouch (note, value);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        // Intentionally empty
    }


    /**
     * Get the ID of the color to use for a pad with respect to the current scale settings.
     *
     * @param pad The MIDI note of the pad
     * @param track A track to use the track color for coloring the octave notes, set to null to use
     *            the default color
     * @return The color ID
     */
    protected String getPadColor (final int pad, final ITrack track)
    {
        return replaceOctaveColorWithTrackColor (track, this.keyManager.getColor (pad));
    }


    /**
     * If the given color ID is the octave color ID it will be replaced with the track color ID.
     *
     * @param track A track to use the track color for coloring the octave notes, set to null to use
     *            the default color
     * @param colorID
     * @return The color ID
     */
    public static String replaceOctaveColorWithTrackColor (final ITrack track, final String colorID)
    {
        if (Scales.SCALE_COLOR_OCTAVE.equals (colorID))
        {
            if (track == null)
                return Scales.SCALE_COLOR_OCTAVE;
            final String c = DAWColor.getColorID (track.getColor ());
            return c == null ? Scales.SCALE_COLOR_OCTAVE : c;
        }
        return colorID;
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (EMPTY_TABLE);
    }


    /** {@inheritDoc} */
    @Override
    public KeyManager getKeyManager ()
    {
        return this.keyManager;
    }


    protected void delayedUpdateNoteMapping (final int [] matrix)
    {
        this.surface.scheduleTask ( () -> {
            this.keyManager.setNoteMatrix (matrix);
            if (matrix.length == 128)
                this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (matrix));
        }, 6);
    }


    /**
     * Simulate pressing a button by sending a button down and up event.
     *
     * @param buttonID The ID of the button to trigger
     */
    protected void simulateButtonPress (final ButtonID buttonID)
    {
        final IHwButton button = this.surface.getButton (buttonID);
        final AbstractTriggerCommand<?, ?> triggerCommand = (AbstractTriggerCommand<?, ?>) button.getCommand ();
        triggerCommand.executeNormal (ButtonEvent.DOWN);
        triggerCommand.executeNormal (ButtonEvent.UP);
    }


    /**
     * Simulate pressing a button with combination of the shift button by sending a button down and
     * up event.
     *
     * @param buttonID The ID of the button to trigger
     */
    protected void simulateShiftedButtonPress (final ButtonID buttonID)
    {
        final IHwButton button = this.surface.getButton (buttonID);
        final AbstractTriggerCommand<?, ?> triggerCommand = (AbstractTriggerCommand<?, ?>) button.getCommand ();
        triggerCommand.executeShifted (ButtonEvent.DOWN);
        triggerCommand.executeShifted (ButtonEvent.UP);
    }
}