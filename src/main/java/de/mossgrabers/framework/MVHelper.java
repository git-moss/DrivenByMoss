// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.scale.Scales;

import java.util.Optional;
import java.util.function.Supplier;


/**
 * Helper functions which would otherwise be repeated in different view and mode classes.
 *
 * @param <C> The type of the configuration
 * @param <S> The type of the control surface
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MVHelper<S extends IControlSurface<C>, C extends Configuration>
{
    private static final String SELECTED_TRACK_NONE = "Selected track: None";
    private static final String NONE                = "None";
    private static final int    DISPLAY_DELAY       = 200;

    private final IModel        model;
    private final ITransport    transport;
    private final S             surface;
    private final IDisplay      display;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MVHelper (final IModel model, final S surface)
    {
        this.model = model;
        this.surface = surface;
        this.display = this.surface == null ? null : this.surface.getDisplay ();
        this.transport = this.model == null ? null : this.model.getTransport ();
    }


    /**
     * Display the name of the selected track.
     */
    public void notifySelectedTrack ()
    {
        this.delayDisplay ( () -> {

            final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
            final Optional<ITrack> selectedTrack = currentTrackBank.getSelectedItem ();
            if (selectedTrack.isEmpty ())
                return SELECTED_TRACK_NONE;

            final ITrack t = selectedTrack.get ();
            if (!t.doesExist ())
                return SELECTED_TRACK_NONE;

            return t.getPosition () + 1 + ": " + t.getName ();

        });
    }


    /**
     * Display the range of the track page.
     */
    public void notifyTrackRange ()
    {
        this.delayDisplay ( () -> {

            final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
            final int scrollPosition = currentTrackBank.getScrollPosition ();
            return "Tracks: " + (scrollPosition + 1) + "-" + (scrollPosition + currentTrackBank.getPageSize ());

        });
    }


    /**
     * Display the range of the layer page.
     */
    public void notifyLayerRange ()
    {
        this.delayDisplay ( () -> {

            final ISpecificDevice specificDevice = this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
            final ILayerBank layerBank = specificDevice.getLayerBank ();
            final int scrollPosition = layerBank.getScrollPosition ();
            if (specificDevice.hasLayers () && scrollPosition >= 0)
                return "Layers: " + (scrollPosition + 1) + "-" + (scrollPosition + layerBank.getPageSize ());
            return "";
        });
    }


    /**
     * Display the range of the drum pad page.
     */
    public void notifyDrumPadRange ()
    {
        this.delayDisplay ( () -> {

            final ISpecificDevice specificDevice = this.model.getDrumDevice ();
            final IDrumPadBank drumPadBank = specificDevice.getDrumPadBank ();
            final int scrollPosition = drumPadBank.getScrollPosition ();
            if (specificDevice.hasDrumPads () && scrollPosition >= 0)
                return "Drum Pads: " + scrollPosition + "-" + (scrollPosition + drumPadBank.getPageSize ());
            return "";

        });
    }


    /**
     * Display the range of the sends page.
     *
     * @param sendBank The send bank
     */
    public void notifySelectedSends (final ISendBank sendBank)
    {
        this.delayDisplay ( () -> {

            final int scrollPosition = sendBank.getScrollPosition () + 1;
            return "Sends: " + scrollPosition + "-" + (scrollPosition + 1);

        });
    }


    /**
     * Display the name of the selected cursor device.
     */
    public void notifySelectedDevice ()
    {
        this.delayDisplay ( () -> {

            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            return "Selected device: " + (cursorDevice.doesExist () ? cursorDevice.getName () : NONE);

        });
    }


    /**
     * Display the name of the selected layer if any.
     */
    public void notifySelectedLayer ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (!cursorDevice.doesExist () || !cursorDevice.hasLayers ())
            return;

        this.delayDisplay ( () -> {

            final Optional<ILayer> selectedLayer = cursorDevice.getLayerBank ().getSelectedItem ();
            return "Selected layer: " + (selectedLayer.isPresent () ? selectedLayer.get ().getName () : NONE);

        });
    }


    /**
     * Display the name of the selected cursor device and parameter page.
     */
    public void notifySelectedDeviceAndParameterPage ()
    {
        this.delayDisplay ( () -> {

            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (!cursorDevice.doesExist ())
                return "No device selected";

            String text = cursorDevice.getName ();

            final Optional<String> selectedItem = cursorDevice.getParameterPageBank ().getSelectedItem ();
            if (selectedItem.isPresent ())
            {
                String pageName = selectedItem.get ();
                if (pageName == null || pageName.isBlank ())
                    pageName = "None";
                text += " - " + pageName;
            }

            return text;
        });
    }


    /**
     * Display the name of the first device on the device chain and parameter page.
     */
    public void notifyFirstDeviceAndParameterPage ()
    {
        this.delayDisplay ( () -> {

            final ISpecificDevice cursorDevice = this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
            if (cursorDevice.doesExist ())
            {
                final Optional<String> selectedItem = cursorDevice.getParameterPageBank ().getSelectedItem ();
                if (selectedItem.isPresent ())
                    return cursorDevice.getName () + " - " + selectedItem.get ();
            }

            return "No device selected";

        });
    }


    /**
     * Display the name of the selected parameter page.
     */
    public void notifySelectedParameterPage ()
    {
        this.delayDisplay ( () -> {

            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (cursorDevice.doesExist ())
            {
                final Optional<String> selectedItem = cursorDevice.getParameterPageBank ().getSelectedItem ();
                if (selectedItem.isPresent ())
                    return "Page: " + selectedItem.get ();
            }
            return "Page: " + NONE;

        });
    }


    /**
     * Display the name and value of the given parameter.
     *
     * @param parameter The parameter to display
     */
    public void notifyParameter (final IParameter parameter)
    {
        if (parameter.doesExist ())
            this.delayDisplay ( () -> parameter.getName () + ": " + parameter.getDisplayedValue ());
    }


    /**
     * Display the name of the selected user parameter page.
     */
    public void notifySelectedUserPage ()
    {
        this.delayDisplay ( () -> {

            final IParameterBank userBank = this.model.getUserParameterBank ();
            final int page = userBank.getScrollPosition () / userBank.getPageSize ();
            return "User Page: " + (page + 1);

        });
    }


    /**
     * Display the name of the selected item in the current mode.
     *
     * @param mode The mode
     */
    public void notifySelectedItem (final IMode mode)
    {
        this.delayDisplay ( () -> {

            final Optional<String> selectedItemName = mode.getSelectedItemName ();
            return selectedItemName.isPresent () ? selectedItemName.get () : NONE;

        });
    }


    /**
     * Display the scene range in the current page.
     */
    public void notifyScenePage ()
    {
        this.delayDisplay ( () -> {

            final ISceneBank sceneBank = this.model.getSceneBank ();
            int lastScene = -1;
            for (int i = sceneBank.getPageSize () - 1; i >= 0; i--)
            {
                if (sceneBank.getItem (i).doesExist ())
                {
                    lastScene = i;
                    break;
                }
            }

            if (lastScene == -1)
                return "No scenes";

            final IScene first = sceneBank.getItem (0);
            final String firstText = String.format ("%d: %s", Integer.valueOf (first.getPosition () + 1), first.getName ());
            if (lastScene == 0)
                return firstText;
            final IScene last = sceneBank.getItem (lastScene);
            return firstText + String.format (" - %d: %s", Integer.valueOf (last.getPosition () + 1), last.getName ());

        });
    }


    /**
     * Display the current tempo.
     */
    public void notifyTempo ()
    {
        this.delayDisplay ( () -> "Tempo: " + this.transport.formatTempo (this.transport.getTempo ()));
    }


    /**
     * Display the current play position.
     */
    public void notifyPlayPosition ()
    {
        this.delayDisplay ( () -> "Play Pos.: " + this.transport.getBeatText ());
    }


    /**
     * Display the current edit page of the note clip.
     *
     * @param clip The clip
     */
    public void notifyEditPage (final INoteClip clip)
    {
        if (clip != null && clip.doesExist ())
            this.delayDisplay ( () -> "Edit page: " + (clip.getEditPage () + 1));
    }


    /**
     * Display the currently selected scale.
     *
     * @param scales The scales
     */
    public void notifyScale (final Scales scales)
    {
        this.delayDisplay ( () -> "Scale: " + scales.getScale ().getName ());
    }


    /**
     * Notify a text after 200ms.
     *
     * @param supplier The supplier to provide the text
     */
    public void delayDisplay (final Supplier<String> supplier)
    {
        this.surface.scheduleTask ( () -> {

            final String message = supplier.get ();
            if (message != null && !message.isBlank ())
                this.display.notify (message);

        }, DISPLAY_DELAY);
    }
}
