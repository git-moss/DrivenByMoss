// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.continuous;

import de.mossgrabers.controller.ableton.push.command.trigger.PlayPositionKnobCommand;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Command to handle the big encoder.
 *
 * @author Jürgen Moßgraber
 */
public class Push3EncoderCommand extends PlayPositionKnobCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public Push3EncoderCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        // Encoder turn

        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode instanceof final IPush3Encoder encoderMode)
        {
            encoderMode.encoderTurn (value);
            return;
        }

        // Change play position and loop length
        super.execute (value);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        // Encoder pressed

        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode instanceof final IPush3Encoder encoderMode)
        {
            encoderMode.encoderPress (event);
            return;
        }

        if (event == ButtonEvent.DOWN)
            this.transport.play ();
    }


    /**
     * Encoder was moved left.
     *
     * @param event The button event
     */
    public void left (final ButtonEvent event)
    {
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode instanceof final IPush3Encoder encoderMode)
        {
            encoderMode.encoderLeft (event);
            return;
        }

        if (event != ButtonEvent.DOWN)
            return;

        // Move play position to loop end, start or the project start, depending on the current play
        // position
        final double position = this.transport.getPosition ();
        double pos = this.transport.getLoopEnd ();
        if (position > pos)
            this.transport.selectLoopEnd ();
        else
        {
            pos = this.transport.getLoopStart ();
            if (position > pos)
                this.transport.selectLoopStart ();
            else
                this.transport.setPosition (0);
        }
        this.displayPosition ();
    }


    /**
     * Encoder was moved right.
     *
     * @param event The button event
     */
    public void right (final ButtonEvent event)
    {
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode instanceof final IPush3Encoder encoderMode)
        {
            encoderMode.encoderRight (event);
            return;
        }

        if (event != ButtonEvent.DOWN)
            return;

        // Move play position to loop start, end or the project end, depending on the current play
        // position
        final double position = this.transport.getPosition ();
        double pos = this.transport.getLoopStart ();
        if (position < pos)
            this.transport.selectLoopStart ();
        else
        {
            pos = this.transport.getLoopEnd ();
            if (position < pos)
                this.transport.selectLoopEnd ();
            else
                this.transport.setPositionToEnd ();
        }
        this.displayPosition ();
    }


    /**
     * Center button of the 4 arrows was pressed.
     *
     * @param event The button event
     */
    public void center (final ButtonEvent event)
    {
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode instanceof final IPush3Encoder encoderMode)
        {
            encoderMode.arrowCenter (event);
            return;
        }

        final Optional<IScene> selectedScene = this.model.getSceneBank ().getSelectedItem ();
        if (selectedScene.isPresent ())
            selectedScene.get ().launch (event == ButtonEvent.DOWN, true);
    }
}
