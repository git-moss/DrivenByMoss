// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.graphics.BitmapImpl;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwGraphicsDisplay;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.controller.hardware.IHwPianoKeyboard;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.hardware.IHwSurfaceFactory;
import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.utils.OperatingSystem;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareLightVisualState;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.InternalHardwareLightState;
import com.bitwig.extension.controller.api.MultiStateHardwareLight;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;


/**
 * Factory for creating hardware elements proxies of a hardware controller device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwSurfaceFactoryImpl implements IHwSurfaceFactory
{
    private final HostImpl        host;
    private final HardwareSurface hardwareSurface;

    private int                   lightCounter = 0;
    private final long            startup      = System.currentTimeMillis ();
    private boolean               startupDone  = false;


    /**
     * Constructor.
     *
     * @param host The host
     * @param width The width of the controller device
     * @param height The height of the controller device
     */
    public HwSurfaceFactoryImpl (final HostImpl host, final double width, final double height)
    {
        this.host = host;
        this.hardwareSurface = host.getControllerHost ().createHardwareSurface ();
        this.hardwareSurface.setPhysicalSize (width, height);
    }


    /** {@inheritDoc} */
    @Override
    public IHwButton createButton (final int surfaceID, final ButtonID buttonID, final String label)
    {
        final String id = createID (surfaceID, buttonID.name ());
        final HardwareButton hwButton = this.hardwareSurface.createHardwareButton (id);
        return new HwButtonImpl (this.host, hwButton, label);
    }


    /** {@inheritDoc} */
    @Override
    public IHwLight createLight (final int surfaceID, final OutputID outputID, final Supplier<ColorEx> supplier, final Consumer<ColorEx> sendValueConsumer)
    {
        this.lightCounter++;
        final String id = createID (surfaceID, outputID == null ? "LIGHT" + this.lightCounter : outputID.name ());

        final MultiStateHardwareLight hardwareLight = this.hardwareSurface.createMultiStateHardwareLight (id);
        final Supplier<InternalHardwareLightState> valueSupplier = () -> new RawColorLightState (supplier.get ());
        final Consumer<InternalHardwareLightState> hardwareUpdater = state -> {
            final HardwareLightVisualState visualState = state == null ? null : state.getVisualState ();
            final Color c = visualState == null ? Color.blackColor () : visualState.getColor ();
            sendValueConsumer.accept (new ColorEx (c.getRed (), c.getGreen (), c.getBlue ()));
        };
        return new HwLightImpl (this.host, hardwareLight, valueSupplier, hardwareUpdater);
    }


    /** {@inheritDoc} */
    @Override
    public IHwLight createLight (final int surfaceID, final OutputID outputID, final IntSupplier supplier, final IntConsumer sendValueConsumer, final IntFunction<ColorEx> stateToColorFunction, final IHwButton button)
    {
        this.lightCounter++;
        final String id = createID (surfaceID, outputID == null ? "LIGHT" + this.lightCounter : outputID.name ());

        final MultiStateHardwareLight hardwareLight = this.hardwareSurface.createMultiStateHardwareLight (id);

        final Supplier<InternalHardwareLightState> valueSupplier = () -> new EncodedColorLightState (supplier.getAsInt (), stateToColorFunction);
        final Consumer<InternalHardwareLightState> hardwareUpdater = state -> {
            final HardwareLightVisualState visualState = state == null ? null : state.getVisualState ();
            final int encodedColorState = visualState == null ? 0 : supplier.getAsInt ();
            sendValueConsumer.accept (encodedColorState);
        };

        final HwLightImpl lightImpl = new HwLightImpl (this.host, hardwareLight, valueSupplier, hardwareUpdater);
        if (button != null)
            button.addLight (lightImpl);
        return lightImpl;
    }


    /** {@inheritDoc} */
    @Override
    public IHwFader createFader (final int surfaceID, final ContinuousID faderID, final String label, final boolean isVertical)
    {
        final String id = createID (surfaceID, faderID.name ());
        return new HwFaderImpl (this.host, this.hardwareSurface.createHardwareSlider (id), label, isVertical);
    }


    /** {@inheritDoc} */
    @Override
    public IHwAbsoluteKnob createAbsoluteKnob (final int surfaceID, final ContinuousID knobID, final String label)
    {
        final String id = createID (surfaceID, knobID.name ());
        return new HwAbsoluteKnobImpl (this.host, this.hardwareSurface.createAbsoluteHardwareKnob (id), label);
    }


    /** {@inheritDoc} */
    @Override
    public IHwRelativeKnob createRelativeKnob (final int surfaceID, final ContinuousID knobID, final String label)
    {
        final String id = createID (surfaceID, knobID.name ());
        return new HwRelativeKnobImpl (this.host, this.hardwareSurface.createRelativeHardwareKnob (id), label);
    }


    /** {@inheritDoc} */
    @Override
    public IHwRelativeKnob createRelativeKnob (final int surfaceID, final ContinuousID knobID, final String label, final RelativeEncoding encoding)
    {
        final String id = createID (surfaceID, knobID.name ());
        return new HwRelativeKnobImpl (this.host, this.hardwareSurface.createRelativeHardwareKnob (id), label, encoding);
    }


    /** {@inheritDoc} */
    @Override
    public IHwTextDisplay createTextDisplay (final int surfaceID, final OutputID outputID, final int numLines)
    {
        final String id = createID (surfaceID, outputID.name ());
        return new HwTextDisplayImpl (this.hardwareSurface.createHardwareTextDisplay (id, numLines));
    }


    /** {@inheritDoc} */
    @Override
    public IHwGraphicsDisplay createGraphicsDisplay (final int surfaceID, final OutputID outputID, final IBitmap bitmap)
    {
        final String id = createID (surfaceID, outputID.name ());
        return new HwGraphicsDisplayImpl (this.hardwareSurface.createHardwarePixelDisplay (id, ((BitmapImpl) bitmap).bitmap ()));
    }


    /** {@inheritDoc} */
    @Override
    public IHwPianoKeyboard createPianoKeyboard (final int surfaceID, final int numKeys)
    {
        final int octave = 0;
        final int startKeyInOctave = 0;

        final String id = createID (surfaceID, "KEYBOARD");
        return new HwPianoKeyboardImpl (this.hardwareSurface.createPianoKeyboard (id, numKeys, octave, startKeyInOctave));
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Workaround for state not updated on first startup on MacOs 11
        if (OperatingSystem.isMacOS () && !this.startupDone && System.currentTimeMillis () - this.startup > 10000)
        {
            this.hardwareSurface.invalidateHardwareOutputState ();
            this.startupDone = true;
        }

        this.hardwareSurface.updateHardware ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearCache ()
    {
        this.hardwareSurface.invalidateHardwareOutputState ();
    }


    private static String createID (final int surfaceID, final String name)
    {
        return surfaceID + 1 + "_" + name;
    }
}
