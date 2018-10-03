// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;


/**
 * The Color view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorView extends AbstractView<PushControlSurface, PushConfiguration> implements SceneView
{
    /** What should the color be selected for? */
    public enum SelectMode
    {
    /** Select a track color. */
    MODE_TRACK,
    /** Select a layer color. */
    MODE_LAYER,
    /** Select a clip color. */
    MODE_CLIP
    }

    private SelectMode mode;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ColorView (final PushControlSurface surface, final IModel model)
    {
        super ("Color", surface, model);
        this.mode = SelectMode.MODE_TRACK;
    }


    /**
     * Set the color selections mode.
     *
     * @param mode The selection mode
     */
    public void setMode (final SelectMode mode)
    {
        this.mode = mode;
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        switch (buttonID)
        {
            case PushControlSurface.PUSH_BUTTON_REPEAT:
                return this.model.getHost ().hasRepeat ();

            case PushControlSurface.PUSH_BUTTON_OCTAVE_UP:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN:
                return false;

            default:
                return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        for (int i = 0; i < 64; i++)
            this.surface.getPadGrid ().light (36 + i, i < DAWColors.DAW_COLORS.length ? DAWColors.DAW_COLORS[i] : PadGrid.GRID_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int color = note - 36;
        if (color < DAWColors.DAW_COLORS.length)
        {
            final double [] entry = DAWColors.getColorEntry (DAWColors.DAW_COLORS[color]);
            switch (this.mode)
            {
                case MODE_TRACK:
                    final ITrack t = this.model.getSelectedTrack ();
                    if (t == null)
                    {
                        final IMasterTrack master = this.model.getMasterTrack ();
                        if (master.isSelected ())
                            master.setColor (entry[0], entry[1], entry[2]);
                    }
                    else
                        t.setColor (entry[0], entry[1], entry[2]);
                    break;

                case MODE_LAYER:
                    this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ().setColor (entry[0], entry[1], entry[2]);
                    break;

                case MODE_CLIP:
                    final IClip clip = this.model.getClip ();
                    if (clip != null)
                        clip.setColor (entry[0], entry[1], entry[2]);
                    break;
            }
        }
        this.surface.getViewManager ().restoreView ();
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final int colorOff = this.model.getColorManager ().getColor (AbstractSequencerView.COLOR_RESOLUTION_OFF);
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, colorOff);
    }
}