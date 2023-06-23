// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.AbstractChordView;
import de.mossgrabers.framework.view.Views;


/**
 * The chord view.
 *
 * @author Jürgen Moßgraber
 */
public class ChordsView extends AbstractChordView<LaunchpadControlSurface, LaunchpadConfiguration> implements IScrollableView
{
    private static final int [] MODULATION_INTENSITIES =
    {
        0,
        32,
        64,
        92,
        127
    };

    private boolean             playControls           = false;
    private boolean             isSustain              = false;
    private boolean             isPitchDown            = false;
    private boolean             isPitchUp              = false;
    private int                 isModulation           = 0;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ChordsView (final LaunchpadControlSurface surface, final IModel model)
    {
        this (Views.NAME_CHORDS, surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public ChordsView (final String name, final LaunchpadControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);

        final Configuration configuration = this.surface.getConfiguration ();
        configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (this.model.canSelectedTrackHoldNotes ())
        {
            if (buttonID == ButtonID.SCENE1 || buttonID == ButtonID.SCENE2 || buttonID == ButtonID.SCENE7 || buttonID == ButtonID.SCENE8)
                return LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI;

            if (buttonID == ButtonID.SCENE4)
                return this.playControls ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;

            if (buttonID == ButtonID.SCENE5)
                return LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_HI;
        }

        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
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
                this.playControls = !this.playControls;
                this.setBlockedNotes (this.playControls ? 8 : 0);
                break;
            case SCENE5:
                this.activatePreferredView (Views.PLAY);
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

        if (!this.playControls)
            return;

        final IPadGrid padGrid = this.surface.getPadGrid ();

        final int startNote = this.scales.getStartNote ();

        padGrid.light (startNote, this.isSustain ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO);

        padGrid.light (startNote + 1, this.isPitchDown ? LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_LO);
        padGrid.light (startNote + 2, this.isPitchUp ? LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_LO);

        for (int i = 0; i < 5; i++)
            padGrid.light (startNote + 3 + i, this.isModulation == i ? LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_LO);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        if (this.playControls)
        {
            final int pos = key - this.scales.getStartNote ();
            if (pos < 8)
            {
                final boolean isDown = velocity > 0;

                final IMidiInput midiInput = this.surface.getMidiInput ();
                switch (pos)
                {
                    // Sustain
                    case 0:
                        this.isSustain = isDown;
                        midiInput.sendRawMidiEvent (MidiConstants.CMD_CC, 64, this.isSustain ? 127 : 0);
                        return;

                    // Pitch
                    case 1:
                        this.isPitchDown = isDown;
                        midiInput.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND, 0, this.isPitchDown ? Math.abs (velocity / 2 - 63) : 64);
                        return;
                    case 2:
                        this.isPitchUp = isDown;
                        midiInput.sendRawMidiEvent (MidiConstants.CMD_PITCHBEND, 0, this.isPitchUp ? 64 + velocity / 2 : 64);
                        return;

                    // Modulation
                    default:
                        if (isDown)
                        {
                            this.isModulation = pos - 3;
                            midiInput.sendRawMidiEvent (MidiConstants.CMD_CC, 1, MODULATION_INTENSITIES[this.isModulation]);
                        }
                        return;
                }
            }
        }

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