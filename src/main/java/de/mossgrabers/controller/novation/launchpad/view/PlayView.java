// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.Views;


/**
 * The play view.
 *
 * @author Jürgen Moßgraber
 */
public class PlayView extends AbstractPlayView<LaunchpadControlSurface, LaunchpadConfiguration> implements IScrollableView
{
    private final PlayControls playControls;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final LaunchpadControlSurface surface, final IModel model)
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
    public PlayView (final String name, final LaunchpadControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);

        this.playControls = new PlayControls (surface, this.scales);

        final Configuration configuration = this.surface.getConfiguration ();
        configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;

        switch (buttonID)
        {
            case SCENE1, SCENE2, SCENE7, SCENE8:
                return LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI;

            case SCENE4:
                return this.playControls.getToggleButtonColor ();

            case SCENE5:
                return LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_LO;

            case SCENE6:
                return this.scales.isChromatic () ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;

            default:
                return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.model.canSelectedTrackHoldNotes ())
            return;

        final ITextDisplay display = this.surface.getTextDisplay ();
        String name;

        switch (buttonID)
        {
            case SCENE1:
                this.scales.nextScaleLayout ();
                name = this.scales.getScaleLayout ().getName ();
                this.surface.getConfiguration ().setScaleLayout (name);
                display.notify (name);
                break;
            case SCENE2:
                this.scales.prevScaleLayout ();
                name = this.scales.getScaleLayout ().getName ();
                this.surface.getConfiguration ().setScaleLayout (name);
                display.notify (name);
                break;
            case SCENE4:
                this.playControls.toggle ();
                this.setBlockedNotes (this.playControls.isActive () ? 8 : 0);
                break;
            case SCENE5:
                this.activatePreferredView (Views.CHORDS);
                display.notify ("Chords: On");
                // Do not update note map!
                return;
            case SCENE6:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                display.notify (isChromatic ? "Chromatic" : "In Key");
                break;
            case SCENE7:
                this.scales.setScaleOffsetByIndex (this.scales.getScaleOffsetIndex () + 1);
                name = Scales.BASES.get (this.scales.getScaleOffsetIndex ());
                this.surface.getConfiguration ().setScaleBase (name);
                display.notify (name);
                break;
            case SCENE8:
                this.scales.setScaleOffsetByIndex (this.scales.getScaleOffsetIndex () - 1);
                name = Scales.BASES.get (this.scales.getScaleOffsetIndex ());
                this.surface.getConfiguration ().setScaleBase (name);
                display.notify (name);
                break;
            default:
                // Intentionally empty
                break;
        }
        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        super.drawGrid ();
        this.playControls.draw ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        if (!this.playControls.handleGridNotes (key, velocity))
            super.onGridNote (key, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final int octave = this.scales.getOctave ();
        final int scale = this.scales.getScale ().ordinal ();
        scrollStates.setCanScrollLeft (scale > 0);
        scrollStates.setCanScrollRight (scale < Scale.values ().length - 1);
        scrollStates.setCanScrollUp (octave < 3);
        scrollStates.setCanScrollDown (octave > -3);
    }
}