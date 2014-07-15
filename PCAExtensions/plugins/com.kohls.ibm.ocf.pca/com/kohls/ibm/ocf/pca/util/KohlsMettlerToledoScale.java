package com.kohls.ibm.ocf.pca.util;

import com.yantra.ycp.ui.io.YCPWeighingScaleConnector;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.ui.io.YFCPortConfig;
import com.yantra.yfc.util.YFCException;

/**
 * An implementation of the Mettler Toledo scale for Kohls.
 * 
 * @author IBM SWG Professional Services Team
 */
public class KohlsMettlerToledoScale implements YCPWeighingScaleConnector {

    KohlsMettlerToledoScale() {
        //
        highRes = false;
        //
    }// constructor

    /**
     * Implementation of the <code>init()</code> interface method. It
     * initializes the class with its parameters.
     * 
     * @throws YFCException
     *             if initialization parameters are bad
     */
    public void init(YFCElement configElement) {
        //
        String val = null;
        try {
            sio = new KohlsSerialIO();
            //
            // process scale port
            portId = configElement.getAttribute("PortId");
            if (YFCObject.isVoid(portId)) {
                // TODO -- log exception
                final YFCException yfce = new YFCException(
                        "PortId attribute for weighing scale config cannot be null");
                throw yfce;
            }// end: if (YFCObject.isVoid(portId))
             //
            config = new YFCPortConfig(portId);
            if (!YFCObject.isVoid(configElement.getAttribute("BaudRate")))
                config.setBaudRate(configElement.getIntAttribute("BaudRate"));
            if (!YFCObject.isVoid(configElement.getAttribute("DataBits")))
                config.setDatabits(configElement.getIntAttribute("DataBits"));
            if (!YFCObject.isVoid(configElement.getAttribute("StopBits")))
                config.setStopbits(configElement.getIntAttribute("StopBits"));
            //
            // set scale parity
            val = configElement.getAttribute("Parity");
            if (!YFCObject.isVoid(val)) {
                if ("NONE".equalsIgnoreCase(val))
                    config.setParity(0);
                else if ("ODD".equalsIgnoreCase(val))
                    config.setParity(1);
                if ("EVEN".equalsIgnoreCase(val))
                    config.setParity(2);
            }// end: if (!YFCObject.isVoid(val))
             //
             // set the FlowIn parameter
            val = configElement.getAttribute("FlowIn");
            if (!YFCObject.isVoid(val)) {
                if ("NONE".equalsIgnoreCase(val))
                    config.setFlowIn(0);
                if ("XONXOFF".equalsIgnoreCase(val))
                    config.setFlowIn(4);
            }// end: if (!YFCObject.isVoid(val))
             //
             // set the FlowOut parameter
            val = configElement.getAttribute("FlowOut");
            if (!YFCObject.isVoid(val)) {
                if ("NONE".equalsIgnoreCase(val))
                    config.setFlowOut(0);
                if ("XONXOFF".equalsIgnoreCase(val))
                    config.setFlowOut(8);
            }// end: if (!YFCObject.isVoid(val))
             //
             // set HighResolution
            if (!YFCObject.isVoid(configElement
                    .getAttribute("HighResolutionData"))) {
                highRes = configElement
                        .getBooleanAttribute("HighResolutionData");
            }// end:if
             // (!YFCObject.isVoid(configElement.getAttribute("HighResolutionData")))
             //
        } finally {
        }
    }// init

    /**
     * Implementation of the <code>getWeight()</code> interface
     * 
     * @return the weight read from the scale
     * @throws YFCException
     *             if processing errors occur
     */
    public double getWeight() {
        //
        String response = null;
        try {
            sio.openConnection(config);
            response = sendCommand();
            return processResponse(response);
        } finally {
            sio.closeConnection();
        }
    }// getWeight:double

    /**
     * A helper method that send the command to the scale to read.
     * 
     * 
     * @return string read from the scale
     * 
     */
    private String sendCommand() {
        //
        String cmd = highRes ? "H" : "W";
        sio.write(cmd);
        // we can put the 20L if weight not reading properly
        //
        sio.waitForResponse(20L, 1000L);
        String resp = sio.read();
        byte respBytes[] = resp.getBytes();
        this.validateResponse(respBytes);
        for (int i = 0; i < respBytes.length; i++) {
            if (respBytes[i] < 0) {
                respBytes[i] = (byte) (respBytes[i] + 128);
            }
        }// end: for (int i = 0; i < respBytes.length; i++)
        this.validateReading(respBytes);

        return new String(respBytes);
        //
    }// sendCommand:String

