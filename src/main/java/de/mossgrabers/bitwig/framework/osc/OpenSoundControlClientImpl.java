// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.osc;

import de.mossgrabers.framework.osc.IOpenSoundControlClient;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.bitwig.framework.daw.ModelImpl;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
// import java.io.File;
// import java.io.FileWriter;

/**
 * Implementation of an OSC server connection (the client).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OpenSoundControlClientImpl implements IOpenSoundControlClient
{
    private final OscConnection connection;


    /**
     * Constructor.
     *
     * @param connection The OSC connection
     */
    public OpenSoundControlClientImpl (final OscConnection connection)
    {
        this.connection = connection;
    }


    /** {@inheritDoc} */
    @Override
    public void sendMessage (final IOpenSoundControlMessage message) throws IOException
    {
        final String address = message.getAddress ();
        final Object [] values = message.getValues ();

        if (address.startsWith("/device/param/") && address.endsWith("/value"))
        {
          final String sParamNumber = address.replace("/device/param/", "").replace("/value", "");
          Integer paramNumber = Integer.parseInt(sParamNumber);

          //Get a reference to the model
          ModelImpl model = ModelImpl.sharedModel;

          final ITrack cursorTrack = model.getCursorTrack ();
          final Integer trackNumber = cursorTrack.getPosition() + 1;

          final ICursorDevice cursorDevice = model.getCursorDevice();
          final IParameterPageBank pageBank = cursorDevice.getParameterPageBank ();
          final Integer pageIndex = pageBank.getSelectedItemIndex () + 1;

          final String newAddress = "/track/" + trackNumber.toString() + "/params/" + pageIndex.toString() + "/" + paramNumber.toString() + "/value";

          this.connection.sendMessage (newAddress, values);

          // FileWriter myWriter = new FileWriter("/Users/bjorn/Desktop/filename.txt", true);
          // myWriter.write("Got device param update for parameter # ");
          // myWriter.write(paramNumber.toString());
          // myWriter.write("\n");
          //
          // myWriter.write("New address: ");
          // myWriter.write(newAddress);
          //
          // myWriter.write("\n\n");
          //
          // myWriter.close();
        }

        this.connection.sendMessage (address, values);
    }


    /** {@inheritDoc} */
    @Override
    public void sendBundle (final List<IOpenSoundControlMessage> messages) throws IOException
    {
        int pos = 0;
        this.connection.startBundle ();
        for (final IOpenSoundControlMessage message: messages)
        {
            this.sendMessage (message);
            pos++;
            // We cannot get the exact size of the message due to the API, so let's try to stay
            // below 64K, which is the maximum of an UDP message
            if (pos > 100)
            {
                pos = 0;
                this.connection.endBundle ();

                // Ugly hack, but we need to slow down a bit otherwise clients like Open Stage
                // Control cannot keep up...
                try
                {
                    Thread.sleep (10);
                }
                catch (final InterruptedException ex)
                {
                    Thread.currentThread ().interrupt ();
                }

                this.connection.startBundle ();
            }
        }
        this.connection.endBundle ();
    }
}
