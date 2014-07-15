package com.kohls.shipment.api;



	import java.util.Properties;
	 
	import org.w3c.dom.Attr;
	import org.w3c.dom.Document;
	import org.w3c.dom.Element;
	import org.w3c.dom.NamedNodeMap;
	import org.w3c.dom.NodeList;
	 
	import com.yantra.yfc.dom.YFCDocument;
	import com.yantra.yfs.japi.YFSEnvironment;
	import com.yantra.yfs.japi.YFSException;
	 
	/**
	* A class that fakes a response returned from the GIV store lo! cation interface
	* 
	 * @author IBM SWG Professional Services Team
	*/
	public final class KohlsGIVLocationSimulator {
	    //
	    // api properties
	    private Properties apiProps = null;
	    	    
	   /**
	     * A method that receives the GIV input document and generates a simulated
	     * response document based on the percentages set for STOCK_ROOM, FLOOR, and
	     * MIXED_ORDER in the API parameters.
	     * 
	     * @param env
	     *            handle to the MCF environemnt
	     * @param inXml
	     *            the incoming GIV location input document
	     * @return the simulated GIV response
	     * @throws YFDException
	     *             if processing errors occur
	     */
	    public final Document getGIVLocations(final YFSEnvironment env,
	            final Document inXml) throws YFSException {
	        //
	        double stockRoom = 0.00;
	        double floor = 0.00;
	        double rand = 0.00;
	        Document out = null;
	        Element root = null;
	        NodeList supplies = null;
	        Element supply = null;
	        Element item = null;
	        Element suppliesOut = null;
	        Element supplyOut = null;
	        int totalQty = 0;
	        int qtyStockRoom = 0;
	        int qtyFloor = 0;
	        try {
	            stockRoom = new Double(this.apiProps.getProperty("STOCK_ROOM"));
	            floor = new Double(this.apiProps.getProperty("FLOOR")) + stockRoom;
	           //
	            out = YFCDocument.createDocument("InventorySupplies").getDocument();
	            root = out.getDocumentElement();
	            //
	            //supplies = inXml.getElementsByTagName("InventorySupply");
	            supplies = inXml.getElementsByTagName("inp:InventorySupply");
	            for (int i = 0; i < supplies.getLength(); i++) {
	                supply = (Element) supplies.item(i);
	                item = out.createElement("Item");
	                root.appendChild(item);
	                this.copySupplyToItem(supply, item);
	            //
	                suppliesOut = out.createElement("Supplies");
	                item.appendChild(suppliesOut);
	                //
	                rand = Math.random() * 100.00;
	                totalQty = new Integer(supply.getAttribute("ExtnTotalQuantity"));
	                // stock room only
	                if (rand <= stockRoom) {
	                    qtyStockRoom = (int) (totalQty + Math.random() * 10);
	                    qtyFloor = (int) (totalQty + Math.random() * 3);
	                }
	 
	                else if ((rand > stockRoom) && (rand <= floor)) {
	                    qtyStockRoom = 0;
	                    qtyFloor = (int) (totalQty + Math.random() * 10);
	                }
	 
	                else {
	                    qtyStockRoom = (int) (totalQty * Math.random());
	                    qtyFloor = (int) ((totalQty - qtyStockRoom) + Math.random() * 10);
	                } // end:if ((rand > stockRoom) && (rand <= floor))
	                 //
	                 // STOCKROOM supply
	                supplyOut = out.createElement("InventorySupply");
	                suppliesOut.appendChild(supplyOut);
	                supplyOut.setAttribute("SupplyType", "STOCKROOM");
	                supplyOut.setAttribute("Quantity", qtyStockRoom + "");
	                  //
	                // FLOOR supply
	                supplyOut = out.createElement("InventorySupply");
	                suppliesOut.appendChild(supplyOut);
	                supplyOut.setAttribute("SupplyType", "FLOOR" );
	                supplyOut.setAttribute("Quantity", qtyFloor + "");
	                //
	            }// end: for (int i = 0; i < supplies.getLength(); i++)
	             //
	            return out;
	        } catch (NumberFormatException ex) {
	            // TODO -- log error
	            final YFSException yfe = new YFSException();
	            yfe.setErrorCode("GIVSIM-050");
	            yfe.setErrorDescription("Invalid ExtnTotalQuantity on input.");
	            yfe.setStackTrace(ex.getStackTrace());
	            throw yfe;
	        }
	    }// getGIVLocations(YFSEnvironment, Document):Document
	 
	    /**
	     * A helper method that copies attributes from the incoming Supply element
	     * to the outgoing Item element
	     * 
	     * @param supply
	     *            the incoming supply element
	     * @param item
	     *            the outgoing item element
	     * @throws YFSException
	     *             if processing e! rrors occur
	     */
	    private final void copySupplyToItem(final Element supply, final Element item)
	            throws YFSException {
	        //
	        NamedNodeMap attrs = null;
	        Attr attr = null;
	         try {
	            attrs = supply.getAttributes();
	            for (int i = 0; i < attrs.getLength(); i++) {
	                //
	                attr = (Attr) attrs.item(i);
	                if (attr.getName().equals("ExtnTotalQuantity")) {
	                    continue;
	               }// end: if (attr.getName().equals("ExtnTotalQuantity"))
	                 //
	                if (attr.getName().equals("ShipNode")) {
	                    continue;
	                }// end: if (attr.getName().equals("ShipNode"))! 
	                 //
	                item.setAttribute(attr.getName(), attr.getValue());
	                  //
	            }// end: for (int i = 0; i < attrs.getLength(); i++)
	        } finally {
	      }
	    }// copySupplyToItem(Element, Element)
	 
	    /**
	     * A method that reads configured parameters that says the percentage! to of
	     * times to allocate inventory to the STOCK_ROOM, and the FLOOR. The
	     * remaining percentage is assumed to be MIXED_ORDER. They must add up to
	     * 100; otherwise an error is thrown
	     * 
	     * @return the properties
	     */
	    public final void setProperties(final Properties apiProps) throws Exception {
	        //
	        try {
	            //
	            this.validateProps(apiProps);
	            this.apiProps = apiProps;
	            //
	        } finally {
	        }
	    } // setProperties(Properties)
	 
	    /**
	     * A private helper method that validates he properties
	     * 
	     * @param the
	     *            properties to validate
	     * @throws YFSException
	     *             if the properites are invalid
	     */
	    private final void validateProps(final Properties props) {
	        //
	        String name = null;
	        String value = null;
	        double total = 0.0;
	        try {
	            // validate STOCK_ROOM parameter
	            name = "STOCK_ROOM";
	            value = props.getProperty(name);
	            if ((value == null) || (value.trim().length() == 0)) {
	                // TODO -- log error
	                final YFSException yfe = new YFSException(); 
	                yfe.setErrorCode("GIVSIM-010");
	                yfe.setErrorDescription("Parameter '" + name
	                        + "' not configured for service.");
	                throw yfe;
	            }// end: if ((value == null) || (value.trim().length() == 0))
	            total = total + new Double(value);
	            //
	            // validate FLOOR parameter
	            name = "FLOOR";
	          value = props.getProperty(name);
	            if ((value == null) || (value.trim().length() == 0)) {
	                // TODO -- log error
	                final YFSException yfe = new YFSException();
	               yfe.setErrorCode("GIVSIM-020");
	                yfe.setErrorDescription("Parameter '" + name
	                       + "' not configured for service.");
	                throw yfe;
	            }// end:! if ((value == null) || (value.trim().length() == 0))
	            total = total + new Double(value);
	            //
	             // check STOCK_ROOM + FLOOR is <= 100.00
	            if (total <= 100.00) {
	                return;
	            }// end: if (total <= 100.00)
	             //
	            final YFSException yfe = new YFSException();
	            yfe.setErrorCode("GIVSIM-030");
	             yfe.setErrorDescription("STOCK_ROOM + FLOOR > 100.00 [" + total
	                    + "].");
	            throw yfe;
	            //
	        } catch (NumberFormatException ex) {
	            // TODO -- log exception
	            final YFSException yfe = new YFSException();
	            yfe.setErrorCode("GIVSIM-040");
	            yfe.setErrorDescription("Parameter '" + name + "' is not numeric ["
	                    + value + "].");
	            yfe.setStackTrace(ex.getStackTrace());
	            throw yfe;
	        }
	    }// validateProps(Properties)
}