    /**
     * A helper method that processes the response coming from the scale
     * 
     * @param response
     *            the response read from the scale
     * @return the processed response from the scale double
     * 
     */
    private double processResponse(String response) {
        //
        String strWeight = null;
        double weight = -1D;
        try {
            strWeight = response.substring(1, response.length() - 1);
            weight = Double.parseDouble(strWeight);
            return this.applyRetailRounding(weight);
        } catch (NumberFormatException ex) {
            // TODO -- log error
            final YFCException yfce = new YFCException(ex.getMessage());
            yfce.setStackTrace(ex.getStackTrace());
            throw yfce;
        }
        //
    }// processResponse(String):double

    /**
     * Apply retail rounding. Retail rounding rounds the scale display to the
     * closest 0.05 lb whereas reading the scale through code does not apply
     * retail rounding. Without applying this rounding to the result, the scale
     * display would not match the weight returned to SIM in many, many
     * instances.
     * 
     * @param weight
     *            the weigh to apply retail rounding to
     * @return the weight rounded to to 0.025.
     */
    private final double applyRetailRounding(double weight) {
        //
        int whole = -1;
        double frac = 0.0;
        try {
            // find the decimal portion
            whole = (int) (weight * 10);
            frac = weight * 10 - whole;
            //
            if (frac >= 0 && frac <= 0.25) {
                frac = 0.0;
            } else if (frac > 0.25 && frac <= 0.5) {
                frac = 0.5;
            } else if (frac > 0.5 && frac <= 0.75) {
                frac = 0.5;
            } else {
                frac = 1.0;
            }
            weight = (whole + frac) / 10;
            return weight;
        } finally {
        }
    }// applyRetailRounding(double):double

    /**
     * Implememntation of the <code>resetScale()</code> interface method
     * 
     */
    public void resetScale() {
        //
        sio.write("Z");
        sio.waitForResponse(20L, 1000L);
        String resp = sio.read();
        byte respBytes[] = resp.getBytes();
        validateReading(respBytes);
        //
    }// resetScale

    /**
     * Validates the response from the scale for correctness
     * 
     * @param respBytes
     *            the bytes read from the scale
     * @throws YFCException
     *             if validation errors occur
     */
    private final void validateReading(final byte[] respBytes) {
        //
        try {
            // null read from scale
            if (respBytes == null || respBytes.length == 0) {
                throw new YFCException("Null response from Weighing Scale.");
            }// end: if (respBytes == null || respBytes.length == 0)
             //
             // length: format is ?999.000?
            if (respBytes[respBytes.length - 1] != 63) {
                throw new YFCException("Partial response from Weighing Scale.");
            }// end: if (respBytes[respBytes.length - 1] != 9)
             //
             // if (
        } finally {
        }
    }// validateReading(byte[])

    /**
     * A method that validates scale errors
     * 
     * @param bytes
     *            the bytes read from the scale
     * @throws YFCException
     *             if there were errors reading the scale.
     */
    private void validateResponse(final byte[] bytes) {
        //
        StringBuffer text = null;
        try {
            if ((bytes == null) || (bytes.length == 0)) {
                //
                // no response from scale
                final YFCException yfce = new YFCException(
                        "Null response from weighing scale.");
                throw yfce;
            }// end: if ((bytes == null) || (bytes.length == 0))
             //
             // check that final character is ?
            if (bytes[bytes.length - 1] != 63) {
                // TODO -- log error
                final YFCException yfce = new YFCException(
                        "Partial response returned from scale.");
                throw yfce;
            }// end: if (bytes[bytes.length - 1] != 63)
             //
             // ??......? is an error in reading
             // check for there being no error in the reading
            if (bytes[1] != 63) {
                return;
            }// end: if (bytes[1] != 63)
             //
             // if the 3rd byte is 105, then the scale is in motion
            if (bytes[2] == 105) {
                //
                // TODO -- log error
                final YFCException yfce = new YFCException(
                        "Scale is moving.  Wait for it to settle.");
                throw yfce;
            }// end: if (bytes[2] == 105)
             //
             // if the weight is less thatn zero, throw error
            if (bytes[2] == -28) {
                // TODO -- log error
                final YFCException yfce = new YFCException(
                        "The weight is under zero.  Please re-zero scale.");
                throw yfce;
            }// end:if ( bytes[2] == -28)
             //
             // over the capacity of the scale
            if (bytes[2] == -21) {
                // TODO -- log error
                final YFCException yfce = new YFCException(
                        "Weight on scale is over its capacity to measure.");
                throw yfce;
            }// end: if (bytes[2] == -21)
             //
             // unknown error returned from scale
            text = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                text.append("[" + bytes[i] + "]");
            }// end: for (int i = 0; i < bytes.length; i++)
             //
            final YFCException yfce = new YFCException(
                    "Unknown error returned by scale: " + text.toString());
            throw yfce;
        } finally {
        }
    }// validateResponse(byte[])

    private KohlsSerialIO sio;

    private String portId;

    private boolean highRes;

    private YFCPortConfig config;
    //
}// class:KohlsKettlerToledoScale
