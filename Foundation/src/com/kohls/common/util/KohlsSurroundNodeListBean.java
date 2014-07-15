package com.kohls.common.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSException;

/**
 * A helper bean that caches information passed in the
 * <code>GetSurroundingNodeLilst</code> input XML
 * 
 * @author IBM SWG Professional Services Team
 */
public final class KohlsSurroundNodeListBean {
    //
    // properties
    private Integer distanceToConsider = null;

    private String fulfillType = null;

    private String nodeType = null;

    private String orgCode = null;

    private String state = null;

    private String zipCode = null;

    private String country = null;

    private Double latitude = null;

    private Double longitude = null;

    /**
     * Private constructor
     */
    private KohlsSurroundNodeListBean() {
        //
    }// constructor

    /**
     * Setter for the distance to consider
     * 
     * @param distanceToConsider
     *            the distance to consider
     */
    public final void setDistanceToConsider(final Integer distanceToConsider) {
        //
        this.distanceToConsider = distanceToConsider;
        //
    }// setDistanceToConsider(Integer)

    /**
     * Getter for the distance to consider
     * 
     * @return the distance to consider
     */
    public final Integer getDistanceToConsider() {
        //
        return this.distanceToConsider;
        //
    }// getDistanceToConsider:Integer

    /**
     * Setter for the fulfillment type
     * 
     * @param fulfillType
     *            the fulfillment type
     */
    public final void setFulfillType(final String fulfillType) {
        //
        this.fulfillType = fulfillType;
        //
    }// setFulfillType(String)

    /**
     * Getter for the fulfillment type
     * 
     * @return the fulfillmenet type
     */
    public final String getFulfillType() {
        //
        return this.fulfillType;
        //
    }// getFulfillType:String

    /**
     * Setter for the node type
     * 
     * @param nodeType
     *            the node type
     */
    public final void setNodeType(final String nodeType) {
        //
        this.nodeType = nodeType;
        //
    }// setNodeType(String)

    /**
     * Getter for the node type
     * 
     * @return returns the node type
     */
    public final String getNodeType() {
        //
        return this.nodeType;
        //
    }// getNodeType:String

    /**
     * Setter for the organization code
     * 
     * @param orgCode
     *            the organization code
     */
    public final void setOrgCode(final String orgCode) {
        //
        this.orgCode = orgCode;
        //
    }// setOrgCode(String)

    /**
     * Getter for the org code
     * 
     * @return the organization code
     */
    public final String getOrgCode() {
        //
        return this.orgCode;
        //
    }// getOrgCode:String

    /**
     * Setter for the state code
     * 
     * @param state
     *            the state code
     */
    public final void setState(final String state) {
        //
        this.state = state;
        //
    }// setState(String)

    /**
     * Getter for the state code
     * 
     * @return the state code
     */
    public final String getState() {
        //
        return this.state;
        //
    }// getState:String

    /**
     * Setter for the zip code
     * 
     * @param zipCode
     *            the zip code
     */
    public final void setZipCode(final String zipCode) {
        //
        this.zipCode = zipCode;
        //
    }// setZipCode(String)

    /**
     * Getter for the zip code
     * 
     * @return the zip code
     */
    public final String getZipCode() {
        //
        return this.zipCode;
        //
    }// getZipCode:String

    /**
     * Setter for the country code
     * 
     * @param country
     *            the country code
     */
    public final void setCountry(final String country) {
        //
        this.country = country;
        //
    }// setCountry(String)

    /**
     * Getter for the country
     * 
     * @return the country code
     */
    public final String getCountry() {
        //
        return this.country;
        //
    }// getCountry:String

    /**
     * Setter for the latitude
     * 
     * @param latitude
     */
    public final void setLatitude(final Double latitude) {
        //
        this.latitude = latitude;
        //
    }// setLatitude(Double)
     //

    /**
     * getter for the latitude
     * 
     * @return the latitude
     */
    public final Double getLatitude() {
        //
        return this.latitude;
        //
    }// getLatitude:Double

    /**
     * Setter for the longitude
     * 
     * @param longitude
     *            the longitude
     */
    public final void setLongitude(final Double longitude) {
        //
        this.longitude = longitude;
        //
    }// setLongitude(Double)

    /**
     * Getter for the longitude
     * 
     * @return the longitude
     */
    public final Double getLongitude() {
        //
        return this.longitude;
        //
    }// getLongitude:Double

    /**
     * Gets the latitude in radians
     * 
     * @return the latitude in radians
     */
    public final Double getLatRadians() {
        //
        return this.latitude * Math.PI / 180.0;
        //
    }// getLatRadians:Double

    /**
     * A helper method that returns the longitude in radians
     * 
     * @return the longitude in radians
     */
    public final Double getLongRadians() {
        //
        return this.longitude * Math.PI / 180.0;
        //
    }// getLongRadians:Double

