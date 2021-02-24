// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Menu for adding tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AddTrackMode extends BaseMode<IItem>
{
    private static final String STR_EMPTY = "Empty";

    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AddTrackMode (final PushControlSurface surface, final IModel model)
    {
        super ("Add Track", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (index < 4)
        {
            this.model.getApplication ().addAudioTrack ();

            if (index > 0)
            {
                this.surface.scheduleTask ( () -> {

                    final PushConfiguration conf = this.surface.getConfiguration ();
                    this.model.getCursorTrack ().addDevice (conf.getAudioFavorite (index - 1));

                }, 300);
            }
        }
        else
        {
            this.model.getApplication ().addEffectTrack ();

            if (index > 4)
            {
                this.surface.scheduleTask ( () -> {

                    final PushConfiguration conf = this.surface.getConfiguration ();
                    this.model.getCursorTrack ().addDevice (conf.getEffectFavorite (index - 5));

                }, 300);
            }
        }

        this.surface.getModeManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        this.model.getApplication ().addInstrumentTrack ();

        if (index > 0)
        {
            this.surface.scheduleTask ( () -> {

                final PushConfiguration conf = this.surface.getConfiguration ();
                this.model.getCursorTrack ().addDevice (conf.getInstrumentFavorite (index - 1));

            }, 300);
        }

        this.surface.getModeManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            if (index == 0)
                return this.isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_HI : PushColorManager.PUSH1_COLOR_GREEN_HI;
            if (index < 4)
                return this.isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_LO : PushColorManager.PUSH1_COLOR_GREEN_LO;
            if (index == 4)
                return this.isPush2 ? PushColorManager.PUSH2_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR_RED_HI;
            return this.isPush2 ? PushColorManager.PUSH2_COLOR2_BLUE_LO : PushColorManager.PUSH1_COLOR_RED_LO;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (index == 0)
                return this.isPush2 ? PushColorManager.PUSH2_COLOR2_YELLOW_HI : PushColorManager.PUSH1_COLOR2_YELLOW_HI;
            return this.isPush2 ? PushColorManager.PUSH2_COLOR2_YELLOW_LO : PushColorManager.PUSH1_COLOR2_YELLOW_LO;
        }

        return this.colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final PushConfiguration conf = this.surface.getConfiguration ();

        display.setCell (0, 0, STR_EMPTY);
        for (int i = 0; i < 7; i++)
            display.setCell (0, i + 1, StringUtils.shortenAndFixASCII (conf.getInstrumentFavorite (i).getName (), 8));

        display.setBlock (1, 0, "INSTRUMENT");
        display.setBlock (2, 0, "AUDIO").setBlock (2, 2, "EFFECT");

        display.setCell (3, 0, STR_EMPTY);
        display.setCell (3, 4, STR_EMPTY);
        for (int i = 0; i < 3; i++)
        {
            display.setCell (3, 1 + i, StringUtils.shortenAndFixASCII (conf.getAudioFavorite (i).getName (), 8));
            display.setCell (3, 5 + i, StringUtils.shortenAndFixASCII (conf.getEffectFavorite (i).getName (), 8));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final PushConfiguration conf = this.surface.getConfiguration ();

        display.addOptionElement ("Instrument", STR_EMPTY, false, ColorEx.YELLOW, "Audio", STR_EMPTY, false, ColorEx.GREEN, false, false);

        IDeviceMetadata instrFav = conf.getInstrumentFavorite (0);
        IDeviceMetadata audioFav = conf.getAudioFavorite (0);
        display.addOptionElement ("", instrFav == null ? "" : instrFav.getName (), false, "", audioFav == null ? "" : audioFav.getName (), false, false);
        instrFav = conf.getInstrumentFavorite (1);
        audioFav = conf.getAudioFavorite (1);
        display.addOptionElement ("", instrFav == null ? "" : instrFav.getName (), false, "", audioFav == null ? "" : audioFav.getName (), false, false);
        instrFav = conf.getInstrumentFavorite (2);
        audioFav = conf.getAudioFavorite (2);
        display.addOptionElement ("", instrFav == null ? "" : instrFav.getName (), false, "", audioFav == null ? "" : audioFav.getName (), false, false);

        instrFav = conf.getInstrumentFavorite (3);
        display.addOptionElement ("", instrFav == null ? "" : instrFav.getName (), false, null, "Effect", STR_EMPTY, false, ColorEx.BLUE, false, false);

        instrFav = conf.getInstrumentFavorite (4);
        IDeviceMetadata effectFavorite = conf.getEffectFavorite (0);
        display.addOptionElement ("", instrFav == null ? "" : instrFav.getName (), false, "", effectFavorite == null ? "" : effectFavorite.getName (), false, false);
        instrFav = conf.getInstrumentFavorite (5);
        effectFavorite = conf.getEffectFavorite (1);
        display.addOptionElement ("", instrFav == null ? "" : instrFav.getName (), false, "", effectFavorite == null ? "" : effectFavorite.getName (), false, false);
        instrFav = conf.getInstrumentFavorite (6);
        effectFavorite = conf.getEffectFavorite (2);
        display.addOptionElement ("", instrFav == null ? "" : instrFav.getName (), false, "", effectFavorite == null ? "" : effectFavorite.getName (), false, false);
    }
}
