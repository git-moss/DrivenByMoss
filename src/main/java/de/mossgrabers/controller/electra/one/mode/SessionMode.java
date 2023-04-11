// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameter.PlayPositionParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The session mode. Provides 5 scenes and 5x5 clips.
 *
 * @author Jürgen Moßgraber
 */
public class SessionMode extends AbstractElectraOneMode
{
    private static final int        FIRST_TRACK_GROUP    = 550;

    /** The color for a scene. */
    public static final String      COLOR_SCENE          = "COLOR_SCENE";
    /** The color for a selected scene. */
    public static final String      COLOR_SELECTED_SCENE = "COLOR_SELECTED_SCENE";
    /** The color for no scene. */
    public static final String      COLOR_SCENE_OFF      = "COLOR_SELECTED_OFF";

    private static final String []  FUNCTION_NAMES       =
    {
        "New",
        "Delete",
        "Duplicate",
        "Quantize",
        "Stop"
    };

    private static final String []  NAVIGATION_NAMES     =
    {
        "Tracks -",
        "Tracks +",
        "",
        "Scenes -",
        "Scenes+"
    };

    private static final ColorEx [] NAVIGATION_COLORS    =
    {
        ColorEx.PURPLE,
        ColorEx.PURPLE,
        ColorEx.BLACK,
        ColorEx.OLIVE,
        ColorEx.OLIVE
    };


    private enum SessionUI
    {
        NORMAL,
        FUNCTIONS,
        NAVIGATION
    }


    private final ITransport   transport;
    private final IMasterTrack masterTrack;

    private SessionUI          sessionUI    = SessionUI.NORMAL;
    private int                clipFunction = 4;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SessionMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (5, "Session", surface, model);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();

