// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ActionCategory;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Proxy to the Bitwig Application.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ApplicationProxy
{
    /** The panel layout Arrange. */
    public static final String PANEL_LAYOUT_ARRANGE = "ARRANGE";
    /** The panel layout Mix. */
    public static final String PANEL_LAYOUT_MIX     = "MIX";
    /** The panel layout Edit. */
    public static final String PANEL_LAYOUT_EDIT    = "EDIT";
    /** The panel layout Play. */
    public static final String PANEL_LAYOUT_PLAY    = "PLAY";

    private Application        application;


    /**
     * Constructor.
     *
     * @param host The host object
     */
    public ApplicationProxy (final ControllerHost host)
    {
        this.application = host.createApplication ();

        this.application.hasActiveEngine ().markInterested ();
        this.application.projectName ().markInterested ();
        this.application.panelLayout ().markInterested ();
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.application.hasActiveEngine ().setIsSubscribed (enable);
        this.application.projectName ().setIsSubscribed (enable);
        this.application.panelLayout ().setIsSubscribed (enable);
    }


    /**
     * Returns whether the current project's audio engine is active.
     *
     * @return True if active
     */
    public boolean isEngineActive ()
    {
        return this.application.hasActiveEngine ().get ();
    }


    /**
     * Get the name of the active project.
     *
     * @return The name
     */
    public String getProjectName ()
    {
        return this.application.projectName ().get ();
    }


    /**
     * Switch to the previous open project.
     */
    public void previousProject ()
    {
        this.application.previousProject ();
    }


    /**
     * Switch to the next open project.
     */
    public void nextProject ()
    {
        this.application.nextProject ();
    }


    /**
     * Returns whether the current Bitwig panel layout is ARRANGE.
     *
     * @return True if arrange
     */
    public boolean isArrangeLayout ()
    {
        return PANEL_LAYOUT_ARRANGE.equals (this.application.panelLayout ().get ());
    }


    /**
     * Returns whether the current Bitwig panel layout is MIX.
     *
     * @return True if mixer
     */
    public boolean isMixerLayout ()
    {
        return PANEL_LAYOUT_MIX.equals (this.application.panelLayout ().get ());
    }


    /**
     * Returns whether the current Bitwig panel layout is EDIT.
     *
     * @return True if edit
     */
    public boolean isEditLayout ()
    {
        return PANEL_LAYOUT_EDIT.equals (this.application.panelLayout ().get ());
    }


    /**
     * Returns whether the current Bitwig panel layout is PLAY.
     *
     * @return True if touch play
     */
    public boolean isPlayLayout ()
    {
        return PANEL_LAYOUT_PLAY.equals (this.application.panelLayout ().get ());
    }


    /**
     * Sets whether the active project's audio engine is active.
     *
     * @param active Current project's engine active.
     */
    public void setEngineActive (final boolean active)
    {
        if (active)
            this.application.activateEngine ();
        else
            this.application.deactivateEngine ();
    }


    /**
     * Toggles the on/off state of the audio engine.
     */
    public void toggleEngineActive ()
    {
        if (this.application.hasActiveEngine ().get ())
            this.application.deactivateEngine ();
        else
            this.application.activateEngine ();
    }


    /**
     * Switches the Bitwig Studio user interface to the panel layout with the given name.
     *
     * @param panelLayout The name of the new panel layout (ARRANGE, MIX, EDIT)
     */
    public void setPanelLayout (final String panelLayout)
    {
        this.application.setPanelLayout (panelLayout);
    }


    /**
     * Returns the active panel layout (ARRANGE, MIX or EDIT).
     *
     * @return (ARRANGE, MIX or EDIT)
     */
    public String getPanelLayout ()
    {
        return this.application.panelLayout ().get ();
    }


    /**
     * Toggles the visibility of the note editor panel.
     */
    public void toggleNoteEditor ()
    {
        this.application.toggleNoteEditor ();
    }


    /**
     * Toggles the visibility of the automation editor panel.
     */
    public void toggleAutomationEditor ()
    {
        this.application.toggleAutomationEditor ();
    }


    /**
     * Toggles the visibility of the device chain panel.
     */
    public void toggleDevices ()
    {
        this.application.toggleDevices ();
    }


    /**
     * Toggles the visibility of the inspector panel.
     */
    public void toggleInspector ()
    {
        this.application.toggleInspector ();
    }


    /**
     * Toggles the visibility of the mixer panel.
     */
    public void toggleMixer ()
    {
        this.application.toggleMixer ();
    }


    /**
     * Toggles between full screen and windowed user interface.
     */
    public void toggleFullScreen ()
    {
        this.application.toggleFullScreen ();
    }


    /**
     * Toggles the visibility of the browser panel.
     */
    public void toggleBrowserVisibility ()
    {
        this.application.toggleBrowserVisibility ();
    }


    /**
     * Duplicates the active selection in Bitwig Studio if applicable.
     */
    public void duplicate ()
    {
        this.application.duplicate ();
    }


    /**
     * Deletes the selected items in Bitwig Studio if applicable.
     */
    public void deleteSelection ()
    {
        this.application.remove ();
    }


    /**
     * Sends a redo command to Bitwig Studio.
     */
    public void redo ()
    {
        this.application.redo ();
    }


    /**
     * Sends an undo command to Bitwig Studio.
     */
    public void undo ()
    {
        this.application.undo ();
    }


    /**
     * Creates a new audio track.
     */
    public void addAudioTrack ()
    {
        this.application.createAudioTrack (-1);
    }


    /**
     * Creates a new effect track.
     */
    public void addEffectTrack ()
    {
        this.application.createEffectTrack (-1);
    }


    /**
     * Creates a new instrument track.
     */
    public void addInstrumentTrack ()
    {
        this.application.createInstrumentTrack (-1);
    }


    /**
     * Equivalent to an Arrow-Left key stroke on the computer keyboard. The concrete functionality
     * depends on the current keyboard focus in Bitwig Studio.
     */
    public void arrowKeyLeft ()
    {
        this.application.arrowKeyLeft ();
    }


    /**
     * Equivalent to an Arrow-Up key stroke on the computer keyboard. The concrete functionality
     * depends on the current keyboard focus in Bitwig Studio.
     */
    public void arrowKeyUp ()
    {
        this.application.arrowKeyUp ();
    }


    /**
     * Equivalent to an Arrow-Right key stroke on the computer keyboard. The concrete functionality
     * depends on the current keyboard focus in Bitwig Studio.
     */
    public void arrowKeyRight ()
    {
        this.application.arrowKeyRight ();
    }


    /**
     * Equivalent to an Arrow-Down key stroke on the computer keyboard. The concrete functionality
     * depends on the current keyboard focus in Bitwig Studio.
     */
    public void arrowKeyDown ()
    {
        this.application.arrowKeyDown ();
    }


    /**
     * Equivalent to an Enter key stroke on the computer keyboard. The concrete functionality
     * depends on the current keyboard focus in Bitwig Studio.
     */
    public void enter ()
    {
        this.application.enter ();
    }


    /**
     * Equivalent to an Escape key stroke on the computer keyboard. The concrete functionality
     * depends on the current keyboard focus in Bitwig Studio.
     */
    public void escape ()
    {
        this.application.escape ();
    }


    /**
     * Returns a list of action categories that is used by Bitwig Studio to group actions into
     * categories.
     *
     * @return All action categories
     */
    public ActionCategory [] getActionCategories ()
    {
        return this.application.getActionCategories ();
    }


    /**
     * Returns the action category associated with the given identifier.
     *
     * @param id the category identifier string, must not be `null`
     * @return The category
     */
    public ActionCategory getActionCategory (final String id)
    {
        return this.application.getActionCategory (id);
    }


    /**
     * Invokes the action for the given action identifier.
     *
     * @param id the action identifier string, must not be `null`
     */
    public void invokeAction (final String id)
    {
        this.getAction (id).invoke ();
    }


    /**
     * Returns the action for the given action identifier.
     *
     * @param id the action identifier string, must not be `null`
     * @return The action
     */
    private Action getAction (final String id)
    {
        return this.application.getAction (id);
    }
}