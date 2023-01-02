// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.EnumMap;
import java.util.Map;


/**
 * A select track command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private static final Map<Modes, Modes> TRACK_LAYER_MODE_MAP = new EnumMap<> (Modes.class);
    private static final Map<Modes, Modes> LAYER_TRACK_MODE_MAP = new EnumMap<> (Modes.class);
    static
    {
        TRACK_LAYER_MODE_MAP.put (Modes.TRACK, Modes.DEVICE_LAYER);
        TRACK_LAYER_MODE_MAP.put (Modes.VOLUME, Modes.DEVICE_LAYER_VOLUME);
        TRACK_LAYER_MODE_MAP.put (Modes.PAN, Modes.DEVICE_LAYER_PAN);
        TRACK_LAYER_MODE_MAP.put (Modes.SEND1, Modes.DEVICE_LAYER_SEND1);
        TRACK_LAYER_MODE_MAP.put (Modes.SEND2, Modes.DEVICE_LAYER_SEND2);
        TRACK_LAYER_MODE_MAP.put (Modes.SEND3, Modes.DEVICE_LAYER_SEND3);
        TRACK_LAYER_MODE_MAP.put (Modes.SEND4, Modes.DEVICE_LAYER_SEND4);
        TRACK_LAYER_MODE_MAP.put (Modes.SEND5, Modes.DEVICE_LAYER_SEND5);
        TRACK_LAYER_MODE_MAP.put (Modes.SEND6, Modes.DEVICE_LAYER_SEND6);
        TRACK_LAYER_MODE_MAP.put (Modes.SEND7, Modes.DEVICE_LAYER_SEND7);
        TRACK_LAYER_MODE_MAP.put (Modes.SEND8, Modes.DEVICE_LAYER_SEND8);

        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER, Modes.TRACK);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_VOLUME, Modes.VOLUME);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_PAN, Modes.PAN);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_SEND1, Modes.SEND1);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_SEND2, Modes.SEND2);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_SEND3, Modes.SEND3);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_SEND4, Modes.SEND4);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_SEND5, Modes.SEND5);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_SEND6, Modes.SEND6);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_SEND7, Modes.SEND7);
        LAYER_TRACK_MODE_MAP.put (Modes.DEVICE_LAYER_SEND8, Modes.SEND8);
    }

    private final boolean useFxBank;

    protected final int   index;
    protected final int   channel;


    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public SelectCommand (final int index, final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);

        final MCUConfiguration configuration = this.surface.getConfiguration ();
        this.useFxBank = configuration.shouldPinFXTracksToLastController () && this.surface.getSurfaceID () == configuration.getNumMCUDevices () - 1;

        this.index = index;
        this.channel = this.useFxBank ? this.index : this.surface.getExtenderOffset () + this.index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (this.handleButtonCombinations (event))
            return;

        if (event == ButtonEvent.UP)
            this.handleSelectEnter ();
        else if (event == ButtonEvent.LONG)
            this.handleLeave ();
    }


    private void handleSelectEnter ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ModeManager modeManager = this.surface.getModeManager ();

        // If layer modes are active select the layer...
        if (Modes.isLayerMode (modeManager.getActiveID ()))
        {
            final ILayer layer = cursorDevice.getLayerBank ().getItem (this.channel);
            if (!layer.isSelected ())
                layer.select ();
            return;
        }

        final ITrackBank trackBank = this.getTrackBank ();
        final ITrack track = trackBank.getItem (this.channel);

        // Select the track if not already selected...
        if (!track.isSelected ())
        {
            track.select ();
            return;
        }

        // If the track is a group dive in...
        if (track.isGroup ())
        {
            if (this.surface.getConfiguration ().isTrackNavigationFlat ())
                track.toggleGroupExpanded ();
            else
            {
                track.setGroupExpanded (true);
                track.enter ();
            }
            return;
        }

        // If the tracks cursor device has layers dive in...
        if (cursorDevice.hasLayers ())
        {
            final Modes layerMode = TRACK_LAYER_MODE_MAP.get (modeManager.getActiveID ());
            if (layerMode != null)
                modeManager.setActive (layerMode);
        }
    }


    private void handleLeave ()
    {
        // If layer modes are active switch back to track mode...
        final ModeManager modeManager = this.surface.getModeManager ();
        if (Modes.isLayerMode (modeManager.getActiveID ()))
        {
            final Modes trackMode = LAYER_TRACK_MODE_MAP.get (modeManager.getActiveID ());
            if (trackMode != null)
                modeManager.setActive (trackMode);
        }
        else
        {
            // ... otherwise leave the track group
            if (!this.surface.getConfiguration ().isTrackNavigationFlat ())
                this.getTrackBank ().selectParent ();
        }

        // Consume upcoming the button up event
        for (int i = 0; i < 8; i++)
            this.surface.setTriggerConsumed (ButtonID.get (ButtonID.ROW_SELECT_1, i));
    }


    private boolean handleButtonCombinations (final ButtonEvent event)
    {
        // Select Send channels if Send button is pressed
        if (this.surface.isPressed (ButtonID.SENDS))
        {
            if (event == ButtonEvent.DOWN)
                this.handleSendSelection ();
            return true;
        }

        // Execute stop if Select is pressed
        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.DOWN)
                this.getTrackBank ().getItem (this.channel).stop ();
            return true;
        }

        if (this.surface.isPressed (ButtonID.CONTROL))
        {
            if (event == ButtonEvent.DOWN)
            {
                final ITrack track = this.getTrackBank ().getItem (this.channel);
                if (track.isGroup ())
                    track.toggleGroupExpanded ();
            }
            return true;
        }

        if (this.surface.isPressed (ButtonID.ALT))
        {
            this.surface.getDisplay ().notify (AbstractConfiguration.getNewClipLengthValue (this.index));
            this.surface.getConfiguration ().setNewClipLength (this.index);
            return true;
        }

        if (this.surface.getConfiguration ().hasOnly1Fader ())
        {
            if (event != ButtonEvent.DOWN)
                return true;

            final ModeManager modeManager = this.surface.getModeManager ();

            // Select marker if marker mode is active
            if (modeManager.isActive (Modes.MARKERS))
            {
                this.model.getMarkerBank ().getItem (this.channel).select ();
                return true;
            }

            // Select parameter if device mode is active
            if (modeManager.isActive (Modes.DEVICE_PARAMS))
            {
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                if (cursorDevice.doesExist ())
                    cursorDevice.getParameterBank ().getItem (this.channel).select ();
                return true;
            }
        }

        return false;
    }


    private void handleSendSelection ()
    {
        final ITextDisplay display = this.surface.getTextDisplay ();
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null && effectTrackBank.getItem (this.channel).doesExist ())
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            final boolean isLayerMode = Modes.isLayerMode (modeManager.getActiveID ());
            modeManager.setActive (Modes.get (isLayerMode ? Modes.DEVICE_LAYER_SEND1 : Modes.SEND1, this.index));
            display.notify ("Send channel " + (this.channel + 1) + " selected.");
        }
        else
            display.notify ("Send channel " + (this.channel + 1) + " does not exist.");
        this.surface.setTriggerConsumed (ButtonID.SENDS);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || this.index >= 8)
            return;
        // Allow multi selection
        this.getTrackBank ().getItem (this.channel).toggleMultiSelect ();
    }


    protected ITrackBank getTrackBank ()
    {
        if (this.useFxBank)
        {
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null)
                return effectTrackBank;
        }
        return this.model.getCurrentTrackBank ();
    }
}
