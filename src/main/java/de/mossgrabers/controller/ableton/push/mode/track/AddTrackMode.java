// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.BaseMode;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Menu for adding tracks.
 *
 * @author Jürgen Moßgraber
 */
public class AddTrackMode extends BaseMode<IItem>
{
    private static final AddMode []                                     TOP_MENU             =
    {
        AddMode.INSTRUMENT,
        AddMode.AUDIO,
        AddMode.EFFECT,
        null,
        AddMode.DEVICE,
        null,
        null,
        null
    };

    private static final String []                                      SUB_MENU             =
    {
        "Add Track",
        "",
        "",
        "",
        "Add Device",
        "",
        "",
        ""
    };

    private final Map<ColorEx, Integer>                                 buttonColorsHiFirst  = new HashMap<> ();
    private final Map<ColorEx, Integer>                                 buttonColorsLoFirst  = new HashMap<> ();
    private final Map<ColorEx, Integer>                                 buttonColorsHiSecond = new HashMap<> ();
    private final Map<ColorEx, Integer>                                 buttonColorsLoSecond = new HashMap<> ();

    private AddMode                                                     addMode              = AddMode.INSTRUMENT;
    private final BrowserCommand<PushControlSurface, PushConfiguration> browserCommand;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AddTrackMode (final PushControlSurface surface, final IModel model)
    {
        super ("Add Track", surface, model);

        this.browserCommand = new BrowserCommand<> (model, surface);

        this.buttonColorsHiFirst.put (ColorEx.YELLOW, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_YELLOW_HI : PushColorManager.PUSH1_COLOR_YELLOW_MD));
        this.buttonColorsHiFirst.put (ColorEx.GREEN, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_GREEN_HI : PushColorManager.PUSH1_COLOR_GREEN_HI));
        this.buttonColorsHiFirst.put (ColorEx.BLUE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR_RED_HI));
        this.buttonColorsHiFirst.put (ColorEx.ORANGE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_AMBER_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI));
        this.buttonColorsHiFirst.put (ColorEx.DARK_ORANGE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_AMBER_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI));

        this.buttonColorsLoFirst.put (ColorEx.YELLOW, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_YELLOW_LO : PushColorManager.PUSH1_COLOR_YELLOW_LO));
        this.buttonColorsLoFirst.put (ColorEx.GREEN, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_GREEN_LO : PushColorManager.PUSH1_COLOR_GREEN_LO));
        this.buttonColorsLoFirst.put (ColorEx.BLUE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_BLUE_LO : PushColorManager.PUSH1_COLOR_RED_LO));
        this.buttonColorsLoFirst.put (ColorEx.ORANGE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_AMBER_LO : PushColorManager.PUSH1_COLOR_ORANGE_LO));
        this.buttonColorsLoFirst.put (ColorEx.DARK_ORANGE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_AMBER_LO : PushColorManager.PUSH1_COLOR_ORANGE_LO));

        this.buttonColorsHiSecond.put (ColorEx.YELLOW, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_YELLOW_HI : PushColorManager.PUSH1_COLOR2_YELLOW_HI));
        this.buttonColorsHiSecond.put (ColorEx.GREEN, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_GREEN_HI : PushColorManager.PUSH1_COLOR2_GREEN_HI));
        this.buttonColorsHiSecond.put (ColorEx.BLUE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR2_RED_HI));
        this.buttonColorsHiSecond.put (ColorEx.ORANGE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_AMBER_HI : PushColorManager.PUSH1_COLOR2_AMBER_HI));
        this.buttonColorsHiSecond.put (ColorEx.DARK_ORANGE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_AMBER_HI : PushColorManager.PUSH1_COLOR2_AMBER_HI));

        this.buttonColorsLoSecond.put (ColorEx.YELLOW, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_YELLOW_LO : PushColorManager.PUSH1_COLOR2_YELLOW_LO));
        this.buttonColorsLoSecond.put (ColorEx.GREEN, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_GREEN_LO : PushColorManager.PUSH1_COLOR2_GREEN_LO));
        this.buttonColorsLoSecond.put (ColorEx.BLUE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_BLUE_LO : PushColorManager.PUSH1_COLOR2_RED_LO));
        this.buttonColorsLoSecond.put (ColorEx.ORANGE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_AMBER_LO : PushColorManager.PUSH1_COLOR2_AMBER_LO));
        this.buttonColorsLoSecond.put (ColorEx.DARK_ORANGE, Integer.valueOf (this.isPushModern ? PushColorManager.PUSH2_COLOR2_AMBER_LO : PushColorManager.PUSH1_COLOR2_AMBER_LO));
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        this.surface.getModeManager ().restore ();

        final ChannelType channelType = this.addMode.getChannelType ();
        final Optional<IDeviceMetadata> favorite;
        if (index == 0)
        {
            if (this.addMode == AddMode.DEVICE)
            {
                this.browserCommand.startBrowser (true, false);
                return;
            }
            favorite = Optional.empty ();
        }
        else
        {
            final PushConfiguration conf = this.surface.getConfiguration ();
            switch (this.addMode)
            {
                case INSTRUMENT:
                    favorite = conf.getInstrumentFavorite (index - 1);
                    break;
                case AUDIO:
                    favorite = conf.getAudioFavorite (index - 1);
                    break;
                case EFFECT:
                    favorite = conf.getEffectFavorite (index - 1);
                    break;
                case DEVICE:
                    favorite = conf.getDeviceFavorite (index - 1);
                    break;
                default:
                    return;
            }
        }

        String channelName = null;
        IDeviceMetadata deviceMetadata = null;
        if (favorite.isPresent ())
        {
            deviceMetadata = favorite.get ();
            channelName = deviceMetadata.name ();
        }

        if (channelType == ChannelType.UNKNOWN)
        {
            if (deviceMetadata != null)
                this.model.getCursorTrack ().addDevice (deviceMetadata);
        }
        else
            this.model.getTrackBank ().addChannel (channelType, channelName, deviceMetadata == null ? Collections.emptyList () : Collections.singletonList (deviceMetadata));
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP && TOP_MENU[index] != null)
            this.addMode = TOP_MENU[index];
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final ColorEx color = this.addMode.getColor ();
            return index == 0 ? this.buttonColorsHiFirst.get (color).intValue () : this.buttonColorsLoFirst.get (color).intValue ();
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            if (TOP_MENU[index] == null)
                return this.colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);
            final ColorEx color = TOP_MENU[index].getColor ();
            return TOP_MENU[index] == this.addMode ? this.buttonColorsHiSecond.get (color).intValue () : this.buttonColorsLoSecond.get (color).intValue ();
        }

        return this.colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        display.setBlock (1, 0, SUB_MENU[0]);
        display.setBlock (1, 2, SUB_MENU[4]);

        for (int i = 0; i < 8; i++)
        {
            final String lowerMenu;
            if (i == 0)
                lowerMenu = this.addMode == AddMode.DEVICE ? "Browse" : "Empty";
            else
            {
                final Optional<IDeviceMetadata> favorite = this.getFavorite (i - 1);
                lowerMenu = favorite.isEmpty () ? "" : StringUtils.limit (favorite.get ().name (), 13);
            }
            display.setCell (0, i, TOP_MENU[i] == null ? "" : TOP_MENU[i].getLabel ());
            display.setCell (2, i, i == 0 ? this.addMode.getLabel () : "");
            display.setCell (3, i, lowerMenu);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        for (int i = 0; i < 8; i++)
        {
            final String lowerMenu;
            String lowerLabel = "";
            ColorEx lowerMenuColor = null;
            if (i == 0)
            {
                lowerMenu = this.addMode == AddMode.DEVICE ? "Browse" : "Empty";
                lowerLabel = this.addMode.getLabel ();
                lowerMenuColor = this.addMode.getColor ();
            }
            else
            {
                final Optional<IDeviceMetadata> favorite = this.getFavorite (i - 1);
                lowerMenu = favorite.isEmpty () ? "" : StringUtils.limit (favorite.get ().name (), 13);
            }
            final String topLabel = TOP_MENU[i] == null ? "" : TOP_MENU[i].getLabel ();
            final ColorEx topColor = TOP_MENU[i] == null ? null : TOP_MENU[i].getColor ();
            display.addOptionElement (SUB_MENU[i], topLabel, false, topColor, lowerLabel, lowerMenu, false, lowerMenuColor, false, false);
        }
    }


    /**
     * Get the selected favorite depending on the current add mode.
     *
     * @param index The index of the favorite
     * @return The metadata of the favorite, if one is configured
     */
    private Optional<IDeviceMetadata> getFavorite (final int index)
    {
        final PushConfiguration conf = this.surface.getConfiguration ();
        switch (this.addMode)
        {
            case INSTRUMENT:
                return conf.getInstrumentFavorite (index);
            case AUDIO:
                return conf.getAudioFavorite (index);
            case EFFECT:
                return conf.getEffectFavorite (index);
            case DEVICE:
                return conf.getDeviceFavorite (index);
            default:
                return Optional.empty ();
        }
    }
}
