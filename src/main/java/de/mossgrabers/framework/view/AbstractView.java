// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.MVHelper;
import de.mossgrabers.framework.command.core.AftertouchCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.mode.Mode;
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
public abstract class AbstractView<S extends IControlSurface<C>, C extends Configuration> implements View
{
    protected static final int []  EMPTY_TABLE = Scales.getEmptyMatrix ();

    private final String           name;

    protected final S              surface;
    protected final IModel         model;
    protected final ColorManager   colorManager;
    protected final Scales         scales;
    protected final KeyManager     keyManager;
    protected final MVHelper<S, C> mvHelper;

    private AftertouchCommand      aftertouchCommand;

    protected boolean              canScrollLeft;
    protected boolean              canScrollRight;
    protected boolean              canScrollUp;
    protected boolean              canScrollDown;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public AbstractView (final String name, final S surface, final IModel model)
    {
        this.name = name;
        this.surface = surface;
        this.model = model;
        this.colorManager = this.model.getColorManager ();
        this.scales = model.getScales ();
        this.keyManager = new KeyManager (model, surface.getPadGrid ());
        this.mvHelper = new MVHelper<> (model, surface);

        this.canScrollLeft = true;
        this.canScrollRight = true;
        this.canScrollUp = true;
        this.canScrollDown = true;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name;
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
        final Mode m = this.surface.getModeManager ().getActiveOrTempMode ();
        if (m != null)
            m.updateDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return this.colorManager.getColorIndex (this.getButtonColorID (buttonID));
    }


    /**
     * Get the color ID for a button, which is controlled by the view.
     *
     * @param buttonID The ID of the button
     * @return A color ID
     */
    protected String getButtonColorID (final ButtonID buttonID)
    {
        return AbstractMode.BUTTON_COLOR_OFF;
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
     * @param pad The midi note of the pad
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
    protected static String replaceOctaveColorWithTrackColor (final ITrack track, final String colorID)
    {
        if (Scales.SCALE_COLOR_OCTAVE.equals (colorID))
        {
            if (track == null)
                return Scales.SCALE_COLOR_OCTAVE;
            final String c = DAWColor.getColorIndex (track.getColor ());
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


    protected void delayedUpdateNoteMapping (final int [] matrix)
    {
        this.surface.scheduleTask ( () -> {
            this.keyManager.setNoteMatrix (matrix);
            if (matrix.length == 128)
                this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (matrix));
        }, 6);
    }


    /**
     * Get the key manager.
     *
     * @return The key manager
     */
    public KeyManager getKeyManager ()
    {
        return this.keyManager;
    }


    /**
     * Tests if the button is pressed. If yes, the button UP event is consumed.
     *
     * @param buttonID The button to test
     * @return True if button is pressed
     */
    protected boolean isButtonCombination (final ButtonID buttonID)
    {
        if (this.surface.isPressed (buttonID))
        {
            this.surface.setTriggerConsumed (buttonID);
            return true;
        }
        return false;
    }
}