        final IParameterProvider emptyParameterProvider = new EmptyParameterProvider (5);
        final IParameterProvider emptyParameterProvider2 = new EmptyParameterProvider (1);

        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                emptyParameterProvider, new FixedParameterProvider (this.masterTrack.getVolumeParameter ()),
                // Row 2
                emptyParameterProvider, new FixedParameterProvider (new PlayPositionParameter (model.getValueChanger (), this.transport, surface)),
                // Row 3
                emptyParameterProvider, emptyParameterProvider2,
                // Row 4
                emptyParameterProvider, emptyParameterProvider2,
                // Row 5
                emptyParameterProvider, emptyParameterProvider2,
                // Row 6
                emptyParameterProvider, emptyParameterProvider2));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
            return;

        if (index == 5)
        {
            if (event != ButtonEvent.DOWN)
                return;
            switch (row)
            {
                case 2:
                    this.sessionUI = this.sessionUI == SessionUI.NAVIGATION ? SessionUI.NORMAL : SessionUI.NAVIGATION;
                    break;
                case 3:
                    this.sessionUI = this.sessionUI == SessionUI.FUNCTIONS ? SessionUI.NORMAL : SessionUI.FUNCTIONS;
                    break;
                // Record
                case 4:
                    this.transport.startRecording ();
                    break;
                // Play
                case 5:
                    this.transport.play ();
                    break;

                default:
                    // Not used
                    break;
            }
        }

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = this.model.getSceneBank ();
        final ElectraOneConfiguration configuration = this.surface.getConfiguration ();

        // Scenes
        if (row == 0)
        {
            switch (this.sessionUI)
            {
                case NORMAL:
                    sceneBank.getItem (index).launch (event == ButtonEvent.DOWN, false);
                    break;
                case FUNCTIONS:
                    if (event == ButtonEvent.DOWN)
                        this.clipFunction = index;
                    break;
                case NAVIGATION:
                    if (event == ButtonEvent.DOWN)
                    {
                        if (index == 0)
                            tb.selectPreviousPage ();
                        else if (index == 1)
                            tb.selectNextPage ();
                        else if (index == 3)
                            sceneBank.selectPreviousPage ();
                        else if (index == 4)
                            sceneBank.selectNextPage ();
                    }
                    break;
            }
            return;
        }

        final ITrack track = tb.getItem (row - 1);
        if (!track.doesExist ())
            return;
        final ISlot slot = track.getSlotBank ().getItem (index);

        switch (this.sessionUI)
        {
            case NORMAL:
                if (slot.doesExist () && slot.hasContent ())
                    slot.launch (event == ButtonEvent.DOWN, false);
                break;

            case FUNCTIONS:
                if (event != ButtonEvent.DOWN)
                    return;
                switch (this.clipFunction)
                {
                    // New
                    case 0:
                        final int lengthInBeats = configuration.getNewClipLenghthInBeats (this.model.getTransport ().getQuartersPerMeasure ());
                        track.createClip (slot.getIndex (), lengthInBeats);
                        slot.select ();
                        break;
                    // Delete
                    case 1:
                        slot.remove ();
                        break;
                    // Duplicate
                    case 2:
                        slot.duplicate ();
                        break;
                    // Quantize
                    case 3:
                        slot.select ();
                        this.surface.scheduleTask ( () -> {
                            final IClip clip = this.model.getCursorClip ();
                            if (clip.doesExist ())
                                clip.quantize (configuration.getQuantizeAmount () / 100.0);
                        }, 100);
                        break;
                    // Stop
                    default:
                    case 4:
                        track.stop ();
                        break;
                }
                break;

            case NAVIGATION:
                if (event != ButtonEvent.DOWN)
                    return;
                // Calculate page offsets
                final int numTracks = tb.getPageSize ();
                final int numScenes = sceneBank.getPageSize ();
                final int trackPosition = tb.getItem (0).getPosition () / numTracks;
                final int scenePosition = sceneBank.getScrollPosition () / numScenes;
                final int selX = trackPosition;
                final int selY = scenePosition;
                final int padsX = 5;
                final int padsY = 5 + 1;
                final int offsetX = selX / padsX * padsX;
                final int offsetY = selY / padsY * padsY;
                sceneBank.scrollTo (offsetX * numScenes + index * padsY);
                tb.scrollTo (offsetY * numTracks + (row - 1) * padsX);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.pageCache.updateElement (2, 5, null, this.sessionUI == SessionUI.NAVIGATION ? ColorEx.SKY_BLUE : ColorEx.DARK_GRAY, Boolean.TRUE);
        this.pageCache.updateElement (3, 5, null, this.sessionUI == SessionUI.FUNCTIONS ? ColorEx.SKY_BLUE : ColorEx.DARK_GRAY, Boolean.TRUE);

        final int columns = 5;
        final int rows = 5;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = this.model.getSceneBank ();

        if (this.sessionUI == SessionUI.NORMAL || this.sessionUI == SessionUI.FUNCTIONS)
        {
            for (int column = 0; column < columns; column++)
            {
                final IScene scene = sceneBank.getItem (column);
                this.pageCache.updateGroupLabel (FIRST_TRACK_GROUP + column, scene.doesExist () ? scene.getPosition () + 1 + ": " + scene.getName () : "");

                if (this.sessionUI == SessionUI.FUNCTIONS)
                    this.pageCache.updateElement (0, column, FUNCTION_NAMES[column], this.clipFunction == column ? ColorEx.SKY_BLUE : ColorEx.DARK_GRAY, Boolean.TRUE);
                else
                    this.pageCache.updateElement (0, column, scene.getName (), scene.getColor (), Boolean.valueOf (scene.doesExist ()));

                for (int row = 1; row < rows + 1; row++)
                {
                    final ITrack t = tb.getItem (row - 1);
                    final boolean isArmed = t.isRecArm ();

                    final ISlotBank slotBank = t.getSlotBank ();
                    final ISlot slot = slotBank.getItem (column);

                    // Set the track name if the slot doesn't have one
                    final boolean slotDoesExist = slot.doesExist ();
                    String slotName = slotDoesExist && slot.hasContent () ? slot.getName () : "";
                    if (slotName.isBlank ())
                        slotName = slotDoesExist ? "(" + t.getName () + ")" : "-";
                    this.pageCache.updateElement (row, column, slotName, this.getPadColor (slot, isArmed), Boolean.valueOf (slotDoesExist));
                }
            }
        }
        else
        {
            // Navigation

            for (int column = 0; column < columns; column++)
            {
                this.pageCache.updateElement (0, column, NAVIGATION_NAMES[column], NAVIGATION_COLORS[column], Boolean.TRUE);

                final int numTracks = tb.getPageSize ();
                final int numScenes = sceneBank.getPageSize ();
                final int sceneCount = sceneBank.getItemCount ();
                final int trackCount = tb.getItemCount ();
                final int maxScenePads = sceneCount / numScenes + (sceneCount % numScenes > 0 ? 1 : 0);
                final int maxTrackPads = trackCount / numTracks + (trackCount % numTracks > 0 ? 1 : 0);
                final int scenePosition = sceneBank.getScrollPosition ();
                final int trackPosition = tb.getItem (0).getPosition ();
                final int sceneSelection = scenePosition / numScenes + (scenePosition % numScenes > 0 ? 1 : 0);
                final int trackSelection = trackPosition / numTracks + (trackPosition % numTracks > 0 ? 1 : 0);
                int selX = trackSelection;
                int selY = sceneSelection;
                final int padsX = columns;
                final int padsY = rows;
                final int offsetX = selX / padsX * padsX;
                final int offsetY = selY / padsY * padsY;
                final int maxX = maxTrackPads - offsetX;
                final int maxY = maxScenePads - offsetY;
                selX -= offsetX;
                selY -= offsetY;

                final ColorEx rowColor = column < maxX ? ColorEx.RED : ColorEx.BLACK;
                for (int y = 0; y < rows; y++)
                {
                    final boolean exists = y < maxY;
                    ColorEx color = exists ? rowColor : ColorEx.BLACK;
                    if (selX == column && selY == y)
                        color = ColorEx.ORANGE;
                    final String n = exists && column < maxX ? String.format ("Sc. %d - Tr. %d", Integer.valueOf (scenePosition + 1 + 5 * column), Integer.valueOf (trackPosition + 1 + 5 * y)) : "-";
                    this.pageCache.updateElement (column + 1, y, n, color, Boolean.TRUE);
                }
            }
        }

        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.masterTrack.getVolumeStr ()), 15));
        this.pageCache.updateValue (1, 5, 0, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getBeatText ()), 15));
        this.pageCache.updateElement (1, 5, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getPositionText ()), 15), null, null);

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);

        this.pageCache.flush ();
    }


    /**
     * Get the pad color for a slot.
     *
     * @param slot The slot
     * @param isArmed True if armed
     * @return The light info
     */
    public ColorEx getPadColor (final ISlot slot, final boolean isArmed)
    {
        if (slot.isSelected ())
            return ColorEx.WHITE;

        if (slot.isRecordingQueued ())
            return ColorEx.DARK_RED;

        if (slot.isRecording ())
            return ColorEx.RED;

        if (slot.isPlayingQueued ())
            return ColorEx.DARK_GREEN;

        if (slot.isPlaying ())
            return ColorEx.GREEN;

        if (slot.hasContent ())
            return slot.getColor ();

        return slot.doesExist () && isArmed && this.surface.getConfiguration ().isDrawRecordStripe () ? ColorEx.RED_WINE : ColorEx.BLACK;
    }
}