// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.view;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.Views;


/**
 * The play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final APCControlSurface surface, final IModel model)
    {
        super (Views.NAME_PLAY, surface, model, surface.isMkII ());
    }


    /** {@inheritDoc} */
    @Override
    protected void playNote (final int note, final int velocity)
    {
        if (!this.surface.isMkII ())
            this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (index >= 0 && index < 8)
            return index == 2 ? ColorManager.BUTTON_STATE_OFF : ColorManager.BUTTON_STATE_ON;

        return ColorManager.BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.scales.nextScale ();
                this.updateScale ();
                this.surface.getDisplay ().notify ("Scale: " + this.scales.getScale ().getName ());
                break;

            case SCENE2:
                this.scales.prevScale ();
                this.updateScale ();
                this.surface.getDisplay ().notify ("Scale: " + this.scales.getScale ().getName ());
                break;

            case SCENE3:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                this.surface.getDisplay ().notify (isChromatic ? "Chromatic" : "In Key");
                break;

            case SCENE4:
                this.onOctaveUp (event);
                break;

            case SCENE5:
                this.onOctaveDown (event);
                break;

            default:
                // Not used
                break;
        }
        this.updateNoteMapping ();
    }
}