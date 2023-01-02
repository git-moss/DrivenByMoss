// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.graphics.IBitmap;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;


/**
 * Interface for a factory to create hardware elements proxies of a hardware controller device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IHwSurfaceFactory
{
    /**
     * Create a proxy to a hardware button.
     *
     * @param surfaceID The ID of the surface
     * @param buttonID The button ID to use
     * @param label The label of the button
     * @return The created button
     */
    IHwButton createButton (int surfaceID, ButtonID buttonID, String label);


    /**
     * Create a proxy to a hardware light.
     *
     * @param surfaceID The ID of the surface
     * @param outputID The ID of the light, may be null
     * @param supplier Callback for getting the color of the light
     * @param sendValueConsumer Callback for sending the state to the controller device
     * @return The created light
     */
    IHwLight createLight (int surfaceID, OutputID outputID, Supplier<ColorEx> supplier, Consumer<ColorEx> sendValueConsumer);


    /**
     * Create a proxy to a hardware light.
     *
     * @param surfaceID The ID of the surface
     * @param outputID The ID of the light, may be null
     * @param supplier Callback for getting the state of the light
     * @param sendValueConsumer Callback for sending the state to the controller device
     * @param stateToColorFunction Convert the state of the light to a color, which can be displayed
     *            in the simulated GUI
     * @param button Binds the light to this button, can be null
     * @return The created light
     */
    IHwLight createLight (int surfaceID, OutputID outputID, IntSupplier supplier, IntConsumer sendValueConsumer, IntFunction<ColorEx> stateToColorFunction, IHwButton button);


    /**
     * Create a proxy to a hardware fader.
     *
     * @param surfaceID The ID of the surface
     * @param faderID The fader ID to use
     * @param label The label of the button
     * @param isVertical True if the fader is vertical, otherwise horizontal
     * @return The created fader
     */
    IHwFader createFader (int surfaceID, ContinuousID faderID, String label, boolean isVertical);


    /**
     * Create a proxy to a hardware absolute knob.
     *
     * @param surfaceID The ID of the surface
     * @param knobID The knob ID to use
     * @param label The label of the knob
     * @return The created knob
     */
    IHwAbsoluteKnob createAbsoluteKnob (int surfaceID, ContinuousID knobID, String label);


    /**
     * Create a proxy to a hardware relative knob.
     *
     * @param surfaceID The ID of the surface
     * @param knobID The knob ID to use
     * @param label The label of the knob
     * @return The created knob
     */
    IHwRelativeKnob createRelativeKnob (int surfaceID, ContinuousID knobID, String label);


    /**
     * Create a proxy to a hardware relative knob.
     *
     * @param surfaceID The ID of the surface
     * @param knobID The knob ID to use
     * @param label The label of the knob
     * @param encoding The encoding of the relative value
     * @return The created knob
     */
    IHwRelativeKnob createRelativeKnob (int surfaceID, ContinuousID knobID, String label, RelativeEncoding encoding);


    /**
     * Create a proxy to a hardware text display.
     *
     * @param surfaceID The ID of the surface
     * @param outputID The ID of the display
     * @param numLines The number of lines of the display
     * @return The created display
     */
    IHwTextDisplay createTextDisplay (int surfaceID, OutputID outputID, int numLines);


    /**
     * Create a proxy to a hardware graphics display.
     *
     * @param surfaceID The ID of the surface
     * @param outputID The ID of the display
     * @param bitmap The bitmap
     * @return The created display
     */
    IHwGraphicsDisplay createGraphicsDisplay (int surfaceID, OutputID outputID, IBitmap bitmap);


    /**
     * Create a proxy to a piano keyboard.
     *
     * @param surfaceID The ID of the surface
     * @param numKeys The number of keys, e.g. 25 or 88
     * @return The created keyboard
     */
    IHwPianoKeyboard createPianoKeyboard (int surfaceID, int numKeys);


    /**
     * Flush the state to the hardware device.
     */
    void flush ();


    /**
     * Clear all hardware caches.
     */
    void clearCache ();
}