    /**
     * Unmarshals a XML representation into an object representation.
     * 
     * @param node
     *            the node to unmarshal
     * @return object representation of the XML
     * @throws YFCException
     *             if the process fails
     * 
     */
    public final static KohlsSurroundNodeListBean unmarshal(final Node node) {
        //
        Element element = null;
        KohlsSurroundNodeListBean bean = null;
        String attrName = null;
        String value = null;
        NodeList addresses = null;
        Element address = null;
        try {
            if (node.getNodeType() == Node.DOCUMENT_NODE) {
                element = ((Document) node).getDocumentElement();
            } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                element = (Element) node;
            } else {
                // TODO -- log exception
                final YFSException yfe = new YFSException();
                yfe.setErrorCode("KSN-010");
                yfe.setErrorDescription("Expecting Document or Element, but got: "
                        + node.getClass().getName());
                throw yfe;
            }// end: if (node.getNodeType() == Node.DOCUMENT_NODE)

            if (!element.getTagName().equals("GetSurroundingNodeList")) {
                // TODO -- log error
                final YFSException yfe = new YFSException();
                yfe.setErrorCode("KSN-020");
                yfe.setErrorDescription("Document not of type GetSurroundingNodeList.  It is "
                        + element.getTagName());
                throw yfe;
            }// end: if (!element.getTagName().equals("GetSurroundingNodeList"))
             //
             // begin un-marshaling the object
            bean = new KohlsSurroundNodeListBean();
            //
            // GetSurroundingNodeList/@DistanceToConsider
            value = element.getAttribute("DistanceToConsider");
            if ((value == null) || (value.trim().length() == 0)) {
                // TODO -- log exception
                final YFSException yfe = new YFSException(
                        "Mandatory parameter 'DistanceToConsider' missing.");
                yfe.setErrorCode("KSN-025");
                yfe.setErrorDescription(yfe.getMessage());
                throw yfe;
            }// end: if ((value == null) || (value.length() == 0))
            attrName = "DistanceToConsider";
            bean.setDistanceToConsider(new Integer(value));
            //
            // GetSurroundingNodeList/@FulfillmentType
            value = element.getAttribute("FulfillmentType");
            if ((value == null) || (value.trim().length() == 0)) {
                // TODO -- log exception
                final YFSException yfe = new YFSException();
                yfe.setErrorCode("KSN-030");
                yfe.setErrorDescription("Mandatory parameter 'FulfillmentType' missing.");
                throw yfe;
            }// end: if ((value == null) || (value.length() == 0))
            bean.setFulfillType(value);
            //
            // GetSurroundingNodeList/@NodeType
            value = element.getAttribute("NodeType");
            if ((value == null) || (value.trim().length() == 0)) {
                // TODO -- log exception
                final YFSException yfe = new YFSException();
                yfe.setErrorCode("KSN-035");
                yfe.setErrorDescription("Mandatory parameter 'NodeType' missing.");
                throw yfe;
            }// end: if ((value == null) || (value.length() == 0))
            bean.setNodeType(value);
            //
            // GetSurroundingNodeList/@OrganizationCode
            value = element.getAttribute("OrganizationCode");
            if ((value == null) || (value.trim().length() == 0)) {
                // TODO -- log exception
                final YFSException yfe = new YFSException();
                yfe.setErrorCode("KSN-040");
                yfe.setErrorDescription("Mandatory parameter 'OrganizationCode' missing.");
                throw yfe;
            }// end: if ((value == null) || (value.length() == 0))
            bean.setOrgCode(value);
            //
            // GetSurroundingNodeList/ShipToAddress
            addresses = element.getElementsByTagName("ShipToAddress");
            if (addresses.getLength() == 0) {
                // TODO -- log exception
                final YFSException yfe = new YFSException();
                yfe.setErrorCode("KSN-045");
                yfe.setErrorDescription("Mandatory element 'ShipToAddress' missing.");
                throw yfe;
            }// end: if (addresses.getLength() == 0)
             //
            address = (Element) addresses.item(0);
            //
            // GetSurroundingNodeList/ShipToAddress/@State
            value = address.getAttribute("State");
            if (value != null) {
                if (value.trim().length() != 0) {
                    bean.setState(value);
                }// end: if (value.trim().length() != 0)
            }// end: if (value != null)
             //
             // GetSurroundingNodeList/ShipToAddress/@ZipCode
            value = address.getAttribute("ZipCode");
            if ((value == null) || (value.trim().length() == 0)) {
                // TODO -- log exception
                final YFSException yfe = new YFSException();
                yfe.setErrorCode("KSN-050");
                yfe.setErrorDescription("Manadtory parameter 'ZipCode' missing.");
                throw yfe;
            }// end: if ( (value == null) || (value.trim().length() == 0))
            bean.setZipCode(value);
            //
            // GetSurroundingNodeList/ShipToAddress/@Country
            value = address.getAttribute("Country");
            if ((value == null) || (value.trim().length() == 0)) {
                // TODO -- log error
                final YFSException yfe = new YFSException();
                yfe.setErrorCode("KSN-055");
                yfe.setErrorDescription("Manandatory parameter 'Country' missing.");
                throw yfe;
            }// end: if ((value == null) || (value.trim().length() == 0))
            bean.setCountry(value);
            //
            return bean;
        } catch (NumberFormatException ex) {
            // TODO -- log exception
            final YFSException yfe = new YFSException();
            yfe.setErrorCode("KSN-070");
            yfe.setErrorDescription(attrName
                    + " must be numeric.  Value passed is: " + value);
            yfe.setStackTrace(ex.getStackTrace());
            throw yfe;
        }
    }// unmarshal(Node):KohlsSurroundingNodeListBean

}// class:KohlsSurroundNodeListBean

