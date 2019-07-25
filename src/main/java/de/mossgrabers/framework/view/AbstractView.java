// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.core.AftertouchCommand;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.PitchbendCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.KeyManager;

import java.util.EnumMap;
import java.util.Map;


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
    protected static final int []                             EMPTY_TABLE        = Scales.getEmptyMatrix ();

    private final String                                      name;

    protected final S                                         surface;
    protected final IModel                                    model;
    protected final Scales                                    scales;
    protected final KeyManager                                keyManager;

    private AftertouchCommand                                 aftertouchCommand;
    private PitchbendCommand                                  pitchbendCommand;

    private final Map<TriggerCommandID, TriggerCommand>       triggerCommands    = new EnumMap<> (TriggerCommandID.class);
    private final Map<TriggerCommandID, TriggerCommand>       noteCommands       = new EnumMap<> (TriggerCommandID.class);
    private final Map<ContinuousCommandID, ContinuousCommand> continuousCommands = new EnumMap<> (ContinuousCommandID.class);

    protected boolean                                         canScrollLeft;
    protected boolean                                         canScrollRight;
    protected boolean                                         canScrollUp;
    protected boolean                                         canScrollDown;


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
        this.scales = model.getScales ();
        this.keyManager = new KeyManager (model, surface.getPadGrid ());

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
        {
            m.updateDisplay ();
            m.updateFirstRow ();
            m.updateSecondRow ();
        }
        this.updateButtons ();
        this.updateArrows ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        return true;
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
    public void registerPitchbendCommand (final PitchbendCommand command)
    {
        this.pitchbendCommand = command;
    }


    /** {@inheritDoc} */
    @Override
    public void executePitchbendCommand (final int channel, final int data1, final int data2)
    {
        if (this.pitchbendCommand != null)
            this.pitchbendCommand.onPitchbend (channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public PitchbendCommand getPitchbendCommand ()
    {
        return this.pitchbendCommand;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void registerTriggerCommand (final TriggerCommandID commandID, final TriggerCommand command)
    {
        this.triggerCommands.put (commandID, command);
    }


    /** {@inheritDoc} */
    @Override
    public void executeTriggerCommand (final TriggerCommandID commandID, final ButtonEvent event)
    {
        final TriggerCommand triggerCommand = this.triggerCommands.get (commandID);
        if (triggerCommand != null)
            triggerCommand.execute (event);
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommand getTriggerCommand (final TriggerCommandID commandID)
    {
        return this.triggerCommands.get (commandID);
    }


    /** {@inheritDoc} */
    @Override
    public void registerContinuousCommand (final ContinuousCommandID commandID, final ContinuousCommand command)
    {
        this.continuousCommands.put (commandID, command);
    }


    /** {@inheritDoc} */
    @Override
    public ContinuousCommand getContinuousCommand (final ContinuousCommandID commandID)
    {
        return this.continuousCommands.get (commandID);
    }


    /** {@inheritDoc} */
    @Override
    public void executeContinuousCommand (final ContinuousCommandID commandID, final int value)
    {
        final ContinuousCommand continuousCommand = this.continuousCommands.get (commandID);
        if (continuousCommand != null)
            continuousCommand.execute (value);
    }


    /** {@inheritDoc} */
    @Override
    public void registerNoteCommand (final TriggerCommandID commandID, final TriggerCommand command)
    {
        this.noteCommands.put (commandID, command);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNoteCommand (final TriggerCommandID commandID, final int value)
    {
        final TriggerCommand command = this.noteCommands.get (commandID);
        if (command != null)
            command.execute (value == 0 ? ButtonEvent.UP : ButtonEvent.DOWN);
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommand getNoteCommand (final TriggerCommandID commandID)
    {
        return this.noteCommands.get (commandID);
    }


    /**
     * Get the ID of the color to use for a pad with respect to the current scale settings.
     *
     * @param pad The midi note of the pad
     * @param track A track to use the track color for coloring the octave notes, set to null to use
     *            the default color
     * @return The color ID
     */
    protected String getColor (final int pad, final ITrack track)
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
            final double [] color = track.getColor ();
            final String c = DAWColors.getColorIndex (color[0], color[1], color[2]);
            return c == null ? Scales.SCALE_COLOR_OCTAVE : c;
        }
        return colorID;
    }


    /**
     * Implement to update button LEDs.
     */
    protected void updateButtons ()
    {
        // Intentionally empty
    }


    /**
     * Implement to update arrow button LEDs.
     */
    protected void updateArrows ()
    {
        // Intentionally empty
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
}