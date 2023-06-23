// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.grid.IVirtualFader;
import de.mossgrabers.framework.controller.grid.IVirtualFaderCallback;
import de.mossgrabers.framework.controller.grid.VirtualFaderImpl;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ScrollStates;

import java.util.Optional;


/**
 * A view for mixing with track select, mute, solo, rec arm, stop clip, volume and panorama.
 *
 * @author Jürgen Moßgraber
 */
public class MixView extends AbstractView<LaunchpadControlSurface, LaunchpadConfiguration> implements IVirtualFaderCallback, IScrollableView
{
    private final IVirtualFader fader;


    private enum FaderMode
    {
        VOLUME,
        PAN,
        SEND1,
        SEND2
    }


    private FaderMode faderMode = FaderMode.VOLUME;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public MixView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Mix", surface, model);

        this.fader = new VirtualFaderImpl (model.getHost (), this);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            if (track.doesExist ())
            {
                final boolean isSelected = track.isSelected ();
                final boolean hasSends = track.getSendBank ().getItemCount () > 0;

                // Volume
                padGrid.light (92 + i, this.colorManager.getColorIndex (DAWColor.getColorID (track.getColor ())));
                // Panorama
                padGrid.light (84 + i, isSelected ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);
                // Send 1
                final int send1ColorID;
                if (hasSends)
                    send1ColorID = isSelected ? LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
                else
                    send1ColorID = LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
                padGrid.light (76 + i, send1ColorID);
                // Send 2
                final int send2ColorID;
                if (hasSends)
                    send2ColorID = isSelected ? LaunchpadColorManager.LAUNCHPAD_COLOR_LIME_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
                else
                    send2ColorID = LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
                padGrid.light (68 + i, send2ColorID);
                // Stop
                padGrid.light (60 + i, this.surface.isPressed (ButtonID.get (ButtonID.PAD25, i)) ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED : LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE);
                // Mute
                padGrid.light (52 + i, track.isMute () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI);
                // Solo
                padGrid.light (44 + i, track.isSolo () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_MD);
                // Record Arm
                padGrid.light (36 + i, track.isRecArm () ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);
            }
            else
            {
                padGrid.light (92 + i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
                padGrid.light (84 + i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
                padGrid.light (76 + i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
                padGrid.light (68 + i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
                padGrid.light (60 + i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
                padGrid.light (52 + i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
                padGrid.light (44 + i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
                padGrid.light (36 + i, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int n = note - 36;
        final int index = n % 8;
        final int what = n / 8;

        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        final IDisplay display = this.surface.getDisplay ();

        switch (what)
        {
            case 7:
                this.faderMode = FaderMode.VOLUME;
                display.notify ("Volume");
                this.selectTrack (track);
                break;

            case 6:
                this.faderMode = FaderMode.PAN;
                display.notify ("Panorama");
                this.selectTrack (track);
                break;

            case 5:
                this.faderMode = FaderMode.SEND1;
                final ISend send1 = track.getSendBank ().getItem (0);
                display.notify ("Send 1: " + (send1.doesExist () ? send1.getName () : "None"));
                this.selectTrack (track);
                break;

            case 4:
                this.faderMode = FaderMode.SEND2;
                final ISend send2 = track.getSendBank ().getItem (1);
                display.notify ("Send 2: " + (send2.doesExist () ? send2.getName () : "None"));
                this.selectTrack (track);
                break;

            case 3:
                track.stop (false);
                display.notify ("Stop clip");
                break;

            case 2:
                track.toggleMute ();
                display.notify ("Mute");
                break;

            case 1:
                track.toggleSolo ();
                display.notify ("Solo");
                break;

            case 0:
                track.toggleRecArm ();
                display.notify ("Rec Arm");
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
        final int index = 7 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ());

        int color = 0;
        int value = 0;

        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isPresent ())
        {
            switch (this.faderMode)
            {
                default:
                case VOLUME:
                    value = track.get ().getVolume ();
                    color = LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN;
                    break;
                case PAN:
                    value = track.get ().getPan ();
                    color = LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI;
                    break;
                case SEND1:
                    final ISend send1 = track.get ().getSendBank ().getItem (0);
                    value = send1.doesExist () ? send1.getValue () : 0;
                    color = LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID_HI;
                    break;
                case SEND2:
                    final ISend send2 = track.get ().getSendBank ().getItem (1);
                    value = send2.doesExist () ? send2.getValue () : 0;
                    color = LaunchpadColorManager.LAUNCHPAD_COLOR_LIME_HI;
                    break;
            }
        }

        this.fader.setup (color, this.faderMode == FaderMode.PAN);
        this.fader.setValue (value);

        return this.fader.getColorState (index);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;
        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isEmpty ())
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        this.fader.moveTo (7 - index, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isEmpty ())
            return 0;
        switch (this.faderMode)
        {
            default:
            case VOLUME:
                return track.get ().getVolume ();
            case PAN:
                return track.get ().getPan ();
            case SEND1:
                final ISend send1 = track.get ().getSendBank ().getItem (0);
                return send1.doesExist () ? send1.getValue () : 0;
            case SEND2:
                final ISend send2 = track.get ().getSendBank ().getItem (1);
                return send2.doesExist () ? send2.getValue () : 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isEmpty ())
            return;
        switch (this.faderMode)
        {
            default:
            case VOLUME:
                track.get ().getVolumeParameter ().setValueImmediatly (value);
                break;
            case PAN:
                track.get ().getPanParameter ().setValueImmediatly (value);
                break;
            case SEND1:
                final ISend send1 = track.get ().getSendBank ().getItem (0);
                if (send1.doesExist ())
                    send1.setValueImmediatly (value);
                break;
            case SEND2:
                final ISend send2 = track.get ().getSendBank ().getItem (1);
                if (send2.doesExist ())
                    send2.setValueImmediatly (value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final Optional<ITrack> sel = tb.getSelectedItem ();
        final int selIndex = sel.isPresent () ? sel.get ().getIndex () : -1;
        final ISceneBank sceneBank = tb.getSceneBank ();
        scrollStates.setCanScrollLeft (selIndex > 0 || tb.canScrollPageBackwards ());
        scrollStates.setCanScrollRight (selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollPageForwards ());
        scrollStates.setCanScrollUp (sceneBank.canScrollPageBackwards ());
        scrollStates.setCanScrollDown (sceneBank.canScrollPageForwards ());
    }


    private void selectTrack (final ITrack track)
    {
        if (track.isSelected ())
            return;
        track.select ();
        this.surface.getDisplay ().notify (track.getPosition () + 1 + ": " + track.getName ());
    }
}