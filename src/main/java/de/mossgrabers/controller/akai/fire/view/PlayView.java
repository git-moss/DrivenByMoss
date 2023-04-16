// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.Views;


/**
 * The play view.
 *
 * @author Jürgen Moßgraber
 */
public class PlayView extends AbstractPlayView<FireControlSurface, FireConfiguration> implements IFireView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final FireControlSurface surface, final IModel model)
    {
        this (Views.NAME_PLAY, surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final String name, final FireControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);

        final Configuration configuration = this.surface.getConfiguration ();
        configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final boolean isShiftPressed = this.surface.isShiftPressed ();
        final boolean isAltPressed = this.surface.isPressed (ButtonID.ALT);

        final ITrack cursorTrack = this.model.getCursorTrack ();

        switch (buttonID)
        {
            case ARROW_LEFT:
                this.handleArrowLeft (isShiftPressed, isAltPressed);
                break;

            case ARROW_RIGHT:
                this.handleArrowRight (isShiftPressed, isAltPressed);
                break;

            case SCENE1:
                if (cursorTrack.doesExist ())
                    cursorTrack.stop (false);
                break;

            case SCENE2:
                if (cursorTrack.doesExist ())
                    cursorTrack.toggleMute ();
                break;

            case SCENE3:
                if (cursorTrack.doesExist ())
                    cursorTrack.toggleSolo ();
                break;

            case SCENE4:
                if (cursorTrack.doesExist ())
                    cursorTrack.toggleRecArm ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case SCENE1:
            case SCENE2:
            case SCENE3:
            case SCENE4:
                return this.surface.isPressed (buttonID) ? 2 : 1;

            default:
                return super.getButtonColor (buttonID);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();

        switch (index)
        {
            case 0:
                return 0;

            case 1:
                return cursorTrack.doesExist () && cursorTrack.isMute () ? 3 : 0;

            case 2:
                return cursorTrack.doesExist () && cursorTrack.isSolo () ? 4 : 0;

            case 3:
                return cursorTrack.doesExist () && cursorTrack.isRecArm () ? 1 : 0;

            default:
                return 0;
        }
    }


    private void handleArrowLeft (final boolean isShiftPressed, final boolean isAltPressed)
    {
        if (isShiftPressed)
        {
            if (isAltPressed)
            {
                this.scales.prevScaleOffset ();
                this.mvHelper.delayDisplay ( () -> Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
            }
            else
            {
                this.scales.prevScale ();
                this.mvHelper.delayDisplay ( () -> this.scales.getScale ().getName ());
            }
        }
        else if (isAltPressed)
        {
            this.scales.prevScaleLayout ();
            this.mvHelper.delayDisplay ( () -> this.scales.getScaleLayout ().getName ());
        }
        else
        {
            this.scales.toggleChromatic ();
            this.mvHelper.delayDisplay ( () -> this.scales.isChromatic () ? "Chromatic" : "In Scale");
        }
        this.updateScale ();
    }


    private void handleArrowRight (final boolean isShiftPressed, final boolean isAltPressed)
    {
        if (isShiftPressed)
        {
            if (isAltPressed)
            {
                this.scales.nextScaleOffset ();
                this.mvHelper.delayDisplay ( () -> Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
            }
            else
            {
                this.scales.nextScale ();
                this.mvHelper.delayDisplay ( () -> this.scales.getScale ().getName ());
            }
        }
        else if (isAltPressed)
        {
            this.scales.nextScaleLayout ();
            this.mvHelper.delayDisplay ( () -> this.scales.getScaleLayout ().getName ());
        }
        else
        {
            this.scales.toggleChromatic ();
            this.mvHelper.delayDisplay ( () -> this.scales.isChromatic () ? "Chromatic" : "In Scale");
        }
        this.updateScale ();
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        if (this.model.getValueChanger ().isIncrease (value))
            this.scales.incOctave ();
        else
            this.scales.decOctave ();
        this.mvHelper.delayDisplay (this.scales::getRangeText);
        this.updateNoteMapping ();
    }
}