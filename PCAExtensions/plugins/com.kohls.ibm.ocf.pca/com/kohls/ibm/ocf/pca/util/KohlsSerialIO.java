package com.kohls.ibm.ocf.pca.util;

import gnu.io.CommDriver;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import com.yantra.yfc.ui.io.YFCPortConfig;
import com.yantra.yfc.ui.io.YFCSerialPortDataListener;
import com.yantra.yfc.util.YFCException;

/**
 * A package that implements a serial IO port for the Kohl's scale.
 * 
 * @author IMB SWG Professional Services Team
 */
public class KohlsSerialIO implements SerialPortEventListener {
    //
    /**
     * Default no-arguments constructor
     */
    public KohlsSerialIO() {
        //
        dataReady = false;
        crToNewLine = false;
        listener = null;
        open = false;
        //
    }// constructor

    /**
     * Implementation of the <code>addSerialPortDataListener()</code> interface
     * 
     * @param listener
     *            the listener to set
     * @param replaceListener
     *            true to replace the listener; false otherwise
     * @throws YFCException
     *             if processing errors occur
     * 
     */
    public void addSerialPortDataListener(YFCSerialPortDataListener listener,
            boolean replaceListener) {
        //
        try {
            if (this.listener == null || replaceListener) {
                this.listener = listener;
                return;
            }// end: if (this.listener == null || replaceListener)
             //
             // TODO -- log exception
            final YFCException yfce = new YFCException(
                    "Listener already set.  Call removeListener or use replace option.");
            throw yfce;
        } finally {
        }

    }// addSerialPortDataListener(YFCSerialPortDataListener, boolean)

    /**
     * Implementation of the <code>removeSerialPortDataListener()</code>
     * interface method
     * 
     */
    public void removeSerialPortDataListener() {
        //
        listener = null;
        //
    }// removeSerialPortDataListener

    /**
     * Implementation of the <code>openConnection()</code> interface method
     * Description of openConnection
     * 
     * @param config
     *            void
     * 
     */
    public void openConnection(YFCPortConfig config) {
        //
        if (open)
            closeConnection();
        try {
            portId = CommPortIdentifier.getPortIdentifier(config.getPortName());
        } catch (NoSuchPortException e) {
            throw new YFCException(e);
        }
        try {
            port = (SerialPort) portId.open("KohlsSerialIO", 3000);
        } catch (PortInUseException e) {
            throw new YFCException(e);
        }
        try {
            port.setSerialPortParams(config.getBaudRate(),
                    config.getDatabits(), config.getStopbits(),
                    config.getParity());
        } catch (UnsupportedCommOperationException e) {
            port.close();
            throw new YFCException(e);
        }
        try {
            port.setFlowControlMode(config.getFlowIn() | config.getFlowOut());
        } catch (UnsupportedCommOperationException e) {
            port.close();
            throw new YFCException(e);
        }
        try {
            outStream = port.getOutputStream();
            inStream = port.getInputStream();
        } catch (IOException e) {
            port.close();
            throw new YFCException(e);
        }
        try {
            port.addEventListener(this);
        } catch (TooManyListenersException e) {
            port.close();
            throw new YFCException(e);
        }
        port.notifyOnDataAvailable(true);
        port.notifyOnBreakInterrupt(false);
        try {
            port.enableReceiveTimeout(100);
        } catch (UnsupportedCommOperationException e) {
            throw new YFCException(e);
        }
        open = true;
    }// openConnection

    /**
     * Implementation of the <code>closeConnection()</> interface method
     * 
     */
    public void closeConnection() {
        if (!open)
            return;
        if (port != null) {
            try {
                outStream.close();
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            port.close();
        }
        open = false;
    }// closeConnection

    /**
     * Implementation of the <code>serialEvent()</code> interface method.
     * 
     */
    public void serialEvent(SerialPortEvent e) {
        switch (e.getEventType()) {
            case 1: // '\001'
                dataReady = true;
                if (listener != null)
                    pushData();
                break;
        }
    }// serialEvent

    private void pushData() {
        String resp = read();
        listener.acceptData(resp);
    }

    public void write(String str) {
        if (!open)
            throw new YFCException(
                    "Port is not open. Call openConnection first");
        try {
            for (int i = 0; i < str.length(); i++)
                outStream.write(str.charAt(i));

        } catch (IOException e) {
            closeConnection();
            throw new YFCException(e);
        }
    }

    public String read() {
        StringBuffer buf;
        int newData;
        if (!open)
            throw new YFCException(
                    "Port is not open. Call openConnection first");
        buf = new StringBuffer();
        newData = 0;
        if (!dataReady) {
            dataReady = false;
            return buf.toString();
        }

        try {
            while ((newData = inStream.read()) != -1) {
                if ('\r' == (char) newData && crToNewLine)
                    buf.append('\n');
                else
                    buf.append((char) newData);
            }
        } catch (IOException ex) {
            dataReady = false;
            closeConnection();
            throw new YFCException(ex);
        }

        dataReady = false;
        return buf.toString();
    }

    public void convertCRToNewLine(boolean convert) {
        crToNewLine = convert;
    }

    public void waitForResponse(long sleepInterval, long timeout) {
        try {
            for (int numSleeps = 0; !dataReady && (long) numSleeps < timeout; numSleeps = (int) ((long) numSleeps + sleepInterval)) {
                Thread.currentThread();
                Thread.sleep(sleepInterval);
            }

        } catch (Exception ex) {
            throw new YFCException(ex);
        }
    }

    /**
     * Initializes the driver
     * 
     */
    public static void initializeWin32Driver() {
        // String driverClassName = "com.ibm.comm.IBMCommDriver";
        // TODO -- MAKE THIS A PROPERTY READ FROM customer_overrides.properties
        //
        String driverClassName = "gnu.io.RXTXCommDriver";
        CommDriver driver = null;
        try {
            // System.setSecurityManager(new YFCSecurityManager());
            driver = (CommDriver) Class.forName(driverClassName).newInstance();
            driver.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (UnsatisfiedLinkError e) {
            System.err.println((new StringBuilder()).append("KohlsSerialIO: ")
                    .append(e.getMessage()).toString());
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    private OutputStream outStream;

    private InputStream inStream;

    private CommPortIdentifier portId;

    private SerialPort port;

    private boolean dataReady;

    private boolean open;

    private boolean crToNewLine;

    private YFCSerialPortDataListener listener;

}// class:KohlsSerialIO

