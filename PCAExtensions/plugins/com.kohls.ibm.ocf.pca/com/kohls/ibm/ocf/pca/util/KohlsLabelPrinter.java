package com.kohls.ibm.ocf.pca.util;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.xml.bind.DatatypeConverter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSException;

/**
 * A class that prints the shipping label.
 * 
 * @author IBM SWG Professional Services Team
 */
public final class KohlsLabelPrinter {
    //
    /**
     * A method that prints out a label. It first decodes the label, then sends
     * it to the printer. The input XML has the following format:
     * <p/>
     * 
     * <code>
     * &lt;ShipmentCarrierServiceCode=&quot;BWTI_UPS.UPS.NDA&quot; SCAC=&quot;UPS&quot; ShipmentKey=&quot;2012130606151222383&quot; &gt;<br/>
     * &nbsp;&lt;Containers&gt;<br/>
     * &nbsp;&nbsp;&lt;ContainerBasicFreightCharge=&quot;108.08&quot; ContainerScm=&quot;&quot;ExtnB64Label=&quot;XlhBCl....&quot; <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ShipmentContainerKey=&quot;20130607124132898&quot; TrackingNo=&quot;1ZEA81020100000317&quot;&gt;<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Extn ExtnLblPrintFormat=&quot;ZPLII_203&quot; ExtnVoidLabelId=&quot;BUPS.1ZEA81020100000317&quot;/&gt;<br/>
     * &nbsp;&nbsp;&lt;/Container&gt;<br/>
     *  &nbsp;&lt;/Containers&gt;<br/>
     *   &lt;/Shipment&gt;<br/>
     * </code>
     * 
     * @param inXml
     *            the container document with label, returned from ProShip
     * @return the input document un-modified
     * @throws YFCException
     *             if processing errors occur
     */
    public final Document printLabel(final Document inXml) {
        //
        NodeList containers = null;
        Element container = null;
        String b64 = null;
        byte[] label = null;
        String printerName = null;
        PrintService[] services = null;
        PrintService lblService = null;
        DocPrintJob job = null;
        DocFlavor flavor = null;
        Doc printDoc = null;
        try {
            containers = inXml.getElementsByTagName("Container");
            if (containers.getLength() == 0) {
                // TODO -- log error
                final YFSException yfs = new YFSException(
                        "No containers found in input document.");
                throw yfs;
            }// end: if (containers.getLength() == 0)
             //
            container = (Element) containers.item(0);
            b64 = container.getAttribute("ExtnB64Label");
            if ((b64 == null) || (b64.length() == 0)) {
                // TODO -- log error
                final YFCException yfs = new YFCException(
                        "Empty label returned from ProShip.");
                throw yfs;
            }// if ((b64 == null) || (b64.length() == 0))
             //
            label = this.decode(b64);
            //
            services = PrintServiceLookup.lookupPrintServices(null, null);
            if (services.length == 0) {
                // TODO -- log error
                final YFCException yfs = new YFCException(
                        "No print services configured on machine.");
                throw yfs;
            }// end: if (services.length == 0)
             // insert code here to read the current lable printer from the
             // singleton
            printerName = inXml.getDocumentElement().getAttribute(KOHLSPCAConstants.SELECTED_LABEL_PRINTER);
            for (int i = 0; i < services.length; i++) {
                if (services[i].getName().equalsIgnoreCase(printerName)) {
                    lblService = services[i];
                    break;
                }// end: if
                 // (services[i].getName().equalsIgnoreCase(printerName))
                 // (services[i].getName().equalsIgnoreCase(printerName))
            }// end: for (int i = 0; i < services.length; i++)
             //
            if (lblService == null) {
                // TODO -- log error
                final YFCException yfs = new YFCException("Label printer '"
                        + printerName + "' not configured on machine.");
                throw yfs;
            }// end: if (lblService == null)
             //
            job = lblService.createPrintJob();
            flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            printDoc = new SimpleDoc(label, flavor, null);
            job.print(printDoc, null);
            //
            return inXml;
        } catch (Exception ex) {
            // TODO -- log error
            final YFCException yfs = new YFCException(ex);
            yfs.setStackTrace(ex.getStackTrace());
            throw yfs;
        }
    }// printLabel(Document):Document

    /**
     * A helper method that base64 decodes a string
     * 
     * @param text
     *            the input string to base-64 decode
     * @return the input string decoded into a byte array
     * @throws YFCException
     *             if decoding errors occur
     */
    private final byte[] decode(final String text) {
        //
        byte[] out = null;
        try {
            out = DatatypeConverter.parseBase64Binary(text);
            return out;
        } catch (Exception ex) {
            // TODO -- log exception
            final YFCException yfc = new YFCException(ex);
            yfc.setStackTrace(ex.getStackTrace());
            throw yfc;
        }
    }// decode(String):byte[]
}// class:KohlsLabelPrinter