// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;


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
    public ColorView (final PushControlSurface surface, final Model model)
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
    public void onActivate ()
    {
        super.onActivate ();
        this.noteMap = Scales.getEmptyMatrix ();
        this.surface.setKeyTranslationTable (this.noteMap);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        switch (buttonID)
        {
            case PushControlSurface.PUSH_BUTTON_REPEAT:
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
            this.surface.getPadGrid ().light (36 + i, i < BitwigColors.BITWIG_COLORS.length ? BitwigColors.BITWIG_COLORS[i] : PadGrid.GRID_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int color = note - 36;
        if (color < BitwigColors.BITWIG_COLORS.length)
        {
            final double [] entry = BitwigColors.getColorEntry (BitwigColors.BITWIG_COLORS[color]);
            switch (this.mode)
            {
                case MODE_TRACK:
                    final IChannelBank tb = this.model.getCurrentTrackBank ();
                    final ITrack t = tb.getSelectedTrack ();
                    if (t == null)
                    {
                        final IMasterTrack master = this.model.getMasterTrack ();
                        if (master.isSelected ())
                            master.setColor (entry[0], entry[1], entry[2]);
                    }
                    else
                        tb.setTrackColor (t.getIndex (), entry[0], entry[1], entry[2]);
                    break;

                case MODE_LAYER:
                    final ICursorDevice cd = this.model.getCursorDevice ();
                    final IChannel deviceChain = cd.getSelectedLayerOrDrumPad ();
                    cd.setLayerOrDrumPadColor (deviceChain.getIndex (), entry[0], entry[1], entry[2]);
                    break;

                case MODE_CLIP:
                    final ICursorClip clip = ((DrumView) this.surface.getViewManager ().getView (Views.VIEW_DRUM)).getClip ();
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
        final int black = this.surface.getConfiguration ().isPush2 () ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, black);
    }
}